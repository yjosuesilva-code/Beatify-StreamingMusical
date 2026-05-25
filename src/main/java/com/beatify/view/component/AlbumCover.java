package com.beatify.view.component;

import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Representacion visual de una portada de album o playlist usando
 * un gradiente diagonal y, opcionalmente, el titulo superpuesto.
 *
 * Cada album tiene dos colores de paleta (color1, color2) que forman
 * el gradiente de fondo. El titulo va en blanco semitransparente,
 * familia Sora, con tamaño proporcional al lado del cover.
 *
 * Tamaños tipicos del handoff: 36, 56, 120, 160, 200, 420 px.
 *
 * Uso:
 *   AlbumCover cover = new AlbumCover(120, "#c97a1f", "#3a1a05",
 *                                     "Clasicos de la Provincia");
 *   contenedor.getChildren().add(cover);
 */
public class AlbumCover extends StackPane {

    /**
     * Crea una portada cuadrada con gradiente diagonal entre dos colores.
     *
     * @param size      lado en pixeles
     * @param color1Hex color superior-izquierdo (formato "#rrggbb")
     * @param color2Hex color inferior-derecho (formato "#rrggbb")
     * @param titulo    titulo opcional sobrepuesto (puede ser null o vacio)
     */
    public AlbumCover(final double size, final String color1Hex,
                      final String color2Hex, final String titulo) {

        this.setPrefSize(size, size);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);
        this.setAlignment(Pos.CENTER);

        // Gradiente diagonal (135 grados, de esquina superior-izq a inferior-der)
        final Color c1 = Color.web(color1Hex);
        final Color c2 = Color.web(color2Hex);
        final LinearGradient gradiente = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, c1),
                new Stop(1, c2));

        // Radio dependiente del tamaño (covers pequeños usan radio menor)
        final double radio = size <= 56 ? 3 : (size <= 120 ? 6 : 10);

        this.setBackground(new Background(
                new BackgroundFill(gradiente, new CornerRadii(radio), null)));

        // Titulo superpuesto (opcional)
        if (titulo != null && !titulo.isBlank() && size >= 56) {
            final Label lbl = new Label(titulo);
            lbl.setFont(Font.font("Sora", FontWeight.BOLD,
                    Math.max(10, size * 0.11)));
            lbl.setTextFill(Color.web("white", 0.85));
            lbl.setWrapText(true);
            lbl.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 6, 0, 0, 2);");
            lbl.setMaxWidth(size * 0.85);
            this.getChildren().add(lbl);
        }
    }

    /** Variante sin titulo: solo el gradiente (util para covers chiquitos). */
    public AlbumCover(final double size, final String color1Hex, final String color2Hex) {
        this(size, color1Hex, color2Hex, null);
    }
}
