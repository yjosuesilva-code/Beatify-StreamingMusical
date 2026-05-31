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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pantalla de detalle de un ÁLBUM o ARTISTA (detalle.fxml).
 * Lee tipo + id desde SessionContext.getTipoDetalle()/getIdDetalle().
 *  - ALBUM:   hero con portada + título + artista/año; tracklist del álbum.
 *  - ARTISTA: hero con avatar + nombre + país; tracklist con sus canciones.
 * Reproduce toda la lista como cola en el PlayerManager.
 */
public class DetalleController {

    private static final Logger LOG = Logger.getLogger(DetalleController.class.getName());

    private static final String[][] PALETA = {
        {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
        {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
    };

    @FXML private TextField txtBusqueda;
    @FXML private Button    btnNotif, btnUserMenu, btnPlayAll;
    @FXML private Label     lblNotifCount, lblUserNombre, lblTipo, lblNombre, lblSubtitulo, lblConteo;
    @FXML private StackPane userAvatarHolder, coverHolder;
    @FXML private VBox      sidebarPlaylistsBox, tracklistBox;

    private final PlaylistDAO playlistDAO = new PlaylistDAO();
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
        cargarDetalle();
    }

    private void configurarTopBar(final Cliente c) {
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#1ed760", "#1ab44e", c.getNombre() + " " + c.getApellido()));
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
    // Carga del álbum o artista seleccionado
    // -----------------------------------------------------------------
    private void cargarDetalle() {
        final String  tipo = SessionContext.getInstance().getTipoDetalle();
        final Integer id   = SessionContext.getInstance().getIdDetalle();
        if (tipo == null || id == null) {
            lblNombre.setText("No encontrado");
            return;
        }

        final String[] col = PALETA[Math.abs(id) % PALETA.length];
        final boolean esArtista = "ARTISTA".equalsIgnoreCase(tipo);

        // Cabecera (nombre + subtítulo) y cover/avatar
        if (esArtista) {
            lblTipo.setText("ARTISTA");
            final String[] info = cabeceraArtista(id);   // [nombre, pais]
            lblNombre.setText(info[0]);
            lblSubtitulo.setText(info[1] != null ? info[1] : "");
            coverHolder.getChildren().setAll(new ArtistAvatar(200, col[0], col[1], info[0]));
        } else {
            lblTipo.setText("ÁLBUM");
            final String[] info = cabeceraAlbum(id);      // [titulo, "año · artista"]
            lblNombre.setText(info[0]);
            lblSubtitulo.setText(info[1] != null ? info[1] : "");
            coverHolder.getChildren().setAll(new AlbumCover(200, col[0], col[1], info[0]));
        }

        // Canciones
        final String sql = esArtista
            ? """
                SELECT cc.id_cancion, cc.titulo, a.nombre_artistico, cc.duracion_seg,
                       al.id_album, al.titulo, cc.ruta_archivo
                  FROM CANCION cc
                  JOIN ALBUM al  ON al.id_album = cc.ALBUM_id_album
                  JOIN ARTISTA a ON a.id_artista = al.ARTISTA_id_artista
                 WHERE al.ARTISTA_id_artista = ?
                 ORDER BY cc.id_cancion"""
            : """
                SELECT cc.id_cancion, cc.titulo, a.nombre_artistico, cc.duracion_seg,
                       al.id_album, al.titulo, cc.ruta_archivo
                  FROM CANCION cc
                  JOIN ALBUM al  ON al.id_album = cc.ALBUM_id_album
                  JOIN ARTISTA a ON a.id_artista = al.ARTISTA_id_artista
                 WHERE cc.ALBUM_id_album = ?
                 ORDER BY cc.id_cancion""";

        int total = 0;
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
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
                    final Cancion cancion  = new Cancion(idCancion, titulo, dur, ruta,
                                                        null, null, null, albId, null);
                    cola.add(new PlayerManager.SongInfo(cancion, artista, album));
                    tracklistBox.getChildren().add(crearFila(idx + 1, cancion, artista, durStr, colT));
                    idx++;
                }
                total = idx;
            }
        } catch (final SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando detalle", ex);
        }

        lblConteo.setText(total + (total == 1 ? " canción" : " canciones"));
        if (total == 0) {
            tracklistBox.getChildren().add(crearLabel("Sin canciones disponibles.", "bf-rowlist-sub"));
            btnPlayAll.setDisable(true);
        }
    }

    private String[] cabeceraAlbum(final int idAlbum) {
        final String sql = """
                SELECT al.titulo, al.anio_lanzamiento, a.nombre_artistico
                  FROM ALBUM al JOIN ARTISTA a ON a.id_artista = al.ARTISTA_id_artista
                 WHERE al.id_album = ?""";
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlbum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{ rs.getString(1), rs.getInt(2) + " · " + rs.getString(3) };
                }
            }
        } catch (final SQLException ex) {
            LOG.log(Level.WARNING, "Error cabecera álbum", ex);
        }
        return new String[]{ "Álbum #" + idAlbum, "" };
    }

    private String[] cabeceraArtista(final int idArtista) {
        final String sql = "SELECT nombre_artistico, pais FROM ARTISTA WHERE id_artista = ?";
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new String[]{ rs.getString(1), rs.getString(2) };
            }
        } catch (final SQLException ex) {
            LOG.log(Level.WARNING, "Error cabecera artista", ex);
        }
        return new String[]{ "Artista #" + idArtista, "" };
    }

    private Button crearFila(final int num, final Cancion cancion, final String artista,
                             final String durStr, final String[] col) {
        final Button row = new Button();
        row.getStyleClass().add("bf-rowlist-item");
        row.setMaxWidth(Double.MAX_VALUE);
        final int indice = num - 1;
        row.setOnAction(e -> reproducirDesde(indice));

        final Label lblNum = crearLabel(String.valueOf(num), "bf-track-num");
        lblNum.setMinWidth(28);

        final Button btnAdd = new Button();
        btnAdd.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4;");
        final FontIcon icAdd = new FontIcon("bi-plus-circle");
        icAdd.setIconSize(20);
        icAdd.setIconColor(Color.web("#1ed760"));
        btnAdd.setGraphic(icAdd);
        btnAdd.setTooltip(new javafx.scene.control.Tooltip("Agregar a playlist"));
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

    private void reproducirDesde(final int indice) {
        if (!cola.isEmpty()) PlayerManager.getInstance().reproducirCola(cola, indice);
    }

    @FXML private void onPlayAll()    { reproducirDesde(0); }
    @FXML private void onShuffleAll() {
        if (cola.isEmpty()) return;
        PlayerManager.getInstance().reproducirCola(cola, 0);
        if (!PlayerManager.getInstance().isShuffle()) PlayerManager.getInstance().toggleShuffle();
    }

    // ---- Navegación (idéntica al resto) ----
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
    @FXML private void onNuevaPlaylist() {
        PlaylistUtil.crearNueva(btnUserMenu, () -> {
            sidebarPlaylistsBox.getChildren().clear();
            configurarSidebarPlaylists(SessionContext.getInstance().getClienteActual());
        });
    }

    @FXML
    private void onBuscar() {
        final String t = txtBusqueda.getText();
        if (t == null || t.isBlank()) return;
        SessionContext.getInstance().setTerminoBusqueda(t);
        HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml");
    }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // ---- Helpers ----
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
        c.setNombre("Yilver"); c.setApellido("Silva"); c.setCiudad("Valledupar");
        return c;
    }
}
