package com.beatify.view.util;

import com.beatify.model.Cliente;
import com.beatify.view.SessionContext;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Construye y muestra el menú desplegable del usuario (topbar).
 * Diseño: tarjeta oscura con cabecera (nombre + correo) y acciones con icono.
 * Llamar desde onUserMenu() en cualquier controller de app.
 */
public final class UserMenuUtil {

    private UserMenuUtil() {
    }

    /** Muestra el menú anclado debajo de {@code ancla}. */
    public static void mostrar(final Button ancla) {
        final ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("bf-user-menu");

        final Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual != null) {
            menu.getItems().addAll(cabecera(actual), new SeparatorMenuItem());
        }

        menu.getItems().add(item("Mi perfil", "bi-person", false,
                () -> HistorialNavegacion.getInstance().navegar("/view/perfil.fxml")));
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(item("Cerrar sesión", "bi-box-arrow-right", true, () -> {
            SessionContext.getInstance().cerrarSesion();
            HistorialNavegacion.getInstance().reset();
            NavegacionUtil.cambiarA("/view/login.fxml", ancla);
        }));

        menu.show(ancla, Side.BOTTOM, 0, 6);
    }

    /** Cabecera no interactiva con el nombre y el correo del usuario. */
    private static CustomMenuItem cabecera(final Cliente c) {
        final String nombre = ((c.getNombre() == null ? "" : c.getNombre()) + " "
                + (c.getApellido() == null ? "" : c.getApellido())).trim();

        final Label lblNombre = new Label(nombre.isEmpty() ? "Mi cuenta" : nombre);
        lblNombre.getStyleClass().add("bf-um-name");

        final Label lblCorreo = new Label(c.getCorreo() == null ? "" : c.getCorreo());
        lblCorreo.getStyleClass().add("bf-um-mail");

        final VBox box = new VBox(1, lblNombre, lblCorreo);
        box.getStyleClass().add("bf-um-header");

        final CustomMenuItem item = new CustomMenuItem(box, false);
        item.setHideOnClick(false);
        return item;
    }

    /** Item de acción con icono + etiqueta. {@code peligro} lo pinta en rojo. */
    private static CustomMenuItem item(final String texto, final String icono,
                                       final boolean peligro, final Runnable accion) {
        final FontIcon fi = new FontIcon(icono);
        fi.setIconSize(15);
        fi.getStyleClass().add("bf-um-icon");

        final Label lbl = new Label(texto);
        lbl.getStyleClass().add("bf-um-lbl");

        final HBox box = new HBox(10, fi, lbl);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("bf-um-row");

        final CustomMenuItem item = new CustomMenuItem(box, true);
        if (peligro) item.getStyleClass().add("bf-um-danger");
        item.setOnAction(e -> accion.run());
        return item;
    }
}
