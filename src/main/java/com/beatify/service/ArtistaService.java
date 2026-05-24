package com.beatify.service;

import com.beatify.dao.ArtistaDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Artista;

import java.util.List;

public class ArtistaService implements IArtistaService {

    private final ArtistaDAO artistaDAO;

    public ArtistaService(ArtistaDAO artistaDAO) {
        this.artistaDAO = artistaDAO;
    }

    private void validar(Artista artista) {
        if (artista == null) {
            throw new ValidacionException("El artista no puede ser nulo");
        }
        if (artista.getNombre() == null || artista.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del artista es obligatorio");
        }
        if (artista.getNombreArtistico() == null || artista.getNombreArtistico().isBlank()) {
            throw new ValidacionException("El nombre artístico del artista es obligatorio");
        }
        if (artista.getCorreo() != null && !artista.getCorreo().isBlank()
                && !artista.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ValidacionException("El formato del correo del artista no es válido");
        }
    }

    public Integer registrar(Artista artista) {
        validar(artista);
        return artistaDAO.insertar(artista);
    }

    public List<Artista> listar() {
        return artistaDAO.listar();
    }

    public Artista buscarPorId(Integer idArtista) {
        if (idArtista == null || idArtista <= 0) {
            throw new ValidacionException("El id del artista debe ser un entero positivo");
        }
        return artistaDAO.buscarPorId(idArtista);
    }

    public void actualizar(Artista artista) {
        if (artista.getIdArtista() == null || artista.getIdArtista() <= 0) {
            throw new ValidacionException("El id del artista es obligatorio para actualizar");
        }
        validar(artista);
        artistaDAO.actualizar(artista);
    }

    public void eliminar(Integer idArtista) {
        if (idArtista == null || idArtista <= 0) {
            throw new ValidacionException("El id del artista debe ser un entero positivo");
        }
        artistaDAO.eliminar(idArtista);
    }
}
