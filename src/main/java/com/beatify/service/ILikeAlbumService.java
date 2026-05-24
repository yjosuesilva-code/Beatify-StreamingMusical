package com.beatify.service;

import com.beatify.model.LikeAlbum;

import java.util.List;

public interface ILikeAlbumService {
     Integer darLike(LikeAlbum likeAlbum);
    List<LikeAlbum> listar();
    LikeAlbum buscarPorId(Integer idLikeAlbum);
    void quitarLike(Integer idCliente, Integer idAlbum);
    int contarLikesPorAlbum(Integer idAlbum);
}
