package com.beatify.service;

import com.beatify.dao.LikeCancionDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.LikeCancion;

import java.time.LocalDateTime;
import java.util.List;

public class LikeCancionService implements ILikeCancionService {

    private final LikeCancionDAO likeCancionDAO;

    public LikeCancionService(LikeCancionDAO likeCancionDAO) {
        this.likeCancionDAO = likeCancionDAO;
    }

    private void validarLikeCancion(LikeCancion likeCancion) {

        if (likeCancion == null) {
            throw new ValidacionException(
                    "El like no puede ser null");
        }

        if (likeCancion.getIdCliente() == null ||
                likeCancion.getIdCliente() <= 0) {

            throw new ValidacionException(
                    "El id del cliente es obligatorio");
        }

        if (likeCancion.getIdCancion() == null ||
                likeCancion.getIdCancion() <= 0) {

            throw new ValidacionException(
                    "El id de la canción es obligatorio");
        }
    }

    public Integer darLike(LikeCancion likeCancion) {

        validarLikeCancion(likeCancion);

        if (likeCancion.getFechaLike() == null) {
            likeCancion.setFechaLike(LocalDateTime.now());
        }

        return likeCancionDAO.insertar(likeCancion);
    }


    public List<LikeCancion> listar() {
        return likeCancionDAO.listar();
    }


    public LikeCancion buscarPorId(Integer idLikeCancion) {

        if (idLikeCancion == null || idLikeCancion <= 0) {
            throw new ValidacionException(
                    "El id del like es inválido");
        }

        return likeCancionDAO.buscarPorId(idLikeCancion);
    }


    public void quitarLike(Integer idCliente, Integer idCancion) {

        if (idCliente == null || idCliente <= 0) {
            throw new ValidacionException(
                    "El id del cliente es inválido");
        }

        if (idCancion == null || idCancion <= 0) {
            throw new ValidacionException(
                    "El id de la canción es inválido");
        }

        likeCancionDAO.eliminar(idCliente, idCancion);
    }


    public int contarLikesPorCancion(Integer idCancion) {

        if (idCancion == null || idCancion <= 0) {
            throw new ValidacionException(
                    "El id de la canción es inválido");
        }

        return likeCancionDAO.contarLikesPorCancion(idCancion);
    }
}