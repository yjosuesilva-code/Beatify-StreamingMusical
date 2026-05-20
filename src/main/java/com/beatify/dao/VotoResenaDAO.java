package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.VotoResena;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VotoResenaDAO {

    private static final String SQL_INSERT = """
        INSERT INTO VOTO_RESENA
            (id_voto_resena, util, fecha_voto, RESENA_id_resena, CLIENTE_id_cliente)
        VALUES (seq_voto_resena.NEXTVAL, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_voto_resena, util, fecha_voto, RESENA_id_resena, CLIENTE_id_cliente
          FROM VOTO_RESENA
         ORDER BY id_voto_resena
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_voto_resena, util, fecha_voto, RESENA_id_resena, CLIENTE_id_cliente
          FROM VOTO_RESENA
         WHERE id_voto_resena = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE VOTO_RESENA
           SET util               = ?,
               fecha_voto         = ?,
               RESENA_id_resena   = ?,
               CLIENTE_id_cliente = ?
         WHERE id_voto_resena = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM VOTO_RESENA WHERE id_voto_resena = ?";

    private VotoResena mapearResultSet(ResultSet rs) throws SQLException {
        Integer idVotoResena = rs.getInt("id_voto_resena");
        String  util         = rs.getString("util");

        Timestamp ts = rs.getTimestamp("fecha_voto");
        LocalDateTime fechaVoto = (ts != null) ? ts.toLocalDateTime() : null;

        Integer idResena  = rs.getInt("RESENA_id_resena");
        Integer idCliente = rs.getInt("CLIENTE_id_cliente");

        return new VotoResena(idVotoResena, util, fechaVoto, idResena, idCliente);
    }

    public Integer insertar(VotoResena votoResena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_voto_resena"})) {

            ps.setString(1, votoResena.getUtil());

            LocalDateTime fv = votoResena.getFechaVoto();
            if (fv != null) {
                ps.setTimestamp(2, Timestamp.valueOf(fv));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }

            ps.setInt(3, votoResena.getIdResena());
            ps.setInt(4, votoResena.getIdCliente());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_voto_resena generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar voto_resena: " + e.getMessage(), e);
        }
    }

    public List<VotoResena> listar() {
        List<VotoResena> votos = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                votos.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar voto_resena: " + e.getMessage(), e);
        }
        return votos;
    }

    public VotoResena buscarPorId(Integer idVotoResena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idVotoResena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("VotoResena con id " + idVotoResena + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar voto_resena: " + e.getMessage(), e);
        }
    }

    public void actualizar(VotoResena votoResena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, votoResena.getUtil());

            LocalDateTime fv = votoResena.getFechaVoto();
            if (fv != null) {
                ps.setTimestamp(2, Timestamp.valueOf(fv));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }

            ps.setInt(3, votoResena.getIdResena());
            ps.setInt(4, votoResena.getIdCliente());
            ps.setInt(5, votoResena.getIdVotoResena());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "VotoResena con id " + votoResena.getIdVotoResena() + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar voto_resena: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idVotoResena) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idVotoResena);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("VotoResena con id " + idVotoResena + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar voto_resena: " + e.getMessage(), e);
        }
    }
}