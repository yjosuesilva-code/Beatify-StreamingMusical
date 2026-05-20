package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.LikePlaylist;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LikePlaylistDAO {

    private static final String SQL_INSERT = """
        INSERT INTO LIKE_PLAYLIST
            (id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like)
        VALUES (seq_like_playlist.NEXTVAL, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like
          FROM LIKE_PLAYLIST
         ORDER BY id_like_playlist
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like
          FROM LIKE_PLAYLIST
         WHERE id_like_playlist = ?
        """;

    private static final String SQL_DELETE_BY_KEY = """
        DELETE FROM LIKE_PLAYLIST
         WHERE CLIENTE_id_cliente = ? AND PLAYLIST_id_playlist = ?
        """;

    private static final String SQL_COUNT_BY_PLAYLIST = """
        SELECT COUNT(*) AS total_likes
          FROM LIKE_PLAYLIST
         WHERE PLAYLIST_id_playlist = ?
        """;

    private LikePlaylist mapearResultSet(ResultSet rs) throws SQLException {
        Integer idLikePlaylist = rs.getInt("id_like_playlist");
        Integer idCliente      = rs.getInt("CLIENTE_id_cliente");
        Integer idPlaylist     = rs.getInt("PLAYLIST_id_playlist");

        Timestamp tsSql = rs.getTimestamp("fecha_like");
        LocalDateTime fechaLike = (tsSql != null) ? tsSql.toLocalDateTime() : null;

        return new LikePlaylist(idLikePlaylist, idCliente, idPlaylist, fechaLike);
    }

    public Integer insertar(LikePlaylist likePlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_like_playlist"})) {

            ps.setInt(1, likePlaylist.getIdCliente());
            ps.setInt(2, likePlaylist.getIdPlaylist());

            LocalDateTime fl = likePlaylist.getFechaLike();
            if (fl != null) {
                ps.setTimestamp(3, Timestamp.valueOf(fl));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_like_playlist generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar like de playlist: " + e.getMessage(), e);
        }
    }

    public List<LikePlaylist> listar() {
        List<LikePlaylist> likes = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                likes.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar likes de playlist: " + e.getMessage(), e);
        }
        return likes;
    }

    public LikePlaylist buscarPorId(Integer idLikePlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idLikePlaylist);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("LikePlaylist con id " + idLikePlaylist + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar like de playlist: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idCliente, Integer idPlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_KEY)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idPlaylist);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Like de cliente " + idCliente + " a playlist " + idPlaylist + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar like de playlist: " + e.getMessage(), e);
        }
    }

    public int contarLikesPorPlaylist(Integer idPlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_PLAYLIST)) {
            ps.setInt(1, idPlaylist);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_likes");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al contar likes de la playlist: " + e.getMessage(), e);
        }
    }
}