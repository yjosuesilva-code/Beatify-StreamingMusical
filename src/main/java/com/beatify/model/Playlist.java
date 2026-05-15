package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Playlist {
    private Integer idPlaylist;
    private String nombre;
    private String descripcion;
    private LocalDate fechaCreacion;
    private String publica;
    private Integer idCliente;

    public Playlist() {
    }

    public Playlist(final String nombre, final String descripcion, final LocalDate fechaCreacion, final String publica, final Integer idCliente) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.publica = publica;
        this.idCliente = idCliente;
    }

    public Playlist(final Integer idPlaylist, final String nombre, final String descripcion, final LocalDate fechaCreacion, final String publica, final Integer idCliente) {
        this.idPlaylist = idPlaylist;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.publica = publica;
        this.idCliente = idCliente;
    }

    public Integer getIdPlaylist() {
        return this.idPlaylist;
    }

    public void setIdPlaylist(final Integer idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCreacion() {
        return this.fechaCreacion;
    }

    public void setFechaCreacion(final LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getPublica() {
        return this.publica;
    }

    public void setPublica(final String publica) {
        this.publica = publica;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Playlist playlist = (Playlist) o;
        return Objects.equals(this.idPlaylist, playlist.idPlaylist);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idPlaylist);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "idPlaylist=" + idPlaylist +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", publica='" + publica + '\'' +
                ", idCliente=" + idCliente +
                '}';
    }
}
