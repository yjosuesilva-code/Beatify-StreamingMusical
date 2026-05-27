package com.beatify.service;

import com.beatify.api.dto.AlbumApiDTO;
import com.beatify.api.dto.ArtistaApiDTO;

/**
 * Service de cache para Last.fm.
 *
 * Maneja dos tipos de objeto: artista (via CACHE_LASTFM_ARTISTA) y album
 * (via CACHE_LASTFM_ALBUM). Mismo patron de cache-then-fetch que MusicBrainz.
 *
 * TTL: 3 dias (listeners/playcount cambian mas seguido que datos biograficos).
 *
 * Comportamiento ante fallo de API: igual que MusicBrainz cache — stale-while-error.
 */
public interface ILastFmCacheService {

    /**
     * Devuelve los datos de un artista identificado por nombre artistico.
     */
    ArtistaApiDTO obtenerArtista(String nombreArtistico);

    /**
     * Devuelve los datos de un album identificado por artista + titulo.
     * La clave de cache se construye como artista|titulo normalizado.
     */
    AlbumApiDTO obtenerAlbum(String nombreArtista, String tituloAlbum);
}