package com.beatify.exceptions;

/**
 * Error en la capa de cache (serializacion/deserializacion de payload,
 * fallos al guardar o leer una entrada de cache que no sean de conexion).

 * Distinto de ConexionException (errores de BD) y ApiException (errores
 * de API externa). Aqui caen los errores internos del cache mismo —
 * tipicamente fallos de Jackson al convertir DTO <-> payload_json.
 */
public class CacheException extends BeatifyException {

    public CacheException(String mensaje) {
        super(mensaje);
    }

    public CacheException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}