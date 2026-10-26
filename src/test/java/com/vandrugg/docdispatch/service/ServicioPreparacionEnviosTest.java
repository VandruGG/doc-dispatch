package com.vandrugg.docdispatch.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.enums.EstadoEnvio;
import com.vandrugg.docdispatch.model.PreparacionEnvio;
import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;
import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.ResultadoSimulacion;
import com.vandrugg.docdispatch.model.SolicitudCorreo;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;
import com.vandrugg.docdispatch.repository.RepositorioHistorialEnvios;

public class ServicioPreparacionEnviosTest {

        private RepositorioDestinatarios repositorioDestinatarios;
        private ServicioPreparacionEnvios servicioPreparacionEnvios;
        private RepositorioConfiguracion repositorioConfiguracion;
        private RepositorioHistorialEnvios repositorioHistorialEnvios;
        private ServicioCorreoFalso servicioCorreoFalso;

        @BeforeEach
        void setUp(@TempDir Path carpetaTemporal) {
                Path archivoDb = carpetaTemporal.resolve("test.db");

                DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + archivoDb);

                databaseManager.inicializarBaseDeDatos();

                repositorioConfiguracion = new RepositorioConfiguracion(databaseManager);

                repositorioConfiguracion.guardarCodigoDocumento("LIQ");

                repositorioDestinatarios = new RepositorioDestinatarios(databaseManager);

                repositorioHistorialEnvios = new RepositorioHistorialEnvios(databaseManager);

                ServicioDocumentos servicioDocumentos = new ServicioDocumentos(repositorioConfiguracion);

                servicioCorreoFalso = new ServicioCorreoFalso();

                servicioPreparacionEnvios = new ServicioPreparacionEnvios(
                                servicioDocumentos,
                                repositorioDestinatarios,
                                servicioCorreoFalso,
                                repositorioConfiguracion,
                                repositorioHistorialEnvios);

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

                configurarCorreoValido();

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

        @Test
        void debeEnviarDocumentosPreparados(
                        @TempDir Path carpeta) throws IOException {

                configurarCorreoValido();

                Files.createFile(
                                carpeta.resolve("LIQ166.pdf"));

                Files.createFile(
                                carpeta.resolve("LIQ166_anexo.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                166,
                                "correo1@example.com");

                repositorioDestinatarios.guardarDestinatario(
                                166,
                                "correo2@example.com");

                List<ResultadoEnvio> resultados = servicioPreparacionEnvios.enviar(carpeta);

                assertEquals(1, resultados.size());

                assertEquals(
                                EstadoEnvio.ENVIADO,
                                resultados.get(0).estado());

                assertEquals(1, servicioCorreoFalso.cantidadEnvios);
                assertEquals(2, servicioCorreoFalso.ultimosDestinatarios.size());
                assertEquals(2, servicioCorreoFalso.ultimosAdjuntos.size());
        }

        @Test
        void noDebeEnviarSiNoHayDestinatarios(
                        @TempDir Path carpeta) throws IOException {

                configurarCorreoValido();

                Files.createFile(carpeta.resolve("LIQ200.pdf"));

                List<ResultadoEnvio> resultados = servicioPreparacionEnvios.enviar(carpeta);

                assertEquals(1, resultados.size());
                assertEquals(
                                EstadoEnvio.SIN_DESTINATARIO,
                                resultados.get(0).estado());
                assertEquals(0, servicioCorreoFalso.cantidadEnvios);
        }

        @Test
        void debeMarcarComoNoListoSiFaltaAsunto(
                        @TempDir Path carpeta) throws IOException {

                repositorioConfiguracion.guardarCuerpoCorreo("Cuerpo de prueba");

                Files.createFile(
                                carpeta.resolve("LIQ300.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                300,
                                "prueba@correo.com");

                List<ResultadoSimulacion> resultados = servicioPreparacionEnvios.simular(carpeta);

                assertEquals(1, resultados.size());

                ResultadoSimulacion resultado = resultados.get(0);

                assertFalse(resultado.listoParaEnviar());

                assertEquals(
                                "El asunto del correo no esta configurado.",
                                resultado.detalle());
        }

        @Test
        void debeMarcarComoNoListoSiFaltaCuerpo(
                        @TempDir Path carpeta) throws IOException {

                repositorioConfiguracion.guardarAsuntoCorreo("Asunto de prueba");

                Files.createFile(carpeta.resolve("LIQ301.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                301,
                                "prueba@correo.com");

                List<ResultadoSimulacion> resultados = servicioPreparacionEnvios.simular(carpeta);

                assertEquals(1, resultados.size());

                ResultadoSimulacion resultado = resultados.get(0);

                assertFalse(resultado.listoParaEnviar());

                assertEquals(
                                "El cuerpo del correo no esta configurado.",
                                resultado.detalle());
        }

        @Test
        void debeConstruirSolicitudCorreoCompleta(
                        @TempDir Path carpeta) throws IOException {

                repositorioConfiguracion.guardarAsuntoCorreo(
                                "Liquidacion mensual");

                repositorioConfiguracion.guardarCuerpoCorreo(
                                "Se adjunta la documentacion.");

                Path archivo1 = Files.createFile(
                                carpeta.resolve(
                                                "LIQ166.pdf"));

                Path archivo2 = Files.createFile(
                                carpeta.resolve(
                                                "LIQ166_anexo.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                166,
                                "correo1@example.com");

                repositorioDestinatarios.guardarDestinatario(
                                166,
                                "correo2@example.com");

                servicioPreparacionEnvios.enviar(carpeta);

                SolicitudCorreo solicitud = servicioCorreoFalso.ultimaSolicitud;

                assertEquals(
                                "Liquidacion mensual",
                                solicitud.asunto());

                assertEquals(
                                "Se adjunta la documentacion.",
                                solicitud.cuerpo());

                assertEquals(
                                2,
                                solicitud.destinatarios().size());

                assertTrue(
                                solicitud.destinatarios()
                                                .contains(
                                                                "correo1@example.com"));

                assertTrue(
                                solicitud.destinatarios()
                                                .contains(
                                                                "correo2@example.com"));

                assertEquals(
                                2,
                                solicitud.adjuntos().size());

                assertTrue(
                                solicitud.adjuntos()
                                                .contains(archivo1));

                assertTrue(
                                solicitud.adjuntos()
                                                .contains(archivo2));
        }

        @Test
        void debeDevolverErrorSiFallaElEnvioDeCorreo(
                        @TempDir Path carpeta) throws IOException {

                configurarCorreoValido();

                Files.createFile(
                                carpeta.resolve("LIQ400.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                400,
                                "correo@example.com");

                servicioCorreoFalso.resultadoEnvio = new ResultadoEnvio(
                                EstadoEnvio.ERROR,
                                "Error SMTP simulado.");

                List<ResultadoEnvio> resultados = servicioPreparacionEnvios.enviar(
                                carpeta);

                assertEquals(1, resultados.size());

                assertEquals(
                                EstadoEnvio.ERROR,
                                resultados.get(0).estado());

                assertTrue(
                                resultados.get(0)
                                                .detalle()
                                                .contains(
                                                                "Error SMTP simulado."));

                assertEquals(
                                1,
                                servicioCorreoFalso.cantidadEnvios);
        }

        @Test
        void debeRegistrarEnvioExitosoEnHistorial(
                        @TempDir Path carpeta) throws IOException {

                configurarCorreoValido();

                Files.createFile(
                                carpeta.resolve("LIQ500.pdf"));

                Files.createFile(
                                carpeta.resolve(
                                                "LIQ500_anexo.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                500,
                                "correo@example.com");

                servicioPreparacionEnvios.enviar(carpeta);

                var historial = repositorioHistorialEnvios.listar();

                assertEquals(1, historial.size());

                var registro = historial.get(0);

                assertEquals(
                                500,
                                registro.numeroDocumento());

                assertEquals(
                                EstadoEnvio.ENVIADO,
                                registro.estado());

                assertTrue(
                                registro.destinatarios()
                                                .contains(
                                                                "correo@example.com"));

                assertTrue(
                                registro.archivos()
                                                .contains(
                                                                "LIQ500.pdf"));

                assertTrue(
                                registro.archivos()
                                                .contains(
                                                                "LIQ500_anexo.pdf"));
        }

        @Test
        void debeRegistrarErrorSmtpEnHistorial(
                        @TempDir Path carpeta) throws IOException {

                configurarCorreoValido();

                Files.createFile(
                                carpeta.resolve("LIQ600.pdf"));

                repositorioDestinatarios.guardarDestinatario(
                                600,
                                "correo@example.com");

                servicioCorreoFalso.resultadoEnvio = new ResultadoEnvio(
                                EstadoEnvio.ERROR,
                                "Servidor SMTP no disponible.");

                servicioPreparacionEnvios.enviar(carpeta);

                var historial = repositorioHistorialEnvios.listar();

                assertEquals(1, historial.size());

                var registro = historial.get(0);

                assertEquals(
                                600,
                                registro.numeroDocumento());

                assertEquals(
                                EstadoEnvio.ERROR,
                                registro.estado());

                assertTrue(
                                registro.detalle()
                                                .contains(
                                                                "Servidor SMTP no disponible."));
        }

        private static class ServicioCorreoFalso implements ServicioCorreo {

                private int cantidadEnvios;

                private List<String> ultimosDestinatarios = new ArrayList<>();

                private List<Path> ultimosAdjuntos = new ArrayList<>();

                private SolicitudCorreo ultimaSolicitud;
                
                private ResultadoEnvio resultadoEnvio = new ResultadoEnvio(
                                EstadoEnvio.ENVIADO,
                                "Envio simulado por test.");

                @Override
                public ResultadoEnvio enviar(SolicitudCorreo solicitud) {
                        cantidadEnvios++;

                        ultimaSolicitud = solicitud;

                        ultimosDestinatarios = new ArrayList<>(solicitud.destinatarios());

                        ultimosAdjuntos = new ArrayList<>(solicitud.adjuntos());

                        return resultadoEnvio;
                }

                @Override
                public ResultadoConexionCorreo probarConexion() {
                        return new ResultadoConexionCorreo(
                                        true,
                                        "Conexion simulada correctamente.");
                }
        }

        private void configurarCorreoValido() {

                repositorioConfiguracion.guardarAsuntoCorreo(
                                "Asunto de prueba.");

                repositorioConfiguracion.guardarCuerpoCorreo(
                                "Correo de prueba.");
        }
}
