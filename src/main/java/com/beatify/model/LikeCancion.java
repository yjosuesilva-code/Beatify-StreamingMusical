package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class LikeCancion {
    private Integer idLikeCancion;
    private Integer idCliente;
    private Integer idCancion;
    private LocalDateTime fechaLike;

    public LikeCancion() {
    }

    public LikeCancion(final Integer idCliente, final Integer idCancion, final LocalDateTime fechaLike) {
        this.idCliente = idCliente;
        this.idCancion = idCancion;
        this.fechaLike = fechaLike;
    }

    public LikeCancion(final Integer idLikeCancion, final Integer idCliente, final Integer idCancion,
                       final LocalDateTime fechaLike) {
        this.idLikeCancion = idLikeCancion;
        this.idCliente = idCliente;
        this.idCancion = idCancion;
        this.fechaLike = fechaLike;
    }

    public Integer getIdLikeCancion() {
        return this.idLikeCancion;
    }

    public void setIdLikeCancion(final Integer idLikeCancion) {
        this.idLikeCancion = idLikeCancion;
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

    public LocalDateTime getFechaLike() {
        return this.fechaLike;
    }

    public void setFechaLike(final LocalDateTime fechaLike) {
        this.fechaLike = fechaLike;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final LikeCancion that = (LikeCancion) o;
        return Objects.equals(this.idLikeCancion, that.idLikeCancion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idLikeCancion);
    }

    @Override
    public String toString() {
        return "LikeCancion{" +
                "idLikeCancion=" + idLikeCancion +
                ", idCliente=" + idCliente +
                ", idCancion=" + idCancion +
                ", fechaLike=" + fechaLike +
                '}';
    }
}