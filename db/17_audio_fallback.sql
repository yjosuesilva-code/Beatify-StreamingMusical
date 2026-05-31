-- ============================================================
-- 17_audio_fallback.sql
-- Las 8 canciones sin archivo de audio propio se apuntan a otra
-- canción del MISMO artista, para que suenen en la demo.
-- (El audio no es exacto; reemplazar por el MP3 real cuando se tenga
--  y restaurar la ruta original ruta_archivo='audio/<nombre>.mp3'.)
-- ============================================================
SET DEFINE OFF;

-- Aterciopelados (solo hay at_florecita.mp3 disponible)
UPDATE CANCION SET ruta_archivo='audio/at_florecita.mp3' WHERE ruta_archivo='audio/at_bolero.mp3';
UPDATE CANCION SET ruta_archivo='audio/at_florecita.mp3' WHERE ruta_archivo='audio/at_estuche.mp3';
UPDATE CANCION SET ruta_archivo='audio/at_florecita.mp3' WHERE ruta_archivo='audio/at_hey.mp3';
UPDATE CANCION SET ruta_archivo='audio/at_florecita.mp3' WHERE ruta_archivo='audio/at_mujergallo.mp3';

-- Silvestre Dangond (4 audios disponibles → asignación 1 a 1 para variedad)
UPDATE CANCION SET ruta_archivo='audio/sd_cantinero.mp3'     WHERE ruta_archivo='audio/sd_dolordecabeza.mp3';
UPDATE CANCION SET ruta_archivo='audio/sd_casate.mp3'        WHERE ruta_archivo='audio/sd_esamujer.mp3';
UPDATE CANCION SET ruta_archivo='audio/sd_justicia.mp3'      WHERE ruta_archivo='audio/sd_mediallamada.mp3';
UPDATE CANCION SET ruta_archivo='audio/sd_locoparanoico.mp3' WHERE ruta_archivo='audio/sd_niegame.mp3';

COMMIT;
