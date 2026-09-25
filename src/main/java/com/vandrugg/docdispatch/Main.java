package com.vandrugg.docdispatch;

import java.util.Scanner;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;
import com.vandrugg.docdispatch.ui.MenuDestinatarios;
import com.vandrugg.docdispatch.ui.MenuPrincipal;

public class Main {

    public static void main(String[] args) {

        DatabaseManager databaseManager = DatabaseManager.produccion();
        databaseManager.inicializarBaseDeDatos();

        RepositorioDestinatarios repositorioDestinatarios = new RepositorioDestinatarios(databaseManager);

        try(Scanner scanner = new Scanner(System.in)) {

            MenuDestinatarios menuDestinatarios = 
                new MenuDestinatarios(
                    repositorioDestinatarios,
                    scanner
                );
                
            MenuPrincipal menuPrincipal = 
                new MenuPrincipal(
                    menuDestinatarios,
                scanner);
            
            menuPrincipal.mostrar();
        }         
    }
}
