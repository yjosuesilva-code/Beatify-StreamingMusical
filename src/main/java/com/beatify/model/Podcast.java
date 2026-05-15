package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Podcast {
    private Integer idPodcast;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String portadaUrl;
    private LocalDate fechaCreacion;
    private Integer idArtista;

    public Podcast() {
    }

    public Podcast(final String titulo, final String descripcion, final String categoria, final String portadaUrl, final LocalDate fechaCreacion, final Integer idArtista) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.portadaUrl = portadaUrl;
        this.fechaCreacion = fechaCreacion;
        this.idArtista = idArtista;
    }

    public Podcast(final Integer idPodcast, final String titulo, final String descripcion, final String categoria, final String portadaUrl, final LocalDate fechaCreacion, final Integer idArtista) {
        this.idPodcast = idPodcast;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.portadaUrl = portadaUrl;
        this.fechaCreacion = fechaCreacion;
        this.idArtista = idArtista;
    }

    public Integer getIdPodcast() {
        return this.idPodcast;
    }

    public void setIdPodcast(final Integer idPodcast) {
        this.idPodcast = idPodcast;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public void setCategoria(final String categoria) {
        this.categoria = categoria;
    }

    public String getPortadaUrl() {
        return this.portadaUrl;
    }

    public void setPortadaUrl(final String portadaUrl) {
        this.portadaUrl = portadaUrl;
    }

    public LocalDate getFechaCreacion() {
        return this.fechaCreacion;
    }

    public void setFechaCreacion(final LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getIdArtista() {
        return this.idArtista;
    }

    public void setIdArtista(final Integer idArtista) {
        this.idArtista = idArtista;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Podcast podcast = (Podcast) o;
        return Objects.equals(this.idPodcast, podcast.idPodcast);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idPodcast);
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "idPodcast=" + idPodcast +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", portadaUrl='" + portadaUrl + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", idArtista=" + idArtista +
                '}';
    }
}
