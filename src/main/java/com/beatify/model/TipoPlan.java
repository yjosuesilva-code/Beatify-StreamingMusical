package com.beatify.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Catalogo de planes de suscripcion de Beatify y la diferencia funcional de
 * cada uno. Es la unica fuente de verdad para precios y caracteristicas:
 * tanto el {@code SuscripcionService} (validacion) como la UI de registro
 * y el {@code PlayerManager} (limite de saltos, anuncios) derivan de aqui,
 * de modo que los planes no se desincronicen entre capas.
 *
 * El {@code nombre} coincide exactamente con el CHECK de la BD
 * (SUSCRIPCION_TIPO_PLAN_CK) y con el valor que se guarda en
 * {@code Suscripcion.tipoPlan}.
 */
public enum TipoPlan {

    //         nombre        etiqueta       precio  perfiles  frecAnuncio descargas maxSaltos/h calidad               simult verif  vence
    FREE      ("FREE",       "Gratis",          0.00, 1,      4,          false,    6,          CalidadAudio.ESTANDAR, 1,    false, false),
    ESTUDIANTE("ESTUDIANTE", "Estudiante",   5990.00, 1,      0,          true,     -1,         CalidadAudio.ALTA,     1,    true,  true),
    INDIVIDUAL("INDIVIDUAL", "Individual",  14900.00, 1,      0,          true,     -1,         CalidadAudio.ALTA,     1,    false, true),
    DUO       ("DUO",        "Dúo",         19900.00, 2,      0,          true,     -1,         CalidadAudio.ALTA,     2,    false, true),
    FAMILIAR  ("FAMILIAR",   "Familiar",    25900.00, 6,      0,          true,     -1,         CalidadAudio.ALTA,     6,    false, true);

    /** Calidad de reproduccion de audio ofrecida por el plan. */
    public enum CalidadAudio {
        ESTANDAR("Estándar"),
        ALTA("Alta");

        private final String etiqueta;

        CalidadAudio(final String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public String getEtiqueta() {
            return this.etiqueta;
        }
    }

    private final String nombre;
    private final String etiqueta;
    private final double precioMensual;
    private final int perfiles;
    private final int frecuenciaAnuncioCanciones;
    private final boolean descargasOffline;
    private final int maxSaltosPorHora;
    private final CalidadAudio calidadAudio;
    private final int reproduccionesSimultaneas;
    private final boolean requiereVerificacion;
    private final boolean venceMensual;

    TipoPlan(final String nombre, final String etiqueta, final double precioMensual,
             final int perfiles, final int frecuenciaAnuncioCanciones,
             final boolean descargasOffline, final int maxSaltosPorHora,
             final CalidadAudio calidadAudio, final int reproduccionesSimultaneas,
             final boolean requiereVerificacion, final boolean venceMensual) {
        this.nombre = nombre;
        this.etiqueta = etiqueta;
        this.precioMensual = precioMensual;
        this.perfiles = perfiles;
        this.frecuenciaAnuncioCanciones = frecuenciaAnuncioCanciones;
        this.descargasOffline = descargasOffline;
        this.maxSaltosPorHora = maxSaltosPorHora;
        this.calidadAudio = calidadAudio;
        this.reproduccionesSimultaneas = reproduccionesSimultaneas;
        this.requiereVerificacion = requiereVerificacion;
        this.venceMensual = venceMensual;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getEtiqueta() {
        return this.etiqueta;
    }

    public double getPrecioMensual() {
        return this.precioMensual;
    }

    public int getPerfiles() {
        return this.perfiles;
    }

    public int getFrecuenciaAnuncioCanciones() {
        return this.frecuenciaAnuncioCanciones;
    }

    /** {@code true} si la reproduccion incluye anuncios. */
    public boolean isConAnuncios() {
        return this.frecuenciaAnuncioCanciones > 0;
    }

    public boolean isDescargasOffline() {
        return this.descargasOffline;
    }

    public int getMaxSaltosPorHora() {
        return this.maxSaltosPorHora;
    }

    /** {@code true} si los saltos manuales son ilimitados. */
    public boolean isSaltosIlimitados() {
        return this.maxSaltosPorHora < 0;
    }

    public CalidadAudio getCalidadAudio() {
        return this.calidadAudio;
    }

    public int getReproduccionesSimultaneas() {
        return this.reproduccionesSimultaneas;
    }

    public boolean isRequiereVerificacion() {
        return this.requiereVerificacion;
    }

    public boolean isVenceMensual() {
        return this.venceMensual;
    }

    /** {@code true} si es un plan de pago (precio mayor que cero). */
    public boolean esDePago() {
        return this.precioMensual > 0.0;
    }

    /**
     * Resuelve el plan a partir de su nombre (sin distinguir mayusculas).
     *
     * @throws IllegalArgumentException si el nombre no corresponde a ningun plan
     */
    public static TipoPlan desdeNombre(final String nombre) {
        if (nombre != null) {
            final String objetivo = nombre.trim().toUpperCase();
            for (final TipoPlan plan : values()) {
                if (plan.nombre.equals(objetivo)) {
                    return plan;
                }
            }
        }
        throw new IllegalArgumentException("Tipo de plan desconocido: " + nombre);
    }

    /**
     * Igual que {@link #desdeNombre(String)} pero devuelve {@link #FREE} en vez
     * de lanzar cuando el nombre es nulo o desconocido. Util para resolver el
     * plan efectivo de un cliente sin propagar errores a la UI.
     */
    public static TipoPlan desdeNombreOFree(final String nombre) {
        return esValido(nombre) ? desdeNombre(nombre) : FREE;
    }

    /** {@code true} si {@code nombre} corresponde a un plan valido. */
    public static boolean esValido(final String nombre) {
        if (nombre == null) {
            return false;
        }
        final String objetivo = nombre.trim().toUpperCase();
        return Arrays.stream(values()).anyMatch(plan -> plan.nombre.equals(objetivo));
    }

    /** Conjunto de nombres de plan validos (preserva el orden de declaracion). */
    public static Set<String> nombresValidos() {
        return Arrays.stream(values())
                .map(TipoPlan::getNombre)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
