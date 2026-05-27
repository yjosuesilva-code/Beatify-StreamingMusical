package com.beatify.service;

import com.beatify.api.dto.ArtistaApiDTO;

/**
 * Service de cache para MusicBrainz.
 *
 * Encapsula la logica de "mira el cache primero; si miss o vencido, pega
 * la API y persiste el resultado". El consumidor (EnriquecimientoService)
 * no conoce ni el DAO de cache ni el cliente HTTP — solo este metodo.
 *
 * TTL: 7 dias (datos de MusicBrainz son estables: bio, pais, anio).
 *
 * Comportamiento ante fallo de API:
 *  - Si hay cache stale (vencido) lo devuelve con warning + incrementa intentos.
 *  - Si no hay cache, propaga ApiException.
 */
public interface IMusicBrainzCacheService {

    /**
     * Devuelve los datos de un artista identificado por nombre artistico.
     *
     * @param nombreArtistico nombre del artista (puede venir con espacios o mayusculas;
     *                        la implementacion lo normaliza)
     * @return DTO con los datos del artista (de cache o de la API)
     * @throws com.beatify.exceptions.ApiException si la API falla y no hay cache
     */
    ArtistaApiDTO obtenerArtista(String nombreArtistico);
}