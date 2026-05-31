package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ClienteGeneroDAO {

    private static final String SQL_INSERT = """
        INSERT INTO CLIENTE_GENERO
            (CLIENTE_id_cliente, GENERO_id_genero)
        VALUES (?, ?)
        """;

    private static final String SQL_DELETE_POR_CLIENTE =
        "DELETE FROM CLIENTE_GENERO WHERE CLIENTE_id_cliente = ?";

    public void insertar(Integer idCliente, Integer idGenero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idGenero);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionException(
                "Error al insertar preferencia de género: " + e.getMessage(), e);
        }
    }

    public void insertarTodos(Integer idCliente, List<Integer> idsGenero) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            for (Integer idGenero : idsGenero) {
                ps.setInt(1, idCliente);
                ps.setInt(2, idGenero);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new ConexionException(
                "Error al insertar preferencias de género en lote: " + e.getMessage(), e);
        }
    }

    public void eliminarPorCliente(Integer idCliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_POR_CLIENTE)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionException(
                "Error al eliminar preferencias de género del cliente: " + e.getMessage(), e);
        }
    }
}
