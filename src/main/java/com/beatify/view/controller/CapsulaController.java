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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller de Cápsulas del Tiempo (capsulas.fxml) — cableado a BD.
 *
 * Funcionalidad distintiva #3 de Beatify.
 *
 * Datos calculados sobre REPRODUCCION del cliente activo dentro de un
 * rango de fechas. Permite navegar por la timeline (5 nodos predefinidos
 * desde SYSDATE) o usar el ComboBox de presets / rango personalizado.
 */
public class CapsulaController {

    private static final Logger LOG = Logger.getLogger(CapsulaController.class.getName());
    private static final DateTimeFormatter FMT_MES = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es"));

    // ---- TopBar ----
    @FXML private Button btnNotif;
    @FXML private Button btnUserMenu;
    @FXML private Label lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;
    @FXML private TextField txtBusqueda;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Controles de rango ----
    @FXML private ComboBox<String> cmbPreset;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    @FXML private Button btnGenerar;

    // ---- Timeline ----
    @FXML private HBox timelineBox;

    // ---- Detalle ----
    @FXML private VBox detalleCapsule;
    @FXML private StackPane heroCoverHolder;
    @FXML private Label lblHeroTitulo;
    @FXML private Label lblHeroDesc;
    @FXML private Label lblHeroReproducciones;

    // ---- Stat cards ----
    @FXML private Label lblTopArtistaNombre;
    @FXML private Label lblTopArtistaPct;
    @FXML private Label lblTopCancionNombre;
    @FXML private Label lblTopCancionPlays;
    @FXML private Label lblGeneroNombre;
    @FXML private Label lblNuevosArtistas;

    // ---- Tracklist ----
    @FXML private VBox tracklistCapsula;

    private int capsuleSeleccionada = 0;

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }
        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarControlesRango();
        construirTimeline();
        mostrarDetalleCapsule(0);
    }

    // -----------------------------------------------------------------
    // TopBar / Sidebar (idénticos a las otras pantallas)
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
    // Controles de rango
    // -----------------------------------------------------------------
    private void configurarControlesRango() {
        cmbPreset.getItems().addAll(
                "Último mes", "Últimos 3 meses", "Últimos 6 meses",
                "Último año", "Rango personalizado");
        cmbPreset.setValue("Últimos 6 meses");
        dpDesde.setValue(LocalDate.now().minusMonths(6));
        dpHasta.setValue(LocalDate.now());
        dpDesde.setVisible(false); dpDesde.setManaged(false);
        dpHasta.setVisible(false); dpHasta.setManaged(false);
        cmbPreset.setOnAction(e -> {
            boolean custom = "Rango personalizado".equals(cmbPreset.getValue());
            dpDesde.setVisible(custom); dpDesde.setManaged(custom);
            dpHasta.setVisible(custom); dpHasta.setManaged(custom);
        });
    }

    // -----------------------------------------------------------------
    // Timeline dinámica
    // -----------------------------------------------------------------
    private void construirTimeline() {
        timelineBox.getChildren().clear();
        final LocalDate hoy = LocalDate.now();
        final String[][] nodos = {
                {hoy.format(FMT_MES), "Este mes"},
                {hoy.minusMonths(1).format(FMT_MES), "Hace 1 mes"},
                {hoy.minusMonths(3).format(FMT_MES), "Hace 3 meses"},
                {hoy.minusMonths(6).format(FMT_MES), "Hace 6 meses"},
                {hoy.minusYears(1).format(FMT_MES), "Hace 1 año"},
        };
        for (int i = 0; i < nodos.length; i++) {
            final int idx = i;
            final String[] n = nodos[i];
            if (i > 0) {
                final Region linea = new Region();
                linea.setStyle("-fx-background-color: -bf-border; -fx-pref-width: 48px; -fx-pref-height: 2px;");
                timelineBox.getChildren().add(linea);
            }
            final VBox nodo = new VBox(6);
            nodo.setAlignment(Pos.CENTER);
            nodo.setStyle("-fx-padding: 10px; -fx-cursor: hand;");
            if (idx == capsuleSeleccionada) {
                nodo.setStyle(nodo.getStyle()
                        + "-fx-background-color: rgba(30, 215, 96, 0.1); -fx-background-radius: 12px;");
            }
            final StackPane dot = new StackPane();
            dot.setPrefSize(14, 14);
            dot.setStyle(idx == capsuleSeleccionada
                    ? "-fx-background-color: #1ed760; -fx-background-radius: 999px;"
                    : "-fx-background-color: -bf-border; -fx-background-radius: 999px;");
            final Label lblFecha = new Label(n[0]);
            lblFecha.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 12px; -fx-text-fill: -bf-text-muted;");
            final Label lblSub = new Label(n[1]);
            lblSub.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 10px; -fx-text-fill: -bf-text-dim;");
            nodo.getChildren().addAll(dot, lblFecha, lblSub);
            nodo.setOnMouseClicked(e -> {
                capsuleSeleccionada = idx;
                construirTimeline();
                mostrarDetalleCapsule(idx);
            });
            timelineBox.getChildren().add(nodo);
        }
    }

    // -----------------------------------------------------------------
    // Detalle de la cápsula (consultas a BD)
    // -----------------------------------------------------------------
    private void mostrarDetalleCapsule(final int idx) {
        final Cliente cliente = SessionContext.getInstance().getClienteActual();
        if (cliente == null || cliente.getIdCliente() == null) return;

        final LocalDateTime[] rango = rangoDeNodo(idx);
        final LocalDateTime desde = rango[0];
        final LocalDateTime hasta = rango[1];

        // Hero
        lblHeroTitulo.setText(desde.toLocalDate().format(FMT_MES));
        lblHeroDesc.setText("Tu actividad musical entre "
                + desde.toLocalDate() + " y " + hasta.toLocalDate());
        final String[][] gradientes = {
                {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"}, {"#d4a017", "#2a1a05"},
                {"#a83232", "#3a0a0a"}, {"#7a3a8a", "#1a052a"}
        };
        final String[] grad = gradientes[idx % gradientes.length];
        heroCoverHolder.getChildren().setAll(
                new AlbumCover(120, grad[0], grad[1], desde.toLocalDate().format(FMT_MES)));

        // Total reproducciones
        final int total = contar(cliente.getIdCliente(), desde, hasta,
                "SELECT COUNT(*) FROM REPRODUCCION WHERE CLIENTE_id_cliente = ? "
                        + "AND fecha_hora BETWEEN ? AND ?");
        lblHeroReproducciones.setText(String.valueOf(total));

        // Top artista
        final String[] topArt = topTexto(cliente.getIdCliente(), desde, hasta, """
                SELECT a.nombre_artistico, COUNT(*) cnt
                  FROM REPRODUCCION r
                  JOIN CANCION c  ON c.id_cancion = r.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album = c.ALBUM_id_album
                  JOIN ARTISTA a  ON a.id_artista = al.ARTISTA_id_artista
                 WHERE r.CLIENTE_id_cliente = ? AND r.fecha_hora BETWEEN ? AND ?
                 GROUP BY a.nombre_artistico
                 ORDER BY cnt DESC FETCH FIRST 1 ROWS ONLY""");
        lblTopArtistaNombre.setText(topArt[0] == null ? "—" : topArt[0]);
        lblTopArtistaPct.setText(total == 0 || topArt[1] == null
                ? "—" : String.format("%.0f%%", 100.0 * Integer.parseInt(topArt[1]) / total));

        // Top canción
        final String[] topCanc = topTexto(cliente.getIdCliente(), desde, hasta, """
                SELECT c.titulo, COUNT(*) cnt
                  FROM REPRODUCCION r
                  JOIN CANCION c ON c.id_cancion = r.CANCION_id_cancion
                 WHERE r.CLIENTE_id_cliente = ? AND r.fecha_hora BETWEEN ? AND ?
                 GROUP BY c.titulo
                 ORDER BY cnt DESC FETCH FIRST 1 ROWS ONLY""");
        lblTopCancionNombre.setText(topCanc[0] == null ? "—" : topCanc[0]);
        lblTopCancionPlays.setText(topCanc[1] == null ? "—" : topCanc[1] + " plays");

        // Top género
        final String[] topGen = topTexto(cliente.getIdCliente(), desde, hasta, """
                SELECT g.nombre, COUNT(*) cnt
                  FROM REPRODUCCION r
                  JOIN CANCION c  ON c.id_cancion = r.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album = c.ALBUM_id_album
                  JOIN ARTISTA_GENERO ag ON ag.ARTISTA_id_artista = al.ARTISTA_id_artista
                  JOIN GENERO g   ON g.id_genero = ag.GENERO_id_genero
                 WHERE r.CLIENTE_id_cliente = ? AND r.fecha_hora BETWEEN ? AND ?
                 GROUP BY g.nombre
                 ORDER BY cnt DESC FETCH FIRST 1 ROWS ONLY""");
        lblGeneroNombre.setText(topGen[0] == null ? "—" : topGen[0]);

        // Nuevos artistas (que NO aparecían antes de 'desde')
        final int nuevos = contarNuevos(cliente.getIdCliente(), desde, hasta);
        lblNuevosArtistas.setText(nuevos + " artistas");

        cargarTracklist(cliente.getIdCliente(), desde, hasta);
    }

    private LocalDateTime[] rangoDeNodo(final int idx) {
        final LocalDateTime hasta = LocalDateTime.now();
        final LocalDateTime desde = switch (idx) {
            case 0 -> hasta.minusMonths(1);
            case 1 -> hasta.minusMonths(2).withDayOfMonth(1);
            case 2 -> hasta.minusMonths(4).withDayOfMonth(1);
            case 3 -> hasta.minusMonths(7).withDayOfMonth(1);
            case 4 -> hasta.minusYears(1).withDayOfMonth(1);
            default -> hasta.minusMonths(6);
        };
        return new LocalDateTime[]{desde, hasta};
    }

    // -----------------------------------------------------------------
    // Tracklist (TOP 4 canciones del rango)
    // -----------------------------------------------------------------
    private void cargarTracklist(final Integer idCliente,
                                 final LocalDateTime desde, final LocalDateTime hasta) {
        tracklistCapsula.getChildren().clear();
        final String sql = """
                SELECT c.titulo, a.nombre_artistico, COUNT(*) cnt
                  FROM REPRODUCCION r
                  JOIN CANCION c  ON c.id_cancion = r.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album = c.ALBUM_id_album
                  JOIN ARTISTA a  ON a.id_artista = al.ARTISTA_id_artista
                 WHERE r.CLIENTE_id_cliente = ? AND r.fecha_hora BETWEEN ? AND ?
                 GROUP BY c.titulo, a.nombre_artistico
                 ORDER BY cnt DESC FETCH FIRST 4 ROWS ONLY""";
        final String[][] colores = {
                {"#c97a1f", "#3a1a05"}, {"#1f7a5a", "#072a1a"},
                {"#a83232", "#3a0a0a"}, {"#d4a017", "#2a1a05"}};
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                int pos = 1;
                while (rs.next()) {
                    final String[] c = colores[(pos - 1) % colores.length];
                    final String[] t = {rs.getString(1), rs.getString(2), String.valueOf(rs.getInt(3)), c[0], c[1]};
                    tracklistCapsula.getChildren().add(construirFila(pos++, t));
                }
                if (pos == 1) {
                    tracklistCapsula.getChildren().add(
                            crearLabel("Sin reproducciones en este rango. Prueba ampliar el periodo.", "bf-field-lbl"));
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error tracklist", ex);
            tracklistCapsula.getChildren().add(crearLabel("Error consultando BD", "bf-field-lbl"));
        }
    }

    private HBox construirFila(final int pos, final String[] t) {
        final HBox row = new HBox(12);
        row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 12 16; -fx-background-color: -bf-border;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 12 16; -fx-background-color: transparent;"));
        final Label lblPos = new Label(String.valueOf(pos));
        lblPos.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 13px; -fx-text-fill: -bf-text-dim;");
        lblPos.setMinWidth(30);
        final AlbumCover cover = new AlbumCover(36, t[3], t[4]);
        final VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        final Label lblTit = new Label(t[0]);
        lblTit.setStyle("-fx-font-family: 'Manrope SemiBold'; -fx-font-size: 14px; -fx-text-fill: -bf-text;");
        final Label lblArt = new Label(t[1]);
        lblArt.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 12px; -fx-text-fill: -bf-text-dim;");
        info.getChildren().addAll(lblTit, lblArt);
        final Label lblPlays = new Label(t[2] + " plays");
        lblPlays.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 12px; -fx-text-fill: -bf-text-muted;");
        row.getChildren().addAll(lblPos, cover, info, lblPlays);
        return row;
    }

    // -----------------------------------------------------------------
    // Helpers de BD
    // -----------------------------------------------------------------
    private int contar(final Integer idCliente,
                       final LocalDateTime desde, final LocalDateTime hasta,
                       final String sql) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error contar", ex);
        }
        return 0;
    }

    /** Devuelve [texto, cnt] del top 1; ambos null si no hay datos. */
    private String[] topTexto(final Integer idCliente,
                              final LocalDateTime desde, final LocalDateTime hasta,
                              final String sql) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString(1), String.valueOf(rs.getInt(2))};
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error topTexto", ex);
        }
        return new String[]{null, null};
    }

    private int contarNuevos(final Integer idCliente,
                             final LocalDateTime desde, final LocalDateTime hasta) {
        final String sql = """
                SELECT COUNT(DISTINCT al.ARTISTA_id_artista)
                  FROM REPRODUCCION r
                  JOIN CANCION c  ON c.id_cancion = r.CANCION_id_cancion
                  JOIN ALBUM al   ON al.id_album = c.ALBUM_id_album
                 WHERE r.CLIENTE_id_cliente = ?
                   AND r.fecha_hora BETWEEN ? AND ?
                   AND NOT EXISTS (
                       SELECT 1 FROM REPRODUCCION r2
                         JOIN CANCION c2 ON c2.id_cancion = r2.CANCION_id_cancion
                         JOIN ALBUM al2  ON al2.id_album = c2.ALBUM_id_album
                        WHERE al2.ARTISTA_id_artista = al.ARTISTA_id_artista
                          AND r2.CLIENTE_id_cliente = ?
                          AND r2.fecha_hora < ?)""";
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setTimestamp(2, Timestamp.valueOf(desde));
            ps.setTimestamp(3, Timestamp.valueOf(hasta));
            ps.setInt(4, idCliente);
            ps.setTimestamp(5, Timestamp.valueOf(desde));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error nuevos artistas", ex);
        }
        return 0;
    }

    // -----------------------------------------------------------------
    // Botón Generar
    // -----------------------------------------------------------------
    @FXML
    private void onGenerar() {
        // Mapea preset al nodo más cercano del timeline
        capsuleSeleccionada = switch (cmbPreset.getValue()) {
            case "Último mes" -> 0;
            case "Últimos 3 meses" -> 2;
            case "Últimos 6 meses" -> 3;
            case "Último año" -> 4;
            default -> 1;
        };
        construirTimeline();
        mostrarDetalleCapsule(capsuleSeleccionada);
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
    @FXML private void onIrResenas() { HistorialNavegacion.getInstance().navegar("/view/resenas.fxml"); }
    @FXML private void onIrBarrio() { HistorialNavegacion.getInstance().navegar("/view/barrio.fxml"); }
    @FXML private void onIrCapsulas() { /* ya estamos aquí */ }
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