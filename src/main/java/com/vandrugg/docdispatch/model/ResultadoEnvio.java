package com.vandrugg.docdispatch.model;

import com.vandrugg.docdispatch.enums.EstadoEnvio;

public record ResultadoEnvio(
        EstadoEnvio estado,
        String detalle) {
}
