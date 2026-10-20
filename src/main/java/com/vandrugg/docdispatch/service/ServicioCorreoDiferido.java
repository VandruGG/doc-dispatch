package com.vandrugg.docdispatch.service;

import com.vandrugg.docdispatch.config.ConfiguracionCorreoFactory;
import com.vandrugg.docdispatch.model.ConfiguracionCorreo;
import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;
import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.SolicitudCorreo;

public class ServicioCorreoDiferido implements ServicioCorreo {

    private ServicioCorreo crearServicioReal() {

        ConfiguracionCorreo configuracionCorreo =
                ConfiguracionCorreoFactory.desdeEntorno();

        return new ServicioCorreoJakartaMail(
                configuracionCorreo);
    }

    @Override
    public ResultadoConexionCorreo probarConexion() {

        try {
            return crearServicioReal()
                    .probarConexion();

        } catch (IllegalStateException e) {

            return new ResultadoConexionCorreo(
                    false,
                    e.getMessage());
        }
    }

    @Override
    public ResultadoEnvio enviar(
            SolicitudCorreo solicitud) {

        try {
            return crearServicioReal()
                    .enviar(solicitud);

        } catch (IllegalStateException e) {

            return new ResultadoEnvio(
                    com.vandrugg.docdispatch.enums.EstadoEnvio.ERROR,
                    e.getMessage());
        }
    }
}
