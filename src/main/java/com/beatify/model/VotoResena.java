package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class VotoResena {
    private Integer idVotoResena;
    private String util;          // 'S' o 'N' según el CHECK del schema
    private LocalDateTime fechaVoto;
    private Integer idResena;
    private Integer idCliente;

    public VotoResena() {
    }

    public VotoResena(final String util, final LocalDateTime fechaVoto, final Integer idResena, final Integer idCliente) {
        this.util = util;
        this.fechaVoto = fechaVoto;
        this.idResena = idResena;
        this.idCliente = idCliente;
    }

    public VotoResena(final Integer idVotoResena, final String util, final LocalDateTime fechaVoto, final Integer idResena, final Integer idCliente) {
        this.idVotoResena = idVotoResena;
        this.util = util;
        this.fechaVoto = fechaVoto;
        this.idResena = idResena;
        this.idCliente = idCliente;
    }

    public Integer getIdVotoResena() {
        return this.idVotoResena;
    }

    public void setIdVotoResena(final Integer idVotoResena) {
        this.idVotoResena = idVotoResena;
    }

    public String getUtil() {
        return this.util;
    }

    public void setUtil(final String util) {
        this.util = util;
    }

    public LocalDateTime getFechaVoto() {
        return this.fechaVoto;
    }

    public void setFechaVoto(final LocalDateTime fechaVoto) {
        this.fechaVoto = fechaVoto;
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

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final VotoResena that = (VotoResena) o;
        return Objects.equals(this.idVotoResena, that.idVotoResena);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idVotoResena);
    }

    @Override
    public String toString() {
        return "VotoResena{" +
                "idVotoResena=" + idVotoResena +
                ", util='" + util + '\'' +
                ", fechaVoto=" + fechaVoto +
                ", idResena=" + idResena +
                ", idCliente=" + idCliente +
                '}';
    }
}