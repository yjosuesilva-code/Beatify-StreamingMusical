package com.beatify.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio de envío de correos por SMTP (Gmail) para la verificación de registro.
 * Lee la configuración de {@code /mail.properties}. Si no hay usuario/clave
 * configurados, {@link #estaConfigurado()} devuelve false y la app usa un
 * respaldo (mostrar el código en pantalla).
 */
public final class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());

    private final String host;
    private final String port;
    private final String user;
    private final String password;
    private final String from;

    public EmailService() {
        final Properties p = new Properties();
        try (InputStream in = EmailService.class.getResourceAsStream("/mail.properties")) {
            if (in != null) p.load(in);
        } catch (final Exception ex) {
            LOG.log(Level.WARNING, "No se pudo leer mail.properties", ex);
        }
        this.host     = p.getProperty("mail.smtp.host", "smtp.gmail.com");
        this.port     = p.getProperty("mail.smtp.port", "587");
        this.user     = p.getProperty("mail.user", "").trim();
        this.password = p.getProperty("mail.password", "").trim();
        this.from     = p.getProperty("mail.from", "Beatify <no-reply@beatify.com>");
    }

    /** @return true si hay credenciales SMTP para enviar de verdad. */
    public boolean estaConfigurado() {
        return !user.isBlank() && !password.isBlank();
    }

    /**
     * Envía el código de verificación al destinatario.
     * @return true si se envió; false si no hay config (usar respaldo).
     * @throws RuntimeException si hay config pero el envío falla (credenciales malas, sin red…).
     */
    public boolean enviarCodigo(final String destino, final String codigo) {
        if (!estaConfigurado()) {
            LOG.info("SMTP sin configurar — se omite envío (modo respaldo).");
            return false;
        }

        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        final Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            final MimeMessage msg = new MimeMessage(session);
            msg.setFrom(InternetAddress.parse(from)[0]);   // from ya viene como "Beatify <correo>"
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            msg.setSubject("Tu código de verificación de Beatify");
            msg.setContent(cuerpoHtml(codigo), "text/html; charset=UTF-8");
            Transport.send(msg);
            LOG.info("Código de verificación enviado a " + destino);
            return true;
        } catch (final Exception ex) {
            LOG.log(Level.SEVERE, "Fallo al enviar correo a " + destino, ex);
            throw new RuntimeException("No se pudo enviar el correo: " + ex.getMessage(), ex);
        }
    }

    private static String cuerpoHtml(final String codigo) {
        return """
            <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;
                        background:#121212;color:#ffffff;padding:32px;border-radius:12px;">
              <h1 style="color:#1ed760;margin:0 0 8px;">Beatify</h1>
              <p style="color:#b3b3b3;margin:0 0 24px;">Verifica tu correo para crear tu cuenta.</p>
              <p style="color:#b3b3b3;margin:0 0 8px;">Tu código de verificación es:</p>
              <div style="font-size:36px;font-weight:bold;letter-spacing:8px;color:#1ed760;
                          background:#000;padding:16px;border-radius:8px;text-align:center;">
                %s
              </div>
              <p style="color:#6a6a6a;font-size:12px;margin:24px 0 0;">
                Si no solicitaste esto, ignora este correo. El código expira en unos minutos.
              </p>
            </div>
            """.formatted(codigo);
    }
}
