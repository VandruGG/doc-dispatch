# DocDispatch

Aplicación de escritorio en Java para organizar, preparar y distribuir documentos por correo electrónico de forma segura y controlada.

DocDispatch identifica archivos a partir de un código configurable y un número de documento, agrupa los archivos correspondientes al mismo documento y los envía a los destinatarios asociados.

## Funcionalidades principales

- Identificación automática de documentos a partir del nombre del archivo.
- Código de documento configurable de 3 letras.
- Agrupación de varios archivos pertenecientes al mismo documento.
- Asociación de uno o varios destinatarios por número de documento.
- Configuración de asunto y cuerpo del correo.
- Valores iniciales para código, asunto y cuerpo.
- Simulación previa al envío real.
- Confirmación explícita antes de realizar envíos.
- Envío de múltiples archivos adjuntos en un mismo correo.
- Configuración de Gmail, Outlook y servidores SMTP personalizados.
- Prueba de conexión SMTP desde la aplicación.
- Historial local de envíos.
- Persistencia local mediante SQLite.
- Almacenamiento protegido de las credenciales de correo.
- Manual de uso integrado en la aplicación.
- Distribución ejecutable para Windows mediante `jpackage`.

## Identificación de documentos

DocDispatch utiliza un código configurable de exactamente 3 letras seguido del número del documento.

Por ejemplo, con el código configurado:

```text
LIQ
```

los siguientes archivos:

```text
LIQ166.pdf
LIQ166_anexo.pdf
LIQ200.pdf
```

se interpretan de la siguiente forma:

- `LIQ166.pdf` pertenece al documento `166`.
- `LIQ166_anexo.pdf` también pertenece al documento `166`.
- `LIQ200.pdf` pertenece al documento `200`.

Los archivos correspondientes al mismo número de documento se agrupan y se envían juntos en un único correo a los destinatarios asociados a ese número.

Los archivos que no coincidan con el código configurado no se procesan.

## Flujo de uso

El flujo recomendado de trabajo es:

1. Configurar la cuenta de correo.
2. Revisar el código de documento, asunto y cuerpo actuales.
3. Modificar esos valores solamente si es necesario.
4. Registrar los destinatarios.
5. Preparar los archivos en una carpeta.
6. Analizar la carpeta.
7. Simular el envío.
8. Revisar los archivos, destinatarios, asunto y cuerpo.
9. Confirmar el envío real escribiendo `ENVIAR`.
10. Consultar el historial si es necesario.

## Configuración inicial

DocDispatch incluye valores iniciales para poder comenzar a utilizar la aplicación sin configurar todo desde cero.

Los valores iniciales son:

```text
Código de documento: LIQ
Asunto: Documentacion
Cuerpo: Se adjunta la documentacion correspondiente.
```

Estos valores pueden consultarse y modificarse desde el menú de configuración.

Si los valores actuales ya son adecuados para el uso previsto, no es necesario modificarlos.

## Destinatarios

Cada número de documento puede tener uno o varios destinatarios asociados.

Ejemplo:

```text
Documento 166

correo1@example.com
correo2@example.com
```

Cuando DocDispatch detecta archivos correspondientes al documento `166`, prepara un correo dirigido a todos los destinatarios activos asociados a ese número.

Los destinatarios pueden:

- agregarse;
- consultarse;
- buscarse por número de documento;
- desactivarse.

La desactivación es lógica, por lo que el registro puede conservarse en la base de datos sin utilizarse para futuros envíos.

## Preparación y simulación

Antes de realizar un envío real, DocDispatch permite analizar y simular el procesamiento.

Durante la simulación se muestra:

- número de documento;
- cantidad de archivos;
- cantidad de destinatarios;
- asunto;
- cuerpo;
- estado `LISTO` o `NO LISTO`;
- detalle del resultado.

La simulación no envía correos.

Si existe algún documento que no esté listo para enviar, el proceso real se bloquea antes de realizar los envíos.

## Envío de correo

Para realizar un envío real, DocDispatch vuelve a preparar y validar la información.

Antes de confirmar se recomienda revisar:

- archivos;
- destinatarios;
- asunto;
- cuerpo.

El envío requiere una confirmación explícita escribiendo:

```text
ENVIAR
```

Si no se introduce esa confirmación, el envío se cancela.

## Configuración de correo

DocDispatch permite configurar:

- Gmail;
- Outlook;
- servidor SMTP personalizado.

Para cada cuenta se almacenan los datos necesarios para realizar la conexión SMTP.

En configuraciones personalizadas se puede indicar:

- usuario;
- contraseña;
- host SMTP;
- puerto;
- tipo de seguridad.

Los tipos de seguridad admitidos son:

- STARTTLS;
- SSL/TLS;
- sin cifrado SMTP.

La aplicación también permite probar la conexión con el servidor antes de realizar envíos.

## Seguridad de credenciales

Las credenciales SMTP no se almacenan en texto plano.

DocDispatch utiliza:

- AES/GCM para cifrar la contraseña;
- una clave AES generada aleatoriamente;
- Windows DPAPI para proteger la clave de cifrado;
- SQLite para almacenar localmente la configuración protegida.

La contraseña se descifra únicamente cuando la aplicación necesita utilizarla para conectarse al servidor SMTP.

La clave AES queda protegida mediante Windows DPAPI, por lo que la configuración de credenciales queda vinculada al entorno de Windows donde fue creada.

Si una base de datos es copiada a otro usuario o equipo y las credenciales no pueden recuperarse, DocDispatch informa el problema de forma controlada y permite volver a configurar la cuenta.

## Persistencia

DocDispatch utiliza una base de datos SQLite local.

En Windows, la base se crea automáticamente en:

```text
%LOCALAPPDATA%\DocDispatch\docdispatch.db
```

Por ejemplo:

```text
C:\Users\<usuario>\AppData\Local\DocDispatch\docdispatch.db
```

Esta ubicación mantiene los datos del usuario separados de los archivos instalados de la aplicación y evita depender del directorio desde el que se ejecuta DocDispatch.

La base de datos almacena:

- destinatarios;
- configuración general;
- configuración protegida de correo;
- historial de envíos.

La distribución de DocDispatch no incluye datos personales, destinatarios, historial ni credenciales del desarrollador.

Cada instalación nueva crea su propia base de datos durante la primera ejecución.

## Historial de envíos

DocDispatch registra los intentos de envío realizados.

El historial muestra inicialmente:

- ID;
- fecha y hora;
- número de documento;
- estado.

Los registros se muestran del más reciente al más antiguo.

Al seleccionar un registro se puede consultar información detallada como:

- destinatarios;
- archivos;
- resultado del proceso;
- detalle del envío o error.

## Manual de uso

DocDispatch incluye un manual accesible desde el menú principal.

El manual explica:

- qué es DocDispatch;
- configuración inicial;
- configuración de correo;
- configuración de documentos y mensaje;
- gestión de destinatarios;
- preparación de archivos;
- análisis y simulación;
- envío real;
- historial;
- seguridad y recomendaciones.

## Tecnologías

- Java 21
- Maven
- Jakarta Mail 2.0.2
- SQLite JDBC 3.53.4.0
- JNA / JNA Platform 5.19.1
- Windows DPAPI
- JUnit 5
- Maven Shade Plugin
- jpackage

## Arquitectura general

El proyecto separa las principales responsabilidades en distintos paquetes:

```text
database
enums
model
repository
security
service
ui
```

Las responsabilidades principales están divididas entre:

- acceso y creación de la base de datos;
- persistencia mediante repositorios;
- identificación y procesamiento de documentos;
- preparación de envíos;
- envío SMTP;
- cifrado y protección de credenciales;
- historial;
- interfaz de consola.

Esta separación permite mantener la lógica de procesamiento desacoplada de la persistencia, la seguridad y la interfaz.

## Pruebas automatizadas

DocDispatch cuenta actualmente con 47 pruebas automatizadas.

Las pruebas cubren, entre otros casos:

- identificación de documentos;
- validación del código de documento;
- agrupación de archivos;
- múltiples archivos por documento;
- múltiples destinatarios;
- documentos sin destinatarios;
- preparación de envíos;
- simulación;
- validación de asunto y cuerpo;
- construcción completa de solicitudes de correo;
- errores de envío;
- persistencia del historial;
- orden del historial;
- configuración de correo;
- reemplazo de credenciales;
- configuración SMTP personalizada;
- cifrado de contraseñas;
- verificación de que las contraseñas no se almacenan en texto plano;
- recuperación controlada ante credenciales dañadas.

Para ejecutar las pruebas:

```bash
mvn test
```

## Compilación

Para generar el JAR ejecutable:

```bash
mvn package
```

El proyecto utiliza Maven Shade Plugin para generar un JAR ejecutable que incluye las dependencias necesarias.

El archivo generado se encuentra en:

```text
target/doc-dispatch-1.0-SNAPSHOT.jar
```

Puede ejecutarse con:

```bash
java -jar target/doc-dispatch-1.0-SNAPSHOT.jar
```

## Ejecución durante el desarrollo

Desde la raíz del proyecto:

```bash
mvn compile exec:java
```

También puede ejecutarse desde un entorno de desarrollo compatible con Java 21 y Maven.

## Empaquetado para Windows

DocDispatch puede empaquetarse como una aplicación de Windows mediante `jpackage`.

La aplicación requiere consola porque su interfaz utiliza entrada y salida estándar.

Ejemplo para generar una imagen de aplicación:

```powershell
jpackage `
  --type app-image `
  --name DocDispatch `
  --input target `
  --main-jar doc-dispatch-1.0-SNAPSHOT.jar `
  --main-class com.vandrugg.docdispatch.Main `
  --dest dist `
  --win-console
```

La imagen generada incluye:

- `DocDispatch.exe`;
- el JAR de la aplicación;
- las dependencias necesarias;
- un runtime Java propio.

Por lo tanto, el usuario final no necesita tener Java instalado para ejecutar la aplicación empaquetada.

## Requisitos de desarrollo

Para compilar y ejecutar el proyecto desde el código fuente se requiere:

- JDK 21;
- Maven.

Para verificar las versiones instaladas:

```bash
java -version
```

```bash
mvn -version
```

Para generar paquetes de Windows se requiere además que `jpackage` esté disponible en el JDK utilizado.

Para verificarlo:

```bash
jpackage --version
```

## Datos locales y privacidad

Los datos utilizados por DocDispatch permanecen almacenados localmente en el equipo del usuario.

El repositorio no debe contener:

- bases de datos generadas durante el uso;
- credenciales de correo;
- destinatarios reales;
- historiales de envío;
- archivos utilizados como adjuntos.

La base local se almacena en:

```text
%LOCALAPPDATA%\DocDispatch
```

Esta carpeta pertenece a cada usuario de Windows y no forma parte de los archivos distribuidos con la aplicación.

## Estado del proyecto

La funcionalidad principal de DocDispatch se encuentra implementada y probada.

Actualmente se encuentran validados:

- procesamiento de documentos;
- destinatarios múltiples;
- múltiples adjuntos;
- simulación;
- envío SMTP;
- historial;
- configuración de correo;
- cifrado de credenciales;
- recuperación controlada ante credenciales dañadas;
- persistencia SQLite;
- ejecución mediante JAR independiente;
- generación y ejecución de una `app-image` para Windows;
- almacenamiento de datos en `%LOCALAPPDATA%`.

El proyecto se encuentra en etapa final de generación y prueba del instalador para Windows.

## Autor

Desarrollado por VandruGG.