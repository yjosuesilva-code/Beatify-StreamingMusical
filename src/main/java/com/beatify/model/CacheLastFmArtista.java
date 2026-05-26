package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Cache local de datos de artistas obtenidos de la API de Last.fm.
 * Se usa para evitar pegarle a la API en cada request — TTL definido por
 * el service que la consume (LastFmCacheService).
 * Análogo a CacheMusicBrainzArtista pero con los campos específicos de
 * Last.fm (listeners, playcount, tags).
 */
public class CacheLastFmArtista {

    private Integer idCacheLfmArtista;
    private String lastfmKey;
    private Integer artistaIdArtista;     // FK opcional al ARTISTA local
    private String mbid;
    private String nombreArtistico;
    private String biografia;
    private String fotoUrl;
    private String urlLastfm;
    private Integer listeners;
    private Integer playcount;
    private String tags;
    private String payloadJson;
    private LocalDateTime fetchedAt;
    private LocalDateTime expiresAt;
    private int intentos;

    // Constructor sin id — uso: insertar() lo pasa al DAO, Oracle asigna el id
    public CacheLastFmArtista(String lastfmKey,
                              Integer artistaIdArtista,
                              String mbid,
                              String nombreArtistico,
                              String biografia,
                              String fotoUrl,
                              String urlLastfm,
                              Integer listeners,
                              Integer playcount,
                              String tags,
                              String payloadJson,
                              LocalDateTime fetchedAt,
                              LocalDateTime expiresAt,
                              int intentos) {
        this.lastfmKey = lastfmKey;
        this.artistaIdArtista = artistaIdArtista;
        this.mbid = mbid;
        this.nombreArtistico = nombreArtistico;
        this.biografia = biografia;
        this.fotoUrl = fotoUrl;
        this.urlLastfm = urlLastfm;
        this.listeners = listeners;
        this.playcount = playcount;
        this.tags = tags;
        this.payloadJson = payloadJson;
        this.fetchedAt = fetchedAt;
        this.expiresAt = expiresAt;
        this.intentos = intentos;
    }

    // Constructor con id — uso: mapeo desde ResultSet en buscarPor*/listar
    public CacheLastFmArtista(Integer idCacheLfmArtista,
                              String lastfmKey,
                              Integer artistaIdArtista,
                              String mbid,
                              String nombreArtistico,
                              String biografia,
                              String fotoUrl,
                              String urlLastfm,
                              Integer listeners,
                              Integer playcount,
                              String tags,
                              String payloadJson,
                              LocalDateTime fetchedAt,
                              LocalDateTime expiresAt,
                              int intentos) {
        this(lastfmKey, artistaIdArtista, mbid, nombreArtistico, biografia,
                fotoUrl, urlLastfm, listeners, playcount, tags, payloadJson,
                fetchedAt, expiresAt, intentos);
        this.idCacheLfmArtista = idCacheLfmArtista;
    }

    // --- Getters ---
    public Integer getIdCacheLfmArtista() { return idCacheLfmArtista; }
    public String getLastfmKey() { return lastfmKey; }
    public Integer getArtistaIdArtista() { return artistaIdArtista; }
    public String getMbid() { return mbid; }
    public String getNombreArtistico() { return nombreArtistico; }
    public String getBiografia() { return biografia; }
    public String getFotoUrl() { return fotoUrl; }
    public String getUrlLastfm() { return urlLastfm; }
    public Integer getListeners() { return listeners; }
    public Integer getPlaycount() { return playcount; }
    public String getTags() { return tags; }
    public String getPayloadJson() { return payloadJson; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public int getIntentos() { return intentos; }

    // --- Setters ---
    public void setIdCacheLfmArtista(Integer idCacheLfmArtista) { this.idCacheLfmArtista = idCacheLfmArtista; }
    public void setLastfmKey(String lastfmKey) { this.lastfmKey = lastfmKey; }
    public void setArtistaIdArtista(Integer artistaIdArtista) { this.artistaIdArtista = artistaIdArtista; }
    public void setMbid(String mbid) { this.mbid = mbid; }
    public void setNombreArtistico(String nombreArtistico) { this.nombreArtistico = nombreArtistico; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public void setUrlLastfm(String urlLastfm) { this.urlLastfm = urlLastfm; }
    public void setListeners(Integer listeners) { this.listeners = listeners; }
    public void setPlaycount(Integer playcount) { this.playcount = playcount; }
    public void setTags(String tags) { this.tags = tags; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setIntentos(int intentos) { this.intentos = intentos; }

    // --- equals / hashCode por id ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheLastFmArtista)) return false;
        CacheLastFmArtista that = (CacheLastFmArtista) o;
        return Objects.equals(idCacheLfmArtista, that.idCacheLfmArtista);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCacheLfmArtista);
    }

    // --- toString ---
    @Override
    public String toString() {
        return "CacheLastFmArtista{" +
                "idCacheLfmArtista=" + idCacheLfmArtista +
                ", lastfmKey='" + lastfmKey + '\'' +
                ", nombreArtistico='" + nombreArtistico + '\'' +
                ", listeners=" + listeners +
                ", playcount=" + playcount +
                ", fetchedAt=" + fetchedAt +
                ", expiresAt=" + expiresAt +
                ", intentos=" + intentos +
                '}';
    }
}