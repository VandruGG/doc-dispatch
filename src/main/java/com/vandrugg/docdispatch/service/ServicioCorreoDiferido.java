package com.vandrugg.docdispatch.service;

import com.vandrugg.docdispatch.model.ConfiguracionCorreo;
import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;
import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.SolicitudCorreo;
import com.vandrugg.docdispatch.enums.EstadoEnvio;

public class ServicioCorreoDiferido implements ServicioCorreo {

    private final ServicioCredencialesCorreo servicioCredencialesCorreo;

    public ServicioCorreoDiferido(
            ServicioCredencialesCorreo servicioCredencialesCorreo) {

        if (servicioCredencialesCorreo == null) {
            throw new IllegalArgumentException(
                    "El servicio de credenciales es obligatorio.");
        }

        this.servicioCredencialesCorreo = servicioCredencialesCorreo;
    }

    private ServicioCorreo crearServicioReal() {

        ConfiguracionCorreo configuracionCorreo = servicioCredencialesCorreo.obtener();

        if (configuracionCorreo == null) {
            throw new IllegalStateException(
                    "No existe una configuracion de correo.");
        }

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
                    EstadoEnvio.ERROR,
                    e.getMessage());
        }
    }
}
