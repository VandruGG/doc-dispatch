package com.vandrugg.docdispatch.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class IdentificadorPorCodigoTest {

    @Test
    void debeObtenerNumeroDocumento() {

        IdentificadorPorCodigo identificador = new IdentificadorPorCodigo("LIQ");

        Integer numero = identificador.obtenerNumeroDocumento("Liq166.pdf");

        assertEquals(166, numero);
    }

    @Test
    void debeIgnorarMayusculasYMinusculas() {

        IdentificadorPorCodigo identificador = new IdentificadorPorCodigo("LIQ");

        assertEquals(200, identificador.obtenerNumeroDocumento("liq200.pdf"));
        assertEquals(300, identificador.obtenerNumeroDocumento("LIQ300.pdf"));
    }

    @Test
    void debeAceptarCodigoEnMinusculas() {

        IdentificadorPorCodigo identificador = new IdentificadorPorCodigo("fac");

        assertEquals("FAC", identificador.obtenerCodigo());
    }

    @Test
    void debeIgnorarArchivoSinCodigoCorrespondiente() {

        IdentificadorPorCodigo identificador = new IdentificadorPorCodigo("LIQ");

        Integer numero = identificador.obtenerNumeroDocumento("FAC166.pdf");

        assertNull(numero);
    }

    @Test
    void debeRechazarCodigoConMenosDeTresLetras() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new IdentificadorPorCodigo("LI"));
    }

    @Test
    void debeRechazarCodigoConMasDeTresLetras() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new IdentificadorPorCodigo("LiQU"));
    }

    @Test
    void debeRechazarCodigoConNumeros() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new IdentificadorPorCodigo("L2Q"));
    }

    @Test
    void debeRechazarCodigoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new IdentificadorPorCodigo(null));
    }
}
