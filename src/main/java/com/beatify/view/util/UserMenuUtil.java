package com.beatify.view.util;

import com.beatify.view.SessionContext;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Construye y muestra el menú desplegable del usuario (topbar).
 * Llamar desde onUserMenu() en cualquier controller de app.
 */
public final class UserMenuUtil {

    private UserMenuUtil() {
    }

    /**
     * Muestra el menú anclado debajo de {@code ancla}.
     * Incluye: Mi perfil, Ver cuenta, separador, Cerrar sesión.
     */
    public static void mostrar(final Button ancla) {
        final ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("bf-user-menu");

        final MenuItem itemPerfil = new MenuItem("👤  Mi perfil");
        itemPerfil.setOnAction(e -> HistorialNavegacion.getInstance().navegar("/view/perfil.fxml"));

        final SeparatorMenuItem sep = new SeparatorMenuItem();

        final MenuItem itemSalir = new MenuItem("🚪  Cerrar sesión");
        itemSalir.setOnAction(e -> {
            SessionContext.getInstance().cerrarSesion();
            HistorialNavegacion.getInstance().reset();
            NavegacionUtil.cambiarA("/view/login.fxml", ancla);
        });

        menu.getItems().addAll(itemPerfil, sep, itemSalir);
        menu.show(ancla, Side.BOTTOM, 0, 4);
    }
}
