package com.beatify.view.util;

import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Historial de navegación entre vistas (singleton).
 *
 * Mantiene dos pilas: una hacia atrás y una hacia adelante.
 * Necesita ser inicializado con el Stage cuando el usuario entra a la app
 * (tras login o registro). Se resetea al cerrar sesión.
 *
 * Rutas de autenticación (/view/login.fxml, /view/registro.fxml) no se
 * registran en el historial — se navega a ellas con NavegacionUtil.cambiarA()
 * directamente.
 */
public final class HistorialNavegacion {

    private static final HistorialNavegacion INSTANCE = new HistorialNavegacion();

    private final Deque<String> pilaAtras    = new ArrayDeque<>();
    private final Deque<String> pilaAdelante = new ArrayDeque<>();
    private String rutaActual = null;
    private Stage  stage      = null;

    private HistorialNavegacion() {
    }

    public static HistorialNavegacion getInstance() {
        return INSTANCE;
    }

    /**
     * Llama esto justo después de que el usuario inicia sesión o se registra,
     * con el Stage activo y la ruta de la primera pantalla app.
     */
    public void iniciar(final Stage stage, final String rutaInicial) {
        this.stage = stage;
        this.rutaActual = rutaInicial;
        pilaAtras.clear();
        pilaAdelante.clear();
    }

    /**
     * Navega a una nueva ruta registrándola en el historial.
     * Limpia la pila adelante (igual que un navegador al seguir un link nuevo).
     */
    public void navegar(final String nuevaRuta) {
        if (stage == null) return;
        if (nuevaRuta.equals(rutaActual)) return;
        if (rutaActual != null) pilaAtras.push(rutaActual);
        pilaAdelante.clear();
        rutaActual = nuevaRuta;
        NavegacionUtil.cambiarDesdeHistorial(nuevaRuta, stage);
    }

    /** Vuelve a la pantalla anterior si existe. */
    public void atras() {
        if (stage == null || pilaAtras.isEmpty()) return;
        pilaAdelante.push(rutaActual);
        rutaActual = pilaAtras.pop();
        NavegacionUtil.cambiarDesdeHistorial(rutaActual, stage);
    }

    /** Avanza a la pantalla siguiente (solo si se usó atrás antes). */
    public void adelante() {
        if (stage == null || pilaAdelante.isEmpty()) return;
        pilaAtras.push(rutaActual);
        rutaActual = pilaAdelante.pop();
        NavegacionUtil.cambiarDesdeHistorial(rutaActual, stage);
    }

    public boolean puedeAtras()    { return !pilaAtras.isEmpty(); }
    public boolean puedeAdelante() { return !pilaAdelante.isEmpty(); }

    /** Llama esto al cerrar sesión para limpiar el estado. */
    public void reset() {
        pilaAtras.clear();
        pilaAdelante.clear();
        rutaActual = null;
        stage      = null;
    }
}
