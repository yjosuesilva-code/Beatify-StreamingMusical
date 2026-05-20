package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Suscripcion;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuscripcionDAO {

    private static final String SQL_INSERT = """
        INSERT INTO SUSCRIPCION
            (id_suscripcion, tipo_plan, precio, fecha_inicio, fecha_fin,
             estado, CLIENTE_id_cliente)
        VALUES (seq_suscripcion.NEXTVAL, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_suscripcion, tipo_plan, precio, fecha_inicio, fecha_fin,
               estado, CLIENTE_id_cliente
          FROM SUSCRIPCION
         ORDER BY id_suscripcion
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_suscripcion, tipo_plan, precio, fecha_inicio, fecha_fin,
               estado, CLIENTE_id_cliente
          FROM SUSCRIPCION
         WHERE id_suscripcion = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE SUSCRIPCION
           SET tipo_plan          = ?,
               precio             = ?,
               fecha_inicio       = ?,
               fecha_fin          = ?,
               estado             = ?,
               CLIENTE_id_cliente = ?
         WHERE id_suscripcion = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SUSCRIPCION WHERE id_suscripcion = ?";

    private Suscripcion mapearResultSet(ResultSet rs) throws SQLException {
        Integer idSuscripcion = rs.getInt("id_suscripcion");
        String  tipoPlan      = rs.getString("tipo_plan");
        Double  precio        = rs.getDouble("precio");

        Date fechaInicioSql = rs.getDate("fecha_inicio");
        LocalDate fechaInicio = (fechaInicioSql != null) ? fechaInicioSql.toLocalDate() : null;

        Date fechaFinSql = rs.getDate("fecha_fin");
        LocalDate fechaFin = (fechaFinSql != null) ? fechaFinSql.toLocalDate() : null;

        String  estado    = rs.getString("estado");
        Integer idCliente = rs.getInt("CLIENTE_id_cliente");

        return new Suscripcion(idSuscripcion, tipoPlan, precio, fechaInicio, fechaFin, estado, idCliente);
    }

    public Integer insertar(Suscripcion suscripcion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_suscripcion"})) {

            ps.setString(1, suscripcion.getTipoPlan());
            ps.setDouble(2, suscripcion.getPrecio());
            ps.setDate(3, Date.valueOf(suscripcion.getFechaInicio()));

            if (suscripcion.getFechaFin() != null) {
                ps.setDate(4, Date.valueOf(suscripcion.getFechaFin()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, suscripcion.getEstado());
            ps.setInt(6, suscripcion.getIdCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_suscripcion generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar suscripcion: " + e.getMessage(), e);
        }
    }

    public List<Suscripcion> listar() {
        List<Suscripcion> suscripciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                suscripciones.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar suscripciones: " + e.getMessage(), e);
        }
        return suscripciones;
    }

    public Suscripcion buscarPorId(Integer idSuscripcion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idSuscripcion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Suscripcion con id " + idSuscripcion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar suscripcion: " + e.getMessage(), e);
        }
    }

    public void actualizar(Suscripcion suscripcion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, suscripcion.getTipoPlan());
            ps.setDouble(2, suscripcion.getPrecio());
            ps.setDate(3, Date.valueOf(suscripcion.getFechaInicio()));

            if (suscripcion.getFechaFin() != null) {
                ps.setDate(4, Date.valueOf(suscripcion.getFechaFin()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, suscripcion.getEstado());
            ps.setInt(6, suscripcion.getIdCliente());
            ps.setInt(7, suscripcion.getIdSuscripcion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Suscripcion con id " + suscripcion.getIdSuscripcion() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar suscripcion: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idSuscripcion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idSuscripcion);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Suscripcion con id " + idSuscripcion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar suscripcion: " + e.getMessage(), e);
        }
    }
}