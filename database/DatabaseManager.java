package com.vandrugg.docdispatch.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final Path CARPETA_DATA = Path.of("data");
    private static final Path ARCHIVO_BD = CARPETA_DATA.resolve("docdispatch.db");

    private static final String URL = "jdbc:sqlite:" + ARCHIVO_BD.toString();
    
    private DatabaseManager(){}

    public static Connection obtenerConexion() throws SQLException {
        crearCarpetaDataSiNoExiste();
        return DriverManager.getConnection(URL);        
    }

    public static void inicializarBaseDeDatos(){
        crearCarpetaDataSiNoExiste();

        String sql = """
                CREATE TABLE IF NOT EXISTS destinatarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_documento INTEGER NOT NULL,
                email TEXT NOT NULL,
                activo INTEGER NOT NULL DEFAULT 1,
                UNIQUE(numero_documento, email)
                )
                """;

        try(Connection conexion = obtenerConexion();
            Statement statement = conexion.createStatement()){

                statement.execute(sql);
        } catch(SQLException e){
            throw new IllegalStateException(
                "No se pudo inicializar la base de datos.", e);
        }
    }

    private static void crearCarpetaDataSiNoExiste(){

        try(
            Files.createDirectories(CARPETA_DATA);
        ) catch(IOException e){
            throw new IllegalStateException(
                "No se pudo crear la carpeta de datos.",e);
        }
    }
}
