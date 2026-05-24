package com.beatify.service;

import com.beatify.dao.LogroClienteDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.LogroCliente;

import java.util.List;

public class LogroClienteService implements ILogroClienteService {

    private final LogroClienteDAO logroClienteDAO;

    public LogroClienteService(LogroClienteDAO logroClienteDAO) {
        this.logroClienteDAO = logroClienteDAO;
    }

    private void validarLogroCliente(LogroCliente logroCliente) {

        if (logroCliente == null) {
            throw new ValidacionException(
                    "El logroCliente no puede ser null");
        }

        if (logroCliente.getIdCliente() == null ||
                logroCliente.getIdCliente() <= 0) {

            throw new ValidacionException(
                    "El id del cliente es obligatorio");
        }

        if (logroCliente.getIdLogro() == null ||
                logroCliente.getIdLogro() <= 0) {

            throw new ValidacionException(
                    "El id del logro es obligatorio");
        }
    }

    public Integer crear(LogroCliente logroCliente) {

        validarLogroCliente(logroCliente);

        return logroClienteDAO.insertar(logroCliente);
    }

    public List<LogroCliente> listar() {
        return logroClienteDAO.listar();
    }


    public LogroCliente buscarPorId(Integer idLogroCliente) {

        if (idLogroCliente == null || idLogroCliente <= 0) {
            throw new ValidacionException("El id del logroCliente es inválido");
        }

        return logroClienteDAO.buscarPorId(idLogroCliente);
    }


    public void actualizar(LogroCliente logroCliente) {

        if (logroCliente == null) {
            throw new ValidacionException("El logroCliente no puede ser null");
        }

        if (logroCliente.getIdLogroCliente() == null ||
                logroCliente.getIdLogroCliente() <= 0) {

            throw new ValidacionException("El id del logroCliente es obligatorio");
        }

        validarLogroCliente(logroCliente);

        logroClienteDAO.actualizar(logroCliente);
    }


    public void eliminar(Integer idLogroCliente) {

        if (idLogroCliente == null || idLogroCliente <= 0) {
            throw new ValidacionException(
                    "El id del logroCliente es inválido");
        }

        logroClienteDAO.eliminar(idLogroCliente);
    }
}