package com.beatify.service;

import com.beatify.dao.AlbumDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Album;

import java.util.List;
import java.time.Year;

public class AlbumService implements IAlbumService {
    private final AlbumDAO albumDAO;

    public AlbumService(AlbumDAO albumDAO) {
        this.albumDAO = albumDAO;
    }

    private void validar(Album album) {
        if (album == null) {
            throw new ValidacionException("El álbum no puede ser nulo");
        }
        if (album.getTitulo() == null || album.getTitulo().isBlank()) {
            throw new ValidacionException("El título del álbum no puede ser nulo o vacío");
        }
        if (album.getAnioLanzamiento() != null){
            int anioActual = Year.now().getValue();
            if (album.getAnioLanzamiento() < 1900 || album.getAnioLanzamiento() > anioActual) {
                throw new ValidacionException("El año de lanzamiento debe estar entre 1900 y " + anioActual);
            }
        }
        if (album.getIdArtista() == null || album.getIdArtista() <= 0) {
            throw new ValidacionException("El ID del artista es obligatorio para el álbum");
        }
    }

    public Integer registrar(Album album) {
        validar(album);
        return albumDAO.insertar(album);
    }

    public List<Album> listar(){
        return albumDAO.listar();
    }

    public Album buscarPorId(Integer idAlbum) {
        if (idAlbum == null || idAlbum <= 0) {
            throw new ValidacionException("El ID del álbum debe ser un número positivo");
        }
        return albumDAO.buscarPorId(idAlbum);
    }

    public void actualizar(Album album) {
        if (album.getIdAlbum() == null || album.getIdAlbum() <= 0) {
            throw new ValidacionException("El ID del álbum es obligatorio para actualizar");
        }
        validar(album);
        albumDAO.actualizar(album);
    }

    public void eliminar(Integer idAlbum) {
        if (idAlbum == null || idAlbum <= 0) {
            throw new ValidacionException("El ID del álbum debe ser un número positivo");
        }
        albumDAO.eliminar(idAlbum);
    }
}
