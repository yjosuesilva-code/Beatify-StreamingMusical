package com.beatify.util;

import java.io.*;
import java.nio.file.*;
import java.sql.*;

/**
 * Utilidad de desarrollo: genera un archivo WAV de prueba por cada canción
 * de la BD. Cada canción recibe un tono de frecuencia única (basado en su ID)
 * para poder distinguirlas al reproducir.
 *
 * Uso: mvn exec:java -Dexec.mainClass="com.beatify.util.GenerarAudioPrueba"
 *
 * Los archivos se guardan en src/main/resources/audio/<nombre>.wav
 * El PlayerController los detecta automáticamente como fallback del .mp3.
 */
public class GenerarAudioPrueba {

    private static final int    SAMPLE_RATE  = 22050;   // Hz — suficiente para tono demo
    private static final int    DURACION_SEG = 15;       // segundos por archivo
    private static final int    BITS         = 16;

    // Frecuencias base por artista (una nota de la escala pentatónica)
    private static final double[] FREQS_ARTISTA = {
        261.63,  // C4  — Carlos Vives
        293.66,  // D4  — El Cacique (Diomedes)
        329.63,  // E4  — Shakira
        349.23,  // F4  — Joe Arroyo
        392.00,  // G4  — Silvestre Dangond
        440.00,  // A4  — Bomba Estereo
        493.88,  // B4  — Aterciopelados
        523.25,  // C5  — Juanes
        587.33,  // D5  — Fonseca
        659.25   // E5  — Karol G
    };

    public static void main(String[] args) throws Exception {
        // Directorio de salida
        final Path outDir = Paths.get("src/main/resources/audio");
        Files.createDirectories(outDir);

        final Conexion conn = Conexion.getInstancia();

        final String sql = """
            SELECT c.id_cancion, c.ruta_archivo, al.ARTISTA_id_artista
              FROM CANCION c
              JOIN ALBUM al ON al.id_album = c.ALBUM_id_album
             ORDER BY c.id_cancion""";

        int generados = 0;
        int omitidos  = 0;

        try (Connection con = conn.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                final int    idCancion    = rs.getInt(1);
                final String rutaArchivo  = rs.getString(2);
                final int    idArtista    = rs.getInt(3);

                if (rutaArchivo == null || rutaArchivo.isBlank()) {
                    omitidos++;
                    continue;
                }

                // Nombre del archivo: mismo base que ruta_archivo pero .wav
                final String base     = rutaArchivo.replaceFirst("^audio/", "")
                                                   .replaceAll("\\.mp3$", "");
                final Path   wavPath  = outDir.resolve(base + ".wav");

                if (Files.exists(wavPath)) {
                    System.out.println("  [omite] " + wavPath.getFileName() + " ya existe");
                    omitidos++;
                    continue;
                }

                // Frecuencia: nota base del artista + armónico según id de canción
                final double freqBase  = FREQS_ARTISTA[(idArtista - 1) % FREQS_ARTISTA.length];
                final double freqArmon = freqBase * (1.0 + (idCancion % 5) * 0.25);
                final byte[] wavData   = generarWav(freqBase, freqArmon);

                Files.write(wavPath, wavData);
                System.out.printf("  [OK] %-40s  %.0f Hz%n",
                        wavPath.getFileName(), freqBase);
                generados++;
            }
        }

        System.out.printf("%n=== Generados: %d  Omitidos: %d ===%n", generados, omitidos);
    }

    /**
     * Genera un WAV PCM 16-bit mono con dos frecuencias superpuestas
     * (nota + armónico), fade-in 0.5s y fade-out 1s.
     */
    private static byte[] generarWav(final double freq1, final double freq2) throws IOException {
        final int    totalSamples = SAMPLE_RATE * DURACION_SEG;
        final byte[] pcm          = new byte[totalSamples * 2];   // 16-bit = 2 bytes/sample

        for (int i = 0; i < totalSamples; i++) {
            final double t       = (double) i / SAMPLE_RATE;
            final double fadeIn  = Math.min(1.0, t / 0.5);
            final double fadeOut = Math.min(1.0, (DURACION_SEG - t) / 1.0);
            final double envelope = fadeIn * fadeOut;

            final double sample = envelope * 0.45 * (
                    Math.sin(2 * Math.PI * freq1 * t) +
                    0.5 * Math.sin(2 * Math.PI * freq2 * t));

            final short s = (short) (sample * Short.MAX_VALUE);
            pcm[i * 2]     = (byte) (s & 0xFF);
            pcm[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }

        return ensamblarWav(pcm);
    }

    /** Arma el header WAV estándar (RIFF PCM 16-bit mono). */
    private static byte[] ensamblarWav(final byte[] pcm) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos  = new DataOutputStream(baos);

        final int byteRate   = SAMPLE_RATE * 1 * BITS / 8;
        final int blockAlign = 1 * BITS / 8;
        final int dataSize   = pcm.length;
        final int chunkSize  = 36 + dataSize;

        // RIFF header
        dos.writeBytes("RIFF");
        writeInt32LE(dos, chunkSize);
        dos.writeBytes("WAVE");

        // fmt  subchunk
        dos.writeBytes("fmt ");
        writeInt32LE(dos, 16);            // subchunk size (PCM)
        writeInt16LE(dos, (short) 1);     // audio format: PCM
        writeInt16LE(dos, (short) 1);     // channels: mono
        writeInt32LE(dos, SAMPLE_RATE);
        writeInt32LE(dos, byteRate);
        writeInt16LE(dos, (short) (blockAlign & 0xFFFF));
        writeInt16LE(dos, (short) (BITS & 0xFFFF));

        // data subchunk
        dos.writeBytes("data");
        writeInt32LE(dos, dataSize);
        dos.write(pcm);

        dos.flush();
        return baos.toByteArray();
    }

    private static void writeInt32LE(final DataOutputStream dos, final int v) throws IOException {
        dos.write(v & 0xFF);
        dos.write((v >> 8) & 0xFF);
        dos.write((v >> 16) & 0xFF);
        dos.write((v >> 24) & 0xFF);
    }

    private static void writeInt16LE(final DataOutputStream dos, final short v) throws IOException {
        dos.write(v & 0xFF);
        dos.write((v >> 8) & 0xFF);
    }
}
