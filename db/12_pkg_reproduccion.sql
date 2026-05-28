-- =====================================================================
-- 12_pkg_reproduccion.sql
-- PKG_REPRODUCCION — registro y analitica basica de reproducciones.
-- =====================================================================
-- Operaciones:
--   REGISTRAR_REPRODUCCION(cliente, cancion, duracion_seg) -> id
--     - INSERT con fecha_hora=SYSTIMESTAMP, duracion_escuchada=opcional
--   TOTAL_REPRODUCCIONES_CLIENTE(cliente) -> INTEGER
--   TOP_GENERO_CLIENTE(cliente) -> VARCHAR2
--     - JOIN REPRODUCCION->CANCION->ALBUM->ARTISTA_GENERO->GENERO
--     - GROUP BY nombre del genero, ORDER BY count DESC, FETCH FIRST 1
--     - NULL si el cliente no tiene reproducciones o los artistas no tienen
--       generos vinculados
-- =====================================================================
SET DEFINE OFF;

CREATE OR REPLACE PACKAGE PKG_REPRODUCCION IS

    PROCEDURE REGISTRAR_REPRODUCCION(
        p_id_cliente        IN  INTEGER,
        p_id_cancion        IN  INTEGER,
        p_duracion_segundos IN  INTEGER DEFAULT NULL,
        p_id_reproduccion   OUT INTEGER
    );

    FUNCTION TOTAL_REPRODUCCIONES_CLIENTE(p_id_cliente IN INTEGER) RETURN INTEGER;

    FUNCTION TOP_GENERO_CLIENTE(p_id_cliente IN INTEGER) RETURN VARCHAR2;

END PKG_REPRODUCCION;
/

CREATE OR REPLACE PACKAGE BODY PKG_REPRODUCCION IS

    PROCEDURE REGISTRAR_REPRODUCCION(
        p_id_cliente        IN  INTEGER,
        p_id_cancion        IN  INTEGER,
        p_duracion_segundos IN  INTEGER DEFAULT NULL,
        p_id_reproduccion   OUT INTEGER
    ) IS
    BEGIN
        IF p_duracion_segundos IS NOT NULL AND p_duracion_segundos < 0 THEN
            RAISE_APPLICATION_ERROR(-20040,
                'La duracion no puede ser negativa (recibido: ' || p_duracion_segundos || ')');
        END IF;

        INSERT INTO REPRODUCCION (
            id_reproduccion, fecha_hora, duracion_escuchada,
            CLIENTE_id_cliente, CANCION_id_cancion
        ) VALUES (
            seq_reproduccion.NEXTVAL, SYSTIMESTAMP, p_duracion_segundos,
            p_id_cliente, p_id_cancion
        ) RETURNING id_reproduccion INTO p_id_reproduccion;
    END REGISTRAR_REPRODUCCION;

    FUNCTION TOTAL_REPRODUCCIONES_CLIENTE(p_id_cliente IN INTEGER) RETURN INTEGER IS
        v_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM REPRODUCCION
        WHERE CLIENTE_id_cliente = p_id_cliente;
        RETURN v_count;
    END TOTAL_REPRODUCCIONES_CLIENTE;

    FUNCTION TOP_GENERO_CLIENTE(p_id_cliente IN INTEGER) RETURN VARCHAR2 IS
        v_top VARCHAR2(25);
    BEGIN
        SELECT g.nombre INTO v_top
        FROM REPRODUCCION    r
        JOIN CANCION         c  ON c.id_cancion       = r.CANCION_id_cancion
        JOIN ALBUM           al ON al.id_album         = c.ALBUM_id_album
        JOIN ARTISTA_GENERO  ag ON ag.ARTISTA_id_artista = al.ARTISTA_id_artista
        JOIN GENERO          g  ON g.id_genero         = ag.GENERO_id_genero
        WHERE r.CLIENTE_id_cliente = p_id_cliente
        GROUP BY g.nombre
        ORDER BY COUNT(*) DESC
        FETCH FIRST 1 ROWS ONLY;
        RETURN v_top;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END TOP_GENERO_CLIENTE;

END PKG_REPRODUCCION;
/