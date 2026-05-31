package com.beatify.view.util;

import com.beatify.dao.CancionPlaylistDAO;
import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.CancionPlaylist;
import com.beatify.model.Cliente;
import com.beatify.model.Playlist;
import com.beatify.view.SessionContext;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Menú desplegable "Agregar a playlist" (estilo Spotify).
 * Muestra las playlists del usuario; al elegir una, inserta la canción
 * en CANCION_PLAYLIST. Maneja duplicados (UNIQUE canción+playlist).
 *
 * Uso desde el onAction de un botón "+":
 *   AgregarAPlaylistUtil.mostrar(boton, idCancion, "La Gota Fría");
 */
public final class AgregarAPlaylistUtil {

    private static final Logger LOG = Logger.getLogger(AgregarAPlaylistUtil.class.getName());
    private static final PlaylistDAO PLAYLIST_DAO = new PlaylistDAO();
    private static final CancionPlaylistDAO CP_DAO = new CancionPlaylistDAO();

    private AgregarAPlaylistUtil() {
    }

    public static void mostrar(final Node ancla, final Integer idCancion, final String tituloCancion) {
        final ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("bf-user-menu");

        final Cliente c = SessionContext.getInstance().getClienteActual();
        if (c == null || c.getIdCliente() == null || idCancion == null) {
            menu.getItems().add(itemInfo("Inicia sesión para usar playlists"));
            menu.show(ancla, Side.BOTTOM, 0, 4);
            return;
        }

        // Cabecera
        final MenuItem hd = new MenuItem("Agregar a playlist…");
        hd.setDisable(true);
        menu.getItems().addAll(hd, new SeparatorMenuItem());

        try {
            final List<Playlist> mias = PLAYLIST_DAO.listarPorCliente(c.getIdCliente());
            if (mias.isEmpty()) {
                menu.getItems().add(itemInfo("No tienes playlists. Créala con el botón +"));
            } else {
                for (final Playlist pl : mias) {
                    final MenuItem it = new MenuItem(pl.getNombre());
                    it.setOnAction(e -> agregar(ancla, pl, idCancion, tituloCancion));
                    menu.getItems().add(it);
                }
            }
        } catch (final ConexionException ex) {
            LOG.log(Level.WARNING, "Error listando playlists", ex);
            menu.getItems().add(itemInfo("Error de conexión"));
        }

        menu.show(ancla, Side.BOTTOM, 0, 4);
    }

    private static void agregar(final Node ancla, final Playlist pl,
                                final Integer idCancion, final String tituloCancion) {
        try {
            final CancionPlaylist cp = new CancionPlaylist(
                    null, LocalDate.now(), pl.getIdPlaylist(), idCancion);
            CP_DAO.insertar(cp);
            alerta(ancla, Alert.AlertType.INFORMATION,
                    "Añadida", "«" + tituloCancion + "» se agregó a «" + pl.getNombre() + "».");
        } catch (final RuntimeException ex) {
            final String msg = ex.getMessage() == null ? "" : ex.getMessage();
            // UNIQUE (CANCION_id_cancion, PLAYLIST_id_playlist) → ya estaba
            if (msg.contains("UN") || msg.toUpperCase().contains("UNIQUE")
                                   || msg.contains("ORA-00001")) {
                alerta(ancla, Alert.AlertType.INFORMATION,
                        "Ya estaba", "«" + tituloCancion + "» ya está en «" + pl.getNombre() + "».");
            } else {
                LOG.log(Level.WARNING, "Error agregando canción a playlist", ex);
                alerta(ancla, Alert.AlertType.ERROR,
                        "Error", "No se pudo agregar la canción. Revisa la conexión.");
            }
        }
    }

    private static MenuItem itemInfo(final String texto) {
        final MenuItem it = new MenuItem(texto);
        it.setDisable(true);
        return it;
    }

    private static void alerta(final Node ancla, final Alert.AlertType tipo,
                               final String titulo, final String msg) {
        final Alert a = new Alert(tipo, msg, ButtonType.OK);
        a.setTitle(titulo);
        a.setHeaderText(null);
        if (ancla.getScene() != null) a.initOwner(ancla.getScene().getWindow());
        a.showAndWait();
    }
}
