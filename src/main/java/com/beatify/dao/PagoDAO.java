package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Pago;
import com.beatify.util.Conexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {
    private static final String SQL_INSERT = """
        INSERT INTO PAGO 
            (id_pago, monto, fecha_pago, metodo_pago, estado_pago, 
            referencia_externa, SUSCRIPCION_id_suscripcion)
        VALUES (seq_pago.NEXTVAL, ?, ?, ?, ?, ?, ?)
    """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_pago, monto, fecha_pago, metodo_pago, estado_pago, 
                referencia_externa, SUSCRIPCION_id_suscripcion
          FROM PAGO
         ORDER BY id_pago
    """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_pago, monto, fecha_pago, metodo_pago, estado_pago, 
                referencia_externa, SUSCRIPCION_id_suscripcion
          FROM PAGO
         WHERE id_pago = ?
    """;

    private static final String SQL_UPDATE = """
        UPDATE PAGO
           SET monto                 = ?,
               fecha_pago            = ?,
               metodo_pago           = ?,
               estado_pago           = ?,
               referencia_externa    = ?,
               SUSCRIPCION_id_suscripcion = ?
         WHERE id_pago = ?
    """;

    private static final String SQL_DELETE = "DELETE FROM PAGO WHERE id_pago = ?";

    private Pago mapearResultSet(ResultSet rs) throws SQLException {
        Integer idPago = rs.getInt("id_pago");
        Double monto = rs.getDouble("monto");
        Timestamp ts = rs.getTimestamp("fecha_pago");
        LocalDateTime fechaPago =
                (ts != null) ? ts.toLocalDateTime() : null;
        String metodoPago = rs.getString("metodo_pago");
        String estadoPago = rs.getString("estado_pago");
        String referenciaExterna = rs.getString("referencia_externa");
        Integer idSuscripcion = rs.getInt("SUSCRIPCION_id_suscripcion");
        return new Pago(idPago, monto, fechaPago, metodoPago, estadoPago, referenciaExterna, idSuscripcion);
    }

    public Integer  insertar(Pago pago) {
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT, new String[] {"id_pago"})) {
            ps.setDouble(1, pago.getMonto());
            ps.setTimestamp(2, Timestamp.valueOf(pago.getFechaPago()));
            ps.setString(3, pago.getMetodoPago());
            ps.setString(4, pago.getEstadoPago());
            ps.setString(5, pago.getReferenciaExterna());
            ps.setInt(6, pago.getIdSuscripcion());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo insertar el pago");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new ConexionException("No se pudo obtener el ID generado para el pago.");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar el pago: " + e.getMessage(), e);
        }
    }

    public List<Pago> listar(){
        List<Pago> pagos = new ArrayList<>();
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                pagos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar los pagos: " + e.getMessage(), e);
        }
        return pagos;
    }

    public Pago buscarPorId(Integer idPago){
        try(Connection conn =  Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idPago);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Pago con id " + idPago + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar el pago por ID: " + e.getMessage(), e);
        }
    }

    public void actualizar(Pago pago){
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setDouble(1, pago.getMonto());
            ps.setTimestamp(2, Timestamp.valueOf(pago.getFechaPago()));
            ps.setString(3, pago.getMetodoPago());
            ps.setString(4, pago.getEstadoPago());
            ps.setString(5, pago.getReferenciaExterna());
            ps.setInt(6, pago.getIdSuscripcion());
            ps.setInt(7, pago.getIdPago());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("No se encontró el pago con id " + pago.getIdPago() + " para actualizar");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar el pago: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idPago){
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idPago);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("No se encontró el pago con id " + idPago + " para eliminar");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar el pago: " + e.getMessage(), e);
        }
    }
}
