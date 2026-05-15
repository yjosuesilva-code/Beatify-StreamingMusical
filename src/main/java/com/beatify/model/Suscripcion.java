package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Suscripcion {
    private Integer idSuscripcion;
    private String tipoPlan;
    private Double precio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Integer idCliente;

    public Suscripcion() {
    }

    public Suscripcion(final String tipoPlan, final Double precio, final LocalDate fechaInicio, final LocalDate fechaFin, final String estado, final Integer idCliente) {
        this.tipoPlan = tipoPlan;
        this.precio = precio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.idCliente = idCliente;
    }

    public Suscripcion(final Integer idSuscripcion, final String tipoPlan, final Double precio, final LocalDate fechaInicio, final LocalDate fechaFin, final String estado, final Integer idCliente) {
        this.idSuscripcion = idSuscripcion;
        this.tipoPlan = tipoPlan;
        this.precio = precio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.idCliente = idCliente;
    }

    public Integer getIdSuscripcion() {
        return this.idSuscripcion;
    }

    public void setIdSuscripcion(final Integer idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public String getTipoPlan() {
        return this.tipoPlan;
    }

    public void setTipoPlan(final String tipoPlan) {
        this.tipoPlan = tipoPlan;
    }

    public Double getPrecio() {
        return this.precio;
    }

    public void setPrecio(final Double precio) {
        this.precio = precio;
    }

    public LocalDate getFechaInicio() {
        return this.fechaInicio;
    }

    public void setFechaInicio(final LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return this.fechaFin;
    }

    public void setFechaFin(final LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
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
        final Suscripcion that = (Suscripcion) o;
        return Objects.equals(this.idSuscripcion, that.idSuscripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idSuscripcion);
    }

    @Override
    public String toString() {
        return "Suscripcion{" +
                "idSuscripcion=" + idSuscripcion +
                ", tipoPlan='" + tipoPlan + '\'' +
                ", precio=" + precio +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estado='" + estado + '\'' +
                ", idCliente=" + idCliente +
                '}';
    }
}
