package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Episodio {
    private Integer idEpisodio;
    private Integer numeroEpisodio;
    private String titulo;
    private String descripcion;
    private Integer duracion; // Duración en segundos
    private LocalDate fechaPublicacion; // Fecha de publicación en formato "YYYY-MM-DD"
    private String rutaArchivo;
    private Integer idPodcast;

    public Episodio() {
    }

    public Episodio(final Integer numeroEpisodio, final String titulo, final String descripcion, final Integer duracion, final LocalDate fechaPublicacion, final String rutaArchivo, final Integer idPodcast) {
        this.numeroEpisodio = numeroEpisodio;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.fechaPublicacion = fechaPublicacion;
        this.rutaArchivo = rutaArchivo;
        this.idPodcast = idPodcast;
    }

    public Episodio(final Integer idEpisodio, final Integer numeroEpisodio, final String titulo, final String descripcion, final Integer duracion, final LocalDate fechaPublicacion, final String rutaArchivo, final Integer idPodcast) {
        this.idEpisodio = idEpisodio;
        this.numeroEpisodio = numeroEpisodio;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.fechaPublicacion = fechaPublicacion;
        this.rutaArchivo = rutaArchivo;
        this.idPodcast = idPodcast;
    }

    public Integer getIdEpisodio() {
        return this.idEpisodio;
    }

    public void setIdEpisodio(final Integer idEpisodio) {
        this.idEpisodio = idEpisodio;
    }

    public Integer getNumeroEpisodio() {
        return this.numeroEpisodio;
    }

    public void setNumeroEpisodio(final Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
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

    public Integer getDuracion() {
        return this.duracion;
    }

    public void setDuracion(final Integer duracion) {
        this.duracion = duracion;
    }

    public LocalDate getFechaPublicacion() {
        return this.fechaPublicacion;
    }

    public void setFechaPublicacion(final LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getRutaArchivo() {
        return this.rutaArchivo;
    }

    public void setRutaArchivo(final String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public Integer getIdPodcast() {
        return this.idPodcast;
    }

    public void setIdPodcast(final Integer idPodcast) {
        this.idPodcast = idPodcast;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Episodio episodio = (Episodio) o;
        return Objects.equals(this.idEpisodio, episodio.idEpisodio);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idEpisodio);
    }

    @Override
    public String toString() {
        return "Episodio{" +
                "idEpisodio=" + idEpisodio +
                ", numeroEpisodio=" + numeroEpisodio +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", duracion=" + duracion +
                ", fechaPublicacion=" + fechaPublicacion +
                ", rutaArchivo='" + rutaArchivo + '\'' +
                ", idPodcast=" + idPodcast +
                '}';
    }
}
