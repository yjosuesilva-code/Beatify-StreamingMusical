package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Busqueda {
    private Integer idBusqueda;
    private String terminoBusqueda;
    private LocalDateTime fechaBusqueda;
    private Integer resultadosObtenidos;
    private Integer idCliente;

    public Busqueda() {
    }

    public Busqueda(final String terminoBusqueda, final LocalDateTime fechaBusqueda, final Integer resultadosObtenidos, final Integer idCliente) {
        this.terminoBusqueda = terminoBusqueda;
        this.fechaBusqueda = fechaBusqueda;
        this.resultadosObtenidos = resultadosObtenidos;
        this.idCliente = idCliente;
    }

    public Busqueda(final Integer idBusqueda, final String terminoBusqueda, final LocalDateTime fechaBusqueda, final Integer resultadosObtenidos, final Integer idCliente) {
        this.idBusqueda = idBusqueda;
        this.terminoBusqueda = terminoBusqueda;
        this.fechaBusqueda = fechaBusqueda;
        this.resultadosObtenidos = resultadosObtenidos;
        this.idCliente = idCliente;
    }

    public Integer getIdBusqueda() {
        return this.idBusqueda;
    }

    public void setIdBusqueda(final Integer idBusqueda) {
        this.idBusqueda = idBusqueda;
    }

    public String getTerminoBusqueda() {
        return this.terminoBusqueda;
    }

    public void setTerminoBusqueda(final String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }

    public LocalDateTime getFechaBusqueda() {
        return this.fechaBusqueda;
    }

    public void setFechaBusqueda(final LocalDateTime fechaBusqueda) {
        this.fechaBusqueda = fechaBusqueda;
    }

    public Integer getResultadosObtenidos() {
        return this.resultadosObtenidos;
    }

    public void setResultadosObtenidos(final Integer resultadosObtenidos) {
        this.resultadosObtenidos = resultadosObtenidos;
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
        final Busqueda busqueda = (Busqueda) o;
        return Objects.equals(this.idBusqueda, busqueda.idBusqueda);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idBusqueda);
    }

    @Override
    public String toString() {
        return "Busqueda{" +
                "idBusqueda=" + idBusqueda +
                ", terminoBusqueda='" + terminoBusqueda + '\'' +
                ", fechaBusqueda='" + fechaBusqueda + '\'' +
                ", resultadosObtenidos=" + resultadosObtenidos +
                ", idCliente=" + idCliente +
                '}';
    }
}
