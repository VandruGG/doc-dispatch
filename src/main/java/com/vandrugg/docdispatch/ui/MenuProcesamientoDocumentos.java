package com.vandrugg.docdispatch.ui;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import com.vandrugg.docdispatch.enums.EstadoEnvio;
import com.vandrugg.docdispatch.model.Documento;
import com.vandrugg.docdispatch.model.PreparacionEnvio;
import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.ResultadoSimulacion;
import com.vandrugg.docdispatch.service.ServicioPreparacionEnvios;

public class MenuProcesamientoDocumentos {

    private final ServicioPreparacionEnvios servicioPreparacionEnvios;
    private final Scanner scanner;

    public MenuProcesamientoDocumentos(
            ServicioPreparacionEnvios servicioPreparacionEnvios,
            Scanner scanner) {
        this.servicioPreparacionEnvios = servicioPreparacionEnvios;
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean volver = false;

        while (!volver) {
            limpiarPantalla();

            System.out.println("================================");
            System.out.println("      PROCESAR DOCUMENTOS");
            System.out.println("================================");
            System.out.println();
            System.out.println("1. Analizar carpeta");
            System.out.println("2. Simular envio");
            System.out.println("3. Enviar documentos");
            System.out.println("0. Volver al menu principal");
            System.out.println();
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> analizarCarpeta();
                case "2" -> simularEnvios();
                case "3" -> enviarDocumentos();
                case "0" -> volver = true;

                default -> {
                    System.out.println();
                    System.out.println("Opcion invalida.");
                    pausar();
                }
            }
        }
    }

    private void analizarCarpeta() {
        limpiarPantalla();

        System.out.println("=== ANALIZAR CARPETA ===");
        System.out.println();
        System.out.print("Ingrese la ruta de la carpeta: ");

        String entrada = scanner.nextLine().trim();

        try {
            Path carpeta = Path.of(entrada);

            List<PreparacionEnvio> preparaciones = servicioPreparacionEnvios.preparar(carpeta);

            mostrarResultado(preparaciones);

        } catch (InvalidPathException e) {
            System.out.println();
            System.out.println("La ruta ingresada no es valida.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println();
            System.out.println("Error: " + e.getMessage());
        }

        pausar();
    }

    private void mostrarResultado(List<PreparacionEnvio> preparaciones) {
        System.out.println();

        if (preparaciones.isEmpty()) {
            System.out.println(
                    "No se encontraron documentos validos.");
            return;
        }

        System.out.println("DOCUMENTOS DETECTADOS");
        System.out.println("---------------------");

        for (PreparacionEnvio preparacion : preparaciones) {
            System.out.println(
                    "Documento: "
                            + preparacion.numeroDocumento());

            System.out.println("Archivos:");

            for (Documento documento : preparacion.documentos()) {
                System.out.println(
                        "- " + documento.nombreArchivo());
            }

            System.out.println("Destinatarios:");

            if (preparacion.tieneDestinatarios()) {

                for (String destinatario : preparacion.destinatarios()) {

                    System.out.println(
                            "- " + destinatario);
                }
            } else {
                System.out.println(
                        "- SIN DESTINATARIOS CONFIGURADOS");
            }

            System.out.println("---------------------");
        }

        long sinDestinatarios = preparaciones.stream()
                .filter(preparacion -> !preparacion.tieneDestinatarios()).count();

        System.out.println();
        System.out.println(
                "Total de grupos: "
                        + preparaciones.size());

        System.out.println(
                "Grupos sin destinatarios: "
                        + sinDestinatarios);
    }

    private void simularEnvios() {
        limpiarPantalla();

        System.out.println("=== SIMULAR ENVIOS ===");
        System.out.println();
        System.out.print("Ingrese la ruta de la carpeta: ");

        String entrada = scanner.nextLine().trim();

        try {
            Path carpeta = Path.of(entrada);

            List<ResultadoSimulacion> resultados = servicioPreparacionEnvios.simular(carpeta);

            mostrarSimulacion(resultados);
        } catch (InvalidPathException e) {
            System.out.println();
            System.out.println("La ruta ingresada no es valida.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println();
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void mostrarSimulacion(
            List<ResultadoSimulacion> resultados) {

        System.out.println();

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron envios para simular.");
            return;
        }

        System.out.println("RESULTADO DE SIMULACION");
        System.out.println("=======================");

        for (ResultadoSimulacion resultado : resultados) {

            System.out.println();
            System.out.println(
                    "Documento: "
                            + resultado.numeroDocumento());

            System.out.println(
                    "Archivos: "
                            + resultado.cantidadArchivos());

            System.out.println(
                    "Destinatarios: "
                            + resultado.cantidadDestinatarios());

            System.out.println(
                    "Estado: "
                            + (resultado.listoParaEnviar()
                                    ? "LISTO"
                                    : "NO LISTO"));

            System.out.println(
                    "Detalle: "
                            + resultado.detalle());

            System.out.println("---------------------");
        }

        long listos = resultados.stream()
                .filter(
                        ResultadoSimulacion::listoParaEnviar)
                .count();

        System.out.println();
        System.out.println(
                "Listos para enviar: "
                        + listos);

        System.out.println(
                "Con problemas: "
                        + (resultados.size() - listos));
    }

    private void enviarDocumentos() {
        limpiarPantalla();

        System.out.println("=== ENVIAR DOCUMENTOS ===");
        System.out.println();
        System.out.print("Ingrese la ruta de la carpeta: ");

        String entrada = scanner.nextLine().trim();

        try {
            Path carpeta = Path.of(entrada);

            List<ResultadoSimulacion> simulacion = servicioPreparacionEnvios.simular(carpeta);

            if (simulacion.isEmpty()) {
                System.out.println();
                System.out.println(
                        "No se encontraron documentos para enviar.");
                pausar();
                return;
            }

            mostrarSimulacion(simulacion);

            boolean existenProblemas = simulacion.stream().anyMatch(resultado -> !resultado.listoParaEnviar());

            if (existenProblemas) {
                System.out.println();
                System.out.println(
                        "No se puede realizar el envio porque existen grupos con problemas.");
                pausar();
                return;
            }

            System.out.println();
            System.out.println(
                    "ATENCION: ESTA OPERACION ENVIARA CORREOS REALES.");
            System.out.println(
                    "Escriba ENVIAR para confirmar:");
            System.out.print("> ");

            String confirmacion = scanner.nextLine().trim();

            if (!"ENVIAR".equalsIgnoreCase(confirmacion)) {
                System.out.println();
                System.out.println("Envio cancelado.");
                pausar();
                return;
            }

            List<ResultadoEnvio> resultados = servicioPreparacionEnvios.enviar(carpeta);

            mostrarResultadosEnvio(resultados);

        } catch (InvalidPathException e) {
            System.out.println();
            System.out.println("La ruta ingresada no es valida.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println();
            System.out.println("Error: " + e.getMessage());
        }
        pausar();

    }

    private void mostrarResultadosEnvio(
            List<ResultadoEnvio> resultados) {

        System.out.println();
        System.out.println("RESULTADO DE LOS ENVIOS");
        System.out.println("=======================");

        for (ResultadoEnvio resultado : resultados) {
            System.out.println();
            System.out.println(
                    "Estado: " + resultado.estado());

            System.out.println(
                    "Detalle: " + resultado.detalle());

            System.out.println("---------------------");
        }

        long enviados = resultados.stream()
                .filter(resultado -> resultado.estado() == EstadoEnvio.ENVIADO)
                .count();

        System.out.println();
        System.out.println(
                "Enviados correctamente: "
                        + enviados);

        System.out.println(
                "Con error: "
                        + (resultados.size() - enviados));
    }

    private void pausar() {
        System.out.println();
        System.out.println("Presione ENTER para continuar...");
        scanner.nextLine();
    }

    private void limpiarPantalla() {
        try {
            new ProcessBuilder("cmd", "/c", "cls")
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch (IOException | InterruptedException e) {

        }
    }

}
