package com.beatify.service;

import com.beatify.api.LastFmClient;
import com.beatify.api.dto.AlbumApiDTO;
import com.beatify.api.dto.ArtistaApiDTO;
import com.beatify.dao.ApiCallLogDAO;
import com.beatify.dao.CacheLastFmAlbumDAO;
import com.beatify.dao.CacheLastFmArtistaDAO;
import com.beatify.exceptions.ApiException;
import com.beatify.exceptions.CacheException;
import com.beatify.model.ApiCallLog;
import com.beatify.model.CacheLastFmAlbum;
import com.beatify.model.CacheLastFmArtista;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementacion del cache para Last.fm.
 *
 * Cubre dos tipos de objeto: ARTISTA (via CACHE_LASTFM_ARTISTA) y ALBUM
 * (via CACHE_LASTFM_ALBUM). Ambos cachean por lastfm_key normalizada
 * (UK en la tabla) — lookup directo, sin necesidad de scan por nombre.
 *
 * Algoritmo (cache-aside con stale-while-error):
 *   1. Normalizar clave + buscarPorLastfmKey.
 *   2. Si vigente -> devolver de cache.
 *   3. Si miss/expirado -> pegar API, persistir cache, log.
 *   4. Si API falla y hay cache stale -> devolverlo + intentos++.
 *   5. Si API falla sin cache -> propagar ApiException.
 *
 * TTL: 3 dias. Last.fm tiene listeners/playcount que cambian a diario.
 */
public class LastFmCacheService implements ILastFmCacheService {

    private static final Logger LOGGER = Logger.getLogger(LastFmCacheService.class.getName());

    private static final int TTL_DIAS = 3;
    private static final String API_NAME = "LASTFM";
    private static final String ENDPOINT_ARTISTA = "artist.getinfo";
    private static final String ENDPOINT_ALBUM = "album.getinfo";
    private static final String METODO_HTTP = "GET";

    private final LastFmClient client;
    private final CacheLastFmArtistaDAO artistaCacheDAO;
    private final CacheLastFmAlbumDAO albumCacheDAO;
    private final ApiCallLogDAO apiLogDAO;
    private final ObjectMapper objectMapper;

    public LastFmCacheService(LastFmClient client,
                              CacheLastFmArtistaDAO artistaCacheDAO,
                              CacheLastFmAlbumDAO albumCacheDAO,
                              ApiCallLogDAO apiLogDAO) {
        this.client = Objects.requireNonNull(client, "client requerido");
        this.artistaCacheDAO = Objects.requireNonNull(artistaCacheDAO, "artistaCacheDAO requerido");
        this.albumCacheDAO = Objects.requireNonNull(albumCacheDAO, "albumCacheDAO requerido");
        this.apiLogDAO = Objects.requireNonNull(apiLogDAO, "apiLogDAO requerido");
        this.objectMapper = new ObjectMapper();
    }

    // ============================================================
    // ARTISTA
    // ============================================================

    @Override
    public ArtistaApiDTO obtenerArtista(String nombreArtistico) {
        validarTexto(nombreArtistico, "nombreArtistico");
        String clave = normalizarArtista(nombreArtistico);

        CacheLastFmArtista cache = artistaCacheDAO.buscarPorLastfmKey(clave);
        LocalDateTime ahora = LocalDateTime.now();

        if (cache != null && cache.getExpiresAt().isAfter(ahora)) {
            LOGGER.fine(() -> "Cache HIT Last.fm artista: " + clave);
            return deserializarArtista(cache.getPayloadJson());
        }

        long inicio = System.currentTimeMillis();
        try {
            ArtistaApiDTO dto = client.buscarArtista(nombreArtistico);
            long duracion = System.currentTimeMillis() - inicio;

            persistirArtista(cache, clave, dto, ahora);
            logExito(ENDPOINT_ARTISTA, "artist=" + nombreArtistico, duracion, ahora);

            LOGGER.fine(() -> "Cache MISS Last.fm artista, API exitosa para: " + clave);
            return dto;

        } catch (ApiException e) {
            long duracion = System.currentTimeMillis() - inicio;
            logFallo(ENDPOINT_ARTISTA, "artist=" + nombreArtistico, duracion, ahora, e);

            if (cache != null) {
                LOGGER.log(Level.WARNING,
                        "API Last.fm artista fallo, devolviendo cache stale para: " + clave, e);
                // POJO CacheLastFmArtista usa int primitive — no null check
                cache.setIntentos(cache.getIntentos() + 1);
                artistaCacheDAO.actualizar(cache);
                return deserializarArtista(cache.getPayloadJson());
            }
            throw e;
        }
    }

    private void persistirArtista(CacheLastFmArtista existente, String clave,
                                  ArtistaApiDTO dto, LocalDateTime ahora) {
        String payload = serializar(dto);
        LocalDateTime expira = ahora.plusDays(TTL_DIAS);

        if (existente == null) {
            CacheLastFmArtista nuevo = new CacheLastFmArtista(
                    clave,                       // lastfmKey
                    null,                        // artistaIdArtista (FK opcional)
                    dto.mbid(),                  // mbid
                    dto.nombre(),                // nombreArtistico
                    dto.biografia(),             // biografia
                    dto.fotoUrl(),               // fotoUrl
                    null,                        // urlLastfm (el DTO no expone)
                    null,                        // listeners
                    null,                        // playcount
                    unirTags(dto.generos()),     // tags
                    payload,                     // payloadJson
                    ahora,                       // fetchedAt
                    expira,                      // expiresAt
                    1                            // intentos
            );
            artistaCacheDAO.insertar(nuevo);
        } else {
            if (dto.mbid() != null) existente.setMbid(dto.mbid());
            existente.setNombreArtistico(dto.nombre());
            existente.setBiografia(dto.biografia());
            existente.setFotoUrl(dto.fotoUrl());
            existente.setTags(unirTags(dto.generos()));
            existente.setPayloadJson(payload);
            existente.setFetchedAt(ahora);
            existente.setExpiresAt(expira);
            existente.setIntentos(1);     // reset al refrescar
            artistaCacheDAO.actualizar(existente);
        }
    }

    // ============================================================
    // ALBUM
    // ============================================================

    @Override
    public AlbumApiDTO obtenerAlbum(String nombreArtista, String tituloAlbum) {
        validarTexto(nombreArtista, "nombreArtista");
        validarTexto(tituloAlbum, "tituloAlbum");
        String clave = normalizarAlbum(nombreArtista, tituloAlbum);

        CacheLastFmAlbum cache = albumCacheDAO.buscarPorLastfmKey(clave);
        LocalDateTime ahora = LocalDateTime.now();

        if (cache != null && cache.getExpiresAt().isAfter(ahora)) {
            LOGGER.fine(() -> "Cache HIT Last.fm album: " + clave);
            return deserializarAlbum(cache.getPayloadJson());
        }

        long inicio = System.currentTimeMillis();
        try {
            AlbumApiDTO dto = client.buscarAlbum(nombreArtista, tituloAlbum);
            long duracion = System.currentTimeMillis() - inicio;

            persistirAlbum(cache, clave, dto, ahora);
            logExito(ENDPOINT_ALBUM, "artist=" + nombreArtista + "&album=" + tituloAlbum, duracion, ahora);

            LOGGER.fine(() -> "Cache MISS Last.fm album, API exitosa para: " + clave);
            return dto;

        } catch (ApiException e) {
            long duracion = System.currentTimeMillis() - inicio;
            logFallo(ENDPOINT_ALBUM, "artist=" + nombreArtista + "&album=" + tituloAlbum, duracion, ahora, e);

            if (cache != null) {
                LOGGER.log(Level.WARNING,
                        "API Last.fm album fallo, devolviendo cache stale para: " + clave, e);
                // POJO CacheLastFmAlbum usa Integer — sí null check defensivo
                int intentos = cache.getIntentos() != null ? cache.getIntentos() : 1;
                cache.setIntentos(intentos + 1);
                albumCacheDAO.actualizar(cache);
                return deserializarAlbum(cache.getPayloadJson());
            }
            throw e;
        }
    }

    private void persistirAlbum(CacheLastFmAlbum existente, String clave,
                                AlbumApiDTO dto, LocalDateTime ahora) {
        String payload = serializar(dto);
        LocalDateTime expira = ahora.plusDays(TTL_DIAS);

        if (existente == null) {
            CacheLastFmAlbum nuevo = new CacheLastFmAlbum();
            nuevo.setLastfmKey(clave);
            nuevo.setIdAlbum(null);
            nuevo.setMbid(null);                       // AlbumApiDTO no expone mbid
            nuevo.setTitulo(dto.titulo());
            nuevo.setArtistaNombre(dto.artista());
            nuevo.setUrlLastfm(null);                  // DTO no expone url_lastfm
            nuevo.setPortadaUrlLastfm(dto.portadaUrl());
            nuevo.setListeners(null);
            nuevo.setPlaycount(null);
            nuevo.setTags(unirTags(dto.generos()));
            nuevo.setPayloadJson(payload);
            nuevo.setFetchedAt(ahora);
            nuevo.setExpiresAt(expira);
            nuevo.setIntentos(1);
            albumCacheDAO.insertar(nuevo);
        } else {
            existente.setTitulo(dto.titulo());
            existente.setArtistaNombre(dto.artista());
            existente.setPortadaUrlLastfm(dto.portadaUrl());
            existente.setTags(unirTags(dto.generos()));
            existente.setPayloadJson(payload);
            existente.setFetchedAt(ahora);
            existente.setExpiresAt(expira);
            existente.setIntentos(1);
            albumCacheDAO.actualizar(existente);
        }
    }

    // ============================================================
    // Logging
    // ============================================================

    private void logExito(String endpoint, String parametros, long duracionMs, LocalDateTime fecha) {
        ApiCallLog log = new ApiCallLog();
        log.setApiName(API_NAME);
        log.setEndpoint(endpoint);
        log.setMetodoHttp(METODO_HTTP);
        log.setParametros(parametros);
        log.setCodigoRespuesta(200);
        log.setDuracionMs((int) duracionMs);
        log.setFechaLlamada(fecha);
        log.setExitoso("S");
        log.setMensajeError(null);
        log.setIdCliente(null);
        apiLogDAO.insertar(log);
    }

    private void logFallo(String endpoint, String parametros, long duracionMs,
                          LocalDateTime fecha, ApiException e) {
        ApiCallLog log = new ApiCallLog();
        log.setApiName(API_NAME);
        log.setEndpoint(endpoint);
        log.setMetodoHttp(METODO_HTTP);
        log.setParametros(parametros);
        log.setCodigoRespuesta(null);
        log.setDuracionMs((int) duracionMs);
        log.setFechaLlamada(fecha);
        log.setExitoso("N");
        log.setMensajeError(recortar(e.getMessage(), 1000));
        log.setIdCliente(null);
        apiLogDAO.insertar(log);
    }

    // ============================================================
    // Helpers
    // ============================================================

    private void validarTexto(String valor, String nombre) {
        Objects.requireNonNull(valor, nombre + " no puede ser null");
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nombre + " no puede ser vacio");
        }
    }

    private String normalizarArtista(String nombre) {
        return nombre.trim().toLowerCase();
    }

    private String normalizarAlbum(String nombreArtista, String tituloAlbum) {
        return (nombreArtista.trim() + "|" + tituloAlbum.trim()).toLowerCase();
    }

    private String serializar(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new CacheException("Error serializando DTO Last.fm a payload_json", e);
        }
    }

    private ArtistaApiDTO deserializarArtista(String json) {
        try {
            return objectMapper.readValue(json, ArtistaApiDTO.class);
        } catch (JsonProcessingException e) {
            throw new CacheException("Error deserializando payload_json a ArtistaApiDTO", e);
        }
    }

    private AlbumApiDTO deserializarAlbum(String json) {
        try {
            return objectMapper.readValue(json, AlbumApiDTO.class);
        } catch (JsonProcessingException e) {
            throw new CacheException("Error deserializando payload_json a AlbumApiDTO", e);
        }
    }

    private String unirTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return null;
        return recortar(String.join(",", tags), 500);
    }

    private String recortar(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}