# Scripts SQL — Beatify

Esta carpeta contiene los scripts DDL y DML para levantar la base de datos del proyecto Beatify en **Oracle Database XE 18c**.

## Requisitos

- Oracle Database XE 18c instalada y corriendo (listener en `localhost:1521`).
- Pluggable Database `XEPDB1` accesible.
- Oracle SQL Developer (o cualquier cliente SQL).
- Acceso al usuario `SYSTEM` para el script `00_setup_user.sql`.

## Orden de ejecución

Los scripts deben correrse en orden numérico. Cada uno asume que el anterior se ejecutó correctamente.

Todos los scripts (excepto el 00) se ejecutan conectado como `BEATIFY @ XEPDB1`.

| # | Script | Qué hace |
|---|---|---|
| 00 | `00_setup_user.sql` | Crea el usuario `BEATIFY` con los privilegios mínimos (conectado como `SYSTEM @ XEPDB1`). |
| 01 | `01_schema_beatify.sql` | Crea las 25 tablas, secuencias y constraints del modelo. |
| 02 | `02_seed_data.sql` | Inserta datos de prueba: géneros, artistas, álbumes, canciones, clientes, suscripciones, pagos. |
| 03 | `03_cache_apis.sql` | Tablas de caché para APIs externas (MusicBrainz, Last.fm) y log de llamadas. |
| 04 | `04_cache_lastfm_artista.sql` | Tabla de caché de artistas de Last.fm. |
| 05 | `05_plsql_resena.sql` | Lógica PL/SQL de reseñas. |
| 06 | `06_sp_limpiar_cache.sql` | Procedimiento para limpiar la caché de APIs. |
| 07 | `07_sp_reset_datos.sql` | Procedimiento para resetear los datos de prueba. |
| 08 | `08_pkg_resenas.sql` | Paquete `PKG_RESENAS`. |
| 09 | `09_alter_cliente_activo.sql` | Agrega la columna `activo` a `CLIENTE` (soft-delete). |
| 10 | `10_pkg_cliente.sql` | Paquete `PKG_CLIENTE`. |
| 11 | `11_pkg_suscripcion.sql` | Paquete `PKG_SUSCRIPCION` (activar / cancelar / tiene_activa). |
| 12 | `12_pkg_reproduccion.sql` | Paquete `PKG_REPRODUCCION`. |
| 13 | `13_pkg_logros.sql` | Paquete `PKG_LOGROS`. |
| 14 | `14_cliente_genero.sql` | Tabla `CLIENTE_GENERO` (preferencias de género por cliente). |
| 15 | `15_canciones_extra.sql` | Canciones adicionales para la demo. |
| 16 | `16_alter_cliente_rol.sql` | **Agrega la columna `rol` a `CLIENTE` (`CLIENTE`/`ADMIN`) y crea la cuenta admin.** |
| 16 | `16_fix_encoding.sql` | Corrige el doble-encoding UTF-8 en los datos demo (idempotente). |
| 17 | `17_audio_fallback.sql` | Apunta las canciones sin MP3 propio a otra del mismo artista (solo demo). |

> ⚠️ **Crítico tras integrar `dev`:** el script **`16_alter_cliente_rol.sql` es obligatorio**. El código ya hace `SELECT ... rol` sobre `CLIENTE`, así que **si no se corre, fallan todos los logins**. También son nuevos `14`, `15`, `16_fix_encoding` y `17`. Si clonas/actualizas la BD, corre los scripts que te falten en orden.
>
> 🎵 Los archivos de audio (`src/main/resources/audio/*.mp3`, ~380 MB) **no están versionados** (ver `.gitignore`). Sin ellos el reproductor cae a simulación; `17_audio_fallback.sql` ayuda a que la demo suene reusando los pocos MP3 disponibles.
## Procedimiento de despliegue

1. Abrir **SQL Developer** y crear conexión `SYSTEM @ XEPDB1`.
2. Abrir `00_setup_user.sql`, ejecutarlo completo (F5). Esto crea el usuario `BEATIFY/beatify123`.
3. Crear una nueva conexión `BEATIFY @ XEPDB1` con esas credenciales.
4. Conectarse con la nueva conexión, abrir `01_schema_beatify.sql` y ejecutarlo completo (F5).
5. Verificar que se crearon 25 tablas:
```sql
   SELECT COUNT(*) FROM user_tables;
   -- Esperado: 25
```
6. Verificar que se crearon 24 secuencias:
```sql
   SELECT COUNT(*) FROM user_sequences;
   -- Esperado: 24
```
7. Ejecutar `02_seed_data.sql` para poblar la BD con datos de prueba.
8. Ejecutar en orden el resto de scripts (`03` … `17`) según la tabla de arriba. No te saltes `16_alter_cliente_rol.sql` o el login dejará de funcionar.

## Credenciales (solo para entorno de desarrollo)

Host:      localhost
Puerto:    1521
Service:   XEPDB1
Usuario:   BEATIFY
Password:  beatify123

> ⚠️ Estas credenciales están **versionadas a propósito** porque es un proyecto académico local. **NUNCA** usar estas mismas en un entorno real.

## Conexión desde la aplicación Java

La aplicación lee estas credenciales desde `src/main/resources/db.properties`. El Singleton `com.beatify.util.Conexion` se encarga de levantar la conexión JDBC con `ojdbc11`.

## Soporte

Si los scripts fallan, verificar:
- Que el servicio de Oracle XE esté arriba (`lsnrctl status` o servicios de Windows).
- Que el PDB `XEPDB1` esté abierto (`ALTER PLUGGABLE DATABASE XEPDB1 OPEN;` como `SYSTEM`).
- Que el usuario `BEATIFY` no exista previamente (si existe, `DROP USER BEATIFY CASCADE;` y volver a correr `00_setup_user.sql`).