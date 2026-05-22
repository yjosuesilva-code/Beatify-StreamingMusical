package com.beatify.service;

import com.beatify.model.LogroCliente;

import java.util.List;

public interface ILogroClienteService {
    Integer crear(LogroCliente logroCliente);
    List<LogroCliente> listar();
    LogroCliente buscarPorId(Integer idLogroCliente);
    void actualizar(LogroCliente logroCliente);
    void eliminar(Integer idLogroCliente);
}
