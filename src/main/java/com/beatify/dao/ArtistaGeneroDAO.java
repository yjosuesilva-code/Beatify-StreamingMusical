package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.ArtistaGenero;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistaGeneroDAO {

    private static final String SQL_INSERT = """
        INSERT INTO ARTISTA_GENERO
            (ARTISTA_id_artista, GENERO_id_genero)
        VALUES (?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT ARTISTA_id_artista, GENERO_id_genero
          FROM ARTISTA_GENERO
         ORDER BY ARTISTA_id_artista, GENERO_id_genero
        """;

    private static final String SQL_SELECT_BY_ARTISTA = """
        SELECT ARTISTA_id_artista, GENERO_id_genero
          FROM ARTISTA_GENERO
         WHERE ARTISTA_id_artista = ?
        """;

    private static final String SQL_DELETE = """
        DELETE FROM ARTISTA_GENERO
         WHERE ARTISTA_id_artista = ? AND GENERO_id_genero = ?
        """;

    private ArtistaGenero mapearResultSet(ResultSet rs) throws SQLException {
        Integer idArtista = rs.getInt("ARTISTA_id_artista");
        Integer idGenero  = rs.getInt("GENERO_id_genero");
        return new ArtistaGenero(idArtista, idGenero);
    }

    public void insertar(Integer idArtista, Integer idGenero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setInt(1, idArtista);
            ps.setInt(2, idGenero);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new ConexionException("No se insertó la relación artista-genero");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar relación artista-genero: " + e.getMessage(), e);
        }
    }

    public List<ArtistaGenero> listar() {
        List<ArtistaGenero> relaciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                relaciones.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar relaciones artista-genero: " + e.getMessage(), e);
        }
        return relaciones;
    }

    /**
     * Devuelve todos los géneros asociados a un artista específico.
     */
    public List<ArtistaGenero> listarPorArtista(Integer idArtista) {
        List<ArtistaGenero> relaciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ARTISTA)) {
            ps.setInt(1, idArtista);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    relaciones.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar generos del artista: " + e.getMessage(), e);
        }
        return relaciones;
    }

    public void eliminar(Integer idArtista, Integer idGenero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idArtista);
            ps.setInt(2, idGenero);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new NotFoundException(
                        "Relación artista-genero (" + idArtista + ", " + idGenero + ") no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar relación artista-genero: " + e.getMessage(), e);
        }
    }
}