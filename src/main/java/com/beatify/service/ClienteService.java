package com.beatify.service;

import com.beatify.dao.ClienteDAO;
import com.beatify.exceptions.AutenticacionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Cliente;
import com.beatify.util.PasswordUtil;

import java.time.LocalDate;
import java.util.List;

public class ClienteService implements IClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }


    public Integer registrar(Cliente cliente) {
        validarRegistro(cliente);
        if (cliente.getPasswordHash().length() < 6) {
            throw new ValidacionException(
                    "La contraseña debe tener al menos 6 caracteres");
        }
        try {
            clienteDAO.buscarPorCorreo(cliente.getCorreo());

            throw new ValidacionException(
                    "Ya existe un cliente con ese correo");

        } catch (NotFoundException e) {
            // correcto, el correo no existe
        }
        cliente.setPasswordHash(
                PasswordUtil.hash(cliente.getPasswordHash()));
        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(LocalDate.now());
        }
        return clienteDAO.insertar(cliente);
    }



    public Cliente autenticar(String correo, String password) {
        try {
            Cliente cliente = clienteDAO.buscarPorCorreo(correo);
            if (!PasswordUtil.verificar(password, cliente.getPasswordHash())) {
                throw new AutenticacionException("Correo o contraseña incorrectos");
            }
            if (Boolean.FALSE.equals(cliente.getActivo())) {
                throw new AutenticacionException("Correo o contraseña incorrectos");
            }
            return cliente;
        } catch (NotFoundException e) {
            throw new AutenticacionException("Correo o contraseña incorrectos");
        }
    }



    public List<Cliente> listar() {
        return clienteDAO.listar();
    }

    public Cliente buscarPorId(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new ValidacionException("El id del cliente es inválido");
        }
        return clienteDAO.buscarPorId(idCliente);
    }

    public void actualizar(Cliente cliente) {
        if (cliente.getIdCliente() == null || cliente.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio");
        }
        validarActualizacion(cliente);
        clienteDAO.actualizar(cliente);
    }

    public void cambiarPassword(Integer idCliente, String passwordActual, String passwordNueva) {
        if (idCliente == null || idCliente <= 0) {
            throw new ValidacionException("El id del cliente es inválido");
        }
        if (passwordActual == null || passwordActual.isBlank()) {
            throw new ValidacionException("La contraseña actual es obligatoria");
        }
        if (passwordNueva == null || passwordNueva.isBlank()) {
            throw new ValidacionException("La nueva contraseña es obligatoria");
        }

        Cliente cliente = clienteDAO.buscarPorId(idCliente);

        if (!PasswordUtil.verificar(passwordActual, cliente.getPasswordHash())) {
            throw new AutenticacionException("La contraseña actual es incorrecta");
        }

        // actualizar() ya NO toca password_hash; usamos el metodo dedicado
        clienteDAO.actualizarPassword(idCliente, PasswordUtil.hash(passwordNueva));
    }

    public void eliminar(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new ValidacionException("El id del cliente es inválido");
        }
        clienteDAO.eliminar(idCliente);
    }

    private void validarRegistro(Cliente cliente) {
        validarDatosBasicos(cliente);

        if (cliente.getPasswordHash() == null ||
                cliente.getPasswordHash().trim().isEmpty()) {
            throw new ValidacionException("La contraseña es obligatoria");
        }
    }

    private void validarDatosBasicos(Cliente cliente) {
        if (cliente == null) {
            throw new ValidacionException("El cliente no puede ser null");
        }

        if (cliente.getNombre() == null ||
                cliente.getNombre().trim().isEmpty()) {
            throw new ValidacionException("El nombre es obligatorio");
        }

        if (cliente.getApellido() == null ||
                cliente.getApellido().trim().isEmpty()) {
            throw new ValidacionException("El apellido es obligatorio");
        }

        if (cliente.getCorreo() == null ||
                cliente.getCorreo().trim().isEmpty()) {
            throw new ValidacionException("El correo es obligatorio");
        }
        if (!cliente.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidacionException("Formato de correo inválido");
        }
    }
    private void validarActualizacion(Cliente cliente) {
        validarDatosBasicos(cliente);
    }
}
