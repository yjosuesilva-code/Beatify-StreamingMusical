package com.beatify.view.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Tarjeta reutilizable para mostrar un album, playlist o artista en
 * las secciones de "Recomendado para ti" y "Artistas para descubrir"
 * del Home.
 *
 * Estructura:
 *   - Cover arriba (cuadrada con titulo, o circular para artistas)
 *   - Titulo debajo (Manrope 14 bold)
 *   - Subtitulo (Manrope 12, text-dim) — ej: "2002 · Carlos Vives" o "Vallenato · Valledupar"
 *
 * Uso:
 *   MediaCard c = new MediaCard("#c97a1f", "#3a1a05",
 *                               "Clasicos de la Provincia",
 *                               "Carlos Vives", "1993 · Carlos Vives", false);
 *
 *   MediaCard art = new MediaCard("#7a3a8a", "#1a052a",
 *                                 "Carlos Vives", null,
 *                                 "Vallenato · Santa Marta", true);  // circular
 */
public class MediaCard extends VBox {

    private static final double COVER_SIZE = 156;

    /**
     * @param color1Hex   color del gradiente (esquina superior-izq)
     * @param color2Hex   color del gradiente (esquina inferior-der)
     * @param titulo      titulo principal (debajo del cover)
     * @param tituloCover titulo opcional dentro del cover (null = sin titulo dentro)
     * @param subtitulo   subtitulo (ej: año + artista, o genero + ciudad)
     * @param circular    si {@code true} usa un avatar circular en vez de cover cuadrada
     */
    public MediaCard(final String color1Hex, final String color2Hex,
                     final String titulo, final String tituloCover,
                     final String subtitulo, final boolean circular) {

        this.getStyleClass().add("bf-card");
        this.setSpacing(0);
        this.setAlignment(Pos.TOP_LEFT);

        // ----- Wrapper del cover (para centrar si es circular) -----
        final StackPane wrap = new StackPane();
        wrap.setPrefHeight(COVER_SIZE);
        wrap.setAlignment(Pos.CENTER);

        if (circular) {
            // Avatar circular del artista (tamaño grande)
            wrap.getChildren().add(
                    new ArtistAvatar(COVER_SIZE, color1Hex, color2Hex,
                            tituloCover != null ? tituloCover : titulo));
        } else {
            // Cover cuadrada con titulo dentro
            wrap.getChildren().add(
                    new AlbumCover(COVER_SIZE, color1Hex, color2Hex, tituloCover));
        }

        this.getChildren().add(wrap);

        // ----- Titulo + subtitulo debajo -----
        final VBox info = new VBox(4);
        info.setStyle("-fx-padding: 12 0 0 0;");

        if (titulo != null && !titulo.isBlank()) {
            final Label lblTitulo = new Label(titulo);
            lblTitulo.getStyleClass().add("bf-card-title");
            info.getChildren().add(lblTitulo);
        }
        if (subtitulo != null && !subtitulo.isBlank()) {
            final Label lblSub = new Label(subtitulo);
            lblSub.getStyleClass().add("bf-card-sub");
            info.getChildren().add(lblSub);
        }

        this.getChildren().add(info);
    }

    /** Variante simple cuadrada sin titulo dentro del cover. */
    public MediaCard(final String color1Hex, final String color2Hex,
                     final String titulo, final String subtitulo) {
        this(color1Hex, color2Hex, titulo, titulo, subtitulo, false);
    }

    /**
     * Hace la tarjeta clicable. Devuelve {@code this} para encadenar.
     * Ej: {@code new MediaCard(...).onClick(() -> reproducir(album));}
     */
    public MediaCard onClick(final Runnable accion) {
        if (accion != null) {
            this.setStyle("-fx-cursor: hand;");
            this.setOnMouseClicked(e -> accion.run());
        }
        return this;
    }
}
