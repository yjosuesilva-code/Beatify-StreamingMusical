package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Notificacion;
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

public class NotificacionDAO {

    private static final String SQL_INSERT = """
        INSERT INTO NOTIFICACION
            (id_notificacion, titulo, mensaje, tipo, fecha_envio, leida, CLIENTE_id_cliente)
        VALUES (seq_notificacion.NEXTVAL, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_notificacion, titulo, mensaje, tipo, fecha_envio, leida, CLIENTE_id_cliente
          FROM NOTIFICACION
         ORDER BY id_notificacion
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_notificacion, titulo, mensaje, tipo, fecha_envio, leida, CLIENTE_id_cliente
          FROM NOTIFICACION
         WHERE id_notificacion = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE NOTIFICACION
           SET titulo             = ?,
               mensaje            = ?,
               tipo               = ?,
               fecha_envio        = ?,
               leida              = ?,
               CLIENTE_id_cliente = ?
         WHERE id_notificacion = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM NOTIFICACION WHERE id_notificacion = ?";

    private Notificacion mapearResultSet(ResultSet rs) throws SQLException {
        Integer idNotificacion = rs.getInt("id_notificacion");
        String  titulo         = rs.getString("titulo");
        String  mensaje        = rs.getString("mensaje");
        String  tipo           = rs.getString("tipo");

        Timestamp ts = rs.getTimestamp("fecha_envio");
        LocalDateTime fechaEnvio = (ts != null) ? ts.toLocalDateTime() : null;

        String  leida     = rs.getString("leida");
        Integer idCliente = rs.getInt("CLIENTE_id_cliente");

        return new Notificacion(idNotificacion, titulo, mensaje, tipo, fechaEnvio, leida, idCliente);
    }

    public Integer insertar(Notificacion notificacion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_notificacion"})) {

            ps.setString(1, notificacion.getTitulo());
            ps.setString(2, notificacion.getMensaje());
            ps.setString(3, notificacion.getTipo());

            LocalDateTime fe = notificacion.getFechaEnvio();
            if (fe != null) {
                ps.setTimestamp(4, Timestamp.valueOf(fe));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            ps.setString(5, notificacion.getLeida());
            ps.setInt(6, notificacion.getIdCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_notificacion generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar notificacion: " + e.getMessage(), e);
        }
    }

    public List<Notificacion> listar() {
        List<Notificacion> notificaciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                notificaciones.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar notificaciones: " + e.getMessage(), e);
        }
        return notificaciones;
    }

    public Notificacion buscarPorId(Integer idNotificacion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idNotificacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Notificacion con id " + idNotificacion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar notificacion: " + e.getMessage(), e);
        }
    }

    public void actualizar(Notificacion notificacion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, notificacion.getTitulo());
            ps.setString(2, notificacion.getMensaje());
            ps.setString(3, notificacion.getTipo());

            LocalDateTime fe = notificacion.getFechaEnvio();
            if (fe != null) {
                ps.setTimestamp(4, Timestamp.valueOf(fe));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            ps.setString(5, notificacion.getLeida());
            ps.setInt(6, notificacion.getIdCliente());
            ps.setInt(7, notificacion.getIdNotificacion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Notificacion con id " + notificacion.getIdNotificacion() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar notificacion: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idNotificacion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idNotificacion);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Notificacion con id " + idNotificacion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar notificacion: " + e.getMessage(), e);
        }
    }
}