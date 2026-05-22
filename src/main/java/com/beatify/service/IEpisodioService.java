package com.beatify.service;

import com.beatify.model.Episodio;

import java.util.List;

public interface IEpisodioService {
    Integer registrar(Episodio episodio);
    List<Episodio> listar();
    Episodio buscarPorId(Integer idEpisodio);
    void actualizar(Episodio episodio);
    void eliminar(Integer idEpisodio);
}
