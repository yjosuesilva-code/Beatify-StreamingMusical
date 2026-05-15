package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Artista {
    private Integer idArtista;
    private String nombre;
    private String apellido;
    private String nombreArtistico;
    private LocalDate fechaNacimiento;
    private String pais;
    private String correo;
    private String biografia;
    private String fotoUrl;

    public Artista() {
    }

    public Artista(final String nombre, final String apellido, final String nombreArtistico,  final String pais, final String correo, final String biografia, final String fotoUrl) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreArtistico = nombreArtistico;
        this.pais = pais;
        this.correo = correo;
        this.biografia = biografia;
        this.fotoUrl = fotoUrl;
    }

    public Artista(final Integer idArtista, final String nombre, final String apellido, final String nombreArtistico, final LocalDate fechaNacimiento, final String pais, final String correo, final String biografia, final String fotoUrl) {
        this.idArtista = idArtista;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nombreArtistico = nombreArtistico;
        this.fechaNacimiento = fechaNacimiento;
        this.pais = pais;
        this.correo = correo;
        this.biografia = biografia;
        this.fotoUrl = fotoUrl;
    }

    public Integer getIdArtista() {
        return this.idArtista;
    }

    public void setIdArtista(final Integer idArtista) {
        this.idArtista = idArtista;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public void setApellido(final String apellido) {
        this.apellido = apellido;
    }

    public String getNombreArtistico() {
        return this.nombreArtistico;
    }

    public void setNombreArtistico(final String nombreArtistico) {
        this.nombreArtistico = nombreArtistico;
    }

    public LocalDate getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public void setFechaNacimiento(final LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getPais() {
        return this.pais;
    }

    public void setPais(final String pais) {
        this.pais = pais;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo(final String correo) {
        this.correo = correo;
    }

    public String getBiografia() {
        return this.biografia;
    }

    public void setBiografia(final String biografia) {
        this.biografia = biografia;
    }

    public String getFotoUrl() {
        return this.fotoUrl;
    }

    public void setFotoUrl(final String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public boolean equals(final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
        final Artista artista = (Artista) o;
        return Objects.equals(this.idArtista, artista.idArtista);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idArtista);
    }

    @Override
    public String toString() {
        return "Artista{" +
                "idArtista=" + idArtista +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", nombreArtistico='" + nombreArtistico + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", pais='" + pais + '\'' +
                ", correo='" + correo + '\'' +
                ", biografia='" + biografia + '\'' +
                ", fotoUrl='" + fotoUrl + '\'' +
                '}';
    }
}
