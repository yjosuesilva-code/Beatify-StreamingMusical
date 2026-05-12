package com.beatify.exceptions;

public class ValidacionException extends BeatifyException {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }

    public ValidacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
