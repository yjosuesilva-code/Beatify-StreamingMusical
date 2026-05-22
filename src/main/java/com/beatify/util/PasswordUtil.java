package com.beatify.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para hashear y verificar contraseñas con BCrypt.
 *
 * BCrypt genera un salt aleatorio en cada llamada a hash(), por lo que
 * dos hashes de la misma contraseña son distintos pero ambos validos.
 * La verificacion correcta es siempre con verificar(), nunca comparando
 * los strings directamente.
 */
public class PasswordUtil {

    // Factor de coste: 12 es el estandar recomendado (balance seguridad/velocidad)
    private static final int COSTE = 12;

    /**
     * Genera un hash BCrypt seguro de la contraseña en texto plano.
     *
     * @param passwordPlano contraseña sin hashear
     * @return hash BCrypt listo para guardar en BD
     */
    public static String hash(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt(COSTE));
    }

    /**
     * Verifica si una contraseña en texto plano coincide con su hash BCrypt.
     *
     * @param passwordPlano  contraseña ingresada por el usuario
     * @param hashAlmacenado hash guardado en BD
     * @return true si coinciden, false si no
     */
    public static boolean verificar(String passwordPlano, String hashAlmacenado) {
        return BCrypt.checkpw(passwordPlano, hashAlmacenado);
    }
}
