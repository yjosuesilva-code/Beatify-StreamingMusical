package com.beatify.view.util;

import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.view.SessionContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/**
 * Muestra el diálogo "Nueva playlist" y persiste el resultado.
 * Llamar desde onNuevaPlaylist() en cualquier controller de app.
 *
 * @param ancla    nodo usado para centrar el diálogo en la ventana
 * @param onExito  Runnable ejecutado si la playlist se crea correctamente
 *                 (útil para recargar el sidebar del controller llamante)
 */
public final class PlaylistUtil {

    private PlaylistUtil() {
    }

    /**
     * Guarda la playlist seleccionada en el contexto y navega a su pantalla
     * de detalle. Llamar desde el onAction de cualquier botón/card de playlist.
     */
    public static void abrir(final Integer idPlaylist) {
        if (idPlaylist == null) return;
        SessionContext.getInstance().setIdPlaylistSeleccionada(idPlaylist);
        HistorialNavegacion.getInstance().navegar("/view/playlist.fxml");
    }

    public static void crearNueva(final Button ancla, final Runnable onExito) {
        final Cliente cliente = SessionContext.getInstance().getClienteActual();
        if (cliente == null || cliente.getIdCliente() == null) return;

        // ---- Campos ----
        final TextField txtNombre = new TextField();
        txtNombre.setPromptText("Mi playlist favorita");
        txtNombre.setPrefWidth(360);

        final TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción opcional...");
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setWrapText(true);

        final CheckBox chkPublica = new CheckBox("Visible para otros usuarios");
        chkPublica.setSelected(false);

        // ---- Botones ----
        final Button btnAceptar  = new Button("Aceptar");
        btnAceptar.getStyleClass().add("bf-btn-primary");
        btnAceptar.setDisable(true);
        final Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("bf-btn-ghost");
        txtNombre.textProperty().addListener((obs, o, v) -> btnAceptar.setDisable(v.isBlank()));

        final HBox acciones = new HBox(12, btnCancelar, btnAceptar);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        // ---- Tarjeta (contenido del overlay) ----
        final Label titulo = new Label("Nueva playlist");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        final Label sub = new Label("Crea una nueva lista de reproducción");
        sub.setStyle("-fx-text-fill: #b3b3b3;");

        final VBox card = new VBox(14,
                titulo, sub,
                etiqueta("Nombre"),      txtNombre,
                etiqueta("Descripción"), txtDescripcion,
                chkPublica,
                acciones);
        card.setPadding(new Insets(28));
        card.setMaxWidth(440);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setStyle("-fx-background-color: #181818; -fx-background-radius: 16px;"
                + " -fx-border-color: #2a2a2a; -fx-border-radius: 16px;");

        // ---- Mostrar como overlay in-app ----
        final Runnable cerrar = OverlayUtil.mostrar(ancla, card);

        btnCancelar.setOnAction(e -> cerrar.run());

        btnAceptar.setOnAction(e -> {
            final String nombre = txtNombre.getText().trim();
            final String desc   = txtDescripcion.getText().isBlank()
                    ? null : txtDescripcion.getText().trim();
            final String pub    = chkPublica.isSelected() ? "S" : "N";

            final Playlist playlist = new Playlist(nombre, desc, LocalDate.now(), pub,
                    cliente.getIdCliente());
            try {
                new PlaylistDAO().insertar(playlist);
                cerrar.run();
                if (onExito != null) onExito.run();
            } catch (final ConexionException ex) {
                final Alert alert = new Alert(Alert.AlertType.ERROR,
                        "No se pudo crear la playlist. Verifica la conexión.", ButtonType.OK);
                alert.setTitle("Error");
                if (ancla.getScene() != null) alert.initOwner(ancla.getScene().getWindow());
                alert.showAndWait();
            }
        });
    }

    private static Label etiqueta(final String texto) {
        final Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        return l;
    }
}
