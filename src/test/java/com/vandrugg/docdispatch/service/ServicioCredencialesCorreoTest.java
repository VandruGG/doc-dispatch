package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private RepositorioConfiguracionCorreo repositorio;
    private ServicioCredencialesCorreo servicio;

    @BeforeEach 
    void setUp(@TempDir Path carpetaTemporal) {

        Path archivoDb = carpetaTemporal.resolve("test.db");

        DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + archivoDb);

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
