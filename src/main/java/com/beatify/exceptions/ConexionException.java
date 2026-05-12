package com.beatify.exceptions;

public class ConexionException extends BeatifyException {
    public ConexionException(String mensaje) {
        super(mensaje);
    }

    public ConexionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
