package com.beatify.service;

import com.beatify.dao.CancionPlaylistDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.CancionPlaylist;

import java.time.LocalDate;
import java.util.List;

public class CancionPlaylistService implements ICancionPlaylistService {

    private final CancionPlaylistDAO cancionPlaylistDAO;

    public CancionPlaylistService(CancionPlaylistDAO cancionPlaylistDAO) {
        this.cancionPlaylistDAO = cancionPlaylistDAO;
    }

    private void validarCancionPlaylist(CancionPlaylist cp) {

        if (cp == null) {
            throw new ValidacionException("La relación CancionPlaylist no puede ser null");
        }

        if (cp.getIdPlaylist() == null ||
                cp.getIdPlaylist() <= 0) {

            throw new ValidacionException("El id de la playlist es obligatorio");
        }

        if (cp.getIdCancion() == null ||
                cp.getIdCancion() <= 0) {

            throw new ValidacionException("El id de la canción es obligatorio");
        }

        if (cp.getOrden() != null && cp.getOrden() <= 0) {
            throw new ValidacionException("El orden debe ser mayor que 0");
        }
    }

    public Integer registrar(CancionPlaylist cp) {

        validarCancionPlaylist(cp);

        if (cp.getFechaAgregada() == null) {
            cp.setFechaAgregada(LocalDate.now());
        }

        return cancionPlaylistDAO.insertar(cp);
    }


    public List<CancionPlaylist> listar() {
        return cancionPlaylistDAO.listar();
    }


    public CancionPlaylist buscarPorId(Integer idCancionPlaylist) {

        if (idCancionPlaylist == null || idCancionPlaylist <= 0) {
            throw new ValidacionException("El id de CancionPlaylist es inválido");
        }

        return cancionPlaylistDAO.buscarPorId(idCancionPlaylist);
    }

    public void actualizar(CancionPlaylist cp) {

        if (cp.getIdCancionPlaylist() == null ||
                cp.getIdCancionPlaylist() <= 0) {

            throw new ValidacionException("El id de CancionPlaylist es obligatorio");
        }

        validarCancionPlaylist(cp);

        cancionPlaylistDAO.actualizar(cp);
    }

    public void eliminar(Integer idCancionPlaylist) {

        if (idCancionPlaylist == null || idCancionPlaylist <= 0) {
            throw new ValidacionException("El id de CancionPlaylist es inválido");
        }

        cancionPlaylistDAO.eliminar(idCancionPlaylist);
    }


}