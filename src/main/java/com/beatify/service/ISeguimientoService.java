package com.beatify.service;

import com.beatify.model.Seguimiento;

import java.util.List;

public interface ISeguimientoService {
    Integer registrar(Seguimiento seguimiento);
    List<Seguimiento> listar();
    Seguimiento buscarPorId(Integer idSeguimiento);
    void actualizar(Seguimiento seguimiento);
    void eliminar(Integer idSeguimiento);
}
