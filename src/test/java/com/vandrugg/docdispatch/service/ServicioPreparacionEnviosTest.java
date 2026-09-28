package com.vandrugg.docdispatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.model.PreparacionEnvio;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;

public class ServicioPreparacionEnviosTest {

    private RepositorioDestinatarios repositorioDestinatarios;
    private ServicioPreparacionEnvios servicioPreparacionEnvios;

    @BeforeEach
    void setUp(@TempDir Path carpetaTemporal) {
        Path archivoDb = carpetaTemporal.resolve("test.db");

        DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + archivoDb);

        databaseManager.inicializarBaseDeDatos();

        RepositorioConfiguracion repositorioConfiguracion = new RepositorioConfiguracion(databaseManager);

        repositorioConfiguracion.guardarCodigoDocumento("LIQ");

        repositorioDestinatarios = new RepositorioDestinatarios(databaseManager);

        ServicioDocumentos servicioDocumentos = new ServicioDocumentos(repositorioConfiguracion);

        servicioPreparacionEnvios = new ServicioPreparacionEnvios(servicioDocumentos, repositorioDestinatarios);

    }

    @Test
    void debePrepararUnDocumentoConUnDestinatario(
            @TempDir Path carpeta) throws IOException {

        Files.createFile(carpeta.resolve("LIQ166.pdf"));

        repositorioDestinatarios.guardarDestinatario(
                166, "correo@example.com");

        List<PreparacionEnvio> preparaciones = servicioPreparacionEnvios.preparar(carpeta);

        assertEquals(1, preparaciones.size());

        PreparacionEnvio preparacion = preparaciones.get(0);

        assertEquals(166, preparacion.numeroDocumento());
        assertEquals(1, preparacion.documentos().size());
        assertEquals(1, preparacion.destinatarios().size());

        assertEquals(
                "correo@example.com",
                preparacion.destinatarios().get(0));

        assertTrue(preparacion.tieneDestinatarios());
    }

}
