package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Resena {
    private Integer idResena;
    private Integer idCliente;
    private String tipoObjetivo;
    private Integer idObjetivo;
    private String comentario;
    private Integer calificacion;
    private LocalDateTime fechaResena;

     public Resena(){

     }

    public Resena(final Integer idCliente, final String tipoObjetivo, final Integer idObjetivo, final String comentario, final Integer calificacion, final LocalDateTime fechaResena) {
        this.idCliente = idCliente;
        this.tipoObjetivo = tipoObjetivo;
        this.idObjetivo = idObjetivo;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.fechaResena = fechaResena;
    }

    public Resena(final Integer idResena, final Integer idCliente, final String tipoObjetivo, final Integer idObjetivo, final String comentario, final Integer calificacion, final LocalDateTime fechaResena) {
        this.idResena = idResena;
        this.idCliente = idCliente;
        this.tipoObjetivo = tipoObjetivo;
        this.idObjetivo = idObjetivo;
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.fechaResena = fechaResena;
    }

    public Integer getIdResena() {
        return this.idResena;
    }

    public void setIdResena(final Integer idResena) {
        this.idResena = idResena;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoObjetivo() {
        return this.tipoObjetivo;
    }

    public void setTipoObjetivo(final String tipoObjetivo) {
        this.tipoObjetivo = tipoObjetivo;
    }

    public Integer getIdObjetivo() {
        return this.idObjetivo;
    }

    public void setIdObjetivo(final Integer idObjetivo) {
        this.idObjetivo = idObjetivo;
    }

    public String getComentario() {
        return this.comentario;
    }

    public void setComentario(final String comentario) {
        this.comentario = comentario;
    }

    public Integer getCalificacion() {
        return this.calificacion;
    }

    public void setCalificacion(final Integer calificacion) {
        this.calificacion = calificacion;
    }

    public LocalDateTime getFechaResena() {
        return this.fechaResena;
    }

    public void setFechaResena(final LocalDateTime fechaResena) {
        this.fechaResena = fechaResena;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Resena resena = (Resena) o;
        return Objects.equals(this.idResena, resena.idResena);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idResena);
    }

    @Override
    public String toString() {
        return "Resena{" +
                "idResena=" + idResena +
                ", idCliente=" + idCliente +
                ", tipoObjetivo='" + tipoObjetivo + '\'' +
                ", idObjetivo=" + idObjetivo +
                ", comentario='" + comentario + '\'' +
                ", calificacion=" + calificacion +
                ", fechaResena='" + fechaResena + '\'' +
                '}';
    }
}
