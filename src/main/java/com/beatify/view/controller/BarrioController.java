package com.beatify.view.controller;

import com.beatify.model.Cliente;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

import java.util.logging.Logger;

/**
 * Controller de la pantalla Música del Barrio (barrio.fxml).
 *
 * Funcionalidad distintiva #4 de Beatify.
 *
 * Muestra los artistas más reproducidos por usuarios de la misma ciudad
 * del cliente activo durante un periodo seleccionado.
 *
 * Componentes principales:
 *   - Feature header con la ciudad del usuario
 *   - Segmented control: Hoy / Esta semana / Este mes / 6 meses / 1 año
 *   - Podio top 3 estilo olímpico (barras de altura variable)
 *   - Tabla completa con rank, trend, avatar, artista, oyentes locales y nacionales
 */
public class BarrioController {

    private static final Logger LOG = Logger.getLogger(BarrioController.class.getName());

    // ---- TopBar ----
    @FXML private Button btnNotif;
    @FXML private Button btnUserMenu;
    @FXML private Label lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Header ----
    @FXML private Label lblCiudadChip;

    // ---- Segmented control ----
    @FXML private ToggleButton tabHoy;
    @FXML private ToggleButton tabSemana;
    @FXML private ToggleButton tabMes;
    @FXML private ToggleButton tab6M;
    @FXML private ToggleButton tabAnio;

    // ---- Podio ----
    @FXML private VBox podioContainer;

    // ---- Tabla completa ----
    @FXML private VBox tablaBox;

    private String periodoActivo = "semana";

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarHeader(actual);
        configurarFiltros();
        cargarRanking(actual);
    }

    // -----------------------------------------------------------------
    // TopBar y Sidebar
    // -----------------------------------------------------------------

    private void configurarTopBar(final Cliente c) {
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#b794ff", "#8b5cf6",
                        c.getNombre() + " " + c.getApellido()));
        lblUserNombre.setText(c.getNombre());
        lblNotifCount.setText("5");
    }

    private void configurarSidebarPlaylists() {
        final String[][] playlists = {
                {"Mis Vallenatos Clásicos", "Yo · 24 canc.", "#c97a1f", "#3a1a05"},
                {"Cumbia del Caribe", "Yo · 18 canc.", "#1f7a5a", "#072a1a"},
                {"Para escribir tesis", "Yo · 42 canc.", "#3a6a8a", "#051a2a"},
                {"Fiesta de Sábado", "Andrés Z. · 31 canc.", "#a83232", "#3a0a0a"},
                {"Champeta Total", "Kendrick S. · 27 canc.", "#d4a017", "#2a1a05"},
                {"Raíces Andinas", "Yo · 15 canc.", "#7a3a8a", "#1a052a"},
        };

        for (final String[] pl : playlists) {
            final Button item = new Button();
            item.getStyleClass().add("bf-side-playlist");
            item.setMaxWidth(Double.MAX_VALUE);

            final HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().addAll(
                    new AlbumCover(32, pl[2], pl[3]),
                    construirVBox(2,
                            crearLabel(pl[0], "bf-side-pl-name"),
                            crearLabel(pl[1], "bf-side-pl-meta")));
            item.setGraphic(row);
            sidebarPlaylistsBox.getChildren().add(item);
        }
    }

    // -----------------------------------------------------------------
    // Header
    // -----------------------------------------------------------------

    private void configurarHeader(final Cliente c) {
        final String ciudad = c.getCiudad() == null ? "Valledupar" : c.getCiudad();
        lblCiudadChip.setText(ciudad);
    }

    // -----------------------------------------------------------------
    // Segmented control
    // -----------------------------------------------------------------

    private void configurarFiltros() {
        tabSemana.setSelected(true);

        tabHoy.setOnAction(e -> { periodoActivo = "hoy"; recargar(); });
        tabSemana.setOnAction(e -> { periodoActivo = "semana"; recargar(); });
        tabMes.setOnAction(e -> { periodoActivo = "mes"; recargar(); });
        tab6M.setOnAction(e -> { periodoActivo = "6m"; recargar(); });
        tabAnio.setOnAction(e -> { periodoActivo = "anio"; recargar(); });
    }

    private void recargar() {
        podioContainer.getChildren().clear();
        tablaBox.getChildren().clear();
        cargarRanking(SessionContext.getInstance().getClienteActual());
    }

    // -----------------------------------------------------------------
    // Ranking (datos placeholder)
    // -----------------------------------------------------------------

    private void cargarRanking(final Cliente cliente) {
        final String ciudad = cliente.getCiudad() == null ? "Valledupar" : cliente.getCiudad();

        // Formato: {rank, nombre, genero, oyentesCiudad, oyentesNacional, trend, c1, c2}
        final Object[][] datos = {
                {1, "Diomedes Díaz", "Vallenato", "12.3k", "850k", "up", "#c97a1f", "#3a1a05"},
                {2, "Carlos Vives", "Vallenato", "10.7k", "2.3M", "up", "#1f7a5a", "#072a1a"},
                {3, "Silvestre Dangond", "Vallenato", "9.2k", "1.1M", "same", "#d4a017", "#2a1a05"},
                {4, "Jorge Oñate", "Vallenato", "7.8k", "320k", "down", "#a83232", "#3a0a0a"},
                {5, "Los Gaiteros de San Jacinto", "Cumbia", "5.4k", "150k", "up", "#7a3a8a", "#1a052a"},
                {6, "Totó la Momposina", "Cumbia", "4.9k", "210k", "up", "#3a6a8a", "#051a2a"},
                {7, "Joe Arroyo", "Salsa", "4.1k", "490k", "same", "#c97a1f", "#3a1a05"},
                {8, "Petrona Martínez", "Bullerengue", "3.6k", "85k", "down", "#1f7a5a", "#072a1a"},
                {9, "Iván Villazón", "Vallenato", "3.2k", "190k", "up", "#d4a017", "#2a1a05"},
                {10, "Binomio de Oro", "Vallenato", "2.8k", "245k", "down", "#a83232", "#3a0a0a"},
        };

        construirPodio(datos, ciudad);
        construirTabla(datos, ciudad);
    }

    // -----------------------------------------------------------------
    // Podio (top 3 estilo olímpico)
    // -----------------------------------------------------------------

    private void construirPodio(final Object[][] datos, final String ciudad) {
        if (datos.length < 3) return;

        // Orden de aparición en el podio: 2°, 1°, 3°
        final int[] orden = {1, 0, 2};

        final HBox podio = new HBox(16);
        podio.setAlignment(Pos.BOTTOM_CENTER);
        podio.setPadding(new javafx.geometry.Insets(20, 0, 20, 0));

        for (final int idx : orden) {
            final Object[] d = datos[idx];
            final int rank = (int) d[0];
            final String nombre = (String) d[1];
            final String genero = (String) d[2];
            final String oyentes = (String) d[3];
            final String c1 = (String) d[6];
            final String c2 = (String) d[7];

            final double avatarSize = rank == 1 ? 96 : 76;
            final double baseH = rank == 1 ? 110 : rank == 2 ? 85 : 70;

            final VBox slot = new VBox(8);
            slot.setAlignment(Pos.BOTTOM_CENTER);
            slot.setPrefWidth(160);

            // Avatar
            final ArtistAvatar avatar = new ArtistAvatar(avatarSize, c1, c2, nombre);

            // Nombre + género
            final Label lblNombre = new Label(nombre);
            lblNombre.setStyle("-fx-font-family: 'Manrope Bold'; -fx-font-size: 14px; -fx-text-fill: -bf-text; -fx-wrap-text: true; -fx-alignment: center;");
            lblNombre.setWrapText(true);
            lblNombre.setAlignment(Pos.CENTER);

            final Label lblGenero = new Label(genero);
            lblGenero.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 11px; -fx-text-fill: -bf-text-dim;");

            final Label lblOyentes = new Label(oyentes + " · " + ciudad);
            lblOyentes.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 10px; -fx-text-fill: -bf-text-muted;");

            // Barra de la columna
            final Region barra = new Region();
            barra.setPrefHeight(baseH);
            barra.setPrefWidth(120);
            barra.setStyle("-fx-background-radius: 8px 8px 0 0;");
            if (rank == 1) barra.setStyle("-fx-background-color: linear-gradient(to bottom, #c97a1f, #3a1a05); -fx-background-radius: 8px 8px 0 0;");
            else if (rank == 2) barra.setStyle("-fx-background-color: linear-gradient(to bottom, #1f7a5a, #072a1a); -fx-background-radius: 8px 8px 0 0;");
            else barra.setStyle("-fx-background-color: linear-gradient(to bottom, #3a6a8a, #051a2a); -fx-background-radius: 8px 8px 0 0;");

            // Badge de posición
            final Label lblRank = new Label("#" + rank);
            lblRank.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 12px; -fx-padding: 2px 8px; -fx-background-radius: 999px;");
            if (rank == 1) lblRank.setStyle(lblRank.getStyle() + "-fx-background-color: #F2C94C; -fx-text-fill: #1a1a1a;");

            slot.getChildren().addAll(avatar, lblNombre, lblGenero, lblOyentes, lblRank, barra);
            podio.getChildren().add(slot);
        }

        podioContainer.getChildren().add(podio);
    }

    // -----------------------------------------------------------------
    // Tabla completa
    // -----------------------------------------------------------------

    private void construirTabla(final Object[][] datos, final String ciudad) {
        // Cabecera
        final HBox header = new HBox(0);
        header.setStyle("-fx-background-color: -bf-bg-3; -fx-padding: 12 16; -fx-border-color: -bf-border; -fx-border-width: 0 0 1 0;");
        header.getChildren().addAll(
                crearLabel("#", 40, "bf-track-num"),
                crearLabel("", 16, "bf-track-col"),
                crearLabel("ARTISTA", 260, "bf-track-col"),
                crearLabel("EN " + ciudad.toUpperCase(), 130, "bf-track-col"),
                crearLabel("NACIONAL", 100, "bf-track-col")
        );
        tablaBox.getChildren().add(header);

        for (final Object[] d : datos) {
            tablaBox.getChildren().add(construirFilaTabla(d, ciudad));
        }
    }

    private HBox construirFilaTabla(final Object[] d, final String ciudad) {
        final int rank = (int) d[0];
        final String nombre = (String) d[1];
        final String genero = (String) d[2];
        final String oyLoc = (String) d[3];
        final String oyNac = (String) d[4];
        final String trend = (String) d[5];
        final String c1 = (String) d[6];
        final String c2 = (String) d[7];

        final HBox row = new HBox(0);
        row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 12 16; -fx-background-color: -bf-border;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;"));

        // Rank
        final Label lblRank = new Label(String.format("%02d", rank));
        lblRank.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 13px; -fx-text-fill: -bf-text-dim;");
        lblRank.setMinWidth(40);

        // Trend
        final Label lblTrend = new Label(switch (trend) {
            case "up" -> "↑";
            case "down" -> "↓";
            default -> "—";
        });
        String trendColor = switch (trend) {
            case "up" -> "#7ed957";
            case "down" -> "#e57373";
            default -> "-bf-text-dim";
        };
        lblTrend.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 14px; -fx-text-fill: " + trendColor + ";");
        lblTrend.setMinWidth(16);

        // Avatar + nombre + género
        final HBox artistaCell = new HBox(10);
        artistaCell.setAlignment(Pos.CENTER_LEFT);
        artistaCell.setMinWidth(260);
        final ArtistAvatar avatar = new ArtistAvatar(40, c1, c2, nombre);
        final VBox info = new VBox(2);
        final Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-family: 'Manrope SemiBold'; -fx-font-size: 14px; -fx-text-fill: -bf-text;");
        final Label lblGenero = new Label(genero + " · " + ciudad);
        lblGenero.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 12px; -fx-text-fill: -bf-text-dim;");
        info.getChildren().addAll(lblNombre, lblGenero);
        artistaCell.getChildren().addAll(avatar, info);

        // Oyentes ciudad
        final Label lblLoc = new Label(oyLoc);
        lblLoc.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 14px; -fx-text-fill: -bf-accent;");
        lblLoc.setMinWidth(130);

        // Oyentes nacional
        final Label lblNac = new Label(oyNac);
        lblNac.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 13px; -fx-text-fill: -bf-text-dim;");
        lblNac.setMinWidth(100);

        row.getChildren().addAll(lblRank, lblTrend, artistaCell, lblLoc, lblNac);
        return row;
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------

    @FXML private void onAtras() { NavegacionUtil.cambiarA("/view/home.fxml", btnUserMenu); }
    @FXML private void onAdelante() {}
    @FXML private void onUserMenu() {}
    @FXML private void onIrNotificaciones() {}
    @FXML private void onIrInicio() { NavegacionUtil.cambiarA("/view/home.fxml", btnUserMenu); }
    @FXML private void onIrExplorar() { NavegacionUtil.cambiarA("/view/catalogo.fxml", btnUserMenu); }
    @FXML private void onIrBiblioteca() { LOG.info("Biblioteca — próximamente"); }
    @FXML private void onIrResenas() { NavegacionUtil.cambiarA("/view/resenas.fxml", btnUserMenu); }
    @FXML private void onIrBarrio() { /* ya estamos aquí */ }
    @FXML private void onIrCapsulas() { NavegacionUtil.cambiarA("/view/capsulas.fxml", btnUserMenu); }
    @FXML private void onIrLogros() { NavegacionUtil.cambiarA("/view/logros.fxml", btnUserMenu); }
    @FXML private void onNuevaPlaylist() { LOG.info("Nueva playlist"); }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------

    private static Label crearLabel(final String texto, final double minWidth, final String... classes) {
        final Label l = new Label(texto);
        l.getStyleClass().addAll(classes);
        l.setMinWidth(minWidth);
        return l;
    }

    private static Label crearLabel(final String texto, final String... classes) {
        final Label l = new Label(texto);
        l.getStyleClass().addAll(classes);
        return l;
    }

    private static VBox construirVBox(final double spacing, final javafx.scene.Node... hijos) {
        final VBox v = new VBox(spacing);
        v.getChildren().addAll(hijos);
        return v;
    }

    private static Cliente clientePlaceholder() {
        final Cliente c = new Cliente();
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        return c;
    }
}