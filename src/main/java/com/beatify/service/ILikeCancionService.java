package com.beatify.service;

import com.beatify.model.LikeCancion;

import java.util.List;

public interface ILikeCancionService {
    Integer darLike(LikeCancion likeCancion);
    List<LikeCancion> listar();
    LikeCancion buscarPorId(Integer idLikeCancion);
    void quitarLike(Integer idCliente, Integer idCancion);
    int contarLikesPorCancion(Integer idCancion);

}
