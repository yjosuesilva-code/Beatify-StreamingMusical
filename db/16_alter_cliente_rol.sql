-- =====================================================================
-- 16_alter_cliente_rol.sql
-- Agrega el rol de cuenta a CLIENTE: 'CLIENTE' (default) o 'ADMIN'.
-- Crea (o promueve) una cuenta de administrador.
-- =====================================================================
-- Antes de esta migracion todos los usuarios eran clientes normales. Con
-- esta columna el login puede distinguir un administrador y llevarlo al
-- panel de administracion.
--
-- Las filas existentes quedan con rol='CLIENTE' por el DEFAULT.
--
-- Cuenta admin creada por este script:
--   correo:     admin@beatify.co
--   contrasena: Admin2026!
--
-- IMPORTANTE: DDL es commit implicito; no se puede hacer ROLLBACK del ALTER.
-- =====================================================================
SET DEFINE OFF;

ALTER TABLE CLIENTE
    ADD rol VARCHAR2(10 CHAR) DEFAULT 'CLIENTE' NOT NULL;

ALTER TABLE CLIENTE
    ADD CONSTRAINT CLIENTE_ROL_CK CHECK (rol IN ('CLIENTE', 'ADMIN'));

-- Indice para listados/filtros por rol (p.ej. WHERE rol = 'ADMIN')
CREATE INDEX IDX_CLIENTE_ROL ON CLIENTE (rol);

-- Cuenta admin idempotente: si no existe la crea; si existe la promueve.
DECLARE
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM CLIENTE WHERE correo = 'admin@beatify.co';
    IF v_count = 0 THEN
        INSERT INTO CLIENTE (
            id_cliente, nombre, apellido, correo, password_hash,
            telefono, direccion, ciudad, pais, fecha_registro, activo, rol
        ) VALUES (
            seq_cliente.NEXTVAL, 'Admin', 'Beatify', 'admin@beatify.co',
            '$2a$10$s3h6PWU0jTpNNN9fxo/8dugLowLqunP9q6ql/RKMosg3r7wisiT8.',
            NULL, NULL, 'Valledupar', 'Colombia', SYSDATE, 'S', 'ADMIN'
        );
    ELSE
        UPDATE CLIENTE SET rol = 'ADMIN' WHERE correo = 'admin@beatify.co';
    END IF;
END;
/

COMMIT;

-- =====================================================================
-- Verificacion:
--   SELECT id_cliente, correo, rol, activo FROM CLIENTE ORDER BY id_cliente;
--   Esperado: filas existentes rol='CLIENTE'; admin@beatify.co rol='ADMIN'.
-- =====================================================================
