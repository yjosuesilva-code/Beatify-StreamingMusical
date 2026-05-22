package com.beatify.exceptions;

/**
 * Excepcion lanzada cuando ocurre un error al llamar una API externa
 * (Last.fm, MusicBrainz). Puede indicar error de red, respuesta HTTP
 * no exitosa o fallo al parsear el JSON.
 */
public class ApiException extends BeatifyException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
