package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Reproduccion;
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

public class ReproduccionDAO {

    private static final String SQL_INSERT = """
        INSERT INTO REPRODUCCION
            (id_reproduccion, fecha_hora, duracion_escuchada,
             CLIENTE_id_cliente, CANCION_id_cancion)
        VALUES (seq_reproduccion.NEXTVAL, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_reproduccion, fecha_hora, duracion_escuchada,
               CLIENTE_id_cliente, CANCION_id_cancion
          FROM REPRODUCCION
         ORDER BY id_reproduccion
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_reproduccion, fecha_hora, duracion_escuchada,
               CLIENTE_id_cliente, CANCION_id_cancion
          FROM REPRODUCCION
         WHERE id_reproduccion = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE REPRODUCCION
           SET fecha_hora          = ?,
               duracion_escuchada  = ?,
               CLIENTE_id_cliente  = ?,
               CANCION_id_cancion  = ?
         WHERE id_reproduccion = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM REPRODUCCION WHERE id_reproduccion = ?";

    private Reproduccion mapearResultSet(ResultSet rs) throws SQLException {
        Integer idReproduccion = rs.getInt("id_reproduccion");

        Timestamp ts = rs.getTimestamp("fecha_hora");
        LocalDateTime fechaHora = (ts != null) ? ts.toLocalDateTime() : null;

        Integer duracionEscuchada = rs.getInt("duracion_escuchada");
        if (rs.wasNull()) {
            duracionEscuchada = null;
        }

        Integer idCliente = rs.getInt("CLIENTE_id_cliente");
        Integer idCancion = rs.getInt("CANCION_id_cancion");

        return new Reproduccion(idReproduccion, fechaHora, duracionEscuchada, idCliente, idCancion);
    }

    public Integer insertar(Reproduccion reproduccion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_reproduccion"})) {

            ps.setTimestamp(1, Timestamp.valueOf(reproduccion.getFechaHora()));

            if (reproduccion.getDuracionReproduccion() != null) {
                ps.setInt(2, reproduccion.getDuracionReproduccion());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setInt(3, reproduccion.getIdCliente());
            ps.setInt(4, reproduccion.getIdCancion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_reproduccion generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar reproduccion: " + e.getMessage(), e);
        }
    }

    public List<Reproduccion> listar() {
        List<Reproduccion> reproducciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reproducciones.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar reproducciones: " + e.getMessage(), e);
        }
        return reproducciones;
    }

    public Reproduccion buscarPorId(Integer idReproduccion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idReproduccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Reproduccion con id " + idReproduccion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar reproduccion: " + e.getMessage(), e);
        }
    }

    public void actualizar(Reproduccion reproduccion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setTimestamp(1, Timestamp.valueOf(reproduccion.getFechaHora()));

            if (reproduccion.getDuracionReproduccion() != null) {
                ps.setInt(2, reproduccion.getDuracionReproduccion());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setInt(3, reproduccion.getIdCliente());
            ps.setInt(4, reproduccion.getIdCancion());
            ps.setInt(5, reproduccion.getIdReproduccion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Reproduccion con id " + reproduccion.getIdReproduccion() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar reproduccion: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idReproduccion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idReproduccion);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Reproduccion con id " + idReproduccion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar reproduccion: " + e.getMessage(), e);
        }
    }
}