package com.vandrugg.docdispatch;

import java.util.Scanner;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;
import com.vandrugg.docdispatch.service.IdentificadorDocumento;
import com.vandrugg.docdispatch.service.IdentificadorPorCodigo;
import com.vandrugg.docdispatch.service.ProcesadorDocumentos;
import com.vandrugg.docdispatch.service.ServicioDocumentos;
import com.vandrugg.docdispatch.ui.MenuDestinatarios;
import com.vandrugg.docdispatch.ui.MenuPrincipal;
import com.vandrugg.docdispatch.ui.MenuProcesamientoDocumentos;

public class Main {

    public static void main(String[] args) {

        DatabaseManager databaseManager = DatabaseManager.produccion();
        databaseManager.inicializarBaseDeDatos();

        RepositorioDestinatarios repositorioDestinatarios = new RepositorioDestinatarios(databaseManager);

        IdentificadorDocumento identificadorDocumento = new IdentificadorPorCodigo("LIQ");

        ProcesadorDocumentos procesadorDocumentos = new ProcesadorDocumentos(identificadorDocumento);

        ServicioDocumentos servicioDocumentos = new ServicioDocumentos(procesadorDocumentos);

        try (Scanner scanner = new Scanner(System.in)) {

            MenuDestinatarios menuDestinatarios = new MenuDestinatarios(
                    repositorioDestinatarios,
                    scanner);

            MenuProcesamientoDocumentos menuProcesamientoDocumentos = new MenuProcesamientoDocumentos(
                    servicioDocumentos,
                    scanner);

            MenuPrincipal menuPrincipal = new MenuPrincipal(
                    menuDestinatarios,
                    menuProcesamientoDocumentos,
                    scanner);

            menuPrincipal.mostrar();
        }
    }
}
