package com.vandrugg.docdispatch.service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentificadorPorCodigo implements IdentificadorDocumento {

    private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Za-z]{3}$");

    private final String codigo;
    private final Pattern patronArchivo;

    public IdentificadorPorCodigo(String codigo) {
        validarCodigo(codigo);

        this.codigo = codigo.trim().toUpperCase(Locale.ROOT);

        this.patronArchivo = Pattern.compile(
                "(?i)" + Pattern.quote(this.codigo) + "(\\d+)");
    }

    @Override 
    public Integer obtenerNumeroDocumento(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            return null;
        }

        Matcher matcher = patronArchivo.matcher(nombreArchivo);

        if (!matcher.find()) {
            return null;
        }

        return Integer.valueOf(matcher.group(1));
    }

    @Override 
    public String obtenerCodigo(){
        return codigo;
    }

    private void validarCodigo(String codigo){
        if(codigo == null || codigo.isBlank()){
            throw new IllegalArgumentException(
                "El codigo del documento es obligatorio."
            );
        }

        if(!PATRON_CODIGO.matcher(codigo.trim()).matches()){
            throw new IllegalArgumentException(
                "El codigo del documento debe contener exactamente 3 letras."
            );
        }
    }
}
