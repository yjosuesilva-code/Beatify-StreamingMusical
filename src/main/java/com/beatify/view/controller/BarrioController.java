package com.beatify.view.controller;

import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.PlaylistUtil;
import com.beatify.view.util.UserMenuUtil;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BarrioController {

    private static final Logger LOG = Logger.getLogger(BarrioController.class.getName());

    private static final String[][] PALETA = {
        {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
        {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
    };

    // ---- TopBar ----
    @FXML private Button    btnNotif;
    @FXML private Button    btnUserMenu;
    @FXML private Label     lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private TextField txtBusqueda;
    @FXML private Label     lblUserNombre;

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

    // ---- Tabla ----
    @FXML private VBox tablaBox;

    private String periodoActivo = "semana";
    private final PlaylistDAO playlistDAO = new PlaylistDAO();

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists(actual);
        configurarHeader(actual);
        configurarFiltros();
        cargarRanking(actual);
    }

    // -----------------------------------------------------------------
    // TopBar y Sidebar
    // -----------------------------------------------------------------
    private void configurarTopBar(final Cliente c) {
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#1ed760", "#1ab44e",
                        c.getNombre() + " " + c.getApellido()));
        lblUserNombre.setText(c.getNombre());
        com.beatify.view.util.NotificacionMenuUtil.aplicarBadge(lblNotifCount, c.getIdCliente());
    }

    private void configurarSidebarPlaylists(final Cliente c) {
        try {
            final List<Playlist> mias = playlistDAO.listarPorCliente(c.getIdCliente());
            for (int i = 0; i < mias.size(); i++) {
                final Playlist pl  = mias.get(i);
                final String[] col = PALETA[i % PALETA.length];
                final Button item  = new Button();
                item.getStyleClass().add("bf-side-playlist");
                item.setMaxWidth(Double.MAX_VALUE);
                item.setOnAction(e -> PlaylistUtil.abrir(pl.getIdPlaylist()));
                final HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getChildren().addAll(
                        new AlbumCover(32, col[0], col[1]),
                        construirVBox(2,
                                crearLabel(pl.getNombre(), "bf-side-pl-name"),
                                crearLabel("Playlist", "bf-side-pl-meta")));
                item.setGraphic(row);
                sidebarPlaylistsBox.getChildren().add(item);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error sidebar playlists", ex);
        }
    }

    // -----------------------------------------------------------------
    // Header
    // -----------------------------------------------------------------
    private void configurarHeader(final Cliente c) {
        lblCiudadChip.setText(c.getCiudad() == null ? "Valledupar" : c.getCiudad());
    }

    // -----------------------------------------------------------------
    // Segmented control
    // -----------------------------------------------------------------
    private void configurarFiltros() {
        tabSemana.setSelected(true);
        tabHoy.setOnAction(e    -> { periodoActivo = "hoy";    recargar(); });
        tabSemana.setOnAction(e -> { periodoActivo = "semana"; recargar(); });
        tabMes.setOnAction(e    -> { periodoActivo = "mes";    recargar(); });
        tab6M.setOnAction(e     -> { periodoActivo = "6m";     recargar(); });
        tabAnio.setOnAction(e   -> { periodoActivo = "anio";   recargar(); });
    }

    private void recargar() {
        podioContainer.getChildren().clear();
        tablaBox.getChildren().clear();
        cargarRanking(SessionContext.getInstance().getClienteActual());
    }

    // -----------------------------------------------------------------
    // Ranking desde BD
    // -----------------------------------------------------------------
    private void cargarRanking(final Cliente cliente) {
        final String ciudad = cliente.getCiudad() == null ? "Valledupar" : cliente.getCiudad();

        // Filtro de periodo sobre fecha_hora
        final String condPeriodo = switch (periodoActivo) {
            case "hoy"    -> "AND r.fecha_hora >= TRUNC(SYSDATE)";
            case "mes"    -> "AND r.fecha_hora >= ADD_MONTHS(SYSDATE, -1)";
            case "6m"     -> "AND r.fecha_hora >= ADD_MONTHS(SYSDATE, -6)";
            case "anio"   -> "AND r.fecha_hora >= ADD_MONTHS(SYSDATE, -12)";
            default       -> "AND r.fecha_hora >= SYSDATE - 7";  // semana
        };

        final String sql = """
                SELECT a.id_artista, a.nombre_artistico,
                       COUNT(*) oyentes_ciudad,
                       (SELECT COUNT(*) FROM REPRODUCCION r2
                          JOIN CANCION c2  ON c2.id_cancion = r2.CANCION_id_cancion
                          JOIN ALBUM al2   ON al2.id_album  = c2.ALBUM_id_album
                         WHERE al2.ARTISTA_id_artista = a.id_artista) oyentes_nacional
                  FROM REPRODUCCION r
                  JOIN CLIENTE cl  ON cl.id_cliente  = r.CLIENTE_id_cliente
                  JOIN CANCION cc  ON cc.id_cancion  = r.CANCION_id_cancion
                  JOIN ALBUM al    ON al.id_album    = cc.ALBUM_id_album
                  JOIN ARTISTA a   ON a.id_artista   = al.ARTISTA_id_artista
                 WHERE cl.ciudad = ?
                """ + condPeriodo + """
                 GROUP BY a.id_artista, a.nombre_artistico
                 ORDER BY oyentes_ciudad DESC
                 FETCH FIRST 10 ROWS ONLY""";

        final List<Object[]> datos = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciudad);
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    final String nombre  = rs.getString(2);
                    final int    oyLoc   = rs.getInt(3);
                    final int    oyNac   = rs.getInt(4);
                    final String[] col   = PALETA[(rank - 1) % PALETA.length];
                    datos.add(new Object[]{rank, nombre, "—",
                            fmtOyentes(oyLoc), fmtOyentes(oyNac), "same", col[0], col[1]});
                    rank++;
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando barrio ranking", ex);
        }

        if (datos.isEmpty()) {
            tablaBox.getChildren().add(crearLabel(
                    "Sin datos de reproducciones para " + ciudad + " en este periodo.", "bf-field-lbl"));
            return;
        }

        final Object[][] arr = datos.toArray(new Object[0][]);
        construirPodio(arr, ciudad);
        construirTabla(arr, ciudad);
    }

    // -----------------------------------------------------------------
    // Podio (top 3)
    // -----------------------------------------------------------------
    private void construirPodio(final Object[][] datos, final String ciudad) {
        if (datos.length < 1) return;

        final int total  = Math.min(3, datos.length);
        final int[] orden = total == 1 ? new int[]{0}
                          : total == 2 ? new int[]{1, 0}
                          : new int[]{1, 0, 2};

        final HBox podio = new HBox(16);
        podio.setAlignment(Pos.BOTTOM_CENTER);
        podio.setPadding(new Insets(20, 0, 20, 0));

        for (final int idx : orden) {
            final Object[] d    = datos[idx];
            final int    rank   = (int)    d[0];
            final String nombre = (String) d[1];
            final String oyentes = (String) d[3];
            final String c1     = (String) d[6];
            final String c2     = (String) d[7];

            final double avatarSize = rank == 1 ? 96 : 76;
            final double baseH      = rank == 1 ? 110 : rank == 2 ? 85 : 70;

            final VBox slot = new VBox(8);
            slot.setAlignment(Pos.BOTTOM_CENTER);
            slot.setPrefWidth(160);

            final ArtistAvatar avatar = new ArtistAvatar(avatarSize, c1, c2, nombre);

            final Label lblNombre = new Label(nombre);
            lblNombre.setStyle("-fx-font-family: 'Manrope Bold'; -fx-font-size: 14px;"
                    + " -fx-text-fill: -bf-text; -fx-wrap-text: true; -fx-alignment: center;");
            lblNombre.setWrapText(true);
            lblNombre.setAlignment(Pos.CENTER);

            final Label lblOyentes = new Label(oyentes + " · " + ciudad);
            lblOyentes.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 10px;"
                    + " -fx-text-fill: -bf-text-muted;");

            final Region barra = new Region();
            barra.setPrefHeight(baseH);
            barra.setPrefWidth(120);
            final String barraColor = rank == 1 ? c1 : rank == 2 ? c1 : c1;
            barra.setStyle("-fx-background-color: linear-gradient(to bottom, "
                    + c1 + ", " + c2 + "); -fx-background-radius: 8px 8px 0 0;");

            final Label lblRank = new Label("#" + rank);
            lblRank.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 12px;"
                    + " -fx-padding: 2px 8px; -fx-background-radius: 999px;"
                    + (rank == 1 ? " -fx-background-color: #F2C94C; -fx-text-fill: #1a1a1a;" : ""));

            slot.getChildren().addAll(avatar, lblNombre, lblOyentes, lblRank, barra);
            podio.getChildren().add(slot);
        }

        podioContainer.getChildren().add(podio);
    }

    // -----------------------------------------------------------------
    // Tabla completa
    // -----------------------------------------------------------------
    private void construirTabla(final Object[][] datos, final String ciudad) {
        final HBox header = new HBox(0);
        header.setStyle("-fx-background-color: -bf-bg-3; -fx-padding: 12 16;"
                + " -fx-border-color: -bf-border; -fx-border-width: 0 0 1 0;");
        header.getChildren().addAll(
                crearLabel("#",  40, "bf-track-num"),
                crearLabel("",   16, "bf-track-col"),
                crearLabel("ARTISTA", 260, "bf-track-col"),
                crearLabel("EN " + ciudad.toUpperCase(), 130, "bf-track-col"),
                crearLabel("NACIONAL", 100, "bf-track-col"));
        tablaBox.getChildren().add(header);

        for (final Object[] d : datos) {
            tablaBox.getChildren().add(construirFilaTabla(d, ciudad));
        }
    }

    private HBox construirFilaTabla(final Object[] d, final String ciudad) {
        final int    rank   = (int)    d[0];
        final String nombre = (String) d[1];
        final String oyLoc  = (String) d[3];
        final String oyNac  = (String) d[4];
        final String c1     = (String) d[6];
        final String c2     = (String) d[7];

        final HBox row = new HBox(0);
        row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 12 16; -fx-background-color: -bf-border;"));
        row.setOnMouseExited(e  -> row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;"));

        final Label lblRank = new Label(String.format("%02d", rank));
        lblRank.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 13px; -fx-text-fill: -bf-text-dim;");
        lblRank.setMinWidth(40);

        final Label lblTrend = new Label("—");
        lblTrend.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 14px; -fx-text-fill: -bf-text-dim;");
        lblTrend.setMinWidth(16);

        final HBox artistaCell = new HBox(10);
        artistaCell.setAlignment(Pos.CENTER_LEFT);
        artistaCell.setMinWidth(260);
        final ArtistAvatar avatar = new ArtistAvatar(40, c1, c2, nombre);
        final Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-family: 'Manrope SemiBold'; -fx-font-size: 14px; -fx-text-fill: -bf-text;");
        artistaCell.getChildren().addAll(avatar, lblNombre);

        final Label lblLoc = new Label(oyLoc);
        lblLoc.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 14px; -fx-text-fill: -bf-accent;");
        lblLoc.setMinWidth(130);

        final Label lblNac = new Label(oyNac);
        lblNac.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 13px; -fx-text-fill: -bf-text-dim;");
        lblNac.setMinWidth(100);

        row.getChildren().addAll(lblRank, lblTrend, artistaCell, lblLoc, lblNac);
        return row;
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------
    @FXML private void onAtras()        { HistorialNavegacion.getInstance().atras(); }
    @FXML private void onAdelante()     { HistorialNavegacion.getInstance().adelante(); }
    @FXML private void onUserMenu()     { UserMenuUtil.mostrar(btnUserMenu); }
    @FXML private void onNotif() { com.beatify.view.util.NotificacionMenuUtil.mostrar(btnNotif, lblNotifCount); }
    @FXML private void onIrInicio()     { HistorialNavegacion.getInstance().navegar("/view/home.fxml"); }

    @FXML
    private void onBuscar() {
        final String termino = txtBusqueda == null ? null : txtBusqueda.getText();
        if (termino == null || termino.isBlank()) return;
        SessionContext.getInstance().setTerminoBusqueda(termino);
        HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml");
    }
    @FXML private void onIrExplorar()   { HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml"); }
    @FXML private void onIrBiblioteca() { HistorialNavegacion.getInstance().navegar("/view/biblioteca.fxml"); }
    @FXML private void onIrResenas()    { HistorialNavegacion.getInstance().navegar("/view/resenas.fxml"); }
    @FXML private void onIrBarrio()     { /* ya estamos */ }
    @FXML private void onIrCapsulas()   { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros()     { HistorialNavegacion.getInstance().navegar("/view/logros.fxml"); }
    @FXML private void onIrPerfil()     { HistorialNavegacion.getInstance().navegar("/view/perfil.fxml"); }
    @FXML private void onNuevaPlaylist() { PlaylistUtil.crearNueva(btnUserMenu, () -> { sidebarPlaylistsBox.getChildren().clear(); configurarSidebarPlaylists(SessionContext.getInstance().getClienteActual()); }); }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------
    private static String fmtOyentes(final int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000)     return String.format("%.1fk", n / 1_000.0);
        return String.valueOf(n);
    }

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
