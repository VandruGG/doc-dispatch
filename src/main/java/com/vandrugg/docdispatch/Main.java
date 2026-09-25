package com.vandrugg.docdispatch;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;
import com.vandrugg.docdispatch.ui.MenuDestinatarios;

public class Main {

    public static void main(String[] args) {

        DatabaseManager databaseManager = DatabaseManager.produccion();
        databaseManager.inicializarBaseDeDatos();

        RepositorioDestinatarios repositorio = new RepositorioDestinatarios(databaseManager);


        MenuDestinatarios menu = new MenuDestinatarios(repositorio);

        menu.mostrar();

        System.out.println("DecDispatch iniciado correctamente.");
        System.out.println("Base de datos inicializado.");

    }
}
