package com.beatify.view.controller;

import com.beatify.dao.BusquedaDAO;
import com.beatify.dao.GeneroDAO;
import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Busqueda;
import com.beatify.model.Cancion;
import com.beatify.model.Cliente;
import com.beatify.model.Genero;
import com.beatify.model.Playlist;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.component.MediaCard;
import com.beatify.view.util.AgregarAPlaylistUtil;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.PlayerManager;
import com.beatify.view.util.PlaylistUtil;
import com.beatify.view.util.UserMenuUtil;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CatalogoController {

    private static final Logger LOG = Logger.getLogger(CatalogoController.class.getName());

    private static final String[][] PALETA = {
        {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
        {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
    };

    // ---- TopBar ----
    @FXML private TextField txtBusqueda;
    @FXML private Button    btnNotif;
    @FXML private Label     lblNotifCount;
    @FXML private Button    btnUserMenu;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label     lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Filtros ----
    @FXML private FlowPane         filtrosPane;
    @FXML private ComboBox<String> cmbOrden;

    // ---- Contenido ----
    @FXML private FlowPane generosPane;
    @FXML private GridPane albumesGrid;
    @FXML private VBox     tracklistBox;

    private String generoFiltro   = null;
    private String busquedaFiltro = null;

    private final GeneroDAO    generoDAO    = new GeneroDAO();
    private final PlaylistDAO  playlistDAO  = new PlaylistDAO();
    private final BusquedaDAO  busquedaDAO  = new BusquedaDAO();

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists(actual);
        configurarFiltros();
        configurarGeneros();

        // Recoger búsqueda pendiente (viene de Home u otra pantalla)
        final String pendiente = SessionContext.getInstance().consumirTerminoBusqueda();
        if (pendiente != null) {
            txtBusqueda.setText(pendiente);
            busquedaFiltro = pendiente;
        }

        configurarAlbumes();
        configurarTracklist();
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
    // Sidebar: playlists reales del cliente
    // -----------------------------------------------------------------
    private void configurarSidebarPlaylists(final Cliente c) {
        try {
            final List<Playlist> mias = playlistDAO.listarPorCliente(c.getIdCliente());

            for (int i = 0; i < mias.size(); i++) {
                final Playlist pl  = mias.get(i);
                final String[] col = PALETA[i % PALETA.length];
                final Button item  = new Button();
                item.getStyleClass().add("bf-side-playlist");
                item.setMaxWidth(Double.MAX_VALUE);
                final HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.getChildren().addAll(
                        new AlbumCover(32, col[0], col[1]),
                        crearVBox(2,
                                crearLabel(pl.getNombre(), "bf-side-pl-name"),
                                crearLabel("Playlist", "bf-side-pl-meta")));
                item.setGraphic(row);
                sidebarPlaylistsBox.getChildren().add(item);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando playlists sidebar", ex);
        }
    }

    // -----------------------------------------------------------------
    // Filtros: chip "Todos" + géneros reales de BD
    // -----------------------------------------------------------------
    private void configurarFiltros() {
        // Chip "Todos"
        final ToggleButton todos = new ToggleButton("Todos");
        todos.getStyleClass().add("bf-pref-chip");
        todos.setSelected(true);
        todos.setOnAction(e -> {
            deselectOtros(todos);
            generoFiltro = null;
            aplicarFiltro();
        });
        filtrosPane.getChildren().add(todos);

        // Géneros desde BD
        try {
            for (final Genero g : generoDAO.listar()) {
                final ToggleButton chip = new ToggleButton(g.getNombre());
                chip.getStyleClass().add("bf-pref-chip");
                chip.setOnAction(e -> {
                    deselectOtros(chip);
                    todos.setSelected(false);
                    generoFiltro = g.getNombre();
                    aplicarFiltro();
                });
                filtrosPane.getChildren().add(chip);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando géneros para filtros", ex);
        }

        cmbOrden.getItems().addAll("A–Z", "Z–A", "Más reciente", "Más antiguo");
        cmbOrden.setValue("Más reciente");
        cmbOrden.setOnAction(e -> aplicarFiltro());
    }

    private void deselectOtros(final ToggleButton activo) {
        filtrosPane.getChildren().forEach(n -> {
            if (n instanceof ToggleButton tb && tb != activo) tb.setSelected(false);
        });
        activo.setSelected(true);
    }

    private void aplicarFiltro() {
        configurarTracklist();
    }

    // -----------------------------------------------------------------
    // Géneros raíz: tarjetas desde BD
    // -----------------------------------------------------------------
    private void configurarGeneros() {
        generosPane.getChildren().clear();
        try {
            final List<Genero> generos = generoDAO.listar();
            for (int i = 0; i < generos.size(); i++) {
                final Genero g   = generos.get(i);
                final String[] c = PALETA[i % PALETA.length];

                final Button card = new Button();
                card.getStyleClass().add("bf-card");
                card.setPrefWidth(200);
                card.setPrefHeight(100);
                card.setMaxWidth(Double.MAX_VALUE);

                final StackPane sp = new StackPane();
                sp.setPrefSize(200, 100);
                sp.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                        + c[0] + ", " + c[1] + "); -fx-background-radius: 10;");

                final VBox info = new VBox(4);
                info.setAlignment(Pos.BOTTOM_LEFT);
                info.setPadding(new Insets(0, 0, 12, 14));

                final Label lblNombre = new Label(g.getNombre());
                lblNombre.setStyle("-fx-font-family: 'Sora'; -fx-font-size: 18px;"
                        + " -fx-font-weight: bold; -fx-text-fill: white;");

                final String region = g.getOrigenPais() != null ? g.getOrigenPais() : "";
                final Label lblRegion = new Label(region);
                lblRegion.setStyle("-fx-font-family: 'Manrope'; -fx-font-size: 11px;"
                        + " -fx-text-fill: rgba(255,255,255,0.7);");

                info.getChildren().addAll(lblNombre, lblRegion);
                sp.getChildren().add(info);
                card.setGraphic(sp);

                final String generoNombre = g.getNombre();
                card.setOnAction(e -> {
                    generoFiltro = generoNombre;
                    deselectChipPorNombre(generoNombre);
                    aplicarFiltro();
                });
                generosPane.getChildren().add(card);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando géneros raíz", ex);
        }
    }

    private void deselectChipPorNombre(final String nombre) {
        filtrosPane.getChildren().forEach(n -> {
            if (n instanceof ToggleButton tb) {
                tb.setSelected(tb.getText().equals(nombre));
            }
        });
    }

    // -----------------------------------------------------------------
    // Álbumes destacados: últimos 6 (filtrados por búsqueda si hay término)
    // -----------------------------------------------------------------
    private void configurarAlbumes() {
        albumesGrid.getChildren().clear();

        final boolean hayBusqueda = busquedaFiltro != null && !busquedaFiltro.isBlank();
        final String sql = hayBusqueda
                ? """
                  SELECT al.id_album, al.titulo, al.anio_lanzamiento, a.nombre_artistico
                    FROM ALBUM al
                    JOIN ARTISTA a ON a.id_artista = al.ARTISTA_id_artista
                   WHERE UPPER(al.titulo) LIKE UPPER(?)
                      OR UPPER(a.nombre_artistico) LIKE UPPER(?)
                   ORDER BY al.id_album DESC
                   FETCH FIRST 6 ROWS ONLY"""
                : """
                  SELECT al.id_album, al.titulo, al.anio_lanzamiento, a.nombre_artistico
                    FROM ALBUM al
                    JOIN ARTISTA a ON a.id_artista = al.ARTISTA_id_artista
                   ORDER BY al.id_album DESC
                   FETCH FIRST 6 ROWS ONLY""";

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hayBusqueda) {
                final String like = "%" + busquedaFiltro + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                int col = 0;
                while (rs.next()) {
                    final String titulo = rs.getString(2);
                    final int    anio   = rs.getInt(3);
                    final String art    = rs.getString(4);
                    final String[] c    = PALETA[col % PALETA.length];
                    final MediaCard card = new MediaCard(c[0], c[1], titulo, titulo, anio + " · " + art, false);
                    GridPane.setColumnIndex(card, col);
                    albumesGrid.getChildren().add(card);
                    col++;
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando álbumes", ex);
        }
    }

    // -----------------------------------------------------------------
    // Tracklist de canciones (con filtros de género y búsqueda combinados)
    // -----------------------------------------------------------------
    private void configurarTracklist() {
        tracklistBox.getChildren().clear();
        tracklistBox.getChildren().add(crearCabeceraTracklist());

        final boolean hayGenero   = generoFiltro   != null && !generoFiltro.isBlank();
        final boolean hayBusqueda = busquedaFiltro != null && !busquedaFiltro.isBlank();

        final StringBuilder sql = new StringBuilder("""
                SELECT c.id_cancion, c.titulo, a.nombre_artistico,
                       al.titulo, g.nombre, c.duracion_seg, al.id_album,
                       c.ruta_archivo
                  FROM CANCION c
                  JOIN ALBUM al   ON al.id_album = c.ALBUM_id_album
                  JOIN ARTISTA a  ON a.id_artista = al.ARTISTA_id_artista
                  JOIN GENERO g   ON g.id_genero = c.GENERO_id_genero
                """);

        if (hayGenero && hayBusqueda) {
            sql.append(" WHERE UPPER(g.nombre) = UPPER(?)"
                    + " AND (UPPER(c.titulo) LIKE UPPER(?)"
                    + "      OR UPPER(a.nombre_artistico) LIKE UPPER(?))");
        } else if (hayGenero) {
            sql.append(" WHERE UPPER(g.nombre) = UPPER(?)");
        } else if (hayBusqueda) {
            sql.append(" WHERE UPPER(c.titulo) LIKE UPPER(?)"
                    + "    OR UPPER(a.nombre_artistico) LIKE UPPER(?)");
        }
        sql.append(" ORDER BY c.id_cancion FETCH FIRST 50 ROWS ONLY");

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int param = 1;
            if (hayGenero && hayBusqueda) {
                final String like = "%" + busquedaFiltro + "%";
                ps.setString(param++, generoFiltro);
                ps.setString(param++, like);
                ps.setString(param,   like);
            } else if (hayGenero) {
                ps.setString(param, generoFiltro);
            } else if (hayBusqueda) {
                final String like = "%" + busquedaFiltro + "%";
                ps.setString(param++, like);
                ps.setString(param,   like);
            }

            // Recopilar cola completa antes de crear filas
            final java.util.List<PlayerManager.SongInfo> cola = new java.util.ArrayList<>();
            final java.util.List<String[]>               filas = new java.util.ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final int    idCan   = rs.getInt(1);
                    final String titulo  = rs.getString(2);
                    final String artista = rs.getString(3);
                    final String album   = rs.getString(4);
                    final int    durSeg  = rs.getInt(6);
                    final int    albId   = rs.getInt(7);
                    final String ruta    = rs.getString(8);
                    cola.add(new PlayerManager.SongInfo(
                            new Cancion(idCan, titulo, durSeg, ruta, null, null, null, albId, null),
                            artista, album));
                    filas.add(new String[]{
                        String.valueOf(cola.size()), titulo, artista, album,
                        rs.getString(5), formatDur(durSeg),
                        PALETA[Math.abs(albId) % PALETA.length][0],
                        PALETA[Math.abs(albId) % PALETA.length][1]
                    });
                }
            }

            if (cola.isEmpty()) {
                final String msg = hayBusqueda
                        ? "Sin resultados para \"" + busquedaFiltro + "\"."
                        : "Sin canciones" + (hayGenero ? " para " + generoFiltro : "") + ".";
                tracklistBox.getChildren().add(crearLabel(msg, "bf-field-lbl"));
            }

            final java.util.List<PlayerManager.SongInfo> colaFinal = java.util.List.copyOf(cola);
            for (int i = 0; i < filas.size(); i++) {
                final int idx = i;
                final HBox row = crearFilaTracklist(filas.get(i));
                row.setStyle(row.getStyle() + "; -fx-cursor: hand;");
                row.setOnMouseClicked(e -> reproducirEnCola(colaFinal, idx));

                // Botón "+" para agregar a playlist (verde visible, antes del play)
                final com.beatify.model.Cancion can = colaFinal.get(idx).cancion();
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
                    AgregarAPlaylistUtil.mostrar(btnAdd, can.getIdCancion(), can.getTitulo());
                });
                row.getChildren().add(row.getChildren().size() - 1, btnAdd);

                final int last = row.getChildren().size() - 1;
                if (row.getChildren().get(last) instanceof javafx.scene.control.Button bp) {
                    bp.setOnAction(e -> { e.consume(); reproducirEnCola(colaFinal, idx); });
                }
                tracklistBox.getChildren().add(row);
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando tracklist", ex);
            tracklistBox.getChildren().add(crearLabel("Error consultando BD.", "bf-field-lbl"));
        }
    }

    private HBox crearCabeceraTracklist() {
        final HBox header = new HBox(0);
        header.getStyleClass().add("bf-tracklist-header");
        header.setPadding(new Insets(12, 16, 12, 16));
        header.getChildren().addAll(
                crearLabel("#", 40, "bf-track-num"),
                crearLabel("TÍTULO", 280, "bf-track-col"),
                crearLabel("ÁLBUM", 180, "bf-track-col"),
                crearLabel("GÉNERO", 120, "bf-track-col"),
                crearLabel("DURACIÓN", 70, "bf-track-col"));
        return header;
    }

    private HBox crearFilaTracklist(final String[] c) {
        final HBox row = new HBox(0);
        row.getStyleClass().add("bf-tracklist-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));

        final Label lblNum = crearLabel(c[0], 40, "bf-track-num");

        final HBox titleCell = new HBox(10);
        titleCell.setAlignment(Pos.CENTER_LEFT);
        titleCell.setMinWidth(280);
        titleCell.getChildren().addAll(
                new AlbumCover(36, c[6], c[7]),
                crearVBoxTitulo(c[1], c[2]));

        final Label lblAlbum = crearLabel(c[3], 180, "bf-track-album");

        final Label lblGenero = new Label(c[4]);
        lblGenero.getStyleClass().add("bf-genre-pill");
        final HBox generoWrap = new HBox(lblGenero);
        generoWrap.setMinWidth(120);
        generoWrap.setAlignment(Pos.CENTER_LEFT);

        final Label lblDur = crearLabel(c[5], 60, "bf-track-dur");

        final Button btnPlay = new Button("▶");
        btnPlay.setStyle("-fx-background-color: transparent; -fx-text-fill: #1ed760;"
                + " -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 0 4;");

        row.getChildren().addAll(lblNum, titleCell, lblAlbum, generoWrap, lblDur, btnPlay);
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
    // Reproducción: clic en fila — toca sin navegar, con cola completa
    // -----------------------------------------------------------------
    private void reproducirEnCola(final java.util.List<PlayerManager.SongInfo> cola, final int indice) {
        PlayerManager.getInstance().reproducirCola(cola, indice);
    }

    // -----------------------------------------------------------------
    // Búsqueda
    // -----------------------------------------------------------------
    @FXML
    private void onBuscar() {
        final String termino = txtBusqueda.getText();
        busquedaFiltro = (termino == null || termino.isBlank()) ? null : termino.trim();

        configurarAlbumes();
        configurarTracklist();

        // Registrar en BD (no bloquea UI si falla)
        final Cliente c = SessionContext.getInstance().getClienteActual();
        if (busquedaFiltro != null && c != null && c.getIdCliente() != null) {
            try {
                busquedaDAO.insertar(new Busqueda(
                        null, busquedaFiltro, LocalDateTime.now(), null, c.getIdCliente()));
            } catch (final Exception ex) {
                LOG.log(Level.WARNING, "No se pudo registrar la búsqueda en BD", ex);
            }
        }
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------
    @FXML private void onAtras()        { HistorialNavegacion.getInstance().atras(); }
    @FXML private void onAdelante()     { HistorialNavegacion.getInstance().adelante(); }
    @FXML private void onUserMenu()     { UserMenuUtil.mostrar(btnUserMenu); }
    @FXML private void onNotif()        { com.beatify.view.util.NotificacionMenuUtil.mostrar(btnNotif, lblNotifCount); }
    @FXML private void onIrInicio()         { HistorialNavegacion.getInstance().navegar("/view/home.fxml"); }
    @FXML private void onIrExplorar()       { /* ya estamos */ }
    @FXML private void onIrBiblioteca() { HistorialNavegacion.getInstance().navegar("/view/biblioteca.fxml"); }
    @FXML private void onIrResenas()        { HistorialNavegacion.getInstance().navegar("/view/resenas.fxml"); }
    @FXML private void onIrBarrio()         { HistorialNavegacion.getInstance().navegar("/view/barrio.fxml"); }
    @FXML private void onIrCapsulas()       { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros()         { HistorialNavegacion.getInstance().navegar("/view/logros.fxml"); }
    @FXML private void onIrPerfil()         { HistorialNavegacion.getInstance().navegar("/view/perfil.fxml"); }
    @FXML private void onNuevaPlaylist() { PlaylistUtil.crearNueva(btnUserMenu, () -> { sidebarPlaylistsBox.getChildren().clear(); configurarSidebarPlaylists(SessionContext.getInstance().getClienteActual()); }); }
    @FXML private void onVerTodosAlbumes()  { LOG.info("Ver todos los álbumes"); }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------
    private static String formatDur(final int seg) {
        return String.format("%d:%02d", seg / 60, seg % 60);
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
