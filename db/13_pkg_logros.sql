-- =====================================================================
-- 13_pkg_logros.sql
-- PKG_LOGROS — gamificacion: otorgar y evaluar logros automaticamente.
-- =====================================================================
-- Operaciones:
--   OTORGAR_LOGRO(cliente, id_logro)
--     - Idempotente: si ya lo tiene (UK), no falla
--   EVALUAR_LOGROS_AUTO(cliente)
--     - Reglas hardcodeadas contra los 6 logros del seed
--     - Cada regla evalua una condicion y otorga si cumple
--     - Reporta total otorgado por DBMS_OUTPUT
--   CONTAR_LOGROS_CLIENTE(cliente) -> INTEGER
--
-- Reglas (basadas en codigos del seed):
--   PRIMER_LIKE     → cliente tiene ≥ 1 like (cualquiera de las 3 tablas)
--   CRITICO         → cliente tiene ≥ 5 resenas
--   COLECCIONISTA   → cliente tiene ≥ 3 playlists
--   NOCTAMBULO      → cliente tiene ≥ 3 reproducciones entre 00:00-05:00
--   EXP_CARIBE      → cliente ha reproducido canciones de ≥ 3 artistas
--                     distintos cuyo pais='Colombia'
--   FAN_VALLENATO   → cliente tiene ≥ 5 reproducciones de canciones de
--                     genero 'Vallenato'
-- =====================================================================
SET DEFINE OFF;

CREATE OR REPLACE PACKAGE PKG_LOGROS IS

    PROCEDURE OTORGAR_LOGRO(p_id_cliente IN INTEGER, p_id_logro IN INTEGER);

    PROCEDURE EVALUAR_LOGROS_AUTO(p_id_cliente IN INTEGER);

    FUNCTION CONTAR_LOGROS_CLIENTE(p_id_cliente IN INTEGER) RETURN INTEGER;

END PKG_LOGROS;
/

CREATE OR REPLACE PACKAGE BODY PKG_LOGROS IS

    -- ============================================================
    -- Funcion privada: resuelve id_logro a partir de su codigo
    -- ============================================================
    FUNCTION ID_LOGRO_POR_CODIGO(p_codigo IN VARCHAR2) RETURN INTEGER IS
        v_id INTEGER;
    BEGIN
        SELECT id_logro INTO v_id FROM LOGRO WHERE codigo = UPPER(p_codigo);
        RETURN v_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
    END ID_LOGRO_POR_CODIGO;

    -- ============================================================
    -- Procedure privado: otorga por codigo (resuelve id internamente)
    -- ============================================================
    PROCEDURE OTORGAR_POR_CODIGO(p_id_cliente IN INTEGER, p_codigo IN VARCHAR2) IS
        v_id_logro INTEGER;
    BEGIN
        v_id_logro := ID_LOGRO_POR_CODIGO(p_codigo);
        IF v_id_logro IS NOT NULL THEN
            OTORGAR_LOGRO(p_id_cliente, v_id_logro);
        END IF;
    END OTORGAR_POR_CODIGO;

    -- ============================================================
    -- PUBLICO: OTORGAR_LOGRO
    -- Idempotente: si el UK rechaza el duplicado, no falla.
    -- ============================================================
    PROCEDURE OTORGAR_LOGRO(p_id_cliente IN INTEGER, p_id_logro IN INTEGER) IS
    BEGIN
        INSERT INTO LOGRO_CLIENTE (
            id_logro_cliente, fecha_obtencion, CLIENTE_id_cliente, LOGRO_id_logro
        ) VALUES (
            seq_logro_cliente.NEXTVAL, SYSTIMESTAMP, p_id_cliente, p_id_logro
        );
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            NULL;  -- ya lo tenia: idempotente
    END OTORGAR_LOGRO;

    -- ============================================================
    -- PUBLICO: EVALUAR_LOGROS_AUTO
    -- Aplica las 6 reglas hardcodeadas.
    -- ============================================================
    PROCEDURE EVALUAR_LOGROS_AUTO(p_id_cliente IN INTEGER) IS
        v_count INTEGER;
        v_logros_antes INTEGER;
        v_logros_despues INTEGER;
    BEGIN
        v_logros_antes := CONTAR_LOGROS_CLIENTE(p_id_cliente);

        -- Regla 1: PRIMER_LIKE → ≥ 1 like (cualquier tipo)
        SELECT (SELECT COUNT(*) FROM LIKE_CANCION  WHERE CLIENTE_id_cliente = p_id_cliente)
             + (SELECT COUNT(*) FROM LIKE_ALBUM    WHERE CLIENTE_id_cliente = p_id_cliente)
             + (SELECT COUNT(*) FROM LIKE_PLAYLIST WHERE CLIENTE_id_cliente = p_id_cliente)
        INTO v_count FROM dual;
        IF v_count >= 1 THEN
            OTORGAR_POR_CODIGO(p_id_cliente, 'PRIMER_LIKE');
        END IF;

        -- Regla 2: CRITICO → ≥ 5 resenas
        SELECT COUNT(*) INTO v_count
        FROM RESENA WHERE CLIENTE_id_cliente = p_id_cliente;
        IF v_count >= 5 THEN
            OTORGAR_POR_CODIGO(p_id_cliente, 'CRITICO');
        END IF;

        -- Regla 3: COLECCIONISTA → ≥ 3 playlists
        SELECT COUNT(*) INTO v_count
        FROM PLAYLIST WHERE CLIENTE_id_cliente = p_id_cliente;
        IF v_count >= 3 THEN
            OTORGAR_POR_CODIGO(p_id_cliente, 'COLECCIONISTA');
        END IF;

        -- Regla 4: NOCTAMBULO → ≥ 3 reproducciones entre 00:00 y 05:00
        SELECT COUNT(*) INTO v_count
        FROM REPRODUCCION
        WHERE CLIENTE_id_cliente = p_id_cliente
          AND EXTRACT(HOUR FROM fecha_hora) BETWEEN 0 AND 4;
        IF v_count >= 3 THEN
            OTORGAR_POR_CODIGO(p_id_cliente, 'NOCTAMBULO');
        END IF;

        -- Regla 5: EXP_CARIBE → ≥ 3 artistas distintos colombianos reproducidos
        SELECT COUNT(DISTINCT al.ARTISTA_id_artista) INTO v_count
        FROM REPRODUCCION r
        JOIN CANCION  c  ON c.id_cancion = r.CANCION_id_cancion
        JOIN ALBUM    al ON al.id_album  = c.ALBUM_id_album
        JOIN ARTISTA  a  ON a.id_artista = al.ARTISTA_id_artista
        WHERE r.CLIENTE_id_cliente = p_id_cliente
          AND UPPER(a.pais) = 'COLOMBIA';
        IF v_count >= 3 THEN
            OTORGAR_POR_CODIGO(p_id_cliente, 'EXP_CARIBE');
        END IF;

        -- Regla 6: FAN_VALLENATO → ≥ 5 reproducciones de genero 'Vallenato'
        SELECT COUNT(*) INTO v_count
        FROM REPRODUCCION    r
        JOIN CANCION         c  ON c.id_cancion = r.CANCION_id_cancion
        JOIN ALBUM           al ON al.id_album  = c.ALBUM_id_album
        JOIN ARTISTA_GENERO  ag ON ag.ARTISTA_id_artista = al.ARTISTA_id_artista
        JOIN GENERO          g  ON g.id_genero = ag.GENERO_id_genero
        WHERE r.CLIENTE_id_cliente = p_id_cliente
          AND UPPER(g.nombre) = 'VALLENATO';
        IF v_count >= 5 THEN
            OTORGAR_POR_CODIGO(p_id_cliente, 'FAN_VALLENATO');
        END IF;

        v_logros_despues := CONTAR_LOGROS_CLIENTE(p_id_cliente);

        DBMS_OUTPUT.PUT_LINE('Cliente=' || p_id_cliente
            || ' logros antes=' || v_logros_antes
            || ' despues=' || v_logros_despues
            || ' (nuevos: ' || (v_logros_despues - v_logros_antes) || ')');
    END EVALUAR_LOGROS_AUTO;

    -- ============================================================
    -- PUBLICO: CONTAR_LOGROS_CLIENTE
    -- ============================================================
    FUNCTION CONTAR_LOGROS_CLIENTE(p_id_cliente IN INTEGER) RETURN INTEGER IS
        v_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM LOGRO_CLIENTE WHERE CLIENTE_id_cliente = p_id_cliente;
        RETURN v_count;
    END CONTAR_LOGROS_CLIENTE;

END PKG_LOGROS;
/