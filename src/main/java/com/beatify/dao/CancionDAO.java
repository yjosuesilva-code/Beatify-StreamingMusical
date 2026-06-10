package com.beatify.dao;

import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.model.Cancion;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CancionDAO {

    private static final String SQL_INSERT = """
        INSERT INTO CANCION
            (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor,
             fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero)
        VALUES (seq_cancion.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_SELECT_ALL = """
        SELECT id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor,
               fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero
          FROM CANCION
         ORDER BY id_cancion
        """;

    private static final String SQL_SELECT_BY_ID = """
        SELECT id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor,
               fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero
          FROM CANCION
         WHERE id_cancion = ?
        """;

    private static final String SQL_UPDATE = """
        UPDATE CANCION
           SET titulo             = ?,
               duracion_seg       = ?,
               ruta_archivo       = ?,
               letra              = ?,
               compositor         = ?,
               fecha_lanzamiento  = ?,
               ALBUM_id_album     = ?,
               GENERO_id_genero   = ?
         WHERE id_cancion = ?
        """;

    private static final String SQL_DELETE = "DELETE FROM CANCION WHERE id_cancion = ?";

    // Borrado de registros hijos que referencian la cancion por FK (NO ACTION).
    // Se ejecutan antes del DELETE de CANCION para evitar ORA-02292.
    private static final String SQL_DEL_LIKE_CANCION =
            "DELETE FROM LIKE_CANCION      WHERE CANCION_id_cancion = ?";
    private static final String SQL_DEL_REPRODUCCION =
            "DELETE FROM REPRODUCCION      WHERE CANCION_id_cancion = ?";
    private static final String SQL_DEL_CANCION_PLAYLIST =
            "DELETE FROM CANCION_PLAYLIST  WHERE CANCION_id_cancion = ?";
    private static final String SQL_DEL_COLABORACION =
            "DELETE FROM COLABORACION      WHERE CANCION_id_cancion = ?";

    private Cancion mapearResultSet(ResultSet rs) throws SQLException {
        Integer idCancion        = rs.getInt("id_cancion");
        String  titulo           = rs.getString("titulo");
        Integer duracionSegundos = rs.getInt("duracion_seg");
        String  rutaArchivo      = rs.getString("ruta_archivo");
        String  letra            = rs.getString("letra");
        String  compositor       = rs.getString("compositor");

        Date fechaSql = rs.getDate("fecha_lanzamiento");
        LocalDate fechaLanzamiento = (fechaSql != null) ? fechaSql.toLocalDate() : null;

        Integer idAlbum  = rs.getInt("ALBUM_id_album");
        Integer idGenero = rs.getInt("GENERO_id_genero");

        return new Cancion(idCancion, titulo, duracionSegundos, rutaArchivo, letra,
                compositor, fechaLanzamiento, idAlbum, idGenero);
    }

    public Integer insertar(Cancion cancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_cancion"})) {

            ps.setString(1, cancion.getTitulo());
            ps.setInt(2, cancion.getDuracionSegundos());
            ps.setString(3, cancion.getRutaArchivo());
            ps.setString(4, cancion.getLetra());
            ps.setString(5, cancion.getCompositor());

            LocalDate fl = cancion.getFechaLanzamiento();
            if (fl != null) {
                ps.setDate(6, Date.valueOf(fl));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            ps.setInt(7, cancion.getIdAlbum());
            ps.setInt(8, cancion.getIdGenero());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException("No se pudo recuperar el id_cancion generado");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al insertar cancion: " + e.getMessage(), e);
        }
    }

    public List<Cancion> listar() {
        List<Cancion> canciones = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                canciones.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al listar canciones: " + e.getMessage(), e);
        }
        return canciones;
    }

    public Cancion buscarPorId(Integer idCancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, idCancion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException("Cancion con id " + idCancion + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al buscar cancion: " + e.getMessage(), e);
        }
    }

    public void actualizar(Cancion cancion) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, cancion.getTitulo());
            ps.setInt(2, cancion.getDuracionSegundos());
            ps.setString(3, cancion.getRutaArchivo());
            ps.setString(4, cancion.getLetra());
            ps.setString(5, cancion.getCompositor());

            LocalDate fl = cancion.getFechaLanzamiento();
            if (fl != null) {
                ps.setDate(6, Date.valueOf(fl));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }

            ps.setInt(7, cancion.getIdAlbum());
            ps.setInt(8, cancion.getIdGenero());
            ps.setInt(9, cancion.getIdCancion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException("Cancion con id " + cancion.getIdCancion() + " no encontrada");
            }
        } catch (SQLException e) {
            throw new ConexionException("Error al actualizar cancion: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una cancion y, en cascada, todos sus registros hijos
     * (likes, reproducciones, presencia en playlists y colaboraciones).
     * Todo ocurre dentro de UNA transaccion: si algun paso falla, se hace
     * rollback y la cancion queda intacta. Esto evita el ORA-02292 que
     * producian las FKs NO ACTION.
     */
    public void eliminar(Integer idCancion) {
        Connection conn = null;
        try {
            conn = Conexion.getInstancia().obtenerConexion();
            conn.setAutoCommit(false);

            // 1) Borrar hijos que referencian la cancion
            borrarHijos(conn, SQL_DEL_LIKE_CANCION,     idCancion);
            borrarHijos(conn, SQL_DEL_REPRODUCCION,     idCancion);
            borrarHijos(conn, SQL_DEL_CANCION_PLAYLIST, idCancion);
            borrarHijos(conn, SQL_DEL_COLABORACION,     idCancion);

            // 2) Borrar la cancion
            final int filasAfectadas;
            try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
                ps.setInt(1, idCancion);
                filasAfectadas = ps.executeUpdate();
            }
            if (filasAfectadas == 0) {
                conn.rollback();
                throw new NotFoundException("Cancion con id " + idCancion + " no encontrada");
            }

            conn.commit();
        } catch (SQLException e) {
            rollbackSilencioso(conn);
            throw new ConexionException("Error al eliminar cancion: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            rollbackSilencioso(conn);
            throw e;
        } finally {
            cerrar(conn);
        }
    }

    private void borrarHijos(Connection conn, String sql, Integer idCancion) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCancion);
            ps.executeUpdate();
        }
    }

    private void rollbackSilencioso(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // nada que hacer si el rollback falla
            }
        }
    }

    private void cerrar(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {
                // nada que hacer al cerrar
            }
        }
    }
}