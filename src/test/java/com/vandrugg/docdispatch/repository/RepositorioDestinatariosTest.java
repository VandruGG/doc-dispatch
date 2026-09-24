package com.vandrugg.docdispatch.repository;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vandrugg.docdispatch.database.DatabaseManager;

public class RepositorioDestinatariosTest {

    private RepositorioDestinatarios repositorio;

    @BeforeEach
    void setUp(@TempDir Path carpetaTemporal) {

        Path archivoDb = carpetaTemporal.resolve("test.db");

        DatabaseManager databaseManager = new DatabaseManager(
                "jdbc:sqlite:" + archivoDb);

        databaseManager.inicializarBaseDeDatos();

        repositorio = new RepositorioDestinatarios(databaseManager);
    }

    @Test
    void debeGuardarYObtenerDestinatario() {

        repositorio.guardarDestinatario(
                166,
                "correo1@example.com");

        List<String> destinatarios = repositorio.obtenerDestinatarios(166);

        assertEquals(1, destinatarios.size());
        assertEquals("correo1@example.com", destinatarios.get(0));
    }

    @Test
    void debePermitirVariosCorreosParaElMismoDocumento() {

        repositorio.guardarDestinatario(
                166,
                "correo1@example.com");

        repositorio.guardarDestinatario(
                166,
                "correo2@example.com");

        List<String> destinatarios = repositorio.obtenerDestinatarios(166);

        assertEquals(2, destinatarios.size());
        assertTrue(
                destinatarios.contains(
                        "correo1@example.com"));
        assertTrue(
                destinatarios.contains(
                        "correo2@example.com"));
    }

    @Test
    void debeDesactivarDestinatario() {

        repositorio.guardarDestinatario(
                166,
                "correo1@example.com");

        repositorio.eliminarDestinatario(
                166,
                "correo1@example.com");

        List<String> destinatarios = repositorio.obtenerDestinatarios(166);

        assertTrue(destinatarios.isEmpty());
    }

    @Test
    void debeReactivarDestinatarioExistente() {

        repositorio.guardarDestinatario(
                166,
                "correo1@example.com");

        repositorio.eliminarDestinatario(
                166,
                "correo1@example.com");

        repositorio.guardarDestinatario(
                166,
                "correo1@example.com");

        List<String> destinatarios = repositorio.obtenerDestinatarios(166);

        assertEquals(1, destinatarios.size());
        assertEquals(
                "correo1@example.com",
                destinatarios.get(0));
    }

}
