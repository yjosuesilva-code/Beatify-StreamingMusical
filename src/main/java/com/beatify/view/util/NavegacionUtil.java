package com.beatify.view.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utilidad para cambiar entre vistas dentro del mismo Stage.
 * Carga el FXML indicado, aplica los stylesheets globales de Beatify
 * y reemplaza la Scene actual.
 *
 * Todas las vistas comparten los mismos stylesheets:
 *   - beatify-tokens.css  (looked-up colors + tipografia)
 *   - beatify-auth.css    (estilos comunes de Login y Registro)
 *   - beatify-home.css    (estilos de Home: sidebar, topbar, cards, miniplayer)
 */
public final class NavegacionUtil {

    /** Lista de stylesheets aplicados a TODAS las escenas. */
    private static final String[] STYLESHEETS = {
            "/style/beatify-tokens.css",
            "/style/beatify-auth.css",
            "/style/beatify-home.css",
            "/style/beatify-screens.css",
            "/style/beatify-admin.css"
    };

    private NavegacionUtil() {
    }

    /**
     * Cambia la Scene del Stage al que pertenece {@code nodoActual}, cargando
     * el FXML indicado (ej: "/view/login.fxml") y aplicando los estilos globales.
     *
     * @param rutaFxml   ruta absoluta dentro de resources, p.ej. "/view/login.fxml"
     * @param nodoActual cualquier Node de la escena actual (boton, link, etc.) —
     *                   se usa para encontrar el Stage activo
     * @throws RuntimeException si el FXML no se puede cargar
     */
    public static void cambiarA(final String rutaFxml, final Node nodoActual) {
        try {
            final Parent root = FXMLLoader.load(NavegacionUtil.class.getResource(rutaFxml));
            final Stage stage = (Stage) nodoActual.getScene().getWindow();
            final Scene scene = new Scene(root);

            // Aplicar estilos
            for (final String ruta : STYLESHEETS) {
                final java.net.URL url = NavegacionUtil.class.getResource(ruta);
                if (url != null) {
                    scene.getStylesheets().add(url.toExternalForm());
                }
            }

            stage.setScene(scene);
            stage.setWidth(1280);   // ← AGREGAR ESTO
            stage.setHeight(800);   // ← AGREGAR ESTO
            stage.centerOnScreen();

        } catch (final IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + rutaFxml, e);
        }
    }

    /**
     * Variante usada por {@link HistorialNavegacion}: navega sin necesitar un Node,
     * usando el Stage directamente.
     */
    static void cambiarDesdeHistorial(final String rutaFxml, final Stage stage) {
        try {
            final Parent root  = FXMLLoader.load(NavegacionUtil.class.getResource(rutaFxml));
            final Scene  scene = new Scene(root);
            for (final String ruta : STYLESHEETS) {
                final java.net.URL url = NavegacionUtil.class.getResource(ruta);
                if (url != null) scene.getStylesheets().add(url.toExternalForm());
            }
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(800);
            stage.centerOnScreen();
        } catch (final IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + rutaFxml, e);
        }
    }
}
