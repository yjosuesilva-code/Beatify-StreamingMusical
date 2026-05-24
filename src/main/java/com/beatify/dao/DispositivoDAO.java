package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Dispositivo;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DispositivoDAO {

    private static final String SQL_INSERT = """
        INSERT INTO DISPOSITIVO
            (id_dispositivo, nombre_dispositivo, tipo_dispositivo, sistema_operativo, 
            fecha_ultimo_acceso, CLIENTE_id_cliente)
        VALUES (seq_dispositivo.NEXTVAL, ?, ?, ?, SYSDATE, ?)
    """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_dispositivo, nombre_dispositivo, tipo_dispositivo, sistema_operativo, 
               fecha_ultimo_acceso, CLIENTE_id_cliente
          FROM DISPOSITIVO
         ORDER BY id_dispositivo
    """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_dispositivo, nombre_dispositivo, tipo_dispositivo, sistema_operativo, 
               fecha_ultimo_acceso, CLIENTE_id_cliente
          FROM DISPOSITIVO
         WHERE id_dispositivo = ?
    """;

    private static final String SQL_UPDATE = """
        UPDATE DISPOSITIVO
           SET nombre_dispositivo = ?,
               tipo_dispositivo   = ?,
               sistema_operativo  = ?,
               fecha_ultimo_acceso = SYSDATE,
               CLIENTE_id_cliente        = ?
         WHERE id_dispositivo = ?
    """;

    private static final String SQL_DELETE = "DELETE FROM DISPOSITIVO WHERE id_dispositivo = ?";

    private Dispositivo mapearResultSet(ResultSet rs) throws SQLException {
        Integer idDispositivo = rs.getInt("id_dispositivo");
        String nombreDispositivo = rs.getString("nombre_dispositivo");
        String tipoDispositivo = rs.getString("tipo_dispositivo");
        String sistemaOperativo = rs.getString("sistema_operativo");
        // fecha_ultimo_acceso es nullable en el schema; protegemos contra NPE
        java.sql.Timestamp ts = rs.getTimestamp("fecha_ultimo_acceso");
        LocalDateTime fechaUltimoAcceso = (ts != null) ? ts.toLocalDateTime() : null;
        Integer idCliente = rs.getInt("CLIENTE_id_cliente");
        return new Dispositivo(idDispositivo, nombreDispositivo, tipoDispositivo, sistemaOperativo, fechaUltimoAcceso, idCliente);
    }

    public Integer insertar(Dispositivo dispositivo){
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT, new String[]{"id_dispositivo"})) {

            ps.setString(1, dispositivo.getNombreDispositivo());
            ps.setString(2, dispositivo.getTipoDispositivo());
            ps.setString(3, dispositivo.getSistemaOperativo());
            ps.setInt(4, dispositivo.getIdCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo obtener el ID generado.");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar el dispositivo: " + e.getMessage(), e);
        }
    }

    public List<Dispositivo> listar(){
        List<Dispositivo> dispositivos = new ArrayList<>();

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                dispositivos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar los dispositivos: " + e.getMessage(), e);
        }
        return dispositivos;
    }

    public Dispositivo buscarPorId(Integer idDispositivo){
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idDispositivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Dispositivo con id " + idDispositivo + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar el dispositivo por ID: " + e.getMessage(), e);
        }
    }

    public void actualizar(Dispositivo dispositivo){
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, dispositivo.getNombreDispositivo());
            ps.setString(2, dispositivo.getTipoDispositivo());
            ps.setString(3, dispositivo.getSistemaOperativo());
            ps.setInt(4, dispositivo.getIdCliente());
            ps.setInt(5, dispositivo.getIdDispositivo());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("No se encontró el dispositivo con id " + dispositivo.getIdDispositivo() + " para actualizar");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar el dispositivo: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idDispositivo){
        try (Connection connection = Conexion.getInstancia().obtenerConexion();
                PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {

                ps.setInt(1, idDispositivo);
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas == 0) {
                    throw new NotFoundException("No se encontró el dispositivo con id " + idDispositivo + " para eliminar");
                }
            } catch (SQLException e) {
                throw new ConexionException("Error al eliminar el dispositivo: " + e.getMessage(), e);
            }
    }
}
