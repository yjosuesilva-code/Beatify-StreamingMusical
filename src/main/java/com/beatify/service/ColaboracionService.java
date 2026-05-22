package com.beatify.service;

import com.beatify.dao.ColaboracionDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Colaboracion;

import java.util.List;

public class ColaboracionService implements IColaboracionService {

    private final ColaboracionDAO colaboracionDAO;

    public ColaboracionService(ColaboracionDAO colaboracionDAO) {
        this.colaboracionDAO = colaboracionDAO;
    }

    private void validar(Colaboracion colaboracion) {
        if (colaboracion == null) {
            throw new ValidacionException("La colaboración no puede ser nula");
        }
        if (colaboracion.getIdCancion() == null || colaboracion.getIdCancion() <= 0) {
            throw new ValidacionException("El id de la canción es obligatorio para la colaboración");
        }
        if (colaboracion.getIdArtista() == null || colaboracion.getIdArtista() <= 0) {
            throw new ValidacionException("El id del artista es obligatorio para la colaboración");
        }
        if (colaboracion.getRol() == null || colaboracion.getRol().isBlank()) {
            throw new ValidacionException("El rol del artista en la colaboración es obligatorio");
        }
    }

    public Integer registrar(Colaboracion colaboracion) {
        validar(colaboracion);
        return colaboracionDAO.insertar(colaboracion);
    }

    public List<Colaboracion> listar() {
        return colaboracionDAO.listar();
    }

    public Colaboracion buscarPorId(Integer idColaboracion) {
        if (idColaboracion == null || idColaboracion <= 0) {
            throw new ValidacionException("El id de la colaboración debe ser un entero positivo");
        }
        return colaboracionDAO.buscarPorId(idColaboracion);
    }

    public void actualizar(Colaboracion colaboracion) {
        if (colaboracion.getIdColaboracion() == null || colaboracion.getIdColaboracion() <= 0) {
            throw new ValidacionException("El id de la colaboración es obligatorio para actualizar");
        }
        validar(colaboracion);
        colaboracionDAO.actualizar(colaboracion);
    }

    public void eliminar(Integer idColaboracion) {
        if (idColaboracion == null || idColaboracion <= 0) {
            throw new ValidacionException("El id de la colaboración debe ser un entero positivo");
        }
        colaboracionDAO.eliminar(idColaboracion);
    }
}
