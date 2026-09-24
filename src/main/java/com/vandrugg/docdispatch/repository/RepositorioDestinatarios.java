package com.vandrugg.docdispatch.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.model.Destinatario;

public class RepositorioDestinatarios {

    private final DatabaseManager databaseManager;

    public RepositorioDestinatarios(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
    }

    public void guardarDestinatario(int numeroDocumento, String email) {
        String sql = """
                INSERT INTO destinatarios (numero_documento, email, activo)
                VALUES (?, ?, 1)
                ON CONFLICT(numero_documento, email)
                DO UPDATE SET activo = 1
                """;
        try (
                Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, numeroDocumento);
            statement.setString(2, email);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo guardar el destinatario.", e);
        }
    }

    public List<String> obtenerDestinatarios(int numeroDocumento) {
        String sql = """
                SELECT email
                FROM destinatarios
                WHERE numero_documento = ?
                AND activo = 1
                ORDER BY email
                """;

        List<String> destinatarios = new ArrayList<>();

        try (
                Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);) {
            statement.setInt(1, numeroDocumento);

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    destinatarios.add(resultado.getString("email"));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudieron obtener los destinatarios.", e);
        }
        return destinatarios;
    }

    public void eliminarDestinatario(int numeroDocumento, String email) {
        String sql = """
                UPDATE destinatarios
                SET activo = 0
                WHERE numero_documento = ?
                AND email = ?
                """;

        try (
                Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);) {
            statement.setInt(1, numeroDocumento);
            statement.setString(2, email);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo eliminar el destinatario.", e);
        }
    }

    public List<Destinatario> listarDestinatarios() {
        String sql = """
                SELECT id, numero_documento, email, activo
                FROM destinatarios
                WHERE activo = 1
                ORDER BY numero_documento, email
                """;

        List<Destinatario> destinatarios = new ArrayList<>();

        try (
                Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                int id = resultado.getInt("id");
                int numeroDocumento = resultado.getInt("numero_documento");
                String email = resultado.getString("email");
                boolean activo = resultado.getInt("activo") == 1;
                destinatarios.add(
                        new Destinatario(
                                id,
                                numeroDocumento,
                                email,
                                activo));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudieron listar los destinatarios.", e);
        }

        return destinatarios;
    }
}
