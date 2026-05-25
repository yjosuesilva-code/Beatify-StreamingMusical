package com.beatify.view.util;

import java.time.LocalTime;

/**
 * Devuelve un saludo apropiado para la hora actual del usuario.
 * Lo usa el Home para mostrar "Buenos dias, Yilver" / "Buenas tardes, ..."
 * en el hero principal.
 *
 * Franjas:
 *   00:00 - 05:59  -> "Buenas madrugadas"
 *   06:00 - 11:59  -> "Buenos dias"
 *   12:00 - 18:59  -> "Buenas tardes"
 *   19:00 - 23:59  -> "Buenas noches"
 */
public final class SaludoUtil {

    private SaludoUtil() {
    }

    /** Devuelve el saludo segun la hora del sistema (ej: "Buenas tardes"). */
    public static String saludoActual() {
        return saludoPara(LocalTime.now());
    }

    /**
     * Variante testeable: permite pasar la hora especifica para verificar
     * los limites de franja sin depender del reloj del sistema.
     */
    public static String saludoPara(final LocalTime hora) {
        final int h = hora.getHour();
        if (h < 6)  return "Buenas madrugadas";
        if (h < 12) return "Buenos dias";
        if (h < 19) return "Buenas tardes";
        return "Buenas noches";
    }
}
