package com.vandrugg.docdispatch.model;

public record Destinatario(
    int id,
    int numeroDocumento,
    String email,
    boolean activo
) {

}
