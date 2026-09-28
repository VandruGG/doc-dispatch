package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vandrugg.docdispatch.model.Documento;
import com.vandrugg.docdispatch.model.PreparacionEnvio;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;

public class ServicioPreparacionEnvios {

    private final ServicioDocumentos servicioDocumentos;
    private final RepositorioDestinatarios repositorioDestinatarios;

    public ServicioPreparacionEnvios(
            ServicioDocumentos servicioDocumentos,
            RepositorioDestinatarios repositorioDestinatarios) {
        if (servicioDocumentos == null) {
            throw new IllegalArgumentException(
                    "El servicio de documentos es obligatorio.");
        }

        if (repositorioDestinatarios == null) {
            throw new IllegalArgumentException(
                    "El repositorio de destinatarios es obligatorio.");
        }

        this.servicioDocumentos = servicioDocumentos;
        this.repositorioDestinatarios = repositorioDestinatarios;
    }

    public List<PreparacionEnvio> preparar(Path carpeta) {

        List<Documento> documentos = servicioDocumentos.analizarCarpeta(carpeta);

        Map<Integer, List<Documento>> documentosPorNumero = agruparDocumentos(documentos);

        List<PreparacionEnvio> preparaciones = new ArrayList<>();

        for(Map.Entry<Integer, List<Documento>> grupo
            : documentosPorNumero.entrySet()
        ) {

            int numeroDocumento = grupo.getKey();

            List<String> destinatarios = repositorioDestinatarios.obtenerDestinatarios(numeroDocumento);

            preparaciones.add(
                new PreparacionEnvio(
                    numeroDocumento,
                    List.copyOf(grupo.getValue()),
                    List.copyOf(destinatarios)
                )
            );
        }
        return preparaciones;
    }

    private Map<Integer, List<Documento>> agruparDocumentos(List<Documento> documentos) {

        Map<Integer, List<Documento>> grupos = new TreeMap<>();

        for (Documento documento : documentos) {
            grupos
                    .computeIfAbsent(
                            documento.numeroDocumento(),
                            numero -> new ArrayList<>())
                    .add(documento);
        }
        return grupos;
    }
}
