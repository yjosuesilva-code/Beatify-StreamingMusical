package com.beatify.service;

import com.beatify.model.Album;
import com.beatify.model.Artista;
import com.beatify.model.Genero;

/**
 * Contrato del servicio de enriquecimiento de datos musicales.
 * Combina Last.fm y MusicBrainz para poblar la BD con informacion real.
 */
public interface IEnriquecimientoService {

    /**
     * Busca un artista en Last.fm y MusicBrainz, fusiona los datos,
     * persiste el artista en BD y vincula automaticamente sus generos.
     *
     * @param nombreArtistico nombre a buscar en las APIs
     * @return Artista guardado con su id generado
     */
    Artista enriquecerArtista(String nombreArtistico);

    /**
     * Busca un album en Last.fm y MusicBrainz, fusiona los datos
     * y lo persiste en BD asociado al artista indicado.
     *
     * @param nombreArtista nombre del artista (para la busqueda en APIs)
     * @param tituloAlbum   titulo del album
     * @param idArtista     id del artista ya existente en la BD
     * @return Album guardado con su id generado
     */
    Album enriquecerAlbum(String nombreArtista, String tituloAlbum, Integer idArtista);

    /**
     * Busca un genero en BD por nombre. Si no existe, lo crea.
     * Evita duplicados al registrar generos provenientes de las APIs.
     *
     * @param nombreGenero nombre del genero/tag
     * @return Genero existente o recien creado
     */
    Genero buscarOCrearGenero(String nombreGenero);
}
