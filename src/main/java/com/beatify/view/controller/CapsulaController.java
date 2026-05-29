package com.beatify.view.controller;

import com.beatify.model.Cliente;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Controller de la pantalla Cápsulas del Tiempo (capsulas.fxml).
 *
 * Funcionalidad distintiva #3 de Beatify.
 *
 * Permite al usuario consultar qué canciones reprodujo en un rango de
 * fechas pasado, generando una vista cronológica de su historial.
 *
 * Flujo:
 *   1. El usuario elige un preset de período o rango personalizado.
 *   2. Al pulsar "Generar" se recarga la línea de tiempo.
 *   3. Al clickear un nodo se despliega el detalle de esa cápsula.
 */
public class CapsulaController {

    private static final Logger LOG = Logger.getLogger(CapsulaController.class.getName());

    // ---- TopBar ----
    @FXML private Button btnNotif;
    @FXML private Button btnUserMenu;
    @FXML private Label lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Controles de rango ----
    @FXML private ComboBox<String> cmbPreset;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private Button btnGenerar;

    // ---- Línea de tiempo ----
    @FXML private HBox timelineBox;

    // ---- Detalle de la cápsula seleccionada ----
    @FXML private VBox detalleCapsule;
    @FXML private StackPane heroCoverHolder;
    @FXML private Label lblHeroTitulo;
    @FXML private Label lblHeroDesc;
    @FXML private Label lblHeroReproducciones;

    // ---- Stat cards ----
    @FXML private Label lblTopArtistaNombre;
    @FXML private Label lblTopArtistaPct;
    @FXML private Label lblTopCancionNombre;
    @FXML private Label lblTopCancionPlays;
    @FXML private Label lblGeneroNombre;
    @FXML private Label lblNuevosArtistas;

    // ---- Tracklist de la cápsula ----
    @FXML private VBox tracklistCapsula;

    private int capsuleSeleccionada = 0;

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarControlesRango();
        construirTimeline();
        mostrarDetalleCapsule(0);
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
    // Controles de rango
    // -----------------------------------------------------------------

    private void configurarControlesRango() {
        cmbPreset.getItems().addAll(
                "Último mes", "Últimos 3 meses", "Últimos 6 meses",
                "Este año", "Año pasado", "Rango personalizado");
        cmbPreset.setValue("Últimos 6 meses");

        dpDesde.setValue(LocalDate.now().minusMonths(6));
        dpHasta.setValue(LocalDate.now());
        dpDesde.setVisible(false);
        dpDesde.setManaged(false);
        dpHasta.setVisible(false);
        dpHasta.setManaged(false);

        cmbPreset.setOnAction(e -> {
            boolean custom = "Rango personalizado".equals(cmbPreset.getValue());
            dpDesde.setVisible(custom);
            dpDesde.setManaged(custom);
            dpHasta.setVisible(custom);
            dpHasta.setManaged(custom);
        });
    }

    // -----------------------------------------------------------------
    // Línea de tiempo
    // -----------------------------------------------------------------

    private void construirTimeline() {
        timelineBox.getChildren().clear();

        final String[][] nodos = {
                {"may 2026", "Este mes"},
                {"abr 2026", "Hace 1 mes"},
                {"feb 2026", "Hace 3 meses"},
                {"nov 2025", "Hace 6 meses"},
                {"may 2025", "Hace 1 año"},
        };

        for (int i = 0; i < nodos.length; i++) {
            final int idx = i;
            final String[] n = nodos[i];

            if (i > 0) {
                final Region linea = new Region();
                linea.setStyle("-fx-background-color: -bf-border; -fx-pref-width: 48px; -fx-pref-height: 2px;");
                timelineBox.getChildren().add(linea);
            }

            final VBox nodo = new VBox(6);
            nodo.setAlignment(Pos.CENTER);
            nodo.setStyle("-fx-padding: 10px; -fx-cursor: hand;");
            if (idx == capsuleSeleccionada) nodo.setStyle(nodo.getStyle() + "-fx-background-color: rgba(183, 148, 255, 0.1); -fx-background-radius: 12px;");

            final StackPane dot = new StackPane();
            dot.setPrefSize(14, 14);
            dot.setStyle(idx == capsuleSeleccionada
                    ? "-fx-background-color: #b794ff; -fx-background-radius: 999px;"
                    : "-fx-background-color: -bf-border; -fx-background-radius: 999px;");

            final Label lblFecha = new Label(n[0]);
            lblFecha.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 12px; -fx-text-fill: -bf-text-muted;");
            final Label lblSub = new Label(n[1]);
            lblSub.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 10px; -fx-text-fill: -bf-text-dim;");

            nodo.getChildren().addAll(dot, lblFecha, lblSub);
            nodo.setOnMouseClicked(e -> {
                capsuleSeleccionada = idx;
                construirTimeline();
                mostrarDetalleCapsule(idx);
            });

            timelineBox.getChildren().add(nodo);
        }
    }

    // -----------------------------------------------------------------
    // Detalle de la cápsula
    // -----------------------------------------------------------------

    private void mostrarDetalleCapsule(final int idx) {
        // Datos de las cápsulas
        final Object[][][] capsulas = {
                {
                        {"mayo 2026", "Este mes en Beatify", "Tu actividad musical de mayo 2026", "347"},
                        {"Carlos Vives", "38%", "La Tierra del Olvido", "28", "Vallenato", "14"}
                },
                {
                        {"abr 2026", "Abril en Beatify", "Descubriste nuevos artistas", "412"},
                        {"Diomedes Díaz", "45%", "La Gota Fría", "41", "Vallenato", "8"}
                },
                {
                        {"feb 2026", "Febrero 2026", "Tu primer mes completo", "891"},
                        {"Carlos Vives", "32%", "La Cumbia Cienaguera", "33", "Cumbia", "12"}
                },
                {
                        {"nov 2025", "Noviembre 2025", "Descubriste la champeta", "670"},
                        {"Joe Arroyo", "29%", "La Rebelión", "25", "Salsa", "7"}
                },
                {
                        {"may 2025", "Mayo 2025", "Tu primer mes en Beatify", "224"},
                        {"Petrona Martínez", "51%", "Bullerengue", "18", "Bullerengue", "5"}
                },
        };

        final int i = Math.min(idx, capsulas.length - 1);
        final Object[] hero = capsulas[i][0];
        final Object[] stats = capsulas[i][1];

        // Hero card
        final String[][] gradientes = {
                {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#d4a017", "#2a1a05"},
                {"#a83232", "#3a0a0a"}, {"#7a3a8a", "#1a052a"}
        };
        final String[] grad = gradientes[i % gradientes.length];
        heroCoverHolder.getChildren().setAll(new AlbumCover(120, grad[0], grad[1], (String) hero[0]));

        lblHeroTitulo.setText((String) hero[0]);
        lblHeroDesc.setText((String) hero[1]);
        lblHeroReproducciones.setText((String) hero[3]);

        // Stats
        lblTopArtistaNombre.setText((String) stats[0]);
        lblTopArtistaPct.setText((String) stats[1]);
        lblTopCancionNombre.setText((String) stats[2]);
        lblTopCancionPlays.setText((String) stats[3]);
        lblGeneroNombre.setText((String) stats[4]);
        lblNuevosArtistas.setText((String) stats[5]);

        // Tracklist
        cargarTracklistCapsule(i);
    }

    private void cargarTracklistCapsule(final int idx) {
        tracklistCapsula.getChildren().clear();

        final String[][][] tracks = {
                {{"La Tierra del Olvido", "Carlos Vives", "28", "#c97a1f", "#3a1a05"},
                        {"La Gota Fría", "Carlos Vives", "19", "#1f7a5a", "#072a1a"},
                        {"La Rebelión", "Joe Arroyo", "15", "#a83232", "#3a0a0a"},
                        {"La Cumbia Cienaguera", "Totó la Momposina", "12", "#d4a017", "#2a1a05"}},
                {{"La Gota Fría", "Carlos Vives", "41", "#c97a1f", "#3a1a05"},
                        {"La Diosa Coronada", "Diomedes Díaz", "38", "#1f7a5a", "#072a1a"},
                        {"Mi Cafetal", "Carlos Vives", "27", "#a83232", "#3a0a0a"},
                        {"Tres Canciones", "Diomedes Díaz", "22", "#d4a017", "#2a1a05"}},
                {{"La Cumbia Cienaguera", "Totó la Momposina", "33", "#c97a1f", "#3a1a05"},
                        {"La Tierra del Olvido", "Carlos Vives", "28", "#1f7a5a", "#072a1a"},
                        {"Toro Mata", "Los Gaiteros", "19", "#a83232", "#3a0a0a"},
                        {"Yo Me Llamo Cumbia", "Totó la Momposina", "14", "#d4a017", "#2a1a05"}},
                {{"La Rebelión", "Joe Arroyo", "25", "#c97a1f", "#3a1a05"},
                        {"Yamulemau", "Joe Arroyo", "18", "#1f7a5a", "#072a1a"},
                        {"La Tierra del Olvido", "Carlos Vives", "16", "#a83232", "#3a0a0a"},
                        {"Cumbia Cienaguera", "Totó la Momposina", "12", "#d4a017", "#2a1a05"}},
                {{"Bullerengue", "Petrona Martínez", "18", "#c97a1f", "#3a1a05"},
                        {"La Caña de Azúcar", "Petrona Martínez", "14", "#1f7a5a", "#072a1a"},
                        {"La Tierra del Olvido", "Carlos Vives", "11", "#a83232", "#3a0a0a"},
                        {"La Gota Fría", "Carlos Vives", "9", "#d4a017", "#2a1a05"}},
        };

        final int i = Math.min(idx, tracks.length - 1);
        int pos = 1;
        for (final String[] t : tracks[i]) {
            tracklistCapsula.getChildren().add(construirFilaCapsula(pos++, t));
        }
    }

    private HBox construirFilaCapsula(final int pos, final String[] t) {
        final HBox row = new HBox(12);
        row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 12 16; -fx-background-color: -bf-border;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;"));

        final Label lblPos = new Label(String.valueOf(pos));
        lblPos.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 13px; -fx-text-fill: -bf-text-dim;");
        lblPos.setMinWidth(30);

        final AlbumCover cover = new AlbumCover(36, t[3], t[4]);

        final VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        final Label lblTit = new Label(t[0]);
        lblTit.setStyle("-fx-font-family: 'Manrope SemiBold'; -fx-font-size: 14px; -fx-text-fill: -bf-text;");
        final Label lblArt = new Label(t[1]);
        lblArt.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 12px; -fx-text-fill: -bf-text-dim;");
        info.getChildren().addAll(lblTit, lblArt);

        final Label lblPlays = new Label(t[2] + " plays");
        lblPlays.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 12px; -fx-text-fill: -bf-text-muted;");

        row.getChildren().addAll(lblPos, cover, info, lblPlays);
        return row;
    }

    // -----------------------------------------------------------------
    // Generar (botón principal)
    // -----------------------------------------------------------------

    @FXML
    private void onGenerar() {
        LOG.info("Generando cápsula: " + cmbPreset.getValue());
        construirTimeline();
        capsuleSeleccionada = 0;
        mostrarDetalleCapsule(0);
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
    @FXML private void onIrBarrio() { NavegacionUtil.cambiarA("/view/barrio.fxml", btnUserMenu); }
    @FXML private void onIrCapsulas() { /* ya estamos aquí */ }
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