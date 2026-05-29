package com.beatify.view.controller;

import com.beatify.model.Cliente;
import com.beatify.view.SessionContext;
import com.beatify.view.component.AlbumCover;
import com.beatify.view.component.ArtistAvatar;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Logger;

public class PerfilController {

    private static final Logger LOG = Logger.getLogger(PerfilController.class.getName());
    private static final NumberFormat NF = NumberFormat.getInstance(new Locale("es", "CO"));

    @FXML private Button btnNotif;
    @FXML private Button btnUserMenu;
    @FXML private Label lblNotifCount;
    @FXML private StackPane userAvatarHolder;
    @FXML private Label lblUserNombre;
    @FXML private VBox sidebarPlaylistsBox;

    @FXML private StackPane avatarHolder;
    @FXML private Label lblNombre;
    @FXML private Label lblUbicacion;
    @FXML private Label lblEmail;
    @FXML private Label lblPlanBadge;
    @FXML private Label lblMiembroDesde;
    @FXML private Button btnEditarPerfil;

    @FXML private Label lblStatRep, lblStatLikes, lblStatResenas, lblStatSiguiendo, lblStatLogros;
    @FXML private HBox logrosRecientesBox;
    @FXML private VBox capsulasBox;
    @FXML private VBox recienteBox;

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
        configurarStats();
        configurarLogrosRecientes();
        configurarCapsulas();
        configurarReciente();
    }

    private void configurarTopBar(Cliente c) {
        userAvatarHolder.getChildren().setAll(
                new ArtistAvatar(28, "#b794ff", "#8b5cf6", c.getNombre() + " " + c.getApellido()));
        lblUserNombre.setText(c.getNombre());
        lblNotifCount.setText("5");
    }

    private void configurarSidebarPlaylists() {
        // Placeholder
        sidebarPlaylistsBox.getChildren().clear();
        Label lbl = new Label("Tus playlists aquí");
        lbl.setStyle("-fx-text-fill: #8da3bd; -fx-padding: 10;");
        sidebarPlaylistsBox.getChildren().add(lbl);
    }

    private void configurarHero(Cliente c) {
        avatarHolder.getChildren().setAll(
                new ArtistAvatar(120, "#b794ff", "#8b5cf6", c.getNombre() + " " + c.getApellido()));
        lblNombre.setText(c.getNombre() + " " + c.getApellido());
        lblUbicacion.setText("📍 " + (c.getCiudad() == null ? "Valledupar" : c.getCiudad()));
        lblEmail.setText(c.getCorreo() == null ? "usuario@beatify.co" : c.getCorreo());
        lblPlanBadge.setText("PLAN: INDIVIDUAL");
        lblMiembroDesde.setText("Miembro desde mayo 2026");
    }

    private void configurarStats() {
        lblStatRep.setText(NF.format(1247));
        lblStatLikes.setText("89");
        lblStatResenas.setText("23");
        lblStatSiguiendo.setText("42");
        lblStatLogros.setText("8 / 24");
    }

    private void configurarLogrosRecientes() {
        logrosRecientesBox.getChildren().clear();
        String[][] logros = {{"🎵", "Primer Paso"}, {"⭐", "Vallenato de Corazón"}, {"🗺️", "Explorador"}};
        for (String[] l : logros) {
            VBox card = new VBox(4);
            card.setAlignment(Pos.CENTER);
            card.setStyle("-fx-background-color: #1c324f; -fx-background-radius: 12px; -fx-padding: 12px; -fx-pref-width: 100px;");
            Label icono = new Label(l[0]);
            icono.setStyle("-fx-font-size: 28px;");
            Label nombre = new Label(l[1]);
            nombre.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-wrap-text: true; -fx-alignment: center;");
            card.getChildren().addAll(icono, nombre);
            logrosRecientesBox.getChildren().add(card);
        }
        Button btn = new Button("Ver todos →");
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #b794ff; -fx-cursor: hand;");
        btn.setOnAction(e -> NavegacionUtil.cambiarA("/view/logros.fxml", btnUserMenu));
        logrosRecientesBox.getChildren().add(btn);
    }

    private void configurarCapsulas() {
        capsulasBox.getChildren().clear();
        String[] capsulas = {"Hace 1 año (mayo 2025)", "Hace 6 meses (noviembre 2025)"};
        for (String c : capsulas) {
            Label lbl = new Label("📅 " + c);
            lbl.setStyle("-fx-text-fill: #8da3bd; -fx-padding: 8;");
            capsulasBox.getChildren().add(lbl);
        }
        Button btn = new Button("Ver todas →");
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #b794ff; -fx-cursor: hand;");
        btn.setOnAction(e -> NavegacionUtil.cambiarA("/view/capsulas.fxml", btnUserMenu));
        capsulasBox.getChildren().add(btn);
    }

    private void configurarReciente() {
        recienteBox.getChildren().clear();
        String[][] canciones = {{"La Gota Fría", "Carlos Vives"}, {"La Tierra del Olvido", "Carlos Vives"}};
        for (String[] c : canciones) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 8;");
            Label titulo = new Label("🎵 " + c[0]);
            titulo.setStyle("-fx-text-fill: white;");
            Label artista = new Label(c[1]);
            artista.setStyle("-fx-text-fill: #8da3bd;");
            row.getChildren().addAll(titulo, artista);
            recienteBox.getChildren().add(row);
        }
    }

    @FXML private void onAtras() { NavegacionUtil.cambiarA("/view/home.fxml", btnUserMenu); }
    @FXML private void onAdelante() {}
    @FXML private void onUserMenu() {}
    @FXML private void onIrInicio() { NavegacionUtil.cambiarA("/view/home.fxml", btnUserMenu); }
    @FXML private void onIrExplorar() { NavegacionUtil.cambiarA("/view/catalogo.fxml", btnUserMenu); }
    @FXML private void onIrBiblioteca() {}
    @FXML private void onIrResenas() { NavegacionUtil.cambiarA("/view/resenas.fxml", btnUserMenu); }
    @FXML private void onIrBarrio() { NavegacionUtil.cambiarA("/view/barrio.fxml", btnUserMenu); }
    @FXML private void onIrCapsulas() { NavegacionUtil.cambiarA("/view/capsulas.fxml", btnUserMenu); }
    @FXML private void onIrLogros() { NavegacionUtil.cambiarA("/view/logros.fxml", btnUserMenu); }
    @FXML private void onIrPerfil() {}
    @FXML private void onNuevaPlaylist() {}

    @FXML private void onCerrarSesion() {
        SessionContext.getInstance().cerrarSesion();
        NavegacionUtil.cambiarA("/view/login.fxml", btnUserMenu);
    }

    private Cliente clientePlaceholder() {
        Cliente c = new Cliente();
        c.setNombre("Yilver");
        c.setApellido("Silva");
        c.setCiudad("Valledupar");
        c.setCorreo("yjosue@unicesar.edu.co");
        return c;
    }
}