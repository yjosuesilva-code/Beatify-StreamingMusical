package com.beatify.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica el catálogo de planes (TipoPlan) como única fuente de verdad:
 * planes válidos, precios y la diferencia funcional de cada uno (anuncios,
 * saltos, descargas, verificación, vigencia).
 */
class TipoPlanTest {

    @Test
    void nombresValidosSonLosCincoDeLaBD() {
        assertEquals(java.util.Set.of("FREE", "ESTUDIANTE", "INDIVIDUAL", "DUO", "FAMILIAR"),
                TipoPlan.nombresValidos());
    }

    @Test
    void premiumNoEsUnPlanValido() {
        assertFalse(TipoPlan.esValido("PREMIUM"));
        assertFalse(TipoPlan.esValido(null));
        assertTrue(TipoPlan.esValido("individual"));   // case-insensitive
    }

    @Test
    void desdeNombreEsCaseInsensitive() {
        assertEquals(TipoPlan.FAMILIAR, TipoPlan.desdeNombre(" familiar "));
    }

    @Test
    void desdeNombreDesconocidoLanza() {
        assertThrows(IllegalArgumentException.class, () -> TipoPlan.desdeNombre("PREMIUM"));
    }

    @Test
    void desdeNombreOFreeCaeAFreeSiDesconocido() {
        assertEquals(TipoPlan.FREE, TipoPlan.desdeNombreOFree("PREMIUM"));
        assertEquals(TipoPlan.FREE, TipoPlan.desdeNombreOFree(null));
        assertEquals(TipoPlan.DUO, TipoPlan.desdeNombreOFree("DUO"));
    }

    @Test
    void preciosCoincidenConLaUI() {
        assertEquals(0.00,     TipoPlan.FREE.getPrecioMensual());
        assertEquals(5990.00,  TipoPlan.ESTUDIANTE.getPrecioMensual());
        assertEquals(14900.00, TipoPlan.INDIVIDUAL.getPrecioMensual());
        assertEquals(19900.00, TipoPlan.DUO.getPrecioMensual());
        assertEquals(25900.00, TipoPlan.FAMILIAR.getPrecioMensual());
    }

    @Test
    void freeTieneAnunciosSaltosLimitadosYNoVence() {
        assertTrue(TipoPlan.FREE.isConAnuncios());
        assertEquals(4, TipoPlan.FREE.getFrecuenciaAnuncioCanciones());
        assertFalse(TipoPlan.FREE.isSaltosIlimitados());
        assertEquals(6, TipoPlan.FREE.getMaxSaltosPorHora());
        assertFalse(TipoPlan.FREE.isDescargasOffline());
        assertFalse(TipoPlan.FREE.isVenceMensual());
        assertFalse(TipoPlan.FREE.esDePago());
    }

    @Test
    void planesDePagoSinAnunciosConSaltosIlimitadosYDescargas() {
        for (final TipoPlan plan : new TipoPlan[]{
                TipoPlan.ESTUDIANTE, TipoPlan.INDIVIDUAL, TipoPlan.DUO, TipoPlan.FAMILIAR}) {
            assertFalse(plan.isConAnuncios(), plan + " no debería tener anuncios");
            assertTrue(plan.isSaltosIlimitados(), plan + " debería tener saltos ilimitados");
            assertTrue(plan.isDescargasOffline(), plan + " debería permitir descargas");
            assertTrue(plan.isVenceMensual(), plan + " debería vencer mensualmente");
            assertTrue(plan.esDePago(), plan + " debería ser de pago");
        }
    }

    @Test
    void soloEstudianteRequiereVerificacion() {
        assertTrue(TipoPlan.ESTUDIANTE.isRequiereVerificacion());
        assertFalse(TipoPlan.FREE.isRequiereVerificacion());
        assertFalse(TipoPlan.INDIVIDUAL.isRequiereVerificacion());
        assertFalse(TipoPlan.DUO.isRequiereVerificacion());
        assertFalse(TipoPlan.FAMILIAR.isRequiereVerificacion());
    }

    @Test
    void perfilesYStreamsEscalanConElPlan() {
        assertEquals(1, TipoPlan.FREE.getPerfiles());
        assertEquals(2, TipoPlan.DUO.getReproduccionesSimultaneas());
        assertEquals(6, TipoPlan.FAMILIAR.getReproduccionesSimultaneas());
    }
}
