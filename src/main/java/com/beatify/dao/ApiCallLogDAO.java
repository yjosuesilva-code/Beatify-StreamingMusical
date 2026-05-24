package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.ApiCallLog;
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

/**
 * DAO para la tabla API_CALL_LOG.
 * Es un log append-only: solo se insertan y consultan registros, no se actualizan.
 * Operaciones de mantenimiento (truncar/purgar) las hace el DBA o un job aparte.
 */
public class ApiCallLogDAO {

    private static final String SQL_INSERT = """
        INSERT INTO API_CALL_LOG
            (id_api_call, api_name, endpoint, metodo_http, parametros,
             codigo_respuesta, duracion_ms, fecha_llamada, exitoso, mensaje_error,
             CLIENTE_id_cliente)
        VALUES (seq_api_call_log.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_api_call, api_name, endpoint, metodo_http, parametros,
               codigo_respuesta, duracion_ms, fecha_llamada, exitoso, mensaje_error,
               CLIENTE_id_cliente
          FROM API_CALL_LOG
         ORDER BY id_api_call DESC
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_api_call, api_name, endpoint, metodo_http, parametros,
               codigo_respuesta, duracion_ms, fecha_llamada, exitoso, mensaje_error,
               CLIENTE_id_cliente
          FROM API_CALL_LOG
         WHERE id_api_call = ?
        """;

    private static final String SQL_SELECT_BY_API = """
        SELECT id_api_call, api_name, endpoint, metodo_http, parametros,
               codigo_respuesta, duracion_ms, fecha_llamada, exitoso, mensaje_error,
               CLIENTE_id_cliente
          FROM API_CALL_LOG
         WHERE api_name = ?
         ORDER BY id_api_call DESC
        """;

    private ApiCallLog mapearResultSet(ResultSet rs) throws SQLException {
        Integer idApiCall       = rs.getInt("id_api_call");
        String  apiName         = rs.getString("api_name");
        String  endpoint        = rs.getString("endpoint");
        String  metodoHttp      = rs.getString("metodo_http");
        String  parametros      = rs.getString("parametros");
        Integer codigoRespuesta = rs.getObject("codigo_respuesta", Integer.class);
        Integer duracionMs      = rs.getObject("duracion_ms", Integer.class);

        Timestamp tsFecha = rs.getTimestamp("fecha_llamada");
        LocalDateTime fechaLlamada = (tsFecha != null) ? tsFecha.toLocalDateTime() : null;

        String  exitoso      = rs.getString("exitoso");
        String  mensajeError = rs.getString("mensaje_error");
        Integer idCliente    = rs.getObject("CLIENTE_id_cliente", Integer.class);

        return new ApiCallLog(idApiCall, apiName, endpoint, metodoHttp, parametros,
                codigoRespuesta, duracionMs, fechaLlamada, exitoso, mensajeError, idCliente);
    }

    public Integer insertar(ApiCallLog log) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_api_call"})) {

            ps.setString(1, log.getApiName());
            ps.setString(2, log.getEndpoint());
            ps.setString(3, log.getMetodoHttp());
            ps.setString(4, log.getParametros());
            ps.setObject(5, log.getCodigoRespuesta(), Types.INTEGER);
            ps.setObject(6, log.getDuracionMs(), Types.INTEGER);
            ps.setTimestamp(7, Timestamp.valueOf(log.getFechaLlamada()));
            ps.setString(8, log.getExitoso());
            ps.setString(9, log.getMensajeError());

            if (log.getIdCliente() != null) {
                ps.setInt(10, log.getIdCliente());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_api_call generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar API call log: " + e.getMessage(), e);
        }
    }

    public List<ApiCallLog> listar() {
        List<ApiCallLog> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar API call log: " + e.getMessage(), e);
        }
        return resultado;
    }

    public ApiCallLog buscarPorId(Integer idApiCall) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idApiCall);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("API call log con id " + idApiCall + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar API call log por id: " + e.getMessage(), e);
        }
    }

    /**
     * Lista los registros de una API especifica (MUSICBRAINZ o LASTFM), ordenados
     * del mas reciente al mas antiguo. Util para auditoria y rate-limit por API.
     */
    public List<ApiCallLog> listarPorApi(String apiName) {
        List<ApiCallLog> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_API)) {
            ps.setString(1, apiName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapearResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar API call log por api: " + e.getMessage(), e);
        }
        return resultado;
    }
}
