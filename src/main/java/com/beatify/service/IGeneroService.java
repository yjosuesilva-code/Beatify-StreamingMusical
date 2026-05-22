package com.beatify.service;

import com.beatify.model.Genero;

import java.util.List;

public interface IGeneroService {
    Integer registrar(Genero genero);
    List<Genero> listar();
    Genero buscarPorId(Integer idGenero);
    void actualizar(Genero genero);
    void eliminar(Integer idGenero);
}
