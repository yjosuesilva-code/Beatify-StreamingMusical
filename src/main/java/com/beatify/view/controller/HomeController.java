package com.beatify.view.controller;

import com.beatify.model.Cliente;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.component.MediaCard;
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

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Controller de la pantalla principal del Home (home.fxml).
 *
 * Esta pantalla muestra el panorama completo de la app:
 *   - Greeting personalizado por hora del dia
 *   - 4 estadisticas del usuario (placeholders mientras no haya queries)
 *   - 6 playlists recientes en grid 3x2
 *   - BarrioCard con top 5 artistas de la ciudad del usuario
 *   - 6 albumes recomendados
 *   - 6 artistas para descubrir (circulares)
 *   - 4 canciones del historial reciente
 *   - Mini-player abajo con la cancion en curso
 *
 * La mayoria de los datos son HARDCODED por ahora. Cuando los services
 * tengan los metodos correspondientes (PlaylistService.deCliente(),
 * BarrioService.topEnCiudad(), etc.) se reemplazan los placeholders.
 * Ver TODOs marcados como "bloque backend".
 */
public class HomeController {

    private static final Logger LOG = Logger.getLogger(HomeController.class.getName());
    private static final NumberFormat NF_ES_CO = NumberFormat.getInstance(new Locale("es", "CO"));

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
    @FXML private Label lblHeroSub;
    @FXML private Label lblStatRep, lblStatLikes, lblStatResenas, lblStatSiguiendo;

    // ---- Quick row + secciones ----
    @FXML private GridPane quickRowGrid;
    @FXML private GridPane recomendadosGrid;
    @FXML private GridPane artistasGrid;
    @FXML private VBox     continuarBox;

    // ---- BarrioCard ----
    @FXML private Label lblBarrioCiudad;
    @FXML private VBox  barrioRankingBox;

    // ---- MiniPlayer ----
    @FXML private StackPane mpCoverHolder;
    @FXML private Label     mpTitulo, mpArtista;

    @FXML
    private void initialize() {
        // El Cliente actual viene de SessionContext (lo guardo el Login)
        Cliente actual = SessionContext.getInstance().getClienteActual();

        // Defensa: si no hay sesion (alguien navego directo al Home),
        // armamos un cliente placeholder para no romper en demo
        if (actual == null) {
            LOG.warning("No hay cliente en SessionContext — usando placeholder de demo");
            actual = clientePlaceholder();
            SessionContext.getInstance().setClienteActual(actual);
        }

        configurarTopBar(actual);
        configurarHero(actual);
        configurarSidebarPlaylists();
        configurarQuickRow();
        configurarBarrioCard(actual);
        configurarRecomendados();
        configurarArtistas();
        configurarContinuar();
        configurarMiniPlayer();
    }

    // -----------------------------------------------------------------
    // Top bar y hero (depende del Cliente actual)
    // -----------------------------------------------------------------

    private void configurarTopBar(final Cliente c) {
        // Avatar del usuario en la pill (44 px, gradiente lavanda)
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#b794ff", "#8b5cf6",
                        c.getNombre() + " " + c.getApellido()));
        lblUserNombre.setText(c.getNombre());
        lblNotifCount.setText("5");                 // placeholder
    }

    private void configurarHero(final Cliente c) {
        final String saludo = SaludoUtil.saludoActual().toUpperCase();
        lblGreeting.setText(saludo + ", " + c.getNombre().toUpperCase());

        final String ciudad = c.getCiudad() == null ? "tu ciudad" : c.getCiudad();
        lblHeroSub.setText("Tu ciudad — " + ciudad
                + " — está escuchando vallenato clásico y cumbia. Únete.");

        // TODO bloque backend: stats reales de
        //   - ReproduccionDAO.contarPorCliente(idCliente)
        //   - LikeCancionDAO.contarPorCliente(...) + LikeAlbumDAO + LikePlaylistDAO
        //   - ResenaDAO.contarPorCliente(idCliente)
        //   - SeguimientoDAO.contarSeguidos(idCliente)
        lblStatRep.setText(NF_ES_CO.format(1247));
        lblStatLikes.setText("89");
        lblStatResenas.setText("23");
        lblStatSiguiendo.setText("42");
    }

    // -----------------------------------------------------------------
    // Sidebar: lista de playlists (placeholders)
    // -----------------------------------------------------------------

    private void configurarSidebarPlaylists() {
        // TODO bloque backend: PlaylistService.listarDeCliente(idCliente)
        final String[][] playlists = {
                {"Mis Vallenatos Clásicos", "Yo · 24 canc.", "#c97a1f", "#3a1a05"},
                {"Cumbia del Caribe",       "Yo · 18 canc.", "#1f7a5a", "#072a1a"},
                {"Para escribir tesis",     "Yo · 42 canc.", "#3a6a8a", "#051a2a"},
                {"Fiesta de Sábado",        "Andrés Z. · 31 canc.", "#a83232", "#3a0a0a"},
                {"Champeta Total",          "Kendrick S. · 27 canc.", "#d4a017", "#2a1a05"},
                {"Raíces Andinas",          "Yo · 15 canc.", "#7a3a8a", "#1a052a"},
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
                            label(pl[0], "bf-side-pl-name"),
                            label(pl[1], "bf-side-pl-meta")));
            item.setGraphic(row);
            sidebarPlaylistsBox.getChildren().add(item);
        }
    }

    // -----------------------------------------------------------------
    // Quick row (6 playlists recientes en grid 3 col x 2 filas)
    // -----------------------------------------------------------------

    private void configurarQuickRow() {
        final String[][] playlists = {
                {"Mis Vallenatos Clásicos", "#c97a1f", "#3a1a05"},
                {"Cumbia del Caribe",       "#1f7a5a", "#072a1a"},
                {"Para escribir tesis",     "#3a6a8a", "#051a2a"},
                {"Fiesta de Sábado",        "#a83232", "#3a0a0a"},
                {"Champeta Total",          "#d4a017", "#2a1a05"},
                {"Raíces Andinas",          "#7a3a8a", "#1a052a"},
        };

        for (int i = 0; i < playlists.length; i++) {
            final String[] pl = playlists[i];
            final Button quick = new Button();
            quick.getStyleClass().add("bf-quick");
            quick.setMaxWidth(Double.MAX_VALUE);

            final HBox content = new HBox(14);
            content.setAlignment(Pos.CENTER_LEFT);
            content.getChildren().addAll(
                    new AlbumCover(56, pl[1], pl[2]),
                    label(pl[0], "bf-quick-name"));
            quick.setGraphic(content);

            GridPane.setRowIndex(quick, i / 3);
            GridPane.setColumnIndex(quick, i % 3);
            quickRowGrid.getChildren().add(quick);
        }
    }

    // -----------------------------------------------------------------
    // BarrioCard: top 5 artistas en la ciudad del usuario
    // -----------------------------------------------------------------

    private void configurarBarrioCard(final Cliente c) {
        final String ciudad = c.getCiudad() == null ? "Valledupar" : c.getCiudad();
        lblBarrioCiudad.setText(ciudad);

        // TODO bloque backend: BarrioService.topEnCiudad(ciudad, periodo)
        final Object[][] barrio = {
                {1, "Diomedes Díaz",                "12.3k", "#c97a1f", "#3a1a05", "up"},
                {2, "Carlos Vives",                  "10.7k", "#1f7a5a", "#072a1a", "up"},
                {3, "Silvestre Dangond",              "9.2k", "#d4a017", "#2a1a05", "same"},
                {4, "Jorge Oñate",                    "7.8k", "#a83232", "#3a0a0a", "down"},
                {5, "Los Gaiteros de San Jacinto",   "5.4k", "#7a3a8a", "#1a052a", "up"},
        };

        for (final Object[] b : barrio) {
            final int rank        = (int) b[0];
            final String nombre   = (String) b[1];
            final String oyentes  = (String) b[2];
            final String c1       = (String) b[3];
            final String c2       = (String) b[4];
            final String trend    = (String) b[5];

            final HBox item = new HBox(14);
            item.getStyleClass().add("bf-barrio-item");
            item.setAlignment(Pos.CENTER_LEFT);

            // Rank
            final Label lblRank = new Label(String.format("%02d", rank));
            lblRank.getStyleClass().add("bf-barrio-rank");

            // Avatar
            final ArtistAvatar avatar = new ArtistAvatar(44, c1, c2, nombre);

            // Info (nombre + meta)
            final VBox info = construirVBox(2,
                    label(nombre, "bf-barrio-name"),
                    label(oyentes + " oyentes en " + ciudad, "bf-barrio-meta"));
            HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

            // Trend icon
            final StackPane trendIcon = new StackPane();
            trendIcon.getStyleClass().add("bf-trend-" + trend);
            trendIcon.getChildren().add(
                    new FontIcon(switch (trend) {
                        case "up"   -> "mtwo-arrow-upward";
                        case "down" -> "mtwo-arrow-downward";
                        default     -> "mtwo-remove";
                    }));

            item.getChildren().addAll(lblRank, avatar, info, trendIcon);
            barrioRankingBox.getChildren().add(item);
        }
    }

    // -----------------------------------------------------------------
    // Secciones: Recomendados, Artistas, Continuar
    // -----------------------------------------------------------------

    private void configurarRecomendados() {
        // TODO bloque backend: AlbumService.listarRecomendados(idCliente)
        final String[][] albumes = {
                {"Clásicos de la Provincia", "1993 · Carlos Vives",    "#c97a1f", "#3a1a05"},
                {"Cumbia Cienaguera",        "1988 · Totó la Momposina","#1f7a5a", "#072a1a"},
                {"En Concierto",             "1989 · Joe Arroyo",      "#d4a017", "#2a1a05"},
                {"La Tierra del Olvido",     "1995 · Carlos Vives",    "#a83232", "#3a0a0a"},
                {"Cantos de Bullerengue",    "2010 · Petrona Martínez","#7a3a8a", "#1a052a"},
                {"Un Canto a la Vida",       "2008 · Los Gaiteros",    "#3a6a8a", "#051a2a"},
        };

        for (int i = 0; i < albumes.length; i++) {
            final String[] a = albumes[i];
            final MediaCard card = new MediaCard(a[2], a[3], a[0], a[0], a[1], false);
            GridPane.setColumnIndex(card, i);
            recomendadosGrid.getChildren().add(card);
        }
    }

    private void configurarArtistas() {
        // TODO bloque backend: ArtistaService.listarPopulares()
        final String[][] artistas = {
                {"Diomedes Díaz",                "Vallenato · La Junta",        "#c97a1f", "#3a1a05"},
                {"Carlos Vives",                  "Vallenato · Santa Marta",     "#1f7a5a", "#072a1a"},
                {"Totó la Momposina",             "Cumbia · Talaigua",           "#d4a017", "#2a1a05"},
                {"Joe Arroyo",                    "Salsa · Cartagena",           "#a83232", "#3a0a0a"},
                {"Petrona Martínez",              "Bullerengue · Palenque",      "#7a3a8a", "#1a052a"},
                {"Los Gaiteros de San Jacinto",   "Cumbia · San Jacinto",        "#3a6a8a", "#051a2a"},
        };

        for (int i = 0; i < artistas.length; i++) {
            final String[] a = artistas[i];
            final MediaCard card = new MediaCard(a[2], a[3], a[0], a[0], a[1], true);
            GridPane.setColumnIndex(card, i);
            artistasGrid.getChildren().add(card);
        }
    }

    private void configurarContinuar() {
        // TODO bloque backend: ReproduccionService.historialReciente(idCliente, 4)
        final List<String[]> canciones = Arrays.asList(
                new String[]{"La Gota Fría",    "Carlos Vives · Vallenato",       "4:33", "#c97a1f", "#3a1a05"},
                new String[]{"La Cumbia Cienaguera", "Totó la Momposina · Cumbia","3:48", "#1f7a5a", "#072a1a"},
                new String[]{"La Rebelión",     "Joe Arroyo · Salsa",             "5:22", "#a83232", "#3a0a0a"},
                new String[]{"La Tierra del Olvido", "Carlos Vives · Vallenato",  "4:15", "#d4a017", "#2a1a05"}
        );

        for (final String[] c : canciones) {
            final Button row = new Button();
            row.getStyleClass().add("bf-rowlist-item");
            row.setMaxWidth(Double.MAX_VALUE);

            final HBox content = new HBox(16);
            content.setAlignment(Pos.CENTER_LEFT);
            content.getChildren().addAll(
                    new AlbumCover(56, c[3], c[4]),
                    construirVBox(2,
                            label(c[0], "bf-rowlist-title"),
                            label(c[1], "bf-rowlist-sub")),
                    spacerFlex(),
                    label(c[2], "bf-rowlist-dur"));
            row.setGraphic(content);
            continuarBox.getChildren().add(row);
        }
    }

    // -----------------------------------------------------------------
    // MiniPlayer (placeholder)
    // -----------------------------------------------------------------

    private void configurarMiniPlayer() {
        mpCoverHolder.getChildren().setAll(
                new AlbumCover(56, "#c97a1f", "#3a1a05", "Clásicos\nde la\nProvincia"));
    }

    // -----------------------------------------------------------------
    // Acciones de navegacion (top bar y sidebar)
    // -----------------------------------------------------------------

    @FXML private void onAtras()           { /* TODO: historial de navegacion */ }
    @FXML private void onAdelante()        { /* TODO: historial de navegacion */ }
    @FXML private void onUserMenu()        { /* TODO: menu desplegable usuario */ }
    @FXML private void onIrInicio()        { /* ya estamos en inicio */ }
    @FXML private void onIrExplorar()      { LOG.info("Navegar a Explorar — proxima pantalla"); }
    @FXML private void onIrBiblioteca()    { LOG.info("Navegar a Biblioteca — proxima pantalla"); }
    @FXML private void onIrResenas()       { LOG.info("Navegar a Reseñas — proxima pantalla"); }
    @FXML private void onIrBarrio()        { LOG.info("Navegar a Musica del Barrio — proxima pantalla"); }
    @FXML private void onIrCapsulas()      { LOG.info("Navegar a Capsulas del Tiempo — proxima pantalla"); }
    @FXML private void onIrLogros()        { LOG.info("Navegar a Logros — proxima pantalla"); }
    @FXML private void onNuevaPlaylist()   { LOG.info("Crear nueva playlist — proxima funcionalidad"); }

    @FXML
    private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    // -----------------------------------------------------------------
    // Helpers privados
    // -----------------------------------------------------------------

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

    /** Cliente placeholder usado solo si alguien navega al Home sin pasar por Login. */
    private static Cliente clientePlaceholder() {
        final Cliente c = new Cliente();
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        return c;
    }
}
