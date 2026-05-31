package com.beatify.view.controller;

import com.beatify.view.SessionContext;
import com.beatify.view.util.DescargaManager;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.PlayerManager;
import com.beatify.view.util.PlayerManager.SongInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Vista del reproductor expandido. No gestiona audio directamente —
 * delega todo al PlayerManager singleton.
 */
public class PlayerController {

    @FXML private Button    btnVolver;
    @FXML private StackPane coverHolder;
    @FXML private Label     lblCoverInitials;
    @FXML private Label     lblTitulo;
    @FXML private Label     lblArtista;
    @FXML private Label     lblAlbum;
    @FXML private Slider    sliderProgreso;
    @FXML private Slider    sliderVolumen;
    @FXML private Label     lblTiempoActual;
    @FXML private Label     lblTiempoTotal;
    @FXML private Button    btnPlayPause;
    @FXML private Button    btnShuffle;
    @FXML private Button    btnRepeat;
    @FXML private Button    btnDescargar;
    @FXML private Label     lblModoDemo;
    @FXML private FontIcon  iconPlay;     // graphic del btnPlayPause
    @FXML private FontIcon  iconShuffle;  // graphic del btnShuffle
    @FXML private FontIcon  iconRepeat;   // graphic del btnRepeat
    @FXML private FontIcon  iconDescargar;// graphic del btnDescargar

    private static final Color VERDE = Color.web("#1ed760");
    private static final Color GRIS  = Color.web("#a7a7a7");

    private boolean ajustandoSlider = false;

    @FXML
    private void initialize() {
        // Registrar callback para refrescarse con cada cambio del manager
        PlayerManager.getInstance().setOnCambio(this::actualizar);
        PlayerManager.getInstance().setOnAviso(PlayerController::mostrarAviso);

        // Slider de progreso: al soltar el ratón, salta a esa posición
        sliderProgreso.setOnMousePressed(e -> ajustandoSlider = true);
        sliderProgreso.setOnMouseReleased(e -> {
            ajustandoSlider = false;
            PlayerManager.getInstance().setProgreso(sliderProgreso.getValue());
        });

        // Slider de volumen
        sliderVolumen.setValue(PlayerManager.getInstance().getVolumen());
        sliderVolumen.valueProperty().addListener((obs, o, v) ->
                PlayerManager.getInstance().setVolumen(v.doubleValue()));

        actualizar();
    }

    private void actualizar() {
        final PlayerManager pm   = PlayerManager.getInstance();
        final SongInfo      info = pm.getSongActual();

        if (pm.isAnuncioActivo()) {
            lblTitulo.setText("🔊 Anuncio");
            lblArtista.setText("Publicidad");
            lblAlbum.setText("");
            lblModoDemo.setText("Hazte Premium para quitar los anuncios");
        } else if (info != null) {
            lblTitulo.setText(info.cancion().getTitulo());
            lblArtista.setText(info.artista());
            lblAlbum.setText(info.album() != null ? info.album() : "");
            lblModoDemo.setText("");
        } else {
            lblTitulo.setText("Sin canción seleccionada");
            lblArtista.setText("—");
            lblAlbum.setText("");
            lblModoDemo.setText("▸ Selecciona una canción desde Explorar");
        }

        // Play/Pausa — alternar el icono (no setText)
        iconPlay.setIconLiteral(pm.isReproduciendo() ? "bi-pause-fill" : "bi-play-fill");

        // Shuffle / Repeat — verde si activo, gris si no
        iconShuffle.setIconColor(pm.isShuffle() ? VERDE : GRIS);
        iconRepeat.setIconColor(pm.isRepeat() ? VERDE : GRIS);

        // Descarga — check verde si la canción está descargada, flecha gris si no
        final boolean descargada = info != null && info.cancion().getIdCancion() != null
                && DescargaManager.getInstance().estaDescargada(info.cancion().getIdCancion());
        iconDescargar.setIconLiteral(descargada ? "bi-check-circle-fill" : "bi-download");
        iconDescargar.setIconColor(descargada ? VERDE : GRIS);

        // Progreso
        final double dur  = pm.getDuracionSeg();
        final double prog = pm.getProgresoSeg();
        if (!ajustandoSlider && dur > 0) {
            sliderProgreso.setMax(dur);
            sliderProgreso.setValue(prog);
        }
        lblTiempoActual.setText(fmt((int) prog));
        lblTiempoTotal.setText(fmt((int) dur));
    }

    @FXML private void onPlayPause() { PlayerManager.getInstance().playPause(); }
    @FXML private void onAnterior()  { PlayerManager.getInstance().anteriorManual(); }
    @FXML private void onSiguiente() { PlayerManager.getInstance().siguienteManual(); }
    @FXML private void onShuffle()   { PlayerManager.getInstance().toggleShuffle(); }
    @FXML private void onRepeat()    { PlayerManager.getInstance().toggleRepeat(); }

    /**
     * Descargar/quitar descarga de la canción actual. Respeta el plan: en FREE
     * se bloquea con aviso; en planes de pago alterna y persiste.
     */
    @FXML
    private void onDescargar() {
        final SongInfo info = PlayerManager.getInstance().getSongActual();
        if (info == null || info.cancion().getIdCancion() == null) {
            return;
        }
        final DescargaManager.Resultado r =
                DescargaManager.getInstance().alternar(info.cancion());
        if (r == DescargaManager.Resultado.BLOQUEADA_POR_PLAN) {
            mostrarAviso("El plan " + SessionContext.getInstance().getPlanActual().getEtiqueta()
                    + " no permite descargas offline. Hazte Premium para descargar canciones.");
        } else {
            actualizar();   // refresca el icono
        }
    }

    @FXML
    private void onVolver() {
        PlayerManager.getInstance().setOnCambio(null); // desregistrar
        HistorialNavegacion.getInstance().atras();
    }

    private static String fmt(final int seg) {
        return String.format("%d:%02d", seg / 60, seg % 60);
    }

    /** Muestra un aviso de plan (p.ej. límite de saltos) sin bloquear la reproducción. */
    private static void mostrarAviso(final String mensaje) {
        final Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje);
        alerta.setTitle("Beatify");
        alerta.setHeaderText("Tu plan");
        alerta.show();
    }
}
