package com.beatify.api.dto;

import java.util.List;

/**
 * DTO con los datos de un artista obtenidos de Last.fm y/o MusicBrainz.
 * Se usa como intermediario antes de persistir en la BD via ArtistaDAO.
 *
 * @param mbid             MusicBrainz ID (UUID). PK natural en MB; null si solo viene de Last.fm sin mbid
 * @param nombre           Nombre artistico del artista
 * @param pais             Pais de origen (MusicBrainz)
 * @param biografia        Biografia resumida (Last.fm)
 * @param fotoUrl          URL de la imagen del artista (Last.fm)
 * @param generos          Lista de generos/etiquetas (Last.fm tags)
 * @param artistasSimilares Nombres de artistas similares (Last.fm)
 */
public record ArtistaApiDTO(
        String mbid,
        String nombre,
        String pais,
        String biografia,
        String fotoUrl,
        List<String> generos,
        List<String> artistasSimilares
) {}