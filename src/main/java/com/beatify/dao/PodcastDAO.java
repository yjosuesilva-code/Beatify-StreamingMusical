package com.beatify.dao;

import com.beatify.model.Podcast;
import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.util.Conexion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PodcastDAO {
    private static final String SQL_INSERT = """
        INSERT INTO PODCAST
            (id_podcast, titulo, descripcion,  categoria, portada_url, fecha_creacion, ARTISTA_id_artista)
        VALUES (seq_podcast.NEXTVAL, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_podcast, titulo, descripcion, categoria, portada_url, fecha_creacion, ARTISTA_id_artista
        FROM PODCAST
        ORDER BY id_podcast
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_podcast, titulo, descripcion, categoria, portada_url, fecha_creacion, ARTISTA_id_artista
        FROM PODCAST
        WHERE id_podcast = ?
        """;

    private static final String SQL_UPDATE = """
            UPDATE PODCAST
        SET titulo = ?, 
            descripcion = ?, 
            categoria = ?, 
            portada_url = ?, 
            fecha_creacion = ?, 
            ARTISTA_id_artista = ?
        WHERE id_podcast = ?
        """;

    private static final String SQL_DELETE = """
            DELETE FROM PODCAST
        WHERE id_podcast = ?
        """;

    private Podcast mapearResultSet(ResultSet rs) throws SQLException {
        Integer idPodcast = rs.getInt("id_podcast");
        String titulo = rs.getString("titulo");
        String descripcion = rs.getString("descripcion");
        String categoria = rs.getString("categoria");
        String portadaUrl = rs.getString("portada_url");
        Date fechaSql = rs.getDate("fecha_creacion");
        LocalDate fechaCreacion = (fechaSql != null) ? fechaSql.toLocalDate() : null;
        Integer idArtista = rs.getInt("ARTISTA_id_artista");
        return new Podcast(idPodcast, titulo, descripcion, categoria, portadaUrl, fechaCreacion, idArtista);
    }

    public Integer insertar(Podcast podcast){
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT, new String[]{"id_podcast"})) {
            ps.setString(1, podcast.getTitulo());
            ps.setString(2, podcast.getDescripcion());
            ps.setString(3, podcast.getCategoria());
            ps.setString(4, podcast.getPortadaUrl());
            if (podcast.getFechaCreacion() != null) {
                ps.setDate(5, Date.valueOf(podcast.getFechaCreacion()));
            } else {
                ps.setNull(5, Types.DATE);
            }            ps.setInt(6, podcast.getIdArtista());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("Error al obtener el ID generado para el podcast");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar el podcast: " + e.getMessage(), e);
        }
    }

    public List<Podcast> listar(){
        List<Podcast> podcasts = new ArrayList<>();
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                podcasts.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar los podcasts: " + e.getMessage(), e);
        }
        return podcasts;
    }

    public Podcast buscarPorId(Integer idPodcast){
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idPodcast);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("No se encontró el podcast con ID: " + idPodcast);
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar el podcast por ID: " + e.getMessage(), e);
        }
    }

    public void actualizar(Podcast podcast) {
        try(Connection conn = Conexion.getInstancia().obtenerConexion();
            PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, podcast.getTitulo());
            ps.setString(2, podcast.getDescripcion());
            ps.setString(3, podcast.getCategoria());
            ps.setString(4, podcast.getPortadaUrl());
            if (podcast.getFechaCreacion() != null) {
                ps.setDate(5, Date.valueOf(podcast.getFechaCreacion()));
            } else {
                ps.setNull(5, Types.DATE);
            }            ps.setInt(6, podcast.getIdArtista());
            ps.setInt(7, podcast.getIdPodcast());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("No se encontró el podcast con ID: " + podcast.getIdPodcast() + " para actualizar");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar el podcast: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idPodcast) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, idPodcast);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("No se encontró el podcast con ID: " + idPodcast + " para eliminar");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al eliminar el podcast: " + e.getMessage(), e);
        }
    }

}
