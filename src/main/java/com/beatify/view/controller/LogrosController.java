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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

/**
 * Controller de la pantalla de Logros (logros.fxml).
 *
 * Funcionalidad distintiva #2 de Beatify.
 *
 * Muestra:
 *   - Cards de resumen (progreso global + rarezas obtenidas)
 *   - Segmented control: Todos / Obtenidos / Pendientes
 *   - Grid de tarjetas de logros:
 *       * Obtenidos: icono + nombre + descripción + fecha
 *       * Pendientes: icono + nombre + descripción + barra de progreso
 */
public class LogrosController {

    private static final Logger LOG = Logger.getLogger(LogrosController.class.getName());

    // ---- TopBar ----
    @FXML private Button btnNotif;
    @FXML private Button btnUserMenu;
    @FXML private Label lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Cards de resumen ----
    @FXML private Label lblProgreso;
    @FXML private Label lblProgresoSub;
    @FXML private ProgressBar progresoBar;
    @FXML private Label lblRarezas;
    @FXML private Label lblRarezasSub;

    // ---- Filtros ----
    @FXML private ToggleButton tabTodos;
    @FXML private ToggleButton tabObtenidos;
    @FXML private ToggleButton tabPendientes;

    // ---- Grid de logros ----
    @FXML private FlowPane logrosGrid;

    private String filtroActivo = "todos";

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarResumen();
        configurarFiltros();
        cargarLogros();
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
    // Resumen
    // -----------------------------------------------------------------

    private void configurarResumen() {
        lblProgreso.setText("8 / 24");
        lblProgresoSub.setText("logros obtenidos");
        progresoBar.setProgress(8.0 / 24.0);
        lblRarezas.setText("2 épicos · 1 legendario");
        lblRarezasSub.setText("logros raros en tu colección");
    }

    // -----------------------------------------------------------------
    // Filtros
    // -----------------------------------------------------------------

    private void configurarFiltros() {
        tabTodos.setSelected(true);
        tabTodos.setOnAction(e -> { filtroActivo = "todos"; recargar(); });
        tabObtenidos.setOnAction(e -> { filtroActivo = "obtenidos"; recargar(); });
        tabPendientes.setOnAction(e -> { filtroActivo = "pendientes"; recargar(); });
    }

    private void recargar() {
        logrosGrid.getChildren().clear();
        cargarLogros();
    }

    // -----------------------------------------------------------------
    // Grid de logros
    // -----------------------------------------------------------------

    private void cargarLogros() {
        final Object[][] datos = {
                {"🎵", "Primer Paso", "Reproduce tu primera canción", "comun", true, "Obtenido el Feb 14, 2026", null, null},
                {"🎤", "Melómano", "Escucha 100 canciones", "comun", true, "Obtenido el Feb 20, 2026", null, null},
                {"🇨🇴", "Patriota", "Escucha 10 horas de música regional", "comun", true, "Obtenido el Mar 01, 2026", null, null},
                {"⭐", "Vallenato de Corazón", "Escucha 50 canciones de vallenato", "comun", true, "Obtenido el Mar 03, 2026", null, null},
                {"🗺️", "Explorador", "Descubre 10 artistas nuevos", "raro", true, "Obtenido el Mar 15, 2026", null, null},
                {"✍️", "Crítico Constructivo", "Escribe 10 reseñas con +3 votos útiles", "raro", true, "Obtenido el Abr 02, 2026", null, null},
                {"🔥", "Caribe Sound", "Escucha 100 canciones del Caribe colombiano", "epico", true, "Obtenido el Abr 10, 2026", null, null},
                {"🌙", "Maratón Nocturno", "Escucha 4 horas seguidas", "epico", true, "Obtenido el Abr 18, 2026", null, null},
                {"💎", "Embajador", "Comparte 20 playlists públicas", "legendario", true, "Obtenido el May 01, 2026", null, null},
                // Pendientes
                {"🎤", "Vocalista", "Vota 50 reseñas como útiles", "comun", false, null, "23", "50"},
                {"📻", "DJ del barrio", "Aparece en top 3 de Música del Barrio", "raro", false, null, "1", "3"},
                {"🌊", "Caribeño", "Escucha 200 canciones de género Cumbia", "raro", false, null, "62", "200"},
                {"🕰️", "Arqueólogo", "Descubre 10 artistas de antes de 1990", "epico", false, null, "4", "10"},
                {"👑", "Leyenda", "Alcanza 1000 reproducciones totales", "legendario", false, null, "547", "1000"},
        };

        for (final Object[] d : datos) {
            final boolean obtenido = (boolean) d[4];

            if ("obtenidos".equals(filtroActivo) && !obtenido) continue;
            if ("pendientes".equals(filtroActivo) && obtenido) continue;

            logrosGrid.getChildren().add(construirTarjetaLogro(d));
        }
    }

    private VBox construirTarjetaLogro(final Object[] d) {
        final String icono = (String) d[0];
        final String nombre = (String) d[1];
        final String desc = (String) d[2];
        final String rareza = (String) d[3];
        final boolean obtenido = (boolean) d[4];

        final VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: -bf-bg-2; -fx-background-radius: 16px; -fx-padding: 20px; -fx-pref-width: 280px; -fx-border-color: " + getColorRareza(rareza) + "; -fx-border-width: 1px; -fx-border-radius: 16px;");

        if (!obtenido) card.setStyle(card.getStyle() + " -fx-opacity: 0.7;");

        // Icono
        final StackPane iconHolder = new StackPane();
        iconHolder.setPrefSize(80, 80);
        iconHolder.setStyle("-fx-background-color: " + getColorRareza(rareza) + "20; -fx-background-radius: 999px;");
        final Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 40px;");
        iconHolder.getChildren().add(lblIcono);

        // Badge de rareza
        final Label badgeRareza = new Label(rareza.substring(0, 1).toUpperCase() + rareza.substring(1));
        badgeRareza.setStyle("-fx-background-color: " + getColorRareza(rareza) + "; -fx-text-fill: white; -fx-padding: 4px 12px; -fx-background-radius: 999px; -fx-font-family: 'JetBrains Mono'; -fx-font-size: 10px;");

        // Nombre
        final Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-family: 'Manrope Bold'; -fx-font-size: 16px; -fx-text-fill: -bf-text; -fx-wrap-text: true; -fx-alignment: center;");
        lblNombre.setWrapText(true);
        lblNombre.setAlignment(Pos.CENTER);

        // Descripción
        final Label lblDesc = new Label(desc);
        lblDesc.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 12px; -fx-text-fill: -bf-text-muted; -fx-wrap-text: true; -fx-alignment: center;");
        lblDesc.setWrapText(true);
        lblDesc.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconHolder, badgeRareza, lblNombre, lblDesc);

        if (obtenido) {
            final Label lblFecha = new Label("✓ " + (String) d[5]);
            lblFecha.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 10px; -fx-text-fill: #7ed957; -fx-padding: 8px 0 0 0;");
            card.getChildren().add(lblFecha);
        } else {
            final String actual = (String) d[6];
            final String maximo = (String) d[7];
            final double pct = Double.parseDouble(actual) / Double.parseDouble(maximo);

            final ProgressBar pb = new ProgressBar(pct);
            pb.setStyle("-fx-background-color: -bf-border; -fx-background-radius: 4px; -fx-pref-width: 200px;");
            pb.setMaxWidth(Double.MAX_VALUE);

            final Label lblFrac = new Label(actual + " / " + maximo);
            lblFrac.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 11px; -fx-text-fill: -bf-text-dim;");

            card.getChildren().addAll(pb, lblFrac);
        }

        return card;
    }

    private String getColorRareza(final String rareza) {
        return switch (rareza) {
            case "comun" -> "#6c757d";
            case "raro" -> "#0d6efd";
            case "epico" -> "#6f42c1";
            case "legendario" -> "#F2C94C";
            default -> "-bf-text-dim";
        };
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
    @FXML private void onIrCapsulas() { NavegacionUtil.cambiarA("/view/capsulas.fxml", btnUserMenu); }
    @FXML private void onIrLogros() { /* ya estamos aquí */ }
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