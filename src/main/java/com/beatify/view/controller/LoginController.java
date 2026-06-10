package com.beatify.view.controller;

import com.beatify.dao.ClienteDAO;
import com.beatify.exceptions.AutenticacionException;
import com.beatify.exceptions.ConexionException;
import com.beatify.model.Cliente;
import com.beatify.service.ClienteService;
import com.beatify.service.IClienteService;
import com.beatify.view.SessionContext;
import com.beatify.view.util.HistorialNavegacion;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.javafx.FontIcon;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller de la pantalla de inicio de sesion (login.fxml).
 *
 * Flujo principal:
 *   1. Usuario ingresa correo y contraseña.
 *   2. Validacion UI (formato correo, campos no vacios) antes de pegarle al service.
 *   3. {@link IClienteService#autenticar(String, String)} verifica credenciales con BCrypt.
 *   4. Si exito: guarda el Cliente en {@link SessionContext} y navega a home.fxml.
 *   5. Si {@link AutenticacionException}: muestra mensaje generico (anti-enumeracion).
 *
 * El boton SSO de Google es un acceso rapido demo: entra a una cuenta del
 * seed sin pedir contraseña (ver {@link #onSSO}).
 * La persistencia del checkbox "mantener sesion" es un bloque backend aparte
 * (por ahora se guarda solo en memoria via SessionContext).
 */
public class LoginController {

    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

    /** Regex basico para validar formato de email en cliente (defensa en profundidad). */
    private static final String CORREO_REGEX =
            "^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";

    // ---- Inputs ----
    @FXML private TextField     txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private TextField     txtPasswordVisible;   // alterna con txtPassword cuando "mostrar"
    @FXML private CheckBox      chkMantenerSesion;

    // ---- Botones ----
    @FXML private Button    btnIngresar;
    @FXML private Button    btnToggleEye;
    @FXML private Button    btnGoogle;
    @FXML private Hyperlink linkRegistro;

    // ---- Icono del ojo (para alternar visibility/visibility-off) ----
    @FXML private FontIcon iconEye;

    // ---- Banner de error ----
    @FXML private VBox  boxError;
    @FXML private Label lblErrorCode;
    @FXML private Label lblErrorMsg;

    /**
     * Service de cliente. Se instancia con DAO concreto.
     * En un proyecto profesional esto vendria por inyeccion de dependencias.
     */
    private final IClienteService clienteService = new ClienteService(new ClienteDAO());

    /** Indica si la contraseña actualmente se muestra en plano. */
    private boolean mostrandoPassword = false;

    @FXML
    private void initialize() {
        // ----- SSO (inicio rápido demo): mapea a una cuenta del seed -----
        btnGoogle.setTooltip(new Tooltip("Entrar con Google (demo)"));
        btnGoogle.setOnAction(e -> onSSO("Google", "yilver.test@unicesar.edu.co"));

        // ----- Enter en cualquier input dispara el submit -----
        txtCorreo.setOnAction(e -> onIngresar());
        txtPassword.setOnAction(e -> onIngresar());
        txtPasswordVisible.setOnAction(e -> onIngresar());

        // ----- Banner de error oculto al inicio -----
        ocultarError();
    }

    /**
     * Inicio de sesión vía proveedor SSO (demo). El proveedor "valida" la
     * identidad y resolvemos el cliente por su correo, sin pedir contraseña.
     */
    private void onSSO(final String proveedor, final String correo) {
        ocultarError();
        try {
            final Cliente cliente = clienteService.iniciarSesionSSO(correo);
            entrar(cliente);
        } catch (final com.beatify.exceptions.NotFoundException e) {
            mostrarError("SSO", "No hay una cuenta vinculada a " + proveedor + " (" + correo + ")");
        } catch (final ConexionException e) {
            LOG.log(Level.SEVERE, "Error de BD en SSO " + proveedor, e);
            mostrarError("ConexionException", "Error de conexión. Intenta de nuevo.");
        } catch (final RuntimeException e) {
            LOG.log(Level.SEVERE, "Error inesperado en SSO " + proveedor, e);
            mostrarError("Error", "No se pudo iniciar sesión con " + proveedor);
        }
    }

    /** Maneja el click de "Iniciar sesion" y la tecla Enter en los inputs. */
    @FXML
    private void onIngresar() {
        final String correo = txtCorreo.getText() == null
                ? "" : txtCorreo.getText().trim().toLowerCase();
        final String pwd = obtenerPasswordActual();

        // Validacion UI
        if (correo.isEmpty() || pwd == null || pwd.isEmpty()) {
            mostrarError("ValidacionException", "Ingresa correo y contraseña");
            return;
        }
        if (!correo.matches(CORREO_REGEX)) {
            mostrarError("ValidacionException", "Formato de correo invalido");
            return;
        }

        // Bloquear UI mientras llamamos al service
        btnIngresar.setDisable(true);
        ocultarError();

        try {
            final Cliente cliente = clienteService.autenticar(correo, pwd);

            // TODO bloque backend: persistir "mantener sesion" si chkMantenerSesion esta seleccionado
            //   - guardar token local en ~/.beatify/session
            //   - leer en BeatifyApp.start() y auto-login si existe

            entrar(cliente);

        } catch (final AutenticacionException e) {
            // Mensaje generico anti-enumeracion (ya viene asi del service)
            mostrarError("AutenticacionException", e.getMessage());

        } catch (final ConexionException e) {
            LOG.log(Level.SEVERE, "Error de BD al autenticar", e);
            mostrarError("ConexionException", "Error de conexion. Intenta de nuevo.");

        } catch (final RuntimeException e) {
            LOG.log(Level.SEVERE, "Error inesperado en login", e);
            mostrarError("Error", "Ocurrio un error inesperado. Revisa el log.");

        } finally {
            btnIngresar.setDisable(false);
        }
    }

    /**
     * Establece la sesión y navega según el rol: los administradores van al
     * panel de admin; los clientes, al home.
     */
    private void entrar(final Cliente cliente) {
        SessionContext.getInstance().setClienteActual(cliente);
        final String destino = cliente.esAdmin() ? "/view/admin.fxml" : "/view/home.fxml";
        final javafx.stage.Stage stage =
                (javafx.stage.Stage) btnIngresar.getScene().getWindow();
        NavegacionUtil.cambiarA(destino, btnIngresar);
        HistorialNavegacion.getInstance().iniciar(stage, destino);
    }

    /** Alterna entre PasswordField (oculto) y TextField (visible). */
    @FXML
    private void onToggleShowPassword() {
        mostrandoPassword = !mostrandoPassword;

        if (mostrandoPassword) {
            // Mostrar en plano
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            iconEye.setIconLiteral("bi-eye-slash");
        } else {
            // Ocultar
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            iconEye.setIconLiteral("bi-eye");
        }
    }

    /** Navega a la pantalla de registro. */
    @FXML
    private void onIrARegistro() {
        NavegacionUtil.cambiarA("/view/registro.fxml", linkRegistro);
    }

    /** Navega a la pantalla de recuperación de contraseña (con verificación por correo). */
    @FXML
    private void onOlvidoPassword() {
        NavegacionUtil.cambiarA("/view/recuperar.fxml", txtCorreo);
    }

    // ----- helpers privados -----

    /** Devuelve el texto del password sin importar cual de los dos inputs esta visible. */
    private String obtenerPasswordActual() {
        return mostrandoPassword
                ? txtPasswordVisible.getText()
                : txtPassword.getText();
    }


    private void mostrarError(final String code, final String msg) {
        lblErrorCode.setVisible(false);
        lblErrorCode.setManaged(false);
        lblErrorMsg.setText(msg);
        boxError.setVisible(true);
        boxError.setManaged(true);
    }

    private void ocultarError() {
        boxError.setVisible(false);
        boxError.setManaged(false);
    }
}
