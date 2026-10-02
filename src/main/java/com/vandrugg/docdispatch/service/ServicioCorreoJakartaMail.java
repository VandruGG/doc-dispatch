package com.vandrugg.docdispatch.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import com.vandrugg.docdispatch.enums.EstadoEnvio;
import com.vandrugg.docdispatch.model.ConfiguracionCorreo;
import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;
import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.SolicitudCorreo;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class ServicioCorreoJakartaMail implements ServicioCorreo {

    private final ConfiguracionCorreo configuracion;

    public ServicioCorreoJakartaMail(ConfiguracionCorreo configuracion) {
        if (configuracion == null) {
            throw new IllegalArgumentException(
                    "La configuracion de correo es obligatoria.");
        }

        this.configuracion = configuracion;
    }

    @Override
    public ResultadoEnvio enviar(SolicitudCorreo solicitud) {

        if (solicitud == null) {
            return new ResultadoEnvio(
                    EstadoEnvio.ERROR,
                    "La solicitud de correo es obligatoria.");
        }

        if (solicitud.destinatarios() == null
                || solicitud.destinatarios().isEmpty()) {
            return new ResultadoEnvio(
                    EstadoEnvio.SIN_DESTINATARIO,
                    "No existen destinatarios para realizar el envio.");
        }

        if (solicitud.adjuntos() == null
                || solicitud.adjuntos().isEmpty()) {
            return new ResultadoEnvio(
                    EstadoEnvio.ARCHIVO_INVALIDO,
                    "No existen archivos adjuntos para enviar.");
        }

        for (Path adjunto : solicitud.adjuntos()) {
            File archivo = adjunto.toFile();

            if (!archivo.exists() || !archivo.isFile())
                return new ResultadoEnvio(
                        EstadoEnvio.ARCHIVO_INVALIDO,
                        "No se encontro el archivo: " + adjunto);
        }

        try {
            Session session = Session.getInstance(crearPropiedades());
            MimeMessage mensaje = new MimeMessage(session);
            mensaje.setFrom(
                    new InternetAddress(
                            configuracion.usuario()));

            for (String destinatario : solicitud.destinatarios()) {
                mensaje.addRecipient(
                        Message.RecipientType.TO,
                        new InternetAddress(destinatario));
            }

            mensaje.setSubject(
                    solicitud.asunto(),
                    "UTF-8");

            MimeMultipart contenido = new MimeMultipart();
            BodyPart texto = new MimeBodyPart();

            texto.setText(
                    solicitud.cuerpo()
                    );

            contenido.addBodyPart(texto);

            for (Path adjunto : solicitud.adjuntos()) {
                MimeBodyPart archivoAdjunto = new MimeBodyPart();
                archivoAdjunto.attachFile(adjunto.toFile());
                contenido.addBodyPart(archivoAdjunto);
            }

            mensaje.setContent(contenido);

            try (Transport transport = session.getTransport("smtp")) {

                transport.connect(
                        configuracion.host(),
                        configuracion.puerto(),
                        configuracion.usuario(),
                        configuracion.password());

                transport.sendMessage(
                        mensaje,
                        mensaje.getAllRecipients());

            }

            return new ResultadoEnvio(
                    EstadoEnvio.ENVIADO,
                    "Correo enviado correctamente.");
        } catch (MessagingException | IOException e) {
            return new ResultadoEnvio(
                    EstadoEnvio.ERROR,
                    e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        }
    }

    private Properties crearPropiedades() {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", configuracion.host());
        properties.put("mail.smtp.host", String.valueOf(configuracion.puerto()));
        properties.put("mail.smtp.auth", "true");

        switch (configuracion.seguridad()) {

            case STARTTLS -> {
                properties.put(
                        "mail.smtp.starttls.enable",
                        "true");
            }
            case SSL_TLS -> {
                properties.put(
                        "mail.smtp.ssl.enable",
                        "true");
            }
            case NINGUNA -> {
                // No se agrega configuracion TLS.
            }
        }

        return properties;
    }

    @Override
    public ResultadoConexionCorreo probarConexion() {

        try {
            Session session = Session.getInstance(crearPropiedades());

            try (Transport transport = session.getTransport("smtp")) {

                transport.connect(
                        configuracion.host(),
                        configuracion.puerto(),
                        configuracion.usuario(),
                        configuracion.password());
            }

            return new ResultadoConexionCorreo(
                    true,
                    "Conexion SMTP realizada correctamente.");

        } catch (MessagingException e) {
            return new ResultadoConexionCorreo(
                    false,
                    e.getMessage() != null
                            ? e.getMessage()
                            : e.getClass().getSimpleName());
        }
    }
}