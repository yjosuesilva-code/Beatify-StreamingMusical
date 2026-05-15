package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class CancionPlaylist {
    private Integer idCancionPlaylist;
    private Integer orden;
    private LocalDate fechaAgregada;
    private Integer idPlaylist;
    private Integer idCancion;

    public CancionPlaylist() {
    }

    public CancionPlaylist(final Integer orden, final LocalDate fechaAgregada, final Integer idPlaylist, final Integer idCancion) {
        this.orden = orden;
        this.fechaAgregada = fechaAgregada;
        this.idPlaylist = idPlaylist;
        this.idCancion = idCancion;
    }

    public CancionPlaylist(final Integer idCancionPlaylist, final Integer orden, final LocalDate fechaAgregada, final Integer idPlaylist, final Integer idCancion) {
        this.idCancionPlaylist = idCancionPlaylist;
        this.orden = orden;
        this.fechaAgregada = fechaAgregada;
        this.idPlaylist = idPlaylist;
        this.idCancion = idCancion;
    }

    public Integer getIdCancionPlaylist() {
        return this.idCancionPlaylist;
    }

    public void setIdCancionPlaylist(final Integer idCancionPlaylist) {
        this.idCancionPlaylist = idCancionPlaylist;
    }

    public Integer getOrden() {
        return this.orden;
    }

    public void setOrden(final Integer orden) {
        this.orden = orden;
    }

    public LocalDate getFechaAgregada() {
        return this.fechaAgregada;
    }

    public void setFechaAgregada(final LocalDate fechaAgregada) {
        this.fechaAgregada = fechaAgregada;
    }

    public Integer getIdPlaylist() {
        return this.idPlaylist;
    }

    public void setIdPlaylist(final Integer idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public Integer getIdCancion() {
        return this.idCancion;
    }

    public void setIdCancion(final Integer idCancion) {
        this.idCancion = idCancion;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final CancionPlaylist that = (CancionPlaylist) o;
        return Objects.equals(this.idCancionPlaylist, that.idCancionPlaylist);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idCancionPlaylist);
    }

    @Override
    public String toString() {
        return "CancionPlaylist{" +
                "idCancionPlaylist=" + idCancionPlaylist +
                ", orden=" + orden +
                ", fechaAgregada=" + fechaAgregada +
                ", idPlaylist=" + idPlaylist +
                ", idCancion=" + idCancion +
                '}';
    }
}