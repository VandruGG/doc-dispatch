package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.enums.SeguridadSmtp;
import com.vandrugg.docdispatch.model.ConfiguracionCorreo;
import com.vandrugg.docdispatch.model.ConfiguracionCorreoPersistida;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracionCorreo;
import com.vandrugg.docdispatch.security.CifradorCredenciales;
import com.vandrugg.docdispatch.security.ProtectorClave;

public class ServicioCredencialesCorreoTest {

        private DatabaseManager databaseManager;
        private RepositorioConfiguracionCorreo repositorio;
        private ServicioCredencialesCorreo servicio;

        @BeforeEach
        void setUp(@TempDir Path carpetaTemporal) {

                Path archivoDb = carpetaTemporal.resolve("test.db");

                databaseManager = new DatabaseManager("jdbc:sqlite:" + archivoDb);

                databaseManager.inicializarBaseDeDatos();

                repositorio = new RepositorioConfiguracionCorreo(databaseManager);

                servicio = new ServicioCredencialesCorreo(
                                repositorio,
                                new CifradorCredenciales(),
                                new ProtectorClaveFalso());
        }

        @Test
        void debeGuardarYRecuperarCredenciales() {

                servicio.guardar(
                                "GMAIL",
                                "correo@example.com",
                                "password-secreta",
                                "smtp.example.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                ConfiguracionCorreo configuracion = servicio.obtener();

                assertNotNull(configuracion);

                assertEquals("smtp.example.com", configuracion.host());
                assertEquals(587, configuracion.puerto());
                assertEquals("correo@example.com", configuracion.usuario());
                assertEquals("password-secreta", configuracion.password());
                assertEquals(SeguridadSmtp.STARTTLS, configuracion.seguridad());
        }

        @Test
        void noDebeGuardarPasswordEnTextoPlano() {

                String passwordOriginal = "password-secreta";

                servicio.guardar(
                                "GMAIL",
                                "correo@example.com",
                                passwordOriginal,
                                "smtp.example.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                ConfiguracionCorreoPersistida persistida = repositorio.obtener();

                assertNotNull(persistida);

                assertNotEquals(
                                passwordOriginal,
                                persistida.passwordCifrada());

                assertFalse(
                                persistida.passwordCifrada().contains(passwordOriginal));
        }

        @Test
        void debeIndicarSiExisteConfiguracion() {

                assertFalse(servicio.estaConfigurado());

                servicio.guardar(
                                "GMAIL",
                                "correo@example.com",
                                "password",
                                "smtp.example.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                assertTrue(servicio.estaConfigurado());
        }

        @Test
        void debeReemplazarConfiguracionExistente() {

                servicio.guardar(
                                "GMAIL",
                                "correo1@example.com",
                                "password-1",
                                "smtp.gmail.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                servicio.guardar(
                                "OUTLOOK",
                                "correo2@example.com",
                                "password-2",
                                "smtp-mail.outlook.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                ConfiguracionCorreo configuracion = servicio.obtener();

                ConfiguracionCorreoPersistida persistida = repositorio.obtener();

                assertNotNull(configuracion);
                assertNotNull(persistida);

                assertEquals(
                                "OUTLOOK",
                                persistida.proveedor());

                assertEquals(
                                "correo2@example.com",
                                configuracion.usuario());

                assertEquals(
                                "password-2",
                                configuracion.password());

                assertEquals(
                                "smtp-mail.outlook.com",
                                configuracion.host());

                assertEquals(
                                587,
                                configuracion.puerto());

                assertEquals(
                                SeguridadSmtp.STARTTLS,
                                configuracion.seguridad());
        }

        @Test
        void debeReemplazarPasswordCifrada() {

                servicio.guardar(
                                "GMAIL",
                                "correo@example.com",
                                "password-1",
                                "smtp.gmail.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                ConfiguracionCorreoPersistida primera = repositorio.obtener();

                servicio.guardar(
                                "GMAIL",
                                "correo@example.com",
                                "password-2",
                                "smtp.gmail.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                ConfiguracionCorreoPersistida segunda = repositorio.obtener();

                assertNotNull(primera);
                assertNotNull(segunda);

                assertNotEquals(
                                primera.passwordCifrada(),
                                segunda.passwordCifrada());

                assertNotEquals(
                                primera.claveProtegida(),
                                segunda.claveProtegida());

                assertEquals(
                                "password-2",
                                servicio.obtener().password());
        }

        @Test
        void debeRetornarNullSiNoExisteConfiguracion() {

                ConfiguracionCorreo configuracion = servicio.obtener();

                assertNull(configuracion);
        }

        @Test
        void debeGuardarConfiguracionPersonalizada() {

                servicio.guardar(
                                "PERSONALIZADO",
                                "usuario@example.com",
                                "password-secreta",
                                "smtp.example.com",
                                465,
                                SeguridadSmtp.SSL_TLS);

                ConfiguracionCorreoPersistida persistida = repositorio.obtener();

                ConfiguracionCorreo configuracion = servicio.obtener();

                assertNotNull(persistida);
                assertNotNull(configuracion);

                assertEquals(
                                "PERSONALIZADO",
                                persistida.proveedor());

                assertEquals(
                                "smtp.example.com",
                                configuracion.host());

                assertEquals(
                                465,
                                configuracion.puerto());

                assertEquals(
                                SeguridadSmtp.SSL_TLS,
                                configuracion.seguridad());

                assertEquals(
                                "usuario@example.com",
                                configuracion.usuario());

                assertEquals(
                                "password-secreta",
                                configuracion.password());
        }

        @Test
        void debeFallarDeFormaControladaSiLaClaveProtegidaEstaCorrupta()
                        throws SQLException {

                servicio.guardar(
                                "GMAIL",
                                "correo@example.com",
                                "password-secreta",
                                "smtp.gmail.com",
                                587,
                                SeguridadSmtp.STARTTLS);

                String sql = """
                                UPDATE configuracion
                                SET valor = ?
                                WHERE clave = 'mail_aes_key'
                                """;

                try (var connection = databaseManager.obtenerConexion();

                                var statement = connection.prepareStatement(sql)) {

                        statement.setString(
                                        1,
                                        "valor-base64-invalido");

                        statement.executeUpdate();
                }

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> servicio.obtener());

                assertTrue(
                                exception.getMessage()
                                                .contains(
                                                                "No se pudieron recuperar las credenciales"));
        }

        @Test
        void noDebeGuardarPasswordVacia() {

                assertThrows(
                                IllegalArgumentException.class,
                                () -> servicio.guardar(
                                                "GMAIL",
                                                "correo@example.com",
                                                " ",
                                                "smtp.gmail.com",
                                                587,
                                                SeguridadSmtp.STARTTLS));
        }

        private static class ProtectorClaveFalso implements ProtectorClave {

                private static final byte MASCARA = (byte) 0x5A;

                @Override
                public byte[] proteger(byte[] datos) {

                        byte[] resultado = Arrays.copyOf(
                                        datos,
                                        datos.length);

                        for (int i = 0; i < resultado.length; i++) {
                                resultado[i] = (byte) (resultado[i] ^ MASCARA);
                        }

                        return resultado;
                }

                @Override
                public byte[] desproteger(byte[] datos) {
                        return proteger(datos);
                }
        }

}
