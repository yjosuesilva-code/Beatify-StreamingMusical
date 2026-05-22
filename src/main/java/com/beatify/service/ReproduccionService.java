package com.beatify.service;

import com.beatify.dao.ReproduccionDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Reproduccion;

import java.util.List;

public class ReproduccionService implements IReproduccionService {

    private final ReproduccionDAO reproduccionDAO;

    public ReproduccionService(ReproduccionDAO reproduccionDAO) {
        this.reproduccionDAO = reproduccionDAO;
    }



    private void validar(Reproduccion reproduccion) {
        if (reproduccion == null) {
            throw new ValidacionException("La reproducción no puede ser nula");
        }
        if (reproduccion.getFechaHora() == null) {
            throw new ValidacionException("La fecha y hora de la reproducción son obligatorias");
        }
        if (reproduccion.getDuracionReproduccion() == null
                || reproduccion.getDuracionReproduccion() < 0) {
            throw new ValidacionException(
                    "La duración de reproducción no puede ser negativa");
        }
        if (reproduccion.getIdCliente() == null || reproduccion.getIdCliente() <= 0) {
            throw new ValidacionException(
                    "El id del cliente es obligatorio para la reproducción");
        }
        if (reproduccion.getIdCancion() == null || reproduccion.getIdCancion() <= 0) {
            throw new ValidacionException(
                    "El id de la canción es obligatorio para la reproducción");
        }
    }



    public Integer registrar(Reproduccion reproduccion) {
        validar(reproduccion);
        return reproduccionDAO.insertar(reproduccion);
    }

    public List<Reproduccion> listar() {
        return reproduccionDAO.listar();
    }

    public Reproduccion buscarPorId(Integer idReproduccion) {
        if (idReproduccion == null || idReproduccion <= 0) {
            throw new ValidacionException("El id de la reproducción debe ser un entero positivo");
        }
        return reproduccionDAO.buscarPorId(idReproduccion);
    }

    public void actualizar(Reproduccion reproduccion) {
        if (reproduccion.getIdReproduccion() == null || reproduccion.getIdReproduccion() <= 0) {
            throw new ValidacionException(
                    "El id de la reproducción es obligatorio para actualizar");
        }
        validar(reproduccion);
        reproduccionDAO.actualizar(reproduccion);
    }

    public void eliminar(Integer idReproduccion) {
        if (idReproduccion == null || idReproduccion <= 0) {
            throw new ValidacionException("El id de la reproducción debe ser un entero positivo");
        }
        reproduccionDAO.eliminar(idReproduccion);
    }
}
