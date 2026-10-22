package com.vandrugg.docdispatch.enums;

public enum ProveedorCorreo {

    GMAIL("smtp.gmail.com", 587, SeguridadSmtp.STARTTLS),
    OUTLOOK("smtp-mail.outlook.com", 587, SeguridadSmtp.STARTTLS),
    PERSONALIZADO(null, 0, null);

    private final String host;
    private final int puerto;
    private final SeguridadSmtp seguridad;

    ProveedorCorreo(
        String host,
        int puerto,
        SeguridadSmtp seguridad
    ){
        this.host = host;
        this.puerto = puerto;
        this.seguridad = seguridad;
    }

    public String host(){
        return host;
    }

    public int puerto(){
        return puerto;
    }

    public SeguridadSmtp seguridad(){
        return seguridad;
    }
}
