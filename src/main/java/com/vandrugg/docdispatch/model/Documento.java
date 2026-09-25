package com.vandrugg.docdispatch.model;

import java.nio.file.Path;

public record Documento(
        int numeroDocumento,
        String nombreArchivo,
        Path ruta) {

}
