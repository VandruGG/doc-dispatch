package com.vandrugg.docdispatch.service;

import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;
import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.SolicitudCorreo;

public interface ServicioCorreo {

    ResultadoConexionCorreo probarConexion();

    ResultadoEnvio enviar(
            SolicitudCorreo solicitud);
}
