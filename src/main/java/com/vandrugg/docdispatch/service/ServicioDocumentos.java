package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.util.List;

import com.vandrugg.docdispatch.model.Documento;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;

public class ServicioDocumentos {

    private final RepositorioConfiguracion repositorioConfiguracion;


    public ServicioDocumentos(
        RepositorioConfiguracion repositorioConfiguracion
    ) {

        if(repositorioConfiguracion == null){
            throw new IllegalArgumentException(
                "El repositorio de configuracion es obligatorio."
            );
        }
        this.repositorioConfiguracion = repositorioConfiguracion;
    }

    public List<Documento> analizarCarpeta(Path carpeta){
        
        String codigo = repositorioConfiguracion.obtenerCodigoDocumento();

        if(codigo == null){
            throw new IllegalStateException(
                "No existe un codigo de documento configurado."
            );
        }

        IdentificadorDocumento identificador = new IdentificadorPorCodigo(codigo); 

        ProcesadorDocumentos procesador = new ProcesadorDocumentos(identificador);

        return procesador.obtenerDocumentos(carpeta);
    }







}
