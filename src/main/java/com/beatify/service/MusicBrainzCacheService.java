package com.beatify.service;

import com.beatify.api.MusicBrainzClient;
import com.beatify.api.dto.ArtistaApiDTO;
import com.beatify.dao.ApiCallLogDAO;
import com.beatify.dao.CacheMusicBrainzArtistaDAO;
import com.beatify.exceptions.ApiException;
import com.beatify.exceptions.CacheException;
import com.beatify.model.ApiCallLog;
import com.beatify.model.CacheMusicBrainzArtista;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementacion del cache para MusicBrainz.
 *
 * Algoritmo (cache-aside con stale-while-error):
 *   1. Buscar en cache por nombre. Si vigente (no expirado) -> devolver de cache.
 *   2. Pegar la API. Si exito -> persistir cache (insert o update) + log + devolver DTO.
 *   3. Si la API falla y hay cache previo -> devolver cache stale + log fallo + intentos++.
 *   4. Si la API falla y no hay cache -> propagar ApiException.
 *
 * TTL: 7 dias. Datos de MusicBrainz (pais, anio fundacion) son estables.
 *
 * Concurrencia: no hay locking. Dos hilos pidiendo el mismo artista pueden
 * pegar la API ambos. Aceptable para el proyecto academico (TODO en plan).
 */
public class MusicBrainzCacheService implements IMusicBrainzCacheService {

    private static final Logger LOGGER = Logger.getLogger(MusicBrainzCacheService.class.getName());

    private static final int TTL_DIAS = 7;
    private static final String API_NAME = "MUSICBRAINZ";
    private static final String ENDPOINT = "artist";
    private static final String METODO_HTTP = "GET";

    private final MusicBrainzClient client;
    private final CacheMusicBrainzArtistaDAO cacheDAO;
    private final ApiCallLogDAO apiLogDAO;
    private final ObjectMapper objectMapper;

    public MusicBrainzCacheService(MusicBrainzClient client,
                                   CacheMusicBrainzArtistaDAO cacheDAO,
                                   ApiCallLogDAO apiLogDAO) {
        this.client = Objects.requireNonNull(client, "client requerido");
        this.cacheDAO = Objects.requireNonNull(cacheDAO, "cacheDAO requerido");
        this.apiLogDAO = Objects.requireNonNull(apiLogDAO, "apiLogDAO requerido");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ArtistaApiDTO obtenerArtista(String nombreArtistico) {
        validarNombre(nombreArtistico);

        CacheMusicBrainzArtista cache = cacheDAO.buscarPorNombreCompleto(nombreArtistico);
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Cache hit vigente
        if (cache != null && cache.getExpiresAt().isAfter(ahora)) {
            LOGGER.fine(() -> "Cache HIT MusicBrainz: " + nombreArtistico);
            return deserializar(cache.getPayloadJson());
        }

        // 2. Cache miss o expirado: pegar API
        long inicio = System.currentTimeMillis();
        try {
            ArtistaApiDTO dto = client.buscarArtista(nombreArtistico);
            long duracion = System.currentTimeMillis() - inicio;

            persistirCache(cache, dto, ahora);
            logExito(nombreArtistico, duracion, ahora);

            LOGGER.fine(() -> "Cache MISS MusicBrainz, API exitosa para: " + nombreArtistico);
            return dto;

        } catch (ApiException e) {
            long duracion = System.currentTimeMillis() - inicio;
            logFallo(nombreArtistico, duracion, ahora, e);

            // 3. Stale-while-error: hay cache previo (aunque vencido) -> devolverlo
            if (cache != null) {
                LOGGER.log(Level.WARNING,
                        "API MusicBrainz fallo, devolviendo cache stale para: " + nombreArtistico, e);
                int intentosActuales = cache.getIntentos() != null ? cache.getIntentos() : 1;
                cache.setIntentos(intentosActuales + 1);
                cacheDAO.actualizar(cache);
                return deserializar(cache.getPayloadJson());
            }

            // 4. No hay cache previo -> propagar
            throw e;
        }
    }

    // ---------- Persistencia del cache ----------

    private void persistirCache(CacheMusicBrainzArtista existente,
                                ArtistaApiDTO dto,
                                LocalDateTime ahora) {
        String payload = serializar(dto);
        LocalDateTime expira = ahora.plusDays(TTL_DIAS);

        if (existente == null) {
            CacheMusicBrainzArtista nuevo = new CacheMusicBrainzArtista();
            nuevo.setMbid(dto.mbid());
            nuevo.setIdArtista(null);          // FK opcional; service no toca ARTISTA local
            nuevo.setNombreCompleto(dto.nombre());
            nuevo.setBiografia(dto.biografia());
            nuevo.setPaisOrigen(dto.pais());
            nuevo.setYearInicio(null);         // el DTO actual no expone año
            nuevo.setYearFin(null);
            nuevo.setTags(unirTags(dto.generos()));
            nuevo.setPayloadJson(payload);
            nuevo.setFetchedAt(ahora);
            nuevo.setExpiresAt(expira);
            nuevo.setIntentos(1);
            cacheDAO.insertar(nuevo);
        } else {
            // Refresh: mantiene id y FK existentes
            if (dto.mbid() != null) {
                existente.setMbid(dto.mbid());  // el DTO pudo no traer mbid
            }
            existente.setNombreCompleto(dto.nombre());
            existente.setBiografia(dto.biografia());
            existente.setPaisOrigen(dto.pais());
            existente.setTags(unirTags(dto.generos()));
            existente.setPayloadJson(payload);
            existente.setFetchedAt(ahora);
            existente.setExpiresAt(expira);
            existente.setIntentos(1);          // reset al refrescar exitosamente
            cacheDAO.actualizar(existente);
        }
    }

    // ---------- Logging de llamadas externas ----------

    private void logExito(String nombre, long duracionMs, LocalDateTime fechaLlamada) {
        ApiCallLog log = new ApiCallLog();
        log.setApiName(API_NAME);
        log.setEndpoint(ENDPOINT);
        log.setMetodoHttp(METODO_HTTP);
        log.setParametros("query=" + nombre);
        log.setCodigoRespuesta(200);
        log.setDuracionMs((int) duracionMs);
        log.setFechaLlamada(fechaLlamada);
        log.setExitoso("S");
        log.setMensajeError(null);
        log.setIdCliente(null);  // TODO: cuando UI integre auth, pasar id_cliente
        apiLogDAO.insertar(log);
    }

    private void logFallo(String nombre, long duracionMs, LocalDateTime fechaLlamada, ApiException e) {
        ApiCallLog log = new ApiCallLog();
        log.setApiName(API_NAME);
        log.setEndpoint(ENDPOINT);
        log.setMetodoHttp(METODO_HTTP);
        log.setParametros("query=" + nombre);
        log.setCodigoRespuesta(null);  // ApiException no expone codigo HTTP
        log.setDuracionMs((int) duracionMs);
        log.setFechaLlamada(fechaLlamada);
        log.setExitoso("N");
        log.setMensajeError(recortar(e.getMessage(), 1000));
        log.setIdCliente(null);
        apiLogDAO.insertar(log);
    }

    // ---------- Helpers de serializacion y validacion ----------

    private void validarNombre(String nombre) {
        Objects.requireNonNull(nombre, "nombreArtistico no puede ser null");
        if (nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("nombreArtistico no puede ser vacio");
        }
    }

    private String serializar(ArtistaApiDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new CacheException("Error serializando ArtistaApiDTO a payload_json", e);
        }
    }

    private ArtistaApiDTO deserializar(String json) {
        try {
            return objectMapper.readValue(json, ArtistaApiDTO.class);
        } catch (JsonProcessingException e) {
            throw new CacheException("Error deserializando payload_json a ArtistaApiDTO", e);
        }
    }

    private String unirTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return null;
        String unido = String.join(",", tags);
        return recortar(unido, 500);
    }

    private String recortar(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}