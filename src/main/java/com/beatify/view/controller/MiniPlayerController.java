package com.beatify.view.controller;

import com.beatify.dao.LikeCancionDAO;
import com.beatify.model.Cancion;
import com.beatify.model.Cliente;
import com.beatify.model.LikeCancion;
import com.beatify.service.ILikeCancionService;
import com.beatify.service.LikeCancionService;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.util.DescargaManager;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.PlayerManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador del miniplayer compartido (miniplayer.fxml).
 * Se auto-actualiza escuchando PlayerManager — no necesita comunicación
 * con el controller padre de la pantalla que lo incluye.
 *
 * Funcionalidades:
 *   - Play/Pause (icono se alterna entre bi-play-fill y bi-pause-fill)
 *   - Anterior/Siguiente
 *   - Shuffle/Repeat con indicador visual (clase .is-on -> verde)
 *   - Like real contra LIKE_CANCION (icono bi-heart -> bi-heart-fill)
 *   - Mute con icono bi-volume-up -> bi-volume-mute-fill
 *   - Barra de progreso con click + drag para hacer seek
 *   - Barra de volumen con click + drag para ajustar
 *   - Botón cola/lista de reproducción → expande al PlayerScreen
 *   - Botón expandir → navega a player.fxml
 */
public class MiniPlayerController {

    private static final Logger LOG = Logger.getLogger(MiniPlayerController.class.getName());

    @FXML private StackPane mpCoverHolder;
    @FXML private Label     mpTitulo, mpArtista;
    @FXML private Button    btnMpPlay, btnMpShuffle, btnMpRepeat;
    @FXML private Button    btnMpAnterior, btnMpSiguiente, btnMpLike, btnMpExpand;
    @FXML private Button    btnMpDescargar;
    @FXML private Button    btnMpQueue, btnMpVolumen;
    @FXML private Label     lblMpTiempo, lblMpDuracion;
    @FXML private Region    mpBarraFill, mpVolFill;
    @FXML private StackPane mpBarraStack, mpVolStack;
    @FXML private FontIcon  iconMpPlay;     // declarado en miniplayer.fxml
    @FXML private FontIcon  iconMpLike;     // declarado en miniplayer.fxml
    @FXML private FontIcon  iconMpDescargar;// declarado en miniplayer.fxml
    @FXML private FontIcon  iconMpVolumen;  // declarado en miniplayer.fxml

    private static final String[][] PALETA = {
        {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
        {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
    };

    private final ILikeCancionService likeService = new LikeCancionService(new LikeCancionDAO());
    private boolean liked = false;  // estado del like de la canción actual (cacheado)

    @FXML
    private void initialize() {
        PlayerManager.getInstance().setOnCambio(this::actualizar);
        PlayerManager.getInstance().setOnAviso(MiniPlayerController::mostrarAviso);
        actualizar();
    }

    /** Muestra un aviso de plan (p.ej. límite de saltos) sin bloquear la reproducción. */
    private static void mostrarAviso(final String mensaje) {
        final Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alerta.setTitle("Beatify");
        alerta.setHeaderText("Tu plan");
        alerta.show();   // no modal: no congela el reproductor
    }

    private void actualizar() {
        final PlayerManager pm   = PlayerManager.getInstance();
        final PlayerManager.SongInfo info = pm.getSongActual();

        if (info != null) {
            mpTitulo.setText(info.cancion().getTitulo());
            mpArtista.setText(info.artista());
            final int id = info.cancion().getIdCancion() != null ? info.cancion().getIdCancion() : 0;
            final String[] col = PALETA[Math.abs(id) % PALETA.length];
            mpCoverHolder.getChildren().setAll(new AlbumCover(48, col[0], col[1]));

            // Recalcular like si la canción cambió
            liked = consultarLikeActual(info.cancion());
            actualizarIconoLike();

            // Reflejar el estado de descarga de la canción actual
            actualizarIconoDescarga(info.cancion());
        }

        // Overlay de anuncio (plan FREE): sustituye título/artista mientras dure
        if (pm.isAnuncioActivo()) {
            mpTitulo.setText("🔊 Anuncio");
            mpArtista.setText("Publicidad · Hazte Premium para quitar anuncios");
        }

        // Play/Pause: cambiar el FontIcon (no setText — evita doble flecha)
        iconMpPlay.setIconLiteral(pm.isReproduciendo() ? "bi-pause-fill" : "bi-play-fill");

        // Indicador activo de shuffle/repeat — toggle clase .is-on
        toggleClase(btnMpShuffle, "is-on", pm.isShuffle());
        toggleClase(btnMpRepeat,  "is-on", pm.isRepeat());

        // Volumen: icono según mute / nivel
        if (pm.isMuteado() || pm.getVolumen() == 0) {
            iconMpVolumen.setIconLiteral("bi-volume-mute-fill");
        } else if (pm.getVolumen() < 0.5) {
            iconMpVolumen.setIconLiteral("bi-volume-down-fill");
        } else {
            iconMpVolumen.setIconLiteral("bi-volume-up-fill");
        }

        // Tiempos + barras
        final double dur  = pm.getDuracionSeg();
        final double prog = pm.getProgresoSeg();
        lblMpTiempo.setText(fmt((int) prog));
        lblMpDuracion.setText(fmt((int) dur));

        final double anchoBar = mpBarraStack.getWidth();
        if (anchoBar > 0 && dur > 0) {
            mpBarraFill.setMaxWidth(anchoBar * (prog / dur));
        }

        final double anchoVol = mpVolStack.getWidth();
        if (anchoVol > 0) {
            mpVolFill.setMaxWidth(anchoVol * pm.getVolumen());
        }
    }

    // ---- Handlers de eventos ----

    @FXML private void onMpPlayPause() { PlayerManager.getInstance().playPause(); }
    @FXML private void onMpAnterior()  { PlayerManager.getInstance().anteriorManual();  }
    @FXML private void onMpSiguiente() { PlayerManager.getInstance().siguienteManual(); }
    @FXML private void onMpShuffle()   { PlayerManager.getInstance().toggleShuffle(); }
    @FXML private void onMpRepeat()    { PlayerManager.getInstance().toggleRepeat();  }

    /** Like real contra LIKE_CANCION. Toggle insert/delete. */
    @FXML
    private void onMpLike() {
        final PlayerManager.SongInfo info = PlayerManager.getInstance().getSongActual();
        final Cliente actual = SessionContext.getInstance().getClienteActual();
        if (info == null || actual == null || actual.getIdCliente() == null
                          || info.cancion().getIdCancion() == null) {
            return;  // nada que likear / sin sesión
        }

        try {
            if (liked) {
                likeService.quitarLike(actual.getIdCliente(), info.cancion().getIdCancion());
                liked = false;
            } else {
                final LikeCancion l = new LikeCancion(
                        actual.getIdCliente(), info.cancion().getIdCancion(), LocalDateTime.now());
                likeService.darLike(l);
                liked = true;
            }
            actualizarIconoLike();
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Error toggleando like de canción", ex);
        }
    }

    /** Botón altavoz: alternar mute. */
    @FXML
    private void onMpMute() {
        PlayerManager.getInstance().toggleMute();
    }

    /**
     * Descargar/quitar descarga de la canción actual. Respeta el plan: en FREE
     * se bloquea con un aviso; en planes de pago alterna y persiste el estado.
     */
    @FXML
    private void onMpDescargar() {
        final PlayerManager.SongInfo info = PlayerManager.getInstance().getSongActual();
        if (info == null || info.cancion().getIdCancion() == null) {
            return;   // nada que descargar
        }
        final DescargaManager.Resultado r =
                DescargaManager.getInstance().alternar(info.cancion());
        switch (r) {
            case BLOQUEADA_POR_PLAN -> mostrarAviso(
                    "El plan " + SessionContext.getInstance().getPlanActual().getEtiqueta()
                    + " no permite descargas offline. Hazte Premium para descargar canciones.");
            case SIN_SESION -> { /* sin sesión: no hacer nada */ }
            default -> actualizarIconoDescarga(info.cancion());
        }
    }

    /** Botón lista de reproducción: por ahora abre el player expandido. */
    @FXML
    private void onMpQueue() {
        HistorialNavegacion.getInstance().navegar("/view/player.fxml");
    }

    @FXML
    private void onMpExpand() {
        HistorialNavegacion.getInstance().navegar("/view/player.fxml");
    }

    // ---- Barras: click + drag ----

    @FXML
    private void onMpBarraClick(final MouseEvent e) {
        ajustarProgresoBarraTiempo(e.getX());
    }

    @FXML
    private void onMpBarraDrag(final MouseEvent e) {
        ajustarProgresoBarraTiempo(e.getX());
    }

    private void ajustarProgresoBarraTiempo(final double x) {
        final double ancho = mpBarraStack.getWidth();
        final double dur   = PlayerManager.getInstance().getDuracionSeg();
        if (ancho > 0 && dur > 0) {
            final double clampX = Math.max(0, Math.min(ancho, x));
            PlayerManager.getInstance().setProgreso((clampX / ancho) * dur);
        }
    }

    @FXML
    private void onMpVolClick(final MouseEvent e) {
        ajustarVolumen(e.getX());
    }

    @FXML
    private void onMpVolDrag(final MouseEvent e) {
        ajustarVolumen(e.getX());
    }

    private void ajustarVolumen(final double x) {
        final double ancho = mpVolStack.getWidth();
        if (ancho > 0) {
            final double clampX = Math.max(0, Math.min(ancho, x));
            PlayerManager.getInstance().setVolumen(clampX / ancho);
        }
    }

    // ---- Helpers ----

    /** Aplica o quita una pseudo-clase (styleClass) de un botón. */
    private static void toggleClase(final Button btn, final String clase, final boolean activo) {
        if (activo && !btn.getStyleClass().contains(clase)) {
            btn.getStyleClass().add(clase);
        } else if (!activo) {
            btn.getStyleClass().remove(clase);
        }
    }

    /** Pinta el ícono de like según el estado actual (lleno verde / outline gris). */
    private void actualizarIconoLike() {
        iconMpLike.setIconLiteral(liked ? "bi-heart-fill" : "bi-heart");
        toggleClase(btnMpLike, "is-on", liked);
    }

    /** Pinta el ícono de descarga: check verde si está descargada, flecha si no. */
    private void actualizarIconoDescarga(final Cancion cancion) {
        final boolean descargada = cancion.getIdCancion() != null
                && DescargaManager.getInstance().estaDescargada(cancion.getIdCancion());
        iconMpDescargar.setIconLiteral(descargada ? "bi-check-circle-fill" : "bi-download");
        toggleClase(btnMpDescargar, "is-on", descargada);
    }

    /**
     * Consulta a BD si la canción dada ya está likeada por el cliente actual.
     * Es best-effort — si falla devuelve false sin romper la UI.
     */
    private boolean consultarLikeActual(final Cancion cancion) {
        final Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null || actual.getIdCliente() == null
                          || cancion.getIdCancion() == null) {
            return false;
        }
        try {
            return likeService.listar().stream().anyMatch(l ->
                actual.getIdCliente().equals(l.getIdCliente())
                && cancion.getIdCancion().equals(l.getIdCancion()));
        } catch (final RuntimeException ex) {
            LOG.log(Level.FINE, "No se pudo verificar like (BD no disponible)", ex);
            return false;
        }
    }

    private static String fmt(final int seg) {
        return String.format("%d:%02d", seg / 60, seg % 60);
    }
}
