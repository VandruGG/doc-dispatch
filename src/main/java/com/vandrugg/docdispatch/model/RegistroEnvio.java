package com.vandrugg.docdispatch.model;

import java.time.LocalDateTime;

import com.vandrugg.docdispatch.enums.EstadoEnvio;

public record RegistroEnvio(
    int id,
    LocalDateTime fechaHora,
    int numeroDocumento,
    EstadoEnvio estado,
    String destinatarios,
    String archivos,
    String detalle
) {

}
