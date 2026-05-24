package com.beatify.service;

import com.beatify.model.Pago;

import java.util.List;

public interface IPagoService {
    Integer registrar(Pago pago);
    List<Pago> listar();
    Pago buscarPorId(Integer idPago);
    void actualizar(Pago pago);
    void eliminar(Integer idPago);
}
