package com.beatify.view.controller;

import com.beatify.dao.PlaylistDAO;
import com.beatify.dao.ResenaDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.model.Resena;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.PlayerManager;
import com.beatify.view.util.PlaylistUtil;
import com.beatify.view.util.UserMenuUtil;
import com.beatify.view.util.NavegacionUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller de Reseñas (resenas.fxml).
 *
 * Funcionalidad distintiva #1 de Beatify.
 *
 * Flujo:
 *   1. initialize() carga las reseñas del cliente activo via ResenaDAO.
 *   2. Filtros por tipo (Canción / Álbum / Artista) repintan la lista.
 *   3. El form de "Nueva reseña" invoca PKG_RESENAS.CREAR_RESENA
 *      como CallableStatement, demostrando integración con PL/SQL.
 *   4. Captura ORA-20010 (calificación inválida) y la muestra al usuario.
 */
public class ResenasController {

    private static final Logger LOG = Logger.getLogger(ResenasController.class.getName());
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ---- TopBar ----
    @FXML private Button btnNotif;
    @FXML private Button btnUserMenu;
    @FXML private Label lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Resumen ----
    @FXML private Label lblTotalResenas;
    @FXML private Label lblPromedioDado;

    // ---- Filtros ----
    @FXML private ToggleButton tabTodas;
    @FXML private ToggleButton tabCancion;
    @FXML private ToggleButton tabAlbum;
    @FXML private ToggleButton tabArtista;

    // ---- Lista ----
    @FXML private VBox listaResenasBox;

    // ---- Topbar search ----
    @FXML private TextField txtBusqueda;

    // ---- Form nueva reseña ----
    @FXML private ComboBox<String>      cmbTipo;        // CANCION / ALBUM / ARTISTA
    @FXML private ComboBox<ObjetivoRef> cmbObjetivo;    // objetivos del tipo, por nombre
    @FXML private TextField txtIdObjetivo;              // interno (compat)
    @FXML private Spinner<Integer> spnCalificacion;
    @FXML private TextArea txtComentario;
    @FXML private Label lblFeedback;
    @FXML private Button btnUsarActual;

    private final ResenaDAO resenaDAO = new ResenaDAO();
    private String filtroActivo = "TODAS";
    private List<Resena> todasMisResenas;

    /** Referencia ligera id→nombre para los ComboBox de objetivo. */
    private record ObjetivoRef(int id, String nombre) {
        @Override public String toString() { return nombre; }
    }

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }
        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarForm();
        configurarFiltros();
        cargarResenasDelCliente(actual.getIdCliente());

        // Si ya hay una canción sonando, precargarla como objetivo de reseña
        // Si hay una canción sonando, preseleccionarla como atajo (sin warning)
        if (PlayerManager.getInstance().getSongActual() != null) onUsarCancionActual();
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

    private void configurarSidebarPlaylists() {
        final Cliente c = SessionContext.getInstance().getClienteActual();
        if (c == null || c.getIdCliente() == null) return;
        final String[][] colores = {
                {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#3a6a8a", "#051a2a"},
                {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}, {"#7a3a8a", "#1a052a"}
        };
        try {
            final List<Playlist> mias = new PlaylistDAO().listarPorCliente(c.getIdCliente());
            for (int i = 0; i < mias.size(); i++) {
                final Playlist pl  = mias.get(i);
                final String[] col = colores[i % colores.length];
                final Button item = new Button();
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
            LOG.log(Level.WARNING, "Error cargando playlists sidebar", ex);
        }
    }

    // -----------------------------------------------------------------
    // Form y filtros
    // -----------------------------------------------------------------
    private void configurarForm() {
        cmbTipo.getItems().addAll("CANCION", "ALBUM", "ARTISTA");
        // Al cambiar el tipo, recargar la lista de objetivos por nombre
        cmbTipo.valueProperty().addListener((obs, o, tipo) -> cargarObjetivos(tipo));
        cmbTipo.setValue("CANCION");   // dispara cargarObjetivos("CANCION")
        spnCalificacion.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5));
    }

    /** Llena cmbObjetivo con los nombres reales (id+nombre) del tipo dado. */
    private void cargarObjetivos(final String tipo) {
        cmbObjetivo.getItems().clear();
        if (tipo == null) return;
        final String sql = switch (tipo.toUpperCase()) {
            case "ALBUM"   -> "SELECT id_album, titulo FROM ALBUM ORDER BY titulo";
            case "ARTISTA" -> "SELECT id_artista, nombre_artistico FROM ARTISTA ORDER BY nombre_artistico";
            default        -> "SELECT id_cancion, titulo FROM CANCION ORDER BY titulo";
        };
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbObjetivo.getItems().add(new ObjetivoRef(rs.getInt(1), rs.getString(2)));
            }
        } catch (final SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando objetivos de reseña", ex);
        }
    }

    private void configurarFiltros() {
        tabTodas.setSelected(true);
        tabTodas.setOnAction(e -> { filtroActivo = "TODAS"; pintarLista(); });
        tabCancion.setOnAction(e -> { filtroActivo = "CANCION"; pintarLista(); });
        tabAlbum.setOnAction(e -> { filtroActivo = "ALBUM"; pintarLista(); });
        tabArtista.setOnAction(e -> { filtroActivo = "ARTISTA"; pintarLista(); });
    }

    // -----------------------------------------------------------------
    // Carga desde BD
    // -----------------------------------------------------------------
    private void cargarResenasDelCliente(final Integer idCliente) {
        try {
            todasMisResenas = resenaDAO.listarPorCliente(idCliente);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error cargando reseñas", ex);
            todasMisResenas = List.of();
            lblFeedback.setText("⚠ Error al cargar reseñas: " + ex.getMessage());
        }
        actualizarResumen();
        pintarLista();
    }

    private void actualizarResumen() {
        if (todasMisResenas == null || todasMisResenas.isEmpty()) {
            lblTotalResenas.setText("0");
            lblPromedioDado.setText("—");
            return;
        }
        lblTotalResenas.setText(String.valueOf(todasMisResenas.size()));
        double prom = todasMisResenas.stream()
                .mapToInt(Resena::getCalificacion).average().orElse(0);
        lblPromedioDado.setText(String.format("%.1f ★", prom));
    }

    private void pintarLista() {
        listaResenasBox.getChildren().clear();
        if (todasMisResenas == null || todasMisResenas.isEmpty()) {
            final Label vacio = new Label("Aún no has escrito reseñas. ¡Usa el form de abajo!");
            vacio.getStyleClass().add("bf-field-lbl");
            listaResenasBox.getChildren().add(vacio);
            return;
        }
        int impresas = 0;
        for (final Resena r : todasMisResenas) {
            if (!"TODAS".equals(filtroActivo) && !filtroActivo.equals(r.getTipoObjetivo())) {
                continue;
            }
            listaResenasBox.getChildren().add(construirCard(r));
            impresas++;
        }
        if (impresas == 0) {
            final Label nada = new Label("Sin reseñas de tipo " + filtroActivo + ".");
            nada.getStyleClass().add("bf-field-lbl");
            listaResenasBox.getChildren().add(nada);
        }
    }

    /**
     * Traduce (tipo, id) al nombre legible del objetivo reseñado.
     * CANCION/ALBUM → titulo; ARTISTA → nombre_artistico.
     * Si no se encuentra, devuelve "id N" como respaldo.
     */
    private String resolverNombreObjetivo(final String tipo, final Integer idObj) {
        if (tipo == null || idObj == null) return "—";
        final String sql;
        switch (tipo.toUpperCase()) {
            case "CANCION" -> sql = "SELECT titulo FROM CANCION WHERE id_cancion = ?";
            case "ALBUM"   -> sql = "SELECT titulo FROM ALBUM WHERE id_album = ?";
            case "ARTISTA" -> sql = "SELECT nombre_artistico FROM ARTISTA WHERE id_artista = ?";
            default        -> { return "id " + idObj; }
        }
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idObj);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final String nombre = rs.getString(1);
                    return (nombre == null || nombre.isBlank()) ? "id " + idObj : nombre;
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.FINE, "No se pudo resolver nombre del objetivo", ex);
        }
        return "id " + idObj;
    }

    private VBox construirCard(final Resena r) {
        final VBox card = new VBox(8);
        card.getStyleClass().add("bf-card");

        // Header: tipo · NOMBRE  +  estrellas
        final HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        final String nombreObj = resolverNombreObjetivo(r.getTipoObjetivo(), r.getIdObjetivo());
        final Label tipo = new Label(r.getTipoObjetivo() + " · " + nombreObj);
        tipo.getStyleClass().add("bf-section-eyebrow");
        final Label estrellas = new Label("★".repeat(r.getCalificacion())
                + "☆".repeat(5 - r.getCalificacion()));
        estrellas.setStyle("-fx-font-size: 18px; -fx-text-fill: #F2C94C;");
        HBox.setHgrow(tipo, Priority.ALWAYS);
        tipo.setMaxWidth(Double.MAX_VALUE);
        header.getChildren().addAll(tipo, estrellas);

        // Comentario
        final Label coment = new Label(
                r.getComentario() == null || r.getComentario().isBlank()
                        ? "(sin comentario)" : r.getComentario());
        coment.setWrapText(true);
        coment.getStyleClass().add("bf-page-meta");

        // Footer: fecha
        final Label fecha = new Label(r.getFechaResena() != null
                ? r.getFechaResena().format(FECHA_FMT) : "—");
        fecha.getStyleClass().add("bf-field-lbl");

        card.getChildren().addAll(header, coment, fecha);
        return card;
    }

    // -----------------------------------------------------------------
    // Crear nueva reseña via PKG_RESENAS.CREAR_RESENA (CallableStatement)
    // -----------------------------------------------------------------
    @FXML
    private void onPublicar() {
        final Cliente cliente = SessionContext.getInstance().getClienteActual();
        if (cliente == null || cliente.getIdCliente() == null) {
            lblFeedback.setText("⚠ Sin sesión activa");
            return;
        }
        // Validar form
        final String tipo = cmbTipo.getValue();
        final Integer calif = spnCalificacion.getValue();
        final ObjetivoRef objetivo = cmbObjetivo.getValue();
        if (tipo == null) {
            lblFeedback.setText("⚠ Elige un tipo (canción, álbum o artista)");
            return;
        }
        if (objetivo == null) {
            lblFeedback.setText("⚠ Elige qué quieres reseñar en la lista");
            return;
        }
        final int idObj = objetivo.id();
        final String coment = txtComentario.getText();

        // Invocar PKG_RESENAS.CREAR_RESENA via CallableStatement
        final String sql = "{call PKG_RESENAS.CREAR_RESENA(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, cliente.getIdCliente());
            cs.setString(2, tipo);
            cs.setInt(3, idObj);
            cs.setInt(4, calif);
            cs.setString(5, coment);
            cs.registerOutParameter(6, OracleTypes.INTEGER);
            cs.executeUpdate();
            final int idNueva = cs.getInt(6);
            lblFeedback.setText("✓ Reseña publicada (id=" + idNueva + ")");
            onLimpiar();
            cargarResenasDelCliente(cliente.getIdCliente());
        } catch (SQLException ex) {
            // Traducir ORA-20001 (objetivo no existe) y ORA-20010 (calificación)
            // a mensajes legibles
            final String msg = ex.getMessage() == null ? "" : ex.getMessage();
            if (msg.contains("ORA-20010")) {
                lblFeedback.setText("⚠ Calificación debe estar entre 1 y 5");
            } else if (msg.contains("ORA-20001")) {
                lblFeedback.setText("⚠ El " + tipo + " con id " + idObj + " no existe");
            } else if (msg.contains("RESENA_ID_CLIENTE_TIPO_OBJETIVO_ID_OBJETIVO_UN")) {
                lblFeedback.setText("⚠ Ya tienes una reseña de ese " + tipo);
            } else {
                lblFeedback.setText("⚠ Error: " + msg.split("\n")[0]);
            }
            LOG.log(Level.WARNING, "Fallo al crear reseña", ex);
        }
    }

    /**
     * Atajo: pone tipo=CANCION y selecciona en el ComboBox la canción que
     * está sonando en el reproductor.
     */
    @FXML
    private void onUsarCancionActual() {
        final PlayerManager.SongInfo info = PlayerManager.getInstance().getSongActual();
        if (info == null || info.cancion() == null || info.cancion().getIdCancion() == null) {
            lblFeedback.setText("⚠ No hay ninguna canción reproduciéndose. Dale play primero.");
            return;
        }
        final int idCancion = info.cancion().getIdCancion();
        cmbTipo.setValue("CANCION");          // dispara cargarObjetivos("CANCION")
        // Seleccionar la canción actual en el ComboBox de objetivos
        cmbObjetivo.getItems().stream()
                .filter(o -> o.id() == idCancion)
                .findFirst()
                .ifPresent(cmbObjetivo::setValue);
        lblFeedback.setText("");
    }

    @FXML
    private void onLimpiar() {
        txtComentario.clear();
        spnCalificacion.getValueFactory().setValue(5);
        cmbTipo.setValue("CANCION");
        cmbObjetivo.setValue(null);
        lblFeedback.setText("");
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------
    @FXML private void onAtras()        { HistorialNavegacion.getInstance().atras(); }
    @FXML private void onAdelante()     { HistorialNavegacion.getInstance().adelante(); }
    @FXML private void onUserMenu()     { UserMenuUtil.mostrar(btnUserMenu); }
    @FXML private void onNotif() { com.beatify.view.util.NotificacionMenuUtil.mostrar(btnNotif, lblNotifCount); }
    @FXML private void onIrInicio() { HistorialNavegacion.getInstance().navegar("/view/home.fxml"); }

    @FXML
    private void onBuscar() {
        final String termino = txtBusqueda == null ? null : txtBusqueda.getText();
        if (termino == null || termino.isBlank()) return;
        SessionContext.getInstance().setTerminoBusqueda(termino);
        HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml");
    }
    @FXML private void onIrExplorar() { HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml"); }
    @FXML private void onIrBiblioteca() { HistorialNavegacion.getInstance().navegar("/view/biblioteca.fxml"); }
    @FXML private void onIrResenas() { /* ya estamos aquí */ }
    @FXML private void onIrBarrio() { HistorialNavegacion.getInstance().navegar("/view/barrio.fxml"); }
    @FXML private void onIrCapsulas() { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros() { HistorialNavegacion.getInstance().navegar("/view/logros.fxml"); }
    @FXML private void onIrPerfil() { HistorialNavegacion.getInstance().navegar("/view/perfil.fxml"); }
    @FXML private void onNuevaPlaylist() { PlaylistUtil.crearNueva(btnUserMenu, () -> { sidebarPlaylistsBox.getChildren().clear(); configurarSidebarPlaylists(); }); }
    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
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
        c.setIdCliente(1);
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        return c;
    }
}