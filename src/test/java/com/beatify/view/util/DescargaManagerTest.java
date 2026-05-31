package com.beatify.view.util;

import com.beatify.model.Cancion;
import com.beatify.model.TipoPlan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba la lógica del DescargaManager sin BD ni tocar el user.home real:
 * inyecta el directorio (temp), el plan y el id de cliente.
 */
class DescargaManagerTest {

    /** Canción sin ruta de audio: ejercita índice/gate sin depender de recursos. */
    private static Cancion cancion(final int id) {
        final Cancion c = new Cancion();
        c.setIdCancion(id);
        return c;
    }

    private static Cancion cancionConRuta(final int id, final String ruta) {
        final Cancion c = cancion(id);
        c.setRutaArchivo(ruta);
        return c;
    }

    private static DescargaManager manager(final Path dir, final TipoPlan plan, final Integer idCliente) {
        return new DescargaManager(dir, () -> plan, () -> idCliente);
    }

    @Test
    void freeNoPuedeDescargarYQuedaBloqueada(@TempDir final Path dir) {
        final DescargaManager dm = manager(dir, TipoPlan.FREE, 1);
        assertFalse(dm.puedeDescargar());
        assertEquals(DescargaManager.Resultado.BLOQUEADA_POR_PLAN, dm.alternar(cancion(10)));
        assertFalse(dm.estaDescargada(10));
    }

    @Test
    void planDePagoPuedeDescargarYAlternar(@TempDir final Path dir) {
        final DescargaManager dm = manager(dir, TipoPlan.INDIVIDUAL, 1);
        assertTrue(dm.puedeDescargar());

        assertEquals(DescargaManager.Resultado.DESCARGADA, dm.alternar(cancion(10)));
        assertTrue(dm.estaDescargada(10));

        assertEquals(DescargaManager.Resultado.ELIMINADA, dm.alternar(cancion(10)));
        assertFalse(dm.estaDescargada(10));
    }

    @Test
    void sinSesionDevuelveSinSesion(@TempDir final Path dir) {
        final DescargaManager dm = manager(dir, TipoPlan.INDIVIDUAL, null);
        assertEquals(DescargaManager.Resultado.SIN_SESION, dm.alternar(cancion(10)));
        assertFalse(dm.estaDescargada(10));
    }

    @Test
    void descargaPersisteEntreInstancias(@TempDir final Path dir) {
        manager(dir, TipoPlan.FAMILIAR, 7).alternar(cancion(42));

        // Una instancia nueva (misma carpeta y cliente) debe ver la descarga
        final DescargaManager otra = manager(dir, TipoPlan.FAMILIAR, 7);
        assertTrue(otra.estaDescargada(42));
    }

    @Test
    void indiceEsPorCliente(@TempDir final Path dir) {
        manager(dir, TipoPlan.DUO, 1).alternar(cancion(5));
        // Otro cliente no comparte el índice
        assertFalse(manager(dir, TipoPlan.DUO, 2).estaDescargada(5));
    }

    @Test
    void nullSongDevuelveSinSesion(@TempDir final Path dir) {
        final DescargaManager dm = manager(dir, TipoPlan.INDIVIDUAL, 1);
        assertEquals(DescargaManager.Resultado.SIN_SESION, dm.alternar(null));
    }

    @Test
    void noBorraElArchivoMientrasOtroClienteLoTenga(@TempDir final Path dir) throws Exception {
        final Cancion c = cancionConRuta(5, "audio/test.mp3");
        // Coloca un MP3 de prueba donde el manager lo buscaría
        final Path audio = dir.resolve("audio").resolve("test.mp3");
        Files.createDirectories(audio.getParent());
        Files.writeString(audio, "fake-mp3");

        final DescargaManager dm1 = manager(dir, TipoPlan.INDIVIDUAL, 1);
        final DescargaManager dm2 = manager(dir, TipoPlan.INDIVIDUAL, 2);
        dm1.alternar(c);   // cliente 1 la descarga
        dm2.alternar(c);   // cliente 2 también

        // Cliente 1 la quita: el archivo debe seguir (cliente 2 aún la tiene)
        assertEquals(DescargaManager.Resultado.ELIMINADA, dm1.alternar(c));
        assertTrue(Files.exists(audio), "el MP3 no debe borrarse mientras otro cliente lo tenga");

        // Cliente 2 la quita: ahora sí se borra
        assertEquals(DescargaManager.Resultado.ELIMINADA, dm2.alternar(c));
        assertFalse(Files.exists(audio), "el MP3 debe borrarse cuando ya nadie lo tiene");
    }
}
