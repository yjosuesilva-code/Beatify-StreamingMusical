SET DEFINE OFF;

CREATE OR REPLACE PROCEDURE SP_LIMPIAR_CACHE_VENCIDO IS
    v_mb_borrados      INTEGER := 0;
    v_lfm_art_borrados INTEGER := 0;
    v_lfm_alb_borrados INTEGER := 0;
BEGIN
DELETE FROM CACHE_MUSICBRAINZ_ARTISTA WHERE expires_at < SYSDATE;
v_mb_borrados := SQL%ROWCOUNT;

DELETE FROM CACHE_LASTFM_ARTISTA WHERE expires_at < SYSDATE;
v_lfm_art_borrados := SQL%ROWCOUNT;

DELETE FROM CACHE_LASTFM_ALBUM WHERE expires_at < SYSDATE;
v_lfm_alb_borrados := SQL%ROWCOUNT;

    DBMS_OUTPUT.PUT_LINE('Cache vencido eliminado:');
    DBMS_OUTPUT.PUT_LINE('  CACHE_MUSICBRAINZ_ARTISTA: ' || v_mb_borrados || ' filas');
    DBMS_OUTPUT.PUT_LINE('  CACHE_LASTFM_ARTISTA:      ' || v_lfm_art_borrados || ' filas');
    DBMS_OUTPUT.PUT_LINE('  CACHE_LASTFM_ALBUM:        ' || v_lfm_alb_borrados || ' filas');
    DBMS_OUTPUT.PUT_LINE('  TOTAL:                     ' || (v_mb_borrados + v_lfm_art_borrados + v_lfm_alb_borrados));
END SP_LIMPIAR_CACHE_VENCIDO;
/

