package com.vandrugg.docdispatch.ui;

import java.io.IOException;
import java.util.Scanner;

public class MenuManual {

        private final Scanner scanner;

        public MenuManual(Scanner scanner) {

                if (scanner == null) {
                        throw new IllegalArgumentException(
                                        "El scanner es obligatorio.");
                }

                this.scanner = scanner;
        }

        public void mostrar() {

                boolean volver = false;

                while (!volver) {

                        limpiarPantalla();

                        System.out.println("========================================");
                        System.out.println("           MANUAL DOCDISPATCH");
                        System.out.println("========================================");
                        System.out.println();
                        System.out.println("1. Que es DocDispatch?");
                        System.out.println("2. Antes de comenzar");
                        System.out.println("3. Configurar correo");
                        System.out.println("4. Configurar documentos y mensaje");
                        System.out.println("5. Gestionar destinatarios");
                        System.out.println("6. Preparar archivos");
                        System.out.println("7. Analizar y simular");
                        System.out.println("8. Realizar un envio");
                        System.out.println("9. Consultar historial");
                        System.out.println("10. Seguridad y recomendaciones");
                        System.out.println("0. Volver al menu principal");
                        System.out.println();

                        System.out.print("Seleccione una opcion: ");

                        String opcion = scanner.nextLine().trim();

                        switch (opcion) {
                                case "1" -> mostrarIntroduccion();
                                case "2" -> mostrarAntesDeComenzar();
                                case "3" -> mostrarConfiguracionCorreo();
                                case "4" -> mostrarConfiguracionDocumentos();
                                case "5" -> mostrarDestinatarios();
                                case "6" -> mostrarPreparacionArchivos();
                                case "7" -> mostrarAnalisisSimulacion();
                                case "8" -> mostrarEnvio();
                                case "9" -> mostrarHistorial();
                                case "10" -> mostrarSeguridad();
                                case "0" -> volver = true;

                                default -> {
                                        System.out.println();
                                        System.out.println("Opcion invalida.");
                                        pausar();
                                }
                        }
                }
        }

        private void mostrarIntroduccion() {

                limpiarPantalla();

                System.out.println("========================================");
                System.out.println("           QUE ES DOCDISPATCH?");
                System.out.println("========================================");
                System.out.println();

                System.out.println(
                                "DocDispatch es una aplicacion para el envio");
                System.out.println(
                                "automatizado de correos electronicos con archivos");
                System.out.println(
                                "adjuntos.");
                System.out.println();

                System.out.println(
                                "Los archivos se agrupan segun un numero de documento");
                System.out.println(
                                "obtenido a partir de su nombre.");
                System.out.println();

                System.out.println(
                                "Para detectar los archivos, DocDispatch utiliza un");
                System.out.println(
                                "patron configurable compuesto por un codigo de tres");
                System.out.println(
                                "letras seguido del numero del documento.");
                System.out.println();

                System.out.println("Ejemplo:");
                System.out.println();
                System.out.println("Codigo configurado: LIQ");
                System.out.println("Archivos:");
                System.out.println("- LIQ166.pdf");
                System.out.println("- LIQ166_anexo.pdf");
                System.out.println();
                System.out.println(
                                "Ambos archivos pertenecen al documento 166 y se");
                System.out.println(
                                "envian juntos en un mismo correo a los destinatarios");
                System.out.println(
                                "asociados a ese numero.");
                System.out.println();

                System.out.println(
                                "Si es la primera vez que utiliza DocDispatch,");
                System.out.println(
                                "se recomienda leer las secciones en orden.");

                pausar();
        }

        private void mostrarAntesDeComenzar() {

                limpiarPantalla();

                System.out.println("================================");
                System.out.println("        ANTES DE COMENZAR");
                System.out.println("================================");
                System.out.println();

                System.out.println(
                                "El flujo recomendado de trabajo es:");
                System.out.println();

                System.out.println("1. Configurar la cuenta de correo.");
                System.out.println("2. Revisar codigo, asunto y cuerpo configurados.");
                System.out.println("3. Modificarlos solo si es necesario.");
                System.out.println("4. Registrar los destinatarios.");
                System.out.println("5. Preparar los archivos.");
                System.out.println("6. Analizar la carpeta.");
                System.out.println("7. Simular el envio.");
                System.out.println("8. Revisar los resultados.");
                System.out.println("9. Realizar el envio real.");
                System.out.println("10. Consultar el historial si es necesario.");
                System.out.println();
                System.out.println(
                                "DocDispatch incluye valores iniciales para el codigo "
                                                + "de documento, el asunto y el cuerpo del correo.");
                System.out.println();

                System.out.println(
                                "Revise esos valores antes de comenzar y cambielos "
                                                + "solamente si no se ajustan a sus necesidades.");
                System.out.println();

                pausar();
        }

        private void mostrarConfiguracionCorreo() {

                limpiarPantalla();

                System.out.println("========================================");
                System.out.println("          CONFIGURAR CORREO");
                System.out.println("========================================");
                System.out.println();

                System.out.println(
                                "Antes de realizar envios reales debe configurar");
                System.out.println(
                                "la cuenta de correo que utilizara DocDispatch.");
                System.out.println();

                System.out.println(
                                "Desde el menu Configuracion puede seleccionar:");
                System.out.println("- Gmail.");
                System.out.println("- Outlook.");
                System.out.println("- SMTP personalizado.");
                System.out.println();

                System.out.println(
                                "Debe ingresar el correo o usuario SMTP y la");
                System.out.println(
                                "credencial correspondiente al proveedor.");
                System.out.println();

                System.out.println("GMAIL");
                System.out.println("----------------------------------------");
                System.out.println(
                                "No utilice la contraseña normal de su cuenta");
                System.out.println(
                                "de Google.");
                System.out.println();

                System.out.println(
                                "DocDispatch necesita una Contraseña de Aplicacion");
                System.out.println(
                                "(App Password) generada desde la seguridad");
                System.out.println(
                                "de su cuenta de Google.");
                System.out.println();

                System.out.println(
                                "Para generar una App Password debe tener activada");
                System.out.println(
                                "la verificacion en dos pasos de Google.");
                System.out.println();

                System.out.println(
                                "Consulte la ayuda oficial de Google sobre");
                System.out.println(
                                "Contraseñas de Aplicacion.");
                System.out.println();

                System.out.println("OUTLOOK / MICROSOFT");
                System.out.println("----------------------------------------");
                System.out.println(
                                "Microsoft utiliza actualmente autenticacion moderna");
                System.out.println(
                                "(OAuth2) para muchas cuentas de Outlook y");
                System.out.println(
                                "Microsoft 365.");
                System.out.println();

                System.out.println(
                                "Algunas cuentas Microsoft con verificacion en dos");
                System.out.println(
                                "pasos permiten generar una Contraseña de Aplicacion.");
                System.out.println();

                System.out.println(
                                "Sin embargo, esta opcion puede no estar disponible");
                System.out.println(
                                "en cuentas empresariales, educativas o cuando");
                System.out.println(
                                "la autenticacion SMTP basica esta deshabilitada.");
                System.out.println();

                System.out.println(
                                "Si Outlook rechaza la contraseña, consulte primero");
                System.out.println(
                                "la configuracion de seguridad de su cuenta Microsoft");
                System.out.println(
                                "y la documentacion oficial sobre App Passwords.");
                System.out.println();

                System.out.println("SMTP PERSONALIZADO");
                System.out.println("----------------------------------------");
                System.out.println(
                                "Para otros proveedores utilice la contraseña SMTP");
                System.out.println(
                                "o contraseña de aplicacion indicada por el");
                System.out.println(
                                "proveedor del servicio.");
                System.out.println();

                System.out.println(
                                "Por seguridad, la contraseña no se muestra mientras");
                System.out.println(
                                "se escribe cuando la consola lo permite.");
                System.out.println();

                System.out.println(
                                "Una vez guardada la cuenta, utilice la opcion");
                System.out.println(
                                "\"Probar conexion de correo\" antes del primer envio.");
                System.out.println();

                System.out.println(
                                "La configuracion queda almacenada de forma protegida");
                System.out.println(
                                "para futuros usos de DocDispatch.");

                pausar();
        }

        private void mostrarConfiguracionDocumentos() {

                limpiarPantalla();

                System.out.println("========================================");
                System.out.println("   CONFIGURAR DOCUMENTOS Y MENSAJE");
                System.out.println("========================================");
                System.out.println();

                System.out.println(
                                "DocDispatch incluye un codigo de documento,");
                System.out.println(
                                "un asunto y un cuerpo iniciales.");
                System.out.println();

                System.out.println(
                                "Antes de modificarlos, revise los valores actuales");
                System.out.println(
                                "desde el menu Configuracion.");
                System.out.println();

                System.out.println(
                                "Si los valores ya son adecuados para sus envios,");
                System.out.println(
                                "no es necesario cambiarlos.");
                System.out.println();

                System.out.println(
                                "El codigo de documento debe contener exactamente");
                System.out.println(
                                "3 letras y se utiliza para identificar los archivos.");
                System.out.println();

                System.out.println("Ejemplo con el codigo LIQ:");
                System.out.println();
                System.out.println("LIQ166.pdf");
                System.out.println("LIQ166_anexo.pdf");
                System.out.println();

                System.out.println(
                                "Ambos archivos corresponden al documento 166");
                System.out.println(
                                "y se enviaran juntos.");
                System.out.println();

                System.out.println(
                                "El asunto y el cuerpo configurados se utilizaran");
                System.out.println(
                                "en los correos enviados.");
                System.out.println();

                System.out.println(
                                "El cuerpo del correo puede contener varias lineas.");
                System.out.println(
                                "Escriba el contenido linea por linea.");
                System.out.println(
                                "Cuando termine, escriba FIN en una linea separada");
                System.out.println(
                                "y presione ENTER para guardar el nuevo cuerpo.");

                pausar();
        }

        private void mostrarDestinatarios() {

                limpiarPantalla();

                System.out.println("================================");
                System.out.println("      GESTIONAR DESTINATARIOS");
                System.out.println("================================");
                System.out.println();

                System.out.println(
                                "Cada numero de documento puede tener uno o varios");
                System.out.println(
                                "destinatarios asociados.");
                System.out.println();

                System.out.println("Ejemplo:");
                System.out.println();
                System.out.println("Documento 166");
                System.out.println("- correo1@example.com");
                System.out.println("- correo2@example.com");
                System.out.println();

                System.out.println(
                                "Cuando DocDispatch encuentre archivos correspondientes");
                System.out.println(
                                "al documento 166, utilizara esos destinatarios.");
                System.out.println();

                System.out.println(
                                "Los destinatarios pueden agregarse, consultarse o");
                System.out.println(
                                "desactivarse desde el menu correspondiente.");

                pausar();
        }

        private void mostrarPreparacionArchivos() {

                limpiarPantalla();

                System.out.println("================================");
                System.out.println("        PREPARAR ARCHIVOS");
                System.out.println("================================");
                System.out.println();

                System.out.println(
                                "Coloque en una misma carpeta los archivos que desea");
                System.out.println(
                                "procesar.");
                System.out.println();

                System.out.println(
                                "El nombre debe contener el codigo configurado seguido");
                System.out.println(
                                "del numero del documento.");
                System.out.println();

                System.out.println("Ejemplo:");
                System.out.println();
                System.out.println("LIQ166.pdf");
                System.out.println("LIQ166_anexo.pdf");
                System.out.println("LIQ200.pdf");
                System.out.println();

                System.out.println(
                                "DocDispatch agrupara los dos primeros archivos como");
                System.out.println(
                                "documento 166 y el tercero como documento 200.");
                System.out.println();

                System.out.println(
                                "Los archivos que no coincidan con el patron configurado");
                System.out.println(
                                "no se procesaran.");

                pausar();
        }

        private void mostrarAnalisisSimulacion() {

                limpiarPantalla();

                System.out.println("================================");
                System.out.println("       ANALIZAR Y SIMULAR");
                System.out.println("================================");
                System.out.println();

                System.out.println(
                                "Analizar carpeta permite comprobar que archivos fueron");
                System.out.println(
                                "detectados y que destinatarios tiene cada documento.");
                System.out.println();

                System.out.println(
                                "Simular envio muestra como quedara cada correo antes");
                System.out.println(
                                "de realizar un envio real.");
                System.out.println();

                System.out.println("Durante la simulacion revise:");
                System.out.println("- Numero de documento.");
                System.out.println("- Cantidad de archivos.");
                System.out.println("- Cantidad de destinatarios.");
                System.out.println("- Asunto.");
                System.out.println("- Cuerpo.");
                System.out.println("- Estado LISTO o NO LISTO.");
                System.out.println();

                System.out.println(
                                "Analizar y simular no envian correos.");

                pausar();
        }

        private void mostrarEnvio() {

                limpiarPantalla();

                System.out.println("================================");
                System.out.println("        REALIZAR UN ENVIO");
                System.out.println("================================");
                System.out.println();

                System.out.println(
                                "Antes del envio real DocDispatch vuelve a comprobar");
                System.out.println(
                                "la informacion preparada.");
                System.out.println();

                System.out.println(
                                "Si algun grupo presenta problemas, el proceso se");
                System.out.println(
                                "bloquea.");
                System.out.println();

                System.out.println(
                                "Antes de confirmar revise cuidadosamente:");
                System.out.println("- Archivos.");
                System.out.println("- Destinatarios.");
                System.out.println("- Asunto.");
                System.out.println("- Cuerpo.");
                System.out.println();

                System.out.println(
                                "Para realizar el envio real debe escribir:");
                System.out.println();
                System.out.println("ENVIAR");
                System.out.println();

                System.out.println(
                                "Si no se introduce esa confirmacion, el envio");
                System.out.println(
                                "se cancela.");

                pausar();
        }

        private void mostrarHistorial() {

                limpiarPantalla();

                System.out.println("================================");
                System.out.println("       HISTORIAL DE ENVIOS");
                System.out.println("================================");
                System.out.println();

                System.out.println(
                                "DocDispatch registra los intentos de envio realizados.");
                System.out.println();

                System.out.println(
                                "La pantalla inicial del historial muestra:");
                System.out.println("- ID.");
                System.out.println("- Fecha.");
                System.out.println("- Numero de documento.");
                System.out.println("- Estado.");
                System.out.println();

                System.out.println(
                                "Seleccione el ID de un envio para consultar");
                System.out.println(
                                "informacion detallada, como destinatarios, archivos");
                System.out.println(
                                "y resultado del proceso.");

                pausar();
        }

        private void mostrarSeguridad() {

                limpiarPantalla();

                System.out.println("================================");
                System.out.println("    SEGURIDAD Y RECOMENDACIONES");
                System.out.println("================================");
                System.out.println();

                System.out.println(
                                "- Utilice siempre la simulacion antes del envio real.");
                System.out.println(
                                "- Revise los destinatarios antes de confirmar.");
                System.out.println(
                                "- Verifique el codigo de documento configurado.");
                System.out.println(
                                "- No comparta sus credenciales de correo.");
                System.out.println(
                                "- Pruebe la conexion despues de cambiar credenciales.");
                System.out.println(
                                "- Revise el historial si necesita confirmar un envio.");
                System.out.println();

                System.out.println(
                                "La contraseña de correo se almacena de forma cifrada.");
                System.out.println();

                System.out.println(
                                "La clave utilizada para protegerla queda vinculada");
                System.out.println(
                                "al entorno de Windows mediante su mecanismo de");
                System.out.println(
                                "proteccion de datos.");

                pausar();
        }

        private void pausar() {

                System.out.println();
                System.out.println(
                                "Presione ENTER para volver al indice del manual...");

                scanner.nextLine();
        }

        private void limpiarPantalla() {

                try {

                        new ProcessBuilder(
                                        "cmd",
                                        "/c",
                                        "cls")
                                        .inheritIO()
                                        .start()
                                        .waitFor();

                } catch (IOException
                                | InterruptedException e) {

                }
        }
}