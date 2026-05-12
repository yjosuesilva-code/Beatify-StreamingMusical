package com.beatify.exceptions;

public class AutenticacionException extends BeatifyException {

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }

    public AutenticacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
