package com.beatify.service;

import com.beatify.model.Colaboracion;

import java.util.List;

public interface IColaboracionService {
    Integer registrar(Colaboracion colaboracion);
    List<Colaboracion> listar();
    Colaboracion buscarPorId(Integer idColaboracion);
    void actualizar(Colaboracion colaboracion);
    void eliminar(Integer idColaboracion);
}
