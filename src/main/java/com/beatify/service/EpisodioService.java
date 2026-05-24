package com.beatify.service;

import com.beatify.dao.EpisodioDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Episodio;

import java.util.List;

public class EpisodioService implements IEpisodioService {

    private final EpisodioDAO episodioDAO;

    public EpisodioService(EpisodioDAO episodioDAO) {
        this.episodioDAO = episodioDAO;
    }

    private void validar(Episodio episodio) {
        if (episodio == null) {
            throw new ValidacionException("El episodio no puede ser nulo");
        }
        if (episodio.getTitulo() == null || episodio.getTitulo().isBlank()) {
            throw new ValidacionException("El título del episodio es obligatorio");
        }
        if (episodio.getNumeroEpisodio() == null || episodio.getNumeroEpisodio() <= 0) {
            throw new ValidacionException("El número de episodio debe ser mayor a 0");
        }
        if (episodio.getDuracion() == null || episodio.getDuracion() <= 0) {
            throw new ValidacionException("La duración del episodio debe ser mayor a 0 segundos");
        }
        if (episodio.getRutaArchivo() == null || episodio.getRutaArchivo().isBlank()) {
            throw new ValidacionException("La ruta del archivo del episodio es obligatoria");
        }
        if (episodio.getIdPodcast() == null || episodio.getIdPodcast() <= 0) {
            throw new ValidacionException("El id del podcast es obligatorio para el episodio");
        }
    }

    public Integer registrar(Episodio episodio) {
        validar(episodio);
        return episodioDAO.insertar(episodio);
    }

    public List<Episodio> listar() {
        return episodioDAO.listar();
    }

    public Episodio buscarPorId(Integer idEpisodio) {
        if (idEpisodio == null || idEpisodio <= 0) {
            throw new ValidacionException("El id del episodio debe ser un entero positivo");
        }
        return episodioDAO.buscarPorId(idEpisodio);
    }

    public void actualizar(Episodio episodio) {
        if (episodio.getIdEpisodio() == null || episodio.getIdEpisodio() <= 0) {
            throw new ValidacionException("El id del episodio es obligatorio para actualizar");
        }
        validar(episodio);
        episodioDAO.actualizar(episodio);
    }

    public void eliminar(Integer idEpisodio) {
        if (idEpisodio == null || idEpisodio <= 0) {
            throw new ValidacionException("El id del episodio debe ser un entero positivo");
        }
        episodioDAO.eliminar(idEpisodio);
    }
}
