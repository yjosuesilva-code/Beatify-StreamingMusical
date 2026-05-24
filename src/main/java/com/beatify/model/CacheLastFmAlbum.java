package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Cache local de metadatos de albumes obtenidos desde Last.fm.
 * Una fila por lastfm_key (combinacion "artista::album").
 * El campo expires_at define hasta cuando es valido el cache.
 */
public class CacheLastFmAlbum {

    private Integer idCacheLfmAlbum;
    private String  lastfmKey;          // clave compuesta "artista::album" (UNIQUE)
    private Integer idAlbum;            // FK opcional a ALBUM
    private String  mbid;               // MBID de MusicBrainz para enlazar (opcional)
    private String  titulo;
    private String  artistaNombre;
    private String  urlLastfm;
    private String  portadaUrlLastfm;
    private Integer listeners;
    private Integer playcount;
    private String  tags;
    private String  payloadJson;        // CLOB con el JSON crudo de la API
    private LocalDateTime fetchedAt;
    private LocalDateTime expiresAt;
    private Integer intentos;

    public CacheLastFmAlbum() {
    }

    public CacheLastFmAlbum(final String lastfmKey, final Integer idAlbum, final String mbid,
                            final String titulo, final String artistaNombre, final String urlLastfm,
                            final String portadaUrlLastfm, final Integer listeners, final Integer playcount,
                            final String tags, final String payloadJson, final LocalDateTime fetchedAt,
                            final LocalDateTime expiresAt, final Integer intentos) {
        this.lastfmKey = lastfmKey;
        this.idAlbum = idAlbum;
        this.mbid = mbid;
        this.titulo = titulo;
        this.artistaNombre = artistaNombre;
        this.urlLastfm = urlLastfm;
        this.portadaUrlLastfm = portadaUrlLastfm;
        this.listeners = listeners;
        this.playcount = playcount;
        this.tags = tags;
        this.payloadJson = payloadJson;
        this.fetchedAt = fetchedAt;
        this.expiresAt = expiresAt;
        this.intentos = intentos;
    }

    public CacheLastFmAlbum(final Integer idCacheLfmAlbum, final String lastfmKey, final Integer idAlbum,
                            final String mbid, final String titulo, final String artistaNombre,
                            final String urlLastfm, final String portadaUrlLastfm,
                            final Integer listeners, final Integer playcount, final String tags,
                            final String payloadJson, final LocalDateTime fetchedAt,
                            final LocalDateTime expiresAt, final Integer intentos) {
        this.idCacheLfmAlbum = idCacheLfmAlbum;
        this.lastfmKey = lastfmKey;
        this.idAlbum = idAlbum;
        this.mbid = mbid;
        this.titulo = titulo;
        this.artistaNombre = artistaNombre;
        this.urlLastfm = urlLastfm;
        this.portadaUrlLastfm = portadaUrlLastfm;
        this.listeners = listeners;
        this.playcount = playcount;
        this.tags = tags;
        this.payloadJson = payloadJson;
        this.fetchedAt = fetchedAt;
        this.expiresAt = expiresAt;
        this.intentos = intentos;
    }

    public Integer getIdCacheLfmAlbum() { return this.idCacheLfmAlbum; }
    public void setIdCacheLfmAlbum(final Integer idCacheLfmAlbum) { this.idCacheLfmAlbum = idCacheLfmAlbum; }

    public String getLastfmKey() { return this.lastfmKey; }
    public void setLastfmKey(final String lastfmKey) { this.lastfmKey = lastfmKey; }

    public Integer getIdAlbum() { return this.idAlbum; }
    public void setIdAlbum(final Integer idAlbum) { this.idAlbum = idAlbum; }

    public String getMbid() { return this.mbid; }
    public void setMbid(final String mbid) { this.mbid = mbid; }

    public String getTitulo() { return this.titulo; }
    public void setTitulo(final String titulo) { this.titulo = titulo; }

    public String getArtistaNombre() { return this.artistaNombre; }
    public void setArtistaNombre(final String artistaNombre) { this.artistaNombre = artistaNombre; }

    public String getUrlLastfm() { return this.urlLastfm; }
    public void setUrlLastfm(final String urlLastfm) { this.urlLastfm = urlLastfm; }

    public String getPortadaUrlLastfm() { return this.portadaUrlLastfm; }
    public void setPortadaUrlLastfm(final String portadaUrlLastfm) { this.portadaUrlLastfm = portadaUrlLastfm; }

    public Integer getListeners() { return this.listeners; }
    public void setListeners(final Integer listeners) { this.listeners = listeners; }

    public Integer getPlaycount() { return this.playcount; }
    public void setPlaycount(final Integer playcount) { this.playcount = playcount; }

    public String getTags() { return this.tags; }
    public void setTags(final String tags) { this.tags = tags; }

    public String getPayloadJson() { return this.payloadJson; }
    public void setPayloadJson(final String payloadJson) { this.payloadJson = payloadJson; }

    public LocalDateTime getFetchedAt() { return this.fetchedAt; }
    public void setFetchedAt(final LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }

    public LocalDateTime getExpiresAt() { return this.expiresAt; }
    public void setExpiresAt(final LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getIntentos() { return this.intentos; }
    public void setIntentos(final Integer intentos) { this.intentos = intentos; }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final CacheLastFmAlbum that = (CacheLastFmAlbum) o;
        return Objects.equals(this.idCacheLfmAlbum, that.idCacheLfmAlbum);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idCacheLfmAlbum);
    }

    @Override
    public String toString() {
        return "CacheLastFmAlbum{" +
                "idCacheLfmAlbum=" + idCacheLfmAlbum +
                ", lastfmKey='" + lastfmKey + '\'' +
                ", idAlbum=" + idAlbum +
                ", titulo='" + titulo + '\'' +
                ", artistaNombre='" + artistaNombre + '\'' +
                ", listeners=" + listeners +
                ", playcount=" + playcount +
                ", fetchedAt=" + fetchedAt +
                ", expiresAt=" + expiresAt +
                ", intentos=" + intentos +
                '}';
    }
}
