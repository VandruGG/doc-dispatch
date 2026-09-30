package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.util.List;

import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;

public interface ServicioCorreo {

    ResultadoConexionCorreo probarConexion();

    ResultadoEnvio enviar(
            List<String> destinatarios,
            List<Path> adjuntos);
}
