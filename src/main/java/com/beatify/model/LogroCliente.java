package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class LogroCliente {
    private Integer idLogroCliente;
    private LocalDateTime fechaObtencion;
    private Integer idCliente;
    private Integer idLogro;

    public LogroCliente() {

    }

    public LogroCliente(final LocalDateTime fechaObtencion, final Integer idCliente, final Integer idLogro) {
        this.fechaObtencion = fechaObtencion;
        this.idCliente = idCliente;
        this.idLogro = idLogro;
    }

    public LogroCliente(final Integer idLogroCliente, final LocalDateTime fechaObtencion, final Integer idCliente, final Integer idLogro) {
        this.idLogroCliente = idLogroCliente;
        this.fechaObtencion = fechaObtencion;
        this.idCliente = idCliente;
        this.idLogro = idLogro;
    }

    public Integer getIdLogroCliente() {
        return this.idLogroCliente;
    }

    public void setIdLogroCliente(final Integer idLogroCliente) {
        this.idLogroCliente = idLogroCliente;
    }

    public LocalDateTime getFechaObtencion() {
        return this.fechaObtencion;
    }

    public void setFechaObtencion(final LocalDateTime fechaObtencion) {
        this.fechaObtencion = fechaObtencion;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdLogro() {
        return this.idLogro;
    }

    public void setIdLogro(final Integer idLogro) {
        this.idLogro = idLogro;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final LogroCliente that = (LogroCliente) o;
        return Objects.equals(this.idLogroCliente, that.idLogroCliente);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idLogroCliente);
    }

    @Override
    public String toString() {
        return "LogroCliente{" +
                "idLogroCliente=" + idLogroCliente +
                ", fechaObtencion=" + fechaObtencion +
                ", idCliente=" + idCliente +
                ", idLogro=" + idLogro +
                '}';
    }
}