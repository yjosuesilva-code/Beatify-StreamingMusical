-- =====================================================================
-- 10_pkg_cliente.sql
-- PKG_CLIENTE — gestion de clientes y soft-delete.
-- =====================================================================
-- Operaciones:
--   REGISTRAR_CLIENTE(...) -> id_cliente
--     - Valida correo unico antes del INSERT (anti-duplicado)
--     - Normaliza correo a LOWER
--     - activo='S' por defecto
--   EXISTE_CORREO(correo) -> BOOLEAN
--     - Case-insensitive
--   CONTAR_ACTIVIDAD(cliente) -> INTEGER
--     - Suma resenas + reproducciones + likes (3 tablas) del cliente
--   DESACTIVAR_CLIENTE(cliente)
--     - Soft-delete: activo='N'. ClienteService.autenticar lo rechaza
--
-- Nota: el password_hash llega ya hasheado (BCrypt) desde la capa Java.
-- Este paquete NO conoce de hashing.
-- =====================================================================
SET DEFINE OFF;

CREATE OR REPLACE PACKAGE PKG_CLIENTE IS

    PROCEDURE REGISTRAR_CLIENTE(
        p_nombre        IN  VARCHAR2,
        p_apellido      IN  VARCHAR2,
        p_correo        IN  VARCHAR2,
        p_password_hash IN  VARCHAR2,
        p_telefono      IN  VARCHAR2 DEFAULT NULL,
        p_direccion     IN  VARCHAR2 DEFAULT NULL,
        p_ciudad        IN  VARCHAR2 DEFAULT NULL,
        p_pais          IN  VARCHAR2 DEFAULT NULL,
        p_id_cliente    OUT INTEGER
    );

    FUNCTION EXISTE_CORREO(p_correo IN VARCHAR2) RETURN BOOLEAN;

    FUNCTION CONTAR_ACTIVIDAD(p_id_cliente IN INTEGER) RETURN INTEGER;

    PROCEDURE DESACTIVAR_CLIENTE(p_id_cliente IN INTEGER);

END PKG_CLIENTE;
/

CREATE OR REPLACE PACKAGE BODY PKG_CLIENTE IS

    PROCEDURE REGISTRAR_CLIENTE(
        p_nombre        IN  VARCHAR2,
        p_apellido      IN  VARCHAR2,
        p_correo        IN  VARCHAR2,
        p_password_hash IN  VARCHAR2,
        p_telefono      IN  VARCHAR2 DEFAULT NULL,
        p_direccion     IN  VARCHAR2 DEFAULT NULL,
        p_ciudad        IN  VARCHAR2 DEFAULT NULL,
        p_pais          IN  VARCHAR2 DEFAULT NULL,
        p_id_cliente    OUT INTEGER
    ) IS
    BEGIN
        IF EXISTE_CORREO(p_correo) THEN
            RAISE_APPLICATION_ERROR(-20020,
                'Ya existe un cliente con ese correo: ' || p_correo);
        END IF;

        INSERT INTO CLIENTE (
            id_cliente, nombre, apellido, correo, password_hash,
            telefono, direccion, ciudad, pais, fecha_registro, activo
        ) VALUES (
            seq_cliente.NEXTVAL, p_nombre, p_apellido, LOWER(p_correo), p_password_hash,
            p_telefono, p_direccion, p_ciudad, p_pais, SYSDATE, 'S'
        ) RETURNING id_cliente INTO p_id_cliente;
    END REGISTRAR_CLIENTE;

    FUNCTION EXISTE_CORREO(p_correo IN VARCHAR2) RETURN BOOLEAN IS
        v_count INTEGER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM CLIENTE
        WHERE LOWER(correo) = LOWER(p_correo);
        RETURN v_count > 0;
    END EXISTE_CORREO;

    FUNCTION CONTAR_ACTIVIDAD(p_id_cliente IN INTEGER) RETURN INTEGER IS
        v_resenas        INTEGER := 0;
        v_reproducciones INTEGER := 0;
        v_likes          INTEGER := 0;
    BEGIN
        SELECT COUNT(*) INTO v_resenas
        FROM RESENA WHERE CLIENTE_id_cliente = p_id_cliente;

        SELECT COUNT(*) INTO v_reproducciones
        FROM REPRODUCCION WHERE CLIENTE_id_cliente = p_id_cliente;

        SELECT (SELECT COUNT(*) FROM LIKE_CANCION  WHERE CLIENTE_id_cliente = p_id_cliente)
             + (SELECT COUNT(*) FROM LIKE_ALBUM    WHERE CLIENTE_id_cliente = p_id_cliente)
             + (SELECT COUNT(*) FROM LIKE_PLAYLIST WHERE CLIENTE_id_cliente = p_id_cliente)
        INTO v_likes FROM dual;

        RETURN v_resenas + v_reproducciones + v_likes;
    END CONTAR_ACTIVIDAD;

    PROCEDURE DESACTIVAR_CLIENTE(p_id_cliente IN INTEGER) IS
    BEGIN
        UPDATE CLIENTE SET activo = 'N' WHERE id_cliente = p_id_cliente;
        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(-20021,
                'No existe cliente con id=' || p_id_cliente);
        END IF;
    END DESACTIVAR_CLIENTE;

END PKG_CLIENTE;
/