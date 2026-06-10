package com.beatify.view.util;

import com.beatify.dao.NotificacionDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Notificacion;
import com.beatify.view.SessionContext;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Construye y muestra el panel desplegable de notificaciones (campana del topbar).
 * Diseño: tarjeta oscura con cabecera y notificaciones en formato "card"
 * (chip de icono por tipo + título + mensaje + fecha), las no leídas resaltadas.
 * Llamar desde onNotif() en cualquier controller de app.
 */
public final class NotificacionMenuUtil {

    private static final Logger LOG = Logger.getLogger(NotificacionMenuUtil.class.getName());
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM HH:mm");
    private static final NotificacionDAO DAO = new NotificacionDAO();

    private NotificacionMenuUtil() {
    }

    /** Cuenta las notificaciones NO leídas del cliente. Best-effort → 0 si falla. */
    public static int contarNoLeidas(final Integer idCliente) {
        if (idCliente == null) return 0;
        try {
            return (int) DAO.listar().stream()
                    .filter(n -> idCliente.equals(n.getIdCliente()))
                    .filter(n -> "N".equalsIgnoreCase(n.getLeida()))
                    .count();
        } catch (final RuntimeException ex) {
            LOG.log(Level.FINE, "No se pudo contar notificaciones", ex);
            return 0;
        }
    }

    /**
     * Aplica el conteo real de no leídas al badge y lo oculta si es 0.
     * Llamar desde configurarTopBar de cada controller.
     */
    public static void aplicarBadge(final Label badge, final Integer idCliente) {
        if (badge == null) return;
        final int n = contarNoLeidas(idCliente);
        badge.setText(String.valueOf(n));
        badge.setVisible(n > 0);
        badge.setManaged(n > 0);
    }

    /**
     * Muestra el panel anclado debajo de {@code ancla}.
     * Si no hay cliente en sesión o no hay notificaciones, muestra mensaje vacío.
     */
    public static void mostrar(final Button ancla) {
        mostrar(ancla, null);
    }

    /**
     * Igual que {@link #mostrar(Button)} pero refresca el {@code badge} en vivo
     * cuando se marca una notificación como leída.
     */
    public static void mostrar(final Button ancla, final Label badge) {
        final ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("bf-notif-menu");

        final Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null || actual.getIdCliente() == null) {
            menu.getItems().add(itemMensaje("Inicia sesión para ver tus notificaciones."));
            menu.show(ancla, Side.BOTTOM, 0, 6);
            return;
        }
        final Integer idCli = actual.getIdCliente();

        try {
            // Filtrar notificaciones del cliente actual, no leídas primero
            final List<Notificacion> propias = DAO.listar().stream()
                    .filter(n -> actual.getIdCliente().equals(n.getIdCliente()))
                    .sorted((a, b) -> {
                        // No leídas primero, luego por fecha desc
                        final boolean aN = "N".equalsIgnoreCase(a.getLeida());
                        final boolean bN = "N".equalsIgnoreCase(b.getLeida());
                        if (aN != bN) return aN ? -1 : 1;
                        if (a.getFechaEnvio() == null) return 1;
                        if (b.getFechaEnvio() == null) return -1;
                        return b.getFechaEnvio().compareTo(a.getFechaEnvio());
                    })
                    .limit(8)  // máximo 8 en el panel
                    .toList();

            if (propias.isEmpty()) {
                menu.getItems().add(itemMensaje("Sin notificaciones por ahora."));
            } else {
                // Cabecera
                menu.getItems().add(itemMensaje("Notificaciones"));
                menu.getItems().add(new SeparatorMenuItem());

                for (final Notificacion n : propias) {
                    menu.getItems().add(construirItem(n, badge, idCli));
                }
            }
        } catch (final ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando notificaciones", ex);
            menu.getItems().add(itemMensaje("No se pudieron cargar las notificaciones."));
        }

        menu.show(ancla, Side.BOTTOM, 0, 6);
    }

    /**
     * Construye una "card" de notificación: chip de icono por tipo + título +
     * mensaje + fecha. Las no leídas llevan título en blanco y una franja de acento.
     */
    private static CustomMenuItem construirItem(final Notificacion n,
                                                final Label badge,
                                                final Integer idCliente) {
        final boolean noLeida = "N".equalsIgnoreCase(n.getLeida());

        // Chip con el icono del tipo
        final FontIcon icon = iconoSeguro(iconoPorTipo(n.getTipo()));
        icon.setIconSize(15);
        icon.getStyleClass().add("bf-notif-icon");
        final StackPane chip = new StackPane(icon);
        chip.getStyleClass().add("bf-notif-chip");

        // Título
        final Label titulo = new Label(n.getTitulo() == null ? "Notificación" : n.getTitulo());
        titulo.getStyleClass().add("bf-notif-title");
        if (!noLeida) titulo.getStyleClass().add("is-read");
        titulo.setWrapText(true);
        titulo.setMaxWidth(240);

        // Mensaje
        final Label mensaje = new Label(n.getMensaje() == null ? "" : n.getMensaje());
        mensaje.getStyleClass().add("bf-notif-msg");
        mensaje.setWrapText(true);
        mensaje.setMaxWidth(240);

        // Fecha
        final Label fecha = new Label(n.getFechaEnvio() == null ? "" : n.getFechaEnvio().format(FMT));
        fecha.getStyleClass().add("bf-notif-date");

        final VBox textos = new VBox(2, titulo, mensaje, fecha);

        final HBox row = new HBox(11, chip, textos);
        row.setAlignment(Pos.TOP_LEFT);
        row.setMaxWidth(280);
        row.getStyleClass().add("bf-notif-card");
        if (noLeida) row.getStyleClass().add("is-unread");

        final CustomMenuItem item = new CustomMenuItem(row, true);
        item.setHideOnClick(true);
        item.setOnAction(e -> {
            marcarLeida(n);
            if (badge != null) aplicarBadge(badge, idCliente);  // refresco en vivo
        });
        return item;
    }

    /** Item simple con texto plano (cabecera / mensaje vacío / error). */
    private static MenuItem itemMensaje(final String texto) {
        final Label l = new Label(texto);
        l.getStyleClass().add("bf-notif-header");
        final CustomMenuItem item = new CustomMenuItem(l, false);
        item.setHideOnClick(false);
        return item;
    }

    /** Marca leída la notificación en BD (best-effort, no rompe UI si falla). */
    private static void marcarLeida(final Notificacion n) {
        if (n.getIdNotificacion() == null || "S".equalsIgnoreCase(n.getLeida())) return;
        try {
            n.setLeida("S");
            DAO.actualizar(n);
        } catch (final Exception ex) {
            LOG.log(Level.WARNING, "Error marcando notificación leída", ex);
        }
    }

    /** Crea el FontIcon; si el literal no existe en el pack, cae a la campana. */
    private static FontIcon iconoSeguro(final String literal) {
        try {
            return new FontIcon(literal);
        } catch (final RuntimeException ex) {
            LOG.log(Level.FINE, "Icono no disponible: " + literal, ex);
            return new FontIcon("bi-bell");
        }
    }

    private static String iconoPorTipo(final String tipo) {
        if (tipo == null) return "bi-bell";
        return switch (tipo.toUpperCase()) {
            case "PROMO"         -> "bi-gift";
            case "RECOMENDACION" -> "bi-music-note-beamed";
            case "SISTEMA"       -> "bi-gear";
            case "INFO"          -> "bi-info-circle";
            default              -> "bi-bell";
        };
    }
}
