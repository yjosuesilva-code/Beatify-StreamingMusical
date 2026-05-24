package com.beatify.service;

import com.beatify.dao.SeguimientoDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Seguimiento;

import java.util.List;

public class SeguimientoService implements ISeguimientoService {

    private final SeguimientoDAO seguimientoDAO;

    public SeguimientoService(SeguimientoDAO seguimientoDAO) {
        this.seguimientoDAO = seguimientoDAO;
    }

    private void validar(Seguimiento seguimiento) {
        if (seguimiento == null) {
            throw new ValidacionException("El seguimiento no puede ser nulo");
        }
        if (seguimiento.getIdCliente() == null || seguimiento.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para el seguimiento");
        }
        if (seguimiento.getIdArtista() == null || seguimiento.getIdArtista() <= 0) {
            throw new ValidacionException("El id del artista es obligatorio para el seguimiento");
        }
        if (seguimiento.getIdCliente().equals(seguimiento.getIdArtista())) {
            throw new ValidacionException("Un cliente no puede seguirse a sí mismo como artista");
        }
    }

    public Integer registrar(Seguimiento seguimiento) {
        validar(seguimiento);
        return seguimientoDAO.insertar(seguimiento);
    }

    public List<Seguimiento> listar() {
        return seguimientoDAO.listar();
    }

    public Seguimiento buscarPorId(Integer idSeguimiento) {
        if (idSeguimiento == null || idSeguimiento <= 0) {
            throw new ValidacionException("El id del seguimiento debe ser un entero positivo");
        }
        return seguimientoDAO.buscarPorId(idSeguimiento);
    }

    public void actualizar(Seguimiento seguimiento) {
        if (seguimiento.getIdSeguimiento() == null || seguimiento.getIdSeguimiento() <= 0) {
            throw new ValidacionException("El id del seguimiento es obligatorio para actualizar");
        }
        validar(seguimiento);
        seguimientoDAO.actualizar(seguimiento);
    }

    public void eliminar(Integer idSeguimiento) {
        if (idSeguimiento == null || idSeguimiento <= 0) {
            throw new ValidacionException("El id del seguimiento debe ser un entero positivo");
        }
        seguimientoDAO.eliminar(idSeguimiento);
    }
}
