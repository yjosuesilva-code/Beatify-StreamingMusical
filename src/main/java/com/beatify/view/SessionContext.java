package com.beatify.view;

import com.beatify.dao.SuscripcionDAO;
import com.beatify.model.Cliente;
import com.beatify.model.Cancion;
import com.beatify.model.TipoPlan;
import com.beatify.service.SuscripcionService;

/**
 * Singleton que guarda el {@link Cliente} actualmente autenticado.
 * Vive durante la vida de la JVM. Lo usan los controllers para conocer
 * al usuario logueado sin tener que pasarlo como parametro por todas partes.
 *
 * No es thread-safe entre hilos no-UI, pero JavaFX corre todo en el
 * "Application Thread" asi que para la UI es suficiente.
 *
 * La persistencia entre arranques de la app (checkbox "Mantener sesion
 * iniciada") es un bloque backend aparte: aqui solo se mantiene memoria.
 */
public final class SessionContext {

    private static final SessionContext INSTANCE = new SessionContext();

    private Cliente clienteActual;
    private TipoPlan planActual;              // plan efectivo del cliente (cache perezoso)
    private String  terminoBusqueda;
    private Integer idPlaylistSeleccionada;   // playlist a mostrar en playlist.fxml
    private String  tipoDetalle;              // "ALBUM" | "ARTISTA" para detalle.fxml
    private Integer idDetalle;                // id del álbum/artista a mostrar

    // Canción que está (o estaba) en reproducción
    private Cancion cancionActual;
    private String  artistaActual;
    private String  albumActual;

    private SessionContext() {
    }

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    public Cliente getClienteActual() {
        return this.clienteActual;
    }

    public void setClienteActual(final Cliente clienteActual) {
        this.clienteActual = clienteActual;
        this.planActual = null;   // invalidar cache: se resolvera al pedirlo
    }

    public boolean haySesion() {
        return this.clienteActual != null;
    }

    /** {@code true} si el cliente logueado es administrador. */
    public boolean esAdmin() {
        return this.clienteActual != null && this.clienteActual.esAdmin();
    }

    /**
     * Plan efectivo del cliente logueado ({@link TipoPlan#FREE} si no hay
     * sesion). Se resuelve una vez contra BD y se cachea; si la BD falla se
     * asume FREE para no romper la UI. Llamar a {@link #refrescarPlan()} tras
     * un cambio de plan.
     */
    public TipoPlan getPlanActual() {
        if (this.clienteActual == null || this.clienteActual.getIdCliente() == null) {
            return TipoPlan.FREE;
        }
        if (this.planActual == null) {
            try {
                this.planActual = new SuscripcionService(new SuscripcionDAO())
                        .planActual(this.clienteActual.getIdCliente());
            } catch (final RuntimeException ex) {
                this.planActual = TipoPlan.FREE;
            }
        }
        return this.planActual;
    }

    /** Fuerza re-resolver el plan en la proxima consulta (tras cambiar de plan). */
    public void refrescarPlan() {
        this.planActual = null;
    }

    public Cancion getCancionActual()  { return this.cancionActual; }
    public String  getArtistaActual()  { return this.artistaActual; }
    public String  getAlbumActual()    { return this.albumActual; }

    public void setCancionActual(final Cancion cancion,
                                 final String artista,
                                 final String album) {
        this.cancionActual = cancion;
        this.artistaActual = artista;
        this.albumActual   = album;
    }

    /** Cierra la sesion limpiando el cliente actual. */
    public void cerrarSesion() {
        this.clienteActual   = null;
        this.planActual      = null;
        this.terminoBusqueda = null;
        this.cancionActual   = null;
        this.artistaActual   = null;
        this.albumActual     = null;
    }

    /** Playlist seleccionada para abrir en la pantalla de detalle. */
    public Integer getIdPlaylistSeleccionada() {
        return this.idPlaylistSeleccionada;
    }

    public void setIdPlaylistSeleccionada(final Integer idPlaylist) {
        this.idPlaylistSeleccionada = idPlaylist;
    }

    /** Detalle a abrir en detalle.fxml: tipo "ALBUM"/"ARTISTA" + id. */
    public String  getTipoDetalle() { return this.tipoDetalle; }
    public Integer getIdDetalle()   { return this.idDetalle; }

    public void setDetalle(final String tipo, final Integer id) {
        this.tipoDetalle = tipo;
        this.idDetalle   = id;
    }

    public String getTerminoBusqueda() {
        return this.terminoBusqueda;
    }

    /** Guarda un término para que la pantalla de destino lo recoja y limpie. */
    public void setTerminoBusqueda(final String termino) {
        this.terminoBusqueda = (termino == null || termino.isBlank()) ? null : termino.trim();
    }

    /** Devuelve y borra el término pendiente (consume-once). */
    public String consumirTerminoBusqueda() {
        final String t = this.terminoBusqueda;
        this.terminoBusqueda = null;
        return t;
    }
}
