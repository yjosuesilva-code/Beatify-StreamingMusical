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
    
    public Cliente(){
    }

    public Cliente( String nombre,  String apellido,  String correo,  String passwordHash,  
                    String telefono,  String direccion,  String ciudad,  String pais) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    public Cliente( Integer idCliente,  String nombre,  String apellido,  String correo,  String passwordHash,  String telefono,  String direccion,  String ciudad,  String pais,  LocalDate fechaRegistro) {
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
    }

    public Integer getIdCliente() {
        return this.idCliente;
    }

    public void setIdCliente( Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre( String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public void setApellido( String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo( String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash( String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono( String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setDireccion( String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return this.ciudad;
    }

    public void setCiudad( String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPais() {
        return this.pais;
    }

    public void setPais( String pais) {
        this.pais = pais;
    }

    public LocalDate getFechaRegistro() {
        return this.fechaRegistro;
    }

    public void setFechaRegistro( LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public boolean equals( Object o) {
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
                '}';
    }
}
