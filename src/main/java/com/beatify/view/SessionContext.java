package com.beatify.view;

import com.beatify.model.Cliente;

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
    }

    public boolean haySesion() {
        return this.clienteActual != null;
    }

    /** Cierra la sesion limpiando el cliente actual. */
    public void cerrarSesion() {
        this.clienteActual = null;
    }
}
