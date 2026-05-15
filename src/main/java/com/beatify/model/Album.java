package com.beatify.model;

import java.util.Objects;

public class Album {
    private Integer idAlbum;
    private String titulo;
    private Integer anioLanzamiento;
    private String selloDiscografico;
    private String tipo;
    private String portadaUrl;
    private String descripcion;
    private Integer idArtista;

    public Album() {
    }

    public Album(final String titulo, final Integer anioLanzamiento, final String selloDiscografico, final String tipo, final String portadaUrl, final String descripcion) {
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.selloDiscografico = selloDiscografico;
        this.tipo = tipo;
        this.portadaUrl = portadaUrl;
        this.descripcion = descripcion;
    }

    public Album(final Integer idAlbum, final String titulo, final Integer anioLanzamiento, final String selloDiscografico, final String tipo, final String portadaUrl, final String descripcion, final Integer idArtista) {
        this.idAlbum = idAlbum;
        this.titulo = titulo;
        this.anioLanzamiento = anioLanzamiento;
        this.selloDiscografico = selloDiscografico;
        this.tipo = tipo;
        this.portadaUrl = portadaUrl;
        this.descripcion = descripcion;
        this.idArtista = idArtista;
    }

    public Integer getIdAlbum() {
        return this.idAlbum;
    }

    public void setIdAlbum(final Integer idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public Integer getAnioLanzamiento() {
        return this.anioLanzamiento;
    }

    public void setAnioLanzamiento(final Integer anioLanzamiento) {
        this.anioLanzamiento = anioLanzamiento;
    }

    public String getSelloDiscografico() {
        return this.selloDiscografico;
    }

    public void setSelloDiscografico(final String selloDiscografico) {
        this.selloDiscografico = selloDiscografico;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    public String getPortadaUrl() {
        return this.portadaUrl;
    }

    public void setPortadaUrl(final String portadaUrl) {
        this.portadaUrl = portadaUrl;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
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
        final Album album = (Album) o;
        return Objects.equals(this.idAlbum, album.idAlbum);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idAlbum);
    }

    @Override
    public String toString() {
        return "Album{" +
                "idAlbum=" + idAlbum +
                ", titulo='" + titulo + '\'' +
                ", anioLanzamiento=" + anioLanzamiento +
                ", selloDiscografico='" + selloDiscografico + '\'' +
                ", tipo='" + tipo + '\'' +
                ", portadaUrl='" + portadaUrl + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", idArtista=" + idArtista +
                '}';
    }
}
