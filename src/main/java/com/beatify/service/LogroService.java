package com.beatify.service;

import com.beatify.dao.LogroDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Logro;

import java.util.List;

public class LogroService implements ILogroService {

    private final LogroDAO logroDAO;

    public LogroService(LogroDAO logroDAO) {
        this.logroDAO = logroDAO;
    }

    private void validar(Logro logro) {
        if (logro == null) {
            throw new ValidacionException("El logro no puede ser nulo");
        }
        if (logro.getCodigo() == null || logro.getCodigo().isBlank()) {
            throw new ValidacionException("El código del logro es obligatorio");
        }
        if (logro.getNombre() == null || logro.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del logro es obligatorio");
        }
        if (logro.getPuntos() != null && logro.getPuntos() < 0) {
            throw new ValidacionException("Los puntos del logro no pueden ser negativos");
        }
    }

    public Integer registrar(Logro logro) {
        validar(logro);
        return logroDAO.insertar(logro);
    }

    public List<Logro> listar() {
        return logroDAO.listar();
    }

    public Logro buscarPorId(Integer idLogro) {
        if (idLogro == null || idLogro <= 0) {
            throw new ValidacionException("El id del logro debe ser un entero positivo");
        }
        return logroDAO.buscarPorId(idLogro);
    }

    public void actualizar(Logro logro) {
        if (logro.getIdLogro() == null || logro.getIdLogro() <= 0) {
            throw new ValidacionException("El id del logro es obligatorio para actualizar");
        }
        validar(logro);
        logroDAO.actualizar(logro);
    }

    public void eliminar(Integer idLogro) {
        if (idLogro == null || idLogro <= 0) {
            throw new ValidacionException("El id del logro debe ser un entero positivo");
        }
        logroDAO.eliminar(idLogro);
    }
}
