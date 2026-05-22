package com.beatify;

import com.beatify.api.LastFmClient;
import com.beatify.api.MusicBrainzClient;
import com.beatify.dao.AlbumDAO;
import com.beatify.dao.ArtistaDAO;
import com.beatify.dao.ArtistaGeneroDAO;
import com.beatify.dao.GeneroDAO;
import com.beatify.model.Album;
import com.beatify.model.Artista;
import com.beatify.service.EnriquecimientoService;
import com.beatify.util.Conexion;

import java.sql.Connection;
import java.sql.SQLException;


public class Main {

    public static void main(String[] args) {
        probarConexion();
        probarEnriquecimiento();
    }

    // -------------------------------------------------------------------------

    private static void probarConexion() {
        System.out.println("======================================");
        System.out.println("  BEATIFY - Prueba de Conexion BD");
        System.out.println("======================================");
        try (Connection conn = Conexion.getInstancia().obtenerConexion()) {

            System.out.println("[OK] Conexion establecida con Oracle XE");
            System.out.println("  - URL: " + conn.getMetaData().getURL());
            System.out.println("  - Usuario: " + conn.getMetaData().getUserName());
            System.out.println("  - Driver: " + conn.getMetaData().getDriverName());
            System.out.println("  - Version BD: " + conn.getMetaData().getDatabaseProductVersion().split("\n")[0]);

            // El try-with-resources cierra la conexion al salir de este bloque
        } catch (SQLException e) {
            System.err.println("[ERROR] No se pudo conectar a Oracle:");
            System.err.println("  " + e.getMessage());
        }
        System.out.println("[OK] Conexion cerrada correctamente");
    }

    // -------------------------------------------------------------------------

    private static void probarEnriquecimiento() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("  BEATIFY - Prueba de APIs Externas");
        System.out.println("======================================");

        EnriquecimientoService svc = new EnriquecimientoService(
                new LastFmClient(),
                new MusicBrainzClient(),
                new ArtistaDAO(),
                new AlbumDAO(),
                new GeneroDAO(),
                new ArtistaGeneroDAO()
        );

        // --- Prueba: enriquecer artista ---
        try {
            System.out.println("\n[TEST] Enriqueciendo artista: Carlos Vives");
            Artista artista = svc.enriquecerArtista("Carlos Vives");

            System.out.println("[OK] Artista guardado:");
            System.out.println("  - ID          : " + artista.getIdArtista());
            System.out.println("  - Nombre art. : " + artista.getNombreArtistico());
            System.out.println("  - Pais        : " + artista.getPais());
            System.out.println("  - Foto URL    : " + artista.getFotoUrl());
            System.out.println("  - Bio (100c)  : " + truncar(artista.getBiografia(), 100));

            // --- Prueba: enriquecer album ---
            System.out.println("\n[TEST] Enriqueciendo album: El Amor de Mi Tierra");
            Album album = svc.enriquecerAlbum(
                    "Carlos Vives", "El Amor de Mi Tierra", artista.getIdArtista());

            System.out.println("[OK] Album guardado:");
            System.out.println("  - ID          : " + album.getIdAlbum());
            System.out.println("  - Titulo      : " + album.getTitulo());
            System.out.println("  - Año         : " + album.getAnioLanzamiento());
            System.out.println("  - Sello       : " + album.getSelloDiscografico());
            System.out.println("  - Tipo        : " + album.getTipo());
            System.out.println("  - Portada URL : " + album.getPortadaUrl());

        } catch (Exception e) {
            System.err.println("[ERROR] Fallo el enriquecimiento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Recorta un texto a maxChars caracteres para mostrar en consola. */
    private static String truncar(String texto, int maxChars) {
        if (texto == null) return "(null)";
        return texto.length() <= maxChars ? texto : texto.substring(0, maxChars) + "...";
    }
}