package com.vandrugg.docdispatch.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.enums.EstadoEnvio;
import com.vandrugg.docdispatch.model.RegistroEnvio;

public class RepositorioHistorialEnvios {

    private final DatabaseManager databaseManager;

    public RepositorioHistorialEnvios(
            DatabaseManager databaseManager) {

        if (databaseManager == null) {
            throw new IllegalArgumentException(
                    "El database manager es obligatorio.");
        }

        this.databaseManager = databaseManager;
    }

    public void registrar(
            int numeroDocumento,
            EstadoEnvio estado,
            String destinatarios,
            String archivos,
            String detalle) {
        if (numeroDocumento <= 0) {
            throw new IllegalArgumentException(
                    "El numero de documento debe ser mayor a cero.");
        }

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio.");
        }

        if (destinatarios == null) {
            throw new IllegalArgumentException(
                    "Los destinatarios son obligatorios.");
        }

        if (archivos == null) {
            throw new IllegalArgumentException(
                    "Los archivos son obligatorios.");
        }

        if (detalle == null || detalle.isBlank()) {
            throw new IllegalArgumentException(
                    "El detalle es obligatorio.");
        }

        String sql = """
                INSERT INTO historial_envios (
                fecha_hora,
                numero_documento,
                estado,
                destinatarios,
                archivos,
                detalle
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, LocalDateTime.now().toString());
            statement.setInt(2, numeroDocumento);
            statement.setString(3, estado.name());
            statement.setString(4, destinatarios);
            statement.setString(5, archivos);
            statement.setString(6, detalle);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al registrar el historial de envio.",
                    e);
        }
    }

    public List<RegistroEnvio> listar() {

        String sql = """
                SELECT
                id,
                fecha_hora,
                numero_documento,
                estado,
                destinatarios,
                archivos,
                detalle
                FROM historial_envios
                ORDER BY fecha_hora DESC, id DESC
                """;

        List<RegistroEnvio> registros = new ArrayList<>();

        try (Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                registros.add(mapearRegistro(resultSet));
            }
            return registros;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar el historial de envios.",
                    e);
        }
    }

    public RegistroEnvio buscarPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor a cero.");
        }

        String sql = """
                SELECT
                    id,
                    fecha_hora,
                    numero_documento,
                    estado,
                    destinatarios,
                    archivos,
                    detalle
                FROM historial_envios
                WHERE id = ?
                """;

        try (Connection connection = databaseManager.obtenerConexion();

                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapearRegistro(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el historial de envio.",
                    e);
        }
    }

    private RegistroEnvio mapearRegistro(
            ResultSet resultSet) throws SQLException {

        return new RegistroEnvio(
                resultSet.getInt("id"),
                LocalDateTime.parse(
                        resultSet.getString("fecha_hora")),
                resultSet.getInt("numero_documento"),
                EstadoEnvio.valueOf(
                        resultSet.getString("estado")),
                resultSet.getString("destinatarios"),
                resultSet.getString("archivos"),
                resultSet.getString("detalle"));
    }
}
