package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Cancion {
    private Integer idCancion;
    private String titulo;
    private Integer duracionSegundos;
    private String rutaArchivo;
    private String letra;
    private String compositor;
    private LocalDate fechaLanzamiento;
    private Integer idAlbum;
    private Integer idGenero;

    public Cancion() {
    }

    public Cancion(final String titulo, final Integer duracionSegundos, final String rutaArchivo, final String letra, final String compositor, final LocalDate fechaLanzamiento, final Integer idAlbum, final Integer idGenero) {
        this.titulo = titulo;
        this.duracionSegundos = duracionSegundos;
        this.rutaArchivo = rutaArchivo;
        this.letra = letra;
        this.compositor = compositor;
        this.fechaLanzamiento = fechaLanzamiento;
        this.idAlbum = idAlbum;
        this.idGenero = idGenero;
    }

    public Cancion(final Integer idCancion, final String titulo, final Integer duracionSegundos, final String rutaArchivo, final String letra, final String compositor, final LocalDate fechaLanzamiento, final Integer idAlbum, final Integer idGenero) {
        this.idCancion = idCancion;
        this.titulo = titulo;
        this.duracionSegundos = duracionSegundos;
        this.rutaArchivo = rutaArchivo;
        this.letra = letra;
        this.compositor = compositor;
        this.fechaLanzamiento = fechaLanzamiento;
        this.idAlbum = idAlbum;
        this.idGenero = idGenero;
    }

    public Integer getIdCancion() {
        return this.idCancion;
    }

    public void setIdCancion(final Integer idCancion) {
        this.idCancion = idCancion;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public Integer getDuracionSegundos() {
        return this.duracionSegundos;
    }

    public void setDuracionSegundos(final Integer duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public String getRutaArchivo() {
        return this.rutaArchivo;
    }

    public void setRutaArchivo(final String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getLetra() {
        return this.letra;
    }

    public void setLetra(final String letra) {
        this.letra = letra;
    }

    public String getCompositor() {
        return this.compositor;
    }

    public void setCompositor(final String compositor) {
        this.compositor = compositor;
    }

    public LocalDate getFechaLanzamiento() {
        return this.fechaLanzamiento;
    }

    public void setFechaLanzamiento(final LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public Integer getIdAlbum() {
        return this.idAlbum;
    }

    public void setIdAlbum(final Integer idAlbum) {
        this.idAlbum = idAlbum;
    }

    public Integer getIdGenero() {
        return this.idGenero;
    }

    public void setIdGenero(final Integer idGenero) {
        this.idGenero = idGenero;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Cancion cancion = (Cancion) o;
        return Objects.equals(this.idCancion, cancion.idCancion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idCancion);
    }

    @Override
    public String toString() {
        return "Cancion{" +
                "idCancion=" + idCancion +
                ", titulo='" + titulo + '\'' +
                ", duracionSegundos=" + duracionSegundos +
                ", rutaArchivo='" + rutaArchivo + '\'' +
                ", letra='" + letra + '\'' +
                ", compositor='" + compositor + '\'' +
                ", fechaLanzamiento=" + fechaLanzamiento +
                ", idAlbum=" + idAlbum +
                ", idGenero=" + idGenero +
                '}';
    }
}
