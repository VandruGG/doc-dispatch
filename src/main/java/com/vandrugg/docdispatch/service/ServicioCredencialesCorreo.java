package com.vandrugg.docdispatch.service;

import java.util.Base64;

import com.vandrugg.docdispatch.enums.SeguridadSmtp;
import com.vandrugg.docdispatch.model.ConfiguracionCorreo;
import com.vandrugg.docdispatch.model.ConfiguracionCorreoPersistida;
import com.vandrugg.docdispatch.model.ResumenConfiguracionCorreo;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracionCorreo;
import com.vandrugg.docdispatch.security.CifradorCredenciales;
import com.vandrugg.docdispatch.security.ProtectorClave;

public class ServicioCredencialesCorreo {

    private final RepositorioConfiguracionCorreo repositorio;
    private final CifradorCredenciales cifrador;
    private final ProtectorClave protectorClave;

    public ServicioCredencialesCorreo(
            RepositorioConfiguracionCorreo repositorio,
            CifradorCredenciales cifrador,
            ProtectorClave protectorClave) {

        if (repositorio == null || cifrador == null || protectorClave == null) {
            throw new IllegalArgumentException(
                    "Las dependencias de credenciales son obligatorias.");
        }

        this.repositorio = repositorio;
        this.cifrador = cifrador;
        this.protectorClave = protectorClave;
    }

    public void guardar(
            String proveedor,
            String usuario,
            String password,
            String host,
            int puerto,
            SeguridadSmtp seguridad) {
        validar(
                proveedor,
                usuario,
                password,
                host,
                puerto,
                seguridad);

        byte[] claveAes = cifrador.generarClave();

        String passwordCifrada = cifrador.cifrar(
                password,
                claveAes);

        byte[] claveProtegida = protectorClave.proteger(claveAes);

        String claveProtegidaBase64 = Base64.getEncoder().encodeToString(claveProtegida);

        ConfiguracionCorreoPersistida persistida = new ConfiguracionCorreoPersistida(
                proveedor.trim().toUpperCase(),
                usuario.trim(),
                host.trim(),
                puerto,
                seguridad,
                passwordCifrada,
                claveProtegidaBase64);

        repositorio.guardar(persistida);
    }

    public ConfiguracionCorreo obtener() {

        ConfiguracionCorreoPersistida persistida = repositorio.obtener();

        if (persistida == null) {
            return null;
        }

        try {
            byte[] claveProtegida = Base64.getDecoder().decode(persistida.claveProtegida());

            byte[] claveAes = protectorClave.desproteger(claveProtegida);

            String password = cifrador.descifrar(
                    persistida.passwordCifrada(),
                    claveAes);

            return new ConfiguracionCorreo(
                    persistida.host(),
                    persistida.puerto(),
                    persistida.usuario(),
                    password,
                    persistida.seguridad());

        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "No se puedieron recuperar las credenciales de correo. "
                            + "Puede que pertenezcan a otro usuario o equipo.",
                    e);
        }
    }

    public boolean estaConfigurado() {
        return repositorio.obtener() != null;
    }

    private void validar(
            String proveedor,
            String usuario,
            String password,
            String host,
            int puerto,
            SeguridadSmtp seguridad) {
        if (proveedor == null || proveedor.isBlank()) {
            throw new IllegalArgumentException(
                    "El proveedor es obligatorio.");
        }

        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException(
                    "El usuario de correo es obligatorio.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException(
                    "El host SMTP es obligatorio.");
        }

        if (puerto <= 0) {
            throw new IllegalArgumentException(
                    "El puerto SMTP debe ser mayor a cero.");
        }

        if (seguridad == null) {
            throw new IllegalArgumentException(
                    "La seguridad SMTP es obligatoria.");
        }
    }

    public ResumenConfiguracionCorreo obtenerResumen() {

        ConfiguracionCorreoPersistida persistida = repositorio.obtener();

        if (persistida == null) {
            return null;
        }

        return new ResumenConfiguracionCorreo(
                persistida.proveedor(),
                persistida.usuario(),
                persistida.host(),
                persistida.puerto(),
                persistida.seguridad());
    }
}
