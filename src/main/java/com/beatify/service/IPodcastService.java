package com.beatify.service;

import com.beatify.model.Podcast;

import java.util.List;

public interface IPodcastService {
    Integer registrar(Podcast podcast);
    List<Podcast> listar();
    Podcast buscarPorId(Integer idPodcast);
    void actualizar(Podcast podcast);
    void eliminar(Integer idPodcast);
}
