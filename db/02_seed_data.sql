-- =====================================================================
-- 02_seed_data.sql
-- Datos de prueba para Beatify
-- =====================================================================
-- IMPORTANTE: Ejecutar conectado como BEATIFY @ XEPDB1
--             DESPUÉS de 01_schema_beatify.sql sobre un schema limpio.
--             Las referencias entre tablas usan IDs literales asumiendo
--             que las secuencias arrancan en 1.
--
-- Para volver a correrlo, primero limpiar con:
--   BEGIN
--     FOR t IN (SELECT table_name FROM user_tables) LOOP
--       EXECUTE IMMEDIATE 'DELETE FROM ' || t.table_name;
--     END LOOP;
--     FOR s IN (SELECT sequence_name FROM user_sequences) LOOP
--       EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
--       EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || s.sequence_name ||
--                         ' START WITH 1 INCREMENT BY 1 NOCACHE';
--     END LOOP;
--     COMMIT;
--   END;
--   /
-- =====================================================================

SET DEFINE OFF;

-- =====================================================================
-- 1. GENERO (7)  — IDs 1..7
-- =====================================================================
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais) VALUES (seq_genero.NEXTVAL, 'Vallenato',     'Musica tradicional del Caribe colombiano', 'Colombia');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais) VALUES (seq_genero.NEXTVAL, 'Cumbia',        'Ritmo afrolatino tradicional',            'Colombia');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais) VALUES (seq_genero.NEXTVAL, 'Champeta',      'Genero urbano del Caribe colombiano',     'Colombia');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais) VALUES (seq_genero.NEXTVAL, 'Salsa',         'Musica latina bailable',                  'Cuba');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais) VALUES (seq_genero.NEXTVAL, 'Reggaeton',     'Musica urbana latina',                    'Puerto Rico');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais) VALUES (seq_genero.NEXTVAL, 'Rock',          'Musica rock contemporanea',               'Estados Unidos');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais) VALUES (seq_genero.NEXTVAL, 'Pop Latino',    'Pop en espanol',                          'Latinoamerica');

-- =====================================================================
-- 2. ARTISTA (10)  — IDs 1..10
-- =====================================================================
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Carlos',     'Vives',       'Carlos Vives',     DATE '1961-08-07', 'Colombia',     NULL, 'Cantante y actor colombiano, icono del vallenato fusion.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Diomedes',   'Diaz',        'El Cacique',       DATE '1957-05-26', 'Colombia',     NULL, 'Cantante de vallenato mas popular de la historia colombiana.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Shakira',    'Mebarak',     'Shakira',          DATE '1977-02-02', 'Colombia',     NULL, 'Cantautora barranquillera con proyeccion internacional.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Joe',        'Arroyo',      'Joe Arroyo',       DATE '1955-11-01', 'Colombia',     NULL, 'Cantante y compositor de salsa y musica tropical caribena.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Silvestre',  'Dangond',     'Silvestre',        DATE '1980-05-12', 'Colombia',     NULL, 'Cantante de vallenato moderno, referente de la nueva generacion.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Liliana',    'Saumet',      'Bomba Estereo',    DATE '1979-04-15', 'Colombia',     NULL, 'Voz principal de Bomba Estereo, banda de electro-tropical.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Andrea',     'Echeverri',   'Aterciopelados',   DATE '1965-09-10', 'Colombia',     NULL, 'Banda de rock alternativo bogotana, pioneros del rock en espanol.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Juan',       'Aristizabal', 'Juanes',           DATE '1972-08-09', 'Colombia',     NULL, 'Cantautor de rock latino, multiple ganador del Grammy.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Hansel',     'Camacho',     'Fonseca',          DATE '1979-06-29', 'Colombia',     NULL, 'Cantante de pop y musica tropical contemporanea.', NULL);
INSERT INTO ARTISTA (id_artista, nombre, apellido, nombre_artistico, fecha_nacimiento, pais, correo, biografia, foto_url) VALUES
  (seq_artista.NEXTVAL, 'Carolina',   'Giraldo',     'Karol G',          DATE '1991-02-14', 'Colombia',     NULL, 'Cantante de reggaeton, referente del urbano femenino latino.', NULL);

-- =====================================================================
-- 3. ARTISTA_GENERO (12)  — Relacion M:N
-- =====================================================================
-- Carlos Vives -> Vallenato + Pop Latino
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (1, 1);
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (1, 7);
-- Diomedes Diaz -> Vallenato
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (2, 1);
-- Shakira -> Pop Latino + Rock
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (3, 7);
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (3, 6);
-- Joe Arroyo -> Salsa + Cumbia
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (4, 4);
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (4, 2);
-- Silvestre -> Vallenato
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (5, 1);
-- Bomba Estereo -> Cumbia + Pop Latino
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (6, 2);
-- Aterciopelados -> Rock
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (7, 6);
-- Juanes -> Rock + Pop Latino
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (8, 6);
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (8, 7);
-- Karol G -> Reggaeton
INSERT INTO ARTISTA_GENERO (ARTISTA_id_artista, GENERO_id_genero) VALUES (10, 5);

-- =====================================================================
-- 4. ALBUM (15)  — IDs 1..15
-- =====================================================================
-- Carlos Vives (artista 1)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Clasicos de la Provincia', 1993, 'Sony Music',  'ALBUM', NULL, 'Album que fusiono vallenato con rock.',  1);
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'El Amor de Mi Tierra',     1999, 'EMI Latin',   'ALBUM', NULL, 'Continuacion del estilo vallenato pop.', 1);
-- Diomedes Diaz (artista 2)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Mi Biografia',             1988, 'CBS',         'ALBUM', NULL, 'Album clasico del Cacique.', 2);
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Tu Eres La Reina',         1990, 'CBS',         'ALBUM', NULL, 'Album de exitos romanticos.', 2);
-- Shakira (artista 3)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Pies Descalzos',           1995, 'Sony Music',  'ALBUM', NULL, 'Album debut internacional.', 3);
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Donde Estan los Ladrones', 1998, 'Sony Music',  'ALBUM', NULL, 'Album que consolido su carrera.', 3);
-- Joe Arroyo (artista 4)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Fuego en Mi Mente',        1988, 'Discos Fuent','ALBUM', NULL, 'Album clasico de salsa caribena.', 4);
-- Silvestre Dangond (artista 5)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Sigo Invicto',             2014, 'Sony Music',  'ALBUM', NULL, 'Album de vallenato moderno.', 5);
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Las Locuras Mias',         2018, 'Sony Music',  'ALBUM', NULL, 'Disco con colaboraciones.', 5);
-- Bomba Estereo (artista 6)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Elegancia Tropical',       2012, 'Polen',       'ALBUM', NULL, 'Electro-tropical desde Bogota.', 6);
-- Aterciopelados (artista 7)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'La Pipa de la Paz',        1996, 'BMG',         'ALBUM', NULL, 'Album clasico del rock alternativo bogotano.', 7);
-- Juanes (artista 8)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Un Dia Normal',            2002, 'Universal',   'ALBUM', NULL, 'Album que catapulto su carrera global.', 8);
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Mi Sangre',                2004, 'Universal',   'ALBUM', NULL, 'Disco con La Camisa Negra.', 8);
-- Fonseca (artista 9)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'Corazon',                  2005, 'EMI',         'ALBUM', NULL, 'Album debut con vallenato pop.', 9);
-- Karol G (artista 10)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, sello_discografico, tipo, portada_url, descripcion, ARTISTA_id_artista) VALUES
  (seq_album.NEXTVAL, 'KG0516',                   2021, 'Universal',   'ALBUM', NULL, 'Album consolidado de reggaeton femenino.', 10);

-- =====================================================================
-- 5. CANCION (30)  — IDs 1..30
-- Géneros: 1=Vallenato 2=Cumbia 3=Champeta 4=Salsa 5=Reggaeton 6=Rock 7=Pop Latino
-- =====================================================================
-- Album 1: Clasicos de la Provincia (Carlos Vives, Vallenato)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'La Gota Fria',            213, 'audio/cv_lagotafria.mp3',     NULL, 'Emiliano Zuleta', DATE '1993-09-15', 1, 1);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'La Tierra del Olvido',    234, 'audio/cv_tierraolvido.mp3',   NULL, 'Carlos Vives',    DATE '1993-09-15', 1, 1);
-- Album 2: El Amor de Mi Tierra (Carlos Vives, Vallenato)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Fruta Fresca',            245, 'audio/cv_frutafresca.mp3',    NULL, 'Carlos Vives',    DATE '1999-07-20', 2, 1);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Decisiones',              228, 'audio/cv_decisiones.mp3',     NULL, 'Carlos Vives',    DATE '1999-07-20', 2, 1);
-- Album 3: Mi Biografia (Diomedes Diaz, Vallenato)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Mi Muchacho',             276, 'audio/dd_mimuchacho.mp3',     NULL, 'Diomedes Diaz',   DATE '1988-04-10', 3, 1);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Cancion de Cuna',         298, 'audio/dd_canciondecuna.mp3',  NULL, 'Diomedes Diaz',   DATE '1988-04-10', 3, 1);
-- Album 4: Tu Eres La Reina (Diomedes Diaz, Vallenato)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Tu Eres La Reina',        256, 'audio/dd_eresreina.mp3',      NULL, 'Diomedes Diaz',   DATE '1990-06-01', 4, 1);
-- Album 5: Pies Descalzos (Shakira, Rock)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Estoy Aqui',              232, 'audio/sk_estoyaqui.mp3',      NULL, 'Shakira',         DATE '1995-11-15', 5, 6);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Pies Descalzos',          218, 'audio/sk_piesdescalzos.mp3',  NULL, 'Shakira',         DATE '1995-11-15', 5, 6);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Antologia',               239, 'audio/sk_antologia.mp3',      NULL, 'Shakira',         DATE '1995-11-15', 5, 7);
-- Album 6: Donde Estan los Ladrones (Shakira, Rock)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Ciega Sordomuda',         193, 'audio/sk_ciega.mp3',          NULL, 'Shakira',         DATE '1998-09-29', 6, 6);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Ojos Asi',                235, 'audio/sk_ojosasi.mp3',        NULL, 'Shakira',         DATE '1998-09-29', 6, 7);
-- Album 7: Fuego en Mi Mente (Joe Arroyo, Salsa)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'La Rebelion',             274, 'audio/ja_larebelion.mp3',     NULL, 'Joe Arroyo',      DATE '1988-08-15', 7, 4);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'En Barranquilla Me Quedo', 268, 'audio/ja_barranquilla.mp3',  NULL, 'Joe Arroyo',      DATE '1988-08-15', 7, 4);
-- Album 8: Sigo Invicto (Silvestre, Vallenato)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Cantinero',               241, 'audio/sd_cantinero.mp3',      NULL, 'Silvestre',       DATE '2014-10-30', 8, 1);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Necio Corazon',           228, 'audio/sd_necio.mp3',          NULL, 'Silvestre',       DATE '2014-10-30', 8, 1);
-- Album 9: Las Locuras Mias (Silvestre, Vallenato)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Justicia',                212, 'audio/sd_justicia.mp3',       NULL, 'Silvestre',       DATE '2018-03-23', 9, 1);
-- Album 10: Elegancia Tropical (Bomba Estereo, Cumbia)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'To My Love',              194, 'audio/be_tomylove.mp3',       NULL, 'Bomba Estereo',   DATE '2012-11-09', 10, 2);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Soy Yo',                  202, 'audio/be_soyyo.mp3',          NULL, 'Bomba Estereo',   DATE '2012-11-09', 10, 7);
-- Album 11: La Pipa de la Paz (Aterciopelados, Rock)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Bolero Falaz',            218, 'audio/at_bolero.mp3',         NULL, 'Aterciopelados',  DATE '1996-05-14', 11, 6);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Florecita Rockera',       181, 'audio/at_florecita.mp3',      NULL, 'Aterciopelados',  DATE '1996-05-14', 11, 6);
-- Album 12: Un Dia Normal (Juanes, Rock)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'A Dios Le Pido',          227, 'audio/jn_adiosele.mp3',       NULL, 'Juanes',          DATE '2002-05-21', 12, 6);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Es Por Ti',               210, 'audio/jn_esporti.mp3',        NULL, 'Juanes',          DATE '2002-05-21', 12, 6);
-- Album 13: Mi Sangre (Juanes, Rock)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'La Camisa Negra',         215, 'audio/jn_camisanegra.mp3',    NULL, 'Juanes',          DATE '2004-09-28', 13, 6);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Volverte a Ver',          204, 'audio/jn_volverte.mp3',       NULL, 'Juanes',          DATE '2004-09-28', 13, 7);
-- Album 14: Corazon (Fonseca, Pop Latino)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Te Mando Flores',         223, 'audio/fn_temandoflores.mp3',  NULL, 'Fonseca',         DATE '2005-10-01', 14, 7);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Hace Tiempo',             247, 'audio/fn_hacetiempo.mp3',     NULL, 'Fonseca',         DATE '2005-10-01', 14, 7);
-- Album 15: KG0516 (Karol G, Reggaeton)
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Bichota',                 165, 'audio/kg_bichota.mp3',        NULL, 'Karol G',         DATE '2021-03-26', 15, 5);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Tusa',                    200, 'audio/kg_tusa.mp3',           NULL, 'Karol G',         DATE '2021-03-26', 15, 5);
INSERT INTO CANCION (id_cancion, titulo, duracion_seg, ruta_archivo, letra, compositor, fecha_lanzamiento, ALBUM_id_album, GENERO_id_genero) VALUES (seq_cancion.NEXTVAL, 'Provenza',                212, 'audio/kg_provenza.mp3',       NULL, 'Karol G',         DATE '2021-03-26', 15, 5);

-- =====================================================================
-- 6. CLIENTE (5)  — IDs 1..5
-- Passwords BCrypt cost=10:
--   yilver_test, andres_test, kendrick_test => 'Beatify123!'
--   demo_user1, demo_user2                  => 'Demo2026!'
-- =====================================================================
INSERT INTO CLIENTE (id_cliente, nombre,    apellido,    correo,                        password_hash,                                                  telefono,      direccion,        ciudad,       pais,        fecha_registro) VALUES
  (seq_cliente.NEXTVAL, 'Yilver',  'Silva',     'yilver.test@unicesar.edu.co',  '$2b$10$opIAjfKM1yTQF1HmtYK93eFJpZe4KU4ihOcm2y6xVdTA3ee433GUe', '3001234567',  'Cra 19 # 9-50',  'Valledupar', 'Colombia',  DATE '2026-04-15');
INSERT INTO CLIENTE (id_cliente, nombre,    apellido,    correo,                        password_hash,                                                  telefono,      direccion,        ciudad,       pais,        fecha_registro) VALUES
  (seq_cliente.NEXTVAL, 'Andres',  'Zabaleta',  'andres.test@unicesar.edu.co',  '$2b$10$PaIrDygPr4Sw6xwdG0Nv4OrtvavDi4dPoC1eRRUAZfJ9iI0xLw7LK', '3002345678',  'Calle 16 # 9-10','Valledupar', 'Colombia',  DATE '2026-04-15');
INSERT INTO CLIENTE (id_cliente, nombre,    apellido,    correo,                        password_hash,                                                  telefono,      direccion,        ciudad,       pais,        fecha_registro) VALUES
  (seq_cliente.NEXTVAL, 'Kendrick','Sayago',    'kendrick.test@unicesar.edu.co','$2b$10$qg2zK1lLxQQfH5a6m0PxA.5pxgWZwBXLZ3nAT2RnUeDrpl85bnCo2', '3003456789',  'Diag 10 # 8-15', 'Valledupar', 'Colombia',  DATE '2026-04-15');
INSERT INTO CLIENTE (id_cliente, nombre,    apellido,    correo,                        password_hash,                                                  telefono,      direccion,        ciudad,       pais,        fecha_registro) VALUES
  (seq_cliente.NEXTVAL, 'Maria',   'Rodriguez', 'demo1@beatify.co',             '$2b$10$Xasr.R5CWbUaC.HP2Ljou.r/TxwxZmip8TVxvTeykaeS94NcaaW86', '3004567890',  'Cra 5 # 12-30',  'Barranquilla', 'Colombia',DATE '2026-04-20');
INSERT INTO CLIENTE (id_cliente, nombre,    apellido,    correo,                        password_hash,                                                  telefono,      direccion,        ciudad,       pais,        fecha_registro) VALUES
  (seq_cliente.NEXTVAL, 'Carlos',  'Perez',     'demo2@beatify.co',             '$2b$10$EbYIPJmOnE3u7FvWH/QMuOyRCuHsnlK3jU9uiJA9mrqfMB2XlPHCW', '3005678901',  'Cl 80 # 11-15',  'Bogota',     'Colombia',  DATE '2026-04-25');

-- =====================================================================
-- 7. SUSCRIPCION (5)  — IDs 1..5  (una activa por cliente)
-- =====================================================================
INSERT INTO SUSCRIPCION (id_suscripcion, tipo_plan,    precio,   fecha_inicio,        fecha_fin,           estado,    CLIENTE_id_cliente) VALUES (seq_suscripcion.NEXTVAL, 'ESTUDIANTE', 5990.00,  DATE '2026-04-15',   DATE '2027-04-15',   'ACTIVA',  1);
INSERT INTO SUSCRIPCION (id_suscripcion, tipo_plan,    precio,   fecha_inicio,        fecha_fin,           estado,    CLIENTE_id_cliente) VALUES (seq_suscripcion.NEXTVAL, 'ESTUDIANTE', 5990.00,  DATE '2026-04-15',   DATE '2027-04-15',   'ACTIVA',  2);
INSERT INTO SUSCRIPCION (id_suscripcion, tipo_plan,    precio,   fecha_inicio,        fecha_fin,           estado,    CLIENTE_id_cliente) VALUES (seq_suscripcion.NEXTVAL, 'ESTUDIANTE', 5990.00,  DATE '2026-04-15',   DATE '2027-04-15',   'ACTIVA',  3);
INSERT INTO SUSCRIPCION (id_suscripcion, tipo_plan,    precio,   fecha_inicio,        fecha_fin,           estado,    CLIENTE_id_cliente) VALUES (seq_suscripcion.NEXTVAL, 'INDIVIDUAL', 14900.00, DATE '2026-04-20',   DATE '2026-10-20',   'ACTIVA',  4);
INSERT INTO SUSCRIPCION (id_suscripcion, tipo_plan,    precio,   fecha_inicio,        fecha_fin,           estado,    CLIENTE_id_cliente) VALUES (seq_suscripcion.NEXTVAL, 'FREE',       0.00,     DATE '2026-04-25',   NULL,                'ACTIVA',  5);

-- =====================================================================
-- 8. PAGO (8)
-- =====================================================================
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 5990.00,  TIMESTAMP '2026-04-15 09:00:00',  'NEQUI',     'EXITOSO',  'NEQ-2026-001', 1);
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 5990.00,  TIMESTAMP '2026-04-15 09:05:00',  'NEQUI',     'EXITOSO',  'NEQ-2026-002', 2);
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 5990.00,  TIMESTAMP '2026-04-15 09:10:00',  'NEQUI',     'EXITOSO',  'NEQ-2026-003', 3);
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 14900.00, TIMESTAMP '2026-04-20 14:30:00',  'TARJETA',   'EXITOSO',  'TC-2026-100',  4);
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 14900.00, TIMESTAMP '2026-05-20 14:30:00',  'TARJETA',   'PENDIENTE','TC-2026-101',  4);
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 5990.00,  TIMESTAMP '2026-05-15 09:00:00',  'NEQUI',     'EXITOSO',  'NEQ-2026-050', 1);
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 5990.00,  TIMESTAMP '2026-05-15 09:05:00',  'NEQUI',     'FALLIDO',  'NEQ-2026-051', 2);
INSERT INTO PAGO (id_pago, monto,    fecha_pago,                        metodo_pago, estado_pago, referencia_externa,    SUSCRIPCION_id_suscripcion) VALUES (seq_pago.NEXTVAL, 5990.00,  TIMESTAMP '2026-05-15 09:30:00',  'PSE',       'EXITOSO',  'PSE-2026-200', 2);

-- =====================================================================
-- 9. DISPOSITIVO (6)
-- =====================================================================
INSERT INTO DISPOSITIVO (id_dispositivo, nombre_dispositivo,  tipo_dispositivo, sistema_operativo, fecha_ultimo_acceso,             CLIENTE_id_cliente) VALUES (seq_dispositivo.NEXTVAL, 'Galaxy S22',           'MOVIL',  'Android 13',  TIMESTAMP '2026-05-11 18:00:00', 1);
INSERT INTO DISPOSITIVO (id_dispositivo, nombre_dispositivo,  tipo_dispositivo, sistema_operativo, fecha_ultimo_acceso,             CLIENTE_id_cliente) VALUES (seq_dispositivo.NEXTVAL, 'Laptop ASUS Zen',      'PC',     'Windows 11',  TIMESTAMP '2026-05-12 22:30:00', 1);
INSERT INTO DISPOSITIVO (id_dispositivo, nombre_dispositivo,  tipo_dispositivo, sistema_operativo, fecha_ultimo_acceso,             CLIENTE_id_cliente) VALUES (seq_dispositivo.NEXTVAL, 'iPhone 14 Pro',        'MOVIL',  'iOS 17',      TIMESTAMP '2026-05-11 12:15:00', 2);
INSERT INTO DISPOSITIVO (id_dispositivo, nombre_dispositivo,  tipo_dispositivo, sistema_operativo, fecha_ultimo_acceso,             CLIENTE_id_cliente) VALUES (seq_dispositivo.NEXTVAL, 'iPad Pro',             'TABLET', 'iPadOS 17',   TIMESTAMP '2026-05-10 20:00:00', 3);
INSERT INTO DISPOSITIVO (id_dispositivo, nombre_dispositivo,  tipo_dispositivo, sistema_operativo, fecha_ultimo_acceso,             CLIENTE_id_cliente) VALUES (seq_dispositivo.NEXTVAL, 'Samsung Smart TV',     'SMARTTV','Tizen 7',     TIMESTAMP '2026-05-09 21:00:00', 4);
INSERT INTO DISPOSITIVO (id_dispositivo, nombre_dispositivo,  tipo_dispositivo, sistema_operativo, fecha_ultimo_acceso,             CLIENTE_id_cliente) VALUES (seq_dispositivo.NEXTVAL, 'Xiaomi Redmi Note',    'MOVIL',  'Android 12',  TIMESTAMP '2026-05-08 15:00:00', 5);

-- =====================================================================
-- 10. NOTIFICACION (8)
-- =====================================================================
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Bienvenido a Beatify',         'Tu cuenta fue activada con exito.',                            'SISTEMA',         TIMESTAMP '2026-04-15 09:01:00', 'S',   1);
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Nuevo album de Karol G',       'Acaba de salir KG0516, escuchalo ya.',                         'RECOMENDACION',   TIMESTAMP '2026-04-20 10:00:00', 'N',   1);
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Tu plan vence pronto',          'Tu suscripcion vence el 15 de octubre.',                       'INFO',            TIMESTAMP '2026-05-01 08:00:00', 'N',   4);
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Promocion 2x1 plan FAMILIAR', 'Por tiempo limitado obten plan familiar con descuento.',       'PROMO',           TIMESTAMP '2026-05-05 14:00:00', 'N',   5);
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Carlos Vives lanzo nueva cancion','Escucha el nuevo sencillo de Carlos Vives.',                'RECOMENDACION',   TIMESTAMP '2026-05-08 11:00:00', 'S',   2);
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Tu pago fue procesado',         'Pago de $5.990 procesado exitosamente.',                       'INFO',            TIMESTAMP '2026-05-15 09:01:00', 'S',   1);
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Pago fallido',                 'No se pudo procesar tu pago. Intenta de nuevo.',               'INFO',            TIMESTAMP '2026-05-15 09:06:00', 'N',   2);
INSERT INTO NOTIFICACION (id_notificacion, titulo,                       mensaje,                                                       tipo,              fecha_envio,                       leida, CLIENTE_id_cliente) VALUES (seq_notificacion.NEXTVAL, 'Logro desbloqueado',           'Felicidades, ganaste el logro Explorador Caribe.',             'SISTEMA',         TIMESTAMP '2026-05-10 16:00:00', 'S',   1);

-- =====================================================================
-- 11. PLAYLIST (6)  — IDs 1..6
-- =====================================================================
INSERT INTO PLAYLIST (id_playlist, nombre,                  descripcion,                                fecha_creacion,                       publica, CLIENTE_id_cliente) VALUES (seq_playlist.NEXTVAL, 'Vallenatos de oro',     'Lo mejor del vallenato clasico.',           TIMESTAMP '2026-04-16 10:00:00',   'S',     1);
INSERT INTO PLAYLIST (id_playlist, nombre,                  descripcion,                                fecha_creacion,                       publica, CLIENTE_id_cliente) VALUES (seq_playlist.NEXTVAL, 'Para estudiar',         'Musica suave para concentrarse.',          TIMESTAMP '2026-04-18 18:00:00',   'N',     1);
INSERT INTO PLAYLIST (id_playlist, nombre,                  descripcion,                                fecha_creacion,                       publica, CLIENTE_id_cliente) VALUES (seq_playlist.NEXTVAL, 'Mix rock latino',       'Aterciopelados, Juanes y mas.',            TIMESTAMP '2026-04-20 14:00:00',   'S',     2);
INSERT INTO PLAYLIST (id_playlist, nombre,                  descripcion,                                fecha_creacion,                       publica, CLIENTE_id_cliente) VALUES (seq_playlist.NEXTVAL, 'Fiesta caribe',         'Para gozar en el Caribe colombiano.',       TIMESTAMP '2026-04-22 19:00:00',   'S',     3);
INSERT INTO PLAYLIST (id_playlist, nombre,                  descripcion,                                fecha_creacion,                       publica, CLIENTE_id_cliente) VALUES (seq_playlist.NEXTVAL, 'Reggaeton mood',        'Lo mas escuchado del urbano.',              TIMESTAMP '2026-04-25 21:00:00',   'S',     4);
INSERT INTO PLAYLIST (id_playlist, nombre,                  descripcion,                                fecha_creacion,                       publica, CLIENTE_id_cliente) VALUES (seq_playlist.NEXTVAL, 'Colombia suena',        'Lo mejor de la musica colombiana.',         TIMESTAMP '2026-04-28 17:00:00',   'S',     5);

-- =====================================================================
-- 12. CANCION_PLAYLIST (20)
-- =====================================================================
-- Playlist 1 (Vallenatos de oro): canciones 1,2,5,6,7
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 1, DATE '2026-04-16', 1, 1);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 2, DATE '2026-04-16', 1, 2);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 3, DATE '2026-04-16', 1, 5);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 4, DATE '2026-04-16', 1, 6);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 5, DATE '2026-04-16', 1, 7);
-- Playlist 2 (Para estudiar): canciones 10, 19
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 1, DATE '2026-04-18', 2, 10);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 2, DATE '2026-04-18', 2, 19);
-- Playlist 3 (Mix rock latino): canciones 20, 21, 22, 23, 24
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 1, DATE '2026-04-20', 3, 20);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 2, DATE '2026-04-20', 3, 21);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 3, DATE '2026-04-20', 3, 22);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 4, DATE '2026-04-20', 3, 23);
-- Playlist 4 (Fiesta caribe): canciones 13, 14, 17, 18
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 1, DATE '2026-04-22', 4, 13);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 2, DATE '2026-04-22', 4, 14);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 3, DATE '2026-04-22', 4, 17);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 4, DATE '2026-04-22', 4, 18);
-- Playlist 5 (Reggaeton mood): canciones 28, 29, 30
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 1, DATE '2026-04-25', 5, 28);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 2, DATE '2026-04-25', 5, 29);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 3, DATE '2026-04-25', 5, 30);
-- Playlist 6 (Colombia suena): canciones 1, 8, 11, 26
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 1, DATE '2026-04-28', 6, 1);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 2, DATE '2026-04-28', 6, 11);
INSERT INTO CANCION_PLAYLIST (id_cancion_playlist, orden, fecha_agregada,     PLAYLIST_id_playlist, CANCION_id_cancion) VALUES (seq_cancion_playlist.NEXTVAL, 3, DATE '2026-04-28', 6, 26);

-- =====================================================================
-- 13. REPRODUCCION (50) — actividad de escucha
-- =====================================================================
-- Cliente 1 (Yilver)  — escucha vallenatos y rock
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-01 08:30:00', 213, 1, 1);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-01 08:35:00', 234, 1, 2);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-02 09:00:00', 245, 1, 3);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-02 09:05:00', 228, 1, 4);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-03 18:00:00', 276, 1, 5);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-04 18:30:00', 298, 1, 6);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-05 22:00:00', 215, 1, 23);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-06 22:30:00', 204, 1, 24);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-07 08:00:00', 213, 1, 1);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-08 08:00:00', 234, 1, 2);
-- Cliente 2 (Andres) — escucha de todo
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-01 10:00:00', 232, 2, 8);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-01 10:05:00', 218, 2, 9);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-02 11:00:00', 239, 2, 10);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-02 11:05:00', 193, 2, 11);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-03 12:00:00', 235, 2, 12);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-04 13:00:00', 218, 2, 20);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-05 14:00:00', 181, 2, 21);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-06 15:00:00', 165, 2, 28);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-07 16:00:00', 200, 2, 29);
-- Cliente 3 (Kendrick) — fiesta caribe
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-01 19:00:00', 274, 3, 13);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-02 19:30:00', 268, 3, 14);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-03 20:00:00', 241, 3, 15);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-04 20:30:00', 228, 3, 16);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-05 21:00:00', 194, 3, 18);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-06 21:30:00', 202, 3, 19);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-07 22:00:00', 213, 3, 1);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-08 22:30:00', 234, 3, 2);
-- Cliente 4 (Maria) — reggaeton
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-01 21:00:00', 165, 4, 28);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-02 21:00:00', 200, 4, 29);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-03 21:00:00', 212, 4, 30);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-04 21:00:00', 165, 4, 28);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-05 21:00:00', 200, 4, 29);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-06 21:00:00', 165, 4, 28);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-07 21:00:00', 212, 4, 30);
-- Cliente 5 (Carlos) — pop latino y vallenato
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-01 13:00:00', 223, 5, 25);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-02 14:00:00', 247, 5, 26);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-03 15:00:00', 213, 5, 1);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-04 16:00:00', 245, 5, 3);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-05 17:00:00', 241, 5, 15);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-06 18:00:00', 212, 5, 17);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-07 19:00:00', 223, 5, 25);
-- Extras para llegar a 50
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-08 19:00:00', 247, 5, 26);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-09 10:00:00', 232, 2, 8);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-09 11:00:00', 218, 2, 9);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-09 12:00:00', 165, 4, 28);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-10 08:00:00', 213, 1, 1);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-10 09:00:00', 165, 4, 28);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-11 10:00:00', 200, 4, 29);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-11 11:00:00', 212, 4, 30);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-11 12:00:00', 165, 3, 28);
INSERT INTO REPRODUCCION (id_reproduccion, fecha_hora,                       duracion_escuchada, CLIENTE_id_cliente, CANCION_id_cancion) VALUES (seq_reproduccion.NEXTVAL, TIMESTAMP '2026-05-12 08:00:00', 213, 1, 1);

-- =====================================================================
-- 14. SEGUIMIENTO (8) — clientes siguen artistas
-- =====================================================================
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-16', 1, 1);
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-17', 1, 5);
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-18', 2, 8);
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-19', 2, 3);
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-20', 3, 1);
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-21', 3, 4);
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-22', 4, 10);
INSERT INTO SEGUIMIENTO (id_seguimiento, fecha_seguimiento,   CLIENTE_id_cliente, ARTISTA_id_artista) VALUES (seq_seguimiento.NEXTVAL, DATE '2026-04-23', 5, 9);

-- =====================================================================
-- 15. BUSQUEDA (15)
-- =====================================================================
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'carlos vives',              TIMESTAMP '2026-05-01 08:00:00', 1, 1);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'vallenato',                 TIMESTAMP '2026-05-01 08:01:00', 4, 1);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'silvestre dangond',         TIMESTAMP '2026-05-02 09:00:00', 1, 1);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'shakira',                   TIMESTAMP '2026-05-01 10:00:00', 1, 2);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'rock latino',               TIMESTAMP '2026-05-02 11:00:00', 5, 2);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'aterciopelados',            TIMESTAMP '2026-05-03 12:00:00', 1, 2);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'salsa',                     TIMESTAMP '2026-05-01 19:00:00', 1, 3);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'joe arroyo',                TIMESTAMP '2026-05-02 19:30:00', 1, 3);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'fiesta',                    TIMESTAMP '2026-05-04 21:00:00', 0, 3);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'karol g',                   TIMESTAMP '2026-05-01 21:00:00', 1, 4);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'bichota',                   TIMESTAMP '2026-05-02 21:00:00', 1, 4);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'reggaeton',                 TIMESTAMP '2026-05-03 21:00:00', 3, 4);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'fonseca',                   TIMESTAMP '2026-05-01 13:00:00', 1, 5);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'pop colombiano',            TIMESTAMP '2026-05-02 14:00:00', 6, 5);
INSERT INTO BUSQUEDA (id_busqueda, termino_buscado,             fecha_busqueda,                       resultados_obtenidos, CLIENTE_id_cliente) VALUES (seq_busqueda.NEXTVAL, 'cumbia',                    TIMESTAMP '2026-05-05 17:00:00', 2, 5);

-- =====================================================================
-- 16. LIKE_CANCION (25)
-- =====================================================================
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 1, 1,  TIMESTAMP '2026-05-01 08:30:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 1, 2,  TIMESTAMP '2026-05-01 08:40:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 1, 15, TIMESTAMP '2026-05-03 18:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 1, 23, TIMESTAMP '2026-05-05 22:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 1, 24, TIMESTAMP '2026-05-06 22:30:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 2, 8,  TIMESTAMP '2026-05-01 10:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 2, 9,  TIMESTAMP '2026-05-01 10:05:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 2, 20, TIMESTAMP '2026-05-04 13:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 2, 21, TIMESTAMP '2026-05-05 14:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 2, 22, TIMESTAMP '2026-05-06 14:30:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 3, 13, TIMESTAMP '2026-05-01 19:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 3, 14, TIMESTAMP '2026-05-02 19:30:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 3, 18, TIMESTAMP '2026-05-05 21:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 3, 1,  TIMESTAMP '2026-05-07 22:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 4, 28, TIMESTAMP '2026-05-01 21:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 4, 29, TIMESTAMP '2026-05-02 21:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 4, 30, TIMESTAMP '2026-05-03 21:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 5, 25, TIMESTAMP '2026-05-01 13:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 5, 26, TIMESTAMP '2026-05-02 14:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 5, 1,  TIMESTAMP '2026-05-03 15:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 5, 3,  TIMESTAMP '2026-05-04 16:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 5, 15, TIMESTAMP '2026-05-05 17:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 5, 17, TIMESTAMP '2026-05-06 18:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 1, 12, TIMESTAMP '2026-05-08 19:00:00');
INSERT INTO LIKE_CANCION (id_like_cancion, CLIENTE_id_cliente, CANCION_id_cancion, fecha_like) VALUES (seq_like_cancion.NEXTVAL, 3, 17, TIMESTAMP '2026-05-09 20:00:00');

-- =====================================================================
-- 17. LIKE_ALBUM (10)
-- =====================================================================
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 1, 1,  TIMESTAMP '2026-05-01 09:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 1, 8,  TIMESTAMP '2026-05-03 18:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 2, 5,  TIMESTAMP '2026-05-01 10:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 2, 12, TIMESTAMP '2026-05-04 13:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 3, 7,  TIMESTAMP '2026-05-01 19:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 3, 1,  TIMESTAMP '2026-05-07 22:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 4, 15, TIMESTAMP '2026-05-01 21:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 5, 14, TIMESTAMP '2026-05-01 13:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 5, 1,  TIMESTAMP '2026-05-03 15:00:00');
INSERT INTO LIKE_ALBUM (id_like_ALBUM, CLIENTE_id_cliente, ALBUM_id_album, fecha_like) VALUES (seq_like_album.NEXTVAL, 5, 8,  TIMESTAMP '2026-05-05 17:00:00');

-- =====================================================================
-- 18. LIKE_PLAYLIST (5)
-- =====================================================================
INSERT INTO LIKE_PLAYLIST (id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like) VALUES (seq_like_playlist.NEXTVAL, 2, 1, TIMESTAMP '2026-04-17 10:00:00');
INSERT INTO LIKE_PLAYLIST (id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like) VALUES (seq_like_playlist.NEXTVAL, 3, 1, TIMESTAMP '2026-04-17 11:00:00');
INSERT INTO LIKE_PLAYLIST (id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like) VALUES (seq_like_playlist.NEXTVAL, 1, 3, TIMESTAMP '2026-04-21 12:00:00');
INSERT INTO LIKE_PLAYLIST (id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like) VALUES (seq_like_playlist.NEXTVAL, 5, 4, TIMESTAMP '2026-04-23 13:00:00');
INSERT INTO LIKE_PLAYLIST (id_like_playlist, CLIENTE_id_cliente, PLAYLIST_id_playlist, fecha_like) VALUES (seq_like_playlist.NEXTVAL, 4, 6, TIMESTAMP '2026-04-29 14:00:00');

-- =====================================================================
-- 19. PODCAST (3)  — IDs 1..3
-- =====================================================================
INSERT INTO PODCAST (id_podcast, titulo,                       descripcion,                                              categoria,             portada_url, fecha_creacion,    ARTISTA_id_artista) VALUES (seq_podcast.NEXTVAL, 'Ritmos de Colombia',         'Charla sobre la historia de los ritmos colombianos.',     'Cultura musical',     NULL,        DATE '2026-03-01', 1);
INSERT INTO PODCAST (id_podcast, titulo,                       descripcion,                                              categoria,             portada_url, fecha_creacion,    ARTISTA_id_artista) VALUES (seq_podcast.NEXTVAL, 'Detras del Vallenato',       'Entrevistas con leyendas del vallenato.',                 'Vallenato',           NULL,        DATE '2026-03-15', 5);
INSERT INTO PODCAST (id_podcast, titulo,                       descripcion,                                              categoria,             portada_url, fecha_creacion,    ARTISTA_id_artista) VALUES (seq_podcast.NEXTVAL, 'Mujeres en la musica',       'Conversaciones con artistas femeninas latinas.',           'Cultura urbana',     NULL,        DATE '2026-04-01', 10);

-- =====================================================================
-- 20. EPISODIO (9)
-- =====================================================================
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 1, 'Origenes del vallenato',         'Como nacio el vallenato en el Magdalena Grande.',          1820, DATE '2026-03-01', 'podcast/ritmos_ep1.mp3',           1);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 2, 'La cumbia y sus raices',         'La cumbia colombiana y africana.',                          1950, DATE '2026-03-08', 'podcast/ritmos_ep2.mp3',           1);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 3, 'Champeta africana al Caribe',    'Como llego la champeta a Cartagena.',                       1700, DATE '2026-03-15', 'podcast/ritmos_ep3.mp3',           1);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 1, 'Diomedes, el Cacique',           'Vida y obra de Diomedes Diaz.',                             2300, DATE '2026-03-15', 'podcast/vallenato_ep1.mp3',        2);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 2, 'Carlos Vives y la fusion',       'El boom del vallenato pop.',                                2100, DATE '2026-03-22', 'podcast/vallenato_ep2.mp3',        2);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 3, 'Silvestre y la nueva ola',       'La generacion 2010 del vallenato.',                          1850, DATE '2026-03-29', 'podcast/vallenato_ep3.mp3',        2);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 1, 'Karol G: bichota global',        'Como llego Karol G a ser figura mundial.',                  1750, DATE '2026-04-01', 'podcast/mujeres_ep1.mp3',          3);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 2, 'Shakira: la loba',               'La carrera de la barranquillera.',                           2400, DATE '2026-04-08', 'podcast/mujeres_ep2.mp3',          3);
INSERT INTO EPISODIO (id_episodio, numero_episodio, titulo,                          descripcion,                                             duracion_seg, fecha_publicacion,  ruta_archivo,                       PODCAST_id_podcast) VALUES (seq_episodio.NEXTVAL, 3, 'Andrea Echeverri: rock con voz', 'La rebeldia de Aterciopelados.',                             2050, DATE '2026-04-15', 'podcast/mujeres_ep3.mp3',          3);

-- =====================================================================
-- 21. COLABORACION (5)
-- =====================================================================
-- Carlos Vives feat Shakira en cancion 1 (La Gota Fria — ejemplo de feat)
INSERT INTO COLABORACION (id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista, rol,            fecha_colaboracion) VALUES (seq_colaboracion.NEXTVAL, 3,  3,  'FEATURING',    DATE '1999-07-20');
-- Karol G feat Juanes
INSERT INTO COLABORACION (id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista, rol,            fecha_colaboracion) VALUES (seq_colaboracion.NEXTVAL, 29, 8,  'FEATURING',    DATE '2021-03-26');
-- Fonseca produccion en album de Silvestre
INSERT INTO COLABORACION (id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista, rol,            fecha_colaboracion) VALUES (seq_colaboracion.NEXTVAL, 15, 9,  'PRODUCTOR',    DATE '2014-10-30');
-- Bomba Estereo feat Carlos Vives
INSERT INTO COLABORACION (id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista, rol,            fecha_colaboracion) VALUES (seq_colaboracion.NEXTVAL, 19, 1,  'FEATURING',    DATE '2012-11-09');
-- Juanes coautor con Aterciopelados
INSERT INTO COLABORACION (id_colaboracion, CANCION_id_cancion, ARTISTA_id_artista, rol,            fecha_colaboracion) VALUES (seq_colaboracion.NEXTVAL, 21, 8,  'COAUTOR',      DATE '1996-05-14');

-- =====================================================================
-- 22. LOGRO (6)  — catalogo de logros
-- =====================================================================
INSERT INTO LOGRO (id_logro, codigo,               nombre,                            descripcion,                                                            icono_url, puntos) VALUES (seq_logro.NEXTVAL, 'EXP_CARIBE',         'Explorador Caribe',               'Escucha 10 canciones de vallenato, cumbia o champeta.',                 NULL,      50);
INSERT INTO LOGRO (id_logro, codigo,               nombre,                            descripcion,                                                            icono_url, puntos) VALUES (seq_logro.NEXTVAL, 'PRIMER_LIKE',        'Primer Me Gusta',                 'Dale tu primer like a una cancion.',                                    NULL,      10);
INSERT INTO LOGRO (id_logro, codigo,               nombre,                            descripcion,                                                            icono_url, puntos) VALUES (seq_logro.NEXTVAL, 'FAN_VALLENATO',      'Fanatico del Vallenato',          'Sigue 3 artistas de vallenato.',                                        NULL,      30);
INSERT INTO LOGRO (id_logro, codigo,               nombre,                            descripcion,                                                            icono_url, puntos) VALUES (seq_logro.NEXTVAL, 'NOCTAMBULO',         'Noctambulo Musical',              'Escucha 5 canciones despues de medianoche.',                            NULL,      20);
INSERT INTO LOGRO (id_logro, codigo,               nombre,                            descripcion,                                                            icono_url, puntos) VALUES (seq_logro.NEXTVAL, 'CRITICO',            'Critico Musical',                 'Escribe 5 resenas.',                                                    NULL,      40);
INSERT INTO LOGRO (id_logro, codigo,               nombre,                            descripcion,                                                            icono_url, puntos) VALUES (seq_logro.NEXTVAL, 'COLECCIONISTA',      'Coleccionista',                   'Crea 3 playlists publicas.',                                            NULL,      30);

-- =====================================================================
-- 23. LOGRO_CLIENTE (10) — logros otorgados
-- =====================================================================
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-10 16:00:00', 1, 1);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-01 08:30:00', 1, 2);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-04-17 11:00:00', 1, 6);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-01 10:00:00', 2, 2);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-04-20 14:00:00', 2, 6);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-01 19:00:00', 3, 1);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-01 19:00:00', 3, 2);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-01 21:00:00', 4, 2);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-01 13:00:00', 5, 2);
INSERT INTO LOGRO_CLIENTE (id_logro_cliente, fecha_obtencion,                       CLIENTE_id_cliente, LOGRO_id_logro) VALUES (seq_logro_cliente.NEXTVAL, TIMESTAMP '2026-05-05 17:00:00', 5, 1);

-- =====================================================================
-- 24. RESENA (12)
-- tipo_objetivo IN ('ALBUM','ARTISTA','CANCION')
-- =====================================================================
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 1, 'ARTISTA', 1,  5, 'Carlos Vives es el rey del vallenato moderno.',         TIMESTAMP '2026-05-02 10:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 1, 'ALBUM',   1,  5, 'Album imperdible, clasico de los noventa.',              TIMESTAMP '2026-05-03 10:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 1, 'CANCION', 1,  5, 'La Gota Fria es himno costeño.',                          TIMESTAMP '2026-05-04 10:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 2, 'ARTISTA', 3,  5, 'Shakira es orgullo barranquillero.',                     TIMESTAMP '2026-05-02 12:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 2, 'ALBUM',   5,  4, 'Pies Descalzos es un buen album debut.',                  TIMESTAMP '2026-05-03 12:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 2, 'ARTISTA', 8,  5, 'Juanes es referente del rock latino.',                    TIMESTAMP '2026-05-04 12:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 3, 'CANCION', 13, 5, 'La Rebelion es un himno de la salsa.',                    TIMESTAMP '2026-05-02 19:30:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 3, 'ARTISTA', 4,  5, 'Joe Arroyo es leyenda de la salsa caribena.',             TIMESTAMP '2026-05-03 19:30:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 4, 'CANCION', 28, 5, 'Bichota es perreo puro.',                                  TIMESTAMP '2026-05-02 21:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 4, 'ALBUM',   15, 4, 'KG0516 muy bueno aunque algunas canciones son parecidas.', TIMESTAMP '2026-05-03 21:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 5, 'CANCION', 25, 4, 'Te Mando Flores siempre me emociona.',                    TIMESTAMP '2026-05-02 14:00:00');
INSERT INTO RESENA (id_resena, CLIENTE_id_cliente, tipo_objetivo, id_objetivo, calificacion, comentario,                                             fecha_resena) VALUES (seq_resena.NEXTVAL, 5, 'ARTISTA', 9,  5, 'Fonseca tiene una voz inconfundible.',                    TIMESTAMP '2026-05-03 14:00:00');

-- =====================================================================
-- 25. VOTO_RESENA (20)
-- =====================================================================
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 10:00:00', 1,  2);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 11:00:00', 1,  3);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 10:00:00', 2,  2);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 11:00:00', 2,  5);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-05 10:00:00', 3,  3);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 13:00:00', 4,  1);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 14:00:00', 4,  5);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'N', TIMESTAMP '2026-05-04 13:00:00', 5,  1);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 14:00:00', 5,  3);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-05 13:00:00', 6,  1);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 20:00:00', 7,  1);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 20:00:00', 7,  4);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 21:00:00', 8,  1);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 22:00:00', 9,  3);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 23:00:00', 9,  5);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'N', TIMESTAMP '2026-05-04 22:00:00', 10, 3);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-03 15:00:00', 11, 1);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 15:00:00', 11, 2);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 16:00:00', 12, 1);
INSERT INTO VOTO_RESENA (id_voto_resena, util, fecha_voto,                        RESENA_id_resena, CLIENTE_id_cliente) VALUES (seq_voto_resena.NEXTVAL, 'S', TIMESTAMP '2026-05-04 17:00:00', 12, 4);

COMMIT;

-- =====================================================================
-- VERIFICACION RAPIDA (opcional)
-- =====================================================================
-- SELECT 'GENERO'          AS tabla, COUNT(*) FROM GENERO          UNION ALL
-- SELECT 'ARTISTA',         COUNT(*) FROM ARTISTA                  UNION ALL
-- SELECT 'ALBUM',           COUNT(*) FROM ALBUM                    UNION ALL
-- SELECT 'CANCION',         COUNT(*) FROM CANCION                  UNION ALL
-- SELECT 'CLIENTE',         COUNT(*) FROM CLIENTE                  UNION ALL
-- SELECT 'PLAYLIST',        COUNT(*) FROM PLAYLIST                 UNION ALL
-- SELECT 'REPRODUCCION',    COUNT(*) FROM REPRODUCCION             UNION ALL
-- SELECT 'LIKE_CANCION',    COUNT(*) FROM LIKE_CANCION             UNION ALL
-- SELECT 'RESENA',          COUNT(*) FROM RESENA;
