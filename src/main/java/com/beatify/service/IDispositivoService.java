package com.beatify.service;

import com.beatify.model.Dispositivo;

import java.util.List;

public interface IDispositivoService {
    Integer registrar(Dispositivo dispositivo);
    List<Dispositivo> listar();
    Dispositivo buscarPorId(Integer idDispositivo);
    void actualizar(Dispositivo dispositivo);
    void eliminar(Integer idDispositivo);
}
