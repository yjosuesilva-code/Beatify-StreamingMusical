package com.beatify.service;

import com.beatify.model.Suscripcion;

import java.util.List;

public interface ISuscripcionService {
    Integer registrar(Suscripcion suscripcion);
    List<Suscripcion> listar();
    Suscripcion buscarPorId(Integer idSuscripcion);
    void actualizar(Suscripcion suscripcion);
    void eliminar(Integer idSuscripcion);
    boolean estaVigente(Suscripcion suscripcion);
}
