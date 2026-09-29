package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.util.List;

import com.vandrugg.docdispatch.model.ResultadoEnvio;

public interface ServicioCorreo {

    ResultadoEnvio enviar(
            List<String> destinatarios,
            List<Path> adjuntos);
}
