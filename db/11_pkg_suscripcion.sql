-- =====================================================================
-- 11_pkg_suscripcion.sql
-- PKG_SUSCRIPCION — gestion de planes y ciclo de vida.
-- =====================================================================
-- Operaciones:
--   ACTIVAR_SUSCRIPCION(cliente, tipo_plan, precio, meses) -> id_suscripcion
--     - Falla si el cliente ya tiene una suscripcion activa
--     - estado='ACTIVA', fecha_inicio=SYSDATE, fecha_fin=SYSDATE+meses*30
--   TIENE_SUSCRIPCION_ACTIVA(cliente) -> BOOLEAN
--     - estado='ACTIVA' AND (fecha_fin IS NULL OR fecha_fin > SYSDATE)
--   CANCELAR_SUSCRIPCION(cliente)
--     - UPDATE estado='CANCELADA'. Falla si no habia activa
--
-- Politica: una sola suscripcion activa por cliente. Si quiere cambiar
-- de plan, primero cancela y luego activa.
-- =====================================================================
SET DEFINE OFF;

CREATE OR REPLACE PACKAGE PKG_SUSCRIPCION IS

    PROCEDURE ACTIVAR_SUSCRIPCION(
        p_id_cliente     IN  INTEGER,
        p_tipo_plan      IN  VARCHAR2,
        p_precio         IN  NUMBER,
        p_meses          IN  INTEGER,
        p_id_suscripcion OUT INTEGER
    );

    FUNCTION TIENE_SUSCRIPCION_ACTIVA(p_id_cliente IN INTEGER) RETURN BOOLEAN;

    PROCEDURE CANCELAR_SUSCRIPCION(p_id_cliente IN INTEGER);

END PKG_SUSCRIPCION;
/

CREATE OR REPLACE PACKAGE BODY PKG_SUSCRIPCION IS

    PROCEDURE ACTIVAR_SUSCRIPCION(
        p_id_cliente     IN  INTEGER,
        p_tipo_plan      IN  VARCHAR2,
        p_precio         IN  NUMBER,
        p_meses          IN  INTEGER,
        p_id_suscripcion OUT INTEGER
    ) IS
    BEGIN
        IF p_meses IS NULL OR p_meses <= 0 THEN
            RAISE_APPLICATION_ERROR(-20030,
                'La duracion (meses) debe ser positiva (recibido: ' || p_meses || ')');
        END IF;

        IF p_precio IS NULL OR p_precio < 0 THEN
            RAISE_APPLICATION_ERROR(-20031,
                'El precio no puede ser negativo (recibido: ' || p_precio || ')');
        END IF;

        IF TIENE_SUSCRIPCION_ACTIVA(p_id_cliente) THEN
            RAISE_APPLICATION_ERROR(-20032,
                'El cliente ' || p_id_cliente || ' ya tiene una suscripcion activa');
        END IF;

        INSERT INTO SUSCRIPCION (
            id_suscripcion, tipo_plan, precio, fecha_inicio, fecha_fin, estado, CLIENTE_id_cliente
        ) VALUES (
            seq_suscripcion.NEXTVAL,
            UPPER(p_tipo_plan),
            p_precio,
            SYSDATE,
            SYSDATE + (p_meses * 30),
            'ACTIVA',
            p_id_cliente
        ) RETURNING id_suscripcion INTO p_id_suscripcion;
    END ACTIVAR_SUSCRIPCION;

    FUNCTION TIENE_SUSCRIPCION_ACTIVA(p_id_cliente IN INTEGER) RETURN BOOLEAN IS
        v_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM SUSCRIPCION
        WHERE CLIENTE_id_cliente = p_id_cliente
          AND estado = 'ACTIVA'
          AND (fecha_fin IS NULL OR fecha_fin > SYSDATE);
        RETURN v_count > 0;
    END TIENE_SUSCRIPCION_ACTIVA;

    PROCEDURE CANCELAR_SUSCRIPCION(p_id_cliente IN INTEGER) IS
    BEGIN
        UPDATE SUSCRIPCION
        SET estado = 'CANCELADA'
        WHERE CLIENTE_id_cliente = p_id_cliente
          AND estado = 'ACTIVA';

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20033,
                'El cliente ' || p_id_cliente || ' no tiene suscripcion activa');
        END IF;
    END CANCELAR_SUSCRIPCION;

END PKG_SUSCRIPCION;
/