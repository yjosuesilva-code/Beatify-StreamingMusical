package com.beatify.service;

import com.beatify.dao.PodcastDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Podcast;

import java.util.List;

public class PodcastService implements IPodcastService {

    private final PodcastDAO podcastDAO;

    public PodcastService(PodcastDAO podcastDAO) {
        this.podcastDAO = podcastDAO;
    }

    private void validar(Podcast podcast) {
        if (podcast == null) {
            throw new ValidacionException("El podcast no puede ser nulo");
        }
        if (podcast.getTitulo() == null || podcast.getTitulo().isBlank()) {
            throw new ValidacionException("El título del podcast es obligatorio");
        }
        if (podcast.getIdArtista() == null || podcast.getIdArtista() <= 0) {
            throw new ValidacionException("El id del artista es obligatorio para el podcast");
        }
    }

    public Integer registrar(Podcast podcast) {
        validar(podcast);
        return podcastDAO.insertar(podcast);
    }

    public List<Podcast> listar() {
        return podcastDAO.listar();
    }

    public Podcast buscarPorId(Integer idPodcast) {
        if (idPodcast == null || idPodcast <= 0) {
            throw new ValidacionException("El id del podcast debe ser un entero positivo");
        }
        return podcastDAO.buscarPorId(idPodcast);
    }

    public void actualizar(Podcast podcast) {
        if (podcast.getIdPodcast() == null || podcast.getIdPodcast() <= 0) {
            throw new ValidacionException("El id del podcast es obligatorio para actualizar");
        }
        validar(podcast);
        podcastDAO.actualizar(podcast);
    }

    public void eliminar(Integer idPodcast) {
        if (idPodcast == null || idPodcast <= 0) {
            throw new ValidacionException("El id del podcast debe ser un entero positivo");
        }
        podcastDAO.eliminar(idPodcast);
    }
}
