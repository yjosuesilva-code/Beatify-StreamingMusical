package com.beatify.api.dto;

import java.util.List;

/**
 * DTO con los datos de un album obtenidos de Last.fm y/o MusicBrainz.
 * Se usa como intermediario antes de persistir en la BD via AlbumDAO.
 *
 * @param titulo             Titulo del album
 * @param artista            Nombre del artista
 * @param anioLanzamiento    Año de lanzamiento (MusicBrainz)
 * @param selloDiscografico  Sello discografico (MusicBrainz)
 * @param tipo               Tipo de release: Album, Single, EP (MusicBrainz)
 * @param portadaUrl         URL de la portada (Last.fm)
 * @param descripcion        Descripcion/resumen del album (Last.fm wiki)
 * @param generos            Lista de generos/etiquetas (Last.fm tags)
 */
public record AlbumApiDTO(
        String titulo,
        String artista,
        Integer anioLanzamiento,
        String selloDiscografico,
        String tipo,
        String portadaUrl,
        String descripcion,
        List<String> generos
) {}
