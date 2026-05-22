package com.beatify.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton que gestiona el ACCESO a la BD Oracle XE 18c.
 *
 * Lee las credenciales desde db.properties UNA sola vez y registra el driver.
 * Cada llamada a {@link #obtenerConexion()} devuelve una conexion NUEVA
 * (no compartida) para que los DAOs puedan usar try-with-resources con
 * seguridad y para evitar race conditions entre hilos JavaFX.
 *
 * Patron de diseno aplicado: Singleton (GRASP - Pure Fabrication).
 *
 * @author Equipo Beatify
 */
public final class Conexion {

    private static Conexion instancia;

    private final String url;
    private final String usuario;
    private final String password;

    /**
     * Constructor privado: lee db.properties y registra el driver Oracle.
     */
    private Conexion() {
        Properties props = cargarPropiedades();
        this.url = props.getProperty("db.url");
        this.usuario = props.getProperty("db.user");
        this.password = props.getProperty("db.password");
        try {
            Class.forName(props.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver Oracle no encontrado en el classpath", e);
        }
    }

    /**
     * Devuelve la unica instancia de Conexion (Singleton thread-safe).
     */
    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    /**
     * Abre y devuelve una conexion JDBC NUEVA cada vez.
     *
     * El llamador es responsable de cerrarla, normalmente con try-with-resources:
     * <pre>
     * try (Connection conn = Conexion.getInstancia().obtenerConexion();
     *      PreparedStatement ps = conn.prepareStatement(SQL)) {
     *     ...
     * }
     * </pre>
     */
    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    /**
     * Carga las propiedades desde db.properties en el classpath.
     */
    private Properties cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream is = Conexion.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("No se encontro db.properties en resources/");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo db.properties", e);
        }
        return props;
    }
}