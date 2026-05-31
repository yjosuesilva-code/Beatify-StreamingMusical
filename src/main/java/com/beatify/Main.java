package com.beatify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Punto de entrada de la aplicacion JavaFX Beatify.
 *
 * Flujo de arranque:
 *   1. {@link Application#launch(String...)} llama a {@link #start(Stage)}.
 *   2. Cargamos las fuentes Manrope, Sora y JetBrains Mono desde
 *      {@code resources/fonts/} (sin esto el CSS cae a las fuentes default
 *      del sistema).
 *   3. Cargamos {@code /view/login.fxml} como escena inicial.
 *   4. Aplicamos los 3 stylesheets globales:
 *        - beatify-tokens.css  (looked-up colors + tipografia)
 *        - beatify-auth.css    (Login y Registro)
 *        - beatify-home.css    (sidebar, topbar, cards, miniplayer)
 *   5. Configuramos el Stage y lo mostramos.
 *
 * Cuando el usuario se autentica, {@code LoginController} llama a
 * {@code NavegacionUtil.cambiarA("/view/home.fxml", ...)} que reemplaza
 * la Scene del mismo Stage. No se abren ventanas nuevas.
 */
public class Main extends Application {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    /** Tamaño minimo para no romper el layout en pantallas pequeñas. */
    private static final double ANCHO_MIN = 980.0;
    private static final double ALTO_MIN  = 640.0;

    @Override
    public void start(final Stage stage) {
        cargarFuentes();
        try {
            final Parent root = FXMLLoader.load(
                    getClass().getResource("/view/login.fxml"));

            // Tamaño inicial: 90% de la pantalla del usuario (cabra en cualquier monitor)
            final javafx.geometry.Rectangle2D pantalla =
                    javafx.stage.Screen.getPrimary().getVisualBounds();
            final double anchoInicial = Math.min(1440, pantalla.getWidth()  * 0.90);
            final double altoInicial  = Math.min(900,  pantalla.getHeight() * 0.90);

            final Scene scene = new Scene(root, anchoInicial, altoInicial);
            aplicarStylesheets(scene);

            stage.setTitle("Beatify — Inicio de sesión");
            stage.setScene(scene);
            stage.setMinWidth(ANCHO_MIN);
            stage.setMinHeight(ALTO_MIN);
            stage.centerOnScreen();
            stage.show();

        } catch (final Exception e) {
            LOG.log(Level.SEVERE, "No se pudo iniciar la UI", e);
            throw new RuntimeException(e);
        }
    }

    /** Carga las 7 fuentes del tema desde resources/fonts/. */
    private void cargarFuentes() {
        final String[] rutas = {
                "/fonts/Manrope-Regular.ttf",
                "/fonts/Manrope-Medium.ttf",
                "/fonts/Manrope-SemiBold.ttf",
                "/fonts/Manrope-Bold.ttf",
                "/fonts/Sora-Bold.ttf",
                "/fonts/Sora-ExtraBold.ttf",
                "/fonts/JetBrainsMono-Medium.ttf"
        };
        for (final String ruta : rutas) {
            try (InputStream in = getClass().getResourceAsStream(ruta)) {
                if (in == null) {
                    LOG.warning("Fuente no encontrada: " + ruta);
                    continue;
                }
                Font.loadFont(in, 14);
            } catch (final Exception e) {
                LOG.log(Level.WARNING, "Error cargando fuente " + ruta, e);
            }
        }
    }

    /** Agrega los stylesheets globales a la Scene. */
    private void aplicarStylesheets(final Scene scene) {
        final String[] hojas = {
                "/style/beatify-tokens.css",
                "/style/beatify-auth.css",
                "/style/beatify-home.css",
                "/style/beatify-screens.css"
        };
        for (final String hoja : hojas) {
            final var url = getClass().getResource(hoja);
            if (url == null) {
                LOG.warning("Stylesheet no encontrado: " + hoja);
                continue;
            }
            scene.getStylesheets().add(url.toExternalForm());
        }
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
