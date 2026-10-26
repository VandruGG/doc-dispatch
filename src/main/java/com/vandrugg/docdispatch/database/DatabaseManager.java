package com.vandrugg.docdispatch.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public final class DatabaseManager {

    private final String url;

    public DatabaseManager(String url) {
        this.url = Objects.requireNonNull(url, "La URL de la base de datos es obligatoria.");
    }

    public static DatabaseManager produccion() {
        String localAppData = System.getenv("LOCALAPPDATA");

        if (localAppData == null || localAppData.isBlank()) {
            throw new IllegalStateException(
                    "No se pudo determinar la carpeta LOCALAPPDATA de Windows.");
        }

        Path carpetaAplicacion = Path.of(
                localAppData,
                "DocDispatch");

        try {
            Files.createDirectories(carpetaAplicacion);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo crear la carpeta de datos de DocDispatch.",
                    e);
        }

        Path archivoDb = carpetaAplicacion.resolve(
                "docdispatch.db");

        return new DatabaseManager(
                "jdbc:sqlite:" + archivoDb);
    }

    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void inicializarBaseDeDatos() {
        String sqlDestinatarios = """
                CREATE TABLE IF NOT EXISTS destinatarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero_documento INTEGER NOT NULL,
                    email TEXT NOT NULL,
                    activo INTEGER NOT NULL DEFAULT 1,
                    UNIQUE(numero_documento, email)
                );
                """;

        String sqlConfiguracion = """
                CREATE TABLE IF NOT EXISTS configuracion (
                clave TEXT PRIMARY KEY,
                valor TEXT NOT NULL
                );
                """;

        String sqlHistorialEnvios = """
                CREATE TABLE IF NOT EXISTS historial_envios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_hora TEXT NOT NULL,
                numero_documento INTEGER NOT NULL,
                estado TEXT NOT NULL,
                destinatarios TEXT NOT NULL,
                archivos TEXT NOT NULL,
                detalle TEXT NOT NULL
                );
                """;

        try (
                Connection conexion = obtenerConexion();
                Statement statement = conexion.createStatement()) {
            statement.execute(sqlDestinatarios);
            statement.execute(sqlConfiguracion);
            statement.execute(sqlHistorialEnvios);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo inicializar la base de datos.",
                    e);
        }
    }
}
