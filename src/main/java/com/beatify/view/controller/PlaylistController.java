package com.beatify.view.controller;

import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cancion;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.AgregarAPlaylistUtil;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.NavegacionUtil;
import com.beatify.view.util.PlayerManager;
import com.beatify.view.util.PlaylistUtil;
import com.beatify.view.util.UserMenuUtil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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

/**
 * Pantalla de detalle de una playlist (playlist.fxml).
 * Lee el id desde SessionContext.getIdPlaylistSeleccionada(), trae los datos
 * de la playlist y sus canciones (JOIN sobre CANCION_PLAYLIST) y permite
 * reproducir toda la lista como cola en el PlayerManager.
 */
public class PlaylistController {

    private static final Logger LOG = Logger.getLogger(PlaylistController.class.getName());

    private static final String[][] PALETA = {
        {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
        {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
    };

    // ---- TopBar ----
    @FXML private TextField txtBusqueda;
    @FXML private Button    btnNotif;
    @FXML private Button    btnUserMenu;
    @FXML private Label     lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label     lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Hero ----
    @FXML private StackPane coverHolder;
    @FXML private Label     lblNombre;
    @FXML private Label     lblDescripcion;
    @FXML private Label     lblCreador;
    @FXML private Label     lblConteo;
    @FXML private Button    btnPlayAll;

    // ---- Tracklist ----
    @FXML private VBox tracklistBox;

    private final PlaylistDAO playlistDAO = new PlaylistDAO();

    /** Cola construida a partir de las canciones de la playlist. */
    private final List<PlayerManager.SongInfo> cola = new ArrayList<>();

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists(actual);
        cargarPlaylist();
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
        } catch (final ConexionException ex) {
            LOG.log(Level.WARNING, "Error sidebar playlists", ex);
        }
    }

    // -----------------------------------------------------------------
    // Carga de la playlist seleccionada + sus canciones
    // -----------------------------------------------------------------
    private void cargarPlaylist() {
        final Integer idPlaylist = SessionContext.getInstance().getIdPlaylistSeleccionada();
        if (idPlaylist == null) {
            lblNombre.setText("Playlist no encontrada");
            return;
        }

        // Datos de la playlist
        Playlist pl = null;
        try {
            pl = playlistDAO.buscarPorId(idPlaylist);
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "Error cargando playlist " + idPlaylist, ex);
        }

        final String[] col = PALETA[Math.abs(idPlaylist) % PALETA.length];
        coverHolder.getChildren().setAll(new AlbumCover(200, col[0], col[1]));

        if (pl != null) {
            lblNombre.setText(pl.getNombre());
            lblDescripcion.setText(pl.getDescripcion() == null ? "" : pl.getDescripcion());
            lblCreador.setText("S".equalsIgnoreCase(pl.getPublica()) ? "Pública" : "Privada");
        } else {
            lblNombre.setText("Playlist #" + idPlaylist);
        }

        // Canciones de la playlist (JOIN sobre CANCION_PLAYLIST, ordenadas)
        final String sql = """
                SELECT cc.id_cancion, cc.titulo, a.nombre_artistico,
                       cc.duracion_seg, al.id_album, al.titulo, cc.ruta_archivo, cp.orden
                  FROM CANCION_PLAYLIST cp
                  JOIN CANCION cc ON cc.id_cancion = cp.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album   = cc.ALBUM_id_album
                  JOIN ARTISTA a  ON a.id_artista  = al.ARTISTA_id_artista
                 WHERE cp.PLAYLIST_id_playlist = ?
                 ORDER BY cp.orden NULLS LAST, cp.fecha_agregada""";

        int total = 0;
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPlaylist);
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
                    final String[] colT    = PALETA[Math.abs(albId) % PALETA.length];
                    final String durStr    = String.format("%d:%02d", dur / 60, dur % 60);

                    final Cancion cancion = new Cancion(idCancion, titulo, dur, ruta,
                                                        null, null, null, albId, null);
                    cola.add(new PlayerManager.SongInfo(cancion, artista, album));

                    tracklistBox.getChildren().add(
                            crearFila(idx + 1, cancion, artista, album, durStr, colT));
                    idx++;
                }
                total = idx;
            }
        } catch (final SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando canciones de la playlist", ex);
        }

        lblConteo.setText(total + (total == 1 ? " canción" : " canciones"));
        if (total == 0) {
            tracklistBox.getChildren().add(
                    crearLabel("Esta playlist aún no tiene canciones.", "bf-rowlist-sub"));
            btnPlayAll.setDisable(true);
        }
    }

    /** Construye una fila clicable de la tracklist. */
    private Button crearFila(final int num, final Cancion cancion, final String artista,
                             final String album, final String durStr, final String[] col) {
        final Button row = new Button();
        row.getStyleClass().add("bf-rowlist-item");
        row.setMaxWidth(Double.MAX_VALUE);
        final int indice = num - 1;
        row.setOnAction(e -> reproducirDesde(indice));

        final Label lblNum = crearLabel(String.valueOf(num), "bf-track-num");
        lblNum.setMinWidth(28);

        // Botón "+" para agregar a otra playlist (verde visible)
        final Button btnAdd = new Button();
        btnAdd.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
        final org.kordamp.ikonli.javafx.FontIcon icAdd =
                new org.kordamp.ikonli.javafx.FontIcon("bi-plus-circle");
        icAdd.setIconSize(22);
        icAdd.setIconColor(javafx.scene.paint.Color.web("#1ed760"));
        btnAdd.setGraphic(icAdd);
        final var tipAdd = new javafx.scene.control.Tooltip("Agregar a playlist");
        btnAdd.setTooltip(tipAdd);
        btnAdd.setOnAction(e -> {
            e.consume();
            AgregarAPlaylistUtil.mostrar(btnAdd, cancion.getIdCancion(), cancion.getTitulo());
        });

        final HBox content = new HBox(14);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(
                lblNum,
                new AlbumCover(44, col[0], col[1]),
                construirVBox(2,
                        crearLabel(cancion.getTitulo(), "bf-rowlist-title"),
                        crearLabel(artista, "bf-rowlist-sub")),
                spacerFlex(),
                btnAdd,
                crearLabel(durStr, "bf-rowlist-dur"));
        row.setGraphic(content);
        return row;
    }

    // -----------------------------------------------------------------
    // Reproducción
    // -----------------------------------------------------------------
    private void reproducirDesde(final int indice) {
        if (cola.isEmpty()) return;
        PlayerManager.getInstance().reproducirCola(cola, indice);
    }

    @FXML
    private void onPlayAll() {
        reproducirDesde(0);
    }

    @FXML
    private void onShuffleAll() {
        if (cola.isEmpty()) return;
        PlayerManager.getInstance().reproducirCola(cola, 0);
        if (!PlayerManager.getInstance().isShuffle()) {
            PlayerManager.getInstance().toggleShuffle();
        }
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------
    @FXML private void onAtras()        { HistorialNavegacion.getInstance().atras(); }
    @FXML private void onAdelante()     { HistorialNavegacion.getInstance().adelante(); }
    @FXML private void onNotif()        { com.beatify.view.util.NotificacionMenuUtil.mostrar(btnNotif, lblNotifCount); }
    @FXML private void onUserMenu()     { UserMenuUtil.mostrar(btnUserMenu); }
    @FXML private void onIrInicio()     { HistorialNavegacion.getInstance().navegar("/view/home.fxml"); }
    @FXML private void onIrExplorar()   { HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml"); }
    @FXML private void onIrBiblioteca() { HistorialNavegacion.getInstance().navegar("/view/biblioteca.fxml"); }
    @FXML private void onIrResenas()    { HistorialNavegacion.getInstance().navegar("/view/resenas.fxml"); }
    @FXML private void onIrBarrio()     { HistorialNavegacion.getInstance().navegar("/view/barrio.fxml"); }
    @FXML private void onIrCapsulas()   { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros()     { HistorialNavegacion.getInstance().navegar("/view/logros.fxml"); }
    @FXML private void onIrPerfil()     { HistorialNavegacion.getInstance().navegar("/view/perfil.fxml"); }
    @FXML private void onNuevaPlaylist() {
        PlaylistUtil.crearNueva(btnUserMenu, () -> {
            sidebarPlaylistsBox.getChildren().clear();
            configurarSidebarPlaylists(SessionContext.getInstance().getClienteActual());
        });
    }

    @FXML
    private void onBuscar() {
        final String termino = txtBusqueda.getText();
        if (termino == null || termino.isBlank()) return;
        SessionContext.getInstance().setTerminoBusqueda(termino);
        HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml");
    }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helpers UI
    // -----------------------------------------------------------------
    private static Label crearLabel(final String texto, final String... styleClasses) {
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
        HBox.setHgrow(r, Priority.ALWAYS);
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
