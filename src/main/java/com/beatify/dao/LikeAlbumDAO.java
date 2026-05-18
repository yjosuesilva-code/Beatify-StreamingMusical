package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.LikeAlbum;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LikeAlbumDAO {

    private static final String SQL_INSERT = """
        INSERT INTO LIKE_ALBUM
            (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like)
        VALUES (seq_like_album.NEXTVAL, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like
          FROM LIKE_ALBUM
         ORDER BY id_like_ALBUM
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like
          FROM LIKE_ALBUM
         WHERE id_like_ALBUM = ?
        """;

    private static final String SQL_DELETE_BY_KEY = """
        DELETE FROM LIKE_ALBUM
         WHERE CLIENTE_id_cliente = ? AND ALBUM_id_album = ?
        """;

    private static final String SQL_COUNT_BY_ALBUM = """
        SELECT COUNT(*) AS total_likes
          FROM LIKE_ALBUM
         WHERE ALBUM_id_album = ?
        """;

    private LikeAlbum mapearResultSet(ResultSet rs) throws SQLException {
        Integer idLikeAlbum = rs.getInt("id_like_ALBUM");
        Integer idCliente   = rs.getInt("CLIENTE_id_cliente");
        Integer idAlbum     = rs.getInt("ALBUM_id_album");

        Timestamp tsSql = rs.getTimestamp("fecha_like");
        LocalDateTime fechaLike = (tsSql != null) ? tsSql.toLocalDateTime() : null;

        return new LikeAlbum(idLikeAlbum, idCliente, idAlbum, fechaLike);
    }

    public Integer insertar(LikeAlbum likeAlbum) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_like_ALBUM"})) {

            ps.setInt(1, likeAlbum.getIdCliente());
            ps.setInt(2, likeAlbum.getIdAlbum());

            LocalDateTime fl = likeAlbum.getFechaLike();
            if (fl != null) {
                ps.setTimestamp(3, Timestamp.valueOf(fl));
            } else {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_like_ALBUM generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar like de album: " + e.getMessage(), e);
        }
    }

    public List<LikeAlbum> listar() {
        List<LikeAlbum> likes = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                likes.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar likes de album: " + e.getMessage(), e);
        }
        return likes;
    }

    public LikeAlbum buscarPorId(Integer idLikeAlbum) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idLikeAlbum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("LikeAlbum con id " + idLikeAlbum + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar like de album: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un like usando la clave natural (cliente + album).
     * Más útil que eliminar por id_like_ALBUM en la práctica.
     */
    public void eliminar(Integer idCliente, Integer idAlbum) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_KEY)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idAlbum);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Like de cliente " + idCliente + " al album " + idAlbum + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar like de album: " + e.getMessage(), e);
        }
    }

    public int contarLikesPorAlbum(Integer idAlbum) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_ALBUM)) {
            ps.setInt(1, idAlbum);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_likes");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al contar likes del album: " + e.getMessage(), e);
        }
    }
}