package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Cancion;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CancionDAO {

    private static final String SQL_INSERT = """
        INSERT INTO CANCION
            (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor,
             fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero)
        VALUES (seq_cancion.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor,
               fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero
          FROM CANCION
         ORDER BY id_cancion
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor,
               fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero
          FROM CANCION
         WHERE id_cancion = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE CANCION
           SET titulo             = ?,
               duracion_seg       = ?,
               ruta_archivo       = ?,
               letra              = ?,
               compositor         = ?,
               fecha_lanzamiento  = ?,
               ALBUM_id_album     = ?,
               GENERO_id_genero   = ?
         WHERE id_cancion = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM CANCION WHERE id_cancion = ?";

    private Cancion mapearResultSet(ResultSet rs) throws SQLException {
        Integer idCancion        = rs.getInt("id_cancion");
        String  titulo           = rs.getString("titulo");
        Integer duracionSegundos = rs.getInt("duracion_seg");
        String  rutaArchivo      = rs.getString("ruta_archivo");
        String  letra            = rs.getString("letra");
        String  compositor       = rs.getString("compositor");

        Date fechaSql = rs.getDate("fecha_lanzamiento");
        LocalDate fechaLanzamiento = (fechaSql != null) ? fechaSql.toLocalDate() : null;

        Integer idAlbum  = rs.getInt("ALBUM_id_album");
        Integer idGenero = rs.getInt("GENERO_id_genero");

        return new Cancion(idCancion, titulo, duracionSegundos, rutaArchivo, letra,
                compositor, fechaLanzamiento, idAlbum, idGenero);
    }

    public Integer insertar(Cancion cancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_cancion"})) {

            ps.setString(1, cancion.getTitulo());
            ps.setInt(2, cancion.getDuracionSegundos());
            ps.setString(3, cancion.getRutaArchivo());
            ps.setString(4, cancion.getLetra());
            ps.setString(5, cancion.getCompositor());

            LocalDate fl = cancion.getFechaLanzamiento();
            if (fl != null) {
                ps.setDate(6, Date.valueOf(fl));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            ps.setInt(7, cancion.getIdAlbum());
            ps.setInt(8, cancion.getIdGenero());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_cancion generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar cancion: " + e.getMessage(), e);
        }
    }

    public List<Cancion> listar() {
        List<Cancion> canciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                canciones.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar canciones: " + e.getMessage(), e);
        }
        return canciones;
    }

    public Cancion buscarPorId(Integer idCancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idCancion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Cancion con id " + idCancion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar cancion: " + e.getMessage(), e);
        }
    }

    public void actualizar(Cancion cancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, cancion.getTitulo());
            ps.setInt(2, cancion.getDuracionSegundos());
            ps.setString(3, cancion.getRutaArchivo());
            ps.setString(4, cancion.getLetra());
            ps.setString(5, cancion.getCompositor());

            LocalDate fl = cancion.getFechaLanzamiento();
            if (fl != null) {
                ps.setDate(6, Date.valueOf(fl));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            ps.setInt(7, cancion.getIdAlbum());
            ps.setInt(8, cancion.getIdGenero());
            ps.setInt(9, cancion.getIdCancion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Cancion con id " + cancion.getIdCancion() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar cancion: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idCancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idCancion);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Cancion con id " + idCancion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar cancion: " + e.getMessage(), e);
        }
    }
}