package com.beatify.service;

import com.beatify.model.LikePlaylist;

import java.util.List;

public interface ILikePlaylistService {
    Integer darLike(LikePlaylist likePlaylist);
    List<LikePlaylist> listar();
    LikePlaylist buscarPorId(Integer idLikePlaylist);
    void quitarLike(Integer idCliente, Integer idPlaylist);
    int contarLikesPorPlaylist(Integer idPlaylist);
}
