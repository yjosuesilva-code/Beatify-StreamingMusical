SET DEFINE OFF;

CREATE OR REPLACE FUNCTION FN_VERIFICAR_OBJETIVO_EXISTE(
    p_tipo IN VARCHAR2,
    p_id   IN INTEGER
) RETURN BOOLEAN IS
    v_count INTEGER := 0;
BEGIN
CASE UPPER(p_tipo)
        WHEN 'CANCION' THEN
SELECT COUNT(*) INTO v_count FROM CANCION WHERE id_cancion = p_id;
WHEN 'ALBUM' THEN
SELECT COUNT(*) INTO v_count FROM ALBUM   WHERE id_album = p_id;
WHEN 'ARTISTA' THEN
SELECT COUNT(*) INTO v_count FROM ARTISTA WHERE id_artista = p_id;
ELSE
            RETURN FALSE;
END CASE;
RETURN v_count > 0;
END;
/

CREATE OR REPLACE TRIGGER TRG_VALIDAR_RESENA_OBJETIVO
BEFORE INSERT OR UPDATE OF tipo_objetivo, id_objetivo ON RESENA
    FOR EACH ROW
BEGIN
    IF NOT FN_VERIFICAR_OBJETIVO_EXISTE(:NEW.tipo_objetivo, :NEW.id_objetivo) THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'No existe ' || :NEW.tipo_objetivo
            || ' con id=' || :NEW.id_objetivo
            || '. Verifica que la fila exista en la tabla correspondiente.'
        );
END IF;
END;
/

COMMIT;