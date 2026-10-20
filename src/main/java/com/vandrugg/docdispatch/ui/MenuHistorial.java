package com.vandrugg.docdispatch.ui;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import com.vandrugg.docdispatch.model.RegistroEnvio;
import com.vandrugg.docdispatch.repository.RepositorioHistorialEnvios;

public class MenuHistorial {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");

    private final RepositorioHistorialEnvios repositorioHistorialEnvios;
    private final Scanner scanner;

    public MenuHistorial(
            RepositorioHistorialEnvios repositorioHistorialEnvios,
            Scanner scanner) {
        if (repositorioHistorialEnvios == null) {
            throw new IllegalArgumentException(
                    "El repositorio de historial es obligatorio.");
        }

        if (scanner == null) {
            throw new IllegalArgumentException(
                    "El scanner es obligatorio.");
        }

        this.repositorioHistorialEnvios = repositorioHistorialEnvios;
        this.scanner = scanner;
    }

    public void mostrar() {

        boolean volver = false;

        while (!volver) {
            limpiarPantalla();

            List<RegistroEnvio> registros = repositorioHistorialEnvios.listar();

            System.out.println("==============================================");
            System.out.println("              HISTORIAL DE ENVIOS");
            System.out.println("==============================================");
            System.out.println();

            if (registros.isEmpty()) {
                System.out.println(
                        "No existen envios registrados.");
                pausar();
                return;
            }

            mostrarListado(registros);

            System.out.println();
            System.out.println(
                    "Ingrese el ID para ver el detalle.");
            System.out.println(
                    "0. Volver al menu principal.");
            System.out.println();
            System.out.println(
                    "Seleccione una opcion: ");

            String entrada = scanner.nextLine().trim();

            if ("0".equals(entrada)) {
                volver = true;
                continue;
            }

            try {
                int id = Integer.parseInt(entrada);

                mostrarDetalle(id);
                
            } catch (NumberFormatException e) {
                System.out.println();
                System.out.println(
                        "Debe ingresar un ID numerico.");
                pausar();
            }
        }
    }

    private void mostrarListado(
            List<RegistroEnvio> registros) {

        System.out.printf(
                "%-5s %-18s %-12s %-18s%n",
                "ID",
                "Fecha",
                "Documento",
                "Estado");

        System.out.println(
                "-------------------------------------------------------");

        for (RegistroEnvio registro : registros) {

            System.out.printf("%-5d %-18s %-12d %-18s%n",
                    registro.id(),
                    registro.fechaHora()
                            .format(FORMATO_FECHA),
                    registro.numeroDocumento(),
                    registro.estado());
        }
    }

    private void mostrarDetalle(int id){

        RegistroEnvio regitro = repositorioHistorialEnvios.buscarPorId(id);

        limpiarPantalla();

        if(regitro == null){
            System.out.println(
                "No existe un envio con ID " + id + "."
            );
            pausar();
            return;
        }

        System.out.println("================================");
        System.out.println("       DETALLE DEL ENVIO");
        System.out.println("================================");
        System.out.println();

        System.out.println(
            "ID: " + regitro.id()
        );

        System.out.println(
            "Fecha: " + regitro.fechaHora().format(FORMATO_FECHA)
        );

        System.out.println(
            "Documento: " + regitro.numeroDocumento()
        );

        System.out.println(
            "Estado: " + regitro.estado()
        );

        System.out.println();

        System.out.println("Destinatarios:");
        mostrarElementosSeparados(regitro.destinatarios());

        System.out.println();

        System.out.println("Archivos:");
        mostrarElementosSeparados(regitro.archivos());

        System.out.println();

        System.out.println("Detalle:");
        System.out.println(regitro.detalle());

        pausar();
    }

    private void mostrarElementosSeparados(String valor){

        if(valor == null || valor.isBlank()){
            System.out.println(
                "- Sin datos"
            );
            return;
        }

        String[] elementos = valor.split(",");

        for(String elemento : elementos){
            System.out.println(
                "- " + elemento.trim()
            );
        }
    }

    private void pausar(){

        System.out.println();
        System.out.println(
            "Presiones ENTER para continuar..."
        );
        scanner.nextLine();
    }

    private void limpiarPantalla() {

        try {

            new ProcessBuilder(
                    "cmd",
                    "/c",
                    "cls")
                    .inheritIO()
                    .start()
                    .waitFor();

        } catch (IOException
                | InterruptedException e) {

        }
    }

}
