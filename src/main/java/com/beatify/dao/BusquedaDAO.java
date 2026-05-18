package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Busqueda;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BusquedaDAO {

    private static final String SQL_INSERT = """
        INSERT INTO BUSQUEDA
            (id_busqueda, termino_buscado, fecha_busqueda,
             resultados_obtenidos, CLIENTE_id_cliente)
        VALUES (seq_busqueda.NEXTVAL, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_busqueda, termino_buscado, fecha_busqueda,
               resultados_obtenidos, CLIENTE_id_cliente
          FROM BUSQUEDA
         ORDER BY id_busqueda
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_busqueda, termino_buscado, fecha_busqueda,
               resultados_obtenidos, CLIENTE_id_cliente
          FROM BUSQUEDA
         WHERE id_busqueda = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE BUSQUEDA
           SET termino_buscado      = ?,
               fecha_busqueda       = ?,
               resultados_obtenidos = ?,
               CLIENTE_id_cliente   = ?
         WHERE id_busqueda = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM BUSQUEDA WHERE id_busqueda = ?";

    private Busqueda mapearResultSet(ResultSet rs) throws SQLException {
        Integer idBusqueda     = rs.getInt("id_busqueda");
        String  terminoBuscado = rs.getString("termino_buscado");

        Timestamp tsSql = rs.getTimestamp("fecha_busqueda");
        LocalDateTime fechaBusqueda = (tsSql != null) ? tsSql.toLocalDateTime() : null;

        Integer resultadosObtenidos = rs.getInt("resultados_obtenidos");
        Integer idCliente           = rs.getInt("CLIENTE_id_cliente");

        return new Busqueda(idBusqueda, terminoBuscado, fechaBusqueda, resultadosObtenidos, idCliente);
    }

    public Integer insertar(Busqueda busqueda) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_busqueda"})) {

            ps.setString(1, busqueda.getTerminoBusqueda());

            LocalDateTime fb = busqueda.getFechaBusqueda();
            if (fb != null) {
                ps.setTimestamp(2, Timestamp.valueOf(fb));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }

            if (busqueda.getResultadosObtenidos() != null) {
                ps.setInt(3, busqueda.getResultadosObtenidos());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setInt(4, busqueda.getIdCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_busqueda generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar busqueda: " + e.getMessage(), e);
        }
    }

    public List<Busqueda> listar() {
        List<Busqueda> busquedas = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                busquedas.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar busquedas: " + e.getMessage(), e);
        }
        return busquedas;
    }

    public Busqueda buscarPorId(Integer idBusqueda) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idBusqueda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Busqueda con id " + idBusqueda + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar busqueda: " + e.getMessage(), e);
        }
    }

    public void actualizar(Busqueda busqueda) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, busqueda.getTerminoBusqueda());

            LocalDateTime fb = busqueda.getFechaBusqueda();
            if (fb != null) {
                ps.setTimestamp(2, Timestamp.valueOf(fb));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }

            if (busqueda.getResultadosObtenidos() != null) {
                ps.setInt(3, busqueda.getResultadosObtenidos());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setInt(4, busqueda.getIdCliente());
            ps.setInt(5, busqueda.getIdBusqueda());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Busqueda con id " + busqueda.getIdBusqueda() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar busqueda: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idBusqueda) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idBusqueda);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Busqueda con id " + idBusqueda + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar busqueda: " + e.getMessage(), e);
        }
    }
}