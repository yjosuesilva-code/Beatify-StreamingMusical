package com.beatify.dao;

import com.beatify.model.Cliente;
import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.util.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private static final String SQL_INSERT = """
    INSERT INTO CLIENTE
        (id_cliente, nombre, apellido, correo, password_hash,
        telefono, direccion, ciudad, pais, fecha_registro, activo)
    VALUES (seq_cliente.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ?)
""";

    private static final String SQL_SELECT_ALL = """
    SELECT id_cliente, nombre, apellido, correo, password_hash,
           telefono, direccion, ciudad, pais, fecha_registro, activo, rol
      FROM CLIENTE
     ORDER BY id_cliente
""";

    private static final String SQL_SELECT_BY_ID = """
    SELECT id_cliente, nombre, apellido, correo, password_hash,
           telefono, direccion, ciudad, pais, fecha_registro, activo, rol
      FROM CLIENTE
     WHERE id_cliente = ?
""";

    private static final String SQL_SELECT_BY_CORREO = """
    SELECT id_cliente, nombre, apellido, correo, password_hash,
           telefono, direccion, ciudad, pais, fecha_registro, activo, rol
      FROM CLIENTE
     WHERE correo = ?
""";

    // OJO: este UPDATE NO toca password_hash a proposito.
    // Para cambiar la contrasena se usa actualizarPassword() desde el service.
    private static final String SQL_UPDATE = """
        UPDATE CLIENTE
            SET nombre = ?,
            apellido = ?,
            correo = ?,
            telefono = ?,
            direccion = ?,
            ciudad = ?,
            pais = ?,
            activo = ?
        WHERE id_cliente = ?
    """;

    private static final String SQL_UPDATE_PASSWORD = """
        UPDATE CLIENTE
           SET password_hash = ?
         WHERE id_cliente = ?
    """;

    private static final String SQL_DELETE = """
        UPDATE CLIENTE
            SET activo = 0
        WHERE id_cliente = ?
    """;

    private Cliente mapearResultSet(ResultSet rs) throws SQLException{
        Integer idCliente = rs.getInt("id_cliente");
        String nombre     = rs.getString("nombre");
        String apellido   = rs.getString("apellido");
        String correo     = rs.getString("correo");
        String passwordHash = rs.getString("password_hash");
        String telefono   = rs.getString("telefono");
        String direccion  = rs.getString("direccion");
        String ciudad     = rs.getString("ciudad");
        String pais       = rs.getString("pais");
        Boolean activo = "S".equals(rs.getString("activo"));
        Date fechaSql = rs.getDate("fecha_registro");
        LocalDate fechaRegistro = fechaSql != null ? fechaSql.toLocalDate() : null;
        String rol = rs.getString("rol");

        Cliente cliente = new Cliente(
                idCliente, nombre, apellido, correo, passwordHash, telefono, direccion,
                ciudad, pais, fechaRegistro, activo
        );
        if (rol != null) {
            cliente.setRol(rol);
        }
        return cliente;
    }

    public Integer insertar(Cliente cliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(
                     SQL_INSERT, new String[]{"id_cliente"})) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getPasswordHash());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getDireccion());
            ps.setString(7, cliente.getCiudad());
            ps.setString(8, cliente.getPais());
            ps.setString(9, (cliente.getActivo() == null || cliente.getActivo()) ? "S" : "N");
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ConexionException(
                        "No se pudo recuperar el id_cliente generado por la secuencia");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al insertar cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al listar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }
    public Cliente buscarPorId(Integer idCliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
                throw new NotFoundException(
                        "Cliente con id " + idCliente + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al buscar cliente por id: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorCorreo(String correo) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_CORREO)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }

                throw new NotFoundException(
                        "Cliente no encontrado con correo: " + correo);
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al buscar cliente por correo: " + e.getMessage(), e);
        }
    }


    public void actualizar(Cliente cliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getDireccion());
            ps.setString(6, cliente.getCiudad());
            ps.setString(7, cliente.getPais());
            ps.setString(8, (cliente.getActivo() == null || cliente.getActivo()) ? "S" : "N");
            ps.setInt(9, cliente.getIdCliente());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Cliente con id " + cliente.getIdCliente() + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza UNICAMENTE el hash de la contrasena del cliente.
     * La validacion de la contrasena actual debe hacerla la capa de servicio.
     */
    public void actualizarPassword(Integer idCliente, String passwordHash) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, idCliente);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Cliente con id " + idCliente + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al actualizar password del cliente: " + e.getMessage(), e);
        }
    }

    public void eliminar(Integer idCliente) {
        try (Connection conn = Conexion.getInstancia().obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, idCliente);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new NotFoundException(
                        "Cliente con id " + idCliente + " no encontrado");
            }

        } catch (SQLException e) {
            throw new ConexionException(
                    "Error al eliminar cliente: " + e.getMessage(), e);
        }
    }
}
