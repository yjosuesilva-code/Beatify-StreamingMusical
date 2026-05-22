package com.beatify.service;

import com.beatify.model.Reproduccion;

import java.util.List;

public interface IReproduccionService {
    Integer registrar(Reproduccion reproduccion);
    List<Reproduccion> listar();
    Reproduccion buscarPorId(Integer idReproduccion);
    void actualizar(Reproduccion reproduccion);
    void eliminar(Integer idReproduccion);
}
