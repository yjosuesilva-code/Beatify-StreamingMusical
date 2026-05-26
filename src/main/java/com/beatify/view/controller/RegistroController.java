package com.beatify.view.controller;

import com.beatify.dao.ClienteDAO;
import com.beatify.exceptions.ConexionException;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Cliente;
import com.beatify.service.ClienteService;
import com.beatify.service.IClienteService;
import com.beatify.view.SessionContext;
import com.beatify.view.util.NavegacionUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller del wizard de registro (registro.fxml).
 *
 * Flujo:
 *   Paso 1: nombre, apellido, correo, password + confirmar (con medidor de fuerza).
 *           Validacion estricta antes de avanzar.
 *   Paso 2: ciudad (default Valledupar), telefono opcional, chips de generos preferidos.
 *           Todo opcional excepto la ciudad que ya tiene default.
 *   Paso 3: selector de plan (FREE/INDIVIDUAL/ESTUDIANTE/DUO/FAMILIAR).
 *           Default FREE.
 *
 * Al "Crear cuenta" llama {@link IClienteService#registrar(Cliente)} y navega al Home.
 * Los generos seleccionados y el plan elegido se guardan en memoria pero su
 * persistencia en BD queda como TODO de bloque backend aparte (tablas
 * CLIENTE_GENERO y SUSCRIPCION).
 */
public class RegistroController {

    private static final Logger LOG = Logger.getLogger(RegistroController.class.getName());

    private static final String CORREO_REGEX =
            "^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";

    private static final List<String> GENEROS_DISPONIBLES = Arrays.asList(
            "Vallenato", "Cumbia", "Champeta", "Bullerengue",
            "Salsa colombiana", "Música andina", "Currulao", "Joropo");

    // ---- Header dinamico ----
    @FXML private Label lblStep, lblTitulo, lblSub;

    // ---- Indicadores del wizard (lado izquierdo) ----
    @FXML private Label lblNum1, lblNum2, lblNum3;

    // ---- Banner de error ----
    @FXML private VBox  boxError;
    @FXML private Label lblErrorCode, lblErrorMsg;

    // ---- Paso 1 ----
    @FXML private VBox          paso1Pane;
    @FXML private TextField     txtNombre, txtApellido, txtCorreo;
    @FXML private PasswordField txtPwd, txtPwd2;
    @FXML private HBox          pwdStrengthBox;
    @FXML private Label         lblFuerza;

    // ---- Paso 2 ----
    @FXML private VBox             paso2Pane;
    @FXML private ComboBox<String> cmbCiudad;
    @FXML private TextField        txtTelefono;
    @FXML private FlowPane         chipsGeneros;

    // ---- Paso 3 ----
    @FXML private VBox         paso3Pane;
    @FXML private ToggleGroup  planGroup;
    @FXML private ToggleButton tbtnFree, tbtnIndividual, tbtnEstudiante, tbtnDuo, tbtnFamiliar;

    // ---- Botones de navegacion ----
    @FXML private Button btnAtras, btnContinuar;

    private final IClienteService clienteService = new ClienteService(new ClienteDAO());

    /** Paso actual (1, 2 o 3). */
    private int pasoActual = 1;

    /** Plan seleccionado en el paso 3 (default FREE). */
    private String planSeleccionado = "FREE";

    /** Generos seleccionados en el paso 2 (orden de seleccion). */
    private final List<String> generosSeleccionados = new ArrayList<>();

    @FXML
    private void initialize() {
        // ----- Default de ciudad -----
        cmbCiudad.setValue("Valledupar");

        // ----- Generar chips de generos (8 ToggleButtons en el FlowPane) -----
        for (final String genero : GENEROS_DISPONIBLES) {
            final ToggleButton chip = new ToggleButton(genero);
            chip.getStyleClass().add("bf-pref-chip");
            chip.setUserData(genero);
            chip.setOnAction(e -> {
                if (chip.isSelected()) {
                    if (!generosSeleccionados.contains(genero)) {
                        generosSeleccionados.add(genero);
                    }
                } else {
                    generosSeleccionados.remove(genero);
                }
            });
            chipsGeneros.getChildren().add(chip);
        }

        // ----- Medidor de fuerza de password -----
        txtPwd.textProperty().addListener((obs, old, val) -> actualizarFuerza(val));

        // ----- Listener del ToggleGroup de planes -----
        planGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            if (val != null && val.getUserData() != null) {
                planSeleccionado = val.getUserData().toString();
            } else if (old != null) {
                // No permitir deseleccionar el plan actual (se queda el ultimo)
                old.setSelected(true);
            }
        });

        // ----- Estado inicial: paso 1 -----
        mostrarPaso(1);
        ocultarError();
    }

    // -----------------------------------------------------------------
    // Navegacion entre pasos
    // -----------------------------------------------------------------

    @FXML
    private void onContinuar() {
        ocultarError();
        if (pasoActual == 1) {
            if (!validarPaso1()) return;
            mostrarPaso(2);
        } else if (pasoActual == 2) {
            // Paso 2 no requiere validacion estricta (ciudad ya tiene default,
            // telefono y generos son opcionales)
            mostrarPaso(3);
        } else {
            registrar();
        }
    }

    @FXML
    private void onAtras() {
        ocultarError();
        if (pasoActual == 1) {
            // En el paso 1, "Atras" significa "Ya tengo cuenta" → volver a Login
            NavegacionUtil.cambiarA("/view/login.fxml", btnAtras);
        } else {
            mostrarPaso(pasoActual - 1);
        }
    }

    private void mostrarPaso(final int n) {
        pasoActual = n;

        // Mostrar el panel correspondiente
        paso1Pane.setVisible(n == 1); paso1Pane.setManaged(n == 1);
        paso2Pane.setVisible(n == 2); paso2Pane.setManaged(n == 2);
        paso3Pane.setVisible(n == 3); paso3Pane.setManaged(n == 3);

        // Actualizar textos del header
        lblStep.setText("PASO " + n + " DE 3");
        switch (n) {
            case 1 -> {
                lblTitulo.setText("Cuéntanos quién eres");
                lblSub.setText("Estos datos quedarán en la tabla CLIENTE.");
                btnAtras.setText("Ya tengo cuenta");
                btnContinuar.setText("Continuar →");
            }
            case 2 -> {
                lblTitulo.setText("¿De dónde escuchas?");
                lblSub.setText("Lo usamos para alimentar tu top regional.");
                btnAtras.setText("← Atrás");
                btnContinuar.setText("Continuar →");
            }
            case 3 -> {
                lblTitulo.setText("Elige tu plan");
                lblSub.setText("Puedes cambiar de plan cuando quieras.");
                btnAtras.setText("← Atrás");
                btnContinuar.setText("Crear cuenta");
            }
            default -> { /* nunca llega aqui */ }
        }

        // Actualizar circulos del wizard (sidebar izquierdo)
        actualizarCirculos(n);
    }

    private void actualizarCirculos(final int n) {
        // Reset y aplicar 'is-active' a los pasos hasta el actual inclusive
        aplicarEstado(lblNum1, n >= 1);
        aplicarEstado(lblNum2, n >= 2);
        aplicarEstado(lblNum3, n >= 3);
    }

    private void aplicarEstado(final Label lbl, final boolean activo) {
        lbl.getStyleClass().removeAll("is-active");
        if (activo) {
            lbl.getStyleClass().add("is-active");
        }
    }

    // -----------------------------------------------------------------
    // Validaciones
    // -----------------------------------------------------------------

    private boolean validarPaso1() {
        final String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        final String apellido = txtApellido.getText() == null ? "" : txtApellido.getText().trim();
        final String correo = txtCorreo.getText() == null ? "" : txtCorreo.getText().trim().toLowerCase();
        final String pwd = txtPwd.getText() == null ? "" : txtPwd.getText();
        final String pwd2 = txtPwd2.getText() == null ? "" : txtPwd2.getText();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarError("ValidacionException", "Nombre y apellido son obligatorios");
            return false;
        }
        if (correo.isEmpty() || !correo.matches(CORREO_REGEX)) {
            mostrarError("ValidacionException", "Correo invalido");
            return false;
        }
        if (pwd.length() < 8) {
            mostrarError("ValidacionException", "La contraseña debe tener al menos 8 caracteres");
            return false;
        }
        if (!pwd.equals(pwd2)) {
            mostrarError("ValidacionException", "Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    // -----------------------------------------------------------------
    // Registro
    // -----------------------------------------------------------------

    private void registrar() {
        final Cliente cliente = new Cliente(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtCorreo.getText().trim().toLowerCase(),
                txtPwd.getText(),                              // sera hasheado por el service
                nullSiBlank(txtTelefono.getText()),
                null,                                          // direccion (no se pide en el wizard)
                cmbCiudad.getValue(),
                "Colombia"                                     // pais hardcoded por ahora
        );

        btnContinuar.setDisable(true);
        try {
            final Integer idCliente = clienteService.registrar(cliente);
            cliente.setIdCliente(idCliente);

            // TODO bloque backend: guardar generosSeleccionados en CLIENTE_GENERO
            //   (lista actual: ver this.generosSeleccionados)
            // TODO bloque backend: crear SUSCRIPCION con planSeleccionado
            //   (valor actual: ver this.planSeleccionado)

            SessionContext.getInstance().setClienteActual(cliente);
            NavegacionUtil.cambiarA("/view/home.fxml", btnContinuar);

        } catch (final ValidacionException e) {
            mostrarError("ValidacionException", e.getMessage());

        } catch (final ConexionException e) {
            // Probablemente violacion UNIQUE de correo, o BD caida
            LOG.log(Level.WARNING, "Error de BD al registrar cliente", e);
            mostrarError("ConexionException",
                    "El correo ya está registrado o hubo un error de conexión.");

        } catch (final RuntimeException e) {
            LOG.log(Level.SEVERE, "Error inesperado al registrar", e);
            mostrarError("Error", "Ocurrio un error inesperado. Revisa el log.");

        } finally {
            btnContinuar.setDisable(false);
        }
    }

    // -----------------------------------------------------------------
    // Medidor de fuerza
    // -----------------------------------------------------------------

    private void actualizarFuerza(final String pwd) {
        final int score = calcularPuntaje(pwd);

        // Las 4 primeras hijos del HBox son las Region de barra (la 5ta es el label)
        for (int i = 0; i < 4; i++) {
            final Region barra = (Region) pwdStrengthBox.getChildren().get(i);
            barra.getStyleClass().remove("is-on");
            if (i < score) {
                barra.getStyleClass().add("is-on");
            }
        }

        lblFuerza.setText(etiquetaFuerza(score));
    }

    private int calcularPuntaje(final String p) {
        if (p == null || p.isEmpty()) return 0;
        int s = 0;
        if (p.length() >= 8) s++;
        if (p.length() >= 12) s++;
        if (p.matches(".*[A-Z].*") && p.matches(".*[a-z].*") && p.matches(".*\\d.*")) s++;
        if (p.matches(".*[^A-Za-z0-9].*")) s++;
        return Math.min(s, 4);
    }

    private String etiquetaFuerza(final int score) {
        return switch (score) {
            case 0    -> "";
            case 1    -> "Débil";
            case 2    -> "Aceptable";
            case 3    -> "Buena";
            case 4    -> "Fuerte";
            default   -> "";
        };
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------

    private static String nullSiBlank(final String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private void mostrarError(final String code, final String msg) {
        lblErrorCode.setText(code);
        lblErrorMsg.setText(msg);
        boxError.setVisible(true);
        boxError.setManaged(true);
    }

    private void ocultarError() {
        boxError.setVisible(false);
        boxError.setManaged(false);
    }
}
