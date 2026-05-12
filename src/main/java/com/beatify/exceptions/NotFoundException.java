package com.beatify.exceptions;

public class NotFoundException extends BeatifyException {
    public NotFoundException(String mensaje) {
        super(mensaje);
    }

    public NotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
