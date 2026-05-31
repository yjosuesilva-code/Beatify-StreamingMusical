package com.beatify.view.controller;

import com.beatify.dao.LogroClienteDAO;
import com.beatify.dao.LogroDAO;
import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.model.Logro;
import com.beatify.model.LogroCliente;
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
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controller de Logros (logros.fxml) — cableado a BD real.
 *
 * Funcionalidad distintiva #2 de Beatify.
 *
 * Fuentes de datos:
 *   - LogroDAO.listar() para el catalogo de logros.
 *   - LogroClienteDAO.listar() filtrado por idCliente para los obtenidos.
 *   - Queries inline (mismas reglas que PKG_LOGROS.EVALUAR_LOGROS_AUTO)
 *     para calcular el progreso de los logros pendientes.
 *
 * Rareza: derivada de puntos del logro (no existe columna 'rareza').
 *   <100: comun · <200: raro · <300: epico · >=300: legendario · null: comun.
 */
public class LogrosController {

    private static final Logger LOG = Logger.getLogger(LogrosController.class.getName());
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ---- TopBar ----
    @FXML private Button btnNotif;
    @FXML private Button btnUserMenu;
    @FXML private Label lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;
    @FXML private TextField txtBusqueda;

    // ---- Sidebar ----
    @FXML private VBox sidebarPlaylistsBox;

    // ---- Resumen ----
    @FXML private Label lblProgreso;
    @FXML private Label lblProgresoSub;
    @FXML private ProgressBar progresoBar;
    @FXML private Label lblRarezas;
    @FXML private Label lblRarezasSub;

    // ---- Filtros ----
    @FXML private ToggleButton tabTodos;
    @FXML private ToggleButton tabObtenidos;
    @FXML private ToggleButton tabPendientes;

    // ---- Grid ----
    @FXML private FlowPane logrosGrid;

    private final LogroDAO logroDAO = new LogroDAO();
    private final LogroClienteDAO logroClienteDAO = new LogroClienteDAO();

    private String filtroActivo = "todos";
    private List<Logro> catalogo;
    private List<LogroCliente> obtenidosCliente;

    @FXML
    private void initialize() {
        Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null) {
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }
        configurarTopBar(actual);
        configurarSidebarPlaylists();
        configurarFiltros();
        cargarDatos(actual);
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
    // Filtros
    // -----------------------------------------------------------------
    private void configurarFiltros() {
        tabTodos.setSelected(true);
        tabTodos.setOnAction(e -> { filtroActivo = "todos"; pintarGrid(); });
        tabObtenidos.setOnAction(e -> { filtroActivo = "obtenidos"; pintarGrid(); });
        tabPendientes.setOnAction(e -> { filtroActivo = "pendientes"; pintarGrid(); });
    }

    // -----------------------------------------------------------------
    // Carga desde BD
    // -----------------------------------------------------------------
    private void cargarDatos(final Cliente cliente) {
        try {
            catalogo = logroDAO.listar();
            obtenidosCliente = logroClienteDAO.listar().stream()
                    .filter(lc -> cliente.getIdCliente() != null
                            && cliente.getIdCliente().equals(lc.getIdCliente()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error cargando logros", ex);
            catalogo = List.of();
            obtenidosCliente = List.of();
        }
        actualizarResumen();
        pintarGrid();
    }

    private void actualizarResumen() {
        final int total = catalogo.size();
        final int obten = obtenidosCliente.size();
        lblProgreso.setText(obten + " / " + total);
        lblProgresoSub.setText("logros obtenidos");
        progresoBar.setProgress(total == 0 ? 0 : (double) obten / total);

        // Rarezas obtenidas
        long raros = 0, epicos = 0, legendarios = 0;
        for (final LogroCliente lc : obtenidosCliente) {
            final Logro l = findLogro(lc.getIdLogro());
            if (l == null) continue;
            switch (rarezaDe(l)) {
                case "raro": raros++; break;
                case "epico": epicos++; break;
                case "legendario": legendarios++; break;
                default: break;
            }
        }
        if (obten == 0) {
            lblRarezas.setText("—");
            lblRarezasSub.setText("Sin logros aún");
        } else {
            final StringBuilder sb = new StringBuilder();
            if (legendarios > 0) sb.append(legendarios).append(" legendario").append(legendarios > 1 ? "s" : "");
            if (epicos > 0) { if (sb.length() > 0) sb.append(" · "); sb.append(epicos).append(" épico").append(epicos > 1 ? "s" : ""); }
            if (raros > 0) { if (sb.length() > 0) sb.append(" · "); sb.append(raros).append(" raro").append(raros > 1 ? "s" : ""); }
            if (sb.length() == 0) sb.append(obten).append(" comune").append(obten > 1 ? "s" : "");
            lblRarezas.setText(sb.toString());
            lblRarezasSub.setText("logros raros en tu colección");
        }
    }

    private void pintarGrid() {
        logrosGrid.getChildren().clear();
        if (catalogo.isEmpty()) {
            logrosGrid.getChildren().add(crearLabel("Catálogo vacío. Ejecuta 02_seed_data.sql.", "bf-field-lbl"));
            return;
        }
        final Cliente cliente = SessionContext.getInstance().getClienteActual();
        int impresos = 0;
        for (final Logro l : catalogo) {
            final LogroCliente obt = findObtenido(l.getIdLogro());
            final boolean obtenido = obt != null;
            if ("obtenidos".equals(filtroActivo) && !obtenido) continue;
            if ("pendientes".equals(filtroActivo) && obtenido) continue;
            logrosGrid.getChildren().add(construirTarjeta(l, obtenido, obt, cliente));
            impresos++;
        }
        if (impresos == 0) {
            logrosGrid.getChildren().add(crearLabel("Sin logros en este filtro.", "bf-field-lbl"));
        }
    }

    // -----------------------------------------------------------------
    // Tarjeta individual
    // -----------------------------------------------------------------
    private VBox construirTarjeta(final Logro l, final boolean obtenido,
                                  final LogroCliente lc, final Cliente cliente) {
        final String rareza = rarezaDe(l);
        final String colorRar = colorRareza(rareza);
        final String emoji = emojiPorCodigo(l.getCodigo());

        final VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: -bf-bg-2; -fx-background-radius: 16px; -fx-padding: 20px; -fx-pref-width: 280px; -fx-border-color: " + colorRar + "; -fx-border-width: 1px; -fx-border-radius: 16px;"
                + (obtenido ? "" : " -fx-opacity: 0.7;"));

        // Icono
        final StackPane iconHolder = new StackPane();
        iconHolder.setPrefSize(80, 80);
        iconHolder.setStyle("-fx-background-color: " + colorRar + "20; -fx-background-radius: 999px;");
        final Label lblIcono = new Label(emoji);
        lblIcono.setStyle("-fx-font-size: 40px;");
        iconHolder.getChildren().add(lblIcono);

        // Rareza badge
        final Label badgeRareza = new Label(capitalizar(rareza));
        badgeRareza.setStyle("-fx-background-color: " + colorRar + "; -fx-text-fill: white; -fx-padding: 4px 12px; -fx-background-radius: 999px; -fx-font-family: 'JetBrains Mono'; -fx-font-size: 10px;");

        // Nombre + descripción
        final Label lblNombre = new Label(l.getNombre());
        lblNombre.setStyle("-fx-font-family: 'Manrope Bold'; -fx-font-size: 16px; -fx-text-fill: -bf-text; -fx-wrap-text: true; -fx-alignment: center;");
        lblNombre.setWrapText(true);
        lblNombre.setAlignment(Pos.CENTER);

        final Label lblDesc = new Label(l.getDescripcion() == null ? "" : l.getDescripcion());
        lblDesc.setStyle("-fx-font-family: 'Manrope Regular'; -fx-font-size: 12px; -fx-text-fill: -bf-text-muted; -fx-wrap-text: true; -fx-alignment: center;");
        lblDesc.setWrapText(true);
        lblDesc.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconHolder, badgeRareza, lblNombre, lblDesc);

        // Footer: fecha o progreso
        if (obtenido && lc != null) {
            final String fechaTxt = lc.getFechaObtencion() != null
                    ? lc.getFechaObtencion().format(FECHA_FMT) : "—";
            final Label lblFecha = new Label("✓ Obtenido el " + fechaTxt);
            lblFecha.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 10px; -fx-text-fill: #7ed957; -fx-padding: 8px 0 0 0;");
            card.getChildren().add(lblFecha);
        } else {
            final int[] prog = calcularProgreso(l.getCodigo(),
                    cliente == null ? null : cliente.getIdCliente());
            final ProgressBar pb = new ProgressBar(prog[1] == 0 ? 0 : (double) prog[0] / prog[1]);
            pb.setStyle("-fx-background-color: -bf-border; -fx-background-radius: 4px; -fx-pref-width: 200px;");
            pb.setMaxWidth(Double.MAX_VALUE);
            final Label lblFrac = new Label(prog[0] + " / " + prog[1]);
            lblFrac.setStyle("-fx-font-family: 'JetBrains Mono'; -fx-font-size: 11px; -fx-text-fill: -bf-text-dim;");
            card.getChildren().addAll(pb, lblFrac);
        }
        return card;
    }

    // -----------------------------------------------------------------
    // Reglas de progreso (mismas que PKG_LOGROS.EVALUAR_LOGROS_AUTO)
    // -----------------------------------------------------------------
    private int[] calcularProgreso(final String codigo, final Integer idCliente) {
        if (idCliente == null) return new int[]{0, 1};
        return switch (codigo == null ? "" : codigo.toUpperCase()) {
            case "PRIMER_LIKE" -> contar(idCliente, """
                    SELECT (SELECT COUNT(*) FROM LIKE_CANCION  WHERE CLIENTE_id_cliente = ?)
                         + (SELECT COUNT(*) FROM LIKE_ALBUM    WHERE CLIENTE_id_cliente = ?)
                         + (SELECT COUNT(*) FROM LIKE_PLAYLIST WHERE CLIENTE_id_cliente = ?)
                      FROM dual""", 3, 1);
            case "CRITICO" -> contar(idCliente,
                    "SELECT COUNT(*) FROM RESENA WHERE CLIENTE_id_cliente = ?", 1, 5);
            case "COLECCIONISTA" -> contar(idCliente,
                    "SELECT COUNT(*) FROM PLAYLIST WHERE CLIENTE_id_cliente = ?", 1, 3);
            case "NOCTAMBULO" -> contar(idCliente, """
                    SELECT COUNT(*) FROM REPRODUCCION
                     WHERE CLIENTE_id_cliente = ?
                       AND EXTRACT(HOUR FROM fecha_hora) BETWEEN 0 AND 4""", 1, 3);
            case "EXP_CARIBE" -> contar(idCliente, """
                    SELECT COUNT(DISTINCT al.ARTISTA_id_artista)
                      FROM REPRODUCCION r
                      JOIN CANCION c  ON c.id_cancion = r.CANCION_id_cancion
                      JOIN ALBUM al   ON al.id_album = c.ALBUM_id_album
                      JOIN ARTISTA a  ON a.id_artista = al.ARTISTA_id_artista
                     WHERE r.CLIENTE_id_cliente = ? AND UPPER(a.pais) = 'COLOMBIA'""", 1, 3);
            case "FAN_VALLENATO" -> contar(idCliente, """
                    SELECT COUNT(*)
                      FROM REPRODUCCION r
                      JOIN CANCION c  ON c.id_cancion = r.CANCION_id_cancion
                      JOIN ALBUM al   ON al.id_album = c.ALBUM_id_album
                      JOIN ARTISTA_GENERO ag ON ag.ARTISTA_id_artista = al.ARTISTA_id_artista
                      JOIN GENERO g   ON g.id_genero = ag.GENERO_id_genero
                     WHERE r.CLIENTE_id_cliente = ? AND UPPER(g.nombre) = 'VALLENATO'""", 1, 5);
            default -> new int[]{0, 1};
        };
    }

    private int[] contar(final Integer idCliente, final String sql, final int reps, final int target) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= reps; i++) ps.setInt(i, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{Math.min(rs.getInt(1), target), target};
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Error progreso logro", ex);
        }
        return new int[]{0, target};
    }

    // -----------------------------------------------------------------
    // Helpers de modelo
    // -----------------------------------------------------------------
    private Logro findLogro(final Integer idLogro) {
        if (idLogro == null) return null;
        return catalogo.stream()
                .filter(l -> idLogro.equals(l.getIdLogro()))
                .findFirst().orElse(null);
    }

    private LogroCliente findObtenido(final Integer idLogro) {
        return obtenidosCliente.stream()
                .filter(lc -> idLogro.equals(lc.getIdLogro()))
                .findFirst().orElse(null);
    }

    private String rarezaDe(final Logro l) {
        final Integer p = l.getPuntos();
        if (p == null) return "comun";
        if (p >= 300) return "legendario";
        if (p >= 200) return "epico";
        if (p >= 100) return "raro";
        return "comun";
    }

    private String colorRareza(final String rareza) {
        return switch (rareza) {
            case "raro" -> "#0d6efd";
            case "epico" -> "#6f42c1";
            case "legendario" -> "#F2C94C";
            default -> "#6c757d";
        };
    }

    private String emojiPorCodigo(final String codigo) {
        return switch (codigo == null ? "" : codigo.toUpperCase()) {
            case "PRIMER_LIKE" -> "❤";
            case "CRITICO" -> "✍️";
            case "COLECCIONISTA" -> "📚";
            case "NOCTAMBULO" -> "🌙";
            case "EXP_CARIBE" -> "🌊";
            case "FAN_VALLENATO" -> "🎵";
            default -> "🏆";
        };
    }

    private String capitalizar(final String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
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
    @FXML private void onIrCapsulas() { HistorialNavegacion.getInstance().navegar("/view/capsulas.fxml"); }
    @FXML private void onIrLogros() { /* ya estamos aquí */ }
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