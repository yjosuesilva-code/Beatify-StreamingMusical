package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.CacheLastFmArtista;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para CACHE_LASTFM_ARTISTA.
 *
 * Patrón análogo a CacheLastFmAlbumDAO pero con las particularidades del
 * cache de artista de Last.fm.
 *
 * Métodos de negocio:
 *  - buscarPorLastfmKey: devuelve null en cache miss (no lanza), porque el
 *    miss es caso esperado para un servicio de cache.
 *  - eliminarVencidos: borra entradas con expires_at < SYSDATE. Pensado
 *    para un job de mantenimiento periodico (TODO: SP_LIMPIAR_CACHE_VENCIDO).
 */
public class CacheLastFmArtistaDAO {

    private static final String SQL_INSERTAR = """
            INSERT INTO CACHE_LASTFM_ARTISTA (
                id_cache_lfm_artista, lastfm_key, ARTISTA_id_artista, mbid,
                nombre_artistico, biografia, foto_url, url_lastfm,
                listeners, playcount, tags, payload_json,
                fetched_at, expires_at, intentos
            ) VALUES (
                seq_cache_lfm_artista.NEXTVAL, ?, ?, ?,
                ?, ?, ?, ?,
                ?, ?, ?, ?,
                ?, ?, ?
            )
            """;

    private static final String SQL_LISTAR = """
            SELECT id_cache_lfm_artista, lastfm_key, ARTISTA_id_artista, mbid,
                   nombre_artistico, biografia, foto_url, url_lastfm,
                   listeners, playcount, tags, payload_json,
                   fetched_at, expires_at, intentos
            FROM CACHE_LASTFM_ARTISTA
            ORDER BY id_cache_lfm_artista
            """;

    private static final String SQL_BUSCAR_POR_ID = """
            SELECT id_cache_lfm_artista, lastfm_key, ARTISTA_id_artista, mbid,
                   nombre_artistico, biografia, foto_url, url_lastfm,
                   listeners, playcount, tags, payload_json,
                   fetched_at, expires_at, intentos
            FROM CACHE_LASTFM_ARTISTA
            WHERE id_cache_lfm_artista = ?
            """;

    private static final String SQL_BUSCAR_POR_KEY = """
            SELECT id_cache_lfm_artista, lastfm_key, ARTISTA_id_artista, mbid,
                   nombre_artistico, biografia, foto_url, url_lastfm,
                   listeners, playcount, tags, payload_json,
                   fetched_at, expires_at, intentos
            FROM CACHE_LASTFM_ARTISTA
            WHERE lastfm_key = ?
            """;

    private static final String SQL_ACTUALIZAR = """
            UPDATE CACHE_LASTFM_ARTISTA SET
                lastfm_key = ?, ARTISTA_id_artista = ?, mbid = ?,
                nombre_artistico = ?, biografia = ?, foto_url = ?, url_lastfm = ?,
                listeners = ?, playcount = ?, tags = ?, payload_json = ?,
                fetched_at = ?, expires_at = ?, intentos = ?
            WHERE id_cache_lfm_artista = ?
            """;

    private static final String SQL_ELIMINAR = """
            DELETE FROM CACHE_LASTFM_ARTISTA WHERE id_cache_lfm_artista = ?
            """;

    private static final String SQL_ELIMINAR_VENCIDOS = """
            DELETE FROM CACHE_LASTFM_ARTISTA WHERE expires_at < SYSDATE
            """;

    public Integer insertar(CacheLastFmArtista cache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERTAR,
                     new String[]{"id_cache_lfm_artista"})) {

            ps.setString(1, cache.getLastfmKey());
            // ARTISTA_id_artista es nullable
            if (cache.getArtistaIdArtista() != null) {
                ps.setInt(2, cache.getArtistaIdArtista());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, cache.getMbid());
            ps.setString(4, cache.getNombreArtistico());
            ps.setString(5, cache.getBiografia());
            ps.setString(6, cache.getFotoUrl());
            ps.setString(7, cache.getUrlLastfm());
            // listeners nullable
            if (cache.getListeners() != null) {
                ps.setInt(8, cache.getListeners());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            // playcount nullable
            if (cache.getPlaycount() != null) {
                ps.setInt(9, cache.getPlaycount());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            ps.setString(10, cache.getTags());
            ps.setString(11, cache.getPayloadJson());
            ps.setTimestamp(12, Timestamp.valueOf(cache.getFetchedAt()));
            ps.setTimestamp(13, Timestamp.valueOf(cache.getExpiresAt()));
            ps.setInt(14, cache.getIntentos());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Integer idGenerado = keys.getInt(1);
                    cache.setIdCacheLfmArtista(idGenerado);
                    return idGenerado;
                }
                throw new ConexionException("No se obtuvo id generado al insertar cache Last.fm artista");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error insertando cache Last.fm artista", e);
        }
    }

    public List<CacheLastFmArtista> listar() {
        List<CacheLastFmArtista> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.add(mapearResultSet(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new ConexionException("Error listando cache Last.fm artista", e);
        }
    }

    public CacheLastFmArtista buscarPorId(Integer id) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException(
                        "No existe cache Last.fm artista con id=" + id);
            }
        } catch (SQLException e) {
            throw new ConexionException(
                    "Error buscando cache Last.fm artista por id=" + id, e);
        }
    }

    /**
     * Devuelve la entrada de cache para la clave dada, o null si no existe.
     * El null se usa como senal de "cache miss" — caso esperado, no es error.
     */
    public CacheLastFmArtista buscarPorLastfmKey(String lastfmKey) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_BUSCAR_POR_KEY)) {

            ps.setString(1, lastfmKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                return null;  // cache miss — esperado
            }
        } catch (SQLException e) {
            throw new ConexionException(
                    "Error buscando cache Last.fm artista por lastfm_key=" + lastfmKey, e);
        }
    }

    public void actualizar(CacheLastFmArtista cache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, cache.getLastfmKey());
            if (cache.getArtistaIdArtista() != null) {
                ps.setInt(2, cache.getArtistaIdArtista());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, cache.getMbid());
            ps.setString(4, cache.getNombreArtistico());
            ps.setString(5, cache.getBiografia());
            ps.setString(6, cache.getFotoUrl());
            ps.setString(7, cache.getUrlLastfm());
            if (cache.getListeners() != null) {
                ps.setInt(8, cache.getListeners());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }
            if (cache.getPlaycount() != null) {
                ps.setInt(9, cache.getPlaycount());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            ps.setString(10, cache.getTags());
            ps.setString(11, cache.getPayloadJson());
            ps.setTimestamp(12, Timestamp.valueOf(cache.getFetchedAt()));
            ps.setTimestamp(13, Timestamp.valueOf(cache.getExpiresAt()));
            ps.setInt(14, cache.getIntentos());
            ps.setInt(15, cache.getIdCacheLfmArtista());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new NotFoundException(
                        "No existe cache Last.fm artista con id=" + cache.getIdCacheLfmArtista());
            }
        } catch (SQLException e) {
            throw new ConexionException("Error actualizando cache Last.fm artista", e);
        }
    }

    public void eliminar(Integer id) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new NotFoundException(
                        "No existe cache Last.fm artista con id=" + id);
            }
        } catch (SQLException e) {
            throw new ConexionException(
                    "Error eliminando cache Last.fm artista con id=" + id, e);
        }
    }

    /**
     * Borra todas las entradas vencidas (expires_at < SYSDATE).
     * @return cantidad de filas eliminadas
     */
    public int eliminarVencidos() {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_ELIMINAR_VENCIDOS)) {

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionException("Error eliminando cache Last.fm artista vencidos", e);
        }
    }

    private CacheLastFmArtista mapearResultSet(ResultSet rs) throws SQLException {
        Timestamp tsFetched = rs.getTimestamp("fetched_at");
        Timestamp tsExpires = rs.getTimestamp("expires_at");
        LocalDateTime fetchedAt = (tsFetched != null) ? tsFetched.toLocalDateTime() : null;
        LocalDateTime expiresAt = (tsExpires != null) ? tsExpires.toLocalDateTime() : null;

        return new CacheLastFmArtista(
                rs.getInt("id_cache_lfm_artista"),
                rs.getString("lastfm_key"),
                rs.getObject("ARTISTA_id_artista", Integer.class),
                rs.getString("mbid"),
                rs.getString("nombre_artistico"),
                rs.getString("biografia"),
                rs.getString("foto_url"),
                rs.getString("url_lastfm"),
                rs.getObject("listeners", Integer.class),
                rs.getObject("playcount", Integer.class),
                rs.getString("tags"),
                rs.getString("payload_json"),
                fetchedAt,
                expiresAt,
                rs.getInt("intentos")
        );
    }
}