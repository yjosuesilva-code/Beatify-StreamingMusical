package com.beatify.service;

import com.beatify.dao.CancionDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Cancion;

import java.util.List;

public class CancionService implements ICancionService {

    private final CancionDAO cancionDAO;

    public CancionService(CancionDAO cancionDAO) {
        this.cancionDAO = cancionDAO;
    }

    private void validar(Cancion cancion) {
        if (cancion == null) {
            throw new ValidacionException("La canción no puede ser nula");
        }
        if (cancion.getTitulo() == null || cancion.getTitulo().isBlank()) {
            throw new ValidacionException("El título de la canción es obligatorio");
        }
        if (cancion.getDuracionSegundos() == null || cancion.getDuracionSegundos() <= 0) {
            throw new ValidacionException("La duración de la canción debe ser mayor a 0 segundos");
        }
        if (cancion.getRutaArchivo() == null || cancion.getRutaArchivo().isBlank()) {
            throw new ValidacionException("La ruta del archivo de la canción es obligatoria");
        }
        if (cancion.getIdAlbum() == null || cancion.getIdAlbum() <= 0) {
            throw new ValidacionException("El id del álbum es obligatorio para la canción");
        }
        if (cancion.getIdGenero() == null || cancion.getIdGenero() <= 0) {
            throw new ValidacionException("El id del género es obligatorio para la canción");
        }
    }


    public Integer registrar(Cancion cancion) {
        validar(cancion);
        return cancionDAO.insertar(cancion);
    }

    public List<Cancion> listar() {
        return cancionDAO.listar();
    }

    public Cancion buscarPorId(Integer idCancion) {
        if (idCancion == null || idCancion <= 0) {
            throw new ValidacionException("El id de la canción debe ser un entero positivo");
        }
        return cancionDAO.buscarPorId(idCancion);
    }

    public void actualizar(Cancion cancion) {
        if (cancion.getIdCancion() == null || cancion.getIdCancion() <= 0) {
            throw new ValidacionException("El id de la canción es obligatorio para actualizar");
        }
        validar(cancion);
        cancionDAO.actualizar(cancion);
    }

    public void eliminar(Integer idCancion) {
        if (idCancion == null || idCancion <= 0) {
            throw new ValidacionException("El id de la canción debe ser un entero positivo");
        }
        cancionDAO.eliminar(idCancion);
    }
}
