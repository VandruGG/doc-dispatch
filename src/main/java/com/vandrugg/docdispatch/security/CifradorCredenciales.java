package com.vandrugg.docdispatch.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CifradorCredenciales {

    private static final int TAMANIO_CLAVE_AES = 256;
    private static final int TAMANIO_IV = 12;
    private static final int TAMANIO_TAG = 128;

    private final SecureRandom secureRandom = new SecureRandom();

    public byte[] generarClave() {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

            keyGenerator.init(TAMANIO_CLAVE_AES);

            return keyGenerator.generateKey().getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "No se pudo generar la clave AES.",
                    e);
        }
    }

    public String cifrar(
            String texto,
            byte[] clave) {

        if (texto == null) {
            throw new IllegalArgumentException(
                    "El texto es obligatorio.");
        }

        validarClave(clave);

        try {
            byte[] iv = new byte[TAMANIO_IV];

            secureRandom.nextBytes(iv);

            SecretKey secretKey = new SecretKeySpec(
                    clave,
                    "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            GCMParameterSpec especificacion = new GCMParameterSpec(
                    TAMANIO_TAG,
                    iv);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    especificacion);

            byte[] cifrado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cifrado.length);

            buffer.put(iv);
            buffer.put(cifrado);

            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "No se pudo cifrar la credencial.",
                    e);
        }
    }

    public String descifrar(
            String textoCifrado,
            byte[] clave) {

        if (textoCifrado == null || textoCifrado.isBlank()) {
            throw new IllegalArgumentException(
                    "La credencial cifrada es obligatoria.");
        }

        validarClave(clave);

        try {
            byte[] datos = Base64.getDecoder().decode(textoCifrado);

            ByteBuffer buffer = ByteBuffer.wrap(datos);

            byte[] iv = new byte[TAMANIO_IV];

            buffer.get(iv);

            byte[] cifrado = new byte[buffer.remaining()];

            buffer.get(cifrado);

            SecretKey secretKey = new SecretKeySpec(
                    clave,
                    "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            GCMParameterSpec especificacion = new GCMParameterSpec(
                    TAMANIO_TAG,
                    iv);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    especificacion);

            byte[] resultado = cipher.doFinal(cifrado);

            return new String(
                resultado,
                StandardCharsets.UTF_8
            );

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalStateException(
                "No se pudo descifrar la credencial.",
                e
            );
        }
    }

    private void validarClave(byte[] clave){
        if(clave == null || clave.length == 0){
            throw new IllegalArgumentException(
                "La clave AES debe tener 256 bits."
            );
        }
    }
}
