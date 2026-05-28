-- =====================================================================
-- 09_alter_cliente_activo.sql
-- Soft-delete para CLIENTE: agrega columna 'activo' con CHECK ('S','N').
-- =====================================================================
-- Antes de esta migracion CLIENTE no tenia forma de "desactivar" un
-- usuario; solo eliminarlo en cascada. Con este campo, PKG_CLIENTE.
-- DESACTIVAR_CLIENTE puede marcar inactivo sin perder historial.
--
-- Las filas existentes quedan con activo='S' por el DEFAULT.
--
-- IMPORTANTE: DDL es commit implicito. Si ejecutas este script, no
-- puedes hacer ROLLBACK. Verifica primero.
-- =====================================================================
SET DEFINE OFF;

ALTER TABLE CLIENTE
    ADD activo CHAR(1 CHAR) DEFAULT 'S' NOT NULL;

ALTER TABLE CLIENTE
    ADD CONSTRAINT CLIENTE_ACTIVO_CK CHECK (activo IN ('S', 'N'));

-- Indice para futuras consultas WHERE activo = 'S' (logins, listados activos)
CREATE INDEX IDX_CLIENTE_ACTIVO ON CLIENTE (activo);

COMMIT;

-- =====================================================================
-- Verificacion:
-- =====================================================================
-- SELECT column_name, data_type, data_length, nullable, data_default
-- FROM user_tab_columns WHERE table_name='CLIENTE' AND column_name='ACTIVO';
--
-- SELECT id_cliente, correo, activo FROM CLIENTE;
-- Esperado: las 6 filas existentes con activo='S'