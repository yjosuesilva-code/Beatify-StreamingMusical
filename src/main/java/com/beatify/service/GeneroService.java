package com.beatify.service;

import com.beatify.dao.GeneroDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Genero;

import java.util.List;

public class GeneroService implements IGeneroService {

    private final GeneroDAO generoDAO;

    public GeneroService(GeneroDAO generoDAO) {
        this.generoDAO = generoDAO;
    }

    private void validar(Genero genero) {
        if (genero == null) {
            throw new ValidacionException("El género no puede ser nulo");
        }
        if (genero.getNombre() == null || genero.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del género es obligatorio");
        }
    }

    public Integer registrar(Genero genero) {
        validar(genero);
        return generoDAO.insertar(genero);
    }

    public List<Genero> listar() {
        return generoDAO.listar();
    }

    public Genero buscarPorId(Integer idGenero) {
        if (idGenero == null || idGenero <= 0) {
            throw new ValidacionException("El id del género debe ser un entero positivo");
        }
        return generoDAO.buscarPorId(idGenero);
    }

    public void actualizar(Genero genero) {
        if (genero.getIdGenero() == null || genero.getIdGenero() <= 0) {
            throw new ValidacionException("El id del género es obligatorio para actualizar");
        }
        validar(genero);
        generoDAO.actualizar(genero);
    }

    public void eliminar(Integer idGenero) {
        if (idGenero == null || idGenero <= 0) {
            throw new ValidacionException("El id del género debe ser un entero positivo");
        }
        generoDAO.eliminar(idGenero);
    }
}
