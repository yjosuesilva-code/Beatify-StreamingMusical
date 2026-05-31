package com.beatify.view.util;

import com.beatify.dao.ReproduccionDAO;
import com.beatify.model.Cancion;
import com.beatify.model.Cliente;
import com.beatify.model.Reproduccion;
import com.beatify.model.TipoPlan;
import com.beatify.view.SessionContext;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton que gestiona la reproducción de audio globalmente.
 * Independiente de la pantalla activa — el miniplayer de cada controller
 * se conecta a este manager para reflejar el estado actual.
 */
public final class PlayerManager {

    private static final Logger LOG = Logger.getLogger(PlayerManager.class.getName());
    private static final PlayerManager INSTANCE = new PlayerManager();

    // ---- Canción y cola ----
    public record SongInfo(Cancion cancion, String artista, String album) {}

    private final List<SongInfo> cola        = new ArrayList<>();
    private int     indiceCola  = -1;
    private boolean shuffle     = false;
    private boolean repeat      = false;

    // ---- Estado de reproducción ----
    private MediaPlayer mediaPlayer;
    private Timeline    simulacion;
    private boolean     reproduciendo = false;
    private double      progresoSeg   = 0;
    private double      duracionSeg   = 0;
    private double      volumen       = 0.7;
    private double      volumenAntesMute = 0.7;
    private boolean     muteado       = false;

    // ---- Reglas de plan: límite de saltos y anuncios (plan FREE) ----
    private int           saltosEnVentana = 0;
    private LocalDateTime inicioVentanaSaltos;     // ventana móvil de 1 hora
    private int           cancionesDesdeAnuncio = 0;
    private boolean       anuncioActivo = false;   // overlay de anuncio en curso

    // ---- Callback de UI (el controller activo lo registra) ----
    private Runnable onCambio;
    // Se dispara SOLO cuando se registra una reproducción nueva (para stats en vivo)
    private Runnable onNuevaReproduccion;
    // Aviso textual al usuario (p.ej. límite de saltos alcanzado)
    private Consumer<String> onAviso;

    private PlayerManager() {}

    public static PlayerManager getInstance() { return INSTANCE; }

    // -----------------------------------------------------------------
    // Reproducción con cola
    // -----------------------------------------------------------------

    /** Reproduce una canción sola (sin cola adicional). */
    public void reproducir(final Cancion cancion, final String artista, final String album) {
        reproducirCola(List.of(new SongInfo(cancion, artista, album)), 0);
    }

    /** Reproduce la canción en posición {@code indice} de la cola dada. */
    public void reproducirCola(final List<SongInfo> nuevaCola, final int indice) {
        cola.clear();
        cola.addAll(nuevaCola);
        indiceCola = indice;
        cargarYReproducir();
    }

    private void cargarYReproducir() {
        if (indiceCola < 0 || indiceCola >= cola.size()) return;

        detenerInterno();
        progresoSeg = 0;

        final SongInfo info   = cola.get(indiceCola);
        final Cancion  cancion = info.cancion();
        duracionSeg = cancion.getDuracionSegundos() != null ? cancion.getDuracionSegundos() : 180;

        // Guardar en SessionContext para que el PlayerController lo muestre
        SessionContext.getInstance().setCancionActual(cancion, info.artista(), info.album());

        // Registrar la reproducción en BD (alimenta stats, "continuar", barrio…)
        registrarReproduccion(cancion);

        // Anuncios del plan FREE: cada N canciones se muestra un aviso
        evaluarAnuncio();

        notificarCambio();

        // Intentar cargar audio real: primero la descarga local (offline),
        // luego el recurso empaquetado en la app.
        URL url = DescargaManager.getInstance().urlLocalSiExiste(cancion);
        if (url == null && cancion.getRutaArchivo() != null && !cancion.getRutaArchivo().isBlank()) {
            final String base = cancion.getRutaArchivo().replaceFirst("^audio/", "")
                                                        .replaceAll("\\.mp3$", "");
            url = PlayerManager.class.getResource("/audio/" + base + ".mp3");
            if (url == null) url = PlayerManager.class.getResource("/audio/" + base + ".wav");
        }

        if (url != null) {
            try {
                final Media media = new Media(url.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(volumen);
                mediaPlayer.setOnReady(() -> {
                    final double dur = mediaPlayer.getTotalDuration().toSeconds();
                    if (dur > 0) duracionSeg = dur;
                    notificarCambio();
                });
                mediaPlayer.currentTimeProperty().addListener((obs, o, t) -> {
                    progresoSeg = t.toSeconds();
                    notificarCambio();
                });
                mediaPlayer.setOnEndOfMedia(this::alTerminarCancion);
                mediaPlayer.play();
                reproduciendo = true;
                notificarCambio();
                return;
            } catch (final Exception ex) {
                LOG.log(Level.FINE, "MediaPlayer falló, usando simulación", ex);
            }
        }
        usarSimulacion();
    }

    private void usarSimulacion() {
        simulacion = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            if (!reproduciendo) return;
            progresoSeg += 0.2;
            if (progresoSeg >= duracionSeg) alTerminarCancion();
            else notificarCambio();
        }));
        simulacion.setCycleCount(Timeline.INDEFINITE);
        simulacion.play();
        reproduciendo = true;
        notificarCambio();
    }

    private void alTerminarCancion() {
        if (repeat) {
            progresoSeg = 0;
            if (mediaPlayer != null) mediaPlayer.seek(Duration.ZERO);
            notificarCambio();
        } else {
            siguiente();
        }
    }

    // -----------------------------------------------------------------
    // Controles
    // -----------------------------------------------------------------

    public void playPause() {
        if (cola.isEmpty()) return;
        if (reproduciendo) {
            reproduciendo = false;
            if (mediaPlayer != null) mediaPlayer.pause();
            if (simulacion != null) simulacion.pause();
        } else {
            if (mediaPlayer == null && simulacion == null) {
                cargarYReproducir();
                return;
            }
            reproduciendo = true;
            if (mediaPlayer != null) mediaPlayer.play();
            if (simulacion != null) simulacion.play();
        }
        notificarCambio();
    }

    /**
     * Salto a la siguiente canción iniciado por el usuario. Aplica el límite
     * de saltos del plan; si se supera, no avanza y emite un aviso.
     */
    public void siguienteManual() {
        if (cola.isEmpty()) return;
        if (!permitirSalto()) return;
        siguiente();
    }

    /**
     * "Anterior" iniciado por el usuario. Si solo reinicia la canción actual
     * (llevamos &gt;3 s) no cuenta como salto; si cambia de pista, sí.
     */
    public void anteriorManual() {
        if (cola.isEmpty()) return;
        final boolean cambiaDeCancion = progresoSeg <= 3;
        if (cambiaDeCancion && !permitirSalto()) return;
        anterior();
    }

    /** Avance interno (fin de canción, auto-play): nunca limitado. */
    public void siguiente() {
        if (cola.isEmpty()) return;
        if (shuffle) {
            indiceCola = (int) (Math.random() * cola.size());
        } else {
            indiceCola = (indiceCola + 1) % cola.size();
        }
        cargarYReproducir();
    }

    public void anterior() {
        if (cola.isEmpty()) return;
        if (progresoSeg > 3) {
            // Si llevamos más de 3 s, reinicia la canción actual
            progresoSeg = 0;
            if (mediaPlayer != null) mediaPlayer.seek(Duration.ZERO);
            notificarCambio();
        } else {
            indiceCola = (indiceCola - 1 + cola.size()) % cola.size();
            cargarYReproducir();
        }
    }

    /**
     * Decide si el cliente actual puede saltar según su plan. Para planes con
     * saltos ilimitados siempre permite; si no, lleva una ventana móvil de 1 h
     * y bloquea cuando se alcanza el tope, avisando al usuario.
     */
    private boolean permitirSalto() {
        final TipoPlan plan = SessionContext.getInstance().getPlanActual();
        if (plan.isSaltosIlimitados()) return true;

        final LocalDateTime ahora = LocalDateTime.now();
        if (inicioVentanaSaltos == null || ahora.isAfter(inicioVentanaSaltos.plusHours(1))) {
            inicioVentanaSaltos = ahora;
            saltosEnVentana = 0;
        }
        if (saltosEnVentana >= plan.getMaxSaltosPorHora()) {
            avisar("Llegaste al límite de " + plan.getMaxSaltosPorHora()
                    + " saltos por hora del plan " + plan.getEtiqueta()
                    + ". Hazte Premium para saltos ilimitados.");
            return false;
        }
        saltosEnVentana++;
        return true;
    }

    public void toggleShuffle() {
        shuffle = !shuffle;
        notificarCambio();
    }

    public void toggleRepeat() {
        repeat = !repeat;
        notificarCambio();
    }

    public void setVolumen(final double v) {
        volumen = Math.max(0, Math.min(1, v));
        muteado = volumen == 0;
        if (mediaPlayer != null) mediaPlayer.setVolume(volumen);
        notificarCambio();
    }

    /** Alterna silencio. Guarda el volumen previo para restaurarlo al re-activar. */
    public void toggleMute() {
        if (muteado) {
            volumen = volumenAntesMute > 0 ? volumenAntesMute : 0.7;
            muteado = false;
        } else {
            volumenAntesMute = volumen;
            volumen = 0;
            muteado = true;
        }
        if (mediaPlayer != null) mediaPlayer.setVolume(volumen);
        notificarCambio();
    }

    public boolean isMuteado() { return muteado; }

    public void setProgreso(final double seg) {
        progresoSeg = seg;
        if (mediaPlayer != null) mediaPlayer.seek(Duration.seconds(seg));
        notificarCambio();
    }

    // -----------------------------------------------------------------
    // Getters de estado
    // -----------------------------------------------------------------

    public boolean isReproduciendo() { return reproduciendo; }
    public boolean isShuffle()       { return shuffle; }
    public boolean isRepeat()        { return repeat; }
    public double  getProgresoSeg()  { return progresoSeg; }
    public double  getDuracionSeg()  { return duracionSeg; }
    public double  getVolumen()      { return volumen; }
    public boolean hayCola()         { return !cola.isEmpty(); }

    public SongInfo getSongActual() {
        if (indiceCola >= 0 && indiceCola < cola.size()) return cola.get(indiceCola);
        return null;
    }

    /** {@code true} mientras se está mostrando un anuncio del plan FREE. */
    public boolean isAnuncioActivo() { return anuncioActivo; }

    // -----------------------------------------------------------------
    // Callback de UI
    // -----------------------------------------------------------------

    /** El controller activo registra su lambda aquí para recibir actualizaciones. */
    public void setOnCambio(final Runnable r) { this.onCambio = r; }

    /** Callback disparado al registrar una reproducción nueva (stats en vivo). */
    public void setOnNuevaReproduccion(final Runnable r) { this.onNuevaReproduccion = r; }

    /** El controller activo registra aquí cómo mostrar avisos al usuario. */
    public void setOnAviso(final Consumer<String> c) { this.onAviso = c; }

    private void notificarCambio() {
        if (onCambio != null) {
            javafx.application.Platform.runLater(onCambio);
        }
    }

    private void avisar(final String mensaje) {
        if (onAviso != null) {
            javafx.application.Platform.runLater(() -> onAviso.accept(mensaje));
        }
    }

    // -----------------------------------------------------------------
    // Anuncios (plan FREE)
    // -----------------------------------------------------------------

    /**
     * Cuenta canciones reproducidas y, en planes con anuncios, activa un
     * overlay de anuncio cada {@code frecuenciaAnuncioCanciones}. No interrumpe
     * el audio: es un aviso visual que la UI muestra mientras dure.
     */
    private void evaluarAnuncio() {
        final TipoPlan plan = SessionContext.getInstance().getPlanActual();
        if (!plan.isConAnuncios()) {
            anuncioActivo = false;
            return;
        }
        cancionesDesdeAnuncio++;
        if (cancionesDesdeAnuncio % plan.getFrecuenciaAnuncioCanciones() == 0) {
            anuncioActivo = true;
            final Timeline cierre = new Timeline(new KeyFrame(Duration.seconds(6), e -> {
                anuncioActivo = false;
                notificarCambio();
            }));
            cierre.play();
        }
    }

    // -----------------------------------------------------------------
    // Interno
    // -----------------------------------------------------------------

    /**
     * Inserta un registro en REPRODUCCION para el cliente actual.
     * Best-effort: si no hay sesión o falla la BD, no rompe la reproducción.
     */
    private void registrarReproduccion(final Cancion c) {
        final Cliente cli = SessionContext.getInstance().getClienteActual();
        if (cli == null || cli.getIdCliente() == null || c.getIdCancion() == null) return;
        try {
            new ReproduccionDAO().insertar(new Reproduccion(
                    LocalDateTime.now(),
                    c.getDuracionSegundos() != null ? c.getDuracionSegundos() : null,
                    cli.getIdCliente(),
                    c.getIdCancion()));
            // Notificar a la UI para refrescar stats en vivo
            if (onNuevaReproduccion != null) {
                javafx.application.Platform.runLater(onNuevaReproduccion);
            }
        } catch (final RuntimeException ex) {
            LOG.log(Level.FINE, "No se pudo registrar la reproducción", ex);
        }
    }

    private void detenerInterno() {
        reproduciendo = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (simulacion != null) {
            simulacion.stop();
            simulacion = null;
        }
    }
}
