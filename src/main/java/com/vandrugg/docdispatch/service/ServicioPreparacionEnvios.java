package com.vandrugg.docdispatch.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.vandrugg.docdispatch.model.Documento;
import com.vandrugg.docdispatch.model.ResultadoEnvio;
import com.vandrugg.docdispatch.model.PreparacionEnvio;
import com.vandrugg.docdispatch.model.ResultadoSimulacion;
import com.vandrugg.docdispatch.model.SolicitudCorreo;
import com.vandrugg.docdispatch.enums.EstadoEnvio;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;

public class ServicioPreparacionEnvios {

    private final ServicioDocumentos servicioDocumentos;
    private final ServicioCorreo servicioCorreo;
    private final RepositorioDestinatarios repositorioDestinatarios;
    private final RepositorioConfiguracion repositorioConfiguracion;

    public ServicioPreparacionEnvios(
            ServicioDocumentos servicioDocumentos,
            RepositorioDestinatarios repositorioDestinatarios,
            ServicioCorreo servicioCorreo,
            RepositorioConfiguracion repositorioConfiguracion) {

        if (servicioDocumentos == null) {
            throw new IllegalArgumentException(
                    "El servicio de documentos es obligatorio.");
        }

        if (servicioCorreo == null) {
            throw new IllegalArgumentException(
                    "El servicio de correo es obligatorio.");
        }

        if (repositorioDestinatarios == null) {
            throw new IllegalArgumentException(
                    "El repositorio de destinatarios es obligatorio.");
        }

        if (repositorioConfiguracion == null) {
            throw new IllegalArgumentException(
                    "El repositorio de configuracion es obligatorio.");
        }

        this.servicioDocumentos = servicioDocumentos;
        this.repositorioDestinatarios = repositorioDestinatarios;
        this.servicioCorreo = servicioCorreo;
        this.repositorioConfiguracion = repositorioConfiguracion;
    }

    public List<PreparacionEnvio> preparar(Path carpeta) {

        List<Documento> documentos = servicioDocumentos.analizarCarpeta(carpeta);

        Map<Integer, List<Documento>> documentosPorNumero = agruparDocumentos(documentos);

        List<PreparacionEnvio> preparaciones = new ArrayList<>();

        for (Map.Entry<Integer, List<Documento>> grupo : documentosPorNumero.entrySet()) {

            int numeroDocumento = grupo.getKey();

            List<String> destinatarios = repositorioDestinatarios.obtenerDestinatarios(numeroDocumento);

            preparaciones.add(
                    new PreparacionEnvio(
                            numeroDocumento,
                            List.copyOf(grupo.getValue()),
                            List.copyOf(destinatarios)));
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

    public List<ResultadoSimulacion> simular(Path carpeta) {

        List<PreparacionEnvio> preparaciones = preparar(carpeta);

        List<ResultadoSimulacion> resultados = new ArrayList<>();

        String asunto = repositorioConfiguracion.obtenerAsuntoCorreo();

        String cuerpo = repositorioConfiguracion.obtenerCuerpoCorreo();

        boolean asuntoValido = asunto != null && !asunto.isBlank();

        boolean cuerpoValido = cuerpo != null && !cuerpo.isBlank();

        for (PreparacionEnvio preparacion : preparaciones) {

            boolean tieneDestinatarios = preparacion.tieneDestinatarios();

            boolean tieneDocumentos = !preparacion.documentos().isEmpty();

            boolean listo = tieneDestinatarios
                    && tieneDocumentos
                    && asuntoValido
                    && cuerpoValido;

            String detalle;

            if (!tieneDestinatarios) {
                detalle = "Sin destinatarios configurados.";
            } else if (!tieneDocumentos) {
                detalle = "Sin documentos para enviar.";
            } else if (!asuntoValido) {
                detalle = "El asunto del correo no esta configurado.";
            } else if (!cuerpoValido) {
                detalle = "El cuerpo del correo no esta configurado.";
            } else {
                detalle = "Envio preparado correctamente.";
            }

            resultados.add(
                    new ResultadoSimulacion(
                            preparacion.numeroDocumento(),
                            preparacion.documentos().size(),
                            preparacion.destinatarios().size(),
                            listo,
                            asunto,
                            cuerpo,
                            detalle));
        }

        return resultados;
    }

    public List<ResultadoEnvio> enviar(Path carpeta) {

        List<PreparacionEnvio> preparaciones = preparar(carpeta);

        List<ResultadoEnvio> resultados = new ArrayList<>();

        String asunto = repositorioConfiguracion.obtenerAsuntoCorreo();

        if (asunto == null || asunto.isBlank()) {
            throw new IllegalStateException(
                    "El asunto del correo no esta configurado.");
        }

        String cuerpo = repositorioConfiguracion.obtenerCuerpoCorreo();

        if (cuerpo == null || cuerpo.isBlank()) {
            throw new IllegalStateException(
                    "El cuerpo del correo no esta configurado.");
        }

        for (PreparacionEnvio preparacion : preparaciones) {

            if (!preparacion.tieneDestinatarios()) {
                resultados.add(
                        new ResultadoEnvio(
                                EstadoEnvio.SIN_DESTINATARIO,
                                "Documento "
                                        + preparacion.numeroDocumento()
                                        + ": sin destinatarios configurados."));
                continue;
            }

            List<Path> adjuntos = preparacion.documentos().stream().map(Documento::ruta).toList();

            SolicitudCorreo solicitud = new SolicitudCorreo(
                    preparacion.destinatarios(),
                    asunto,
                    cuerpo,
                    adjuntos);

            ResultadoEnvio resultadoCorreo = servicioCorreo.enviar(
                    solicitud);

            ResultadoEnvio resultado = new ResultadoEnvio(
                    resultadoCorreo.estado(),
                    "Documento "
                            + preparacion.numeroDocumento()
                            + ": "
                            + resultadoCorreo.detalle());

            resultados.add(resultado);
        }

        return resultados;
    }
}
