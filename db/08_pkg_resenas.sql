-- =====================================================================
-- 08_pkg_resenas.sql
-- PKG_RESENAS — gestion de reseñas y votos.
-- =====================================================================
-- Operaciones:
--   CREAR_RESENA(cliente, tipo, id_obj, calif, comentario) -> id_resena
--   VOTAR_RESENA(resena, cliente, util ['S'|'N'])
--   CALCULAR_PROMEDIO(tipo, id_obj) -> NUMBER (0 si no hay reseñas)
--   CONTAR_RESENAS_OBJETIVO(tipo, id_obj) -> INTEGER
--
-- Nota: el trigger TRG_VALIDAR_RESENA_OBJETIVO ya valida que el objetivo
-- exista, asi que no duplicamos esa validacion aqui.
-- =====================================================================
SET DEFINE OFF;

CREATE OR REPLACE PACKAGE PKG_RESENAS IS

    PROCEDURE CREAR_RESENA(
        p_id_cliente    IN  INTEGER,
        p_tipo_objetivo IN  VARCHAR2,
        p_id_objetivo   IN  INTEGER,
        p_calificacion  IN  INTEGER,
        p_comentario    IN  VARCHAR2 DEFAULT NULL,
        p_id_resena     OUT INTEGER
    );

    PROCEDURE VOTAR_RESENA(
        p_id_resena  IN INTEGER,
        p_id_cliente IN INTEGER,
        p_util       IN CHAR
    );

    FUNCTION CALCULAR_PROMEDIO(
        p_tipo_objetivo IN VARCHAR2,
        p_id_objetivo   IN INTEGER
    ) RETURN NUMBER;

    FUNCTION CONTAR_RESENAS_OBJETIVO(
        p_tipo_objetivo IN VARCHAR2,
        p_id_objetivo   IN INTEGER
    ) RETURN INTEGER;

END PKG_RESENAS;
/

CREATE OR REPLACE PACKAGE BODY PKG_RESENAS IS

    PROCEDURE CREAR_RESENA(
        p_id_cliente    IN  INTEGER,
        p_tipo_objetivo IN  VARCHAR2,
        p_id_objetivo   IN  INTEGER,
        p_calificacion  IN  INTEGER,
        p_comentario    IN  VARCHAR2 DEFAULT NULL,
        p_id_resena     OUT INTEGER
    ) IS
BEGIN
        IF p_calificacion < 1 OR p_calificacion > 5 THEN
            RAISE_APPLICATION_ERROR(-20010,
                'La calificacion debe estar entre 1 y 5 (recibido: ' || p_calificacion || ')');
END IF;

INSERT INTO RESENA (
    id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo,
    calificacion, comentario, fecha_resena
) VALUES (
             seq_resena.NEXTVAL, p_id_cliente, UPPER(p_tipo_objetivo), p_id_objetivo,
             p_calificacion, p_comentario, SYSTIMESTAMP
         ) RETURNING id_resena INTO p_id_resena;
END CREAR_RESENA;

    PROCEDURE VOTAR_RESENA(
        p_id_resena  IN INTEGER,
        p_id_cliente IN INTEGER,
        p_util       IN CHAR
    ) IS
BEGIN
        IF UPPER(p_util) NOT IN ('S', 'N') THEN
            RAISE_APPLICATION_ERROR(-20011,
                'p_util debe ser S o N (recibido: "' || p_util || '")');
END IF;

INSERT INTO VOTO_RESENA (
    id_voto_resena, util, fecha_voto, RESENA_id_resena, CLIENTE_id_cliente
) VALUES (
             seq_voto_resena.NEXTVAL, UPPER(p_util), SYSTIMESTAMP, p_id_resena, p_id_cliente
         );
END VOTAR_RESENA;

    FUNCTION CALCULAR_PROMEDIO(
        p_tipo_objetivo IN VARCHAR2,
        p_id_objetivo   IN INTEGER
    ) RETURN NUMBER IS
        v_promedio NUMBER;
BEGIN
SELECT AVG(calificacion) INTO v_promedio
FROM RESENA
WHERE tipo_objetivo = UPPER(p_tipo_objetivo)
  AND id_objetivo = p_id_objetivo;
RETURN NVL(v_promedio, 0);
END CALCULAR_PROMEDIO;

    FUNCTION CONTAR_RESENAS_OBJETIVO(
        p_tipo_objetivo IN VARCHAR2,
        p_id_objetivo   IN INTEGER
    ) RETURN INTEGER IS
        v_count INTEGER;
BEGIN
SELECT COUNT(*) INTO v_count
FROM RESENA
WHERE tipo_objetivo = UPPER(p_tipo_objetivo)
  AND id_objetivo = p_id_objetivo;
RETURN v_count;
END CONTAR_RESENAS_OBJETIVO;

END PKG_RESENAS;
/