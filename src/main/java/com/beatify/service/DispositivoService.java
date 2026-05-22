package com.beatify.service;

import com.beatify.dao.DispositivoDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Dispositivo;

import java.util.List;

public class DispositivoService implements IDispositivoService {

    private final DispositivoDAO dispositivoDAO;

    public DispositivoService(DispositivoDAO dispositivoDAO) {
        this.dispositivoDAO = dispositivoDAO;
    }

    private void validar(Dispositivo dispositivo) {
        if (dispositivo == null) {
            throw new ValidacionException("El dispositivo no puede ser nulo");
        }
        if (dispositivo.getNombreDispositivo() == null || dispositivo.getNombreDispositivo().isBlank()) {
            throw new ValidacionException("El nombre del dispositivo es obligatorio");
        }
        if (dispositivo.getTipoDispositivo() == null || dispositivo.getTipoDispositivo().isBlank()) {
            throw new ValidacionException("El tipo del dispositivo es obligatorio");
        }
        if (dispositivo.getIdCliente() == null || dispositivo.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para el dispositivo");
        }
    }

    public Integer registrar(Dispositivo dispositivo) {
        validar(dispositivo);
        return dispositivoDAO.insertar(dispositivo);
    }

    public List<Dispositivo> listar() {
        return dispositivoDAO.listar();
    }

    public Dispositivo buscarPorId(Integer idDispositivo) {
        if (idDispositivo == null || idDispositivo <= 0) {
            throw new ValidacionException("El id del dispositivo debe ser un entero positivo");
        }
        return dispositivoDAO.buscarPorId(idDispositivo);
    }

    public void actualizar(Dispositivo dispositivo) {
        if (dispositivo.getIdDispositivo() == null || dispositivo.getIdDispositivo() <= 0) {
            throw new ValidacionException("El id del dispositivo es obligatorio para actualizar");
        }
        validar(dispositivo);
        dispositivoDAO.actualizar(dispositivo);
    }

    public void eliminar(Integer idDispositivo) {
        if (idDispositivo == null || idDispositivo <= 0) {
            throw new ValidacionException("El id del dispositivo debe ser un entero positivo");
        }
        dispositivoDAO.eliminar(idDispositivo);
    }
}
