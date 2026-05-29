package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Resena;
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

public class ResenaDAO {

    private static final String SQL_INSERT = """
        INSERT INTO RESENA
            (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo,
             calificacion, comentario, fecha_resena)
        VALUES (seq_resena.NEXTVAL, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo,
               calificacion, comentario, fecha_resena
          FROM RESENA
         ORDER BY id_resena
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo,
               calificacion, comentario, fecha_resena
          FROM RESENA
         WHERE id_resena = ?
        """;
    private static final String SQL_SELECT_BY_CLIENTE = """
            SELECT id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo,
                   calificacion, comentario, fecha_resena
              FROM RESENA
             WHERE CLIENTE_id_cliente = ?
             ORDER BY fecha_resena DESC
            """;
    private static final String SQL_UPDATE = """
        UPDATE RESENA
           SET CLIENTE_id_cliente = ?,
               tipo_objetivo      = ?,
               id_objetivo        = ?,
               calificacion       = ?,
               comentario         = ?,
               fecha_resena       = ?
         WHERE id_resena = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM RESENA WHERE id_resena = ?";

    private Resena mapearResultSet(ResultSet rs) throws SQLException {
        Integer idResena     = rs.getInt("id_resena");
        Integer idCliente    = rs.getInt("CLIENTE_id_cliente");
        String  tipoObjetivo = rs.getString("tipo_objetivo");
        Integer idObjetivo   = rs.getInt("id_objetivo");
        Integer calificacion = rs.getInt("calificacion");
        String  comentario   = rs.getString("comentario");

        Timestamp ts = rs.getTimestamp("fecha_resena");
        LocalDateTime fechaResena = (ts != null) ? ts.toLocalDateTime() : null;

        return new Resena(idResena, idCliente, tipoObjetivo, idObjetivo, comentario, calificacion, fechaResena);
    }

    public Integer insertar(Resena resena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_resena"})) {

            ps.setInt(1, resena.getIdCliente());
            ps.setString(2, resena.getTipoObjetivo());
            ps.setInt(3, resena.getIdObjetivo());
            ps.setInt(4, resena.getCalificacion());
            ps.setString(5, resena.getComentario());

            LocalDateTime fr = resena.getFechaResena();
            if (fr != null) {
                ps.setTimestamp(6, Timestamp.valueOf(fr));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_resena generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar resena: " + e.getMessage(), e);
        }
    }

    public List<Resena> listar() {
        List<Resena> resenas = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resenas.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar resenas: " + e.getMessage(), e);
        }
        return resenas;
    }
    /**
     * Lista las reseñas de un cliente especifico, ordenadas por fecha descendente
     * (mas recientes primero). Devuelve lista vacia si no tiene reseñas.
     */
    public List<Resena> listarPorCliente(Integer idCliente) {
        List<Resena> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_CLIENTE)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapearResultSet(rs));
                }
            }
            return resultado;
        } catch (SQLException e) {
            throw new ConexionException(
                    "Error listando reseñas del cliente " + idCliente, e);
        }
    }
    public Resena buscarPorId(Integer idResena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idResena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Resena con id " + idResena + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar resena: " + e.getMessage(), e);
        }
    }

    public void actualizar(Resena resena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setInt(1, resena.getIdCliente());
            ps.setString(2, resena.getTipoObjetivo());
            ps.setInt(3, resena.getIdObjetivo());
            ps.setInt(4, resena.getCalificacion());
            ps.setString(5, resena.getComentario());

            LocalDateTime fr = resena.getFechaResena();
            if (fr != null) {
                ps.setTimestamp(6, Timestamp.valueOf(fr));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            ps.setInt(7, resena.getIdResena());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Resena con id " + resena.getIdResena() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar resena: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idResena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idResena);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Resena con id " + idResena + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar resena: " + e.getMessage(), e);
        }
    }
}