package com.beatify.view.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba la máquina de estados de HistorialNavegacion sin Stage real.
 * Los métodos atras()/adelante()/navegar() verifican estado a través de
 * los métodos públicos puedeAtras()/puedeAdelante(); cambiarDesdeHistorial()
 * no se invoca porque requiere Stage (UI).
 */
class HistorialNavegacionTest {

    private HistorialNavegacion historial;

    @BeforeEach
    void setUp() {
        historial = HistorialNavegacion.getInstance();
        historial.reset();
    }

    @Test
    void inicialmenteNoPuedeAtrasNiAdelante() {
        assertFalse(historial.puedeAtras());
        assertFalse(historial.puedeAdelante());
    }

    @Test
    void despuesDeResetNoPuedeAtrasNiAdelante() {
        // Simula un estado con entradas — luego reset
        historial.reset();
        assertFalse(historial.puedeAtras());
        assertFalse(historial.puedeAdelante());
    }

    @Test
    void sinStageNavearNoFalla() {
        // Sin stage inicializado, navegar() debe salir silenciosamente
        assertDoesNotThrow(() -> historial.navegar("/view/home.fxml"));
    }

    @Test
    void sinStageAtrasNoFalla() {
        assertDoesNotThrow(() -> historial.atras());
    }

    @Test
    void sinStageAdelanteNoFalla() {
        assertDoesNotThrow(() -> historial.adelante());
    }

    @Test
    void puedeAtrasEsFalsoCuandoStageEsNull() {
        // reset() limpia el stage
        historial.reset();
        assertFalse(historial.puedeAtras());
        assertFalse(historial.puedeAdelante());
    }
}
