package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.CancionPlaylist;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CancionPlaylistDAO {

    private static final String SQL_INSERT = """
        INSERT INTO CANCION_PLAYLIST
            (id_cancion_playlist, orden, fecha_agregada,
             PLAYLIST_id_playlist, CANCION_id_cancion)
        VALUES (seq_cancion_playlist.NEXTVAL, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_cancion_playlist, orden, fecha_agregada,
               PLAYLIST_id_playlist, CANCION_id_cancion
          FROM CANCION_PLAYLIST
         ORDER BY id_cancion_playlist
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_cancion_playlist, orden, fecha_agregada,
               PLAYLIST_id_playlist, CANCION_id_cancion
          FROM CANCION_PLAYLIST
         WHERE id_cancion_playlist = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE CANCION_PLAYLIST
           SET orden                = ?,
               fecha_agregada       = ?,
               PLAYLIST_id_playlist = ?,
               CANCION_id_cancion   = ?
         WHERE id_cancion_playlist = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM CANCION_PLAYLIST WHERE id_cancion_playlist = ?";

    private CancionPlaylist mapearResultSet(ResultSet rs) throws SQLException {
        Integer idCancionPlaylist = rs.getInt("id_cancion_playlist");
        // orden es nullable en el schema
        Integer orden             = rs.getObject("orden", Integer.class);

        Date fechaSql = rs.getDate("fecha_agregada");
        LocalDate fechaAgregada = (fechaSql != null) ? fechaSql.toLocalDate() : null;

        Integer idPlaylist = rs.getInt("PLAYLIST_id_playlist");
        Integer idCancion  = rs.getInt("CANCION_id_cancion");

        return new CancionPlaylist(idCancionPlaylist, orden, fechaAgregada, idPlaylist, idCancion);
    }

    public Integer insertar(CancionPlaylist cp) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_cancion_playlist"})) {

            if (cp.getOrden() != null) {
                ps.setInt(1, cp.getOrden());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            LocalDate fa = cp.getFechaAgregada();
            if (fa != null) {
                ps.setDate(2, Date.valueOf(fa));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.setInt(3, cp.getIdPlaylist());
            ps.setInt(4, cp.getIdCancion());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_cancion_playlist generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar cancion_playlist: " + e.getMessage(), e);
        }
    }

    public List<CancionPlaylist> listar() {
        List<CancionPlaylist> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar cancion_playlist: " + e.getMessage(), e);
        }
        return resultado;
    }

    public CancionPlaylist buscarPorId(Integer idCancionPlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idCancionPlaylist);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("CancionPlaylist con id " + idCancionPlaylist + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar cancion_playlist: " + e.getMessage(), e);
        }
    }

    public void actualizar(CancionPlaylist cp) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            if (cp.getOrden() != null) {
                ps.setInt(1, cp.getOrden());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }

            LocalDate fa = cp.getFechaAgregada();
            if (fa != null) {
                ps.setDate(2, Date.valueOf(fa));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.setInt(3, cp.getIdPlaylist());
            ps.setInt(4, cp.getIdCancion());
            ps.setInt(5, cp.getIdCancionPlaylist());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("CancionPlaylist con id " + cp.getIdCancionPlaylist() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar cancion_playlist: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idCancionPlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idCancionPlaylist);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("CancionPlaylist con id " + idCancionPlaylist + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar cancion_playlist: " + e.getMessage(), e);
        }
    }
}