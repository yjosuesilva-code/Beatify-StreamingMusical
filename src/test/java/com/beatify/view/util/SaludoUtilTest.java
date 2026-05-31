package com.beatify.view.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaludoUtilTest {

    @ParameterizedTest(name = "hora {0}:00 -> {1}")
    @CsvSource({
        "0,  Buenas madrugadas",
        "3,  Buenas madrugadas",
        "5,  Buenas madrugadas",
        "6,  Buenos dias",
        "10, Buenos dias",
        "11, Buenos dias",
        "12, Buenas tardes",
        "15, Buenas tardes",
        "18, Buenas tardes",
        "19, Buenas noches",
        "22, Buenas noches",
        "23, Buenas noches"
    })
    void saludoSegunFranja(final int hora, final String esperado) {
        assertEquals(esperado, SaludoUtil.saludoPara(LocalTime.of(hora, 0)));
    }

    @Test
    void limiteMadrugada_5_59() {
        assertEquals("Buenas madrugadas", SaludoUtil.saludoPara(LocalTime.of(5, 59)));
    }

    @Test
    void limiteDia_6_00() {
        assertEquals("Buenos dias", SaludoUtil.saludoPara(LocalTime.of(6, 0)));
    }

    @Test
    void limiteTarde_12_00() {
        assertEquals("Buenas tardes", SaludoUtil.saludoPara(LocalTime.of(12, 0)));
    }

    @Test
    void limiteNoche_19_00() {
        assertEquals("Buenas noches", SaludoUtil.saludoPara(LocalTime.of(19, 0)));
    }

    @Test
    void saludoActualNoEsNull() {
        // Solo verifica que no lanza y devuelve algo no nulo
        final String saludo = SaludoUtil.saludoActual();
        org.junit.jupiter.api.Assertions.assertNotNull(saludo);
        org.junit.jupiter.api.Assertions.assertFalse(saludo.isBlank());
    }
}
