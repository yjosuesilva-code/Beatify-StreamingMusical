package com.beatify.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class LikePlaylist {
    private Integer idLikePlaylist;
    private Integer idCliente;
    private Integer idPlaylist;
    private LocalDateTime fechaLike;

    public LikePlaylist() {
    }

    public LikePlaylist(final Integer idCliente, final Integer idPlaylist, final LocalDateTime fechaLike) {
        this.idCliente = idCliente;
        this.idPlaylist = idPlaylist;
        this.fechaLike = fechaLike;
    }

    public LikePlaylist(final Integer idLikePlaylist, final Integer idCliente, final Integer idPlaylist, final LocalDateTime fechaLike) {
        this.idLikePlaylist = idLikePlaylist;
        this.idCliente = idCliente;
        this.idPlaylist = idPlaylist;
        this.fechaLike = fechaLike;
    }

    public Integer getIdLikePlaylist() {
        return this.idLikePlaylist;
    }

    public void setIdLikePlaylist(final Integer idLikePlaylist) {
        this.idLikePlaylist = idLikePlaylist;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdPlaylist() {
        return this.idPlaylist;
    }

    public void setIdPlaylist(final Integer idPlaylist) {
        this.idPlaylist = idPlaylist;
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
        final LikePlaylist that = (LikePlaylist) o;
        return Objects.equals(this.idLikePlaylist, that.idLikePlaylist);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idLikePlaylist);
    }

    @Override
    public String toString() {
        return "LikePlaylist{" +
                "idLikePlaylist=" + idLikePlaylist +
                ", idCliente=" + idCliente +
                ", idPlaylist=" + idPlaylist +
                ", fechaLike=" + fechaLike +
                '}';
    }
}