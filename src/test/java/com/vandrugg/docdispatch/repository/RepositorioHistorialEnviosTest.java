package com.vandrugg.docdispatch.repository;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.enums.EstadoEnvio;
import com.vandrugg.docdispatch.model.RegistroEnvio;

public class RepositorioHistorialEnviosTest {

        private RepositorioHistorialEnvios repositorio;

        @BeforeEach
        void setUp(@TempDir Path carpetaTemporal) {

                Path archivoDb = carpetaTemporal.resolve("test.db");

                DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + archivoDb);

                databaseManager.inicializarBaseDeDatos();

                repositorio = new RepositorioHistorialEnvios(databaseManager);
        }

        @Test
        void debeRegistrarYListarUnEnvio() {

                repositorio.registrar(
                                999,
                                EstadoEnvio.ENVIADO,
                                "correo@example.com",
                                "LIQ999.pdf",
                                "Envio realizado correctamente.");

                List<RegistroEnvio> registros = repositorio.listar();

                assertEquals(1, registros.size());

                RegistroEnvio registro = registros.get(0);

                assertEquals(999, registro.numeroDocumento());
                assertEquals(EstadoEnvio.ENVIADO, registro.estado());
                assertEquals("correo@example.com", registro.destinatarios());
                assertEquals("LIQ999.pdf", registro.archivos());
                assertNotNull(registro.fechaHora());
        }

        @Test
        void debeBuscarUnEnvioPorId() {

                repositorio.registrar(
                                166,
                                EstadoEnvio.ENVIADO,
                                "correo@example.com",
                                "LIQ166.pdf",
                                "Envio correcto.");

                RegistroEnvio listado = repositorio.listar().get(0);

                RegistroEnvio encontrado = repositorio.buscarPorId(
                                listado.id());

                assertNotNull(encontrado);

                assertEquals(
                                listado.id(),
                                encontrado.id());

                assertEquals(
                                166,
                                encontrado.numeroDocumento());
        }

        @Test
        void debeRetornarNullSiElIdNoExiste() {

                RegistroEnvio resultado = repositorio.buscarPorId(999);

                assertNull(resultado);
        }

        @Test
        void debeListarEnviosMasRecientesPrimero() {

                repositorio.registrar(
                                100,
                                EstadoEnvio.ENVIADO,
                                "uno@example.com",
                                "LIQ100.pdf",
                                "Primer envio.");

                repositorio.registrar(
                                200,
                                EstadoEnvio.ERROR,
                                "dos@example.com",
                                "LIQ200.pdf",
                                "Segundo envio.");

                List<RegistroEnvio> registros = repositorio.listar();

                assertEquals(2, registros.size());

                assertEquals(
                                200,
                                registros.get(0).numeroDocumento());

                assertEquals(
                                100,
                                registros.get(1).numeroDocumento());
        }

        @Test
        void debeRegistrarEnvioConError() {

                repositorio.registrar(
                                300,
                                EstadoEnvio.ERROR,
                                "correo@example.com",
                                "LIQ300.pdf",
                                "Error de conexion SMTP.");

                RegistroEnvio registro = repositorio.listar().get(0);

                assertEquals(
                                EstadoEnvio.ERROR,
                                registro.estado());

                assertEquals(
                                "Error de conexion SMTP.",
                                registro.detalle());
        }
}
