package com.beatify.view.controller;

import com.beatify.dao.AlbumDAO;
import com.beatify.dao.ArtistaDAO;
import com.beatify.dao.PlaylistDAO;
import com.beatify.model.Cancion;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Artista;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.component.MediaCard;
import com.beatify.view.util.AgregarAPlaylistUtil;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.NotificacionMenuUtil;
import com.beatify.view.util.PlayerManager;
import com.beatify.view.util.PlaylistUtil;
import com.beatify.view.util.UserMenuUtil;
import com.beatify.view.util.NavegacionUtil;
import com.beatify.view.util.SaludoUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController {

    private static final Logger LOG = Logger.getLogger(HomeController.class.getName());
    private static final NumberFormat NF_ES_CO = NumberFormat.getInstance(new Locale("es", "CO"));

    private static final String[][] PALETA = {
        {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
        {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
    };

    // ---- TopBar ----
    @FXML private TextField  txtBusqueda;
    @FXML private Button     btnNotif;
    @FXML private Label      lblNotifCount;
    @FXML private Button     btnUserMenu;
    @FXML private StackPane  userAvatarHolder;
    @FXML private Label      lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Hero ----
    @FXML private Label lblGreeting;
    @FXML private Label lblHeroNombre;
    @FXML private Label lblHeroSub;
    @FXML private Label lblStatRep, lblStatLikes, lblStatResenas, lblStatSiguiendo;
    private int repCount = -1;   // conteo de reproducciones para refresco en vivo

    // ---- Quick row + secciones ----
    @FXML private GridPane quickRowGrid;
    @FXML private GridPane recomendadosGrid;
    @FXML private GridPane artistasGrid;
    @FXML private VBox     continuarBox;

    // ---- BarrioCard ----
    @FXML private GridPane barrioCardRoot;
    @FXML private Label    lblBarrioCiudad;
    @FXML private VBox     barrioRankingBox;

    // MiniPlayer gestionado por MiniPlayerController vía fx:include

    private final PlaylistDAO playlistDAO = new PlaylistDAO();
    private final AlbumDAO    albumDAO    = new AlbumDAO();
    private final ArtistaDAO  artistaDAO  = new ArtistaDAO();

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            LOG.warning("No hay cliente en SessionContext — usando placeholder de demo");
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarHero(actual);
        configurarSidebarPlaylists(actual);
        configurarQuickRow(actual);
        configurarBarrioCard(actual);
        configurarRecomendados();
        configurarArtistas();
        configurarContinuar(actual);
        configurarMiniPlayer();
    }

    // -----------------------------------------------------------------
    // Top bar
    // -----------------------------------------------------------------
    private void configurarTopBar(final Cliente c) {
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#1ed760", "#1ab44e",
                        c.getNombre() + " " + c.getApellido()));
        lblUserNombre.setText(c.getNombre());
        NotificacionMenuUtil.aplicarBadge(lblNotifCount, c.getIdCliente());
    }

    // -----------------------------------------------------------------
    // Hero: stats reales del cliente
    // -----------------------------------------------------------------
    private void configurarHero(final Cliente c) {
        final String saludo = SaludoUtil.saludoActual().toUpperCase();
        lblGreeting.setText(saludo.toUpperCase());
        lblHeroNombre.setText(c.getNombre().toUpperCase() + " " + c.getApellido().toUpperCase());

        final String ciudad = c.getCiudad() == null ? "tu ciudad" : c.getCiudad();
        lblHeroSub.setText("Tu ciudad — " + ciudad + " — está escuchando vallenato clásico y cumbia. Únete.");

        if (c.getIdCliente() != null) {
            final int id = c.getIdCliente();
            final int rep = contarBD(
                    "SELECT COUNT(*) FROM REPRODUCCION WHERE CLIENTE_id_cliente = ?", id);
            final int likes = contarBD("""
                    SELECT (SELECT COUNT(*) FROM LIKE_CANCION  WHERE CLIENTE_id_cliente = ?)
                         + (SELECT COUNT(*) FROM LIKE_ALBUM    WHERE CLIENTE_id_cliente = ?)
                         + (SELECT COUNT(*) FROM LIKE_PLAYLIST WHERE CLIENTE_id_cliente = ?)
                      FROM dual""", id, id, id);
            final int resenas   = contarBD(
                    "SELECT COUNT(*) FROM RESENA WHERE CLIENTE_id_cliente = ?", id);
            final int siguiendo = contarBD(
                    "SELECT COUNT(*) FROM SEGUIMIENTO WHERE CLIENTE_id_cliente = ?", id);
            repCount = rep;
            lblStatRep.setText(NF_ES_CO.format(rep));
            lblStatLikes.setText(String.valueOf(likes));
            lblStatResenas.setText(String.valueOf(resenas));
            lblStatSiguiendo.setText(String.valueOf(siguiendo));
        } else {
            lblStatRep.setText("—");
            lblStatLikes.setText("—");
            lblStatResenas.setText("—");
            lblStatSiguiendo.setText("—");
        }

        // Refresco EN VIVO: cuando se registra una reproducción, sube el contador
        PlayerManager.getInstance().setOnNuevaReproduccion(() -> {
            if (lblStatRep.getScene() == null || repCount < 0) return;  // pantalla ya no visible
            repCount++;
            lblStatRep.setText(NF_ES_CO.format(repCount));
        });
    }

    // -----------------------------------------------------------------
    // Sidebar: playlists reales del cliente
    // -----------------------------------------------------------------
    private void configurarSidebarPlaylists(final Cliente c) {
        try {
            final List<Playlist> mias = playlistDAO.listarPorCliente(c.getIdCliente());

            if (mias.isEmpty()) {
                sidebarPlaylistsBox.getChildren().add(label("Sin playlists aún.", "bf-side-pl-meta"));
                return;
            }
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
                                label(pl.getNombre(), "bf-side-pl-name"),
                                label("Playlist", "bf-side-pl-meta")));
                item.setGraphic(row);
                sidebarPlaylistsBox.getChildren().add(item);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando playlists sidebar", ex);
        }
    }

    // -----------------------------------------------------------------
    // Quick row: primeras 6 playlists del cliente
    // -----------------------------------------------------------------
    private void configurarQuickRow(final Cliente c) {
        try {
            final List<Playlist> mias = playlistDAO.listarPorCliente(c.getIdCliente()).stream().limit(6).toList();

            for (int i = 0; i < mias.size(); i++) {
                final Playlist pl  = mias.get(i);
                final String[] col = PALETA[i % PALETA.length];
                final Button quick = new Button();
                quick.getStyleClass().add("bf-quick");
                quick.setMaxWidth(Double.MAX_VALUE);
                quick.setOnAction(e -> PlaylistUtil.abrir(pl.getIdPlaylist()));
                final HBox content = new HBox(14);
                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().addAll(
                        new AlbumCover(56, col[0], col[1]),
                        label(pl.getNombre(), "bf-quick-name"));
                quick.setGraphic(content);
                GridPane.setRowIndex(quick, i / 3);
                GridPane.setColumnIndex(quick, i % 3);
                quickRowGrid.getChildren().add(quick);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando quick row", ex);
        }
    }

    // -----------------------------------------------------------------
    // BarrioCard: top 5 artistas en la ciudad del usuario
    // -----------------------------------------------------------------
    private void configurarBarrioCard(final Cliente c) {
        final String ciudad = c.getCiudad() == null ? "Valledupar" : c.getCiudad();
        lblBarrioCiudad.setText(ciudad);

        final String sql = """
                SELECT a.nombre_artistico, COUNT(*) oyentes
                  FROM REPRODUCCION r
                  JOIN CLIENTE cl ON cl.id_cliente = r.CLIENTE_id_cliente
                  JOIN CANCION cc  ON cc.id_cancion = r.CANCION_id_cancion
                  JOIN ALBUM al    ON al.id_album = cc.ALBUM_id_album
                  JOIN ARTISTA a   ON a.id_artista = al.ARTISTA_id_artista
                 WHERE cl.ciudad = ?
                 GROUP BY a.id_artista, a.nombre_artistico
                 ORDER BY oyentes DESC
                 FETCH FIRST 5 ROWS ONLY""";

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciudad);
            try (ResultSet rs = ps.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    final String nombre  = rs.getString(1);
                    final int    oyentes = rs.getInt(2);
                    final String[] col   = PALETA[(rank - 1) % PALETA.length];

                    final HBox item = new HBox(14);
                    item.getStyleClass().add("bf-barrio-item");
                    item.setAlignment(Pos.CENTER_LEFT);

                    final Label lblRank = new Label(String.format("%02d", rank));
                    lblRank.getStyleClass().add("bf-barrio-rank");

                    final ArtistAvatar avatar = new ArtistAvatar(44, col[0], col[1], nombre);

                    final VBox info = construirVBox(2,
                            label(nombre, "bf-barrio-name"),
                            label(fmtOyentes(oyentes) + " oyentes en " + ciudad, "bf-barrio-meta"));
                    HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

                    final StackPane trendIcon = new StackPane();
                    trendIcon.getStyleClass().add("bf-trend-same");
                    trendIcon.getChildren().add(new FontIcon("bi-dash"));

                    item.getChildren().addAll(lblRank, avatar, info, trendIcon);
                    barrioRankingBox.getChildren().add(item);
                    rank++;
                }
                if (rank == 1) {
                    barrioRankingBox.getChildren().add(
                            label("Sin datos para " + ciudad + " aún.", "bf-barrio-meta"));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando barrio card", ex);
        }
    }

    // -----------------------------------------------------------------
    // Recomendados: últimos 6 álbumes con artista
    // -----------------------------------------------------------------
    private void configurarRecomendados() {
        final String sql = """
                SELECT al.id_album, al.titulo, al.anio_lanzamiento, a.nombre_artistico
                  FROM ALBUM al
                  JOIN ARTISTA a ON a.id_artista = al.ARTISTA_id_artista
                 ORDER BY al.id_album DESC
                 FETCH FIRST 6 ROWS ONLY""";

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int col = 0;
            while (rs.next()) {
                final int    idAlbum = rs.getInt(1);
                final String titulo  = rs.getString(2);
                final int    anio    = rs.getInt(3);
                final String art     = rs.getString(4);
                final String[] c     = PALETA[col % PALETA.length];
                final MediaCard card = new MediaCard(c[0], c[1], titulo, titulo, anio + " · " + art, false);
                card.onClick(() -> abrirDetalle("ALBUM", idAlbum));
                GridPane.setColumnIndex(card, col);
                recomendadosGrid.getChildren().add(card);
                col++;
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando recomendados", ex);
        }
    }

    // -----------------------------------------------------------------
    // Artistas: primeros 6 de la BD
    // -----------------------------------------------------------------
    private void configurarArtistas() {
        try {
            final List<Artista> artistas = artistaDAO.listar();
            final int max = Math.min(6, artistas.size());
            for (int i = 0; i < max; i++) {
                final Artista a  = artistas.get(i);
                final String[] c = PALETA[i % PALETA.length];
                final String sub = a.getPais() != null ? a.getPais() : "";
                final MediaCard card = new MediaCard(c[0], c[1],
                        a.getNombreArtistico(), a.getNombreArtistico(), sub, true);
                final Integer idArtista = a.getIdArtista();
                card.onClick(() -> { if (idArtista != null) abrirDetalle("ARTISTA", idArtista); });
                GridPane.setColumnIndex(card, i);
                artistasGrid.getChildren().add(card);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando artistas", ex);
        }
    }

    // -----------------------------------------------------------------
    // Continuar escuchando: últimas 4 reproducciones del cliente
    // -----------------------------------------------------------------
    private void configurarContinuar(final Cliente c) {
        if (c.getIdCliente() == null) return;

        final String sql = """
                SELECT cc.id_cancion, cc.titulo, a.nombre_artistico,
                       cc.duracion_seg, al.id_album, al.titulo, cc.ruta_archivo
                  FROM REPRODUCCION r
                  JOIN CANCION cc ON cc.id_cancion = r.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album = cc.ALBUM_id_album
                  JOIN ARTISTA a  ON a.id_artista = al.ARTISTA_id_artista
                 WHERE r.CLIENTE_id_cliente = ?
                 ORDER BY r.fecha_hora DESC
                 FETCH FIRST 4 ROWS ONLY""";

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            try (ResultSet rs = ps.executeQuery()) {
                int idx = 0;
                while (rs.next()) {
                    final int    idCancion = rs.getInt(1);
                    final String titulo    = rs.getString(2);
                    final String artista   = rs.getString(3);
                    final int    dur       = rs.getInt(4);
                    final int    albId     = rs.getInt(5);
                    final String album     = rs.getString(6);
                    final String ruta      = rs.getString(7);
                    final String[] col     = PALETA[Math.abs(albId) % PALETA.length];
                    final String durStr    = String.format("%d:%02d", dur / 60, dur % 60);
                    final Cancion cancion  = new Cancion(idCancion, titulo, dur, ruta,
                                                        null, null, null, albId, null);

                    final Button row = new Button();
                    row.getStyleClass().add("bf-rowlist-item");
                    row.setMaxWidth(Double.MAX_VALUE);
                    row.setOnAction(e -> reproducir(cancion, artista, album));
                    final HBox content = new HBox(16);
                    content.setAlignment(Pos.CENTER_LEFT);

                    // Botón "+" agregar a playlist (verde)
                    final Button btnAdd = new Button();
                    btnAdd.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0 8 0 0;");
                    final org.kordamp.ikonli.javafx.FontIcon icAdd =
                            new org.kordamp.ikonli.javafx.FontIcon("bi-plus-circle");
                    icAdd.setIconSize(20);
                    icAdd.setIconColor(javafx.scene.paint.Color.web("#1ed760"));
                    btnAdd.setGraphic(icAdd);
                    btnAdd.setTooltip(new javafx.scene.control.Tooltip("Agregar a playlist"));
                    btnAdd.setOnAction(e -> AgregarAPlaylistUtil.mostrar(
                            btnAdd, cancion.getIdCancion(), cancion.getTitulo()));

                    // Botón ▶ visible
                    final Button btnPlay = new Button("▶");
                    btnPlay.setStyle("-fx-background-color: transparent; -fx-text-fill: #1ed760;"
                            + " -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0 8 0 0;");
                    btnPlay.setOnAction(e -> reproducir(cancion, artista, album));

                    content.getChildren().addAll(
                            new AlbumCover(48, col[0], col[1]),
                            construirVBox(2,
                                    label(titulo,  "bf-rowlist-title"),
                                    label(artista, "bf-rowlist-sub")),
                            spacerFlex(),
                            btnAdd,
                            btnPlay,
                            label(durStr, "bf-rowlist-dur"));
                    row.setGraphic(content);
                    continuarBox.getChildren().add(row);
                    idx++;
                }
                if (idx == 0) {
                    continuarBox.getChildren().add(
                            label("Sin historial de reproducciones aún.", "bf-rowlist-sub"));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando continuar", ex);
        }
    }

    // -----------------------------------------------------------------
    // Reproducción — toca en el miniplayer, no navega
    // -----------------------------------------------------------------
    private void reproducir(final Cancion cancion, final String artista, final String album) {
        PlayerManager.getInstance().reproducir(cancion, artista, album);
    }

    /** Abre la pantalla de detalle de un álbum o artista. */
    private void abrirDetalle(final String tipo, final int id) {
        SessionContext.getInstance().setDetalle(tipo, id);
        HistorialNavegacion.getInstance().navegar("/view/detalle.fxml");
    }

    // -----------------------------------------------------------------
    // MiniPlayer — delegado completamente a MiniPlayerController
    // -----------------------------------------------------------------
    private void configurarMiniPlayer() {
        // El MiniPlayerController se inicializa solo vía fx:include
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------
    @FXML private void onAtras()        { HistorialNavegacion.getInstance().atras(); }
    @FXML private void onAdelante()     { HistorialNavegacion.getInstance().adelante(); }

    @FXML
    private void onBuscar() {
        final String termino = txtBusqueda.getText();
        if (termino == null || termino.isBlank()) return;
        SessionContext.getInstance().setTerminoBusqueda(termino);
        HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml");
    }
    @FXML private void onUserMenu()     { UserMenuUtil.mostrar(btnUserMenu); }
    @FXML private void onNotif()        { NotificacionMenuUtil.mostrar(btnNotif, lblNotifCount); }
    @FXML private void onIrInicio()     { /* ya estamos */ }
    @FXML private void onIrExplorar()   { HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml"); }
    @FXML private void onIrBiblioteca() { HistorialNavegacion.getInstance().navegar("/view/biblioteca.fxml"); }
    @FXML private void onIrResenas()    { HistorialNavegacion.getInstance().navegar("/view/resenas.fxml"); }
    @FXML private void onIrBarrio()     { HistorialNavegacion.getInstance().navegar("/view/barrio.fxml"); }
    @FXML private void onIrCapsulas()   { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros()     { HistorialNavegacion.getInstance().navegar("/view/logros.fxml"); }
    @FXML private void onNuevaPlaylist() { PlaylistUtil.crearNueva(btnUserMenu, () -> { sidebarPlaylistsBox.getChildren().clear(); configurarSidebarPlaylists(SessionContext.getInstance().getClienteActual()); }); }
    @FXML private void onIrPerfil()     { HistorialNavegacion.getInstance().navegar("/view/perfil.fxml"); }
    @FXML private void onAbrirPlayer()  { HistorialNavegacion.getInstance().navegar("/view/player.fxml"); }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helper BD: COUNT con N parámetros enteros
    // -----------------------------------------------------------------
    private int contarBD(final String sql, final int... ids) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.length; i++) ps.setInt(i + 1, ids[i]);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error contarBD", ex);
        }
        return 0;
    }

    // -----------------------------------------------------------------
    // Helpers UI
    // -----------------------------------------------------------------
    private static String fmtOyentes(final int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000)     return String.format("%.1fk", n / 1_000.0);
        return String.valueOf(n);
    }

    private static Label label(final String texto, final String... styleClasses) {
        final Label lbl = new Label(texto);
        lbl.getStyleClass().addAll(styleClasses);
        return lbl;
    }

    private static VBox construirVBox(final double spacing, final javafx.scene.Node... hijos) {
        final VBox v = new VBox(spacing);
        v.getChildren().addAll(hijos);
        return v;
    }

    private static Region spacerFlex() {
        final Region r = new Region();
        HBox.setHgrow(r, javafx.scene.layout.Priority.ALWAYS);
        return r;
    }

    private static Cliente clientePlaceholder() {
        final Cliente c = new Cliente();
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        return c;
    }
}
