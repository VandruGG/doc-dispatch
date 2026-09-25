package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.util.List;

import com.vandrugg.docdispatch.model.Documento;

public class ServicioDocumentos {

    private final ProcesadorDocumentos procesadorDocumentos;

    public ServicioDocumentos(
        ProcesadorDocumentos procesadorDocumentos
    ) {

        if(procesadorDocumentos== null){
            throw new IllegalArgumentException(
                "El procesador de documentos es obligatorio."
            );
        }
        this.procesadorDocumentos = procesadorDocumentos;
    }

    public List<Documento> analizarCarpeta(Path carpeta){
        return procesadorDocumentos.obtenerDocumentos(carpeta);
    }







}
