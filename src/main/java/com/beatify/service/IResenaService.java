package com.beatify.service;

import com.beatify.model.Resena;

import java.util.List;

public interface IResenaService {
    Integer registrar(Resena resena);
    List<Resena> listar();
    Resena buscarPorId(Integer idResena);
    void actualizar(Resena resena);
    void eliminar(Integer idResena);
}
