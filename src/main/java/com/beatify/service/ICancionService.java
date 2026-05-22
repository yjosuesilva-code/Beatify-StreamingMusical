package com.beatify.service;

import com.beatify.model.Cancion;

import java.util.List;

public interface ICancionService {
    Integer registrar(Cancion cancion);
    List<Cancion> listar();
    Cancion buscarPorId(Integer idCancion);
    void actualizar(Cancion cancion);
    void eliminar(Integer idCancion);
}
