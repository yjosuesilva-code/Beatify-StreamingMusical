package com.beatify.service;

import com.beatify.dao.LikePlaylistDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.LikePlaylist;

import java.time.LocalDateTime;
import java.util.List;

public class LikePlaylistService {

    private final LikePlaylistDAO likePlaylistDAO;

    public LikePlaylistService(LikePlaylistDAO likePlaylistDAO) {
        this.likePlaylistDAO = likePlaylistDAO;
    }

    private void validarLikePlaylist(LikePlaylist likePlaylist) {

        if (likePlaylist == null) {
            throw new ValidacionException("El like no puede ser null");
        }

        if (likePlaylist.getIdCliente() == null ||
                likePlaylist.getIdCliente() <= 0) {

            throw new ValidacionException("El id del cliente es obligatorio");
        }

        if (likePlaylist.getIdPlaylist() == null ||
                likePlaylist.getIdPlaylist() <= 0) {

            throw new ValidacionException("El id de la playlist es obligatorio");
        }
    }

    public Integer darLike(LikePlaylist likePlaylist) {

        validarLikePlaylist(likePlaylist);

        if (likePlaylist.getFechaLike() == null) {
            likePlaylist.setFechaLike(LocalDateTime.now());
        }

        return likePlaylistDAO.insertar(likePlaylist);
    }

    public List<LikePlaylist> listar() {
        return likePlaylistDAO.listar();
    }


    public LikePlaylist buscarPorId(Integer idLikePlaylist) {

        if (idLikePlaylist == null || idLikePlaylist <= 0) {
            throw new ValidacionException("El id del like es inválido");
        }

        return likePlaylistDAO.buscarPorId(idLikePlaylist);
    }

    public void quitarLike(Integer idCliente, Integer idPlaylist) {

        if (idCliente == null || idCliente <= 0) {
            throw new ValidacionException("El id del cliente es inválido");
        }

        if (idPlaylist == null || idPlaylist <= 0) {
            throw new ValidacionException("El id de la playlist es inválido");
        }
        likePlaylistDAO.eliminar(idCliente, idPlaylist);
    }


    public int contarLikesPorPlaylist(Integer idPlaylist) {

        if (idPlaylist == null || idPlaylist <= 0) {
            throw new ValidacionException("El id de la playlist es inválido");
        }

        return likePlaylistDAO.contarLikesPorPlaylist(idPlaylist);
    }
}