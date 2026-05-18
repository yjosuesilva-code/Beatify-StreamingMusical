package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Episodio;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EpisodioDAO {

    private static final String SQL_INSERT = """
        INSERT INTO EPISODIO
            (id_episodio, numero_episodio, titulo, descripcion, duracion_seg,
             fecha_publicacion, ruta_archivo, PODCAST_id_podcast)
        VALUES (seq_episodio.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_episodio, numero_episodio, titulo, descripcion, duracion_seg,
               fecha_publicacion, ruta_archivo, PODCAST_id_podcast
          FROM EPISODIO
         ORDER BY id_episodio
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_episodio, numero_episodio, titulo, descripcion, duracion_seg,
               fecha_publicacion, ruta_archivo, PODCAST_id_podcast
          FROM EPISODIO
         WHERE id_episodio = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE EPISODIO
           SET numero_episodio     = ?,
               titulo              = ?,
               descripcion         = ?,
               duracion_seg        = ?,
               fecha_publicacion   = ?,
               ruta_archivo        = ?,
               PODCAST_id_podcast  = ?
         WHERE id_episodio = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM EPISODIO WHERE id_episodio = ?";

    private Episodio mapearResultSet(ResultSet rs) throws SQLException {
        Integer idEpisodio     = rs.getInt("id_episodio");
        Integer numeroEpisodio = rs.getInt("numero_episodio");
        String  titulo         = rs.getString("titulo");
        String  descripcion    = rs.getString("descripcion");
        Integer duracion       = rs.getInt("duracion_seg");

        Date fechaSql = rs.getDate("fecha_publicacion");
        LocalDate fechaPublicacion = (fechaSql != null) ? fechaSql.toLocalDate() : null;

        String  rutaArchivo = rs.getString("ruta_archivo");
        Integer idPodcast   = rs.getInt("PODCAST_id_podcast");

        return new Episodio(idEpisodio, numeroEpisodio, titulo, descripcion,
                duracion, fechaPublicacion, rutaArchivo, idPodcast);
    }

    public Integer insertar(Episodio episodio) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_episodio"})) {

            ps.setInt(1, episodio.getNumeroEpisodio());
            ps.setString(2, episodio.getTitulo());
            ps.setString(3, episodio.getDescripcion());
            ps.setInt(4, episodio.getDuracion());

            LocalDate fp = episodio.getFechaPublicacion();
            if (fp != null) {
                ps.setDate(5, Date.valueOf(fp));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            ps.setString(6, episodio.getRutaArchivo());
            ps.setInt(7, episodio.getIdPodcast());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_episodio generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar episodio: " + e.getMessage(), e);
        }
    }

    public List<Episodio> listar() {
        List<Episodio> episodios = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                episodios.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar episodios: " + e.getMessage(), e);
        }
        return episodios;
    }

    public Episodio buscarPorId(Integer idEpisodio) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idEpisodio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Episodio con id " + idEpisodio + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar episodio: " + e.getMessage(), e);
        }
    }

    public void actualizar(Episodio episodio) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setInt(1, episodio.getNumeroEpisodio());
            ps.setString(2, episodio.getTitulo());
            ps.setString(3, episodio.getDescripcion());
            ps.setInt(4, episodio.getDuracion());

            LocalDate fp = episodio.getFechaPublicacion();
            if (fp != null) {
                ps.setDate(5, Date.valueOf(fp));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }

            ps.setString(6, episodio.getRutaArchivo());
            ps.setInt(7, episodio.getIdPodcast());
            ps.setInt(8, episodio.getIdEpisodio());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Episodio con id " + episodio.getIdEpisodio() + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar episodio: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idEpisodio) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idEpisodio);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Episodio con id " + idEpisodio + " no encontrado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar episodio: " + e.getMessage(), e);
        }
    }
}