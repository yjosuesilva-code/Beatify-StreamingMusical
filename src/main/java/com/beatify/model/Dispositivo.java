package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Dispositivo {
    private Integer idDispositivo;
    private String nombreDispositivo;
    private String tipoDispositivo;
    private String sistemaOperativo;
    private LocalDateTime fechaUltimoAcceso;
    private Integer idCliente;

    public Dispositivo() {
    }

    public Dispositivo(final String nombreDispositivo, final String tipoDispositivo, final String sistemaOperativo, final LocalDateTime fechaUltimoAcceso, final Integer idCliente) {
        this.nombreDispositivo = nombreDispositivo;
        this.tipoDispositivo = tipoDispositivo;
        this.sistemaOperativo = sistemaOperativo;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
        this.idCliente = idCliente;
    }

    public Dispositivo(final Integer idDispositivo, final String nombreDispositivo, final String tipoDispositivo, final String sistemaOperativo, final LocalDateTime fechaUltimoAcceso, final Integer idCliente) {
        this.idDispositivo = idDispositivo;
        this.nombreDispositivo = nombreDispositivo;
        this.tipoDispositivo = tipoDispositivo;
        this.sistemaOperativo = sistemaOperativo;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
        this.idCliente = idCliente;
    }

    public Integer getIdDispositivo() {
        return this.idDispositivo;
    }

    public void setIdDispositivo(final Integer idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getNombreDispositivo() {
        return this.nombreDispositivo;
    }

    public void setNombreDispositivo(final String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getTipoDispositivo() {
        return this.tipoDispositivo;
    }

    public void setTipoDispositivo(final String tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public String getSistemaOperativo() {
        return this.sistemaOperativo;
    }

    public void setSistemaOperativo(final String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public LocalDateTime getFechaUltimoAcceso() {
        return this.fechaUltimoAcceso;
    }

    public void setFechaUltimoAcceso(final LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
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
        final Dispositivo that = (Dispositivo) o;
        return Objects.equals(this.idDispositivo, that.idDispositivo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idDispositivo);
    }

    @Override
    public String toString() {
        return "Dispositivo{" +
                "idDispositivo=" + idDispositivo +
                ", nombreDispositivo='" + nombreDispositivo + '\'' +
                ", tipoDispositivo='" + tipoDispositivo + '\'' +
                ", sistemaOperativo='" + sistemaOperativo + '\'' +
                ", fechaUltimoAcceso='" + fechaUltimoAcceso + '\'' +
                ", idCliente=" + idCliente +
                '}';
    }
}
