package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Artista;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ArtistaDAO {

    private static final String SQL_INSERT = """
        INSERT INTO ARTISTA
            (id_artista, nombre, apellido, nombre_artistico,
             fecha_nacimiento, pais, correo, biografia, foto_url)
        VALUES (seq_artista.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_artista, nombre, apellido, nombre_artistico,
               fecha_nacimiento, pais, correo, biografia, foto_url
          FROM ARTISTA
         ORDER BY id_artista
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_artista, nombre, apellido, nombre_artistico,
               fecha_nacimiento, pais, correo, biografia, foto_url
          FROM ARTISTA
         WHERE id_artista = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE ARTISTA
           SET nombre           = ?,
               apellido         = ?,
               nombre_artistico = ?,
               fecha_nacimiento = ?,
               pais             = ?,
               correo           = ?,
               biografia        = ?,
               foto_url         = ?
         WHERE id_artista = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM ARTISTA WHERE id_artista = ?";

    private Artista mapearResultSet(ResultSet rs) throws SQLException {
        Integer idArtista       = rs.getInt("id_artista");
        String  nombre          = rs.getString("nombre");
        String  apellido        = rs.getString("apellido");
        String  nombreArtistico = rs.getString("nombre_artistico");

        Date fechaNacSql = rs.getDate("fecha_nacimiento");
        LocalDate fechaNacimiento = (fechaNacSql != null) ? fechaNacSql.toLocalDate() : null;

        String  pais      = rs.getString("pais");
        String  correo    = rs.getString("correo");
        String  biografia = rs.getString("biografia");
        String  fotoUrl   = rs.getString("foto_url");

        return new Artista(
                idArtista, nombre, apellido, nombreArtistico,
                fechaNacimiento, pais, correo, biografia, fotoUrl
        );
    }

    public Integer insertar(Artista artista) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_artista"})) {

            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getApellido());
            ps.setString(3, artista.getNombreArtistico());

            // fecha_nacimiento puede ser null
            LocalDate fechaNac = artista.getFechaNacimiento();
            if (fechaNac != null) {
                ps.setDate(4, Date.valueOf(fechaNac));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setString(5, artista.getPais());
            ps.setString(6, artista.getCorreo());
            ps.setString(7, artista.getBiografia());
            ps.setString(8, artista.getFotoUrl());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException(
                        "No se pudo recuperar el id_artista generado por la secuencia");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al insertar artista: " + e.getMessage(), e);
        }
    }

    public List<Artista> listar() {
        List<Artista> artistas = new ArrayList<>();

        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                artistas.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al listar artistas: " + e.getMessage(), e);
        }

        return artistas;
    }

    public Artista buscarPorId(Integer idArtista) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idArtista);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException(
                        "Artista con id " + idArtista + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al buscar artista por id: " + e.getMessage(), e);
        }
    }

    public void actualizar(Artista artista) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, artista.getNombre());
            ps.setString(2, artista.getApellido());
            ps.setString(3, artista.getNombreArtistico());

            LocalDate fechaNac = artista.getFechaNacimiento();
            if (fechaNac != null) {
                ps.setDate(4, Date.valueOf(fechaNac));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setString(5, artista.getPais());
            ps.setString(6, artista.getCorreo());
            ps.setString(7, artista.getBiografia());
            ps.setString(8, artista.getFotoUrl());
            ps.setInt(9, artista.getIdArtista());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Artista con id " + artista.getIdArtista() + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al actualizar artista: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idArtista) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idArtista);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Artista con id " + idArtista + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al eliminar artista: " + e.getMessage(), e);
        }
    }
}