package com.beatify.service;

import com.beatify.model.CancionPlaylist;
import com.beatify.model.VotoResena;

import java.util.List;

public interface ICancionPlaylistService {
    Integer registrar(CancionPlaylist cancionPlaylist);
    List<CancionPlaylist> listar();
    CancionPlaylist buscarPorId(Integer cancionPlaylist);
    void actualizar(CancionPlaylist cancionPlaylist);
    void eliminar(Integer cancionPlaylist);
}
