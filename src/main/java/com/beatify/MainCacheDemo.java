package com.beatify;

import com.beatify.api.LastFmClient;
import com.beatify.api.MusicBrainzClient;
import com.beatify.api.dto.ArtistaApiDTO;
import com.beatify.dao.ApiCallLogDAO;
import com.beatify.dao.CacheLastFmAlbumDAO;
import com.beatify.dao.CacheLastFmArtistaDAO;
import com.beatify.dao.CacheMusicBrainzArtistaDAO;
import com.beatify.service.ILastFmCacheService;
import com.beatify.service.IMusicBrainzCacheService;
import com.beatify.service.LastFmCacheService;
import com.beatify.service.MusicBrainzCacheService;

/**
 * Demo CLI del bloque service cache.
 *
 * Cablea los clientes HTTP, los DAOs de cache + log, y los dos cache services.
 * Hace dos llamadas seguidas a cada cache para demostrar:
 *   - Primera llamada -> cache MISS, pega la API, deja fila en API_CALL_LOG
 *   - Segunda llamada -> cache HIT, lee de BD, NO deja fila nueva en API_CALL_LOG
 *
 * Usar desde IntelliJ (click derecho -> Run 'MainCacheDemo.main()').
 * Pre-requisitos:
 *   - Oracle XE corriendo con schema BEATIFY al dia (scripts 01-04 ejecutados)
 *   - api.properties con lastfm.api.key y lastfm.api.url validos
 *   - db.properties con credenciales BEATIFY
 *
 * NOTA: este demo NO se usa en la app JavaFX. Es solo para validacion
 * manual del bloque service cache.
 */
public class MainCacheDemo {

    private static final String ARTISTA_LASTFM = "Carlos Vives";
    private static final String ARTISTA_MB     = "Shakira";

    public static void main(String[] args) {
        // ---------- 1. Wiring ----------
        System.out.println(">> Cableando dependencias...");

        MusicBrainzClient mbClient = new MusicBrainzClient();
        LastFmClient      lfmClient = new LastFmClient();

        CacheMusicBrainzArtistaDAO mbCacheDAO        = new CacheMusicBrainzArtistaDAO();
        CacheLastFmArtistaDAO      lfmArtistaCacheDAO = new CacheLastFmArtistaDAO();
        CacheLastFmAlbumDAO        lfmAlbumCacheDAO   = new CacheLastFmAlbumDAO();
        ApiCallLogDAO              apiLogDAO          = new ApiCallLogDAO();

        IMusicBrainzCacheService mbCache = new MusicBrainzCacheService(
                mbClient, mbCacheDAO, apiLogDAO);
        ILastFmCacheService lfmCache = new LastFmCacheService(
                lfmClient, lfmArtistaCacheDAO, lfmAlbumCacheDAO, apiLogDAO);

        System.out.println(">> Wiring OK.\n");

        // ---------- 2. Demo Last.fm artista ----------
        ejecutarDemo("Last.fm", ARTISTA_LASTFM, () -> lfmCache.obtenerArtista(ARTISTA_LASTFM));

        // ---------- 3. Demo MusicBrainz artista ----------
        ejecutarDemo("MusicBrainz", ARTISTA_MB, () -> mbCache.obtenerArtista(ARTISTA_MB));

        // ---------- 4. Queries de verificacion ----------
        imprimirQueriesVerificacion();
    }

    /**
     * Ejecuta dos llamadas consecutivas al cache y compara tiempos.
     * Reporta MISS (1a) vs HIT (2a) con la diferencia.
     */
    private static void ejecutarDemo(String apiNombre,
                                     String artista,
                                     java.util.function.Supplier<ArtistaApiDTO> llamada) {
        System.out.println("=".repeat(60));
        System.out.println("=== " + apiNombre + " : " + artista);
        System.out.println("=".repeat(60));

        // Primera llamada — esperamos MISS
        System.out.println("\n[1ra llamada] Esperado: cache MISS + API + insert");
        long t1 = System.currentTimeMillis();
        ArtistaApiDTO dto1;
        try {
            dto1 = llamada.get();
        } catch (Exception e) {
            System.err.println("Fallo: " + e.getMessage());
            return;
        }
        long d1 = System.currentTimeMillis() - t1;
        System.out.println("Tiempo: " + d1 + " ms");
        System.out.println("DTO   : " + resumirDto(dto1));

        // Segunda llamada — esperamos HIT
        System.out.println("\n[2da llamada] Esperado: cache HIT (sin API)");
        long t2 = System.currentTimeMillis();
        ArtistaApiDTO dto2;
        try {
            dto2 = llamada.get();
        } catch (Exception e) {
            System.err.println("Fallo: " + e.getMessage());
            return;
        }
        long d2 = System.currentTimeMillis() - t2;
        System.out.println("Tiempo: " + d2 + " ms");
        System.out.println("DTO   : " + resumirDto(dto2));

        // Diff
        long speedup = (d2 > 0) ? (d1 / d2) : Long.MAX_VALUE;
        System.out.println("\n>> Diff: 1ra " + d1 + " ms / 2da " + d2 + " ms (~" + speedup + "x mas rapido)\n");
    }

    /** Imprime un resumen corto del DTO para no llenar la consola. */
    private static String resumirDto(ArtistaApiDTO dto) {
        if (dto == null) return "null";
        return "mbid=" + dto.mbid()
                + ", nombre=" + dto.nombre()
                + ", pais=" + dto.pais()
                + ", generos=" + (dto.generos() == null ? 0 : dto.generos().size());
    }

    /** Queries para verificar manualmente en SQL Developer despues del demo. */
    private static void imprimirQueriesVerificacion() {
        System.out.println("=".repeat(60));
        System.out.println("VERIFICACION MANUAL (SQL Developer)");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("-- 1) Deben existir 2 filas (una por cada artista cacheado):");
        System.out.println("SELECT lastfm_key, nombre_artistico, fetched_at, expires_at, intentos");
        System.out.println("FROM CACHE_LASTFM_ARTISTA");
        System.out.println("WHERE lastfm_key = LOWER('" + ARTISTA_LASTFM + "');");
        System.out.println();
        System.out.println("SELECT mbid, nombre_completo, pais_origen, fetched_at, expires_at, intentos");
        System.out.println("FROM CACHE_MUSICBRAINZ_ARTISTA");
        System.out.println("WHERE LOWER(nombre_completo) = LOWER('" + ARTISTA_MB + "');");
        System.out.println();
        System.out.println("-- 2) En API_CALL_LOG debe haber EXACTAMENTE 1 fila por cada artista");
        System.out.println("--    (la 2da llamada NO pego la API, fue cache HIT):");
        System.out.println("SELECT api_name, endpoint, parametros, codigo_respuesta, exitoso, duracion_ms");
        System.out.println("FROM API_CALL_LOG");
        System.out.println("WHERE fecha_llamada > SYSDATE - INTERVAL '5' MINUTE");
        System.out.println("ORDER BY fecha_llamada DESC;");
        System.out.println();
        System.out.println("-- 3) Para validar 'eliminarVencidos' con TTL artificial corto:");
        System.out.println("--    UPDATE CACHE_LASTFM_ARTISTA SET expires_at = SYSTIMESTAMP - 1");
        System.out.println("--    WHERE lastfm_key = LOWER('" + ARTISTA_LASTFM + "');");
        System.out.println("--    COMMIT;");
        System.out.println("--    -> Luego correr el demo de nuevo: 1ra llamada debe ser cache MISS (porque expiro).");
        System.out.println();
    }
}