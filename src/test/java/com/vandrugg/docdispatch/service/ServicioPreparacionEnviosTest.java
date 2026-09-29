package com.vandrugg.docdispatch.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.model.PreparacionEnvio;
import com.vandrugg.docdispatch.model.ResultadoSimulacion;
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

        @Test
        void debeAgruparVariosArchivosYDestinatarios(
                        @TempDir Path carpeta) throws IOException {

                Files.createFile(carpeta.resolve("LIQ166.pdf"));
                Files.createFile(carpeta.resolve("LIQ166_anexo.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                166,
                                "correo1@example.com");

                repositorioDestinatarios.guardarDestinatario(
                                166,
                                "correo2@example.com");

                List<PreparacionEnvio> preparaciones = servicioPreparacionEnvios.preparar(carpeta);

                assertEquals(1, preparaciones.size());

                PreparacionEnvio preparacion = preparaciones.get(0);

                assertEquals(2, preparacion.documentos().size());
                assertEquals(2, preparacion.destinatarios().size());
        }

        @Test
        void debeMantenerDocumentoSinDestinatario(
                        @TempDir Path carpeta) throws IOException {

                Files.createFile(carpeta.resolve("LIQ200.pdf"));

                List<PreparacionEnvio> preparaciones = servicioPreparacionEnvios.preparar(carpeta);

                assertEquals(1, preparaciones.size());

                PreparacionEnvio preparacion = preparaciones.get(0);

                assertEquals(200, preparacion.numeroDocumento());
                assertFalse(preparacion.tieneDestinatarios());
                assertTrue(preparacion.destinatarios().isEmpty());
        }

        @Test
        void debeSimularEnvioListo(
                        @TempDir Path carpeta) throws IOException {

                Files.createFile(carpeta.resolve("LIQ166.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                166,
                                "correo@example.com");

                List<ResultadoSimulacion> resultados = servicioPreparacionEnvios.simular(carpeta);

                assertEquals(1, resultados.size());

                ResultadoSimulacion resultado = resultados.get(0);

                assertEquals(166, resultado.numeroDocumento());
                assertEquals(1, resultado.cantidadArchivos());
                assertEquals(1, resultado.cantidadDestinatarios());
                assertTrue(resultado.listoParaEnviar());
                assertEquals("Envio preparado correctamente.", resultado.detalle());
        }

        @Test
        void debeMarcarComoNoListosSiNoTieneDestinatarios(
                        @TempDir Path carpeta) throws IOException {

                Files.createFile(carpeta.resolve("LIQ200.pdf"));

                List<ResultadoSimulacion> resultados = servicioPreparacionEnvios.simular(carpeta);

                assertEquals(1, resultados.size());

                ResultadoSimulacion resultado = resultados.get(0);

                assertEquals(200, resultado.numeroDocumento());
                assertEquals(1, resultado.cantidadArchivos());
                assertEquals(0, resultado.cantidadDestinatarios());
                assertFalse(resultado.listoParaEnviar());
                assertEquals("Sin destinatarios configurados.", resultado.detalle());
        }

}
