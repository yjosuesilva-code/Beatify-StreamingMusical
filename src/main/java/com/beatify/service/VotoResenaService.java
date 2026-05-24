package com.beatify.service;

import com.beatify.dao.VotoResenaDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.VotoResena;

import java.util.List;

public class VotoResenaService implements IVotoResenaService {

    private final VotoResenaDAO votoResenaDAO;

    public VotoResenaService(VotoResenaDAO votoResenaDAO) {
        this.votoResenaDAO = votoResenaDAO;
    }

    private void validar(VotoResena votoResena) {
        if (votoResena == null) {
            throw new ValidacionException("El voto de reseña no puede ser nulo");
        }
        if (votoResena.getIdCliente() == null || votoResena.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para el voto");
        }
        if (votoResena.getIdResena() == null || votoResena.getIdResena() <= 0) {
            throw new ValidacionException("El id de la reseña es obligatorio para el voto");
        }
        if (votoResena.getUtil() == null || votoResena.getUtil().isBlank()) {
            throw new ValidacionException("El campo 'útil' del voto es obligatorio");
        }
        if (!votoResena.getUtil().equalsIgnoreCase("S")
                && !votoResena.getUtil().equalsIgnoreCase("N")) {
            throw new ValidacionException("El campo 'útil' debe ser 'S' o 'N'");
        }
    }

    public Integer registrar(VotoResena votoResena) {
        validar(votoResena);
        return votoResenaDAO.insertar(votoResena);
    }

    public List<VotoResena> listar() {
        return votoResenaDAO.listar();
    }

    public VotoResena buscarPorId(Integer idVotoResena) {
        if (idVotoResena == null || idVotoResena <= 0) {
            throw new ValidacionException("El id del voto debe ser un entero positivo");
        }
        return votoResenaDAO.buscarPorId(idVotoResena);
    }

    public void actualizar(VotoResena votoResena) {
        if (votoResena.getIdVotoResena() == null || votoResena.getIdVotoResena() <= 0) {
            throw new ValidacionException("El id del voto es obligatorio para actualizar");
        }
        validar(votoResena);
        votoResenaDAO.actualizar(votoResena);
    }

    public void eliminar(Integer idVotoResena) {
        if (idVotoResena == null || idVotoResena <= 0) {
            throw new ValidacionException("El id del voto debe ser un entero positivo");
        }
        votoResenaDAO.eliminar(idVotoResena);
    }
}
