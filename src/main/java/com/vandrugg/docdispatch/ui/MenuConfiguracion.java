package com.vandrugg.docdispatch.ui;

import java.io.IOException;
import java.util.Scanner;

import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;
import com.vandrugg.docdispatch.service.IdentificadorPorCodigo;

public class MenuConfiguracion {

    private final RepositorioConfiguracion repositorioConfiguracion;
    private final Scanner scanner;

    public MenuConfiguracion(
            RepositorioConfiguracion repositorioConfiguracion,
            Scanner scanner) {
        this.repositorioConfiguracion = repositorioConfiguracion;
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean volver = false;

        while (!volver) {
            limpiarPantalla();

            String codigoActual = repositorioConfiguracion.obtenerCodigoDocumento();

            System.out.println("================================");
            System.out.println("          CONFIGURACION");
            System.out.println("================================");
            System.out.println();
            System.out.println(
                    "Codigo de documento actual: "
                            + codigoActual);
            System.out.println();
            System.out.println("1. Cambiar codigo de documento");
            System.out.println("0. Volver al menu principal");
            System.out.println();
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> cambiarCodigoDocumento();
                case "0" -> volver = true;

                default -> {
                    System.out.println();
                    System.out.println("Opcion invalida.");
                    pausar();
                }
            }
        }
    }

    private void cambiarCodigoDocumento() {
        limpiarPantalla();

        System.out.println("=== CAMBIAR CODIGO DE DOCUMENTO ===");
        System.out.println();
        System.out.println(
                "El codigo debe contener exactamente 3 letras.");
        System.out.println();
        System.out.print("Nuevo codigo: ");

        String nuevoCodigo = scanner.nextLine().trim();

        try {
            IdentificadorPorCodigo identificador = new IdentificadorPorCodigo(nuevoCodigo);

            repositorioConfiguracion.guardarCodigoDocumento(identificador.obtenerCodigo());

            System.out.println();
            System.out.println(
                    "Codigo actualizado correctamente a: "
                            + identificador.obtenerCodigo());

            System.out.println();
            System.out.println(
                    "El nuevo codigo se utilizara en el procesamiento de documentos.");
        } catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println(
                    "Error: " + e.getMessage());
        }
        pausar();
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
