package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Colaboracion;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ColaboracionDAO {

    private static final String SQL_INSERT = """
        INSERT INTO COLABORACION
            (id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista,
             rol, fecha_colaboracion)
        VALUES (seq_colaboracion.NEXTVAL, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista,
               rol, fecha_colaboracion
          FROM COLABORACION
         ORDER BY id_colaboracion
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista,
               rol, fecha_colaboracion
          FROM COLABORACION
         WHERE id_colaboracion = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE COLABORACION
           SET CANCION_id_cancion  = ?,
               ARTISTA_id_artista  = ?,
               rol                 = ?,
               fecha_colaboracion  = ?
         WHERE id_colaboracion = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM COLABORACION WHERE id_colaboracion = ?";

    private Colaboracion mapearResultSet(ResultSet rs) throws SQLException {
        Integer idColaboracion = rs.getInt("id_colaboracion");
        Integer idCancion      = rs.getInt("CANCION_id_cancion");
        Integer idArtista      = rs.getInt("ARTISTA_id_artista");
        String  rol            = rs.getString("rol");

        Date fechaSql = rs.getDate("fecha_colaboracion");
        LocalDate fechaColaboracion = (fechaSql != null) ? fechaSql.toLocalDate() : null;

        return new Colaboracion(idColaboracion, idCancion, idArtista, rol, fechaColaboracion);
    }

    public Integer insertar(Colaboracion colaboracion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_colaboracion"})) {

            ps.setInt(1, colaboracion.getIdCancion());
            ps.setInt(2, colaboracion.getIdArtista());
            ps.setString(3, colaboracion.getRol());

            LocalDate fc = colaboracion.getFechaColaboracion();
            if (fc != null) {
                ps.setDate(4, Date.valueOf(fc));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_colaboracion generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar colaboracion: " + e.getMessage(), e);
        }
    }

    public List<Colaboracion> listar() {
        List<Colaboracion> colaboraciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                colaboraciones.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar colaboraciones: " + e.getMessage(), e);
        }
        return colaboraciones;
    }

    public Colaboracion buscarPorId(Integer idColaboracion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idColaboracion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Colaboracion con id " + idColaboracion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar colaboracion: " + e.getMessage(), e);
        }
    }

    public void actualizar(Colaboracion colaboracion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setInt(1, colaboracion.getIdCancion());
            ps.setInt(2, colaboracion.getIdArtista());
            ps.setString(3, colaboracion.getRol());

            LocalDate fc = colaboracion.getFechaColaboracion();
            if (fc != null) {
                ps.setDate(4, Date.valueOf(fc));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setInt(5, colaboracion.getIdColaboracion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Colaboracion con id " + colaboracion.getIdColaboracion() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar colaboracion: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idColaboracion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idColaboracion);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Colaboracion con id " + idColaboracion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar colaboracion: " + e.getMessage(), e);
        }
    }
}