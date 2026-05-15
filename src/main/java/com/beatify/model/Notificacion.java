package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Notificacion {
    private Integer idNotificacion;
    private String titulo;
    private String mensaje;
    private String tipo;          // 'INFO', 'PROMO', 'RECOMENDACION', 'SISTEMA'
    private LocalDateTime fechaEnvio;
    private String leida;         // 'N' o 'S'
    private Integer idCliente;

    public Notificacion() {
    }

    public Notificacion(final String titulo, final String mensaje, final String tipo, final LocalDateTime fechaEnvio, final String leida, final Integer idCliente) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fechaEnvio = fechaEnvio;
        this.leida = leida;
        this.idCliente = idCliente;
    }

    public Notificacion(final Integer idNotificacion, final String titulo, final String mensaje, final String tipo, final LocalDateTime fechaEnvio, final String leida, final Integer idCliente) {
        this.idNotificacion = idNotificacion;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.fechaEnvio = fechaEnvio;
        this.leida = leida;
        this.idCliente = idCliente;
    }

    public Integer getIdNotificacion() {
        return this.idNotificacion;
    }

    public void setIdNotificacion(final Integer idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(final String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return this.tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaEnvio() {
        return this.fechaEnvio;
    }

    public void setFechaEnvio(final LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getLeida() {
        return this.leida;
    }

    public void setLeida(final String leida) {
        this.leida = leida;
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
        final Notificacion that = (Notificacion) o;
        return Objects.equals(this.idNotificacion, that.idNotificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idNotificacion);
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "idNotificacion=" + idNotificacion +
                ", titulo='" + titulo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fechaEnvio=" + fechaEnvio +
                ", leida='" + leida + '\'' +
                ", idCliente=" + idCliente +
                '}';
    }
}