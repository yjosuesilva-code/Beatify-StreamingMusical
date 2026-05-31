package com.beatify.service;

import com.beatify.dao.PagoDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Pago;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que la validación de PagoService acepte exactamente los métodos y
 * estados del CHECK de la BD (PAGO_METODO_PAGO_CK / PAGO_ESTADO_PAGO_CK) y
 * rechace los valores antiguos desalineados. Usa un stub de PagoDAO sin BD.
 */
class PagoServiceTest {

    private static class PagoDAOStub extends PagoDAO {
        @Override
        public Integer insertar(final Pago pago) {
            return 1;
        }
    }

    private PagoService service;

    @BeforeEach
    void setUp() {
        service = new PagoService(new PagoDAOStub());
    }

    private static Pago pago(final String metodo, final String estado) {
        return new Pago(9990.0, LocalDateTime.now(), metodo, estado, "REF-1", 1);
    }

    @Test
    void aceptaEstadosYMetodosDeLaBD() {
        assertEquals(1, service.registrar(pago("NEQUI", "EXITOSO")));
        assertEquals(1, service.registrar(pago("TARJETA", "PENDIENTE")));
        assertEquals(1, service.registrar(pago("PSE", "FALLIDO")));
        assertEquals(1, service.registrar(pago("DAVIPLATA", "REEMBOLSADO")));
        assertEquals(1, service.registrar(pago("PAYPAL", "EXITOSO")));
    }

    @Test
    void rechazaEstadoAntiguoAprobado() {
        assertThrows(ValidacionException.class, () -> service.registrar(pago("NEQUI", "APROBADO")));
    }

    @Test
    void rechazaMetodoAntiguoTarjetaCredito() {
        assertThrows(ValidacionException.class, () -> service.registrar(pago("TARJETA_CREDITO", "EXITOSO")));
    }
}
