package com.beatify.exceptions;

public abstract class BeatifyException extends RuntimeException {
    // Errores de validacion o logica de negocio donde no hay excepcion previa que envolver
    public BeatifyException(String mensaje){
        super(mensaje);
    }
    //Para una SQLExcepcion o similar
    public BeatifyException(String mensaje, Throwable causa){
        super(mensaje,causa);
    }
}
