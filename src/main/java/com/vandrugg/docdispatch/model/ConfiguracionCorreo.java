package com.vandrugg.docdispatch.model;

import com.vandrugg.docdispatch.enums.SeguridadSmtp;

public record ConfiguracionCorreo(
        String host,
        int puerto,
        String usuario,
        String password,
        SeguridadSmtp seguridad) {

}
