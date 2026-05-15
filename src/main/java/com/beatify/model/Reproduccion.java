package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reproduccion {
    private Integer idReproduccion;
    private LocalDateTime fechaHora;
    private Integer duracionReproduccion;
    private Integer idCliente;
    private Integer idCancion;

    public Reproduccion(){
    }

    public Reproduccion(final LocalDateTime fechaHora, final Integer duracionReproduccion, final Integer idCliente, final Integer idCancion) {
        this.fechaHora = fechaHora;
        this.duracionReproduccion = duracionReproduccion;
        this.idCliente = idCliente;
        this.idCancion = idCancion;
    }

    public Reproduccion(final Integer idReproduccion, final LocalDateTime fechaHora, final Integer duracionReproduccion, final Integer idCliente, final Integer idCancion) {
        this.idReproduccion = idReproduccion;
        this.fechaHora = fechaHora;
        this.duracionReproduccion = duracionReproduccion;
        this.idCliente = idCliente;
        this.idCancion = idCancion;
    }

    public Integer getIdReproduccion() {
        return this.idReproduccion;
    }

    public void setIdReproduccion(final Integer idReproduccion) {
        this.idReproduccion = idReproduccion;
    }

    public LocalDateTime getFechaHora() {
        return this.fechaHora;
    }

    public void setFechaHora(final LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getDuracionReproduccion() {
        return this.duracionReproduccion;
    }

    public void setDuracionReproduccion(final Integer duracionReproduccion) {
        this.duracionReproduccion = duracionReproduccion;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
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
        final Reproduccion that = (Reproduccion) o;
        return Objects.equals(this.idReproduccion, that.idReproduccion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idReproduccion);
    }

    @Override
    public String toString() {
        return "Reproduccion{" +
                "idReproduccion=" + idReproduccion +
                ", fechaHora='" + fechaHora + '\'' +
                ", duracionReproduccion=" + duracionReproduccion +
                ", idCliente=" + idCliente +
                ", idCancion=" + idCancion +
                '}';
    }
}
