package com.vandrugg.docdispatch.model;

import java.util.List;

public record PreparacionEnvio(
    int numeroDocumento,
    List<Documento> documentos,
    List<String> destinatarios
) {

    public boolean tieneDestinatarios(){
        return !destinatarios.isEmpty();
    }
}
