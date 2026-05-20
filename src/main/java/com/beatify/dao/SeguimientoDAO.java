package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Seguimiento;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SeguimientoDAO {

    private static final String SQL_INSERT = """
        INSERT INTO SEGUIMIENTO
            (id_seguimiento, fecha_seguimiento, CLIENTE_id_cliente, ARTISTA_id_artista)
        VALUES (seq_seguimiento.NEXTVAL, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_seguimiento, fecha_seguimiento, CLIENTE_id_cliente, ARTISTA_id_artista
          FROM SEGUIMIENTO
         ORDER BY id_seguimiento
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_seguimiento, fecha_seguimiento, CLIENTE_id_cliente, ARTISTA_id_artista
          FROM SEGUIMIENTO
         WHERE id_seguimiento = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE SEGUIMIENTO
           SET fecha_seguimiento  = ?,
               CLIENTE_id_cliente = ?,
               ARTISTA_id_artista = ?
         WHERE id_seguimiento = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM SEGUIMIENTO WHERE id_seguimiento = ?";

    private Seguimiento mapearResultSet(ResultSet rs) throws SQLException {
        Integer idSeguimiento = rs.getInt("id_seguimiento");

        Date fechaSql = rs.getDate("fecha_seguimiento");
        LocalDate fechaSeguimiento = (fechaSql != null) ? fechaSql.toLocalDate() : null;

        Integer idCliente = rs.getInt("CLIENTE_id_cliente");
        Integer idArtista = rs.getInt("ARTISTA_id_artista");

        return new Seguimiento(idSeguimiento, fechaSeguimiento, idCliente, idArtista);
    }

    public Integer insertar(Seguimiento seguimiento) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_seguimiento"})) {

            ps.setDate(1, Date.valueOf(seguimiento.getFechaSeguimiento()));
            ps.setInt(2, seguimiento.getIdCliente());
            ps.setInt(3, seguimiento.getIdArtista());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_seguimiento generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar seguimiento: " + e.getMessage(), e);
        }
    }

    public List<Seguimiento> listar() {
        List<Seguimiento> seguimientos = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                seguimientos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar seguimientos: " + e.getMessage(), e);
        }
        return seguimientos;
    }

    public Seguimiento buscarPorId(Integer idSeguimiento) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idSeguimiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Seguimiento con id " + idSeguimiento + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar seguimiento: " + e.getMessage(), e);
        }
    }

    public void actualizar(Seguimiento seguimiento) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setDate(1, Date.valueOf(seguimiento.getFechaSeguimiento()));
            ps.setInt(2, seguimiento.getIdCliente());
            ps.setInt(3, seguimiento.getIdArtista());
            ps.setInt(4, seguimiento.getIdSeguimiento());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Seguimiento con id " + seguimiento.getIdSeguimiento() + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar seguimiento: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idSeguimiento) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idSeguimiento);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Seguimiento con id " + idSeguimiento + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar seguimiento: " + e.getMessage(), e);
        }
    }
}