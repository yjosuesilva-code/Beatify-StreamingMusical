package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Album;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AlbumDAO {


    private static final String SQL_INSERT = """
        INSERT INTO ALBUM
            (id_album, titulo, anio_lanzamiento, sello_discografico,
             tipo, portada_url, descripcion, ARTISTA_id_artista)
        VALUES (seq_album.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_album, titulo, anio_lanzamiento, sello_discografico,
               tipo, portada_url, descripcion, ARTISTA_id_artista
          FROM ALBUM
         ORDER BY id_album
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_album, titulo, anio_lanzamiento, sello_discografico,
               tipo, portada_url, descripcion, ARTISTA_id_artista
          FROM ALBUM
         WHERE id_album = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE ALBUM
           SET titulo             = ?,
               anio_lanzamiento    = ?,
               sello_discografico = ?,
               tipo               = ?,
               portada_url        = ?,
               descripcion        = ?,
               ARTISTA_id_artista = ?
         WHERE id_album = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM ALBUM WHERE id_album = ?";

    private Album mapearResultSet(ResultSet rs) throws SQLException {
        Integer idAlbum            = rs.getInt("id_album");
        String  titulo             = rs.getString("titulo");
        Integer anioLanzamiento    = rs.getObject("año_lanzamiento",Integer.class);
        String  selloDiscografico  = rs.getString("sello_discografico");
        String  tipo               = rs.getString("tipo");
        String  portadaUrl         = rs.getString("portada_url");
        String  descripcion        = rs.getString("descripcion");
        Integer idArtista          = rs.getObject("ARTISTA_id_artista", Integer.class);

        return new Album(
                idAlbum, titulo, anioLanzamiento, selloDiscografico,
                tipo, portadaUrl, descripcion, idArtista
        );
    }

    public Integer insertar(Album album) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_album"})) {

            ps.setString(1, album.getTitulo());
            if (album.getAnioLanzamiento() != null) {
                ps.setInt(2, album.getAnioLanzamiento());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, album.getSelloDiscografico());
            ps.setString(4, album.getTipo());
            ps.setString(5, album.getPortadaUrl());
            ps.setString(6, album.getDescripcion());
            ps.setInt(7, album.getIdArtista());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException(
                        "No se pudo recuperar el id_album generado por la secuencia");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al insertar album: " + e.getMessage(), e);
        }
    }

    public List<Album> listar() {
        List<Album> albumes = new ArrayList<>();

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                albumes.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al listar albumes: " + e.getMessage(), e);
        }

        return albumes;
    }

    public Album buscarPorId(Integer idAlbum) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idAlbum);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException(
                        "Album con id " + idAlbum + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al buscar album por id: " + e.getMessage(), e);
        }
    }

    public void actualizar(Album album) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, album.getTitulo());
            if (album.getAnioLanzamiento() != null) {
                ps.setInt(2, album.getAnioLanzamiento());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, album.getSelloDiscografico());
            ps.setString(4, album.getTipo());
            ps.setString(5, album.getPortadaUrl());
            ps.setString(6, album.getDescripcion());
            ps.setInt(7, album.getIdArtista());
            ps.setInt(8, album.getIdAlbum());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Album con id " + album.getIdAlbum() + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al actualizar album: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idAlbum) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idAlbum);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Album con id " + idAlbum + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al eliminar album: " + e.getMessage(), e);
        }
    }
}