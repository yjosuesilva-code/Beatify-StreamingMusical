package com.beatify.service;

import com.beatify.dao.LikeAlbumDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.LikeAlbum;

import java.time.LocalDateTime;
import java.util.List;

public class LikeAlbumService implements ILikeAlbumService{

    private final LikeAlbumDAO likeAlbumDAO;

    public LikeAlbumService(LikeAlbumDAO likeAlbumDAO) {
        this.likeAlbumDAO = likeAlbumDAO;
    }

    private void validarLikeAlbum(LikeAlbum likeAlbum) {

        if (likeAlbum == null) {
            throw new ValidacionException(
                    "El like no puede ser null");
        }

        if (likeAlbum.getIdCliente() == null ||
                likeAlbum.getIdCliente() <= 0) {

            throw new ValidacionException(
                    "El id del cliente es obligatorio");
        }

        if (likeAlbum.getIdAlbum() == null ||
                likeAlbum.getIdAlbum() <= 0) {

            throw new ValidacionException(
                    "El id del álbum es obligatorio");
        }
    }

    public Integer darLike(LikeAlbum likeAlbum) {

        validarLikeAlbum(likeAlbum);

        if (likeAlbum.getFechaLike() == null) {
            likeAlbum.setFechaLike(LocalDateTime.now());
        }

        return likeAlbumDAO.insertar(likeAlbum);
    }



    public List<LikeAlbum> listar() {
        return likeAlbumDAO.listar();
    }

    public LikeAlbum buscarPorId(Integer idLikeAlbum) {

        if (idLikeAlbum == null || idLikeAlbum <= 0) {
            throw new ValidacionException(
                    "El id del like es inválido");
        }

        return likeAlbumDAO.buscarPorId(idLikeAlbum);
    }

    public void quitarLike(Integer idCliente, Integer idAlbum) {

        if (idCliente == null || idCliente <= 0) {
            throw new ValidacionException(
                    "El id del cliente es inválido");
        }

        if (idAlbum == null || idAlbum <= 0) {
            throw new ValidacionException(
                    "El id del álbum es inválido");
        }

        likeAlbumDAO.eliminar(idCliente, idAlbum);
    }

    public int contarLikesPorAlbum(Integer idAlbum) {

        if (idAlbum == null || idAlbum <= 0) {
            throw new ValidacionException(
                    "El id del álbum es inválido");
        }

        return likeAlbumDAO.contarLikesPorAlbum(idAlbum);
    }
}