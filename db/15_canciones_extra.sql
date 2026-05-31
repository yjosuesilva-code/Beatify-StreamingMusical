-- ================================================================
-- 15_canciones_extra.sql
-- Canciones adicionales para poblar el catálogo de Beatify.
-- Prerequisito: 02_seed_data.sql ya ejecutado (artistas IDs 1-10,
-- álbumes IDs 1-15, géneros IDs 1-7).
-- ================================================================
SET DEFINE OFF;

-- ----------------------------------------------------------------
-- 1. Géneros faltantes (IDs 8-11)
-- ----------------------------------------------------------------
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais)
VALUES (seq_genero.NEXTVAL, 'Bullerengue',    'Ritmo afrocaribe de la costa norte colombiana', 'Colombia');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais)
VALUES (seq_genero.NEXTVAL, 'Música andina',  'Ritmos de la región andina colombiana',         'Colombia');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais)
VALUES (seq_genero.NEXTVAL, 'Currulao',       'Música del Pacífico colombiano',                'Colombia');
INSERT INTO GENERO (id_genero, nombre, descripcion, origen_pais)
VALUES (seq_genero.NEXTVAL, 'Joropo',         'Música llanera colombo-venezolana',             'Colombia');

-- ----------------------------------------------------------------
-- 2. Álbumes nuevos (IDs 16-25)
-- ----------------------------------------------------------------
-- Carlos Vives (1)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'Bienvenido al Paraíso', 1995, 'ALBUM', 1);
-- El Cacique (2)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'El Ídolo', 1991, 'ALBUM', 2);
-- Shakira (3)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'Laundry Service', 2001, 'ALBUM', 3);
-- Joe Arroyo (4)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'El Joe Arroyo', 1986, 'ALBUM', 4);
-- Silvestre Dangond (5)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'Me Llamas', 2012, 'ALBUM', 5);
-- Bomba Estereo (6)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'Amanecer', 2015, 'ALBUM', 6);
-- Aterciopelados (7)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'El Dorado', 1995, 'ALBUM', 7);
-- Juanes (8)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'Loco de Amor', 2014, 'ALBUM', 8);
-- Fonseca (9)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'Conexión', 2016, 'ALBUM', 9);
-- Karol G (10)
INSERT INTO ALBUM (id_album, titulo, anio_lanzamiento, tipo, ARTISTA_id_artista)
VALUES (seq_album.NEXTVAL, 'Mañana Será Bonito', 2023, 'ALBUM', 10);

-- ----------------------------------------------------------------
-- 3. Canciones nuevas
-- Géneros: 1=Vallenato 2=Cumbia 3=Champeta 4=Salsa 5=Reggaeton
--          6=Rock 7=Pop Latino 8=Bullerengue 9=Música andina
--         10=Currulao 11=Joropo
-- ----------------------------------------------------------------

-- === Carlos Vives — álbum 1: Clásicos de la Provincia ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'El Amor de Mi Vida',     198,'audio/cv_amordemiviida.mp3','Carlos Vives',    DATE '1993-09-15', 1, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Carito',                 221,'audio/cv_carito.mp3',       'Carlos Vives',    DATE '1993-09-15', 1, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Alicia Adorada',         208,'audio/cv_alicia.mp3',       'Juancho Rois',    DATE '1993-09-15', 1, 1);

-- === Carlos Vives — álbum 16: Bienvenido al Paraíso ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Bienvenido al Paraíso',  215,'audio/cv_bienvenido.mp3',   'Carlos Vives',    DATE '1995-06-01', 16, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Cumbia en el Río',        233,'audio/cv_cumbia.mp3',       'Carlos Vives',    DATE '1995-06-01', 16, 2);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Déjame Entrar',           204,'audio/cv_dejame.mp3',       'Carlos Vives',    DATE '1995-06-01', 16, 7);

-- === El Cacique — álbum 3: Mi Biografía ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'El Testamento',          285,'audio/dd_testamento.mp3',   'Diomedes Díaz',   DATE '1988-04-10', 3, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Bonita',                 267,'audio/dd_bonita.mp3',        'Diomedes Díaz',   DATE '1988-04-10', 3, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Pedazo de Acordeón',     249,'audio/dd_pedazo.mp3',        'Diomedes Díaz',   DATE '1988-04-10', 3, 1);

-- === El Cacique — álbum 17: El Ídolo ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'La Plata',               238,'audio/dd_laplata.mp3',       'Diomedes Díaz',   DATE '1991-01-01', 17, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Enamorado',              252,'audio/dd_enamorado.mp3',     'Diomedes Díaz',   DATE '1991-01-01', 17, 1);

-- === Shakira — álbum 5: Pies Descalzos ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Quiero',                 196,'audio/sh_quiero.mp3',        'Shakira',         DATE '1995-11-06', 5, 7);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Pienso en Ti',           214,'audio/sh_pienso.mp3',        'Shakira',         DATE '1995-11-06', 5, 6);

-- === Shakira — álbum 18: Laundry Service ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Whenever Wherever',      220,'audio/sh_whenever.mp3',      'Shakira',         DATE '2001-10-22', 18, 7);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Underneath Your Clothes',237,'audio/sh_underneath.mp3',    'Shakira',         DATE '2001-10-22', 18, 7);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Objection (Tango)',      226,'audio/sh_objection.mp3',     'Shakira',         DATE '2001-10-22', 18, 7);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Te Dejo Madrid',         208,'audio/sh_tedejo.mp3',        'Shakira',         DATE '2001-10-22', 18, 6);

-- === Joe Arroyo — álbum 7: Fuego en Mi Mente ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Tumbatecho',             258,'audio/ja_tumbatecho.mp3',    'Joe Arroyo',      DATE '1990-03-01', 7, 4);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Musa Original',          244,'audio/ja_musa.mp3',          'Joe Arroyo',      DATE '1990-03-01', 7, 4);

-- === Joe Arroyo — álbum 19: El Joe Arroyo ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'No Le Pegue a la Negra', 261,'audio/ja_nopegue.mp3',      'Joe Arroyo',      DATE '1986-05-01', 19, 4);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'El Centurión',           278,'audio/ja_centurion.mp3',     'Joe Arroyo',      DATE '1986-05-01', 19, 4);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'La Noche',               232,'audio/ja_lanoche.mp3',       'Joe Arroyo',      DATE '1986-05-01', 19, 4);

-- === Silvestre Dangond — álbum 8: Sigo Invicto ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Si Tú Me Dices Ven',    236,'audio/sd_situmes.mp3',       'Silvestre Dangond',DATE '2013-01-01', 8, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Cásate Conmigo',        218,'audio/sd_casate.mp3',        'Silvestre Dangond',DATE '2013-01-01', 8, 1);

-- === Silvestre Dangond — álbum 20: Me Llamas ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Me Llamas',              224,'audio/sd_mellamas.mp3',      'Silvestre Dangond',DATE '2012-05-01', 20, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Adicto al Dolor',        241,'audio/sd_adicto.mp3',        'Silvestre Dangond',DATE '2012-05-01', 20, 1);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Listo',                  197,'audio/sd_listo.mp3',         'Silvestre Dangond',DATE '2012-05-01', 20, 1);

-- === Bomba Estereo — álbum 21: Amanecer ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Amanecer',               211,'audio/be_amanecer.mp3',      'Bomba Estereo',   DATE '2015-03-01', 21, 2);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Fuego',                  198,'audio/be_fuego.mp3',         'Bomba Estereo',   DATE '2015-03-01', 21, 2);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Duele',                  223,'audio/be_duele.mp3',         'Bomba Estereo',   DATE '2015-03-01', 21, 2);

-- === Aterciopelados — álbum 22: El Dorado ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'El Estuche',             201,'audio/at_estuche.mp3',       'Aterciopelados',  DATE '1995-01-01', 22, 6);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Hey',                    188,'audio/at_hey.mp3',           'Aterciopelados',  DATE '1995-01-01', 22, 6);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Mujer Gallo',            216,'audio/at_mujergallo.mp3',    'Aterciopelados',  DATE '1995-01-01', 22, 6);

-- === Juanes — álbum 23: Loco de Amor ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Juntos',                 209,'audio/ju_juntos.mp3',        'Juanes',          DATE '2014-02-01', 23, 7);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Loco de Amor',           224,'audio/ju_loco.mp3',          'Juanes',          DATE '2014-02-01', 23, 6);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'La Vida Es un Ratico',   233,'audio/ju_laviida.mp3',       'Juanes',          DATE '2014-02-01', 23, 6);

-- === Fonseca — álbum 24: Conexión ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Eres Mi Sueño',          219,'audio/fo_eresmi.mp3',        'Fonseca',         DATE '2016-04-01', 24, 7);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Llegaste Tú',            208,'audio/fo_llegaste.mp3',      'Fonseca',         DATE '2016-04-01', 24, 7);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Yo No Sé Mañana',        225,'audio/fo_yonosemañana.mp3',  'Fonseca',         DATE '2016-04-01', 24, 7);

-- === Karol G — álbum 25: Mañana Será Bonito ===
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Mañana Será Bonito',     187,'audio/kg_manana.mp3',        'Karol G',         DATE '2023-02-24', 25, 5);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Cairo',                  205,'audio/kg_cairo.mp3',         'Karol G',         DATE '2023-02-24', 25, 5);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Mientras Me Curo el Cora',198,'audio/kg_mientras.mp3',     'Karol G',         DATE '2023-02-24', 25, 5);
INSERT INTO CANCION (id_cancion,titulo,duracion_seg,ruta_archivo,compositor,fecha_lanzamiento,ALBUM_id_album,GENERO_id_genero)
VALUES (seq_cancion.NEXTVAL,'Gatúbela',               193,'audio/kg_gatubela.mp3',      'Karol G',         DATE '2023-02-24', 25, 5);

COMMIT;

-- Verificación final
SELECT COUNT(*) total_canciones FROM CANCION;
SELECT COUNT(*) total_albumes FROM ALBUM;
SELECT COUNT(*) total_generos FROM GENERO;
