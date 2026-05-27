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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Cliente HTTP para la API de Last.fm.
 * Documentacion: https://www.last.fm/api
 *
 * Operaciones disponibles:
 *  - buscarArtista(nombre)  -> biografia, foto, generos, similares
 *  - buscarAlbum(artista, album) -> portada, descripcion, generos
 */
public class LastFmClient {

    private static final String FORMATO = "&format=json";

    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // -------------------------------------------------------------------------
    // Constructor: carga api.properties
    // -------------------------------------------------------------------------

    public LastFmClient() {
        Properties props = cargarPropiedades();
        this.apiKey   = props.getProperty("lastfm.api.key");
        this.baseUrl  = props.getProperty("lastfm.api.url");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // -------------------------------------------------------------------------
    // Métodos públicos
    // -------------------------------------------------------------------------

    /**
     * Busca informacion de un artista por nombre.
     * Retorna biografia, URL de foto, generos y artistas similares.
     *
     * @param nombre nombre artistico a buscar
     * @return ArtistaApiDTO con los datos encontrados
     * @throws ApiException si la peticion falla o el artista no existe
     */
    public ArtistaApiDTO buscarArtista(String nombre) {
        String url = baseUrl
                + "?method=artist.getinfo"
                + "&artist=" + encodar(nombre)
                + "&api_key=" + apiKey
                + FORMATO;

        JsonNode raiz = ejecutarGet(url, "artista");

        JsonNode artista = raiz.path("artist");
        if (artista.isMissingNode()) {
            throw new ApiException("Last.fm no encontró al artista: " + nombre);
        }
// Last.fm también devuelve mbid cuando lo conoce; vacio si no
        String mbid = artista.path("mbid").asText("").trim();
        if (mbid.isBlank()) {
            mbid = null;
        }
        String bio       = artista.path("bio").path("summary").asText("").trim();
        // Limpiar el enlace HTML que Last.fm agrega al final de la bio
        if (bio.contains("<a href")) {
            bio = bio.substring(0, bio.indexOf("<a href")).trim();
        }

        String fotoUrl   = extraerImagenGrande(artista.path("image"));
        List<String> generos   = extraerTags(artista.path("tags").path("tag"));
        List<String> similares = extraerNombres(artista.path("similar").path("artist"));

        return new ArtistaApiDTO(mbid, nombre, null, bio, fotoUrl, generos, similares);    }

    /**
     * Busca informacion de un album por nombre de artista y titulo.
     * Retorna portada, descripcion y generos.
     *
     * @param artista nombre del artista
     * @param album   titulo del album
     * @return AlbumApiDTO con los datos encontrados
     * @throws ApiException si la peticion falla o el album no existe
     */
    public AlbumApiDTO buscarAlbum(String artista, String album) {
        String url = baseUrl
                + "?method=album.getinfo"
                + "&artist=" + encodar(artista)
                + "&album="  + encodar(album)
                + "&api_key=" + apiKey
                + FORMATO;

        JsonNode raiz = ejecutarGet(url, "album");

        JsonNode nodoAlbum = raiz.path("album");
        if (nodoAlbum.isMissingNode()) {
            throw new ApiException("Last.fm no encontró el album: " + album + " de " + artista);
        }

        String portadaUrl  = extraerImagenGrande(nodoAlbum.path("image"));
        List<String> generos = extraerTags(nodoAlbum.path("tags").path("tag"));

        String descripcion = nodoAlbum.path("wiki").path("summary").asText("").trim();
        if (descripcion.contains("<a href")) {
            descripcion = descripcion.substring(0, descripcion.indexOf("<a href")).trim();
        }

        return new AlbumApiDTO(album, artista, null, null, null, portadaUrl, descripcion, generos);
    }

    // -------------------------------------------------------------------------
    // Métodos privados de apoyo
    // -------------------------------------------------------------------------

    /** Ejecuta GET al URL dado y devuelve el nodo raiz del JSON. */
    private JsonNode ejecutarGet(String url, String contexto) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(15))
                .build();
        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException(
                        "Last.fm respondio " + response.statusCode() + " al buscar " + contexto);
            }

            JsonNode raiz = objectMapper.readTree(response.body());

            // Last.fm retorna errores en el campo "error"
            if (raiz.has("error")) {
                throw new ApiException(
                        "Last.fm error " + raiz.path("error").asInt()
                        + ": " + raiz.path("message").asText());
            }

            return raiz;

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error de red al llamar Last.fm: " + e.getMessage(), e);
        }
    }

    /** Extrae la imagen de mayor tamaño del array de imagenes de Last.fm. */
    private String extraerImagenGrande(JsonNode imageArray) {
        if (imageArray == null || !imageArray.isArray() || imageArray.isEmpty()) {
            return null;
        }
        // Last.fm devuelve las imagenes ordenadas de menor a mayor — la última es la más grande
        String url = imageArray.get(imageArray.size() - 1).path("#text").asText("").trim();
        return url.isEmpty() ? null : url;
    }

    /** Extrae nombres de tags/generos de un nodo JSON de Last.fm. */
    private List<String> extraerTags(JsonNode tagsNode) {
        if (tagsNode == null || tagsNode.isMissingNode()) return Collections.emptyList();
        List<String> lista = new ArrayList<>();
        if (tagsNode.isArray()) {
            tagsNode.forEach(t -> lista.add(t.path("name").asText()));
        } else if (tagsNode.isObject()) {
            // Cuando hay un solo tag Last.fm lo devuelve como objeto, no array
            lista.add(tagsNode.path("name").asText());
        }
        return lista;
    }

    /** Extrae lista de nombres de artistas similares. */
    private List<String> extraerNombres(JsonNode artistasNode) {
        if (artistasNode == null || artistasNode.isMissingNode()) return Collections.emptyList();
        List<String> lista = new ArrayList<>();
        if (artistasNode.isArray()) {
            artistasNode.forEach(a -> lista.add(a.path("name").asText()));
        }
        return lista;
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
