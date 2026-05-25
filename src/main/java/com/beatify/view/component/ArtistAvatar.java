package com.beatify.view.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Avatar circular de un artista o usuario:
 * un circulo con gradiente diagonal entre dos colores y las iniciales
 * del nombre centradas en blanco (Sora bold).
 *
 * Tamaños tipicos del handoff: 24, 28, 40, 44, 48, 64, 76, 96, 180 px.
 *
 * Uso:
 *   ArtistAvatar a = new ArtistAvatar(44, "#c97a1f", "#3a1a05", "Diomedes Diaz");
 *   contenedor.getChildren().add(a);
 *
 *   // o con iniciales explicitas:
 *   ArtistAvatar a2 = ArtistAvatar.conIniciales(44, "#c97a1f", "#3a1a05", "DD");
 */
public class ArtistAvatar extends StackPane {

    /**
     * Crea un avatar circular con gradiente y las iniciales calculadas
     * automaticamente desde {@code nombreCompleto}.
     *
     * @param size            diametro en pixeles
     * @param color1Hex       color del gradiente (esquina superior-izq)
     * @param color2Hex       color del gradiente (esquina inferior-der)
     * @param nombreCompleto  nombre del artista (se extraen las iniciales)
     */
    public ArtistAvatar(final double size, final String color1Hex,
                        final String color2Hex, final String nombreCompleto) {
        this(size, color1Hex, color2Hex, calcularIniciales(nombreCompleto));
    }

    /** Constructor privado con iniciales ya calculadas. */
    private ArtistAvatar(final double size, final String color1Hex,
                         final String color2Hex, final String iniciales) {

        this.setPrefSize(size, size);
        this.setMinSize(size, size);
        this.setMaxSize(size, size);
        this.setAlignment(Pos.CENTER);

        // Circulo con gradiente diagonal
        final Color c1 = Color.web(color1Hex);
        final Color c2 = Color.web(color2Hex);
        final LinearGradient gradiente = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, c1),
                new Stop(1, c2));

        final Circle circulo = new Circle(size / 2.0);
        circulo.setFill(gradiente);

        this.getChildren().add(circulo);

        // Iniciales centradas (Sora bold, blanco 90%, tamaño proporcional)
        if (iniciales != null && !iniciales.isBlank()) {
            final Label lbl = new Label(iniciales);
            lbl.setFont(Font.font("Sora", FontWeight.BOLD,
                    Math.max(9, size * 0.38)));
            lbl.setTextFill(Color.web("white", 0.9));
            this.getChildren().add(lbl);
        }
    }

    /** Factory alternativo cuando ya tenes las iniciales precalculadas. */
    public static ArtistAvatar conIniciales(final double size,
                                            final String color1Hex,
                                            final String color2Hex,
                                            final String iniciales) {
        return new ArtistAvatar(size, color1Hex, color2Hex, iniciales);
    }

    /**
     * Extrae las iniciales (maximo 2 letras) de un nombre.
     * Ej: "Diomedes Diaz" -> "DD"
     *     "Carlos Vives"  -> "CV"
     *     "Shakira"       -> "SH"
     *     "Los Gaiteros de San Jacinto" -> "LG"
     */
    private static String calcularIniciales(final String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "";
        }
        final String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        }
        // Una sola palabra: primeras 2 letras
        final String unica = partes[0];
        return unica.length() >= 2
                ? unica.substring(0, 2).toUpperCase()
                : unica.toUpperCase();
    }
}
