package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Genero;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GeneroDAO {

    private static final String SQL_INSERT = """
        INSERT INTO GENERO
            (id_genero, nombre, descripcion, origen_pais)
        VALUES (seq_genero.NEXTVAL, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_genero, nombre, descripcion, origen_pais
          FROM GENERO
         ORDER BY id_genero
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_genero, nombre, descripcion, origen_pais
          FROM GENERO
         WHERE id_genero = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE GENERO
           SET nombre      = ?,
               descripcion = ?,
               origen_pais = ?
         WHERE id_genero = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM GENERO WHERE id_genero = ?";

    private Genero mapearResultSet(ResultSet rs) throws SQLException {
        Integer idGenero    = rs.getInt("id_genero");
        String  nombre      = rs.getString("nombre");
        String  descripcion = rs.getString("descripcion");
        String  origenPais  = rs.getString("origen_pais");
        return new Genero(idGenero, nombre, descripcion, origenPais);
    }

    public Integer insertar(Genero genero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_genero"})) {

            ps.setString(1, genero.getNombre());
            ps.setString(2, genero.getDescripcion());
            ps.setString(3, genero.getOrigenPais());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_genero generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar genero: " + e.getMessage(), e);
        }
    }

    public List<Genero> listar() {
        List<Genero> generos = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                generos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar generos: " + e.getMessage(), e);
        }
        return generos;
    }

    public Genero buscarPorId(Integer idGenero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idGenero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Genero con id " + idGenero + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar genero: " + e.getMessage(), e);
        }
    }

    public void actualizar(Genero genero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, genero.getNombre());
            ps.setString(2, genero.getDescripcion());
            ps.setString(3, genero.getOrigenPais());
            ps.setInt(4, genero.getIdGenero());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Genero con id " + genero.getIdGenero() + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar genero: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idGenero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idGenero);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Genero con id " + idGenero + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar genero: " + e.getMessage(), e);
        }
    }
}