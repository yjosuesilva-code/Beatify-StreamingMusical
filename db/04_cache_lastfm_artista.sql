-- Schema 04 — Cache de artistas de Last.fm
-- Análoga a CACHE_MUSICBRAINZ_ARTISTA (script 03), pero con campos
-- específicos de Last.fm: listeners, playcount, tags.

CREATE TABLE CACHE_LASTFM_ARTISTA (
                                      id_cache_lfm_artista INTEGER             NOT NULL,
                                      lastfm_key           VARCHAR2(200 CHAR)  NOT NULL,   -- nombre artistico normalizado (lowercase trim)
                                      ARTISTA_id_artista   INTEGER,                         -- FK opcional al ARTISTA local si ya existe
                                      mbid                 VARCHAR2(36 CHAR),               -- enlace con MusicBrainz si la API lo devuelve
                                      nombre_artistico     VARCHAR2(100 CHAR),
                                      biografia            VARCHAR2(4000 CHAR),
                                      foto_url             VARCHAR2(500 CHAR),
                                      url_lastfm           VARCHAR2(500 CHAR),
                                      listeners            INTEGER,
                                      playcount            INTEGER,
                                      tags                 VARCHAR2(500 CHAR),
                                      payload_json         CLOB,                            -- respuesta cruda Last.fm para no perder datos
                                      fetched_at           TIMESTAMP           NOT NULL,
                                      expires_at           TIMESTAMP           NOT NULL,
                                      intentos             INTEGER             DEFAULT 1
);

-- PK
ALTER TABLE CACHE_LASTFM_ARTISTA
    ADD CONSTRAINT CACHE_LFM_ARTISTA_PK PRIMARY KEY (id_cache_lfm_artista);

-- UK por la clave normalizada — un solo cache por nombre
ALTER TABLE CACHE_LASTFM_ARTISTA
    ADD CONSTRAINT CACHE_LFM_ARTISTA_KEY_UN UNIQUE (lastfm_key);

-- FK opcional al ARTISTA local
ALTER TABLE CACHE_LASTFM_ARTISTA
    ADD CONSTRAINT CACHE_LFM_ARTISTA_ARTISTA_FK
        FOREIGN KEY (ARTISTA_id_artista) REFERENCES ARTISTA (id_artista);

-- CHECKs de integridad de dominio
ALTER TABLE CACHE_LASTFM_ARTISTA
    ADD CONSTRAINT CACHE_LFM_ARTISTA_LISTENERS_CK
        CHECK (listeners IS NULL OR listeners >= 0);

ALTER TABLE CACHE_LASTFM_ARTISTA
    ADD CONSTRAINT CACHE_LFM_ARTISTA_PLAYS_CK
        CHECK (playcount IS NULL OR playcount >= 0);

ALTER TABLE CACHE_LASTFM_ARTISTA
    ADD CONSTRAINT CACHE_LFM_ARTISTA_INTENTOS_CK
        CHECK (intentos > 0);

-- TTL: la fecha de expiración siempre debe ser posterior al fetch
ALTER TABLE CACHE_LASTFM_ARTISTA
    ADD CONSTRAINT CACHE_LFM_ARTISTA_TTL_CK
        CHECK (expires_at > fetched_at);

-- Índices
-- expires_at: para eliminarVencidos() y consultas de purga
CREATE INDEX IDX_CACHE_LFM_ART_EXPIRES ON CACHE_LASTFM_ARTISTA (expires_at);
-- FK no es prefijo de PK/UK, por convención backend del proyecto se indexa
CREATE INDEX IDX_CACHE_LFM_ART_FK      ON CACHE_LASTFM_ARTISTA (ARTISTA_id_artista);

-- Secuencia para la PK (patrón del proyecto)
CREATE SEQUENCE seq_cache_lfm_artista START WITH 1 INCREMENT BY 1 NOCACHE;