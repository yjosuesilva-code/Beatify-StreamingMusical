package com.beatify.service;

import com.beatify.model.VotoResena;

import java.util.List;

public interface IVotoResenaService {
    Integer registrar(VotoResena votoResena);
    List<VotoResena> listar();
    VotoResena buscarPorId(Integer idVotoResena);
    void actualizar(VotoResena votoResena);
    void eliminar(Integer idVotoResena);
}
