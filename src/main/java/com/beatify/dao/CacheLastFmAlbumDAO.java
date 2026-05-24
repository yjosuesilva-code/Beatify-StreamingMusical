package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.CacheLastFmAlbum;
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
 * DAO para la tabla CACHE_LASTFM_ALBUM.
 * Ademas del CRUD estandar expone:
 * <ul>
 *   <li>{@link #buscarPorLastfmKey(String)} — clave de negocio del cache</li>
 *   <li>{@link #eliminarVencidos()} — limpieza periodica (TTL)</li>
 * </ul>
 */
public class CacheLastFmAlbumDAO {

    private static final String SQL_INSERT = """
        INSERT INTO CACHE_LASTFM_ALBUM
            (id_cache_lfm_album, lastfm_key, ALBUM_id_album, mbid, titulo,
             artista_nombre, url_lastfm, portada_url_lastfm, listeners, playcount,
             tags, payload_json, fetched_at, expires_at, intentos)
        VALUES (seq_cache_lfm_album.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_cache_lfm_album, lastfm_key, ALBUM_id_album, mbid, titulo,
               artista_nombre, url_lastfm, portada_url_lastfm, listeners, playcount,
               tags, payload_json, fetched_at, expires_at, intentos
          FROM CACHE_LASTFM_ALBUM
         ORDER BY id_cache_lfm_album
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_cache_lfm_album, lastfm_key, ALBUM_id_album, mbid, titulo,
               artista_nombre, url_lastfm, portada_url_lastfm, listeners, playcount,
               tags, payload_json, fetched_at, expires_at, intentos
          FROM CACHE_LASTFM_ALBUM
         WHERE id_cache_lfm_album = ?
        """;

    private static final String SQL_SELECT_BY_KEY = """
        SELECT id_cache_lfm_album, lastfm_key, ALBUM_id_album, mbid, titulo,
               artista_nombre, url_lastfm, portada_url_lastfm, listeners, playcount,
               tags, payload_json, fetched_at, expires_at, intentos
          FROM CACHE_LASTFM_ALBUM
         WHERE lastfm_key = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE CACHE_LASTFM_ALBUM
           SET lastfm_key         = ?,
               ALBUM_id_album     = ?,
               mbid               = ?,
               titulo             = ?,
               artista_nombre     = ?,
               url_lastfm         = ?,
               portada_url_lastfm = ?,
               listeners          = ?,
               playcount          = ?,
               tags               = ?,
               payload_json       = ?,
               fetched_at         = ?,
               expires_at         = ?,
               intentos           = ?
         WHERE id_cache_lfm_album = ?
        """;

    private static final String SQL_DELETE = """
        DELETE FROM CACHE_LASTFM_ALBUM WHERE id_cache_lfm_album = ?
        """;

    private static final String SQL_DELETE_VENCIDOS = """
        DELETE FROM CACHE_LASTFM_ALBUM WHERE expires_at < SYSDATE
        """;

    private CacheLastFmAlbum mapearResultSet(ResultSet rs) throws SQLException {
        Integer idCache          = rs.getInt("id_cache_lfm_album");
        String  lastfmKey        = rs.getString("lastfm_key");
        Integer idAlbum          = rs.getObject("ALBUM_id_album", Integer.class);
        String  mbid             = rs.getString("mbid");
        String  titulo           = rs.getString("titulo");
        String  artistaNombre    = rs.getString("artista_nombre");
        String  urlLastfm        = rs.getString("url_lastfm");
        String  portadaUrlLastfm = rs.getString("portada_url_lastfm");
        Integer listeners        = rs.getObject("listeners", Integer.class);
        Integer playcount        = rs.getObject("playcount", Integer.class);
        String  tags             = rs.getString("tags");
        String  payloadJson      = rs.getString("payload_json");

        Timestamp tsFetched = rs.getTimestamp("fetched_at");
        LocalDateTime fetchedAt = (tsFetched != null) ? tsFetched.toLocalDateTime() : null;

        Timestamp tsExpires = rs.getTimestamp("expires_at");
        LocalDateTime expiresAt = (tsExpires != null) ? tsExpires.toLocalDateTime() : null;

        Integer intentos = rs.getObject("intentos", Integer.class);

        return new CacheLastFmAlbum(idCache, lastfmKey, idAlbum, mbid, titulo, artistaNombre,
                urlLastfm, portadaUrlLastfm, listeners, playcount, tags, payloadJson,
                fetchedAt, expiresAt, intentos);
    }

    public Integer insertar(CacheLastFmAlbum cache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_cache_lfm_album"})) {

            ps.setString(1, cache.getLastfmKey());

            if (cache.getIdAlbum() != null) {
                ps.setInt(2, cache.getIdAlbum());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, cache.getMbid());
            ps.setString(4, cache.getTitulo());
            ps.setString(5, cache.getArtistaNombre());
            ps.setString(6, cache.getUrlLastfm());
            ps.setString(7, cache.getPortadaUrlLastfm());
            ps.setObject(8, cache.getListeners(), Types.INTEGER);
            ps.setObject(9, cache.getPlaycount(), Types.INTEGER);
            ps.setString(10, cache.getTags());
            ps.setString(11, cache.getPayloadJson());
            ps.setTimestamp(12, Timestamp.valueOf(cache.getFetchedAt()));
            ps.setTimestamp(13, Timestamp.valueOf(cache.getExpiresAt()));
            ps.setObject(14, cache.getIntentos(), Types.INTEGER);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_cache_lfm_album generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar cache Last.fm: " + e.getMessage(), e);
        }
    }

    public List<CacheLastFmAlbum> listar() {
        List<CacheLastFmAlbum> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar cache Last.fm: " + e.getMessage(), e);
        }
        return resultado;
    }

    public CacheLastFmAlbum buscarPorId(Integer idCache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idCache);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Cache Last.fm con id " + idCache + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar cache Last.fm por id: " + e.getMessage(), e);
        }
    }

    /**
     * Busca una entrada de cache por su clave Last.fm (formato "artista::album").
     * Devuelve null si no existe — distinto del CRUD estandar que lanza NotFoundException,
     * porque un cache miss es un caso esperado.
     */
    public CacheLastFmAlbum buscarPorLastfmKey(String lastfmKey) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_KEY)) {
            ps.setString(1, lastfmKey);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                return null; // cache miss
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar cache Last.fm por key: " + e.getMessage(), e);
        }
    }

    public void actualizar(CacheLastFmAlbum cache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, cache.getLastfmKey());

            if (cache.getIdAlbum() != null) {
                ps.setInt(2, cache.getIdAlbum());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, cache.getMbid());
            ps.setString(4, cache.getTitulo());
            ps.setString(5, cache.getArtistaNombre());
            ps.setString(6, cache.getUrlLastfm());
            ps.setString(7, cache.getPortadaUrlLastfm());
            ps.setObject(8, cache.getListeners(), Types.INTEGER);
            ps.setObject(9, cache.getPlaycount(), Types.INTEGER);
            ps.setString(10, cache.getTags());
            ps.setString(11, cache.getPayloadJson());
            ps.setTimestamp(12, Timestamp.valueOf(cache.getFetchedAt()));
            ps.setTimestamp(13, Timestamp.valueOf(cache.getExpiresAt()));
            ps.setObject(14, cache.getIntentos(), Types.INTEGER);
            ps.setInt(15, cache.getIdCacheLfmAlbum());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Cache Last.fm con id " + cache.getIdCacheLfmAlbum() + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar cache Last.fm: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idCache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idCache);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Cache Last.fm con id " + idCache + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar cache Last.fm: " + e.getMessage(), e);
        }
    }

    /**
     * Borra todas las entradas cuyo expires_at ya paso. Devuelve el numero de filas borradas.
     */
    public int eliminarVencidos() {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_VENCIDOS)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionException("Error al limpiar cache Last.fm vencido: " + e.getMessage(), e);
        }
    }
}
