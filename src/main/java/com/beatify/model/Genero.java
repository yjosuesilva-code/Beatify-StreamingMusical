package com.beatify.model;

import java.util.Objects;

public class Genero {
    private Integer idGenero;
    private String nombre;
    private String descripcion;
    private String origenPais;

    public Genero() {
    }

    public Genero(final String nombre, final String descripcion, final String origenPais) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.origenPais = origenPais;
    }

    public Genero(final Integer idGenero, final String nombre, final String descripcion, final String origenPais) {
        this.idGenero = idGenero;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.origenPais = origenPais;
    }

    public Integer getIdGenero() {
        return this.idGenero;
    }

    public void setIdGenero(final Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public String getOrigenPais() {
        return this.origenPais;
    }

    public void setOrigenPais(final String origenPais) {
        this.origenPais = origenPais;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Genero genero = (Genero) o;
        return Objects.equals(this.idGenero, genero.idGenero);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idGenero);
    }

    @Override
    public String toString() {
        return "Genero{" +
                "idGenero=" + idGenero +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", origenPais='" + origenPais + '\'' +
                '}';
    }
}
