package com.vandrugg.docdispatch.model;

import java.nio.file.Path;
import java.util.List;

public record SolicitudCorreo(
    List<String> destinatarios,
    String asunto,
    String cuerpo,
    List<Path> adjuntos
) {

}
