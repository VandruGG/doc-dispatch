package com.vandrugg.docdispatch.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.enums.SeguridadSmtp;
import com.vandrugg.docdispatch.model.ConfiguracionCorreoPersistida;

public class RepositorioConfiguracionCorreo {

    private static final String CLAVE_PROVEEDOR = "mail_provider";
    private static final String CLAVE_USUARIO = "mail_user";
    private static final String CLAVE_HOST = "mail_host";
    private static final String CLAVE_PUERTO = "mail_port";
    private static final String CLAVE_SEGURIDAD = "mail_security";
    private static final String CLAVE_PASSWORD = "mail_password";
    private static final String CLAVE_AES = "mail_aes_key";

    private final DatabaseManager databaseManager;

    public RepositorioConfiguracionCorreo(
            DatabaseManager databaseManager) {
        if (databaseManager == null) {
            throw new IllegalArgumentException(
                    "El database manager es obligatorio.");
        }

        this.databaseManager = databaseManager;
    }

    public void guardar(
            ConfiguracionCorreoPersistida configuracion) {
        if (configuracion == null) {
            throw new IllegalArgumentException(
                    "La configuracion de correo es obligatoria.");
        }

        try (Connection conexion = databaseManager.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                guardarValor(
                        conexion,
                        CLAVE_PROVEEDOR,
                        configuracion.proveedor());
                guardarValor(
                        conexion,
                        CLAVE_USUARIO,
                        configuracion.usuario());
                guardarValor(
                        conexion,
                        CLAVE_HOST,
                        configuracion.host());
                guardarValor(
                        conexion,
                        CLAVE_PUERTO,
                        String.valueOf(
                                configuracion.puerto()));
                guardarValor(
                        conexion,
                        CLAVE_SEGURIDAD,
                        configuracion.seguridad().name());
                guardarValor(
                        conexion,
                        CLAVE_PASSWORD,
                        configuracion.passwordCifrada());
                guardarValor(
                        conexion,
                        CLAVE_AES,
                        configuracion.claveProtegida());

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar la configuracion de correo.",
                    e);
        }
    }

    public ConfiguracionCorreoPersistida obtener() {

        String proveedor = obtenerValor(CLAVE_PROVEEDOR);

        if (proveedor == null) {
            return null;
        }

        String usuario = obtenerValor(CLAVE_USUARIO);

        String host = obtenerValor(CLAVE_HOST);

        String puertoTexto = obtenerValor(CLAVE_PUERTO);

        String seguridadTexto = obtenerValor(CLAVE_SEGURIDAD);

        String passwordCifrada = obtenerValor(CLAVE_PASSWORD);

        String claveProtegida = obtenerValor(CLAVE_AES);

        try {
            int puerto = Integer.parseInt(puertoTexto);

            SeguridadSmtp seguridad = SeguridadSmtp.valueOf(seguridadTexto);

            return new ConfiguracionCorreoPersistida(
                    proveedor,
                    usuario,
                    host,
                    puerto,
                    seguridad,
                    passwordCifrada,
                    claveProtegida);

        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "La configuracion de correo almacedana no es valida.",
                    e);
        }
    }

    private void guardarValor(
            Connection conexion,
            String clave,
            String valor) throws SQLException {

        String sql = """
                INSERT INTO configuracion (clave, valor)
                VALUES (?, ?)
                ON CONFLICT(clave)
                DO UPDATE SET valor = excluded.valor
                """;

        try (PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, clave);
            statement.setString(2, valor);
            statement.executeUpdate();

        }
    }

    private String obtenerValor(String clave) {

        String sql = """
                    SELECT valor
                    FROM configuracion
                    WHERE clave = ?
                """;

        try (Connection conexion = databaseManager.obtenerConexion();
                PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setString(1, clave);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("valor");
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al obtener la configuracion de correo.",
                    e);
        }
    }
}
