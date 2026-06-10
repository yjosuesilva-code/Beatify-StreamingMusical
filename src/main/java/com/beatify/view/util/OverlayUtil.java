package com.beatify.view.util;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Muestra contenido como una capa (overlay) DENTRO de la misma ventana, en
 * lugar de abrir un Dialog/Stage separado del sistema operativo.
 *
 * Técnica: toma la Scene del nodo ancla, guarda su root actual y lo envuelve
 * en un StackPane que apila [rootOriginal, capaOscura(contenido)]. Al cerrar,
 * restaura el root original. Funciona con cualquier tipo de root (VBox,
 * BorderPane, etc.) porque no depende de su estructura interna.
 *
 * Uso típico:
 *   Runnable cerrar = OverlayUtil.mostrar(ancla, miTarjeta);
 *   ... botón Cancelar / Aceptar -> cerrar.run();
 */
public final class OverlayUtil {

    private OverlayUtil() {
    }

    /**
     * Muestra {@code contenido} centrado sobre el resto de la app, con un
     * velo oscuro detrás. Devuelve un Runnable que cierra el overlay y
     * restaura la pantalla original.
     *
     * @param ancla    cualquier nodo que ya esté en la escena (para obtenerla)
     * @param contenido la tarjeta/formulario a mostrar
     * @return Runnable para cerrar el overlay (o un no-op si no hay escena)
     */
    public static Runnable mostrar(final Node ancla, final Node contenido) {
        if (ancla == null || ancla.getScene() == null) {
            return () -> { /* sin escena: nada que cerrar */ };
        }
        final Scene scene = ancla.getScene();
        final Parent rootOriginal = scene.getRoot();

        // Velo + contenido centrado
        final StackPane velo = new StackPane(contenido);
        StackPane.setAlignment(contenido, Pos.CENTER);
        velo.setStyle("-fx-background-color: rgba(0,0,0,0.62);");
        velo.setPickOnBounds(true);   // captura clics fuera de la tarjeta

        // Apilar el root original (debajo) + el velo (encima)
        final StackPane contenedor = new StackPane(rootOriginal, velo);
        if (rootOriginal instanceof Region) {
            ((Region) rootOriginal).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }

        scene.setRoot(contenedor);

        // Cerrar: sacar el root original del contenedor y volver a ponerlo como root
        final Runnable cerrar = () -> {
            contenedor.getChildren().remove(rootOriginal);
            scene.setRoot(rootOriginal);
        };

        // Clic en el velo (fuera de la tarjeta) cierra
        velo.setOnMouseClicked(e -> {
            if (e.getTarget() == velo) cerrar.run();
        });

        return cerrar;
    }
}
