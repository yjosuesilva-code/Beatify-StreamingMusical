package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Seguimiento {
    private Integer idSeguimiento;
    private LocalDate fechaSeguimiento;
    private Integer idCliente;
    private Integer idArtista;

    public Seguimiento() {
    }

    public Seguimiento(final LocalDate fechaSeguimiento, final Integer idCliente, final Integer idArtista) {
        this.fechaSeguimiento = fechaSeguimiento;
        this.idCliente = idCliente;
        this.idArtista = idArtista;
    }

    public Seguimiento(final Integer idSeguimiento, final LocalDate fechaSeguimiento, final Integer idCliente, final Integer idArtista) {
        this.idSeguimiento = idSeguimiento;
        this.fechaSeguimiento = fechaSeguimiento;
        this.idCliente = idCliente;
        this.idArtista = idArtista;
    }

    public Integer getIdSeguimiento() {
        return this.idSeguimiento;
    }

    public void setIdSeguimiento(final Integer idSeguimiento) {
        this.idSeguimiento = idSeguimiento;
    }

    public LocalDate getFechaSeguimiento() {
        return this.fechaSeguimiento;
    }

    public void setFechaSeguimiento(final LocalDate fechaSeguimiento) {
        this.fechaSeguimiento = fechaSeguimiento;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
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
        final Seguimiento that = (Seguimiento) o;
        return Objects.equals(this.idSeguimiento, that.idSeguimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idSeguimiento);
    }

    @Override
    public String toString() {
        return "Seguimiento{" +
                "idSeguimiento=" + idSeguimiento +
                ", fechaSeguimiento='" + fechaSeguimiento + '\'' +
                ", idCliente=" + idCliente +
                ", idArtista=" + idArtista +
                '}';
    }
}
