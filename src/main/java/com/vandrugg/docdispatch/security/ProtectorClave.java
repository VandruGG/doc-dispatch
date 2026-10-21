package com.vandrugg.docdispatch.security;

public interface ProtectorClave {

    byte[] proteger(byte[] datos);

    byte[] desproteger(byte[] datos);

}
