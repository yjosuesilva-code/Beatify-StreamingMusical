package com.beatify.service;

import com.beatify.dao.SuscripcionDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Suscripcion;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class SuscripcionService implements ISuscripcionService {

    private static final Set<String> PLANES_VALIDOS =
            Set.of("FREE", "PREMIUM", "FAMILIAR", "ESTUDIANTE");
    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("ACTIVA", "CANCELADA", "VENCIDA", "PENDIENTE");

    private final SuscripcionDAO suscripcionDAO;

    public SuscripcionService(SuscripcionDAO suscripcionDAO) {
        this.suscripcionDAO = suscripcionDAO;
    }


    private void validar(Suscripcion suscripcion) {
        if (suscripcion == null) {
            throw new ValidacionException("La suscripción no puede ser nula");
        }
        if (suscripcion.getTipoPlan() == null || suscripcion.getTipoPlan().isBlank()) {
            throw new ValidacionException("El tipo de plan de la suscripción es obligatorio");
        }
        if (!PLANES_VALIDOS.contains(suscripcion.getTipoPlan().toUpperCase())) {
            throw new ValidacionException(
                    "El tipo de plan debe ser uno de: " + PLANES_VALIDOS);
        }
        if (suscripcion.getPrecio() == null || suscripcion.getPrecio() < 0) {
            throw new ValidacionException("El precio de la suscripción no puede ser negativo");
        }
        if (suscripcion.getFechaInicio() == null) {
            throw new ValidacionException("La fecha de inicio de la suscripción es obligatoria");
        }
        if (suscripcion.getFechaFin() != null
                && suscripcion.getFechaFin().isBefore(suscripcion.getFechaInicio())) {
            throw new ValidacionException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        if (suscripcion.getEstado() == null || suscripcion.getEstado().isBlank()) {
            throw new ValidacionException("El estado de la suscripción es obligatorio");
        }
        if (!ESTADOS_VALIDOS.contains(suscripcion.getEstado().toUpperCase())) {
            throw new ValidacionException(
                    "El estado debe ser uno de: " + ESTADOS_VALIDOS);
        }
        if (suscripcion.getIdCliente() == null || suscripcion.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para la suscripción");
        }
    }



    public Integer registrar(Suscripcion suscripcion) {
        validar(suscripcion);
        return suscripcionDAO.insertar(suscripcion);
    }

    public List<Suscripcion> listar() {
        return suscripcionDAO.listar();
    }

    public Suscripcion buscarPorId(Integer idSuscripcion) {
        if (idSuscripcion == null || idSuscripcion <= 0) {
            throw new ValidacionException("El id de la suscripción debe ser un entero positivo");
        }
        return suscripcionDAO.buscarPorId(idSuscripcion);
    }

    public void actualizar(Suscripcion suscripcion) {
        if (suscripcion.getIdSuscripcion() == null || suscripcion.getIdSuscripcion() <= 0) {
            throw new ValidacionException("El id de la suscripción es obligatorio para actualizar");
        }
        validar(suscripcion);
        suscripcionDAO.actualizar(suscripcion);
    }

    public void eliminar(Integer idSuscripcion) {
        if (idSuscripcion == null || idSuscripcion <= 0) {
            throw new ValidacionException("El id de la suscripción debe ser un entero positivo");
        }
        suscripcionDAO.eliminar(idSuscripcion);
    }


    
    public boolean estaVigente(Suscripcion suscripcion) {
        if (suscripcion == null) {
            throw new ValidacionException("La suscripción no puede ser nula");
        }
        LocalDate hoy = LocalDate.now();
        boolean inicioValido = !hoy.isBefore(suscripcion.getFechaInicio());
        boolean finValido = suscripcion.getFechaFin() == null
                || !hoy.isAfter(suscripcion.getFechaFin());
        return inicioValido && finValido
                && "ACTIVA".equalsIgnoreCase(suscripcion.getEstado());
    }
}
