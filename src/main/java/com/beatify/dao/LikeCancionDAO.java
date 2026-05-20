package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.LikeCancion;
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

public class LikeCancionDAO {

    private static final String SQL_INSERT = """
        INSERT INTO LIKE_CANCION
            (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like)
        VALUES (seq_like_cancion.NEXTVAL, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like
          FROM LIKE_CANCION
         ORDER BY id_like_cancion
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like
          FROM LIKE_CANCION
         WHERE id_like_cancion = ?
        """;

    private static final String SQL_DELETE_BY_KEY = """
        DELETE FROM LIKE_CANCION
         WHERE CLIENTE_id_cliente = ? AND CANCION_id_cancion = ?
        """;

    private static final String SQL_COUNT_BY_CANCION = """
        SELECT COUNT(*) AS total_likes
          FROM LIKE_CANCION
         WHERE CANCION_id_cancion = ?
        """;

    private LikeCancion mapearResultSet(ResultSet rs) throws SQLException {
        Integer idLikeCancion = rs.getInt("id_like_cancion");
        Integer idCliente     = rs.getInt("CLIENTE_id_cliente");
        Integer idCancion     = rs.getInt("CANCION_id_cancion");

        Timestamp tsSql = rs.getTimestamp("fecha_like");
        LocalDateTime fechaLike = (tsSql != null) ? tsSql.toLocalDateTime() : null;

        return new LikeCancion(idLikeCancion, idCliente, idCancion, fechaLike);
    }

    public Integer insertar(LikeCancion likeCancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_like_cancion"})) {

            ps.setInt(1, likeCancion.getIdCliente());
            ps.setInt(2, likeCancion.getIdCancion());

            LocalDateTime fl = likeCancion.getFechaLike();
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
                throw new ConexionException("No se pudo recuperar el id_like_cancion generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar like de cancion: " + e.getMessage(), e);
        }
    }

    public List<LikeCancion> listar() {
        List<LikeCancion> likes = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                likes.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar likes de cancion: " + e.getMessage(), e);
        }
        return likes;
    }

    public LikeCancion buscarPorId(Integer idLikeCancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idLikeCancion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("LikeCancion con id " + idLikeCancion + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar like de cancion: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idCliente, Integer idCancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_BY_KEY)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idCancion);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Like de cliente " + idCliente + " a cancion " + idCancion + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar like de cancion: " + e.getMessage(), e);
        }
    }

    public int contarLikesPorCancion(Integer idCancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_CANCION)) {
            ps.setInt(1, idCancion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_likes");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al contar likes de la cancion: " + e.getMessage(), e);
        }
    }
}