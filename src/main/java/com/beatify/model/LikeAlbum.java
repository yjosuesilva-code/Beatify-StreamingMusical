package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class LikeAlbum {
    private Integer idLikeAlbum;
    private Integer idCliente;
    private Integer idAlbum;
    private LocalDateTime fechaLike;

    public LikeAlbum() {
    }

    public LikeAlbum(final Integer idCliente, final Integer idAlbum, final LocalDateTime fechaLike) {
        this.idCliente = idCliente;
        this.idAlbum = idAlbum;
        this.fechaLike = fechaLike;
    }

    public LikeAlbum(final Integer idLikeAlbum, final Integer idCliente, final Integer idAlbum, final LocalDateTime fechaLike) {
        this.idLikeAlbum = idLikeAlbum;
        this.idCliente = idCliente;
        this.idAlbum = idAlbum;
        this.fechaLike = fechaLike;
    }

    public Integer getIdLikeAlbum() {
        return this.idLikeAlbum;
    }

    public void setIdLikeAlbum(final Integer idLikeAlbum) {
        this.idLikeAlbum = idLikeAlbum;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdAlbum() {
        return this.idAlbum;
    }

    public void setIdAlbum(final Integer idAlbum) {
        this.idAlbum = idAlbum;
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
        final LikeAlbum likeAlbum = (LikeAlbum) o;
        return Objects.equals(this.idLikeAlbum, likeAlbum.idLikeAlbum);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idLikeAlbum);
    }

    @Override
    public String toString() {
        return "LikeAlbum{" +
                "idLikeAlbum=" + idLikeAlbum +
                ", idCliente=" + idCliente +
                ", idAlbum=" + idAlbum +
                ", fechaLike=" + fechaLike +
                '}';
    }
}