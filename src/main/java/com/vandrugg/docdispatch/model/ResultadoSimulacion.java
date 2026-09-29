package com.vandrugg.docdispatch.model;

public record ResultadoSimulacion(
    int numeroDocumento,
    int cantidadArchivos,
    int cantidadDestinatarios,
    boolean listoParaEnviar,
    String detalle
) {

}
