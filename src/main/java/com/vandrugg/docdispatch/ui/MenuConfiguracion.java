package com.vandrugg.docdispatch.ui;

import java.io.IOException;
import java.util.Scanner;

import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;
import com.vandrugg.docdispatch.service.IdentificadorPorCodigo;
import com.vandrugg.docdispatch.service.ServicioCorreo;
import com.vandrugg.docdispatch.service.ServicioCredencialesCorreo;
import com.vandrugg.docdispatch.enums.ProveedorCorreo;
import com.vandrugg.docdispatch.model.ResumenConfiguracionCorreo;
import com.vandrugg.docdispatch.enums.SeguridadSmtp;

public class MenuConfiguracion {

    private final RepositorioConfiguracion repositorioConfiguracion;
    private final ServicioCredencialesCorreo servicioCredencialesCorreo;
    private final ServicioCorreo servicioCorreo;
    private final Scanner scanner;

    public MenuConfiguracion(
            RepositorioConfiguracion repositorioConfiguracion,
            ServicioCredencialesCorreo servicioCredencialesCorreo,
            ServicioCorreo servicioCorreo,
            Scanner scanner) {

        if (repositorioConfiguracion == null) {
            throw new IllegalArgumentException(
                    "El repositorio de configuracion es obligatorio.");
        }

        if (servicioCredencialesCorreo == null) {
            throw new IllegalArgumentException(
                    "El servicio de credenciales es obligatorio.");
        }

        if (servicioCorreo == null) {
            throw new IllegalArgumentException(
                    "El servicio de correo es obligatorio.");
        }

        if (scanner == null) {
            throw new IllegalArgumentException(
                    "El scanner es obligatorio.");
        }

        this.repositorioConfiguracion = repositorioConfiguracion;
        this.servicioCredencialesCorreo = servicioCredencialesCorreo;
        this.servicioCorreo = servicioCorreo;
        this.scanner = scanner;
    }

    public void mostrar() {
        boolean volver = false;

        while (!volver) {
            limpiarPantalla();

            String codigoActual = repositorioConfiguracion.obtenerCodigoDocumento();

            System.out.println("================================");
            System.out.println("          CONFIGURACION");
            System.out.println("================================");
            System.out.println();
            System.out.println(
                    "Codigo de documento actual: "
                            + codigoActual);
            System.out.println();
            System.out.println("1. Cambiar codigo de documento");
            System.out.println("2. Cambiar asunto del correo");
            System.out.println("3. Cambiar cuerpo del correo");
            System.out.println("4. Configurar cuenta de correo");
            System.out.println("5. Probar conexion de correo");
            System.out.println("0. Volver al menu principal");
            System.out.println();
            System.out.print("Seleccione una opcion: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> cambiarCodigoDocumento();
                case "2" -> cambiarAsuntoCorreo();
                case "3" -> cambiarCuerpoCorreo();
                case "4" -> configurarCorreo();
                case "5" -> probarConexionCorreo();
                case "0" -> volver = true;

                default -> {
                    System.out.println();
                    System.out.println("Opcion invalida.");
                    pausar();
                }
            }
        }
    }

    private void cambiarCodigoDocumento() {
        limpiarPantalla();

        System.out.println("=== CAMBIAR CODIGO DE DOCUMENTO ===");
        System.out.println();
        System.out.println(
                "El codigo debe contener exactamente 3 letras.");
        System.out.println();
        System.out.print("Nuevo codigo: ");

        String nuevoCodigo = scanner.nextLine().trim();

        try {
            IdentificadorPorCodigo identificador = new IdentificadorPorCodigo(nuevoCodigo);

            repositorioConfiguracion.guardarCodigoDocumento(identificador.obtenerCodigo());

            System.out.println();
            System.out.println(
                    "Codigo actualizado correctamente a: "
                            + identificador.obtenerCodigo());

            System.out.println();
            System.out.println(
                    "El nuevo codigo se utilizara en el procesamiento de documentos.");
        } catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println(
                    "Error: " + e.getMessage());
        }
        pausar();
    }

    private void cambiarAsuntoCorreo() {

        System.out.println();
        System.out.print("Nuevo asunto: ");

        String asunto = scanner.nextLine().trim();

        if (asunto.isBlank()) {
            System.out.println(
                    "El asunto no puede estar vacio.");
            return;
        }

        repositorioConfiguracion.guardarAsuntoCorreo(asunto);

        System.out.println("Asunto actualizado correctamente.");
    }

    private void cambiarCuerpoCorreo() {

        System.out.println();
        System.out.println("Ingrese el cuerpo del correo: ");
        System.out.println("Escriba FIN en una linea separada para terminar.");
        System.out.println();

        StringBuilder cuerpo = new StringBuilder();

        while (true) {
            String linea = scanner.nextLine();

            if ("FIN".equalsIgnoreCase(linea.trim())) {
                break;
            }

            if (!cuerpo.isEmpty()) {
                cuerpo.append(System.lineSeparator());
            }

            cuerpo.append(linea);
        }

        String cuerpoFinal = cuerpo.toString().trim();

        if (cuerpoFinal.isBlank()) {
            System.out.println(
                    "El cuerpo no puede estar vacio.");
            return;
        }

        repositorioConfiguracion.guardarCuerpoCorreo(cuerpoFinal);

        System.out.println("Cuerpo actualizado correctamente.");
    }

    private void probarConexionCorreo() {

        System.out.println();
        System.out.println("Probando conexion con el servidor de correo...");

        ResultadoConexionCorreo resultado = servicioCorreo.probarConexion();

        System.out.println();

        if (resultado.exitosa()) {
            System.out.println("Conexion realizada correctamente.");
        } else {
            System.out.println("No se pudo establecer la conexion.");
        }

        System.out.println("Detalle: " + resultado.detalle());

        pausar();
    }

    private void configurarCorreo() {

        limpiarPantalla();

        System.out.println("=== CONFIGURACION DE CORREO ===");
        System.out.println();

        ResumenConfiguracionCorreo actual = servicioCredencialesCorreo.obtenerResumen();

        if (actual == null) {
            System.out.println(
                    "No existe una cuenta de correo configurada.");
        } else {
            System.out.println(
                    "Proveedor: " + actual.proveedor());

            System.out.println(
                    "Usuario: " + actual.usuario());

            System.out.println(
                    "Servidor: "
                            + actual.host()
                            + ":"
                            + actual.puerto());

            System.out.println(
                    "Seguridad: "
                            + actual.seguridad());
        }

        System.out.println();
        System.out.println("1. Gmail");
        System.out.println("2. Outlook");
        System.out.println("3. SMTP personalizado");
        System.out.println("0. Cancelar");
        System.out.println();

        System.out.print("Seleccione proveedor: ");

        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1" ->
                configurarProveedor(
                        ProveedorCorreo.GMAIL);

            case "2" ->
                configurarProveedor(
                        ProveedorCorreo.OUTLOOK);

            case "3" ->
                configurarProveedorPersonalizado();

            case "0" -> {
                return;
            }

            default ->
                System.out.println(
                        "Opcion invalida.");
        }

        pausar();
    }

    private void configurarProveedor(
            ProveedorCorreo proveedor) {

        System.out.println();

        System.out.println(
                "Proveedor: " + proveedor.name());

        System.out.println();

        System.out.print(
                "Correo / usuario SMTP: ");

        String usuario = scanner.nextLine().trim();

        String password = leerPassword();

        try {

            servicioCredencialesCorreo.guardar(
                    proveedor.name(),
                    usuario,
                    password,
                    proveedor.host(),
                    proveedor.puerto(),
                    proveedor.seguridad());

            System.out.println();
            System.out.println(
                    "Configuracion guardada correctamente.");

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            System.out.println();
            System.out.println(
                    "Error: " + e.getMessage());
        }
    }

    private void configurarProveedorPersonalizado() {

        System.out.println();
        System.out.println(
                "=== SMTP PERSONALIZADO ===");
        System.out.println();

        System.out.print(
                "Correo / usuario SMTP: ");

        String usuario = scanner.nextLine().trim();

        String password = leerPassword();

        System.out.print(
                "Host SMTP: ");

        String host = scanner.nextLine().trim();

        System.out.print(
                "Puerto SMTP: ");

        String puertoTexto = scanner.nextLine().trim();

        System.out.println();
        System.out.println(
                "Seguridad:");

        System.out.println(
                "1. STARTTLS");

        System.out.println(
                "2. SSL/TLS");

        System.out.println(
                "3. Ninguna");

        System.out.print(
                "Seleccione una opcion: ");

        String opcionSeguridad = scanner.nextLine().trim();

        try {

            int puerto = Integer.parseInt(
                    puertoTexto);

            var seguridad = switch (opcionSeguridad) {
                case "1" ->
                    SeguridadSmtp.STARTTLS;

                case "2" ->
                    SeguridadSmtp.SSL_TLS;

                case "3" ->
                    SeguridadSmtp.NINGUNA;

                default ->
                    throw new IllegalArgumentException(
                            "Opcion de seguridad invalida.");
            };

            servicioCredencialesCorreo.guardar(
                    ProveedorCorreo.PERSONALIZADO.name(),
                    usuario,
                    password,
                    host,
                    puerto,
                    seguridad);

            System.out.println();
            System.out.println(
                    "Configuracion guardada correctamente.");

        } catch (NumberFormatException e) {

            System.out.println();
            System.out.println(
                    "El puerto debe ser numerico.");

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            System.out.println();
            System.out.println(
                    "Error: " + e.getMessage());
        }
    }

    private void pausar() {
        System.out.println();
        System.out.println("Presione ENTER para continuar...");
        scanner.nextLine();
    }

    private void limpiarPantalla() {
        try {
            new ProcessBuilder("cmd", "/c", "cls")
                    .inheritIO()
                    .start()
                    .waitFor();

        } catch (IOException | InterruptedException e) {
        }
    }

    private String leerPassword() {

        System.out.println();
        System.out.println(
                "Por seguridad, la contraseña no se mostrara mientras escribe.");
        System.out.println(
                "Escribala normalmente y presione ENTER al finalizar.");

        if (System.console() != null) {

            char[] password = System.console().readPassword(
                    "Contraseña / App Password: ");

            if (password == null) {
                return "";
            }

            return new String(password);
        }

        System.out.print(
                "Contraseña / App Password: ");

        return scanner.nextLine();
    }
}
