package com.beatify.service;

import com.beatify.model.Artista;

import java.util.List;

public interface IArtistaService {
    Integer registrar(Artista artista);
    List<Artista> listar();
    Artista buscarPorId(Integer idArtista);
    void actualizar(Artista artista);
    void eliminar(Integer idArtista);
}
