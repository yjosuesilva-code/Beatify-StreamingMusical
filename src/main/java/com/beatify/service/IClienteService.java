package com.beatify.service;

import com.beatify.model.Cliente;

import java.util.List;

public interface IClienteService {
    Integer registrar(Cliente cliente);
    Cliente autenticar(String correo, String password);
    List<Cliente> listar();
    Cliente buscarPorId(Integer idCliente);
    Cliente buscarPorCorreo(String correo);
    void actualizar(Cliente cliente);
    void cambiarPassword(Integer idCliente, String passwordActual, String passwordNueva);
    void resetearPassword(String correo, String passwordNueva);
    Cliente iniciarSesionSSO(String correo);
    void eliminar(Integer idCliente);
}
