package com.beatify.service;

import com.beatify.dao.PlaylistDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Playlist;

import java.util.List;

public class PlaylistService implements IPlaylistService {

    private final PlaylistDAO playlistDAO;

    public PlaylistService(PlaylistDAO playlistDAO) {
        this.playlistDAO = playlistDAO;
    }



    private void validar(Playlist playlist) {
        if (playlist == null) {
            throw new ValidacionException("La playlist no puede ser nula");
        }
        if (playlist.getNombre() == null || playlist.getNombre().isBlank()) {
            throw new ValidacionException("El nombre de la playlist es obligatorio");
        }
        if (playlist.getIdCliente() == null || playlist.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para la playlist");
        }
        if (playlist.getPublica() != null
                && !playlist.getPublica().equalsIgnoreCase("S")
                && !playlist.getPublica().equalsIgnoreCase("N")) {
            throw new ValidacionException("El campo 'publica' debe ser 'S' o 'N'");
        }
    }



    public Integer registrar(Playlist playlist) {
        validar(playlist);
        return playlistDAO.insertar(playlist);
    }

    public List<Playlist> listar() {
        return playlistDAO.listar();
    }

    public Playlist buscarPorId(Integer idPlaylist) {
        if (idPlaylist == null || idPlaylist <= 0) {
            throw new ValidacionException("El id de la playlist debe ser un entero positivo");
        }
        return playlistDAO.buscarPorId(idPlaylist);
    }

    public void actualizar(Playlist playlist) {
        if (playlist.getIdPlaylist() == null || playlist.getIdPlaylist() <= 0) {
            throw new ValidacionException("El id de la playlist es obligatorio para actualizar");
        }
        validar(playlist);
        playlistDAO.actualizar(playlist);
    }

    public void eliminar(Integer idPlaylist) {
        if (idPlaylist == null || idPlaylist <= 0) {
            throw new ValidacionException("El id de la playlist debe ser un entero positivo");
        }
        playlistDAO.eliminar(idPlaylist);
    }
}
