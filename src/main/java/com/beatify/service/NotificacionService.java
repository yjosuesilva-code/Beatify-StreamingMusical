package com.beatify.service;

import com.beatify.dao.NotificacionDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Notificacion;

import java.util.List;
import java.util.Set;

public class NotificacionService implements INotificacionService {

    private static final Set<String> TIPOS_VALIDOS =
            Set.of("INFO", "PROMO", "RECOMENDACION", "SISTEMA");

    private final NotificacionDAO notificacionDAO;

    public NotificacionService(NotificacionDAO notificacionDAO) {
        this.notificacionDAO = notificacionDAO;
    }

    private void validar(Notificacion notificacion) {
        if (notificacion == null) {
            throw new ValidacionException("La notificación no puede ser nula");
        }
        if (notificacion.getTitulo() == null || notificacion.getTitulo().isBlank()) {
            throw new ValidacionException("El título de la notificación es obligatorio");
        }
        if (notificacion.getMensaje() == null || notificacion.getMensaje().isBlank()) {
            throw new ValidacionException("El mensaje de la notificación es obligatorio");
        }
        if (notificacion.getTipo() == null || notificacion.getTipo().isBlank()) {
            throw new ValidacionException("El tipo de la notificación es obligatorio");
        }
        if (!TIPOS_VALIDOS.contains(notificacion.getTipo().toUpperCase())) {
            throw new ValidacionException("El tipo debe ser uno de: " + TIPOS_VALIDOS);
        }
        if (notificacion.getIdCliente() == null || notificacion.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para la notificación");
        }
        if (notificacion.getLeida() != null
                && !notificacion.getLeida().equalsIgnoreCase("S")
                && !notificacion.getLeida().equalsIgnoreCase("N")) {
            throw new ValidacionException("El campo 'leida' debe ser 'S' o 'N'");
        }
    }

    public Integer registrar(Notificacion notificacion) {
        validar(notificacion);
        return notificacionDAO.insertar(notificacion);
    }

    public List<Notificacion> listar() {
        return notificacionDAO.listar();
    }

    public Notificacion buscarPorId(Integer idNotificacion) {
        if (idNotificacion == null || idNotificacion <= 0) {
            throw new ValidacionException("El id de la notificación debe ser un entero positivo");
        }
        return notificacionDAO.buscarPorId(idNotificacion);
    }

    public void actualizar(Notificacion notificacion) {
        if (notificacion.getIdNotificacion() == null || notificacion.getIdNotificacion() <= 0) {
            throw new ValidacionException("El id de la notificación es obligatorio para actualizar");
        }
        validar(notificacion);
        notificacionDAO.actualizar(notificacion);
    }

    public void eliminar(Integer idNotificacion) {
        if (idNotificacion == null || idNotificacion <= 0) {
            throw new ValidacionException("El id de la notificación debe ser un entero positivo");
        }
        notificacionDAO.eliminar(idNotificacion);
    }
}
