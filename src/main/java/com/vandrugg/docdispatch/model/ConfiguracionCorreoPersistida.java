package com.vandrugg.docdispatch.model;

import com.vandrugg.docdispatch.enums.SeguridadSmtp;

public record ConfiguracionCorreoPersistida(
    String proveedor,
    String usuario,
    String host,
    int puerto,
    SeguridadSmtp seguridad,
    String passwordCifrada,
    String claveProtegida
) {

}
