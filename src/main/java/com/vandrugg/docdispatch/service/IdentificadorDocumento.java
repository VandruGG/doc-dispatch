package com.vandrugg.docdispatch.service;

public interface IdentificadorDocumento {

    Integer obtenerNumeroDocumento(String nombreArchivo);

    String obtenerCodigo();
}
