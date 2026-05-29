package com.beatify.view.controller;

import com.beatify.dao.ResenaDAO;
import com.beatify.model.Cliente;
import com.beatify.model.Resena;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
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

    // ---- Form nueva reseña ----
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtIdObjetivo;
    @FXML private Spinner<Integer> spnCalificacion;
    @FXML private TextArea txtComentario;
    @FXML private Label lblFeedback;

    private final ResenaDAO resenaDAO = new ResenaDAO();
    private String filtroActivo = "TODAS";
    private List<Resena> todasMisResenas;

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
    // Form y filtros
    // -----------------------------------------------------------------
    private void configurarForm() {
        cmbTipo.getItems().addAll("CANCION", "ALBUM", "ARTISTA");
        cmbTipo.setValue("CANCION");
        spnCalificacion.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 5));
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

    private VBox construirCard(final Resena r) {
        final VBox card = new VBox(8);
        card.getStyleClass().add("bf-card");

        // Header: tipo · id  +  estrellas
        final HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        final Label tipo = new Label(r.getTipoObjetivo() + " · id " + r.getIdObjetivo());
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
        final String idTxt = txtIdObjetivo.getText() == null ? "" : txtIdObjetivo.getText().trim();
        if (idTxt.isEmpty()) {
            lblFeedback.setText("⚠ Ingresa el ID del objetivo");
            return;
        }
        final Integer idObj;
        try {
            idObj = Integer.valueOf(idTxt);
        } catch (NumberFormatException ex) {
            lblFeedback.setText("⚠ ID debe ser numérico");
            return;
        }
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

    @FXML
    private void onLimpiar() {
        txtIdObjetivo.clear();
        txtComentario.clear();
        spnCalificacion.getValueFactory().setValue(5);
        cmbTipo.setValue("CANCION");
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
    @FXML private void onIrResenas() { /* ya estamos aquí */ }
    @FXML private void onIrBarrio() { NavegacionUtil.cambiarA("/view/barrio.fxml", btnUserMenu); }
    @FXML private void onIrCapsulas() { NavegacionUtil.cambiarA("/view/capsulas.fxml", btnUserMenu); }
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
        c.setIdCliente(1);
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        return c;
    }
}