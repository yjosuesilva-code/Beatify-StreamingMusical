package com.beatify.view.controller;

import com.beatify.dao.AlbumDAO;
import com.beatify.dao.ArtistaDAO;
import com.beatify.dao.CancionDAO;
import com.beatify.dao.GeneroDAO;
import com.beatify.dao.ClienteDAO;
import com.beatify.dao.NotificacionDAO;
import com.beatify.dao.PagoDAO;
import com.beatify.dao.SuscripcionDAO;
import com.beatify.model.Album;
import com.beatify.model.Artista;
import com.beatify.model.Cancion;
import com.beatify.model.Genero;
import com.beatify.model.Cliente;
import com.beatify.model.Notificacion;
import com.beatify.model.Pago;
import com.beatify.model.Suscripcion;
import com.beatify.model.TipoPlan;
import com.beatify.service.AlbumService;
import com.beatify.service.ArtistaService;
import com.beatify.service.CancionService;
import com.beatify.service.GeneroService;
import com.beatify.service.ClienteService;
import com.beatify.service.IClienteService;
import com.beatify.service.INotificacionService;
import com.beatify.service.NotificacionService;
import com.beatify.service.PagoService;
import com.beatify.service.SuscripcionService;
import com.beatify.view.SessionContext;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel de administración (admin.fxml). Solo accesible para cuentas con
 * rol ADMIN (el login enruta aquí). Fase 1: gestión de usuarios (listar y
 * activar/desactivar). Las secciones de catálogo, suscripciones/pagos y
 * notificaciones se irán habilitando.
 */
public class AdminController {

    private static final Logger LOG = Logger.getLogger(AdminController.class.getName());

    @FXML private Label  lblAdminNombre;
    @FXML private Label  lblTituloSeccion;
    @FXML private VBox   contenido;
    @FXML private Button btnSalir;

    private final IClienteService clienteService = new ClienteService(new ClienteDAO());
    private final INotificacionService notificacionService =
            new NotificacionService(new NotificacionDAO());
    private final SuscripcionService suscripcionService =
            new SuscripcionService(new SuscripcionDAO());
    private final PagoService pagoService = new PagoService(new PagoDAO());
    private final ArtistaService artistaService = new ArtistaService(new ArtistaDAO());
    private final AlbumService albumService = new AlbumService(new AlbumDAO());
    private final CancionService cancionService = new CancionService(new CancionDAO());
    private final GeneroService generoService = new GeneroService(new GeneroDAO());

    private static final List<String> TIPOS_NOTIFICACION =
            List.of("INFO", "PROMO", "RECOMENDACION", "SISTEMA");

    @FXML
    private void initialize() {
        final Cliente admin = SessionContext.getInstance().getClienteActual();
        if (admin != null) {
            lblAdminNombre.setText(admin.getNombre() + " " + admin.getApellido() + " · ADMIN");
        }
        mostrarUsuarios();
    }

    // -----------------------------------------------------------------
    // Navegación de secciones
    // -----------------------------------------------------------------

    @FXML private void onUsuarios()       { mostrarUsuarios(); }
    @FXML private void onCatalogo()        { mostrarCatalogoArtistas(); }
    @FXML private void onSuscripciones()   { mostrarSuscripciones(); }
    @FXML private void onNotificaciones()  { mostrarNotificaciones(); }

    @FXML
    private void onSalir() {
        SessionContext.getInstance().cerrarSesion();
        NavegacionUtil.cambiarA("/view/login.fxml", btnSalir);
    }

    // -----------------------------------------------------------------
    // Sección: Usuarios
    // -----------------------------------------------------------------

    private void mostrarUsuarios() {
        lblTituloSeccion.setText("Usuarios");
        contenido.getChildren().clear();
        final List<Cliente> clientes;
        try {
            clientes = clienteService.listar();
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "No se pudieron listar los clientes", ex);
            contenido.getChildren().add(textoInfo("No se pudieron cargar los usuarios: " + ex.getMessage()));
            return;
        }
        contenido.getChildren().add(textoInfo(clientes.size() + " usuarios registrados"));
        for (final Cliente c : clientes) {
            contenido.getChildren().add(filaUsuario(c));
        }
    }

    private HBox filaUsuario(final Cliente c) {
        final VBox info = new VBox(2);
        final Label nombre = new Label(c.getNombre() + " " + c.getApellido());
        nombre.setStyle("-fx-text-fill: white; -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 14px;");
        final Label correo = new Label(c.getCorreo());
        correo.setStyle("-fx-text-fill: #a7a7a7; -fx-font-family: 'Manrope'; -fx-font-size: 12px;");
        info.getChildren().addAll(nombre, correo);
        HBox.setHgrow(info, Priority.ALWAYS);

        final Label rol = new Label(c.getRol() != null ? c.getRol() : "CLIENTE");
        rol.setMinWidth(70);
        rol.setStyle("-fx-text-fill: " + (c.esAdmin() ? "#1ed760" : "#6a6a6a")
                + "; -fx-font-family: 'Manrope'; -fx-font-size: 11px; -fx-font-weight: bold;");

        final boolean activo = Boolean.TRUE.equals(c.getActivo());
        final Label estado = new Label(activo ? "Activo" : "Inactivo");
        estado.setMinWidth(70);
        estado.setStyle("-fx-text-fill: " + (activo ? "#1ed760" : "#e0245e")
                + "; -fx-font-family: 'Manrope'; -fx-font-size: 12px;");

        final HBox fila = new HBox(16, info, rol, estado);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: #181818; -fx-background-radius: 10; -fx-padding: 12 16;");

        if (c.esAdmin()) {
            final Label tu = new Label("(tú)");
            tu.setMinWidth(96);
            tu.setStyle("-fx-text-fill: #6a6a6a; -fx-font-family: 'Manrope'; -fx-font-size: 11px;");
            fila.getChildren().add(tu);
        } else {
            final Button toggle = new Button(activo ? "Desactivar" : "Activar");
            toggle.setMinWidth(96);
            toggle.setStyle("-fx-cursor: hand; -fx-background-radius: 8; -fx-text-fill: white;"
                    + " -fx-font-family: 'Manrope'; -fx-font-size: 12px; -fx-padding: 7 14;"
                    + " -fx-background-color: " + (activo ? "#e0245e" : "#1ed760") + ";");
            toggle.setOnAction(e -> toggleActivo(c));
            fila.getChildren().add(toggle);
        }
        return fila;
    }

    private void toggleActivo(final Cliente c) {
        final boolean nuevoActivo = !Boolean.TRUE.equals(c.getActivo());
        c.setActivo(nuevoActivo);
        try {
            clienteService.actualizar(c);
            mostrarUsuarios();   // refrescar la lista con el nuevo estado
        } catch (final RuntimeException ex) {
            c.setActivo(!nuevoActivo);   // revertir en memoria
            LOG.log(Level.WARNING, "No se pudo cambiar el estado del cliente " + c.getIdCliente(), ex);
            avisar("No se pudo cambiar el estado: " + ex.getMessage());
        }
    }

    // -----------------------------------------------------------------
    // Sección: Notificaciones
    // -----------------------------------------------------------------

    private void mostrarNotificaciones() {
        lblTituloSeccion.setText("Notificaciones");
        contenido.getChildren().clear();

        final List<Cliente> usuarios;
        try {
            usuarios = clienteService.listar().stream().filter(c -> !c.esAdmin()).toList();
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "No se pudieron listar los clientes", ex);
            contenido.getChildren().add(textoInfo("No se pudieron cargar los usuarios: " + ex.getMessage()));
            return;
        }

        final TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Título");
        txtTitulo.setMaxWidth(520);

        final TextArea txtMensaje = new TextArea();
        txtMensaje.setPromptText("Mensaje");
        txtMensaje.setWrapText(true);
        txtMensaje.setPrefRowCount(4);
        txtMensaje.setMaxWidth(520);

        final ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll(TIPOS_NOTIFICACION);
        cmbTipo.setValue("INFO");

        final CheckBox chkTodos = new CheckBox("Enviar a todos los usuarios (" + usuarios.size() + ")");
        chkTodos.setStyle("-fx-text-fill: white; -fx-font-family: 'Manrope';");

        final ComboBox<Cliente> cmbUsuario = new ComboBox<>();
        cmbUsuario.getItems().addAll(usuarios);
        cmbUsuario.setPromptText("Elige un usuario");
        cmbUsuario.setConverter(new StringConverter<>() {
            @Override public String toString(final Cliente c) {
                return c == null ? "" : c.getNombre() + " " + c.getApellido() + " (" + c.getCorreo() + ")";
            }
            @Override public Cliente fromString(final String s) { return null; }
        });
        chkTodos.selectedProperty().addListener((o, a, sel) -> cmbUsuario.setDisable(sel));

        final Label feedback = textoInfo("");

        final Button btnEnviar = new Button("Enviar");
        btnEnviar.setStyle("-fx-cursor: hand; -fx-background-radius: 8; -fx-text-fill: black;"
                + " -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-padding: 9 22;"
                + " -fx-background-color: #1ed760;");
        btnEnviar.setOnAction(e -> enviarNotificacion(
                txtTitulo, txtMensaje, cmbTipo, chkTodos, cmbUsuario, usuarios, feedback));

        contenido.getChildren().addAll(
                campoLabel("Título"),       txtTitulo,
                campoLabel("Mensaje"),      txtMensaje,
                campoLabel("Tipo"),         cmbTipo,
                campoLabel("Destinatario"), chkTodos, cmbUsuario,
                btnEnviar, feedback);
    }

    private void enviarNotificacion(final TextField txtTitulo, final TextArea txtMensaje,
                                    final ComboBox<String> cmbTipo, final CheckBox chkTodos,
                                    final ComboBox<Cliente> cmbUsuario, final List<Cliente> usuarios,
                                    final Label feedback) {
        final String titulo  = txtTitulo.getText() == null ? "" : txtTitulo.getText().trim();
        final String mensaje = txtMensaje.getText() == null ? "" : txtMensaje.getText().trim();
        final String tipo    = cmbTipo.getValue();
        if (titulo.isEmpty() || mensaje.isEmpty()) {
            feedback.setText("⚠ Título y mensaje son obligatorios.");
            return;
        }

        final List<Cliente> destinos;
        if (chkTodos.isSelected()) {
            destinos = usuarios;
        } else if (cmbUsuario.getValue() != null) {
            destinos = List.of(cmbUsuario.getValue());
        } else {
            feedback.setText("⚠ Elige un usuario o marca 'a todos'.");
            return;
        }

        int enviadas = 0;
        for (final Cliente c : destinos) {
            try {
                notificacionService.registrar(new Notificacion(
                        titulo, mensaje, tipo, LocalDateTime.now(), "N", c.getIdCliente()));
                enviadas++;
            } catch (final RuntimeException ex) {
                LOG.log(Level.WARNING, "No se pudo enviar notificación al cliente " + c.getIdCliente(), ex);
            }
        }
        feedback.setText("✓ Enviadas " + enviadas + " de " + destinos.size() + " notificaciones.");
        txtTitulo.clear();
        txtMensaje.clear();
        chkTodos.setSelected(false);
        cmbUsuario.setValue(null);
    }

    // -----------------------------------------------------------------
    // Sección: Suscripciones y pagos
    // -----------------------------------------------------------------

    private void mostrarSuscripciones() {
        lblTituloSeccion.setText("Suscripciones y pagos");
        contenido.getChildren().clear();

        final List<Cliente> usuarios;
        final List<Suscripcion> suscripciones;
        final List<Pago> pagos;
        try {
            usuarios      = clienteService.listar().stream().filter(c -> !c.esAdmin()).toList();
            suscripciones = suscripcionService.listar();
            pagos         = pagoService.listar();
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "No se pudieron cargar suscripciones/pagos", ex);
            contenido.getChildren().add(textoInfo("No se pudieron cargar los datos: " + ex.getMessage()));
            return;
        }

        // Mapa suscripción -> cliente, e ingresos (pagos EXITOSO) por cliente y total.
        final Map<Integer, Integer> suscToCliente = new HashMap<>();
        for (final Suscripcion s : suscripciones) {
            suscToCliente.put(s.getIdSuscripcion(), s.getIdCliente());
        }
        final Map<Integer, Double> pagadoPorCliente = new HashMap<>();
        double totalIngresos = 0;
        for (final Pago p : pagos) {
            if ("EXITOSO".equalsIgnoreCase(p.getEstadoPago()) && p.getMonto() != null) {
                totalIngresos += p.getMonto();
                final Integer cli = suscToCliente.get(p.getIdSuscripcion());
                if (cli != null) {
                    pagadoPorCliente.merge(cli, p.getMonto(), Double::sum);
                }
            }
        }
        final LocalDate hoy = LocalDate.now();
        final long activas = suscripciones.stream()
                .filter(s -> "ACTIVA".equalsIgnoreCase(s.getEstado())
                        && (s.getFechaFin() == null || s.getFechaFin().isAfter(hoy)))
                .count();

        final Label resumen = new Label(String.format(
                "Ingresos (pagos exitosos): $%,.0f   ·   %d suscripciones activas   ·   %d pagos",
                totalIngresos, activas, pagos.size()));
        resumen.setStyle("-fx-text-fill: #1ed760; -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 14px;");
        contenido.getChildren().add(resumen);

        for (final Cliente c : usuarios) {
            contenido.getChildren().add(filaSuscripcion(c, planDe(c.getIdCliente(), suscripciones),
                    pagadoPorCliente.getOrDefault(c.getIdCliente(), 0.0)));
        }
    }

    private HBox filaSuscripcion(final Cliente c, final TipoPlan planActual, final double pagado) {
        final VBox info = new VBox(2);
        final Label nombre = new Label(c.getNombre() + " " + c.getApellido());
        nombre.setStyle("-fx-text-fill: white; -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 14px;");
        final Label correo = new Label(c.getCorreo());
        correo.setStyle("-fx-text-fill: #a7a7a7; -fx-font-family: 'Manrope'; -fx-font-size: 12px;");
        info.getChildren().addAll(nombre, correo);
        HBox.setHgrow(info, Priority.ALWAYS);

        final Label plan = new Label(planActual.getEtiqueta());
        plan.setMinWidth(90);
        plan.setStyle("-fx-text-fill: #1ed760; -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 13px;");

        final Label pagadoLbl = new Label(String.format("Pagado: $%,.0f", pagado));
        pagadoLbl.setMinWidth(120);
        pagadoLbl.setStyle("-fx-text-fill: #a7a7a7; -fx-font-family: 'Manrope'; -fx-font-size: 12px;");

        final ComboBox<TipoPlan> cmbPlan = new ComboBox<>();
        cmbPlan.getItems().addAll(TipoPlan.values());
        cmbPlan.setValue(planActual);
        cmbPlan.setConverter(new StringConverter<>() {
            @Override public String toString(final TipoPlan p) {
                return p == null ? "" : p.getEtiqueta() + " ($" + String.format("%,.0f", p.getPrecioMensual()) + ")";
            }
            @Override public TipoPlan fromString(final String s) { return null; }
        });

        final Button btnCambiar = new Button("Cambiar");
        btnCambiar.setStyle("-fx-cursor: hand; -fx-background-radius: 8; -fx-text-fill: black;"
                + " -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 14;"
                + " -fx-background-color: #1ed760;");
        btnCambiar.setOnAction(e -> cambiarPlanDe(c, cmbPlan.getValue()));

        final HBox fila = new HBox(14, info, pagadoLbl, plan, cmbPlan, btnCambiar);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: #181818; -fx-background-radius: 10; -fx-padding: 12 16;");
        return fila;
    }

    private void cambiarPlanDe(final Cliente c, final TipoPlan nuevo) {
        if (nuevo == null) return;
        try {
            suscripcionService.cambiarPlan(c.getIdCliente(), nuevo);
            mostrarSuscripciones();   // refrescar con el plan nuevo
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "No se pudo cambiar el plan del cliente " + c.getIdCliente(), ex);
            avisar("No se pudo cambiar el plan: " + ex.getMessage());
        }
    }

    /** Plan activo del cliente derivado de la lista de suscripciones ya cargada. */
    private TipoPlan planDe(final Integer idCliente, final List<Suscripcion> suscripciones) {
        final LocalDate hoy = LocalDate.now();
        for (final Suscripcion s : suscripciones) {
            if (idCliente.equals(s.getIdCliente())
                    && "ACTIVA".equalsIgnoreCase(s.getEstado())
                    && (s.getFechaFin() == null || s.getFechaFin().isAfter(hoy))) {
                return TipoPlan.desdeNombreOFree(s.getTipoPlan());
            }
        }
        return TipoPlan.FREE;
    }

    // -----------------------------------------------------------------
    // Sección: Catálogo (Artistas / Álbumes / Canciones)
    // -----------------------------------------------------------------

    /** Barra de sub-navegación del catálogo; resalta la pestaña activa. */
    private HBox subNavCatalogo(final String activa) {
        final HBox barra = new HBox(8);
        barra.getChildren().addAll(
                subNavBtn("Artistas",  "artistas",  activa, this::mostrarCatalogoArtistas),
                subNavBtn("Álbumes",   "albumes",   activa, this::mostrarCatalogoAlbumes),
                subNavBtn("Canciones", "canciones", activa, this::mostrarCatalogoCanciones));
        return barra;
    }

    private Button subNavBtn(final String texto, final String clave, final String activa,
                             final Runnable accion) {
        final boolean on = clave.equals(activa);
        final Button b = new Button(texto);
        b.setStyle("-fx-cursor: hand; -fx-background-radius: 8; -fx-font-family: 'Manrope';"
                + " -fx-font-size: 13px; -fx-padding: 7 16;"
                + (on ? " -fx-background-color: #1ed760; -fx-text-fill: black; -fx-font-weight: bold;"
                      : " -fx-background-color: #232323; -fx-text-fill: white;"));
        b.setOnAction(e -> accion.run());
        return b;
    }

    private void mostrarCatalogoArtistas() {
        lblTituloSeccion.setText("Catálogo");
        contenido.getChildren().clear();
        contenido.getChildren().add(subNavCatalogo("artistas"));

        // --- Form de alta ---
        final TextField txtArtistico = new TextField();
        txtArtistico.setPromptText("Nombre artístico");
        txtArtistico.setMaxWidth(220);
        final TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre real");
        txtNombre.setMaxWidth(200);
        final TextField txtPais = new TextField();
        txtPais.setPromptText("País");
        txtPais.setMaxWidth(140);
        final Button btnAgregar = new Button("Agregar");
        btnAgregar.setStyle("-fx-cursor: hand; -fx-background-radius: 8; -fx-text-fill: black;"
                + " -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-padding: 7 16; -fx-background-color: #1ed760;");
        final HBox form = new HBox(8, txtArtistico, txtNombre, txtPais, btnAgregar);
        form.setAlignment(Pos.CENTER_LEFT);
        btnAgregar.setOnAction(e -> {
            try {
                artistaService.registrar(new Artista(
                        txtNombre.getText(), null, txtArtistico.getText(),
                        null, txtPais.getText(), null, null, null));
                mostrarCatalogoArtistas();   // refrescar
            } catch (final RuntimeException ex) {
                avisar("No se pudo agregar el artista: " + ex.getMessage());
            }
        });
        contenido.getChildren().add(form);

        // --- Lista ---
        final List<Artista> artistas;
        try {
            artistas = artistaService.listar();
        } catch (final RuntimeException ex) {
            contenido.getChildren().add(textoInfo("No se pudieron cargar los artistas: " + ex.getMessage()));
            return;
        }
        contenido.getChildren().add(textoInfo(artistas.size() + " artistas"));
        for (final Artista a : artistas) {
            final String sub = (a.getNombre() != null ? a.getNombre() : "")
                    + (a.getPais() != null ? "  ·  " + a.getPais() : "");
            contenido.getChildren().add(filaCatalogo(
                    a.getNombreArtistico(), sub, () -> eliminarCatalogo(
                            () -> artistaService.eliminar(a.getIdArtista()), this::mostrarCatalogoArtistas)));
        }
    }

    private void mostrarCatalogoAlbumes() {
        lblTituloSeccion.setText("Catálogo");
        contenido.getChildren().clear();
        contenido.getChildren().add(subNavCatalogo("albumes"));

        final List<Artista> artistas;
        final List<Album> albumes;
        try {
            artistas = artistaService.listar();
            albumes  = albumService.listar();
        } catch (final RuntimeException ex) {
            contenido.getChildren().add(textoInfo("No se pudieron cargar los álbumes: " + ex.getMessage()));
            return;
        }
        final Map<Integer, String> artistaNombre = new HashMap<>();
        for (final Artista a : artistas) {
            artistaNombre.put(a.getIdArtista(), a.getNombreArtistico());
        }

        // --- Form de alta ---
        final TextField txtTitulo = new TextField(); txtTitulo.setPromptText("Título"); txtTitulo.setMaxWidth(200);
        final TextField txtAnio   = new TextField(); txtAnio.setPromptText("Año");      txtAnio.setMaxWidth(80);
        final ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("ALBUM", "COMPILACION", "EP", "SINGLE");
        cmbTipo.setValue("ALBUM");
        final ComboBox<Artista> cmbArtista = new ComboBox<>();
        cmbArtista.getItems().addAll(artistas);
        cmbArtista.setPromptText("Artista");
        cmbArtista.setConverter(new StringConverter<>() {
            @Override public String toString(final Artista a) { return a == null ? "" : a.getNombreArtistico(); }
            @Override public Artista fromString(final String s) { return null; }
        });
        final Button btnAgregar = botonVerde("Agregar");
        btnAgregar.setOnAction(e -> agregarAlbum(txtTitulo, txtAnio, cmbTipo, cmbArtista));
        final HBox form = new HBox(8, txtTitulo, txtAnio, cmbTipo, cmbArtista, btnAgregar);
        form.setAlignment(Pos.CENTER_LEFT);
        contenido.getChildren().add(form);

        // --- Lista ---
        contenido.getChildren().add(textoInfo(albumes.size() + " álbumes"));
        for (final Album al : albumes) {
            final String artista = artistaNombre.getOrDefault(al.getIdArtista(), "—");
            final String sub = artista
                    + (al.getAnioLanzamiento() != null ? "  ·  " + al.getAnioLanzamiento() : "")
                    + (al.getTipo() != null ? "  ·  " + al.getTipo() : "");
            contenido.getChildren().add(filaCatalogo(
                    al.getTitulo(), sub, () -> eliminarCatalogo(
                            () -> albumService.eliminar(al.getIdAlbum()), this::mostrarCatalogoAlbumes)));
        }
    }

    private void agregarAlbum(final TextField txtTitulo, final TextField txtAnio,
                              final ComboBox<String> cmbTipo, final ComboBox<Artista> cmbArtista) {
        final Artista artista = cmbArtista.getValue();
        if (artista == null) { avisar("Elige un artista para el álbum."); return; }
        Integer anio = null;
        final String anioTxt = txtAnio.getText() == null ? "" : txtAnio.getText().trim();
        if (!anioTxt.isEmpty()) {
            try {
                anio = Integer.parseInt(anioTxt);
            } catch (final NumberFormatException ex) {
                avisar("El año debe ser un número."); return;
            }
        }
        try {
            albumService.registrar(new Album(
                    txtTitulo.getText(), anio, null, cmbTipo.getValue(), null, null, artista.getIdArtista()));
            mostrarCatalogoAlbumes();
        } catch (final RuntimeException ex) {
            avisar("No se pudo agregar el álbum: " + ex.getMessage());
        }
    }

    private void mostrarCatalogoCanciones() {
        lblTituloSeccion.setText("Catálogo");
        contenido.getChildren().clear();
        contenido.getChildren().add(subNavCatalogo("canciones"));

        final List<Album> albumes;
        final List<Genero> generos;
        final List<Cancion> canciones;
        try {
            albumes   = albumService.listar();
            generos   = generoService.listar();
            canciones = cancionService.listar();
        } catch (final RuntimeException ex) {
            contenido.getChildren().add(textoInfo("No se pudieron cargar las canciones: " + ex.getMessage()));
            return;
        }
        final Map<Integer, String> albumTitulo = new HashMap<>();
        for (final Album al : albumes) {
            albumTitulo.put(al.getIdAlbum(), al.getTitulo());
        }

        // --- Form de alta ---
        final TextField txtTitulo = new TextField(); txtTitulo.setPromptText("Título"); txtTitulo.setMaxWidth(180);
        final TextField txtDur    = new TextField(); txtDur.setPromptText("Seg");       txtDur.setMaxWidth(70);
        final TextField txtRuta   = new TextField(); txtRuta.setPromptText("audio/archivo.mp3"); txtRuta.setMaxWidth(170);
        final ComboBox<Album> cmbAlbum = new ComboBox<>();
        cmbAlbum.getItems().addAll(albumes);
        cmbAlbum.setPromptText("Álbum");
        cmbAlbum.setConverter(new StringConverter<>() {
            @Override public String toString(final Album a) { return a == null ? "" : a.getTitulo(); }
            @Override public Album fromString(final String s) { return null; }
        });
        final ComboBox<Genero> cmbGenero = new ComboBox<>();
        cmbGenero.getItems().addAll(generos);
        cmbGenero.setPromptText("Género");
        cmbGenero.setConverter(new StringConverter<>() {
            @Override public String toString(final Genero g) { return g == null ? "" : g.getNombre(); }
            @Override public Genero fromString(final String s) { return null; }
        });
        final Button btnAgregar = botonVerde("Agregar");
        btnAgregar.setOnAction(e -> agregarCancion(txtTitulo, txtDur, txtRuta, cmbAlbum, cmbGenero));
        final HBox form = new HBox(8, txtTitulo, txtDur, txtRuta, cmbAlbum, cmbGenero, btnAgregar);
        form.setAlignment(Pos.CENTER_LEFT);
        contenido.getChildren().add(form);

        // --- Lista ---
        contenido.getChildren().add(textoInfo(canciones.size() + " canciones"));
        for (final Cancion c : canciones) {
            final String album = albumTitulo.getOrDefault(c.getIdAlbum(), "—");
            final String sub = album + "  ·  " + formatoDuracion(c.getDuracionSegundos());
            contenido.getChildren().add(filaCatalogo(
                    c.getTitulo(), sub, () -> eliminarCatalogo(
                            () -> cancionService.eliminar(c.getIdCancion()), this::mostrarCatalogoCanciones)));
        }
    }

    private void agregarCancion(final TextField txtTitulo, final TextField txtDur, final TextField txtRuta,
                                final ComboBox<Album> cmbAlbum, final ComboBox<Genero> cmbGenero) {
        final Album album = cmbAlbum.getValue();
        final Genero genero = cmbGenero.getValue();
        if (album == null || genero == null) { avisar("Elige álbum y género."); return; }
        final int dur;
        try {
            dur = Integer.parseInt(txtDur.getText() == null ? "" : txtDur.getText().trim());
        } catch (final NumberFormatException ex) {
            avisar("La duración debe ser un número de segundos."); return;
        }
        try {
            cancionService.registrar(new Cancion(
                    txtTitulo.getText(), dur, txtRuta.getText(), null, null, null,
                    album.getIdAlbum(), genero.getIdGenero()));
            mostrarCatalogoCanciones();
        } catch (final RuntimeException ex) {
            avisar("No se pudo agregar la canción: " + ex.getMessage());
        }
    }

    /** Fila genérica del catálogo: título + subtítulo + botón Eliminar. */
    private HBox filaCatalogo(final String titulo, final String subtitulo, final Runnable onEliminar) {
        final VBox info = new VBox(2);
        final Label t = new Label(titulo != null ? titulo : "(sin título)");
        t.setStyle("-fx-text-fill: white; -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 14px;");
        final Label s = new Label(subtitulo);
        s.setStyle("-fx-text-fill: #a7a7a7; -fx-font-family: 'Manrope'; -fx-font-size: 12px;");
        info.getChildren().addAll(t, s);
        HBox.setHgrow(info, Priority.ALWAYS);

        final Button btnEliminar = new Button("Eliminar");
        btnEliminar.setMinWidth(96);
        btnEliminar.setStyle("-fx-cursor: hand; -fx-background-radius: 8; -fx-text-fill: white;"
                + " -fx-font-family: 'Manrope'; -fx-font-size: 12px; -fx-padding: 7 14; -fx-background-color: #e0245e;");
        btnEliminar.setOnAction(e -> onEliminar.run());

        final HBox fila = new HBox(16, info, btnEliminar);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setStyle("-fx-background-color: #181818; -fx-background-radius: 10; -fx-padding: 12 16;");
        return fila;
    }

    private void eliminarCatalogo(final Runnable accionEliminar, final Runnable refrescar) {
        try {
            accionEliminar.run();
            refrescar.run();
        } catch (final RuntimeException ex) {
            LOG.log(Level.WARNING, "No se pudo eliminar el elemento del catálogo", ex);
            avisar("No se pudo eliminar (puede tener elementos asociados): " + ex.getMessage());
        }
    }

    private static String formatoDuracion(final Integer seg) {
        if (seg == null) return "—";
        return String.format("%d:%02d", seg / 60, seg % 60);
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------

    private Label textoInfo(final String texto) {
        final Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #a7a7a7; -fx-font-family: 'Manrope'; -fx-font-size: 13px;");
        return l;
    }

    private Label campoLabel(final String texto) {
        final Label l = new Label(texto);
        l.setStyle("-fx-text-fill: white; -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 12px;");
        return l;
    }

    private Button botonVerde(final String texto) {
        final Button b = new Button(texto);
        b.setStyle("-fx-cursor: hand; -fx-background-radius: 8; -fx-text-fill: black;"
                + " -fx-font-family: 'Manrope'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 7 16;"
                + " -fx-background-color: #1ed760;");
        return b;
    }

    private void avisar(final String mensaje) {
        final Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje);
        alerta.setTitle("Beatify Admin");
        alerta.setHeaderText(null);
        alerta.show();
    }
}
