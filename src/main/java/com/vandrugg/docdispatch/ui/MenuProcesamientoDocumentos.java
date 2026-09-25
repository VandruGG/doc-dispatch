package com.vandrugg.docdispatch.ui;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import com.vandrugg.docdispatch.model.Documento;
import com.vandrugg.docdispatch.service.ServicioDocumentos;

public class MenuProcesamientoDocumentos {

    private final ServicioDocumentos servicioDocumentos;
    private final Scanner scanner;

    public MenuProcesamientoDocumentos(
            ServicioDocumentos servicioDocumentos,
            Scanner scanner) {
        this.servicioDocumentos = servicioDocumentos;
        this.scanner = scanner;
    }

    public void mostrar(){
        boolean volver = false;

        while(!volver){
            limpiarPantalla();

            System.out.println("================================");
            System.out.println("      PROCESAR DOCUMENTOS");
            System.out.println("================================");
            System.out.println();
            System.out.println("1. Analizar carpeta");
            System.out.println("0. Volver al menu principal");
            System.out.println();
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> analizarCarpeta();
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

            List<Documento> documentos = servicioDocumentos.analizarCarpeta(carpeta);

            mostrarResultado(documentos);

        } catch (InvalidPathException e) {
            System.out.println();
            System.out.println("La ruta ingresada no es valida.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println();
            System.out.println("Error: " + e.getMessage());
        }

        pausar();
    }

    private void mostrarResultado(List<Documento> documentos) {
        System.out.println();

        if (documentos.isEmpty()) {
            System.out.println(
                    "No se encontraron documentos validos.");
            return;
        }

        System.out.println("DOCUMENTOS DETECTADOS");
        System.out.println("---------------------");

        for (Documento documento : documentos) {
            System.out.println(
                    documento.numeroDocumento()
                            + " - "
                            + documento.nombreArchivo());
        }

        System.out.println();
        System.out.println(
                "Total de documentos: "
                        + documentos.size());
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
        } catch (Exception e) {

        }
    }

}
