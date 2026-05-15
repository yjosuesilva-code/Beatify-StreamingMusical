package com.beatify.model;

import java.util.Objects;

public class ArtistaGenero {
    private Integer idArtista;
    private Integer idGenero;

    public ArtistaGenero() {
    }

    public ArtistaGenero(final Integer idArtista, final Integer idGenero) {
        this.idArtista = idArtista;
        this.idGenero = idGenero;
    }

    public Integer getIdArtista() {
        return this.idArtista;
    }

    public void setIdArtista(final Integer idArtista) {
        this.idArtista = idArtista;
    }

    public Integer getIdGenero() {
        return this.idGenero;
    }

    public void setIdGenero(final Integer idGenero) {
        this.idGenero = idGenero;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final ArtistaGenero that = (ArtistaGenero) o;
        return Objects.equals(this.idArtista, that.idArtista) && Objects.equals(this.idGenero, that.idGenero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idArtista, this.idGenero);
    }

    @Override
    public String toString() {
        return "ArtistaGenero{" +
                "idArtista=" + idArtista +
                ", idGenero=" + idGenero +
                '}';
    }
}
