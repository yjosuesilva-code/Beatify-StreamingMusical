package com.beatify.service;

import com.beatify.model.Playlist;

import java.util.List;

public interface IPlaylistService {
    Integer registrar(Playlist playlist);
    List<Playlist> listar();
    Playlist buscarPorId(Integer idPlaylist);
    void actualizar(Playlist playlist);
    void eliminar(Integer idPlaylist);
}
