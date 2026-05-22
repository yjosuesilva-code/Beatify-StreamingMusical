package com.beatify.service;

import com.beatify.model.ArtistaGenero;

import java.util.List;

public interface IArtistaGeneroService {
    void crearRelacion(Integer idArtista, Integer idGenero);
    List<ArtistaGenero> listar();
    List<ArtistaGenero> listarPorArtista(Integer idArtista);
    void eliminarRelacion(Integer idArtista, Integer idGenero);
}
