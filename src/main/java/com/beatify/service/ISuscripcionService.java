package com.beatify.service;

import com.beatify.model.Suscripcion;
import com.beatify.model.TipoPlan;

import java.util.List;

public interface ISuscripcionService {
    Integer registrar(Suscripcion suscripcion);
    List<Suscripcion> listar();
    Suscripcion buscarPorId(Integer idSuscripcion);
    void actualizar(Suscripcion suscripcion);
    void eliminar(Integer idSuscripcion);
    boolean estaVigente(Suscripcion suscripcion);

    /** Plan efectivo del cliente; {@link TipoPlan#FREE} si no tiene suscripcion vigente. */
    TipoPlan planActual(Integer idCliente);

    /**
     * Cambia el plan del cliente: actualiza su suscripcion activa al nuevo plan
     * (precio y vigencia segun el plan), o crea una si no tiene ninguna activa.
     */
    void cambiarPlan(Integer idCliente, TipoPlan plan);
}
