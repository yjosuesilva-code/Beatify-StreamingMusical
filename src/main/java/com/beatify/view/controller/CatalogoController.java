package com.beatify.view.controller;

import com.beatify.model.Cliente;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.component.MediaCard;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

import java.util.logging.Logger;

/**
 * Controller de la pantalla de Catálogo / Explorar.
 *
 * Muestra:
 *   - Filtros por género (chips seleccionables)
 *   - Ordenamiento (A-Z, Z-A, más reciente, etc.)
 *   - Tarjetas de géneros raíz (6 géneros con gradientes)
 *   - Grid de álbumes destacados (6 columnas)
 *   - Tracklist de canciones con número, título, álbum, género, duración
 */
public class CatalogoController {

    private static final Logger LOG = Logger.getLogger(CatalogoController.class.getName());

    // ---- TopBar ----
    @FXML private TextField txtBusqueda;
    @FXML private Button btnNotif;
    @FXML private Label lblNotifCount;
    @FXML private Button btnUserMenu;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Filtros ----
    @FXML private FlowPane filtrosPane;
    @FXML private ComboBox<String> cmbOrden;

    // ---- Contenido ----
    @FXML private FlowPane generosPane;
    @FXML private GridPane albumesGrid;
    @FXML private VBox tracklistBox;

    private String generoFiltro = null;

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarFiltros();
        configurarGeneros();
        configurarAlbumes();
        configurarTracklist();
    }

    // -----------------------------------------------------------------
    // TopBar y Sidebar (igual que HomeController)
    // -----------------------------------------------------------------

    private void configurarTopBar(final Cliente c) {
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#b794ff", "#8b5cf6",
                        c.getNombre() + " " + c.getApellido()));
        lblUserNombre.setText(c.getNombre());
        lblNotifCount.setText("5");
    }

    private void configurarSidebarPlaylists() {
        // Placeholder - igual que en HomeController
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
                    crearVBox(2,
                            crearLabel(pl[0], "bf-side-pl-name"),
                            crearLabel(pl[1], "bf-side-pl-meta")));
            item.setGraphic(row);
            sidebarPlaylistsBox.getChildren().add(item);
        }
    }

    // -----------------------------------------------------------------
    // Filtros (chips de género)
    // -----------------------------------------------------------------

    private void configurarFiltros() {
        final String[] generos = {
                "Todos", "Vallenato", "Cumbia", "Champeta",
                "Bullerengue", "Salsa", "Música andina", "Currulao", "Joropo"
        };

        for (final String g : generos) {
            final ToggleButton chip = new ToggleButton(g);
            chip.getStyleClass().add("bf-pref-chip");
            if ("Todos".equals(g)) chip.setSelected(true);
            chip.setOnAction(e -> {
                filtrosPane.getChildren().forEach(n -> ((ToggleButton) n).setSelected(false));
                chip.setSelected(true);
                generoFiltro = "Todos".equals(g) ? null : g;
                aplicarFiltro();
            });
            filtrosPane.getChildren().add(chip);
        }

        cmbOrden.getItems().addAll("A–Z", "Z–A", "Más reciente", "Más antiguo", "Más escuchado");
        cmbOrden.setValue("Más reciente");
        cmbOrden.setOnAction(e -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        LOG.info("Filtro aplicado — género: " + generoFiltro + ", orden: " + cmbOrden.getValue());
        configurarTracklist();
    }

    // -----------------------------------------------------------------
    // Géneros raíz (tarjetas grandes)
    // -----------------------------------------------------------------

    private void configurarGeneros() {
        final Object[][] datos = {
                {"Vallenato", "#c97a1f", "#3a1a05", "Cesar · Guajira"},
                {"Cumbia", "#1f7a5a", "#072a1a", "Caribe colombiano"},
                {"Champeta", "#a83232", "#3a0a0a", "Cartagena"},
                {"Bullerengue", "#d4a017", "#2a1a05", "Bolívar · Atlántico"},
                {"Música andina", "#3a6a8a", "#051a2a", "Boyacá · Cundinamarca"},
                {"Salsa colombiana", "#7a3a8a", "#1a052a", "Cali · Barranquilla"},
        };

        for (final Object[] d : datos) {
            final Button card = new Button();
            card.getStyleClass().add("bf-card");
            card.setPrefWidth(200);
            card.setPrefHeight(100);
            card.setMaxWidth(Double.MAX_VALUE);

            final StackPane sp = new StackPane();
            sp.setPrefSize(200, 100);
            sp.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                    + d[1] + ", " + d[2] + "); -fx-background-radius: 10;");

            final VBox info = new VBox(4);
            info.setAlignment(Pos.BOTTOM_LEFT);
            info.setPadding(new javafx.geometry.Insets(0, 0, 12, 14));
            final Label lblNombre = new Label((String) d[0]);
            lblNombre.setStyle("-fx-font-family: 'Sora'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
            final Label lblRegion = new Label((String) d[3]);
            lblRegion.setStyle("-fx-font-family: 'Manrope'; -fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.7);");
            info.getChildren().addAll(lblNombre, lblRegion);
            sp.getChildren().add(info);
            card.setGraphic(sp);

            final String generoNombre = (String) d[0];
            card.setOnAction(e -> {
                // Seleccionar el chip correspondiente
                for (var node : filtrosPane.getChildren()) {
                    ToggleButton tb = (ToggleButton) node;
                    tb.setSelected(tb.getText().equals(generoNombre));
                    if (tb.isSelected()) generoFiltro = generoNombre;
                    else if (tb.getText().equals("Todos")) tb.setSelected(false);
                }
                aplicarFiltro();
            });
            generosPane.getChildren().add(card);
        }
    }

    // -----------------------------------------------------------------
    // Álbumes destacados (grid 6 columnas)
    // -----------------------------------------------------------------

    private void configurarAlbumes() {
        final String[][] albumes = {
                {"Clásicos de la Provincia", "1993 · Carlos Vives", "#c97a1f", "#3a1a05"},
                {"Cumbia Cienaguera", "1988 · Totó la Momposina", "#1f7a5a", "#072a1a"},
                {"En Concierto", "1989 · Joe Arroyo", "#d4a017", "#2a1a05"},
                {"La Tierra del Olvido", "1995 · Carlos Vives", "#a83232", "#3a0a0a"},
                {"Cantos de Bullerengue", "2010 · Petrona Martínez", "#7a3a8a", "#1a052a"},
                {"Un Canto a la Vida", "2008 · Los Gaiteros", "#3a6a8a", "#051a2a"},
        };

        for (int i = 0; i < albumes.length; i++) {
            final String[] a = albumes[i];
            final MediaCard card = new MediaCard(a[2], a[3], a[0], a[0], a[1], false);
            GridPane.setColumnIndex(card, i);
            albumesGrid.getChildren().add(card);
        }
    }

    // -----------------------------------------------------------------
    // Tracklist de canciones
    // -----------------------------------------------------------------

    private void configurarTracklist() {
        tracklistBox.getChildren().clear();
        tracklistBox.getChildren().add(crearCabeceraTracklist());

        // Datos de canciones (placeholder)
        final String[][] canciones = {
                {"1", "La Gota Fría", "Carlos Vives", "Clásicos de la Provincia", "Vallenato", "4:33", "#c97a1f", "#3a1a05"},
                {"2", "La Cumbia Cienaguera", "Totó la Momposina", "Cumbia Cienaguera", "Cumbia", "3:48", "#1f7a5a", "#072a1a"},
                {"3", "La Rebelión", "Joe Arroyo", "En Concierto", "Salsa", "5:22", "#a83232", "#3a0a0a"},
                {"4", "La Tierra del Olvido", "Carlos Vives", "La Tierra del Olvido", "Vallenato", "4:15", "#d4a017", "#2a1a05"},
                {"5", "Mi Cafetal", "Carlos Vives", "Clásicos de la Provincia", "Vallenato", "3:55", "#c97a1f", "#3a1a05"},
                {"6", "Bullerengue", "Petrona Martínez", "Cantos de Bullerengue", "Bullerengue", "4:01", "#7a3a8a", "#1a052a"},
                {"7", "Toro Mata", "Los Gaiteros", "Un Canto a la Vida", "Cumbia", "3:30", "#3a6a8a", "#051a2a"},
                {"8", "Las Tapias", "Diomedes Díaz", "El Cacique", "Vallenato", "5:10", "#c97a1f", "#3a1a05"},
                {"9", "La Provincia", "Carlos Vives", "Clásicos de la Provincia", "Vallenato", "4:45", "#a83232", "#3a0a0a"},
                {"10", "Negrita", "Joe Arroyo", "Orígenes", "Salsa", "4:20", "#d4a017", "#2a1a05"},
        };

        for (final String[] c : canciones) {
            tracklistBox.getChildren().add(crearFilaTracklist(c));
        }
    }

    private HBox crearCabeceraTracklist() {
        final HBox header = new HBox(0);
        header.getStyleClass().add("bf-tracklist-header");
        header.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
        header.getChildren().addAll(
                crearLabel("#", 40, "bf-track-num"),
                crearLabel("TÍTULO", 280, "bf-track-col"),
                crearLabel("ÁLBUM", 180, "bf-track-col"),
                crearLabel("GÉNERO", 120, "bf-track-col"),
                crearLabel("DURACIÓN", 70, "bf-track-col")
        );
        return header;
    }

    private HBox crearFilaTracklist(final String[] c) {
        final HBox row = new HBox(0);
        row.getStyleClass().add("bf-tracklist-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));

        final Label lblNum = crearLabel(c[0], 40, "bf-track-num");

        final HBox titleCell = new HBox(10);
        titleCell.setAlignment(Pos.CENTER_LEFT);
        titleCell.setMinWidth(280);
        titleCell.getChildren().addAll(
                new AlbumCover(36, c[6], c[7]),
                crearVBoxTitulo(c[1], c[2])
        );

        final Label lblAlbum = crearLabel(c[3], 180, "bf-track-album");

        final Label lblGenero = new Label(c[4]);
        lblGenero.getStyleClass().add("bf-genre-pill");
        final HBox generoWrap = new HBox(lblGenero);
        generoWrap.setMinWidth(120);
        generoWrap.setAlignment(Pos.CENTER_LEFT);

        final Label lblDur = crearLabel(c[5], 70, "bf-track-dur");

        row.getChildren().addAll(lblNum, titleCell, lblAlbum, generoWrap, lblDur);
        return row;
    }

    private VBox crearVBoxTitulo(final String titulo, final String artista) {
        final VBox v = new VBox(2);
        final Label t = new Label(titulo);
        t.getStyleClass().add("bf-track-title");
        final Label a = new Label(artista);
        a.getStyleClass().add("bf-track-artist");
        v.getChildren().addAll(t, a);
        return v;
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------

    @FXML private void onAtras() { /* TODO historial */ }
    @FXML private void onAdelante() { /* TODO historial */ }
    @FXML private void onUserMenu() { /* TODO menú */ }
    @FXML private void onIrInicio() { NavegacionUtil.cambiarA("/view/home.fxml", btnUserMenu); }
    @FXML private void onIrExplorar() { /* ya estamos aquí */ }
    @FXML private void onIrBiblioteca() { LOG.info("Biblioteca — próximamente"); }
    @FXML private void onIrResenas() { NavegacionUtil.cambiarA("/view/resenas.fxml", btnUserMenu); }
    @FXML private void onIrBarrio() { NavegacionUtil.cambiarA("/view/barrio.fxml", btnUserMenu); }
    @FXML private void onIrCapsulas() { NavegacionUtil.cambiarA("/view/capsulas.fxml", btnUserMenu); }
    @FXML private void onIrLogros() { NavegacionUtil.cambiarA("/view/logros.fxml", btnUserMenu); }
    @FXML private void onNuevaPlaylist() { LOG.info("Nueva playlist"); }
    @FXML private void onVerTodosAlbumes() { LOG.info("Ver todos los álbumes"); }

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

    private static VBox crearVBox(final double spacing, final javafx.scene.Node... hijos) {
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