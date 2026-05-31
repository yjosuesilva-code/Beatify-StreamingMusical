package com.beatify.view.controller;

import com.beatify.dao.ClienteDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.NotFoundException;
import com.beatify.exceptions.ValidacionException;
import com.beatify.service.ClienteService;
import com.beatify.service.IClienteService;
import com.beatify.util.EmailService;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pantalla de recuperación de contraseña (recuperar.fxml).
 *
 * Flujo:
 *   Paso 1 — el usuario ingresa su correo. Validamos que exista una cuenta y
 *            enviamos un código de 6 dígitos a ESE correo (Gmail SMTP).
 *   Paso 2 — el usuario ingresa el código recibido + la nueva contraseña.
 *            Si el código coincide, se actualiza con BCrypt vía resetearPassword.
 *
 * Así se verifica que quien cambia la contraseña es el dueño del correo.
 */
public class RecuperarController {

    private static final Logger LOG = Logger.getLogger(RecuperarController.class.getName());
    private static final String CORREO_REGEX = "^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";

    @FXML private Label       lblSub;
    @FXML private VBox        boxError, paso1, paso2;
    @FXML private Label       lblErrorCode, lblErrorMsg;
    @FXML private TextField   txtCorreo, txtCodigo;
    @FXML private PasswordField txtNueva, txtConfirma;
    @FXML private Button      btnEnviar, btnReenviar, btnCambiar;
    @FXML private Hyperlink   linkVolver;

    private final IClienteService clienteService = new ClienteService(new ClienteDAO());

    /** Código enviado al correo + correo validado (en memoria, durante el flujo). */
    private String codigoEnviado;
    private String correoValidado;

    @FXML
    private void initialize() {
        ocultarBanner();
    }

    /** Paso 1: valida el correo, envía el código y muestra el paso 2. */
    @FXML
    private void onEnviarCodigo() {
        ocultarBanner();
        final String correo = txtCorreo.getText() == null ? "" : txtCorreo.getText().trim().toLowerCase();
        if (correo.isEmpty() || !correo.matches(CORREO_REGEX)) {
            mostrar("ValidacionException", "Ingresa un correo válido");
            return;
        }

        // 1) Verificar que exista una cuenta con ese correo
        try {
            clienteService.buscarPorCorreo(correo);
        } catch (final NotFoundException e) {
            mostrar("NotFoundException", "No existe una cuenta registrada con ese correo");
            return;
        } catch (final ConexionException e) {
            LOG.log(Level.SEVERE, "Error BD al buscar correo", e);
            mostrar("ConexionException", "Error de conexión. Intenta de nuevo.");
            return;
        }

        // 2) Generar y enviar el código
        final String codigo = String.valueOf(100000 + new SecureRandom().nextInt(900000));
        final boolean enviado;
        try {
            enviado = new EmailService().enviarCodigo(correo, codigo);
        } catch (final RuntimeException ex) {
            mostrar("Email", "No se pudo enviar el código: " + ex.getMessage());
            return;
        }
        if (!enviado) {
            mostrar("Configuración",
                    "No hay un correo emisor configurado (mail.user / mail.password en mail.properties).");
            return;
        }

        // 3) Mostrar el paso 2
        this.codigoEnviado  = codigo;
        this.correoValidado = correo;
        lblSub.setText("Enviamos un código de 6 dígitos a " + correo + ". Ingrésalo y define tu nueva contraseña.");
        paso1.setVisible(false); paso1.setManaged(false);
        paso2.setVisible(true);  paso2.setManaged(true);
        mostrar("Listo", "Código enviado a " + correo);
    }

    /** Paso 2: valida el código + contraseñas y resetea. */
    @FXML
    private void onCambiar() {
        ocultarBanner();
        final String codigo   = txtCodigo.getText() == null ? "" : txtCodigo.getText().trim();
        final String nueva    = txtNueva.getText();
        final String confirma = txtConfirma.getText();

        if (codigoEnviado == null || !codigoEnviado.equals(codigo)) {
            mostrar("Verificación", "El código no es correcto");
            return;
        }
        if (nueva == null || nueva.length() < 6) {
            mostrar("ValidacionException", "La nueva contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (!nueva.equals(confirma)) {
            mostrar("ValidacionException", "Las contraseñas no coinciden");
            return;
        }

        try {
            clienteService.resetearPassword(correoValidado, nueva);
            // Éxito → volver al login
            NavegacionUtil.cambiarA("/view/login.fxml", btnCambiar);
        } catch (final ValidacionException e) {
            mostrar("ValidacionException", e.getMessage());
        } catch (final ConexionException e) {
            LOG.log(Level.SEVERE, "Error BD al resetear", e);
            mostrar("ConexionException", "Error de conexión. Intenta de nuevo.");
        } catch (final RuntimeException e) {
            mostrar("Error", e.getMessage() == null ? "No se pudo actualizar" : e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        NavegacionUtil.cambiarA("/view/login.fxml", linkVolver);
    }

    // ----- helpers -----
    private void mostrar(final String code, final String msg) {
        lblErrorCode.setText(code);
        lblErrorMsg.setText(msg);
        boxError.setVisible(true);
        boxError.setManaged(true);
    }

    private void ocultarBanner() {
        boxError.setVisible(false);
        boxError.setManaged(false);
    }
}
