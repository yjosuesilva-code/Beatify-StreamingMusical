package com.beatify.service;

import com.beatify.api.LastFmClient;
import com.beatify.api.MusicBrainzClient;
import com.beatify.api.dto.AlbumApiDTO;
import com.beatify.api.dto.ArtistaApiDTO;
import com.beatify.dao.AlbumDAO;
import com.beatify.dao.ArtistaDAO;
import com.beatify.dao.ArtistaGeneroDAO;
import com.beatify.dao.GeneroDAO;
import com.beatify.exceptions.ApiException;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Album;
import com.beatify.model.Artista;
import com.beatify.model.Genero;

import java.util.List;

public class EnriquecimientoService implements IEnriquecimientoService {

    private final LastFmClient lastFmClient;
    private final MusicBrainzClient musicBrainzClient;
    private final ArtistaDAO artistaDAO;
    private final AlbumDAO albumDAO;
    private final GeneroDAO generoDAO;
    private final ArtistaGeneroDAO artistaGeneroDAO;

    public EnriquecimientoService(LastFmClient lastFmClient,
                                  MusicBrainzClient musicBrainzClient,
                                  ArtistaDAO artistaDAO,
                                  AlbumDAO albumDAO,
                                  GeneroDAO generoDAO,
                                  ArtistaGeneroDAO artistaGeneroDAO) {
        this.lastFmClient      = lastFmClient;
        this.musicBrainzClient = musicBrainzClient;
        this.artistaDAO        = artistaDAO;
        this.albumDAO          = albumDAO;
        this.generoDAO         = generoDAO;
        this.artistaGeneroDAO  = artistaGeneroDAO;
    }

    @Override
    public Artista enriquecerArtista(String nombreArtistico) {
        if (nombreArtistico == null || nombreArtistico.isBlank()) {
            throw new ValidacionException("El nombre artistico es obligatorio");
        }

        // --- Llamar Last.fm (bio, foto, generos, similares) ---
        ArtistaApiDTO dtoLastFm = null;
        try {
            dtoLastFm = lastFmClient.buscarArtista(nombreArtistico);
        } catch (ApiException e) {
            System.err.println("[EnriquecimientoService] Last.fm fallo para artista '"
                    + nombreArtistico + "': " + e.getMessage());
        }

        // --- Llamar MusicBrainz (pais de origen) ---
        ArtistaApiDTO dtoMB = null;
        try {
            dtoMB = musicBrainzClient.buscarArtista(nombreArtistico);
        } catch (ApiException e) {
            System.err.println("[EnriquecimientoService] MusicBrainz fallo para artista '"
                    + nombreArtistico + "': " + e.getMessage());
        }

        if (dtoLastFm == null && dtoMB == null) {
            throw new ApiException(
                    "Ninguna API devolvio datos para el artista: " + nombreArtistico);
        }

        // --- Fusionar datos ---
        Artista artista = new Artista();
        artista.setNombreArtistico(nombreArtistico);

        // NOMBRE y APELLIDO son NOT NULL en BD.
        // La API no entrega nombre/apellido separados, asi que dividimos
        // el nombre artistico: primera palabra = nombre, resto = apellido.
        // Ej: "Carlos Vives" -> nombre="Carlos", apellido="Vives"
        //     "Shakira"      -> nombre="Shakira", apellido="-"
        String[] partes = nombreArtistico.trim().split("\\s+", 2);
        artista.setNombre(partes[0]);
        artista.setApellido(partes.length > 1 ? partes[1] : "-");

        if (dtoLastFm != null) {
            artista.setBiografia(dtoLastFm.biografia());
            artista.setFotoUrl(dtoLastFm.fotoUrl());
        }
        if (dtoMB != null && dtoMB.pais() != null) {
            artista.setPais(dtoMB.pais());
        }

        // --- Persistir artista (evitar duplicados) ---
        Artista existente = artistaDAO.buscarPorNombreArtistico(nombreArtistico);
        if (existente != null) {
            return existente;
        }
        Integer idArtista = artistaDAO.insertar(artista);
        artista.setIdArtista(idArtista);

        // --- Vincular generos (solo si Last.fm devolvio tags) ---
        if (dtoLastFm != null && dtoLastFm.generos() != null) {
            vincularGenerosArtista(idArtista, dtoLastFm.generos());
        }

        return artista;
    }

    /**
     * Busca el album en ambas APIs, fusiona los datos y guarda en BD.
     * Last.fm aporta portada y descripcion; MusicBrainz aporta año, sello y tipo.
     */
    @Override
    public Album enriquecerAlbum(String nombreArtista, String tituloAlbum, Integer idArtista) {
        if (nombreArtista == null || nombreArtista.isBlank()) {
            throw new ValidacionException("El nombre del artista es obligatorio");
        }
        if (tituloAlbum == null || tituloAlbum.isBlank()) {
            throw new ValidacionException("El titulo del album es obligatorio");
        }
        if (idArtista == null || idArtista <= 0) {
            throw new ValidacionException("El id del artista debe ser un entero positivo");
        }

        // --- Llamar Last.fm (portada, descripcion) ---
        AlbumApiDTO dtoLastFm = null;
        try {
            dtoLastFm = lastFmClient.buscarAlbum(nombreArtista, tituloAlbum);
        } catch (ApiException e) {
            System.err.println("[EnriquecimientoService] Last.fm fallo para album '"
                    + tituloAlbum + "': " + e.getMessage());
        }

        // --- Llamar MusicBrainz (año, sello, tipo) ---
        AlbumApiDTO dtoMB = null;
        try {
            dtoMB = musicBrainzClient.buscarAlbum(nombreArtista, tituloAlbum);
        } catch (ApiException e) {
            System.err.println("[EnriquecimientoService] MusicBrainz fallo para album '"
                    + tituloAlbum + "': " + e.getMessage());
        }

        if (dtoLastFm == null && dtoMB == null) {
            throw new ApiException(
                    "Ninguna API devolvio datos para el album: " + tituloAlbum);
        }

        // --- Fusionar datos ---
        Album album = new Album();
        album.setTitulo(tituloAlbum);
        album.setIdArtista(idArtista);

        if (dtoLastFm != null) {
            album.setPortadaUrl(dtoLastFm.portadaUrl());
            album.setDescripcion(dtoLastFm.descripcion());
        }
        if (dtoMB != null) {
            album.setAnioLanzamiento(dtoMB.anioLanzamiento());
            album.setSelloDiscografico(dtoMB.selloDiscografico());
            album.setTipo(mapearTipoAlbum(dtoMB.tipo()));
        }

        // --- Persistir album ---
        Integer idAlbum = albumDAO.insertar(album);
        album.setIdAlbum(idAlbum);

        return album;
    }

    /**
     * Busca un genero por nombre en la BD. Si no existe, lo crea.
     * Usa comparacion case-insensitive para evitar duplicados como
     * "Pop" y "pop".
     */
    @Override
    public Genero buscarOCrearGenero(String nombreGenero) {
        if (nombreGenero == null || nombreGenero.isBlank()) {
            throw new ValidacionException("El nombre del genero es obligatorio");
        }

        // Normalizar: primera letra mayuscula, resto minuscula
        String nombreNormalizado = nombreGenero.trim();
        nombreNormalizado = Character.toUpperCase(nombreNormalizado.charAt(0))
                + nombreNormalizado.substring(1).toLowerCase();

        Genero existente = generoDAO.buscarPorNombre(nombreNormalizado);
        if (existente != null) {
            return existente;
        }

        // No existe — crearlo con datos minimos
        Genero nuevo = new Genero(nombreNormalizado, null, null);
        Integer id = generoDAO.insertar(nuevo);
        nuevo.setIdGenero(id);
        return nuevo;
    }


    /**
     * Para cada genero de la lista: busca o crea en BD, luego vincula
     * con el artista via ARTISTA_GENERO. Ignora errores individuales
     * para no interrumpir el proceso por un tag invalido.
     */
    /**
     * Convierte el tipo de release de MusicBrainz al valor permitido
     * por el CHECK constraint ALBUM_TIPO_CK de la BD.
     * Valores validos: 'ALBUM', 'COMPILACION', 'EP', 'SINGLE'.
     * Si el tipo no tiene equivalente conocido, retorna null.
     */
    private String mapearTipoAlbum(String tipoMusicBrainz) {
        if (tipoMusicBrainz == null) return null;
        return switch (tipoMusicBrainz.trim().toUpperCase()) {
            case "ALBUM"       -> "ALBUM";
            case "SINGLE"      -> "SINGLE";
            case "EP"          -> "EP";
            case "COMPILATION" -> "COMPILACION";
            default            -> null; // tipo desconocido: dejamos null
        };
    }

    private void vincularGenerosArtista(Integer idArtista, List<String> nombresGeneros) {
        for (String nombreGenero : nombresGeneros) {
            try {
                Genero genero = buscarOCrearGenero(nombreGenero);
                artistaGeneroDAO.insertar(idArtista, genero.getIdGenero());
            } catch (Exception e) {
                System.err.println("[EnriquecimientoService] No se pudo vincular genero '"
                        + nombreGenero + "' al artista " + idArtista + ": " + e.getMessage());
            }
        }
    }
}
