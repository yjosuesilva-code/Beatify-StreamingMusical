# Scripts SQL — Beatify

Esta carpeta contiene los scripts DDL y DML para levantar la base de datos del proyecto Beatify en **Oracle Database XE 18c**.

## Requisitos

- Oracle Database XE 18c instalada y corriendo (listener en `localhost:1521`).
- Pluggable Database `XEPDB1` accesible.
- Oracle SQL Developer (o cualquier cliente SQL).
- Acceso al usuario `SYSTEM` para el script `00_setup_user.sql`.

## Orden de ejecución

Los scripts deben correrse en orden numérico. Cada uno asume que el anterior se ejecutó correctamente.

| # | Script | Conectado como | Qué hace |
|---|---|---|---|
| 00 | `00_setup_user.sql` | `SYSTEM @ XEPDB1` | Crea el usuario `BEATIFY` con los privilegios mínimos necesarios. |
| 01 | `01_schema_beatify.sql` | `BEATIFY @ XEPDB1` | Crea las 25 tablas, secuencias y constraints del modelo. |
| 02 | `02_seed_data.sql` | `BEATIFY @ XEPDB1` | (Pendiente) Inserta datos de prueba: géneros, artistas, álbumes, canciones, clientes. |

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
7. (Cuando exista) Ejecutar `02_seed_data.sql` para poblar la BD con datos de prueba.

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