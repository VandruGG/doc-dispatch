package com.vandrugg.docdispatch.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.vandrugg.docdispatch.model.Documento;

public class ProcesadorDocumentos {

    private final IdentificadorDocumento identificador;

    public ProcesadorDocumentos(IdentificadorDocumento identificador){

        if(identificador == null){
            throw new IllegalArgumentException(
                "El identificador de documentos es obligatorio."
            );
        }

        this.identificador = identificador;
    }

    public List<Documento> obtenerDocumentos(Path carpeta) {
        validarCarpeta(carpeta);

        List<Documento> documentos = new ArrayList<>();

        try (var archivos = Files.list(carpeta)) {

            archivos
                    .filter(Files::isRegularFile)
                    .forEach(archivo -> {
                        Integer numero = identificador.obtenerNumeroDocumento(
                                archivo.getFileName().toString());

                        if (numero != null) {
                            documentos.add(
                                    new Documento(
                                            numero,
                                            archivo.getFileName().toString(),
                                            archivo));
                        }
                    });
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudieron leer los documentos.",
                    e);
        }

        return documentos;
    }

    private void validarCarpeta(Path carpeta) {
        if (carpeta == null) {
            throw new IllegalArgumentException(
                    "La carpeta es obligatoria.");
        }

        if (!Files.exists(carpeta)) {
            throw new IllegalArgumentException(
                    "La carpeta no existe.");
        }

        if (!Files.isDirectory(carpeta)) {
            throw new IllegalArgumentException(
                    "La ruta indicada no es una carpeta.");
        }
    }
}
