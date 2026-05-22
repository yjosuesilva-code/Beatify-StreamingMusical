package com.beatify.service;

import com.beatify.model.Busqueda;

import java.util.List;

public interface IBusquedaService {
    Integer registrar(Busqueda busqueda);
    List<Busqueda> listar();
    Busqueda buscarPorId(Integer idBusqueda);
    void actualizar(Busqueda busqueda);
    void eliminar(Integer idBusqueda);
}
