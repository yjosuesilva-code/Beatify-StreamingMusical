package com.beatify.view.controller;

import com.beatify.dao.LikeAlbumDAO;
import com.beatify.dao.LikeCancionDAO;
import com.beatify.dao.PlaylistDAO;
import com.beatify.dao.SeguimientoDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.PlayerManager;
import com.beatify.view.util.NavegacionUtil;
import com.beatify.view.util.AgregarAPlaylistUtil;
import com.beatify.view.util.PlaylistUtil;
import com.beatify.view.util.UserMenuUtil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BibliotecaController {

    private static final Logger LOG = Logger.getLogger(BibliotecaController.class.getName());

    private static final String[][] PALETA = {
        {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
        {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
    };

    private enum Tab { PLAYLISTS, ALBUMES, CANCIONES, ARTISTAS }

    @FXML private Button    btnUserMenu;
    @FXML private Button    btnNotif;
    @FXML private Label     lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label     lblUserNombre;
    @FXML private VBox      sidebarPlaylistsBox;
    @FXML private FlowPane  tabsPane;
    @FXML private VBox      contenidoBox;

    private Tab tabActual = Tab.PLAYLISTS;

    private final PlaylistDAO    playlistDAO    = new PlaylistDAO();
    private final LikeAlbumDAO   likeAlbumDAO   = new LikeAlbumDAO();
    private final LikeCancionDAO likeCancionDAO = new LikeCancionDAO();
    private final SeguimientoDAO seguimientoDAO = new SeguimientoDAO();

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }
        configurarTopBar(actual);
        configurarSidebarPlaylists(actual);
        configurarTabs();
        mostrarTab(Tab.PLAYLISTS);
    }

    // -----------------------------------------------------------------
    // TopBar
    // -----------------------------------------------------------------
    private void configurarTopBar(final Cliente c) {
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#1ed760", "#1ab44e",
                        c.getNombre() + " " + c.getApellido()));
        lblUserNombre.setText(c.getNombre());
        com.beatify.view.util.NotificacionMenuUtil.aplicarBadge(lblNotifCount, c.getIdCliente());
    }

    // -----------------------------------------------------------------
    // Sidebar
    // -----------------------------------------------------------------
    private void configurarSidebarPlaylists(final Cliente c) {
        sidebarPlaylistsBox.getChildren().clear();
        try {
            final List<Playlist> mias = playlistDAO.listarPorCliente(c.getIdCliente());
            if (mias.isEmpty()) {
                sidebarPlaylistsBox.getChildren().add(lbl("Sin playlists aún.", "bf-side-pl-meta"));
                return;
            }
            for (int i = 0; i < mias.size(); i++) {
                final Playlist pl  = mias.get(i);
                final String[] col = PALETA[i % PALETA.length];
                final Button btn   = new Button();
                btn.getStyleClass().add("bf-side-playlist");
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setOnAction(e -> PlaylistUtil.abrir(pl.getIdPlaylist()));
                final HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getChildren().addAll(
                        new AlbumCover(32, col[0], col[1]),
                        vbox(2, lbl(pl.getNombre(), "bf-side-pl-name"),
                                lbl("Playlist", "bf-side-pl-meta")));
                btn.setGraphic(row);
                sidebarPlaylistsBox.getChildren().add(btn);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando playlists sidebar", ex);
        }
    }

    // -----------------------------------------------------------------
    // Tabs (ToggleButtons como pestañas)
    // -----------------------------------------------------------------
    private void configurarTabs() {
        final ToggleGroup grupo = new ToggleGroup();
        final String[][] tabs = {
            {"Playlists",  Tab.PLAYLISTS.name()},
            {"Álbumes",    Tab.ALBUMES.name()},
            {"Canciones",  Tab.CANCIONES.name()},
            {"Artistas",   Tab.ARTISTAS.name()}
        };
        for (final String[] t : tabs) {
            final ToggleButton tb = new ToggleButton(t[0]);
            tb.getStyleClass().add("bf-pref-chip");
            tb.setToggleGroup(grupo);
            tb.setUserData(Tab.valueOf(t[1]));
            if (Tab.valueOf(t[1]) == Tab.PLAYLISTS) tb.setSelected(true);
            tb.setOnAction(e -> {
                if (tb.isSelected()) mostrarTab((Tab) tb.getUserData());
                else tb.setSelected(true); // no deseleccionar
            });
            tabsPane.getChildren().add(tb);
        }
    }

    private void mostrarTab(final Tab tab) {
        tabActual = tab;
        contenidoBox.getChildren().clear();
        final Cliente c = SessionContext.getInstance().getClienteActual();
        if (c == null || c.getIdCliente() == null) return;

        switch (tab) {
            case PLAYLISTS -> mostrarPlaylists(c);
            case ALBUMES   -> mostrarAlbumes(c);
            case CANCIONES -> mostrarCanciones(c);
            case ARTISTAS  -> mostrarArtistas(c);
        }
    }

    // -----------------------------------------------------------------
    // Playlists
    // -----------------------------------------------------------------
    private void mostrarPlaylists(final Cliente c) {
        try {
            final List<Playlist> mias = playlistDAO.listarPorCliente(c.getIdCliente());

            if (mias.isEmpty()) {
                contenidoBox.getChildren().add(msgVacio("No tienes playlists. Crea una con el botón +"));
                return;
            }

            final GridPane grid = new GridPane();
            grid.setHgap(16);
            grid.setVgap(16);

            for (int i = 0; i < mias.size(); i++) {
                final Playlist pl  = mias.get(i);
                final String[] col = PALETA[i % PALETA.length];
                final Button card  = tarjetaPlaylist(pl, col);
                grid.add(card, i % 4, i / 4);
            }
            contenidoBox.getChildren().add(grid);
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando playlists", ex);
        }
    }

    private Button tarjetaPlaylist(final Playlist pl, final String[] col) {
        final Button card = new Button();
        card.getStyleClass().add("bf-card");
        card.setPrefSize(200, 220);
        card.setOnAction(e -> PlaylistUtil.abrir(pl.getIdPlaylist()));

        final VBox inner = new VBox(12);
        inner.setAlignment(Pos.BOTTOM_LEFT);
        inner.setPadding(new Insets(12));
        inner.getChildren().addAll(
                new AlbumCover(120, col[0], col[1]),
                lbl(pl.getNombre(), "bf-quick-name"),
                lbl(pl.getPublica() != null && pl.getPublica().equalsIgnoreCase("S")
                        ? "Pública" : "Privada", "bf-side-pl-meta"));
        card.setGraphic(inner);
        return card;
    }

    // -----------------------------------------------------------------
    // Álbumes guardados (LIKE_ALBUM)
    // -----------------------------------------------------------------
    private void mostrarAlbumes(final Cliente c) {
        final String sql = """
                SELECT al.id_album, al.titulo, al.anio_lanzamiento, a.nombre_artistico
                  FROM LIKE_ALBUM la
                  JOIN ALBUM al  ON al.id_album = la.ALBUM_id_album
                  JOIN ARTISTA a ON a.id_artista = al.ARTISTA_id_artista
                 WHERE la.CLIENTE_id_cliente = ?
                 ORDER BY la.fecha_like DESC""";

        final GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        int count = 0;

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final String titulo = rs.getString(2);
                    final int    anio   = rs.getInt(3);
                    final String art    = rs.getString(4);
                    final String[] col  = PALETA[count % PALETA.length];

                    final Button card = new Button();
                    card.getStyleClass().add("bf-card");
                    card.setPrefSize(190, 220);
                    final VBox inner = new VBox(8);
                    inner.setAlignment(Pos.BOTTOM_LEFT);
                    inner.setPadding(new Insets(12));
                    inner.getChildren().addAll(
                            new AlbumCover(110, col[0], col[1]),
                            lbl(titulo, "bf-quick-name"),
                            lbl(anio + " · " + art, "bf-side-pl-meta"));
                    card.setGraphic(inner);
                    grid.add(card, count % 4, count / 4);
                    count++;
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando álbumes guardados", ex);
        }

        if (count == 0) {
            contenidoBox.getChildren().add(msgVacio("No has guardado ningún álbum aún."));
        } else {
            contenidoBox.getChildren().add(grid);
        }
    }

    // -----------------------------------------------------------------
    // Canciones favoritas (LIKE_CANCION)
    // -----------------------------------------------------------------
    private void mostrarCanciones(final Cliente c) {
        final String sql = """
                SELECT cc.id_cancion, cc.titulo, a.nombre_artistico,
                       al.titulo, cc.duracion_seg, cc.ruta_archivo, al.id_album
                  FROM LIKE_CANCION lc
                  JOIN CANCION cc ON cc.id_cancion = lc.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album = cc.ALBUM_id_album
                  JOIN ARTISTA a  ON a.id_artista = al.ARTISTA_id_artista
                 WHERE lc.CLIENTE_id_cliente = ?
                 ORDER BY lc.fecha_like DESC""";

        // Cabecera de tabla
        final HBox header = new HBox(0);
        header.getStyleClass().add("bf-tracklist-header");
        header.setPadding(new Insets(12, 16, 12, 16));
        header.getChildren().addAll(
                lbl("#",       40, "bf-track-num"),
                lbl("TÍTULO", 280, "bf-track-col"),
                lbl("ÁLBUM",  180, "bf-track-col"),
                lbl("DUR.",    70, "bf-track-col"));
        contenidoBox.getChildren().add(header);

        int num = 1;
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final int    idCan   = rs.getInt(1);
                    final String titulo  = rs.getString(2);
                    final String artista = rs.getString(3);
                    final String album   = rs.getString(4);
                    final int    durSeg  = rs.getInt(5);
                    final String ruta    = rs.getString(6);
                    final int    albId   = rs.getInt(7);
                    final String dur     = formatDur(durSeg);
                    final String[] col   = PALETA[num % PALETA.length];
                    final com.beatify.model.Cancion cancion =
                            new com.beatify.model.Cancion(idCan, titulo, durSeg, ruta,
                                                          null, null, null, albId, null);

                    final HBox row = new HBox(0);
                    row.getStyleClass().add("bf-tracklist-row");
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(10, 16, 10, 16));
                    row.setStyle("-fx-cursor: hand;");
                    row.setOnMouseClicked(e -> reproducir(cancion, artista, album));

                    final HBox titleCell = new HBox(10);
                    titleCell.setAlignment(Pos.CENTER_LEFT);
                    titleCell.setMinWidth(280);
                    final VBox info = new VBox(2);
                    info.getChildren().addAll(lbl(titulo, "bf-track-title"), lbl(artista, "bf-track-artist"));
                    titleCell.getChildren().addAll(new AlbumCover(34, col[0], col[1]), info);

                    final Button btnAdd = new Button();
                    btnAdd.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
                    final org.kordamp.ikonli.javafx.FontIcon icAdd =
                            new org.kordamp.ikonli.javafx.FontIcon("bi-plus-circle");
                    icAdd.setIconSize(20);
                    icAdd.setIconColor(javafx.scene.paint.Color.web("#1ed760"));
                    btnAdd.setGraphic(icAdd);
                    btnAdd.setTooltip(new javafx.scene.control.Tooltip("Agregar a playlist"));
                    btnAdd.setOnAction(e -> {
                        e.consume();
                        AgregarAPlaylistUtil.mostrar(btnAdd, cancion.getIdCancion(), cancion.getTitulo());
                    });

                    final Button btnPlay = new Button("▶");
                    btnPlay.setStyle("-fx-background-color: transparent; -fx-text-fill: #1ed760;"
                            + " -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 0 4;");
                    btnPlay.setOnAction(e -> { e.consume(); reproducir(cancion, artista, album); });

                    row.getChildren().addAll(
                            lbl(String.valueOf(num), 40, "bf-track-num"),
                            titleCell,
                            lbl(album, 180, "bf-track-album"),
                            lbl(dur,    60, "bf-track-dur"),
                            btnAdd,
                            btnPlay);
                    contenidoBox.getChildren().add(row);
                    num++;
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando canciones favoritas", ex);
        }

        if (num == 1) {
            contenidoBox.getChildren().clear();
            contenidoBox.getChildren().add(msgVacio("No has marcado canciones como favoritas aún."));
        }
    }

    // -----------------------------------------------------------------
    // Artistas seguidos (SEGUIMIENTO)
    // -----------------------------------------------------------------
    private void mostrarArtistas(final Cliente c) {
        final String sql = """
                SELECT a.id_artista, a.nombre_artistico, a.pais,
                       COUNT(DISTINCT al.id_album) albumes
                  FROM SEGUIMIENTO s
                  JOIN ARTISTA a  ON a.id_artista = s.ARTISTA_id_artista
                  LEFT JOIN ALBUM al ON al.ARTISTA_id_artista = a.id_artista
                 WHERE s.CLIENTE_id_cliente = ?
                 GROUP BY a.id_artista, a.nombre_artistico, a.pais
                 ORDER BY a.nombre_artistico""";

        final FlowPane flow = new FlowPane();
        flow.setHgap(16);
        flow.setVgap(16);
        int count = 0;

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final String nombre  = rs.getString(2);
                    final String pais    = rs.getString(3) != null ? rs.getString(3) : "";
                    final int    albumes = rs.getInt(4);
                    final String[] col   = PALETA[count % PALETA.length];

                    final Button card = new Button();
                    card.getStyleClass().add("bf-card");
                    card.setPrefSize(160, 180);
                    final VBox inner = new VBox(8);
                    inner.setAlignment(Pos.CENTER);
                    inner.setPadding(new Insets(16));
                    inner.getChildren().addAll(
                            new ArtistAvatar(80, col[0], col[1], nombre),
                            lbl(nombre, "bf-quick-name"),
                            lbl(albumes + " álbumes · " + pais, "bf-side-pl-meta"));
                    card.setGraphic(inner);
                    flow.getChildren().add(card);
                    count++;
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando artistas seguidos", ex);
        }

        if (count == 0) {
            contenidoBox.getChildren().add(msgVacio("No sigues a ningún artista aún."));
        } else {
            contenidoBox.getChildren().add(flow);
        }
    }

    // -----------------------------------------------------------------
    // Reproducción — sin navegar
    // -----------------------------------------------------------------
    private void reproducir(final com.beatify.model.Cancion cancion,
                            final String artista, final String album) {
        PlayerManager.getInstance().reproducir(cancion, artista, album);
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------
    @FXML private void onAtras()        { HistorialNavegacion.getInstance().atras(); }
    @FXML private void onAdelante()     { HistorialNavegacion.getInstance().adelante(); }
    @FXML private void onUserMenu()     { UserMenuUtil.mostrar(btnUserMenu); }
    @FXML private void onIrInicio()     { HistorialNavegacion.getInstance().navegar("/view/home.fxml"); }
    @FXML private void onIrExplorar()   { HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml"); }
    @FXML private void onIrBiblioteca() { /* ya estamos */ }
    @FXML private void onIrResenas()    { HistorialNavegacion.getInstance().navegar("/view/resenas.fxml"); }
    @FXML private void onIrBarrio()     { HistorialNavegacion.getInstance().navegar("/view/barrio.fxml"); }
    @FXML private void onIrCapsulas()   { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros()     { HistorialNavegacion.getInstance().navegar("/view/logros.fxml"); }
    @FXML private void onNuevaPlaylist() {
        PlaylistUtil.crearNueva(btnUserMenu, () -> {
            sidebarPlaylistsBox.getChildren().clear();
            configurarSidebarPlaylists(SessionContext.getInstance().getClienteActual());
            if (tabActual == Tab.PLAYLISTS) mostrarTab(Tab.PLAYLISTS);
        });
    }
    @FXML private void onNotif() { com.beatify.view.util.NotificacionMenuUtil.mostrar(btnNotif, lblNotifCount); }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helpers UI
    // -----------------------------------------------------------------
    private static Label msgVacio(final String msg) {
        final Label l = new Label(msg);
        l.setStyle("-fx-text-fill: #a7a7a7; -fx-font-size: 14px; -fx-padding: 20;");
        return l;
    }

    private static Label lbl(final String texto, final String... clases) {
        final Label l = new Label(texto);
        l.getStyleClass().addAll(clases);
        return l;
    }

    private static Label lbl(final String texto, final double minWidth, final String... clases) {
        final Label l = new Label(texto);
        l.getStyleClass().addAll(clases);
        l.setMinWidth(minWidth);
        return l;
    }

    private static VBox vbox(final double spacing, final javafx.scene.Node... hijos) {
        final VBox v = new VBox(spacing);
        v.getChildren().addAll(hijos);
        return v;
    }

    private static String formatDur(final int seg) {
        return String.format("%d:%02d", seg / 60, seg % 60);
    }

    private static Cliente clientePlaceholder() {
        final Cliente c = new Cliente();
        c.setIdCliente(1);
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        return c;
    }
}
