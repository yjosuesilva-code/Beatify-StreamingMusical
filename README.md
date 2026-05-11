# Beatify

> Plataforma de streaming musical de escritorio enfocada en música regional colombiana.

**Universidad Popular del Cesar — Programación de Computadores III (SS462)**
Docente: Ing. Esp. Alfredo Bautista

## Equipo
- Yilver Josué Silva Ospino — Líder técnico de Base de Datos y DAOs
- Andrés Felipe Zabaleta Díaz — Modelos de dominio y lógica de servicio
- Kendrick Javier Sayago Rincones — Vista Swing e integración

## Stack tecnológico
- Java 21 (LTS) + Maven
- Java Swing (interfaz gráfica)
- Oracle Database XE 18c (esquema BEATIFY)
- JDBC `ojdbc11` 23.3
- BCrypt para hash de contraseñas
- JLayer + MP3SPI para reproducción de audio

## Arquitectura
Cuatro capas funcionales + paquete transversal de excepciones:
![img.png](img.png)


## Configuración local

1. Tener instalado Oracle Database XE 18c con el PDB `XEPDB1`.
2. Ejecutar los scripts SQL en orden:
    - `00_setup_user.sql` (como SYSTEM) — crea el usuario BEATIFY
    - `01_schema_beatify.sql` (como BEATIFY) — crea las 25 tablas
3. Configurar `src/main/resources/db.properties` con tus credenciales.
4. Compilar y ejecutar:
```bash
   mvn clean package
   java -jar target/BeatifyApp-1.0-SNAPSHOT.jar
```

## Funcionalidades distintivas
- **Reseñas con calificación** (estilo Letterboxd): estrellas 1-5 + comentario.
- **Logros / Achievements**: gamificación del descubrimiento musical.
- **Cápsulas del tiempo**: vista cronológica de tu historial de escucha.
- **Música del barrio**: top regional según la ciudad del cliente.

## Estado del proyecto
- ✅ Fase 1: Documentación (problema, objetivos, requerimientos)
- ✅ Fase 2: Arquitectura (paquetes, clases, mockups, GRASP/SOLID)
- 🔄 Fase 3: Desarrollo (en curso)
- ⏳ Fase 4: Documentación final