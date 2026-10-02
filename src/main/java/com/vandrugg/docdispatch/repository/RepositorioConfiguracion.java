package com.vandrugg.docdispatch.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vandrugg.docdispatch.database.DatabaseManager;

public class RepositorioConfiguracion {

    private static final String CLAVE_CODIGO_DOCUMENTO = "codigo_documento";
    private static final String CLAVE_ASUNTO_CORREO = "asunto_correo";
    private static final String CLAVE_CUERPO_CORREO = "cuerpo_correo";

    private final DatabaseManager databaseManager;

    public RepositorioConfiguracion(
            DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String obtenerCodigoDocumento() {
        return obtenerValor(CLAVE_CODIGO_DOCUMENTO);
    }

    public void guardarCodigoDocumento(String codigo) {
        guardarValor(CLAVE_CODIGO_DOCUMENTO, codigo);
    }

    public String obtenerAsuntoCorreo() {
        return obtenerValor(CLAVE_ASUNTO_CORREO);
    }

    public void guardarAsuntCorreo(String asunto) {
        guardarValor(CLAVE_ASUNTO_CORREO, asunto);
    }

    public String obtenerCuerpoCorreo() {
        return obtenerValor(CLAVE_CUERPO_CORREO);
    }

    public void guardarCuerpoCorreo(String cuerpo){
        guardarValor(CLAVE_CUERPO_CORREO, cuerpo);
    }

    private String obtenerValor(String clave) {

        String sql = "SELECT valor FROM configuracion WHERE clave = ?";

        try (Connection connection = databaseManager.obtenerConexion();

                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, clave);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getString("valor");
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al obtener la configuracion: "
                            + clave,
                    e);
        }
    }

    private void guardarValor(
            String clave,
            String valor) {

        String sql = """
                INSERT INTO configuracion (clave, valor)
                VALUES (?, ?)
                ON CONFLICT(clave)
                DO UPDATE SET valor = excluded.valor
                """;

        try (Connection connection = databaseManager.obtenerConexion();

                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, clave);
            statement.setString(2, valor);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar la configuracion: "
                            + clave,
                    e);
        }
    }
}
