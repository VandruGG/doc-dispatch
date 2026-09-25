package com.vandrugg.docdispatch.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vandrugg.docdispatch.model.Documento;

public class ProcesadorDocumentosTest {

    @Test
    void debeObternerDocumentosValidos(@TempDir Path carpeta) throws IOException {

        Files.createFile(carpeta.resolve("LIQ166.pdf"));
        Files.createFile(carpeta.resolve("LIQ200.pdf"));
        Files.createFile(carpeta.resolve("otroArchivo.txt"));

        ProcesadorDocumentos procesador = new ProcesadorDocumentos(
                new IdentificadorPorCodigo("LIQ"));

        List<Documento> documentos = procesador.obtenerDocumentos(carpeta);

        assertEquals(2, documentos.size());

        assertTrue(
                documentos.stream()
                        .anyMatch(documento -> documento.numeroDocumento() == 166));
        assertTrue(
                documentos.stream()
                        .anyMatch(documento -> documento.numeroDocumento() == 200));
    }

    @Test
    void debePermitirVariosArchivosParaElMismoNumero(
            @TempDir Path carpeta) throws IOException {

        Files.createFile(
                carpeta.resolve("LIQ166.pdf"));

        Files.createFile(
                carpeta.resolve("LIQ166_complemento.pdf"));

        ProcesadorDocumentos procesador = new ProcesadorDocumentos(
                new IdentificadorPorCodigo("LIQ"));

        List<Documento> documentos = procesador.obtenerDocumentos(carpeta);

        assertEquals(2, documentos.size());

        assertTrue(
                documentos.stream()
                        .allMatch(documento -> documento.numeroDocumento() == 166));
    }

    @Test
    void debeUsarElCodigoConfigurado(@TempDir Path carpeta) throws IOException {

        Files.createFile(
                carpeta.resolve("FAC500.pdf"));

        Files.createFile(
                carpeta.resolve("LIQ600.pdf"));

        ProcesadorDocumentos procesador = new ProcesadorDocumentos(
                new IdentificadorPorCodigo("FAC"));

        List<Documento> documentos = procesador.obtenerDocumentos(carpeta);

        assertEquals(1, documentos.size());
        assertEquals(
                500,
                documentos.get(0).numeroDocumento());
    }

    @Test
    void debeDevolverListaVaciaSiNoHayDocumentosValidos(
            @TempDir Path carpeta) throws IOException {

        Files.createFile(
                carpeta.resolve("archivo.txt"));

        Files.createFile(
                carpeta.resolve("documento.pdf"));

        ProcesadorDocumentos procesador = new ProcesadorDocumentos(
                new IdentificadorPorCodigo("LIQ"));

        List<Documento> documentos = procesador.obtenerDocumentos(carpeta);

        assertTrue(documentos.isEmpty());
    }

}
