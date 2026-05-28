package com.beatify.model;

import java.time.LocalDate;
import java.util.Objects;

public class Cliente {
    private Integer idCliente;
    private String nombre;
    private String apellido;
    private String correo;
    private String passwordHash;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private LocalDate fechaRegistro;
    private Boolean activo;
    
    public Cliente(){
    }

    public Cliente(final String nombre, final String apellido, final String correo, final String passwordHash, final String telefono, final String direccion, final String ciudad, final String pais, final Boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
        this.activo = activo != null ? activo : true;
    }

    public Cliente(final Integer idCliente, final String nombre, final String apellido, final String correo, final String passwordHash, final String telefono, final String direccion, final String ciudad, final String pais, final LocalDate fechaRegistro, final Boolean activo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
        this.fechaRegistro = fechaRegistro;
        this.activo = activo;
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente(final Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre( final String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public void setApellido( final String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo( final String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash( final String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono( final String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion(final String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return this.ciudad;
    }

    public void setCiudad(final String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return this.pais;
    }

    public void setPais(final String pais) {
        this.pais = pais;
    }

    public LocalDate getFechaRegistro() {
        return this.fechaRegistro;
    }

    public void setFechaRegistro(final LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public void setActivo(final Boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals( final Object o) {
        if (null == o || this.getClass() != o.getClass()) return false;
         Cliente cliente = (Cliente) o;
        return Objects.equals(this.idCliente, cliente.idCliente);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.idCliente);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", pais='" + pais + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", activo=" + activo +
                '}';
    }
}
