package com.beatify.service;

import com.beatify.model.Notificacion;

import java.util.List;

public interface INotificacionService {
    Integer registrar(Notificacion notificacion);
    List<Notificacion> listar();
    Notificacion buscarPorId(Integer idNotificacion);
    void actualizar(Notificacion notificacion);
    void eliminar(Integer idNotificacion);
}
