package com.beatify.service;

import com.beatify.dao.SuscripcionDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Suscripcion;
import com.beatify.model.TipoPlan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba la lógica de SuscripcionService sin tocar la base de datos.
 * Usa un stub de SuscripcionDAO que no abre conexiones, igual que
 * {@code ClienteServiceTest}.
 */
class SuscripcionServiceTest {

    // ---- Stub de SuscripcionDAO ----
    private static class SuscripcionDAOStub extends SuscripcionDAO {
        Suscripcion activaADevolver;          // lo que devuelve buscarActivaPorCliente
        int idGenerado = 100;
        Suscripcion insertada;                // captura el ultimo insert
        Suscripcion actualizada;              // captura el ultimo update

        @Override
        public Integer insertar(final Suscripcion s) {
            insertada = s;
            return idGenerado;
        }

        @Override
        public Suscripcion buscarActivaPorCliente(final Integer idCliente) {
            return activaADevolver;
        }

        @Override
        public void actualizar(final Suscripcion s) {
            actualizada = s;
        }
    }

    private SuscripcionService service;
    private SuscripcionDAOStub stub;

    @BeforeEach
    void setUp() {
        stub = new SuscripcionDAOStub();
        service = new SuscripcionService(stub);
    }

    // -----------------------------------------------------------------
    // planActual
    // -----------------------------------------------------------------

    @Test
    void planActualEsFreeSinSuscripcionVigente() {
        stub.activaADevolver = null;
        assertEquals(TipoPlan.FREE, service.planActual(1));
    }

    @Test
    void planActualDevuelveElPlanDeLaSuscripcionVigente() {
        stub.activaADevolver = suscripcion("INDIVIDUAL", "ACTIVA");
        assertEquals(TipoPlan.INDIVIDUAL, service.planActual(1));
    }

    @Test
    void planActualConPlanLegacyDesconocidoCaeAFree() {
        stub.activaADevolver = suscripcion("PREMIUM", "ACTIVA");   // valor que ya no existe
        assertEquals(TipoPlan.FREE, service.planActual(1));
    }

    @Test
    void planActualConIdInvalidoEsFreeSinConsultarBD() {
        assertEquals(TipoPlan.FREE, service.planActual(null));
        assertEquals(TipoPlan.FREE, service.planActual(0));
    }

    // -----------------------------------------------------------------
    // registrar — los 5 planes oficiales son válidos; PREMIUM ya no
    // -----------------------------------------------------------------

    @Test
    void registrarAceptaIndividualYDuo() {
        assertEquals(100, service.registrar(suscripcion("INDIVIDUAL", "ACTIVA")));
        assertEquals(100, service.registrar(suscripcion("DUO", "ACTIVA")));
    }

    @Test
    void registrarRechazaPremium() {
        final Suscripcion s = suscripcion("PREMIUM", "ACTIVA");
        assertThrows(ValidacionException.class, () -> service.registrar(s));
    }

    // -----------------------------------------------------------------
    // registrar — estados deben coincidir con el CHECK de la BD
    // -----------------------------------------------------------------

    @Test
    void registrarAceptaEstadoPausada() {
        assertEquals(100, service.registrar(suscripcion("FREE", "PAUSADA")));
    }

    @Test
    void registrarRechazaEstadoPendiente() {
        final Suscripcion s = suscripcion("FREE", "PENDIENTE");   // no está en el CHECK
        assertThrows(ValidacionException.class, () -> service.registrar(s));
    }

    // -----------------------------------------------------------------
    // cambiarPlan
    // -----------------------------------------------------------------

    @Test
    void cambiarPlanActualizaLaSuscripcionActiva() {
        stub.activaADevolver = new Suscripcion(
                99, "FREE", 0.0, LocalDate.now(), null, "ACTIVA", 1);
        service.cambiarPlan(1, TipoPlan.INDIVIDUAL);

        assertNotNull(stub.actualizada, "debió actualizar la activa");
        assertEquals("INDIVIDUAL", stub.actualizada.getTipoPlan());
        assertEquals(14900.00, stub.actualizada.getPrecio());
        assertNotNull(stub.actualizada.getFechaFin(), "plan de pago debe vencer");
        assertNull(stub.insertada, "no debió crear una nueva");
    }

    @Test
    void cambiarPlanCreaSuscripcionSiNoHayActiva() {
        stub.activaADevolver = null;
        service.cambiarPlan(1, TipoPlan.DUO);

        assertNotNull(stub.insertada, "debió crear una nueva suscripción");
        assertEquals("DUO", stub.insertada.getTipoPlan());
        assertEquals("ACTIVA", stub.insertada.getEstado());
        assertNull(stub.actualizada);
    }

    @Test
    void cambiarPlanAFreeQuitaLaVigencia() {
        stub.activaADevolver = new Suscripcion(
                99, "INDIVIDUAL", 14900.0, LocalDate.now(), LocalDate.now().plusMonths(1), "ACTIVA", 1);
        service.cambiarPlan(1, TipoPlan.FREE);

        assertEquals("FREE", stub.actualizada.getTipoPlan());
        assertEquals(0.0, stub.actualizada.getPrecio());
        assertNull(stub.actualizada.getFechaFin(), "FREE no vence");
    }

    // -----------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------
    private static Suscripcion suscripcion(final String plan, final String estado) {
        return new Suscripcion(plan, 0.0, LocalDate.now(), null, estado, 1);
    }
}
