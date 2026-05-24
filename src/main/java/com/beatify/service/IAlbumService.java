package com.beatify.service;

import com.beatify.model.Album;

import java.util.List;

public interface IAlbumService {
    Integer registrar(Album album);
    List<Album> listar();
    Album buscarPorId(Integer idAlbum);
    void actualizar(Album album);
    void eliminar(Integer idAlbum);
}
