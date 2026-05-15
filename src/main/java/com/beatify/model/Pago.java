package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Pago {
    private Integer idPago;
    private Double monto;
    private LocalDateTime fechaPago;
    private String metodoPago;
    private String estadoPago;
    private String referenciaExterna;
    private Integer idSuscripcion;

    public Pago() {
    }

    public Pago(final Double monto, final LocalDateTime fechaPago, final String metodoPago, final String estadoPago, final String referenciaExterna, final Integer idSuscripcion) {
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.referenciaExterna = referenciaExterna;
        this.idSuscripcion = idSuscripcion;
    }

    public Pago(final Integer idPago, final Double monto, final LocalDateTime fechaPago, final String metodoPago, final String estadoPago, final String referenciaExterna, final Integer idSuscripcion) {
        this.idPago = idPago;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.referenciaExterna = referenciaExterna;
        this.idSuscripcion = idSuscripcion;
    }

    public Integer getIdPago() {
        return this.idPago;
    }

    public void setIdPago(final Integer idPago) {
        this.idPago = idPago;
    }

    public Double getMonto() {
        return this.monto;
    }

    public void setMonto(final Double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return this.fechaPago;
    }

    public void setFechaPago(final LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMetodoPago() {
        return this.metodoPago;
    }

    public void setMetodoPago(final String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPago() {
        return this.estadoPago;
    }

    public void setEstadoPago(final String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getReferenciaExterna() {
        return this.referenciaExterna;
    }

    public void setReferenciaExterna(final String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
    }

    public Integer getIdSuscripcion() {
        return this.idSuscripcion;
    }

    public void setIdSuscripcion(final Integer idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Pago pago = (Pago) o;
        return Objects.equals(this.idPago, pago.idPago);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idPago);
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", monto=" + monto +
                ", fechaPago='" + fechaPago + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                ", estadoPago='" + estadoPago + '\'' +
                ", referenciaExterna='" + referenciaExterna + '\'' +
                ", idSuscripcion=" + idSuscripcion +
                '}';
    }
}
