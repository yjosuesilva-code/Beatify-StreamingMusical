-- Generado por Oracle SQL Developer Data Modeler 24.3.1.351.0831
--   en:        2026-05-09 17:38:37 COT
--   sitio:      Oracle Database 21c
--   tipo:      Oracle Database 21c



DROP TABLE ALBUM CASCADE CONSTRAINTS 
;

DROP TABLE ARTISTA CASCADE CONSTRAINTS 
;

DROP TABLE ARTISTA_GENERO CASCADE CONSTRAINTS 
;

DROP TABLE BUSQUEDA CASCADE CONSTRAINTS 
;

DROP TABLE CANCION CASCADE CONSTRAINTS 
;

DROP TABLE CANCION_PLAYLIST CASCADE CONSTRAINTS 
;

DROP TABLE CLIENTE CASCADE CONSTRAINTS 
;

DROP TABLE COLABORACION CASCADE CONSTRAINTS 
;

DROP TABLE DISPOSITIVO CASCADE CONSTRAINTS 
;

DROP TABLE EPISODIO CASCADE CONSTRAINTS 
;

DROP TABLE GENERO CASCADE CONSTRAINTS 
;

DROP TABLE LIKE_ALBUM CASCADE CONSTRAINTS 
;

DROP TABLE LIKE_CANCION CASCADE CONSTRAINTS 
;

DROP TABLE LIKE_PLAYLIST CASCADE CONSTRAINTS 
;

DROP TABLE LOGRO CASCADE CONSTRAINTS 
;

DROP TABLE LOGRO_CLIENTE CASCADE CONSTRAINTS 
;

DROP TABLE NOTIFICACION CASCADE CONSTRAINTS 
;

DROP TABLE PAGO CASCADE CONSTRAINTS 
;

DROP TABLE PLAYLIST CASCADE CONSTRAINTS 
;

DROP TABLE PODCAST CASCADE CONSTRAINTS 
;

DROP TABLE REPRODUCCION CASCADE CONSTRAINTS 
;

DROP TABLE RESENA CASCADE CONSTRAINTS 
;

DROP TABLE SEGUIMIENTO CASCADE CONSTRAINTS 
;

DROP TABLE SUSCRIPCION CASCADE CONSTRAINTS 
;

DROP TABLE VOTO_RESENA CASCADE CONSTRAINTS 
;

-- predefined type, no DDL - MDSYS.SDO_GEOMETRY

-- predefined type, no DDL - XMLTYPE

CREATE TABLE ALBUM 
    ( 
     id_album           INTEGER  NOT NULL , 
     titulo             VARCHAR2 (150 CHAR)  NOT NULL , 
     anio_lanzamiento    INTEGER  NOT NULL ,
     sello_discografico VARCHAR2 (25 CHAR) , 
     tipo               VARCHAR2 (20 CHAR) , 
     portada_url        VARCHAR2 (500 CHAR) , 
     descripcion        VARCHAR2 (500 CHAR) , 
     ARTISTA_id_artista INTEGER  NOT NULL 
    ) 
;

ALTER TABLE ALBUM 
    ADD CONSTRAINT ALBUM_ANIO_LANZAMIENTO_CK 
    CHECK (anio_lanzamiento BETWEEN 1900 AND 2100) 
;

ALTER TABLE ALBUM 
    ADD CONSTRAINT ALBUM_TIPO_CK 
    CHECK (tipo IN ('ALBUM', 'COMPILACION', 'EP', 'SINGLE')) 
;

ALTER TABLE ALBUM 
    ADD CONSTRAINT ALBUM_PK PRIMARY KEY ( id_album ) ;

CREATE TABLE ARTISTA 
    ( 
     id_artista       INTEGER  NOT NULL , 
     nombre           VARCHAR2 (25 CHAR)  NOT NULL , 
     apellido         VARCHAR2 (25 CHAR)  NOT NULL , 
     nombre_artistico VARCHAR2 (25 CHAR) , 
     fecha_nacimiento DATE , 
     pais             VARCHAR2 (20 CHAR) , 
     correo           VARCHAR2 (120 CHAR) , 
     biografia        VARCHAR2 (4000 CHAR) , 
     foto_url         VARCHAR2 (500 CHAR) 
    ) 
;

ALTER TABLE ARTISTA 
    ADD CONSTRAINT ARTISTA_PK PRIMARY KEY ( id_artista ) ;

CREATE TABLE ARTISTA_GENERO 
    ( 
     ARTISTA_id_artista INTEGER  NOT NULL , 
     GENERO_id_genero   INTEGER  NOT NULL 
    ) 
;

ALTER TABLE ARTISTA_GENERO 
    ADD CONSTRAINT ARTISTA_GENERO_PK PRIMARY KEY ( ARTISTA_id_artista, GENERO_id_genero ) ;

CREATE TABLE BUSQUEDA 
    ( 
     id_busqueda          INTEGER  NOT NULL , 
     termino_buscado      VARCHAR2 (200 CHAR)  NOT NULL , 
     fecha_busqueda       TIMESTAMP , 
     resultados_obtenidos INTEGER , 
     CLIENTE_id_cliente   INTEGER  NOT NULL 
    ) 
;

ALTER TABLE BUSQUEDA 
    ADD CONSTRAINT BUSQUEDA_PK PRIMARY KEY ( id_busqueda ) ;

CREATE TABLE CANCION 
    ( 
     id_cancion        INTEGER  NOT NULL , 
     titulo            VARCHAR2 (150 CHAR)  NOT NULL , 
     duracion_seg      INTEGER  NOT NULL , 
     ruta_archivo      VARCHAR2 (500 CHAR)  NOT NULL , 
     letra             VARCHAR2 (4000 CHAR) , 
     compositor        VARCHAR2 (60 CHAR) , 
     fecha_lanzamiento DATE , 
     ALBUM_id_album    INTEGER  NOT NULL , 
     GENERO_id_genero  INTEGER  NOT NULL 
    ) 
;

ALTER TABLE CANCION 
    ADD CONSTRAINT CANCION_PK PRIMARY KEY ( id_cancion ) ;

CREATE TABLE CANCION_PLAYLIST 
    ( 
     id_cancion_playlist  INTEGER  NOT NULL , 
     orden                INTEGER , 
     fecha_agregada       DATE , 
     PLAYLIST_id_playlist INTEGER  NOT NULL , 
     CANCION_id_cancion   INTEGER  NOT NULL 
    ) 
;

ALTER TABLE CANCION_PLAYLIST 
    ADD CONSTRAINT CANCION_PLAYLIST_PK PRIMARY KEY ( id_cancion_playlist ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE CANCION_PLAYLIST 
    ADD CONSTRAINT CANCION_PLAYLIST_id_cancion_id_playlist_UN UNIQUE ( CANCION_id_cancion , PLAYLIST_id_playlist ) ;

CREATE TABLE CLIENTE 
    ( 
     id_cliente     INTEGER  NOT NULL , 
     nombre         VARCHAR2 (50 CHAR)  NOT NULL , 
     apellido       VARCHAR2 (50 CHAR)  NOT NULL , 
     correo         VARCHAR2 (120 CHAR)  NOT NULL , 
     password_hash  VARCHAR2 (100 CHAR)  NOT NULL , 
     telefono       VARCHAR2 (15 CHAR) , 
     direccion      VARCHAR2 (30 CHAR) , 
     ciudad         VARCHAR2 (15 CHAR) , 
     pais           VARCHAR2 (15 CHAR) , 
     fecha_registro DATE 
    ) 
;

ALTER TABLE CLIENTE 
    ADD CONSTRAINT CLIENTE_PK PRIMARY KEY ( id_cliente ) ;

ALTER TABLE CLIENTE 
    ADD CONSTRAINT CLIENTE_correo_UN UNIQUE ( correo ) ;

CREATE TABLE COLABORACION 
    ( 
     id_colaboracion    INTEGER  NOT NULL , 
     CANCION_id_cancion INTEGER  NOT NULL , 
     ARTISTA_id_artista INTEGER  NOT NULL , 
     rol                VARCHAR2 (30 CHAR)  NOT NULL , 
     fecha_colaboracion DATE 
    ) 
;

ALTER TABLE COLABORACION 
    ADD CONSTRAINT COLABORACION_PK PRIMARY KEY ( id_colaboracion ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE COLABORACION 
    ADD CONSTRAINT COLABORACION_id_cancion_id_artista_rol_UN UNIQUE ( CANCION_id_cancion , ARTISTA_id_artista , rol ) ;

CREATE TABLE DISPOSITIVO 
    ( 
     id_dispositivo      INTEGER  NOT NULL , 
     nombre_dispositivo  VARCHAR2 (80 CHAR) , 
     tipo_dispositivo    VARCHAR2 (20 CHAR) , 
     sistema_operativo   VARCHAR2 (30 CHAR) , 
     fecha_ultimo_acceso TIMESTAMP , 
     CLIENTE_id_cliente  INTEGER  NOT NULL 
    ) 
;

--  ERROR: Column DISPOSITIVO.tipo_dispositivo check constraint name length exceeds maximum allowed length(30) 

ALTER TABLE DISPOSITIVO 
    ADD 
    CHECK (tipo_dispositivo IN ('MOVIL', 'PC', 'SMARTTV', 'TABLET')) 
;

ALTER TABLE DISPOSITIVO 
    ADD CONSTRAINT DISPOSITIVO_PK PRIMARY KEY ( id_dispositivo ) ;

CREATE TABLE EPISODIO 
    ( 
     id_episodio        INTEGER  NOT NULL , 
     numero_episodio    INTEGER  NOT NULL , 
     titulo             VARCHAR2 (150 CHAR)  NOT NULL , 
     descripcion        VARCHAR2 (500 CHAR) , 
     duracion_seg       INTEGER  NOT NULL , 
     fecha_publicacion  DATE , 
     ruta_archivo       VARCHAR2 (500 CHAR)  NOT NULL , 
     PODCAST_id_podcast INTEGER  NOT NULL 
    ) 
;

ALTER TABLE EPISODIO 
    ADD CONSTRAINT EPISODIO_PK PRIMARY KEY ( id_episodio ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE EPISODIO 
    ADD CONSTRAINT EPISODIO_id_podcast_numero_episodio_UN UNIQUE ( PODCAST_id_podcast , numero_episodio ) ;

CREATE TABLE GENERO 
    ( 
     id_genero   INTEGER  NOT NULL , 
     nombre      VARCHAR2 (25 CHAR)  NOT NULL , 
     descripcion VARCHAR2 (50 CHAR) , 
     origen_pais VARCHAR2 (20 CHAR) 
    ) 
;

ALTER TABLE GENERO 
    ADD CONSTRAINT GENERO_PK PRIMARY KEY ( id_genero ) ;

CREATE TABLE LIKE_ALBUM 
    ( 
     id_like_ALBUM      INTEGER  NOT NULL , 
     CLIENTE_id_cliente INTEGER  NOT NULL , 
     ALBUM_id_album     INTEGER  NOT NULL , 
     fecha_like         TIMESTAMP 
    ) 
;

ALTER TABLE LIKE_ALBUM 
    ADD CONSTRAINT LIKE_ALBUM_PK PRIMARY KEY ( id_like_ALBUM ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE LIKE_ALBUM 
    ADD CONSTRAINT LIKE_ALBUM_id_cliente_id_album_UN UNIQUE ( CLIENTE_id_cliente , ALBUM_id_album ) ;

CREATE TABLE LIKE_CANCION 
    ( 
     id_like_cancion    INTEGER  NOT NULL , 
     CLIENTE_id_cliente INTEGER  NOT NULL , 
     CANCION_id_cancion INTEGER  NOT NULL , 
     fecha_like         TIMESTAMP 
    ) 
;

ALTER TABLE LIKE_CANCION 
    ADD CONSTRAINT LIKE_CANCION_PK PRIMARY KEY ( id_like_cancion ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE LIKE_CANCION 
    ADD CONSTRAINT LIKE_CANCION_id_cliente_id_cancion_UN UNIQUE ( CLIENTE_id_cliente , CANCION_id_cancion ) ;

CREATE TABLE LIKE_PLAYLIST 
    ( 
     id_like_playlist     INTEGER  NOT NULL , 
     CLIENTE_id_cliente   INTEGER  NOT NULL , 
     PLAYLIST_id_playlist INTEGER  NOT NULL , 
     fecha_like           TIMESTAMP 
    ) 
;

ALTER TABLE LIKE_PLAYLIST 
    ADD CONSTRAINT LIKE_PLAYLIST_PK PRIMARY KEY ( id_like_playlist ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE LIKE_PLAYLIST 
    ADD CONSTRAINT LIKE_PLAYLIST_id_cliente_id_playlist_UN UNIQUE ( CLIENTE_id_cliente , PLAYLIST_id_playlist ) ;

CREATE TABLE LOGRO 
    ( 
     id_logro    INTEGER  NOT NULL , 
     codigo      VARCHAR2 (30 CHAR)  NOT NULL , 
     nombre      VARCHAR2 (80 CHAR)  NOT NULL , 
     descripcion VARCHAR2 (300 CHAR) , 
     icono_url   VARCHAR2 (500 CHAR) , 
     puntos      INTEGER 
    ) 
;

ALTER TABLE LOGRO 
    ADD CONSTRAINT LOGRO_PK PRIMARY KEY ( id_logro ) ;

ALTER TABLE LOGRO 
    ADD CONSTRAINT LOGRO_codigo_UN UNIQUE ( codigo ) ;

CREATE TABLE LOGRO_CLIENTE 
    ( 
     id_logro_cliente   INTEGER  NOT NULL , 
     fecha_obtencion    TIMESTAMP  NOT NULL , 
     CLIENTE_id_cliente INTEGER  NOT NULL , 
     LOGRO_id_logro     INTEGER  NOT NULL 
    ) 
;

ALTER TABLE LOGRO_CLIENTE 
    ADD CONSTRAINT LOGRO_CLIENTE_PK PRIMARY KEY ( id_logro_cliente ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE LOGRO_CLIENTE 
    ADD CONSTRAINT LOGRO_CLIENTE_id_cliente_id_logro_UN UNIQUE ( CLIENTE_id_cliente , LOGRO_id_logro ) ;

CREATE TABLE NOTIFICACION 
    ( 
     id_notificacion    INTEGER  NOT NULL , 
     titulo             VARCHAR2 (100 CHAR)  NOT NULL , 
     mensaje            VARCHAR2 (500 CHAR)  NOT NULL , 
     tipo               VARCHAR2 (20 CHAR) , 
     fecha_envio        TIMESTAMP , 
     leida              CHAR (1 CHAR) , 
     CLIENTE_id_cliente INTEGER  NOT NULL 
    ) 
;

ALTER TABLE NOTIFICACION 
    ADD CONSTRAINT NOTIFICACION_TIPO_CK 
    CHECK (tipo IN ('INFO', 'PROMO', 'RECOMENDACION', 'SISTEMA')) 
;

ALTER TABLE NOTIFICACION 
    ADD CONSTRAINT NOTIFICACION_LEIDA_CK 
    CHECK (leida IN ('N', 'S')) 
;

ALTER TABLE NOTIFICACION 
    ADD CONSTRAINT NOTIFICACION_PK PRIMARY KEY ( id_notificacion ) ;

CREATE TABLE PAGO 
    ( 
     id_pago                    INTEGER  NOT NULL , 
     monto                      NUMBER (10,2)  NOT NULL , 
     fecha_pago                 TIMESTAMP  NOT NULL , 
     metodo_pago                VARCHAR2 (30 CHAR) , 
     estado_pago                VARCHAR2 (15 CHAR) , 
     referencia_externa         VARCHAR2 (60 CHAR) , 
     SUSCRIPCION_id_suscripcion INTEGER  NOT NULL 
    ) 
;

ALTER TABLE PAGO 
    ADD CONSTRAINT PAGO_METODO_PAGO_CK 
    CHECK (metodo_pago IN ('DAVIPLATA', 'NEQUI', 'PAYPAL', 'PSE', 'TARJETA')) 
;

ALTER TABLE PAGO 
    ADD CONSTRAINT PAGO_ESTADO_PAGO_CK 
    CHECK (estado_pago IN ('EXITOSO', 'FALLIDO', 'PENDIENTE', 'REEMBOLSADO')) 
;

ALTER TABLE PAGO 
    ADD CONSTRAINT PAGO_PK PRIMARY KEY ( id_pago ) ;

CREATE TABLE PLAYLIST 
    ( 
     id_playlist        INTEGER  NOT NULL , 
     nombre             VARCHAR2 (30 CHAR)  NOT NULL , 
     descripcion        VARCHAR2 (150 CHAR) , 
     fecha_creacion     TIMESTAMP , 
     publica            CHAR (1 CHAR) , 
     CLIENTE_id_cliente INTEGER  NOT NULL 
    ) 
;

ALTER TABLE PLAYLIST 
    ADD CONSTRAINT PLAYLIST_PUBLICA_CK 
    CHECK (publica IN ('N', 'S')) 
;

ALTER TABLE PLAYLIST 
    ADD CONSTRAINT PLAYLIST_PK PRIMARY KEY ( id_playlist ) ;

CREATE TABLE PODCAST 
    ( 
     id_podcast         INTEGER  NOT NULL , 
     titulo             VARCHAR2 (150 CHAR)  NOT NULL , 
     descripcion        VARCHAR2 (500 CHAR) , 
     categoria          VARCHAR2 (50 CHAR) , 
     portada_url        VARCHAR2 (500 CHAR) , 
     fecha_creacion     DATE , 
     ARTISTA_id_artista INTEGER  NOT NULL 
    ) 
;

ALTER TABLE PODCAST 
    ADD CONSTRAINT PODCAST_PK PRIMARY KEY ( id_podcast ) ;

CREATE TABLE REPRODUCCION 
    ( 
     id_reproduccion    INTEGER  NOT NULL , 
     fecha_hora         TIMESTAMP  NOT NULL , 
     duracion_escuchada INTEGER , 
     CLIENTE_id_cliente INTEGER  NOT NULL , 
     CANCION_id_cancion INTEGER  NOT NULL 
    ) 
;

ALTER TABLE REPRODUCCION 
    ADD CONSTRAINT REPRODUCCION_PK PRIMARY KEY ( id_reproduccion ) ;

CREATE TABLE RESENA 
    ( 
     id_resena          INTEGER  NOT NULL , 
     CLIENTE_id_cliente INTEGER  NOT NULL , 
     tipo_objetivo      VARCHAR2 (10 CHAR)  NOT NULL , 
     id_objetivo        INTEGER  NOT NULL , 
     calificacion       INTEGER  NOT NULL , 
     comentario         VARCHAR2 (1000 CHAR) , 
     fecha_resena       TIMESTAMP 
    ) 
;

ALTER TABLE RESENA 
    ADD CONSTRAINT RESENA_TIPO_OBJETIVO 
    CHECK (tipo_objetivo IN ('ALBUM', 'ARTISTA', 'CANCION')) 
;

ALTER TABLE RESENA 
    ADD CONSTRAINT RESENA_CALIFICACION 
    CHECK (calificacion BETWEEN 1 AND 5) 
;

ALTER TABLE RESENA 
    ADD CONSTRAINT RESENA_PK PRIMARY KEY ( id_resena ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE RESENA 
    ADD CONSTRAINT RESENA_id_cliente_tipo_objetivo_id_objetivo_UN UNIQUE ( CLIENTE_id_cliente , tipo_objetivo , id_objetivo ) ;

CREATE TABLE SEGUIMIENTO 
    ( 
     id_seguimiento     INTEGER  NOT NULL , 
     fecha_seguimiento  DATE  NOT NULL , 
     CLIENTE_id_cliente INTEGER  NOT NULL , 
     ARTISTA_id_artista INTEGER  NOT NULL 
    ) 
;

ALTER TABLE SEGUIMIENTO 
    ADD CONSTRAINT SEGUIMIENTO_PK PRIMARY KEY ( id_seguimiento ) ;

CREATE TABLE SUSCRIPCION 
    ( 
     id_suscripcion     INTEGER  NOT NULL , 
     tipo_plan          VARCHAR2 (20 CHAR)  NOT NULL , 
     precio             NUMBER (10,2)  NOT NULL , 
     fecha_inicio       DATE  NOT NULL , 
     fecha_fin          DATE , 
     estado             VARCHAR2 (10 CHAR)  NOT NULL , 
     CLIENTE_id_cliente INTEGER  NOT NULL 
    ) 
;

ALTER TABLE SUSCRIPCION 
    ADD CONSTRAINT SUSCRIPCION_TIPO_PLAN_CK 
    CHECK (tipo_plan IN ('DUO', 'ESTUDIANTE', 'FAMILIAR', 'FREE', 'INDIVIDUAL')) 
;

ALTER TABLE SUSCRIPCION 
    ADD CONSTRAINT SUSCRIPCION_ESTADO_CK 
    CHECK (estado IN ('ACTIVA', 'CANCELADA', 'PAUSADA', 'VENCIDA')) 
;

ALTER TABLE SUSCRIPCION 
    ADD CONSTRAINT SUSCRIPCION_FECHAS_CK 
    CHECK (fecha_fin IS NULL OR fecha_fin > fecha_inicio)
;
ALTER TABLE SUSCRIPCION 
    ADD CONSTRAINT SUSCRIPCION_PK PRIMARY KEY ( id_suscripcion ) ;

CREATE TABLE VOTO_RESENA 
    ( 
     id_voto_resena     INTEGER  NOT NULL , 
     util               CHAR (1 CHAR)  NOT NULL , 
     fecha_voto         TIMESTAMP , 
     RESENA_id_resena   INTEGER  NOT NULL , 
     CLIENTE_id_cliente INTEGER  NOT NULL 
    ) 
;

ALTER TABLE VOTO_RESENA 
    ADD CONSTRAINT VOTO_RESENA_UTIL 
    CHECK (util IN ('N', 'S')) 
;

ALTER TABLE VOTO_RESENA 
    ADD CONSTRAINT VOTO_RESENA_PK PRIMARY KEY ( id_voto_resena ) ;


--  ERROR: UK name length exceeds maximum allowed length(30) 
ALTER TABLE VOTO_RESENA 
    ADD CONSTRAINT VOTO_RESENA_id_resena_id_cliente_UN UNIQUE ( RESENA_id_resena , CLIENTE_id_cliente ) ;

ALTER TABLE ALBUM 
    ADD CONSTRAINT ALBUM_ARTISTA_FK FOREIGN KEY 
    ( 
     ARTISTA_id_artista
    ) 
    REFERENCES ARTISTA 
    ( 
     id_artista
    ) 
;

ALTER TABLE ARTISTA_GENERO 
    ADD CONSTRAINT ARTISTA_GENERO_ARTISTA_FK FOREIGN KEY 
    ( 
     ARTISTA_id_artista
    ) 
    REFERENCES ARTISTA 
    ( 
     id_artista
    ) 
;

ALTER TABLE ARTISTA_GENERO 
    ADD CONSTRAINT ARTISTA_GENERO_GENERO_FK FOREIGN KEY 
    ( 
     GENERO_id_genero
    ) 
    REFERENCES GENERO 
    ( 
     id_genero
    ) 
;

ALTER TABLE BUSQUEDA 
    ADD CONSTRAINT BUSQUEDA_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE CANCION 
    ADD CONSTRAINT CANCION_ALBUM_FK FOREIGN KEY 
    ( 
     ALBUM_id_album
    ) 
    REFERENCES ALBUM 
    ( 
     id_album
    ) 
;

ALTER TABLE CANCION 
    ADD CONSTRAINT CANCION_GENERO_FK FOREIGN KEY 
    ( 
     GENERO_id_genero
    ) 
    REFERENCES GENERO 
    ( 
     id_genero
    ) 
;

ALTER TABLE CANCION_PLAYLIST 
    ADD CONSTRAINT CANCION_PLAYLIST_CANCION_FK FOREIGN KEY 
    ( 
     CANCION_id_cancion
    ) 
    REFERENCES CANCION 
    ( 
     id_cancion
    ) 
;

ALTER TABLE CANCION_PLAYLIST 
    ADD CONSTRAINT CANCION_PLAYLIST_PLAYLIST_FK FOREIGN KEY 
    ( 
     PLAYLIST_id_playlist
    ) 
    REFERENCES PLAYLIST 
    ( 
     id_playlist
    ) 
;

ALTER TABLE COLABORACION 
    ADD CONSTRAINT COLABORACION_ARTISTA_FK FOREIGN KEY 
    ( 
     ARTISTA_id_artista
    ) 
    REFERENCES ARTISTA 
    ( 
     id_artista
    ) 
;

ALTER TABLE COLABORACION 
    ADD CONSTRAINT COLABORACION_CANCION_FK FOREIGN KEY 
    ( 
     CANCION_id_cancion
    ) 
    REFERENCES CANCION 
    ( 
     id_cancion
    ) 
;

ALTER TABLE DISPOSITIVO 
    ADD CONSTRAINT DISPOSITIVO_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE EPISODIO 
    ADD CONSTRAINT EPISODIO_PODCAST_FK FOREIGN KEY 
    ( 
     PODCAST_id_podcast
    ) 
    REFERENCES PODCAST 
    ( 
     id_podcast
    ) 
;

ALTER TABLE LIKE_ALBUM 
    ADD CONSTRAINT LIKE_ALBUM_ALBUM_FK FOREIGN KEY 
    ( 
     ALBUM_id_album
    ) 
    REFERENCES ALBUM 
    ( 
     id_album
    ) 
;

ALTER TABLE LIKE_ALBUM 
    ADD CONSTRAINT LIKE_ALBUM_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE LIKE_CANCION 
    ADD CONSTRAINT LIKE_CANCION_CANCION_FK FOREIGN KEY 
    ( 
     CANCION_id_cancion
    ) 
    REFERENCES CANCION 
    ( 
     id_cancion
    ) 
;

ALTER TABLE LIKE_CANCION 
    ADD CONSTRAINT LIKE_CANCION_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE LIKE_PLAYLIST 
    ADD CONSTRAINT LIKE_PLAYLIST_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE LIKE_PLAYLIST 
    ADD CONSTRAINT LIKE_PLAYLIST_PLAYLIST_FK FOREIGN KEY 
    ( 
     PLAYLIST_id_playlist
    ) 
    REFERENCES PLAYLIST 
    ( 
     id_playlist
    ) 
;

ALTER TABLE LOGRO_CLIENTE 
    ADD CONSTRAINT LOGRO_CLIENTE_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE LOGRO_CLIENTE 
    ADD CONSTRAINT LOGRO_CLIENTE_LOGRO_FK FOREIGN KEY 
    ( 
     LOGRO_id_logro
    ) 
    REFERENCES LOGRO 
    ( 
     id_logro
    ) 
;

ALTER TABLE NOTIFICACION 
    ADD CONSTRAINT NOTIFICACION_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE PAGO 
    ADD CONSTRAINT PAGO_SUSCRIPCION_FK FOREIGN KEY 
    ( 
     SUSCRIPCION_id_suscripcion
    ) 
    REFERENCES SUSCRIPCION 
    ( 
     id_suscripcion
    ) 
;

ALTER TABLE PLAYLIST 
    ADD CONSTRAINT PLAYLIST_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE PODCAST 
    ADD CONSTRAINT PODCAST_ARTISTA_FK FOREIGN KEY 
    ( 
     ARTISTA_id_artista
    ) 
    REFERENCES ARTISTA 
    ( 
     id_artista
    ) 
;

ALTER TABLE REPRODUCCION 
    ADD CONSTRAINT REPRODUCCION_CANCION_FK FOREIGN KEY 
    ( 
     CANCION_id_cancion
    ) 
    REFERENCES CANCION 
    ( 
     id_cancion
    ) 
;

ALTER TABLE REPRODUCCION 
    ADD CONSTRAINT REPRODUCCION_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE RESENA 
    ADD CONSTRAINT RESENA_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE SEGUIMIENTO 
    ADD CONSTRAINT SEGUIMIENTO_ARTISTA_FK FOREIGN KEY 
    ( 
     ARTISTA_id_artista
    ) 
    REFERENCES ARTISTA 
    ( 
     id_artista
    ) 
;

ALTER TABLE SEGUIMIENTO 
    ADD CONSTRAINT SEGUIMIENTO_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE SUSCRIPCION 
    ADD CONSTRAINT SUSCRIPCION_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE VOTO_RESENA 
    ADD CONSTRAINT VOTO_RESENA_CLIENTE_FK FOREIGN KEY 
    ( 
     CLIENTE_id_cliente
    ) 
    REFERENCES CLIENTE 
    ( 
     id_cliente
    ) 
;

ALTER TABLE VOTO_RESENA 
    ADD CONSTRAINT VOTO_RESENA_RESENA_FK FOREIGN KEY 
    ( 
     RESENA_id_resena
    ) 
    REFERENCES RESENA 
    ( 
     id_resena
    ) 
;
-- ==========================================
-- POLISH MANUAL: CHECKs Tipo C que faltaron
-- ==========================================

ALTER TABLE CANCION
    ADD CONSTRAINT CANCION_DURACION_CK
    CHECK (duracion_seg > 0);

ALTER TABLE EPISODIO
    ADD CONSTRAINT EPISODIO_DURACION_CK
    CHECK (duracion_seg > 0);

ALTER TABLE EPISODIO
    ADD CONSTRAINT EPISODIO_NUMERO_CK
    CHECK (numero_episodio > 0);

ALTER TABLE PAGO
    ADD CONSTRAINT PAGO_MONTO_CK
    CHECK (monto > 0);

-- ==========================================
-- POLISH MANUAL: secuencias para PKs
-- ==========================================

CREATE SEQUENCE seq_album            START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_artista          START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_busqueda         START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_cancion          START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_cancion_playlist START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_cliente          START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_colaboracion     START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_dispositivo      START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_episodio         START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_genero           START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_like_album       START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_like_cancion     START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_like_playlist    START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_logro            START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_logro_cliente    START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_notificacion     START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_pago             START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_playlist         START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_podcast          START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_reproduccion     START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_resena           START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_seguimiento      START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_suscripcion      START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_voto_resena      START WITH 1 INCREMENT BY 1 NOCACHE;



-- ==========================================
-- INDICES en columnas FK
-- ==========================================
-- Oracle NO crea indice automatico en columnas FK (a diferencia de MySQL).
-- Sin indice, los DELETE/UPDATE en la tabla padre lockean la hija completa
-- y los joins por FK hacen full table scan.
--
-- Los UNIQUE/PK compuestos ya crean indice implicito en la TUPLA, pero solo
-- sirven para la columna que es PREFIJO. Por eso aqui agregamos indice solo
-- sobre las columnas FK que NO son prefijo de ningun indice existente.
-- ==========================================

-- ALBUM
CREATE INDEX IDX_ALBUM_ARTISTA          ON ALBUM(ARTISTA_id_artista);
-- ARTISTA_GENERO (PK ya cubre artista; falta genero)
CREATE INDEX IDX_ARTISTA_GENERO_GENERO  ON ARTISTA_GENERO(GENERO_id_genero);
-- BUSQUEDA
CREATE INDEX IDX_BUSQUEDA_CLIENTE       ON BUSQUEDA(CLIENTE_id_cliente);
-- CANCION
CREATE INDEX IDX_CANCION_ALBUM          ON CANCION(ALBUM_id_album);
CREATE INDEX IDX_CANCION_GENERO         ON CANCION(GENERO_id_genero);
-- CANCION_PLAYLIST (UNIQUE(cancion,playlist) ya cubre cancion; falta playlist)
CREATE INDEX IDX_CANCION_PLAYLIST_PLAYLIST ON CANCION_PLAYLIST(PLAYLIST_id_playlist);
-- COLABORACION (UNIQUE(cancion,artista,rol) ya cubre cancion; falta artista)
CREATE INDEX IDX_COLABORACION_ARTISTA   ON COLABORACION(ARTISTA_id_artista);
-- DISPOSITIVO
CREATE INDEX IDX_DISPOSITIVO_CLIENTE    ON DISPOSITIVO(CLIENTE_id_cliente);
-- EPISODIO (UNIQUE(podcast,numero) ya cubre podcast)
-- LIKE_ALBUM (UNIQUE(cliente,album) ya cubre cliente; falta album)
CREATE INDEX IDX_LIKE_ALBUM_ALBUM       ON LIKE_ALBUM(ALBUM_id_album);
-- LIKE_CANCION (UNIQUE(cliente,cancion) ya cubre cliente; falta cancion)
CREATE INDEX IDX_LIKE_CANCION_CANCION   ON LIKE_CANCION(CANCION_id_cancion);
-- LIKE_PLAYLIST (UNIQUE(cliente,playlist) ya cubre cliente; falta playlist)
CREATE INDEX IDX_LIKE_PLAYLIST_PLAYLIST ON LIKE_PLAYLIST(PLAYLIST_id_playlist);
-- LOGRO_CLIENTE (UNIQUE(cliente,logro) ya cubre cliente; falta logro)
CREATE INDEX IDX_LOGRO_CLIENTE_LOGRO    ON LOGRO_CLIENTE(LOGRO_id_logro);
-- NOTIFICACION
CREATE INDEX IDX_NOTIFICACION_CLIENTE   ON NOTIFICACION(CLIENTE_id_cliente);
-- PAGO
CREATE INDEX IDX_PAGO_SUSCRIPCION       ON PAGO(SUSCRIPCION_id_suscripcion);
-- PLAYLIST
CREATE INDEX IDX_PLAYLIST_CLIENTE       ON PLAYLIST(CLIENTE_id_cliente);
-- PODCAST
CREATE INDEX IDX_PODCAST_ARTISTA        ON PODCAST(ARTISTA_id_artista);
-- REPRODUCCION
CREATE INDEX IDX_REPRODUCCION_CLIENTE   ON REPRODUCCION(CLIENTE_id_cliente);
CREATE INDEX IDX_REPRODUCCION_CANCION   ON REPRODUCCION(CANCION_id_cancion);
-- RESENA (UNIQUE(cliente,tipo,id_objetivo) ya cubre cliente)
-- SEGUIMIENTO
CREATE INDEX IDX_SEGUIMIENTO_CLIENTE    ON SEGUIMIENTO(CLIENTE_id_cliente);
CREATE INDEX IDX_SEGUIMIENTO_ARTISTA    ON SEGUIMIENTO(ARTISTA_id_artista);
-- SUSCRIPCION
CREATE INDEX IDX_SUSCRIPCION_CLIENTE    ON SUSCRIPCION(CLIENTE_id_cliente);
-- VOTO_RESENA (UNIQUE(resena,cliente) ya cubre resena; falta cliente)
CREATE INDEX IDX_VOTO_RESENA_CLIENTE    ON VOTO_RESENA(CLIENTE_id_cliente);

COMMIT;
