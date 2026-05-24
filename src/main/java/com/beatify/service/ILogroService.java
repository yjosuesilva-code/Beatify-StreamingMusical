package com.beatify.service;

import com.beatify.model.Logro;

import java.util.List;

public interface ILogroService {
    Integer registrar(Logro logro);
    List<Logro> listar();
    Logro buscarPorId(Integer idLogro);
    void actualizar(Logro logro);
    void eliminar(Integer idLogro);
}
