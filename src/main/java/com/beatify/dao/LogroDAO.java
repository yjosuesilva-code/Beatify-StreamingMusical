package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Logro;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogroDAO {
    private static final String SQL_INSERT = """
        INSERT INTO LOGRO
            (id_logro, codigo, nombre, descripcion, icono_url, puntos)
        VALUES (seq_logro.NEXTVAL, ?, ?, ?, ?, ?)
    """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_logro, codigo, nombre, descripcion, icono_url, puntos
        FROM LOGRO
        ORDER BY id_logro
    """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_logro, codigo, nombre, descripcion, icono_url, puntos
        FROM LOGRO
        WHERE id_logro = ?
    """;
    private static final String SQL_UPDATE = """
        UPDATE LOGRO
            SET codigo = ?, 
            nombre = ?, 
            descripcion = ?,
            icono_url = ?,
            puntos = ?
        WHERE id_logro = ?
    """;

    private static final String SQL_DELETE = """
        DELETE FROM LOGRO
        WHERE id_logro = ?
    """;

    private Logro mapearResultSet(ResultSet rs) throws SQLException {
        Integer idLogro = rs.getInt("id_logro");
        String codigo = rs.getString("codigo");
        String nombre = rs.getString("nombre");
        String descripcion = rs.getString("descripcion");
        String iconoUrl = rs.getString("icono_url");
        Integer puntos = rs.getInt("puntos");
        return new Logro(idLogro, codigo, nombre, descripcion, iconoUrl, puntos);
    }

    public Integer insertar(Logro logro){
        try(Connection con = Conexion.getInstancia().obtenerConexion();
            var ps = con.prepareStatement(SQL_INSERT, new String[]{"id_logro"})){

            ps.setString(1, logro.getCodigo());
            ps.setString(2, logro.getNombre());
            ps.setString(3, logro.getDescripcion());
            ps.setString(4, logro.getIconoUrl());
            ps.setInt(5, logro.getPuntos());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo obtener el ID generado para el logro.");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar logro: " + e.getMessage(), e);
        }
    }

    public List<Logro> listar() {
        List<Logro> logros = new ArrayList<>();
        try (Connection con = Conexion.getInstancia().obtenerConexion();
             var ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logros.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar logros: " + e.getMessage(), e);
        }
        return logros;
    }

    public Logro buscarPorId(Integer idLogro) {
        try (Connection con = Conexion.getInstancia().obtenerConexion();
             var ps = con.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idLogro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Logro con id " + idLogro + " no encontrado.");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar logro por ID: " + e.getMessage(), e);
        }
    }

    public void actualizar(Logro logro){
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            var ps = conn.prepareStatement(SQL_UPDATE)){

            ps.setString(1, logro.getCodigo());
            ps.setString(2, logro.getNombre());
            ps.setString(3, logro.getDescripcion());
            ps.setString(4, logro.getIconoUrl());
            ps.setInt(5, logro.getPuntos());
            ps.setInt(6, logro.getIdLogro());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Logro con id " + logro.getIdLogro() + " no encontrado para actualizar.");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar el logro: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idLogro) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             var ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idLogro);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Logro con id " + idLogro + " no encontrado para eliminar.");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar el logro: " + e.getMessage(), e);
        }
    }

}
