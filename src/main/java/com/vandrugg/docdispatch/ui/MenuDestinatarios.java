package com.vandrugg.docdispatch.ui;

import java.util.List;
import java.util.Scanner;

import com.vandrugg.docdispatch.model.Destinatario;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;

public class MenuDestinatarios {

    private final RepositorioDestinatarios repositorio;
    private final Scanner scanner;

    public MenuDestinatarios(RepositorioDestinatarios repositorio) {
        this.repositorio = repositorio;
        this.scanner = new Scanner(System.in);
    }

    public void mostrar() {
        boolean salir = false;

        while (!salir) {
            limpiarPantalla();
            
            System.out.println();
            System.out.println("=== GESTION DE DESTINATARIOS ===");
            System.out.println("1. Agregar o reactivar destinatario");
            System.out.println("2. Listar destinatarios");
            System.out.println("3. Buscar destinatarios por numero");
            System.out.println("4. Desactivar destinatario");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> agregarDestinatario();
                case "2" -> listarDestinatarios();
                case "3" -> buscarDestinatarios();
                case "4" -> desactivarDestinatario();
                case "0" -> salir = true;
                default -> System.out.println("Opcion invalida.");
            }
        }
    }

    private void agregarDestinatario() {
        try {
            int numeroDocumento = leerNumeroDocumento();

            System.out.println("Email: ");
            String email = scanner.nextLine().trim();

            repositorio.guardarDestinatario(numeroDocumento, email);

            System.out.println("Destinatario guardado correctamente.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        pausar();
    }

    private void listarDestinatarios() {
        List<Destinatario> destinatarios = repositorio.listarDestinatarios();

        if (destinatarios.isEmpty()) {
            System.out.println("No hay destinatarios activos.");
            return;
        } else {
            System.out.println();
            System.out.println("DESTINATARIOS ACTIVOS");

            for (Destinatario destinatario : destinatarios) {
                System.out.println(
                        destinatario.numeroDocumento()
                                + " - "
                                + destinatario.email());
            }
        }
        pausar();
    }

    private void buscarDestinatarios() {
        try {
            int numeroDocumento = leerNumeroDocumento();

            List<String> destinatarios = repositorio.obtenerDestinatarios(numeroDocumento);

            if (destinatarios.isEmpty()) {
                System.out.println(
                        "No hay destinatarios para el numero "
                                + numeroDocumento);
            } else {
                System.out.println(
                        "Destinatarios para "
                                + numeroDocumento
                                + ":");

                destinatarios.forEach(
                        email -> System.out.println("- " + email));
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        pausar();
    }

    private void desactivarDestinatario() {
        try {
            int numeroDocumento = leerNumeroDocumento();

            System.out.println("Email: ");
            String email = scanner.nextLine().trim();

            repositorio.eliminarDestinatario(numeroDocumento, email);

            System.out.println("Destinatario desactivado.");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        pausar();
    }

    private int leerNumeroDocumento() {
        System.out.println("Numero de documento: ");

        String entrada = scanner.nextLine().trim();

        try {
            return Integer.parseInt(entrada);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El numero de documento debe ser numerico.");
        }
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
