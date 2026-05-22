package com.beatify.service;

import com.beatify.dao.ArtistaGeneroDAO;
import com.beatify.exceptions.NotFoundException;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.ArtistaGenero;

import java.util.List;

public class ArtistaGeneroService implements IArtistaGeneroService {

    private final ArtistaGeneroDAO artistaGeneroDAO;

    public ArtistaGeneroService(ArtistaGeneroDAO artistaGeneroDAO) {
        this.artistaGeneroDAO = artistaGeneroDAO;
    }

    public void crearRelacion(Integer idArtista, Integer idGenero) {

        if (idArtista == null || idArtista <= 0) {
            throw new ValidacionException("El id del artista es obligatorio");
        }

        if (idGenero == null || idGenero <= 0) {
            throw new ValidacionException("El id del género es obligatorio");
        }

        artistaGeneroDAO.insertar(idArtista, idGenero);
    }

    public List<ArtistaGenero> listar() {
        return artistaGeneroDAO.listar();
    }


    public List<ArtistaGenero> listarPorArtista(Integer idArtista) {

        if (idArtista == null || idArtista <= 0) {
            throw new ValidacionException("El id del artista es inválido");
        }
        List<ArtistaGenero> relaciones =
                artistaGeneroDAO.listarPorArtista(idArtista);
        if (relaciones.isEmpty()) {
            throw new NotFoundException("El artista no tiene géneros asociados");
        }
        return relaciones;
    }

    public void eliminarRelacion(Integer idArtista, Integer idGenero) {

        if (idArtista == null || idArtista <= 0) {
            throw new ValidacionException("El id del artista es inválido");
        }

        if (idGenero == null || idGenero <= 0) {
            throw new ValidacionException("El id del género es inválido");
        }
        artistaGeneroDAO.eliminar(idArtista, idGenero);
    }
}