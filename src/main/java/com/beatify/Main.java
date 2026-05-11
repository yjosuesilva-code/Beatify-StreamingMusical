package com.beatify;

import com.beatify.util.Conexion;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Punto de entrada de Beatify. En esta primera version solo prueba la conexion.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("  BEATIFY - Prueba de Conexion BD");
        System.out.println("======================================");
        try {
            Conexion conexion = Conexion.getInstancia();
            Connection conn = conexion.obtenerConexion();

            System.out.println("[OK] Conexion establecida con Oracle XE");
            System.out.println("  - URL: " + conn.getMetaData().getURL());
            System.out.println("  - Usuario: " + conn.getMetaData().getUserName());
            System.out.println("  - Driver: " + conn.getMetaData().getDriverName());
            System.out.println("  - Version BD: " + conn.getMetaData().getDatabaseProductVersion().split("\n")[0]);

            conexion.cerrar();
            System.out.println("[OK] Conexion cerrada correctamente");
        } catch (SQLException e) {
            System.err.println("[ERROR] No se pudo conectar a Oracle:");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
        }
    }
}