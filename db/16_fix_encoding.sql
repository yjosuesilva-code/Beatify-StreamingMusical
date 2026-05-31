-- ============================================================
-- 16_fix_encoding.sql
-- Corrige el doble-encoding UTF-8 en los datos demo.
-- Causa: el seed se cargó con sqlplus en codepage Windows (sin
--        NLS_LANG=.AL32UTF8), re-codificando los bytes UTF-8.
--        Ej: "ñ" (c3 b1) quedó como "Ã±" (c3 83 c2 b1).
--
-- Fix: CONVERT(col,'WE8ISO8859P1','AL32UTF8') deshace una capa.
-- Seguro e idempotente: solo toca filas que contienen 'Ã' (U+00C3),
-- patrón inequívoco del doble-encoding. Reejecutable sin daño.
-- ============================================================
SET DEFINE OFF;

UPDATE RESENA       SET comentario       = CONVERT(comentario,'WE8ISO8859P1','AL32UTF8')       WHERE INSTR(comentario, UNISTR('\00C3'))>0;
UPDATE ALBUM        SET titulo           = CONVERT(titulo,'WE8ISO8859P1','AL32UTF8')           WHERE INSTR(titulo, UNISTR('\00C3'))>0;
UPDATE GENERO       SET nombre           = CONVERT(nombre,'WE8ISO8859P1','AL32UTF8')           WHERE INSTR(nombre, UNISTR('\00C3'))>0;
UPDATE CANCION      SET titulo           = CONVERT(titulo,'WE8ISO8859P1','AL32UTF8')           WHERE INSTR(titulo, UNISTR('\00C3'))>0;
UPDATE CANCION      SET compositor       = CONVERT(compositor,'WE8ISO8859P1','AL32UTF8')       WHERE compositor IS NOT NULL AND INSTR(compositor, UNISTR('\00C3'))>0;
UPDATE CANCION      SET letra            = CONVERT(letra,'WE8ISO8859P1','AL32UTF8')            WHERE letra IS NOT NULL AND INSTR(letra, UNISTR('\00C3'))>0;
UPDATE ARTISTA      SET nombre_artistico = CONVERT(nombre_artistico,'WE8ISO8859P1','AL32UTF8') WHERE INSTR(nombre_artistico, UNISTR('\00C3'))>0;
UPDATE PLAYLIST     SET nombre           = CONVERT(nombre,'WE8ISO8859P1','AL32UTF8')           WHERE INSTR(nombre, UNISTR('\00C3'))>0;
UPDATE PLAYLIST     SET descripcion      = CONVERT(descripcion,'WE8ISO8859P1','AL32UTF8')      WHERE descripcion IS NOT NULL AND INSTR(descripcion, UNISTR('\00C3'))>0;
UPDATE NOTIFICACION SET titulo           = CONVERT(titulo,'WE8ISO8859P1','AL32UTF8')           WHERE INSTR(titulo, UNISTR('\00C3'))>0;
UPDATE NOTIFICACION SET mensaje          = CONVERT(mensaje,'WE8ISO8859P1','AL32UTF8')          WHERE INSTR(mensaje, UNISTR('\00C3'))>0;
UPDATE CLIENTE      SET ciudad           = CONVERT(ciudad,'WE8ISO8859P1','AL32UTF8')           WHERE ciudad IS NOT NULL AND INSTR(ciudad, UNISTR('\00C3'))>0;

COMMIT;
