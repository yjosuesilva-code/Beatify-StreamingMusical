package com.beatify.model;

import java.util.Objects;

public class Logro {
    private Integer idLogro;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String iconoUrl;
    private Integer puntos;

    public Logro() {
    }

    public Logro(final String codigo, final String nombre, final String descripcion, final String iconoUrl, final Integer puntos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.iconoUrl = iconoUrl;
        this.puntos = puntos;
    }

    public Logro(final Integer idLogro, final String codigo, final String nombre, final String descripcion, final String iconoUrl, final Integer puntos) {
        this.idLogro = idLogro;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.iconoUrl = iconoUrl;
        this.puntos = puntos;
    }

    public Integer getIdLogro() {
        return this.idLogro;
    }

    public void setIdLogro(final Integer idLogro) {
        this.idLogro = idLogro;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public void setCodigo(final String codigo) {
        this.codigo = codigo;
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

    public String getIconoUrl() {
        return this.iconoUrl;
    }

    public void setIconoUrl(final String iconoUrl) {
        this.iconoUrl = iconoUrl;
    }

    public Integer getPuntos() {
        return this.puntos;
    }

    public void setPuntos(final Integer puntos) {
        this.puntos = puntos;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Logro logro = (Logro) o;
        return Objects.equals(this.idLogro, logro.idLogro);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idLogro);
    }

    @Override
    public String toString() {
        return "Logro{" +
                "idLogro=" + idLogro +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", iconoUrl='" + iconoUrl + '\'' +
                ", puntos=" + puntos +
                '}';
    }
}

