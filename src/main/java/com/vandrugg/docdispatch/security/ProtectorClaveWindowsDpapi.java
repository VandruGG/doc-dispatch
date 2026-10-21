package com.vandrugg.docdispatch.security;

import com.sun.jna.platform.win32.Crypt32Util;

public class ProtectorClaveWindowsDpapi implements ProtectorClave{

    @Override 
    public byte[] proteger(byte[] datos){

        if(datos == null || datos.length == 0){
            throw new IllegalArgumentException(
                "Los datos a proteger son obligatorios."
            );
        }

        return Crypt32Util.cryptProtectData(datos);
    }

    @Override 
    public byte[] desproteger(byte[] datos){

        if(datos == null || datos.length == 0){
            throw new IllegalArgumentException(
                "Los datos protegidos son obligatorios."
            );
        }

        return Crypt32Util.cryptUnprotectData(datos);
    }
}
