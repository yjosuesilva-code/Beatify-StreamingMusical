package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.CacheMusicBrainzArtista;
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
 * DAO para la tabla CACHE_MUSICBRAINZ_ARTISTA.
 * Ademas del CRUD estandar expone:
 * <ul>
 *   <li>{@link #buscarPorMbid(String)} — clave de negocio del cache</li>
 *   <li>{@link #eliminarVencidos()} — limpieza periodica (TTL)</li>
 * </ul>
 */
public class CacheMusicBrainzArtistaDAO {

    private static final String SQL_INSERT = """
        INSERT INTO CACHE_MUSICBRAINZ_ARTISTA
            (id_cache_mb_artista, mbid, ARTISTA_id_artista, nombre_completo,
             biografia, pais_origen, year_inicio, year_fin, tags, payload_json,
             fetched_at, expires_at, intentos)
        VALUES (seq_cache_mb_artista.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_cache_mb_artista, mbid, ARTISTA_id_artista, nombre_completo,
               biografia, pais_origen, year_inicio, year_fin, tags, payload_json,
               fetched_at, expires_at, intentos
          FROM CACHE_MUSICBRAINZ_ARTISTA
         ORDER BY id_cache_mb_artista
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_cache_mb_artista, mbid, ARTISTA_id_artista, nombre_completo,
               biografia, pais_origen, year_inicio, year_fin, tags, payload_json,
               fetched_at, expires_at, intentos
          FROM CACHE_MUSICBRAINZ_ARTISTA
         WHERE id_cache_mb_artista = ?
        """;

    private static final String SQL_SELECT_BY_MBID = """
        SELECT id_cache_mb_artista, mbid, ARTISTA_id_artista, nombre_completo,
               biografia, pais_origen, year_inicio, year_fin, tags, payload_json,
               fetched_at, expires_at, intentos
          FROM CACHE_MUSICBRAINZ_ARTISTA
         WHERE mbid = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE CACHE_MUSICBRAINZ_ARTISTA
           SET mbid               = ?,
               ARTISTA_id_artista = ?,
               nombre_completo    = ?,
               biografia          = ?,
               pais_origen        = ?,
               year_inicio        = ?,
               year_fin           = ?,
               tags               = ?,
               payload_json       = ?,
               fetched_at         = ?,
               expires_at         = ?,
               intentos           = ?
         WHERE id_cache_mb_artista = ?
        """;

    private static final String SQL_DELETE = """
        DELETE FROM CACHE_MUSICBRAINZ_ARTISTA WHERE id_cache_mb_artista = ?
        """;

    private static final String SQL_DELETE_VENCIDOS = """
        DELETE FROM CACHE_MUSICBRAINZ_ARTISTA WHERE expires_at < SYSDATE
        """;

    private CacheMusicBrainzArtista mapearResultSet(ResultSet rs) throws SQLException {
        Integer idCache         = rs.getInt("id_cache_mb_artista");
        String  mbid            = rs.getString("mbid");
        Integer idArtista       = rs.getObject("ARTISTA_id_artista", Integer.class);
        String  nombreCompleto  = rs.getString("nombre_completo");
        String  biografia       = rs.getString("biografia");
        String  paisOrigen      = rs.getString("pais_origen");
        Integer yearInicio      = rs.getObject("year_inicio", Integer.class);
        Integer yearFin         = rs.getObject("year_fin", Integer.class);
        String  tags            = rs.getString("tags");
        String  payloadJson     = rs.getString("payload_json");

        Timestamp tsFetched = rs.getTimestamp("fetched_at");
        LocalDateTime fetchedAt = (tsFetched != null) ? tsFetched.toLocalDateTime() : null;

        Timestamp tsExpires = rs.getTimestamp("expires_at");
        LocalDateTime expiresAt = (tsExpires != null) ? tsExpires.toLocalDateTime() : null;

        Integer intentos = rs.getObject("intentos", Integer.class);

        return new CacheMusicBrainzArtista(idCache, mbid, idArtista, nombreCompleto, biografia,
                paisOrigen, yearInicio, yearFin, tags, payloadJson, fetchedAt, expiresAt, intentos);
    }

    public Integer insertar(CacheMusicBrainzArtista cache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_cache_mb_artista"})) {

            ps.setString(1, cache.getMbid());

            if (cache.getIdArtista() != null) {
                ps.setInt(2, cache.getIdArtista());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, cache.getNombreCompleto());
            ps.setString(4, cache.getBiografia());
            ps.setString(5, cache.getPaisOrigen());
            ps.setObject(6, cache.getYearInicio(), Types.INTEGER);
            ps.setObject(7, cache.getYearFin(), Types.INTEGER);
            ps.setString(8, cache.getTags());
            ps.setString(9, cache.getPayloadJson());
            ps.setTimestamp(10, Timestamp.valueOf(cache.getFetchedAt()));
            ps.setTimestamp(11, Timestamp.valueOf(cache.getExpiresAt()));
            ps.setObject(12, cache.getIntentos(), Types.INTEGER);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_cache_mb_artista generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar cache MusicBrainz: " + e.getMessage(), e);
        }
    }

    public List<CacheMusicBrainzArtista> listar() {
        List<CacheMusicBrainzArtista> resultado = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar cache MusicBrainz: " + e.getMessage(), e);
        }
        return resultado;
    }

    public CacheMusicBrainzArtista buscarPorId(Integer idCache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idCache);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Cache MusicBrainz con id " + idCache + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar cache MusicBrainz por id: " + e.getMessage(), e);
        }
    }

    /**
     * Busca una entrada de cache por su MBID (UUID de MusicBrainz).
     * Devuelve null si no existe — distinto del CRUD estandar que lanza NotFoundException,
     * porque un cache miss es un caso esperado.
     */
    public CacheMusicBrainzArtista buscarPorMbid(String mbid) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_MBID)) {
            ps.setString(1, mbid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                return null; // cache miss
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar cache MusicBrainz por mbid: " + e.getMessage(), e);
        }
    }

    public void actualizar(CacheMusicBrainzArtista cache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, cache.getMbid());

            if (cache.getIdArtista() != null) {
                ps.setInt(2, cache.getIdArtista());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, cache.getNombreCompleto());
            ps.setString(4, cache.getBiografia());
            ps.setString(5, cache.getPaisOrigen());
            ps.setObject(6, cache.getYearInicio(), Types.INTEGER);
            ps.setObject(7, cache.getYearFin(), Types.INTEGER);
            ps.setString(8, cache.getTags());
            ps.setString(9, cache.getPayloadJson());
            ps.setTimestamp(10, Timestamp.valueOf(cache.getFetchedAt()));
            ps.setTimestamp(11, Timestamp.valueOf(cache.getExpiresAt()));
            ps.setObject(12, cache.getIntentos(), Types.INTEGER);
            ps.setInt(13, cache.getIdCacheMbArtista());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Cache MusicBrainz con id " + cache.getIdCacheMbArtista() + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar cache MusicBrainz: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idCache) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idCache);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Cache MusicBrainz con id " + idCache + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar cache MusicBrainz: " + e.getMessage(), e);
        }
    }

    /**
     * Borra todas las entradas cuyo expires_at ya paso. Devuelve el numero de filas borradas.
     * Pensado para correr periodicamente (job de limpieza) o desde un boton de mantenimiento.
     */
    public int eliminarVencidos() {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_VENCIDOS)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ConexionException("Error al limpiar cache MusicBrainz vencido: " + e.getMessage(), e);
        }
    }
}
