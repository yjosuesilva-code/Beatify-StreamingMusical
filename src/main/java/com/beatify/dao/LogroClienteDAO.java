package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.LogroCliente;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogroClienteDAO {

    private static final String SQL_INSERT = """
        INSERT INTO LOGRO_CLIENTE
            (id_logro_cliente, fecha_obtencion, CLIENTE_id_cliente, LOGRO_id_logro)
        VALUES (seq_logro_cliente.NEXTVAL, SYSDATE, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_logro_cliente, fecha_obtencion, CLIENTE_id_cliente, LOGRO_id_logro
          FROM LOGRO_CLIENTE
         ORDER BY id_logro_cliente
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_logro_cliente, fecha_obtencion, CLIENTE_id_cliente, LOGRO_id_logro
          FROM LOGRO_CLIENTE
         WHERE id_logro_cliente = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE LOGRO_CLIENTE
           SET CLIENTE_id_cliente = ?,
               LOGRO_id_logro     = ?
         WHERE id_logro_cliente = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM LOGRO_CLIENTE WHERE id_logro_cliente = ?";

    private LogroCliente mapearResultSet(ResultSet rs) throws SQLException {
        Integer idLogroCliente = rs.getInt("id_logro_cliente");

        Timestamp ts = rs.getTimestamp("fecha_obtencion");
        LocalDateTime fechaObtencion = (ts != null) ? ts.toLocalDateTime() : null;

        Integer idCliente = rs.getInt("CLIENTE_id_cliente");
        Integer idLogro   = rs.getInt("LOGRO_id_logro");

        return new LogroCliente(idLogroCliente, fechaObtencion, idCliente, idLogro);
    }

    public Integer insertar(LogroCliente logroCliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_logro_cliente"})) {

            ps.setInt(1, logroCliente.getIdCliente());
            ps.setInt(2, logroCliente.getIdLogro());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_logro_cliente generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar logro_cliente: " + e.getMessage(), e);
        }
    }

    public List<LogroCliente> listar() {
        List<LogroCliente> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar logro_cliente: " + e.getMessage(), e);
        }
        return resultado;
    }

    public LogroCliente buscarPorId(Integer idLogroCliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idLogroCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("LogroCliente con id " + idLogroCliente + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar logro_cliente: " + e.getMessage(), e);
        }
    }

    public void actualizar(LogroCliente logroCliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setInt(1, logroCliente.getIdCliente());
            ps.setInt(2, logroCliente.getIdLogro());
            ps.setInt(3, logroCliente.getIdLogroCliente());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "LogroCliente con id " + logroCliente.getIdLogroCliente() + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar logro_cliente: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idLogroCliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idLogroCliente);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "LogroCliente con id " + idLogroCliente + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar logro_cliente: " + e.getMessage(), e);
        }
    }
}