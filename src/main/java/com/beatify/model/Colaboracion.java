package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Colaboracion {
    private Integer idColaboracion;
    private Integer idCancion;
    private Integer idArtista;
    private String rol;
    private LocalDate fechaColaboracion;

    public Colaboracion() {
    }

     public Colaboracion(final Integer idCancion, final Integer idArtista, final String rol, final LocalDate fechaColaboracion) {
        this.idCancion = idCancion;
        this.idArtista = idArtista;
        this.rol = rol;
        this.fechaColaboracion = fechaColaboracion;
    }

    public Colaboracion(final Integer idColaboracion, final Integer idCancion, final Integer idArtista, final String rol, final LocalDate fechaColaboracion) {
        this.idColaboracion = idColaboracion;
        this.idCancion = idCancion;
        this.idArtista = idArtista;
        this.rol = rol;
        this.fechaColaboracion = fechaColaboracion;
    }

    public Integer getIdColaboracion() {
        return this.idColaboracion;
    }

    public void setIdColaboracion(final Integer idColaboracion) {
        this.idColaboracion = idColaboracion;
    }

    public Integer getIdCancion() {
        return this.idCancion;
    }

    public void setIdCancion(final Integer idCancion) {
        this.idCancion = idCancion;
    }

    public Integer getIdArtista() {
        return this.idArtista;
    }

    public void setIdArtista(final Integer idArtista) {
        this.idArtista = idArtista;
    }

    public String getRol() {
        return this.rol;
    }

    public void setRol(final String rol) {
        this.rol = rol;
    }

    public LocalDate getFechaColaboracion() {
        return this.fechaColaboracion;
    }

    public void setFechaColaboracion(final LocalDate fechaColaboracion) {
        this.fechaColaboracion = fechaColaboracion;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Colaboracion that = (Colaboracion) o;
        return Objects.equals(this.idColaboracion, that.idColaboracion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idColaboracion);
    }

    @Override
    public String toString() {
        return "Colaboracion{" +
                "idColaboracion=" + idColaboracion +
                ", idCancion=" + idCancion +
                ", idArtista=" + idArtista +
                ", rol='" + rol + '\'' +
                ", fechaColaboracion=" + fechaColaboracion +
                '}';
    }
}
