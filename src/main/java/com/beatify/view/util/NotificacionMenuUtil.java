package com.beatify.view.util;

import com.beatify.dao.NotificacionDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.model.Notificacion;
import com.beatify.view.SessionContext;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Construye y muestra el panel desplegable de notificaciones (campana del topbar).
 * Trae las notificaciones reales del cliente desde NOTIFICACION (no leídas primero).
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
    public static void aplicarBadge(final javafx.scene.control.Label badge, final Integer idCliente) {
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
    public static void mostrar(final Button ancla, final javafx.scene.control.Label badge) {
        final ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("bf-notif-menu");

        final Cliente actual = SessionContext.getInstance().getClienteActual();
        if (actual == null || actual.getIdCliente() == null) {
            menu.getItems().add(itemMensaje("Inicia sesión para ver tus notificaciones."));
            menu.show(ancla, Side.BOTTOM, 0, 4);
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
                menu.getItems().add(itemMensaje("NOTIFICACIONES"));
                menu.getItems().add(new SeparatorMenuItem());

                for (final Notificacion n : propias) {
                    menu.getItems().add(construirItem(n, badge, idCli));
                }
            }
        } catch (final ConexionException ex) {
            LOG.log(Level.WARNING, "Error cargando notificaciones", ex);
            menu.getItems().add(itemMensaje("Error de conexión al cargar notificaciones."));
        }

        menu.show(ancla, Side.BOTTOM, 0, 4);
    }

    /**
     * Construye un MenuItem custom con título + mensaje + fecha,
     * para que se vea como tarjeta y no como item de menú plano.
     */
    private static CustomMenuItem construirItem(final Notificacion n,
                                                final javafx.scene.control.Label badge,
                                                final Integer idCliente) {
        final VBox box = new VBox(3);
        box.setMaxWidth(320);
        box.setStyle("-fx-padding: 2 4 4 4;");

        // Línea 1: emoji por tipo + título
        final String emoji = emojiPorTipo(n.getTipo());
        final Text titulo = new Text(emoji + "  " + (n.getTitulo() == null ? "Notificación" : n.getTitulo()));
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;"
                + " -fx-fill: " + ("N".equalsIgnoreCase(n.getLeida()) ? "white" : "#a7a7a7") + ";");
        final TextFlow tFlow = new TextFlow(titulo);
        tFlow.setMaxWidth(320);

        // Línea 2: mensaje truncado
        final String msg = n.getMensaje() == null ? "" : n.getMensaje();
        final String msgCorto = msg.length() > 90 ? msg.substring(0, 90) + "…" : msg;
        final Text mensaje = new Text(msgCorto);
        mensaje.setStyle("-fx-font-size: 11.5px; -fx-fill: #b3b3b3;");
        final TextFlow mFlow = new TextFlow(mensaje);
        mFlow.setMaxWidth(320);

        // Línea 3: fecha
        final String fechaStr = n.getFechaEnvio() == null ? "" : n.getFechaEnvio().format(FMT);
        final Text fecha = new Text(fechaStr);
        fecha.setStyle("-fx-font-size: 10.5px; -fx-fill: #6a6a6a; -fx-font-family: 'JetBrains Mono';");

        box.getChildren().addAll(tFlow, mFlow, fecha);

        final CustomMenuItem item = new CustomMenuItem(box, false);
        item.setHideOnClick(true);
        item.setOnAction(e -> {
            marcarLeida(n);
            if (badge != null) aplicarBadge(badge, idCliente);  // refresco en vivo
        });
        return item;
    }

    /** Item simple con texto plano (cabeceras / mensaje vacío). */
    private static MenuItem itemMensaje(final String texto) {
        final Text t = new Text(texto);
        t.setStyle("-fx-fill: #a7a7a7; -fx-font-size: 11.5px; -fx-font-weight: bold;");
        final CustomMenuItem item = new CustomMenuItem(t, false);
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

    private static String emojiPorTipo(final String tipo) {
        if (tipo == null) return "🔔";
        return switch (tipo.toUpperCase()) {
            case "PROMO"         -> "🎁";
            case "RECOMENDACION" -> "🎵";
            case "SISTEMA"       -> "⚙";
            case "INFO"          -> "ℹ";
            default              -> "🔔";
        };
    }
}
