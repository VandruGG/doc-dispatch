package com.vandrugg.docdispatch.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vandrugg.docdispatch.database.DatabaseManager;

public class RepositorioConfiguracion {

    private static final String CLAVE_CODIGO_DOCUMENTO = "codigo_documento";

    private final DatabaseManager databaseManager;

    public RepositorioConfiguracion(
            DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String obtenerCodigoDocumento() {

        String sql = """
                SELECT valor
                FROM configuracion
                WHERE clave = ?
                """;

        try (
                Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);) {
            statement.setString(1, CLAVE_CODIGO_DOCUMENTO);

            try (
                    ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getString("valor");
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo obtener la configuracion.",
                    e);
        }

        return null;
    }

    public void guardarCodigoDocumento(String codigo) {

        String sql = """
                INSERT INTO configuracion (clave, valor)
                VALUES (?, ?)
                ON CONFLICT(clave)
                DO UPDATE SET valor = excluded.valor
                """;

        try (
                Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);) {
            statement.setString(1, CLAVE_CODIGO_DOCUMENTO);
            statement.setString(2, codigo);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo guardad la configuracion.",
                    e);
        }
    }

}
