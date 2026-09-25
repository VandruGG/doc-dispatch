package com.vandrugg.docdispatch.ui;

import java.io.IOException;
import java.util.Scanner;

public class MenuPrincipal {

    private final MenuDestinatarios menuDestinatarios;
    private final Scanner scanner;

    public MenuPrincipal(
            MenuDestinatarios menuDestinatarios,
            Scanner scanner) {
        this.menuDestinatarios = menuDestinatarios;
        this.scanner = new Scanner(System.in);
    }

    public void mostrar() {
        boolean salir = false;

        while (!salir) {
            limpiarPantalla();

            System.out.println("================================");
            System.out.println("          DOCDISPATCH");
            System.out.println("================================");
            System.out.println();
            System.out.println("1. Gestionar destinatarios");
            System.out.println("2. Procesar documentos");
            System.out.println("3. Consultar historial");
            System.out.println("4. Configuracion");
            System.out.println("0. Salir");
            System.out.println();
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> menuDestinatarios.mostrar();

                case "2" -> opcionNoDisponible(
                        "Procesamiento de documentos");

                case "3" -> opcionNoDisponible(
                        "Historial de envios");

                case "4" -> opcionNoDisponible(
                        "Configuracion");

                case "0" -> salir = true;

                default -> {
                    System.out.println();
                    System.out.println("Opcion invalida.");
                    pausar();
                }
            }
        }

        limpiarPantalla();
        System.out.println("DocDispatch finalizado.");
    }

    private void opcionNoDisponible(String opcion) {
        System.out.println();
        System.out.println(
                opcion + " todavia no esta disponible.");
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
