package com.beatify.view.controller;

import com.beatify.dao.ClienteDAO;
import com.beatify.dao.LogroClienteDAO;
import com.beatify.dao.LogroDAO;
import com.beatify.dao.SuscripcionDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Logro;
import com.beatify.model.LogroCliente;
import com.beatify.model.Suscripcion;
import com.beatify.util.Conexion;
import com.beatify.view.SessionContext;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.PlaylistUtil;
import com.beatify.view.util.UserMenuUtil;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PerfilController {

    private static final Logger LOG = Logger.getLogger(PerfilController.class.getName());
    private static final NumberFormat NF = NumberFormat.getInstance(new Locale("es", "CO"));
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es"));

    @FXML private Button    btnNotif;
    @FXML private Button    btnUserMenu;
    @FXML private Label     lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label     lblUserNombre;
    @FXML private VBox      sidebarPlaylistsBox;

    @FXML private StackPane avatarHolder;
    @FXML private Label     lblNombre;
    @FXML private Label     lblUbicacion;
    @FXML private Label     lblEmail;
    @FXML private Label     lblPlanBadge;
    @FXML private Label     lblMiembroDesde;
    @FXML private Button    btnEditarPerfil;

    @FXML private Label lblStatRep, lblStatLikes, lblStatResenas, lblStatSiguiendo, lblStatLogros;
    @FXML private HBox  logrosRecientesBox;
    @FXML private VBox  capsulasBox;
    @FXML private VBox  recienteBox;

    private final ClienteDAO       clienteDAO       = new ClienteDAO();
    private final SuscripcionDAO   suscripcionDAO   = new SuscripcionDAO();
    private final LogroDAO         logroDAO         = new LogroDAO();
    private final LogroClienteDAO  logroClienteDAO  = new LogroClienteDAO();

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarHero(actual);
        configurarStats(actual);
        configurarLogrosRecientes(actual);
        configurarCapsulas(actual);
        configurarReciente(actual);
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
    // Sidebar: playlists del cliente
    // -----------------------------------------------------------------
    private void configurarSidebarPlaylists() {
        sidebarPlaylistsBox.getChildren().clear();
        final Label lbl = new Label("Tus playlists aquí");
        lbl.setStyle("-fx-text-fill: #a7a7a7; -fx-padding: 10;");
        sidebarPlaylistsBox.getChildren().add(lbl);
    }

    // -----------------------------------------------------------------
    // Hero: datos del cliente + plan desde BD
    // -----------------------------------------------------------------
    private void configurarHero(final Cliente c) {
        avatarHolder.getChildren().setAll(
                new ArtistAvatar(120, "#1ed760", "#1ab44e",
                        c.getNombre() + " " + c.getApellido()));
        lblNombre.setText(c.getNombre() + " " + c.getApellido());
        lblUbicacion.setText(c.getCiudad() == null ? "Valledupar" : c.getCiudad());
        lblEmail.setText(c.getCorreo() == null ? "—" : c.getCorreo());

        // Fecha de registro
        if (c.getFechaRegistro() != null) {
            lblMiembroDesde.setText("Miembro desde " + c.getFechaRegistro().format(FMT_FECHA));
        } else {
            lblMiembroDesde.setText("Miembro de Beatify");
        }

        // Plan activo desde SUSCRIPCION
        String plan = "FREE";
        if (c.getIdCliente() != null) {
            try {
                final List<Suscripcion> subs = suscripcionDAO.listar().stream()
                        .filter(s -> c.getIdCliente().equals(s.getIdCliente())
                                  && "ACTIVA".equalsIgnoreCase(s.getEstado()))
                        .toList();
                if (!subs.isEmpty()) plan = subs.get(0).getTipoPlan();
            } catch (ConexionException ex) {
                LOG.log(Level.WARNING, "Error cargando suscripción", ex);
            }
        }
        lblPlanBadge.setText("PLAN: " + plan);
    }

    // -----------------------------------------------------------------
    // Stats reales del cliente
    // -----------------------------------------------------------------
    private void configurarStats(final Cliente c) {
        if (c.getIdCliente() == null) {
            lblStatRep.setText("—"); lblStatLikes.setText("—");
            lblStatResenas.setText("—"); lblStatSiguiendo.setText("—"); lblStatLogros.setText("—");
            return;
        }
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

        // Logros: obtenidos / total
        int totalLogros = 0, obtenidos = 0;
        try {
            totalLogros = logroDAO.listar().size();
            obtenidos   = (int) logroClienteDAO.listar().stream()
                    .filter(lc -> id == (lc.getIdCliente() != null ? lc.getIdCliente() : -1))
                    .count();
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando logros para stats", ex);
        }

        lblStatRep.setText(NF.format(rep));
        lblStatLikes.setText(String.valueOf(likes));
        lblStatResenas.setText(String.valueOf(resenas));
        lblStatSiguiendo.setText(String.valueOf(siguiendo));
        lblStatLogros.setText(obtenidos + " / " + totalLogros);
    }

    // -----------------------------------------------------------------
    // Logros recientes (últimos 3 obtenidos)
    // -----------------------------------------------------------------
    private void configurarLogrosRecientes(final Cliente c) {
        logrosRecientesBox.getChildren().clear();
        if (c.getIdCliente() == null) return;

        try {
            final List<Logro> catalogo = logroDAO.listar();
            final List<LogroCliente> obtenidos = logroClienteDAO.listar().stream()
                    .filter(lc -> c.getIdCliente().equals(lc.getIdCliente()))
                    .limit(3)
                    .collect(Collectors.toList());

            for (final LogroCliente lc : obtenidos) {
                final Logro l = catalogo.stream()
                        .filter(x -> x.getIdLogro().equals(lc.getIdLogro()))
                        .findFirst().orElse(null);
                if (l == null) continue;

                final VBox card = new VBox(4);
                card.setAlignment(Pos.CENTER);
                card.setStyle("-fx-background-color: #282828; -fx-background-radius: 12px;"
                        + " -fx-padding: 12px; -fx-pref-width: 110px;");
                final Label icono = new Label(emojiPorCodigo(l.getCodigo()));
                icono.setStyle("-fx-font-size: 28px;");
                final Label nombre = new Label(l.getNombre());
                nombre.setStyle("-fx-text-fill: white; -fx-font-size: 11px;"
                        + " -fx-wrap-text: true; -fx-alignment: center;");
                nombre.setWrapText(true);
                card.getChildren().addAll(icono, nombre);
                logrosRecientesBox.getChildren().add(card);
            }

            if (obtenidos.isEmpty()) {
                final Label lbl = new Label("Sin logros aún.");
                lbl.setStyle("-fx-text-fill: #a7a7a7; -fx-padding: 8;");
                logrosRecientesBox.getChildren().add(lbl);
            }
        } catch (ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando logros recientes", ex);
        }

        final Button btn = new Button("Ver todos →");
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1ed760; -fx-cursor: hand;");
        btn.setOnAction(e -> HistorialNavegacion.getInstance().navegar("/view/logros.fxml"));
        logrosRecientesBox.getChildren().add(btn);
    }

    // -----------------------------------------------------------------
    // Cápsulas del tiempo (etiquetas de periodos con datos reales)
    // -----------------------------------------------------------------
    private void configurarCapsulas(final Cliente c) {
        capsulasBox.getChildren().clear();
        if (c.getIdCliente() == null) return;

        final String sql = """
                SELECT TO_CHAR(fecha_hora, 'YYYY-MM') periodo, COUNT(*) reproducciones
                  FROM REPRODUCCION
                 WHERE CLIENTE_id_cliente = ?
                 GROUP BY TO_CHAR(fecha_hora, 'YYYY-MM')
                 ORDER BY periodo DESC
                 FETCH FIRST 3 ROWS ONLY""";

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            try (ResultSet rs = ps.executeQuery()) {
                boolean alguno = false;
                while (rs.next()) {
                    final Label lbl = new Label(rs.getString(1)
                            + "  —  " + rs.getInt(2) + " reproducciones");
                    lbl.setStyle("-fx-text-fill: #a7a7a7; -fx-padding: 8;");
                    capsulasBox.getChildren().add(lbl);
                    alguno = true;
                }
                if (!alguno) {
                    final Label lbl = new Label("Sin historial aún.");
                    lbl.setStyle("-fx-text-fill: #a7a7a7; -fx-padding: 8;");
                    capsulasBox.getChildren().add(lbl);
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando cápsulas", ex);
        }

        final Button btn = new Button("Ver todas →");
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1ed760; -fx-cursor: hand;");
        btn.setOnAction(e -> HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"));
        capsulasBox.getChildren().add(btn);
    }

    // -----------------------------------------------------------------
    // Escuchado recientemente (últimas 5 canciones)
    // -----------------------------------------------------------------
    private void configurarReciente(final Cliente c) {
        recienteBox.getChildren().clear();
        if (c.getIdCliente() == null) return;

        final String sql = """
                SELECT cc.titulo, a.nombre_artistico
                  FROM REPRODUCCION r
                  JOIN CANCION cc ON cc.id_cancion = r.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album = cc.ALBUM_id_album
                  JOIN ARTISTA a  ON a.id_artista = al.ARTISTA_id_artista
                 WHERE r.CLIENTE_id_cliente = ?
                 ORDER BY r.fecha_hora DESC
                 FETCH FIRST 5 ROWS ONLY""";

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getIdCliente());
            try (ResultSet rs = ps.executeQuery()) {
                boolean alguno = false;
                while (rs.next()) {
                    final HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 8;");
                    final FontIcon ic = new FontIcon("bi-music-note-beamed");
                    ic.setIconSize(14);
                    ic.setIconColor(javafx.scene.paint.Color.web("#1ed760"));
                    final Label titulo  = new Label(rs.getString(1));
                    titulo.setStyle("-fx-text-fill: white;");
                    final Label artista = new Label(rs.getString(2));
                    artista.setStyle("-fx-text-fill: #a7a7a7;");
                    row.getChildren().addAll(ic, titulo, artista);
                    recienteBox.getChildren().add(row);
                    alguno = true;
                }
                if (!alguno) {
                    final Label lbl = new Label("Sin reproducciones recientes.");
                    lbl.setStyle("-fx-text-fill: #a7a7a7; -fx-padding: 8;");
                    recienteBox.getChildren().add(lbl);
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error cargando reciente", ex);
        }
    }

    // -----------------------------------------------------------------
    // Navegación
    // -----------------------------------------------------------------
    @FXML private void onAtras()        { HistorialNavegacion.getInstance().atras(); }
    @FXML private void onAdelante()     { HistorialNavegacion.getInstance().adelante(); }
    @FXML private void onUserMenu()     { UserMenuUtil.mostrar(btnUserMenu); }
    @FXML private void onNotif()        { com.beatify.view.util.NotificacionMenuUtil.mostrar(btnNotif, lblNotifCount); }
    @FXML private void onIrInicio()     { HistorialNavegacion.getInstance().navegar("/view/home.fxml"); }
    @FXML private void onIrExplorar()   { HistorialNavegacion.getInstance().navegar("/view/catalogo.fxml"); }
    @FXML private void onIrBiblioteca() { HistorialNavegacion.getInstance().navegar("/view/biblioteca.fxml"); }
    @FXML private void onIrResenas()    { HistorialNavegacion.getInstance().navegar("/view/resenas.fxml"); }
    @FXML private void onIrBarrio()     { HistorialNavegacion.getInstance().navegar("/view/barrio.fxml"); }
    @FXML private void onIrCapsulas()   { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros()     { HistorialNavegacion.getInstance().navegar("/view/logros.fxml"); }
    @FXML private void onIrPerfil()     { /* ya estamos */ }
    @FXML private void onNuevaPlaylist() { PlaylistUtil.crearNueva(btnUserMenu, () -> { sidebarPlaylistsBox.getChildren().clear(); configurarSidebarPlaylists(); }); }
    @FXML
    private void onEditarPerfil() {
        final Cliente c = SessionContext.getInstance().getClienteActual();
        if (c == null) return;

        // ---- Campos del formulario ----
        final TextField txtNombre   = new TextField(c.getNombre()   == null ? "" : c.getNombre());
        final TextField txtApellido = new TextField(c.getApellido() == null ? "" : c.getApellido());
        final TextField txtTelefono = new TextField(c.getTelefono() == null ? "" : c.getTelefono());
        final TextField txtCiudad   = new TextField(c.getCiudad()   == null ? "" : c.getCiudad());

        txtNombre.setPrefWidth(220);
        txtApellido.setPrefWidth(220);

        // ---- Botones ----
        final Button btnAceptar  = new Button("Guardar");
        btnAceptar.getStyleClass().add("bf-btn-primary");
        final Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("bf-btn-ghost");
        final Runnable validar = () -> btnAceptar.setDisable(
                txtNombre.getText().isBlank() || txtApellido.getText().isBlank());
        validar.run();
        txtNombre.textProperty().addListener((obs, o, v) -> validar.run());
        txtApellido.textProperty().addListener((obs, o, v) -> validar.run());

        final javafx.scene.layout.HBox acciones =
                new javafx.scene.layout.HBox(12, btnCancelar, btnAceptar);
        acciones.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        // ---- Tarjeta (overlay in-app) ----
        final Label titulo = new Label("Editar perfil");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        final Label sub = new Label("Actualiza tus datos personales");
        sub.setStyle("-fx-text-fill: #b3b3b3;");

        final javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(12,
                titulo, sub,
                etiquetaForm("Nombre"),   txtNombre,
                etiquetaForm("Apellido"), txtApellido,
                etiquetaForm("Teléfono"), txtTelefono,
                etiquetaForm("Ciudad"),   txtCiudad,
                acciones);
        card.setPadding(new Insets(28));
        card.setMaxWidth(440);
        card.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        card.setStyle("-fx-background-color: #181818; -fx-background-radius: 16px;"
                + " -fx-border-color: #2a2a2a; -fx-border-radius: 16px;");

        final Runnable cerrar = com.beatify.view.util.OverlayUtil.mostrar(btnUserMenu, card);
        btnCancelar.setOnAction(e -> cerrar.run());

        btnAceptar.setOnAction(e -> {
            c.setNombre(txtNombre.getText().trim());
            c.setApellido(txtApellido.getText().trim());
            c.setTelefono(txtTelefono.getText().isBlank() ? null : txtTelefono.getText().trim());
            c.setCiudad(txtCiudad.getText().isBlank() ? null : txtCiudad.getText().trim());

            try {
                clienteDAO.actualizar(c);
                SessionContext.getInstance().setClienteActual(c);
                cerrar.run();
                // Refrescar los labels del hero sin recargar la pantalla completa
                configurarHero(c);
                configurarTopBar(c);
            } catch (final ConexionException ex) {
                LOG.log(Level.WARNING, "Error al actualizar perfil", ex);
                final Alert alert = new Alert(Alert.AlertType.ERROR,
                        "No se pudo guardar. Verifica la conexión.", ButtonType.OK);
                alert.setTitle("Error al guardar");
                alert.showAndWait();
            }
        });
    }

    private static Label etiquetaForm(final String texto) {
        final Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        return l;
    }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        HistorialNavegacion.getInstance().reset();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helper BD
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

    private static String emojiPorCodigo(final String codigo) {
        return switch (codigo == null ? "" : codigo.toUpperCase()) {
            case "PRIMER_LIKE"   -> "❤";
            case "CRITICO"       -> "✍️";
            case "COLECCIONISTA" -> "📚";
            case "NOCTAMBULO"    -> "🌙";
            case "EXP_CARIBE"    -> "🌊";
            case "FAN_VALLENATO" -> "🎵";
            default              -> "🏆";
        };
    }

    private static Cliente clientePlaceholder() {
        final Cliente c = new Cliente();
        c.setIdCliente(1);
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        c.setCorreo("yjosue@unicesar.edu.co");
        return c;
    }
}
