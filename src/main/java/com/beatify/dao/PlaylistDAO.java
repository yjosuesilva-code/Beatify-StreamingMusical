package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Playlist;
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

public class PlaylistDAO {

    private static final String SQL_INSERT = """
        INSERT INTO PLAYLIST
            (id_playlist, nombre, descripcion, fecha_creacion, publica, CLIENTE_id_cliente)
        VALUES (seq_playlist.NEXTVAL, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_playlist, nombre, descripcion, fecha_creacion, publica, CLIENTE_id_cliente
          FROM PLAYLIST
         ORDER BY id_playlist
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_playlist, nombre, descripcion, fecha_creacion, publica, CLIENTE_id_cliente
          FROM PLAYLIST
         WHERE id_playlist = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE PLAYLIST
           SET nombre             = ?,
               descripcion        = ?,
               fecha_creacion     = ?,
               publica            = ?,
               CLIENTE_id_cliente = ?
         WHERE id_playlist = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM PLAYLIST WHERE id_playlist = ?";

    private Playlist mapearResultSet(ResultSet rs) throws SQLException {
        Integer idPlaylist  = rs.getInt("id_playlist");
        String  nombre      = rs.getString("nombre");
        String  descripcion = rs.getString("descripcion");

        Date fechaSql = rs.getDate("fecha_creacion");
        LocalDate fechaCreacion = (fechaSql != null) ? fechaSql.toLocalDate() : null;

        String  publica   = rs.getString("publica");
        Integer idCliente = rs.getInt("CLIENTE_id_cliente");

        return new Playlist(idPlaylist, nombre, descripcion, fechaCreacion, publica, idCliente);
    }

    public Integer insertar(Playlist playlist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_playlist"})) {

            ps.setString(1, playlist.getNombre());
            ps.setString(2, playlist.getDescripcion());

            LocalDate fc = playlist.getFechaCreacion();
            if (fc != null) {
                ps.setDate(3, Date.valueOf(fc));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, playlist.getPublica());
            ps.setInt(5, playlist.getIdCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_playlist generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar playlist: " + e.getMessage(), e);
        }
    }

    public List<Playlist> listar() {
        List<Playlist> playlists = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                playlists.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar playlists: " + e.getMessage(), e);
        }
        return playlists;
    }

    public Playlist buscarPorId(Integer idPlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idPlaylist);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Playlist con id " + idPlaylist + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar playlist: " + e.getMessage(), e);
        }
    }

    public void actualizar(Playlist playlist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, playlist.getNombre());
            ps.setString(2, playlist.getDescripcion());

            LocalDate fc = playlist.getFechaCreacion();
            if (fc != null) {
                ps.setDate(3, Date.valueOf(fc));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, playlist.getPublica());
            ps.setInt(5, playlist.getIdCliente());
            ps.setInt(6, playlist.getIdPlaylist());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Playlist con id " + playlist.getIdPlaylist() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar playlist: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idPlaylist) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idPlaylist);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Playlist con id " + idPlaylist + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar playlist: " + e.getMessage(), e);
        }
    }
}