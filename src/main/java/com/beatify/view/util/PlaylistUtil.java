package com.beatify.view.util;

import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.view.SessionContext;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

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
        txtNombre.setPrefWidth(260);

        final TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción opcional...");
        txtDescripcion.setPrefRowCount(3);
        txtDescripcion.setWrapText(true);

        final CheckBox chkPublica = new CheckBox("Visible para otros usuarios");
        chkPublica.setSelected(false);

        final GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));
        grid.add(new Label("Nombre:"),      0, 0); grid.add(txtNombre,      1, 0);
        grid.add(new Label("Descripción:"), 0, 1); grid.add(txtDescripcion, 1, 1);
        grid.add(chkPublica,                1, 2);

        // ---- Diálogo ----
        final Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nueva playlist");
        dialog.setHeaderText("Crea una nueva lista de reproducción");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (ancla.getScene() != null) {
            dialog.initOwner(ancla.getScene().getWindow());
        }

        // Deshabilitar OK si nombre vacío
        final Node btnOk = dialog.getDialogPane().lookupButton(ButtonType.OK);
        btnOk.setDisable(true);
        txtNombre.textProperty().addListener((obs, o, v) -> btnOk.setDisable(v.isBlank()));

        dialog.showAndWait().ifPresent(tipo -> {
            if (tipo != ButtonType.OK) return;

            final String nombre = txtNombre.getText().trim();
            final String desc   = txtDescripcion.getText().isBlank()
                    ? null : txtDescripcion.getText().trim();
            final String pub    = chkPublica.isSelected() ? "S" : "N";

            final Playlist playlist = new Playlist(nombre, desc, LocalDate.now(), pub,
                    cliente.getIdCliente());
            try {
                new PlaylistDAO().insertar(playlist);
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
}
