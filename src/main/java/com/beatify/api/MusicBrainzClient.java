package com.beatify.api;

import com.beatify.api.dto.AlbumApiDTO;
import com.beatify.api.dto.ArtistaApiDTO;
import com.beatify.exceptions.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Cliente HTTP para la API de MusicBrainz.
 * Documentacion: https://musicbrainz.org/doc/MusicBrainz_API
 *
 * MusicBrainz no requiere API key, pero exige un User-Agent descriptivo
 * para identificar la aplicacion. Se respeta el limite de 1 req/seg.
 *
 * Operaciones disponibles:
 *  - buscarArtista(nombre)  -> pais de origen
 *  - buscarAlbum(artista, album) -> año, sello discografico, tipo de release
 */
public class MusicBrainzClient {

    private final String baseUrl;
    private final String userAgent;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // -------------------------------------------------------------------------
    // Constructor: carga api.properties
    // -------------------------------------------------------------------------

    public MusicBrainzClient() {
        Properties props = cargarPropiedades();
        this.baseUrl   = props.getProperty("musicbrainz.api.url");
        this.userAgent = props.getProperty("musicbrainz.user.agent");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------------------------
    // Métodos públicos
    // -------------------------------------------------------------------------

    /**
     * Busca informacion basica de un artista en MusicBrainz.
     * Retorna pais de origen (el resto de campos viene de Last.fm).
     *
     * @param nombre nombre artistico a buscar
     * @return ArtistaApiDTO con pais (los demas campos son null)
     * @throws ApiException si la peticion falla
     */
    public ArtistaApiDTO buscarArtista(String nombre) {
        String url = baseUrl + "artist?query=" + encodar(nombre) + "&limit=1&fmt=json";

        JsonNode raiz = ejecutarGet(url, "artista");

        JsonNode artistas = raiz.path("artists");
        if (!artistas.isArray() || artistas.isEmpty()) {
            throw new ApiException("MusicBrainz no encontró al artista: " + nombre);
        }

        JsonNode primero = artistas.get(0);
        String pais = primero.path("country").asText(null);

        return new ArtistaApiDTO(nombre, pais, null, null,
                Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Busca informacion de un album (release-group) en MusicBrainz.
     * Retorna año de lanzamiento, sello discografico y tipo de release.
     *
     * @param artista nombre del artista
     * @param album   titulo del album
     * @return AlbumApiDTO con anioLanzamiento, selloDiscografico y tipo
     * @throws ApiException si la peticion falla
     */
    public AlbumApiDTO buscarAlbum(String artista, String album) {
        String query = "release:" + encodar(album) + "%20AND%20artist:" + encodar(artista);
        String url   = baseUrl + "release?query=" + query + "&limit=1&fmt=json";

        JsonNode raiz = ejecutarGet(url, "album");

        JsonNode releases = raiz.path("releases");
        if (!releases.isArray() || releases.isEmpty()) {
            throw new ApiException("MusicBrainz no encontró el album: " + album);
        }

        JsonNode release = releases.get(0);

        // Año de lanzamiento desde la fecha (formato "YYYY-MM-DD" o solo "YYYY")
        Integer anio = null;
        String fecha = release.path("date").asText(null);
        if (fecha != null && !fecha.isBlank()) {
            try {
                anio = Integer.parseInt(fecha.substring(0, 4));
            } catch (NumberFormatException ignored) {
                // La fecha no tiene formato esperado; dejamos anio en null
            }
        }

        // Sello: primer label-info disponible
        String sello = null;
        JsonNode labelInfoList = release.path("label-info");
        if (labelInfoList.isArray() && !labelInfoList.isEmpty()) {
            sello = labelInfoList.get(0).path("label").path("name").asText(null);
        }

        // Tipo de release: Album, Single, EP, etc.
        String tipo = release.path("release-group").path("primary-type").asText(null);

        return new AlbumApiDTO(album, artista, anio, sello, tipo,
                null, null, Collections.emptyList());
    }

    // -------------------------------------------------------------------------
    // Métodos privados de apoyo
    // -------------------------------------------------------------------------

    /**
     * Ejecuta GET al URL dado respetando el limite de MusicBrainz (1 req/seg).
     * Devuelve el nodo raiz del JSON de la respuesta.
     */
    private JsonNode ejecutarGet(String url, String contexto) {
        // MusicBrainz pide maximo 1 peticion por segundo
        esperarUnSegundo();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", userAgent)
                .GET()
                .timeout(Duration.ofSeconds(15))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 503) {
                throw new ApiException(
                        "MusicBrainz: limite de tasa excedido. Intentalo en unos segundos.");
            }
            if (response.statusCode() != 200) {
                throw new ApiException(
                        "MusicBrainz respondio " + response.statusCode()
                        + " al buscar " + contexto);
            }

            return objectMapper.readTree(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error de red al llamar MusicBrainz: " + e.getMessage(), e);
        }
    }

    /** Pausa de 1 segundo para respetar el rate limit de MusicBrainz. */
    private void esperarUnSegundo() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Codifica un parametro para incluirlo en la URL. */
    private String encodar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    /** Carga api.properties desde el classpath. */
    private Properties cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("api.properties")) {
            if (is == null) {
                throw new ApiException("No se encontro api.properties en resources/");
            }
            props.load(is);
        } catch (IOException e) {
            throw new ApiException("Error leyendo api.properties: " + e.getMessage(), e);
        }
        return props;
    }
}
