package com.beatify.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton que gestiona la conexion JDBC a Oracle Database XE 18c.
 * Lee las credenciales desde db.properties en src/main/resources.
 *
 * Patron de diseno aplicado: Singleton (GRASP - Pure Fabrication).
 *
 * @author Equipo Beatify
 */
public final class Conexion {

    private static Conexion instancia;

    private Connection conexion;
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
     * Devuelve la conexion JDBC activa. Si no existe o esta cerrada, la abre.
     */
    public Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, password);
        }
        return conexion;
    }

    /**
     * Cierra la conexion JDBC si esta abierta.
     */
    public void cerrar() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
            conexion = null;
        }
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