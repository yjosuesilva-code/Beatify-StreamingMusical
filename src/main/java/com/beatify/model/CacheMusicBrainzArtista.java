package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Cache local de datos de artistas obtenidos desde MusicBrainz.
 * Una fila por MBID. El campo expires_at define hasta cuando es valido el cache.
 */
public class CacheMusicBrainzArtista {

    private Integer idCacheMbArtista;
    private String  mbid;              // identificador MusicBrainz (UUID, 36 chars)
    private Integer idArtista;         // FK opcional a ARTISTA (puede ser null mientras no se vincule)
    private String  nombreCompleto;
    private String  biografia;
    private String  paisOrigen;
    private Integer yearInicio;
    private Integer yearFin;
    private String  tags;
    private String  payloadJson;       // CLOB con el JSON crudo de la API
    private LocalDateTime fetchedAt;
    private LocalDateTime expiresAt;
    private Integer intentos;

    public CacheMusicBrainzArtista() {
    }

    public CacheMusicBrainzArtista(final String mbid, final Integer idArtista, final String nombreCompleto,
                                   final String biografia, final String paisOrigen,
                                   final Integer yearInicio, final Integer yearFin, final String tags,
                                   final String payloadJson, final LocalDateTime fetchedAt,
                                   final LocalDateTime expiresAt, final Integer intentos) {
        this.mbid = mbid;
        this.idArtista = idArtista;
        this.nombreCompleto = nombreCompleto;
        this.biografia = biografia;
        this.paisOrigen = paisOrigen;
        this.yearInicio = yearInicio;
        this.yearFin = yearFin;
        this.tags = tags;
        this.payloadJson = payloadJson;
        this.fetchedAt = fetchedAt;
        this.expiresAt = expiresAt;
        this.intentos = intentos;
    }

    public CacheMusicBrainzArtista(final Integer idCacheMbArtista, final String mbid, final Integer idArtista,
                                   final String nombreCompleto, final String biografia, final String paisOrigen,
                                   final Integer yearInicio, final Integer yearFin, final String tags,
                                   final String payloadJson, final LocalDateTime fetchedAt,
                                   final LocalDateTime expiresAt, final Integer intentos) {
        this.idCacheMbArtista = idCacheMbArtista;
        this.mbid = mbid;
        this.idArtista = idArtista;
        this.nombreCompleto = nombreCompleto;
        this.biografia = biografia;
        this.paisOrigen = paisOrigen;
        this.yearInicio = yearInicio;
        this.yearFin = yearFin;
        this.tags = tags;
        this.payloadJson = payloadJson;
        this.fetchedAt = fetchedAt;
        this.expiresAt = expiresAt;
        this.intentos = intentos;
    }

    public Integer getIdCacheMbArtista() { return this.idCacheMbArtista; }
    public void setIdCacheMbArtista(final Integer idCacheMbArtista) { this.idCacheMbArtista = idCacheMbArtista; }

    public String getMbid() { return this.mbid; }
    public void setMbid(final String mbid) { this.mbid = mbid; }

    public Integer getIdArtista() { return this.idArtista; }
    public void setIdArtista(final Integer idArtista) { this.idArtista = idArtista; }

    public String getNombreCompleto() { return this.nombreCompleto; }
    public void setNombreCompleto(final String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getBiografia() { return this.biografia; }
    public void setBiografia(final String biografia) { this.biografia = biografia; }

    public String getPaisOrigen() { return this.paisOrigen; }
    public void setPaisOrigen(final String paisOrigen) { this.paisOrigen = paisOrigen; }

    public Integer getYearInicio() { return this.yearInicio; }
    public void setYearInicio(final Integer yearInicio) { this.yearInicio = yearInicio; }

    public Integer getYearFin() { return this.yearFin; }
    public void setYearFin(final Integer yearFin) { this.yearFin = yearFin; }

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
        final CacheMusicBrainzArtista that = (CacheMusicBrainzArtista) o;
        return Objects.equals(this.idCacheMbArtista, that.idCacheMbArtista);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idCacheMbArtista);
    }

    @Override
    public String toString() {
        return "CacheMusicBrainzArtista{" +
                "idCacheMbArtista=" + idCacheMbArtista +
                ", mbid='" + mbid + '\'' +
                ", idArtista=" + idArtista +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", paisOrigen='" + paisOrigen + '\'' +
                ", yearInicio=" + yearInicio +
                ", yearFin=" + yearFin +
                ", tags='" + tags + '\'' +
                ", fetchedAt=" + fetchedAt +
                ", expiresAt=" + expiresAt +
                ", intentos=" + intentos +
                '}';
    }
}
