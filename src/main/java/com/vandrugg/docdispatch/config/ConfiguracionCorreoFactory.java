package com.vandrugg.docdispatch.config;

import com.vandrugg.docdispatch.enums.SeguridadSmtp;
import com.vandrugg.docdispatch.model.ConfiguracionCorreo;

public final class ConfiguracionCorreoFactory {

    private ConfiguracionCorreoFactory() {
    }

    public static ConfiguracionCorreo desdeEntorno() {

        String proveedor = obtenerVariable("DOCDISPATCH_MAIL_PROVIDER").toUpperCase();
        String usuario = obtenerVariable("DOCDISPATCH_EMAIL");
        String password = obtenerVariable("DOCDISPATCH_EMAIL_PASSWORD");

        return switch (proveedor) {

            case "GMAIL" -> new ConfiguracionCorreo(
                    "smtp.gmail.com",
                    587,
                    usuario,
                    password,
                    SeguridadSmtp.STARTTLS);

            case "OUTLOOK" -> new ConfiguracionCorreo(
                    "smtp-mail.outlook.com",
                    587,
                    usuario,
                    password,
                    SeguridadSmtp.STARTTLS);

            case "PERSONALIZADO" -> crearPersonalizada(
                    usuario,
                    password);

            default -> throw new IllegalStateException(
                    "Proveeder de correo no soportado. " + proveedor);

        };
    }

    private static ConfiguracionCorreo crearPersonalizada(
            String usuario,
            String password) {
        String host = obtenerVariable("DOCDISPATCH_SMTP_HOST");

        int puerto;

        try {
            puerto = Integer.parseInt(obtenerVariable("DOCDISPATCH_SMTP_PORT"));

        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "DOCDISPATCH_SMTP-PORT debe ser numerico.");
        }

        String seguridadTexto = obtenerVariable("DOCDISPATCH_SMTP_SECURITY").toUpperCase();

        SeguridadSmtp seguridad;

        try {
            seguridad = SeguridadSmtp.valueOf(seguridadTexto);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Seguridad SMTP no validad: " + seguridadTexto);
        }

        return new ConfiguracionCorreo(
            host,
            puerto,
            usuario,
            password,
            seguridad
        );
    }

    private static String obtenerVariable(
            String nombre) {

        String valor = System.getenv(nombre);

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "No esta configurada la variable "
                            + nombre + ".");
        }

        return valor.trim();
    }

}
