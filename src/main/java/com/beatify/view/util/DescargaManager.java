package com.beatify.view.util;

import com.beatify.model.Cancion;
import com.beatify.model.Cliente;
import com.beatify.model.TipoPlan;
import com.beatify.view.SessionContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestiona las descargas offline por cliente. Una descarga real:
 *   - copia el MP3 empaquetado a {@code <user.home>/.beatify/audio/} para que
 *     quede disponible localmente (el {@link PlayerManager} lo prefiere al
 *     recurso empaquetado cuando existe), y
 *   - registra el id de la canción en un índice de texto por cliente
 *     ({@code <user.home>/.beatify/descargas-<idCliente>.txt}).
 *
 * Todo es best-effort: si falla el IO, la operación no rompe la app.
 *
 * La descarga está gateada por el plan: solo se permite si
 * {@link TipoPlan#isDescargasOffline()} es true (planes de pago).
 *
 * Las dependencias (directorio base, proveedor de plan y de id de cliente) se
 * inyectan por constructor para poder probar la lógica sin BD ni tocar el
 * {@code user.home} real; el singleton de producción las cablea contra
 * {@link SessionContext}.
 */
public final class DescargaManager {

    private static final Logger LOG = Logger.getLogger(DescargaManager.class.getName());

    private static final DescargaManager INSTANCE = new DescargaManager(
            Path.of(System.getProperty("user.home"), ".beatify"),
            () -> SessionContext.getInstance().getPlanActual(),
            DescargaManager::idClienteDeSesion);

    /** Resultado de intentar (des)marcar una descarga. */
    public enum Resultado { BLOQUEADA_POR_PLAN, DESCARGADA, ELIMINADA, SIN_SESION }

    private final Path baseDir;
    private final Supplier<TipoPlan> planProvider;
    private final Supplier<Integer> clienteIdProvider;

    private Integer clienteCargado;                       // cliente cuyo set está en memoria
    private final Set<Integer> descargadas = new LinkedHashSet<>();

    /** Para tests: inyecta colaboradores. En producción usar {@link #getInstance()}. */
    DescargaManager(final Path baseDir,
                    final Supplier<TipoPlan> planProvider,
                    final Supplier<Integer> clienteIdProvider) {
        this.baseDir = baseDir;
        this.planProvider = planProvider;
        this.clienteIdProvider = clienteIdProvider;
    }

    public static DescargaManager getInstance() { return INSTANCE; }

    /** {@code true} si el plan del cliente actual permite descargas offline. */
    public boolean puedeDescargar() {
        return planProvider.get().isDescargasOffline();
    }

    /** {@code true} si la canción está descargada para el cliente actual. */
    public boolean estaDescargada(final int idCancion) {
        final Integer idCliente = clienteIdProvider.get();
        if (idCliente == null) return false;
        sincronizarCliente(idCliente);
        return descargadas.contains(idCancion);
    }

    /**
     * Alterna la descarga de una canción respetando el plan. En FREE devuelve
     * {@link Resultado#BLOQUEADA_POR_PLAN} sin cambiar nada; en planes de pago
     * copia/borra el MP3 local y actualiza el índice del cliente.
     */
    public Resultado alternar(final Cancion cancion) {
        if (cancion == null || cancion.getIdCancion() == null) return Resultado.SIN_SESION;
        final Integer idCliente = clienteIdProvider.get();
        if (idCliente == null) return Resultado.SIN_SESION;
        if (!puedeDescargar()) return Resultado.BLOQUEADA_POR_PLAN;

        sincronizarCliente(idCliente);
        final int id = cancion.getIdCancion();
        final Resultado resultado;
        if (descargadas.remove(id)) {
            if (!otroClienteTiene(id, idCliente)) {   // solo borrar el MP3 si nadie más lo tiene
                borrarLocal(cancion);
            }
            resultado = Resultado.ELIMINADA;
        } else {
            descargadas.add(id);
            copiarRecurso(cancion);
            resultado = Resultado.DESCARGADA;
        }
        persistir(idCliente);
        return resultado;
    }

    /**
     * URL del MP3 descargado localmente si existe en disco, o {@code null}.
     * Lo usa el {@link PlayerManager} para reproducir offline.
     */
    public URL urlLocalSiExiste(final Cancion cancion) {
        final Path f = archivoAudioLocal(cancion);
        if (f == null || !Files.exists(f)) return null;
        try {
            return f.toUri().toURL();
        } catch (final MalformedURLException ex) {
            return null;
        }
    }

    // -----------------------------------------------------------------
    // Interno
    // -----------------------------------------------------------------

    private static Integer idClienteDeSesion() {
        final Cliente cli = SessionContext.getInstance().getClienteActual();
        return (cli != null) ? cli.getIdCliente() : null;
    }

    /** Carga el índice del cliente desde disco si cambió respecto al de memoria. */
    private void sincronizarCliente(final Integer idCliente) {
        if (idCliente.equals(clienteCargado)) return;
        descargadas.clear();
        descargadas.addAll(leerIndice(idCliente));
        clienteCargado = idCliente;
    }

    private Path archivoIndice(final Integer idCliente) {
        return baseDir.resolve("descargas-" + idCliente + ".txt");
    }

    /** Ruta del MP3 local para la canción (bajo {@code baseDir/audio/}), o null. */
    private Path archivoAudioLocal(final Cancion cancion) {
        if (cancion == null) return null;
        final String ruta = cancion.getRutaArchivo();
        if (ruta == null || ruta.isBlank()) return null;
        final String relativa = ruta.replaceFirst("^audio/", "");   // "xxx.mp3"
        return baseDir.resolve("audio").resolve(relativa);
    }

    private void copiarRecurso(final Cancion cancion) {
        final Path destino = archivoAudioLocal(cancion);
        if (destino == null || Files.exists(destino)) return;        // ya está o sin ruta
        final String ruta = cancion.getRutaArchivo();
        try (InputStream in = DescargaManager.class.getResourceAsStream("/" + ruta)) {
            if (in == null) {
                LOG.log(Level.FINE, "Recurso de audio no encontrado: /{0} (se marca sin copiar)", ruta);
                return;
            }
            Files.createDirectories(destino.getParent());
            Files.copy(in, destino);
        } catch (final IOException ex) {
            LOG.log(Level.WARNING, "No se pudo copiar el audio de la descarga", ex);
        }
    }

    private void borrarLocal(final Cancion cancion) {
        final Path f = archivoAudioLocal(cancion);
        if (f == null) return;
        try {
            Files.deleteIfExists(f);
        } catch (final IOException ex) {
            LOG.log(Level.FINE, "No se pudo borrar el audio descargado", ex);
        }
    }

    /**
     * ¿Algún cliente DISTINTO al actual sigue teniendo esta canción descargada?
     * Como cada id de canción mapea 1:1 a un archivo de audio, esto indica si el
     * MP3 local todavía está en uso por otro cliente. Best-effort: si no se
     * puede listar el directorio, devuelve false (se permite borrar).
     */
    private boolean otroClienteTiene(final int idCancion, final Integer idClienteActual) {
        if (!Files.isDirectory(baseDir)) return false;
        final String propio = "descargas-" + idClienteActual + ".txt";
        try (Stream<Path> archivos = Files.list(baseDir)) {
            return archivos.anyMatch(p -> {
                final String n = p.getFileName().toString();
                return n.startsWith("descargas-") && n.endsWith(".txt")
                        && !n.equals(propio)
                        && leerIndiceArchivo(p).contains(idCancion);
            });
        } catch (final IOException ex) {
            LOG.log(Level.FINE, "No se pudo revisar otros índices de descargas", ex);
            return false;
        }
    }

    private List<Integer> leerIndice(final Integer idCliente) {
        return leerIndiceArchivo(archivoIndice(idCliente));
    }

    private List<Integer> leerIndiceArchivo(final Path f) {
        final List<Integer> ids = new ArrayList<>();
        if (!Files.exists(f)) return ids;
        try {
            for (final String linea : Files.readAllLines(f)) {
                final String s = linea.trim();
                if (s.isEmpty()) continue;
                try {
                    ids.add(Integer.parseInt(s));
                } catch (final NumberFormatException ignored) {
                    // línea corrupta: se ignora
                }
            }
        } catch (final IOException ex) {
            LOG.log(Level.FINE, "No se pudo leer el índice de descargas " + f, ex);
        }
        return ids;
    }

    private void persistir(final Integer idCliente) {
        try {
            Files.createDirectories(baseDir);
            final List<String> lineas = new ArrayList<>(descargadas.size());
            for (final Integer id : descargadas) {
                lineas.add(String.valueOf(id));
            }
            Files.write(archivoIndice(idCliente), lineas);
        } catch (final IOException ex) {
            LOG.log(Level.WARNING, "No se pudo guardar el índice de descargas del cliente " + idCliente, ex);
        }
    }
}
