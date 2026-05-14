-- =====================================================================
-- 03_cache_apis.sql
-- Tablas de cache para APIs externas (MusicBrainz, Last.fm)
-- =====================================================================
-- Proposito: evitar llamadas repetidas a APIs externas guardando los
-- resultados en BD con un TTL (expires_at). Si el cache expiro o no
-- existe, la capa de servicio en Java llama a la API y guarda el
-- resultado aqui.
--
-- IMPORTANTE: Ejecutar conectado como BEATIFY @ XEPDB1, DESPUES de
--             01_schema_beatify.sql.
-- =====================================================================

SET DEFINE OFF;

-- =====================================================================
-- Tabla: CACHE_MUSICBRAINZ_ARTISTA
-- Datos enriquecidos de artistas desde MusicBrainz (libre, sin API key).
-- =====================================================================
DROP TABLE CACHE_MUSICBRAINZ_ARTISTA CASCADE CONSTRAINTS;

CREATE TABLE CACHE_MUSICBRAINZ_ARTISTA (
    id_cache_mb_artista INTEGER             NOT NULL,
    mbid                VARCHAR2(36 CHAR)   NOT NULL,
    ARTISTA_id_artista  INTEGER,
    nombre_completo     VARCHAR2(200 CHAR),
    biografia           VARCHAR2(4000 CHAR),
    pais_origen         VARCHAR2(50 CHAR),
    year_inicio         INTEGER,
    year_fin            INTEGER,
    tags                VARCHAR2(500 CHAR),
    payload_json        CLOB,
    fetched_at          TIMESTAMP           NOT NULL,
    expires_at          TIMESTAMP           NOT NULL,
    intentos            INTEGER             DEFAULT 1
);

ALTER TABLE CACHE_MUSICBRAINZ_ARTISTA
    ADD CONSTRAINT CACHE_MB_ARTISTA_PK PRIMARY KEY (id_cache_mb_artista);

ALTER TABLE CACHE_MUSICBRAINZ_ARTISTA
    ADD CONSTRAINT CACHE_MB_ARTISTA_MBID_UN UNIQUE (mbid);

ALTER TABLE CACHE_MUSICBRAINZ_ARTISTA
    ADD CONSTRAINT CACHE_MB_ARTISTA_ARTISTA_FK FOREIGN KEY (ARTISTA_id_artista)
    REFERENCES ARTISTA (id_artista);

ALTER TABLE CACHE_MUSICBRAINZ_ARTISTA
    ADD CONSTRAINT CACHE_MB_ARTISTA_YEAR_CK
    CHECK (year_inicio IS NULL OR year_inicio BETWEEN 1900 AND 2100);

ALTER TABLE CACHE_MUSICBRAINZ_ARTISTA
    ADD CONSTRAINT CACHE_MB_ARTISTA_YEARFIN_CK
    CHECK (year_fin IS NULL OR year_fin >= year_inicio);

ALTER TABLE CACHE_MUSICBRAINZ_ARTISTA
    ADD CONSTRAINT CACHE_MB_ARTISTA_INTENTOS_CK
    CHECK (intentos > 0);

ALTER TABLE CACHE_MUSICBRAINZ_ARTISTA
    ADD CONSTRAINT CACHE_MB_ARTISTA_TTL_CK
    CHECK (expires_at > fetched_at);

CREATE INDEX IDX_CACHE_MB_EXPIRES   ON CACHE_MUSICBRAINZ_ARTISTA (expires_at);
CREATE INDEX IDX_CACHE_MB_FK        ON CACHE_MUSICBRAINZ_ARTISTA (ARTISTA_id_artista);

CREATE SEQUENCE seq_cache_mb_artista START WITH 1 INCREMENT BY 1 NOCACHE;

-- =====================================================================
-- Tabla: CACHE_LASTFM_ALBUM
-- Metadatos de albumes desde Last.fm (requiere API key).
-- =====================================================================
DROP TABLE CACHE_LASTFM_ALBUM CASCADE CONSTRAINTS;

CREATE TABLE CACHE_LASTFM_ALBUM (
    id_cache_lfm_album  INTEGER             NOT NULL,
    lastfm_key          VARCHAR2(200 CHAR)  NOT NULL,
    ALBUM_id_album      INTEGER,
    mbid                VARCHAR2(36 CHAR),
    titulo              VARCHAR2(200 CHAR),
    artista_nombre      VARCHAR2(100 CHAR),
    url_lastfm          VARCHAR2(500 CHAR),
    portada_url_lastfm  VARCHAR2(500 CHAR),
    listeners           INTEGER,
    playcount           INTEGER,
    tags                VARCHAR2(500 CHAR),
    payload_json        CLOB,
    fetched_at          TIMESTAMP           NOT NULL,
    expires_at          TIMESTAMP           NOT NULL,
    intentos            INTEGER             DEFAULT 1
);

ALTER TABLE CACHE_LASTFM_ALBUM
    ADD CONSTRAINT CACHE_LFM_ALBUM_PK PRIMARY KEY (id_cache_lfm_album);

ALTER TABLE CACHE_LASTFM_ALBUM
    ADD CONSTRAINT CACHE_LFM_ALBUM_KEY_UN UNIQUE (lastfm_key);

ALTER TABLE CACHE_LASTFM_ALBUM
    ADD CONSTRAINT CACHE_LFM_ALBUM_ALBUM_FK FOREIGN KEY (ALBUM_id_album)
    REFERENCES ALBUM (id_album);

ALTER TABLE CACHE_LASTFM_ALBUM
    ADD CONSTRAINT CACHE_LFM_ALBUM_LISTENERS_CK
    CHECK (listeners IS NULL OR listeners >= 0);

ALTER TABLE CACHE_LASTFM_ALBUM
    ADD CONSTRAINT CACHE_LFM_ALBUM_PLAYS_CK
    CHECK (playcount IS NULL OR playcount >= 0);

ALTER TABLE CACHE_LASTFM_ALBUM
    ADD CONSTRAINT CACHE_LFM_ALBUM_INTENTOS_CK
    CHECK (intentos > 0);

ALTER TABLE CACHE_LASTFM_ALBUM
    ADD CONSTRAINT CACHE_LFM_ALBUM_TTL_CK
    CHECK (expires_at > fetched_at);

CREATE INDEX IDX_CACHE_LFM_EXPIRES   ON CACHE_LASTFM_ALBUM (expires_at);
CREATE INDEX IDX_CACHE_LFM_FK        ON CACHE_LASTFM_ALBUM (ALBUM_id_album);

CREATE SEQUENCE seq_cache_lfm_album START WITH 1 INCREMENT BY 1 NOCACHE;

-- =====================================================================
-- Tabla: API_CALL_LOG
-- Registro de llamadas externas para auditoria, rate-limiting y debug.
-- =====================================================================
DROP TABLE API_CALL_LOG CASCADE CONSTRAINTS;

CREATE TABLE API_CALL_LOG (
    id_api_call         INTEGER             NOT NULL,
    api_name            VARCHAR2(20 CHAR)   NOT NULL,
    endpoint            VARCHAR2(200 CHAR),
    metodo_http         VARCHAR2(10 CHAR),
    parametros          VARCHAR2(1000 CHAR),
    codigo_respuesta    INTEGER,
    duracion_ms         INTEGER,
    fecha_llamada       TIMESTAMP           NOT NULL,
    exitoso             CHAR(1 CHAR)        NOT NULL,
    mensaje_error       VARCHAR2(1000 CHAR),
    CLIENTE_id_cliente  INTEGER
);

ALTER TABLE API_CALL_LOG
    ADD CONSTRAINT API_CALL_LOG_PK PRIMARY KEY (id_api_call);

ALTER TABLE API_CALL_LOG
    ADD CONSTRAINT API_CALL_LOG_CLIENTE_FK FOREIGN KEY (CLIENTE_id_cliente)
    REFERENCES CLIENTE (id_cliente);

ALTER TABLE API_CALL_LOG
    ADD CONSTRAINT API_CALL_LOG_API_CK
    CHECK (api_name IN ('MUSICBRAINZ', 'LASTFM'));

ALTER TABLE API_CALL_LOG
    ADD CONSTRAINT API_CALL_LOG_METODO_CK
    CHECK (metodo_http IS NULL OR metodo_http IN ('GET', 'POST'));

ALTER TABLE API_CALL_LOG
    ADD CONSTRAINT API_CALL_LOG_EXITOSO_CK
    CHECK (exitoso IN ('N', 'S'));

ALTER TABLE API_CALL_LOG
    ADD CONSTRAINT API_CALL_LOG_DURACION_CK
    CHECK (duracion_ms IS NULL OR duracion_ms >= 0);

CREATE INDEX IDX_API_CALL_LOG_FECHA ON API_CALL_LOG (fecha_llamada);
CREATE INDEX IDX_API_CALL_LOG_API   ON API_CALL_LOG (api_name);

CREATE SEQUENCE seq_api_call_log START WITH 1 INCREMENT BY 1 NOCACHE;

COMMIT;

-- =====================================================================
-- Verificacion: las 3 tablas y 3 secuencias deben existir
-- =====================================================================
-- SELECT table_name FROM user_tables WHERE table_name LIKE 'CACHE%' OR table_name = 'API_CALL_LOG';
-- SELECT sequence_name FROM user_sequences WHERE sequence_name LIKE 'SEQ_CACHE%' OR sequence_name = 'SEQ_API_CALL_LOG';
