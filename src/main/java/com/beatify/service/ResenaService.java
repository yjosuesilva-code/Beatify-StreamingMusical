package com.beatify.service;

import com.beatify.dao.ResenaDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Resena;

import java.util.List;
import java.util.Set;

public class ResenaService implements IResenaService {

    private static final Set<String> TIPOS_OBJETIVO =
            Set.of("CANCION", "ALBUM", "PLAYLIST", "PODCAST");

    private final ResenaDAO resenaDAO;

    public ResenaService(ResenaDAO resenaDAO) {
        this.resenaDAO = resenaDAO;
    }


    private void validar(Resena resena) {
        if (resena == null) {
            throw new ValidacionException("La reseña no puede ser nula");
        }
        if (resena.getIdCliente() == null || resena.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para la reseña");
        }
        if (resena.getTipoObjetivo() == null || resena.getTipoObjetivo().isBlank()) {
            throw new ValidacionException("El tipo de objetivo de la reseña es obligatorio");
        }
        if (!TIPOS_OBJETIVO.contains(resena.getTipoObjetivo().toUpperCase())) {
            throw new ValidacionException(
                    "El tipo de objetivo debe ser uno de: " + TIPOS_OBJETIVO);
        }
        if (resena.getIdObjetivo() == null || resena.getIdObjetivo() <= 0) {
            throw new ValidacionException("El id del objetivo de la reseña es obligatorio");
        }
        if (resena.getCalificacion() == null
                || resena.getCalificacion() < 1
                || resena.getCalificacion() > 5) {
            throw new ValidacionException("La calificación debe ser un valor entre 1 y 5");
        }
    }



    public Integer registrar(Resena resena) {
        validar(resena);
        return resenaDAO.insertar(resena);
    }

    public List<Resena> listar() {
        return resenaDAO.listar();
    }

    public Resena buscarPorId(Integer idResena) {
        if (idResena == null || idResena <= 0) {
            throw new ValidacionException("El id de la reseña debe ser un entero positivo");
        }
        return resenaDAO.buscarPorId(idResena);
    }

    public void actualizar(Resena resena) {
        if (resena.getIdResena() == null || resena.getIdResena() <= 0) {
            throw new ValidacionException("El id de la reseña es obligatorio para actualizar");
        }
        validar(resena);
        resenaDAO.actualizar(resena);
    }

    public void eliminar(Integer idResena) {
        if (idResena == null || idResena <= 0) {
            throw new ValidacionException("El id de la reseña debe ser un entero positivo");
        }
        resenaDAO.eliminar(idResena);
    }
}
