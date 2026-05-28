SET DEFINE OFF;

CREATE OR REPLACE PROCEDURE SP_RESET_DATOS_PRUEBA IS
    v_voto_resena       INTEGER := 0;
    v_resena            INTEGER := 0;
    v_like_cancion      INTEGER := 0;
    v_like_album        INTEGER := 0;
    v_like_playlist     INTEGER := 0;
    v_cancion_playlist  INTEGER := 0;
    v_playlist          INTEGER := 0;
    v_logro_cliente     INTEGER := 0;
    v_notificacion      INTEGER := 0;
    v_pago              INTEGER := 0;
    v_suscripcion       INTEGER := 0;
    v_reproduccion      INTEGER := 0;
    v_busqueda          INTEGER := 0;
    v_seguimiento       INTEGER := 0;
    v_dispositivo       INTEGER := 0;
    v_total             INTEGER := 0;
BEGIN
    -- Orden: las tablas que dependen de otras (FKs) van PRIMERO
DELETE FROM VOTO_RESENA;       v_voto_resena      := SQL%ROWCOUNT;
DELETE FROM RESENA;            v_resena           := SQL%ROWCOUNT;
DELETE FROM LIKE_CANCION;      v_like_cancion     := SQL%ROWCOUNT;
DELETE FROM LIKE_ALBUM;        v_like_album       := SQL%ROWCOUNT;
DELETE FROM LIKE_PLAYLIST;     v_like_playlist    := SQL%ROWCOUNT;
DELETE FROM CANCION_PLAYLIST;  v_cancion_playlist := SQL%ROWCOUNT;
DELETE FROM PLAYLIST;          v_playlist         := SQL%ROWCOUNT;
DELETE FROM LOGRO_CLIENTE;     v_logro_cliente    := SQL%ROWCOUNT;
DELETE FROM NOTIFICACION;      v_notificacion     := SQL%ROWCOUNT;
DELETE FROM PAGO;              v_pago             := SQL%ROWCOUNT;
DELETE FROM SUSCRIPCION;       v_suscripcion      := SQL%ROWCOUNT;
DELETE FROM REPRODUCCION;      v_reproduccion     := SQL%ROWCOUNT;
DELETE FROM BUSQUEDA;          v_busqueda         := SQL%ROWCOUNT;
DELETE FROM SEGUIMIENTO;       v_seguimiento      := SQL%ROWCOUNT;
DELETE FROM DISPOSITIVO;       v_dispositivo      := SQL%ROWCOUNT;

    v_total := v_voto_resena + v_resena + v_like_cancion + v_like_album
             + v_like_playlist + v_cancion_playlist + v_playlist
             + v_logro_cliente + v_notificacion + v_pago + v_suscripcion
             + v_reproduccion + v_busqueda + v_seguimiento + v_dispositivo;

    DBMS_OUTPUT.PUT_LINE('Datos de prueba eliminados (catalogo y CLIENTE conservados):');
    DBMS_OUTPUT.PUT_LINE('  VOTO_RESENA:       ' || v_voto_resena);
    DBMS_OUTPUT.PUT_LINE('  RESENA:            ' || v_resena);
    DBMS_OUTPUT.PUT_LINE('  LIKE_CANCION:      ' || v_like_cancion);
    DBMS_OUTPUT.PUT_LINE('  LIKE_ALBUM:        ' || v_like_album);
    DBMS_OUTPUT.PUT_LINE('  LIKE_PLAYLIST:     ' || v_like_playlist);
    DBMS_OUTPUT.PUT_LINE('  CANCION_PLAYLIST:  ' || v_cancion_playlist);
    DBMS_OUTPUT.PUT_LINE('  PLAYLIST:          ' || v_playlist);
    DBMS_OUTPUT.PUT_LINE('  LOGRO_CLIENTE:     ' || v_logro_cliente);
    DBMS_OUTPUT.PUT_LINE('  NOTIFICACION:      ' || v_notificacion);
    DBMS_OUTPUT.PUT_LINE('  PAGO:              ' || v_pago);
    DBMS_OUTPUT.PUT_LINE('  SUSCRIPCION:       ' || v_suscripcion);
    DBMS_OUTPUT.PUT_LINE('  REPRODUCCION:      ' || v_reproduccion);
    DBMS_OUTPUT.PUT_LINE('  BUSQUEDA:          ' || v_busqueda);
    DBMS_OUTPUT.PUT_LINE('  SEGUIMIENTO:       ' || v_seguimiento);
    DBMS_OUTPUT.PUT_LINE('  DISPOSITIVO:       ' || v_dispositivo);
    DBMS_OUTPUT.PUT_LINE('  TOTAL:             ' || v_total);
END SP_RESET_DATOS_PRUEBA;
/