# DocDispatch

DocDispatch es una aplicación de escritorio desarrollada en Java para automatizar el envío de correos electrónicos con documentos adjuntos.

La aplicación identifica documentos a partir de sus nombres, los agrupa por número, busca los destinatarios asociados, permite revisar y simular el proceso antes del envío y mantiene un historial de los resultados.

---

## Versión actual

**DocDispatch 1.0.0**

Primera versión estable de producción.

---

## Características principales

- Identificación automática de documentos.
- Código de documento configurable.
- Agrupación de múltiples archivos pertenecientes al mismo documento.
- Uno o varios destinatarios por documento.
- Gestión persistente de destinatarios.
- Asunto de correo configurable.
- Cuerpo de correo configurable y multilínea.
- Simulación previa al envío.
- Validación de documentos sin destinatarios.
- Confirmación explícita antes de realizar envíos reales.
- Envío de múltiples adjuntos en un único correo.
- Registro de historial de envíos.
- Consulta detallada del historial.
- Configuración de Gmail.
- Configuración de Outlook / Microsoft.
- Configuración SMTP personalizada.
- Prueba de conexión con el servidor SMTP.
- Credenciales almacenadas de forma protegida.
- Manual de usuario incorporado en la aplicación.
- Instalador para Windows.

---

## Identificación de documentos

DocDispatch utiliza un código configurable de tres letras seguido por el número del documento.

Por defecto, el código inicial es:

```text
LIQ
```

Ejemplo:

```text
LIQ166.pdf
LIQ166_anexo.pdf
LIQ200.pdf
```

DocDispatch interpreta:

```text
Documento 166
- LIQ166.pdf
- LIQ166_anexo.pdf

Documento 200
- LIQ200.pdf
```

Los archivos correspondientes al mismo número son enviados juntos en un único correo.

Los archivos que no coincidan con el patrón configurado no son procesados.

---

## Destinatarios

Cada número de documento puede tener uno o varios destinatarios asociados.

Ejemplo:

```text
Documento 166
correo1@ejemplo.com
correo2@ejemplo.com
```

Cuando se procesan archivos correspondientes al documento `166`, ambos destinatarios reciben el mismo correo con todos los archivos asociados.

Los destinatarios pueden ser agregados, consultados y desactivados desde la aplicación.

---

## Flujo recomendado

El flujo normal de trabajo es:

1. Configurar la cuenta de correo.
2. Probar la conexión SMTP.
3. Revisar el código de documento configurado.
4. Revisar el asunto y cuerpo del correo.
5. Registrar los destinatarios.
6. Preparar los archivos a enviar.
7. Analizar la carpeta.
8. Simular el envío.
9. Revisar los resultados.
10. Confirmar el envío real.
11. Consultar el historial cuando sea necesario.

---

## Simulación

Antes de realizar un envío real, DocDispatch permite simular el procesamiento.

La simulación muestra:

- número de documento;
- cantidad de archivos;
- cantidad de destinatarios;
- asunto;
- cuerpo;
- estado del grupo;
- posibles problemas detectados.

La simulación no envía ningún correo.

Si alguno de los grupos presenta problemas, DocDispatch bloquea el envío real.

---

## Confirmación de envío

Antes de enviar correos reales, la aplicación solicita una confirmación explícita.

El usuario debe escribir:

```text
ENVIAR
```

Si no se introduce esta confirmación, el proceso se cancela.

---

## Configuración de correo

DocDispatch 1.0.0 permite configurar:

- Gmail;
- Outlook / Microsoft;
- SMTP personalizado.

### Gmail

No debe utilizarse la contraseña normal de la cuenta de Google.

Debe utilizarse una **Contraseña de Aplicación (App Password)** generada desde la configuración de seguridad de Google.

Para poder crearla, debe estar activada la verificación en dos pasos.

Ayuda oficial:

https://support.google.com/accounts/answer/185833?hl=es

### Outlook / Microsoft

Algunas cuentas Microsoft permiten utilizar Contraseñas de Aplicación cuando tienen habilitada la verificación en dos pasos.

Microsoft utiliza actualmente autenticación moderna OAuth2 en muchas cuentas.

DocDispatch 1.0.0 utiliza autenticación SMTP mediante usuario y contraseña, por lo que algunas cuentas de Outlook, Microsoft 365, empresariales o educativas pueden no ser compatibles dependiendo de su configuración de seguridad.

En cuentas empresariales o educativas, la disponibilidad de determinadas formas de autenticación también puede depender de la configuración realizada por el administrador.

### SMTP personalizado

También puede utilizarse cualquier proveedor SMTP compatible indicando:

- usuario;
- contraseña SMTP o contraseña de aplicación;
- servidor;
- puerto;
- tipo de seguridad.

Los modos disponibles son:

- STARTTLS;
- SSL/TLS;
- sin cifrado SMTP.

---

## Seguridad de credenciales

DocDispatch no almacena la contraseña SMTP en texto plano.

Las credenciales se protegen utilizando:

- AES-256/GCM para cifrar la contraseña;
- una clave AES generada localmente;
- Windows DPAPI para proteger la clave utilizada por la aplicación.

Esto vincula la protección de las credenciales al entorno de Windows donde fueron configuradas.

La configuración sensible no forma parte del código fuente ni del instalador distribuido públicamente.

---

## Persistencia

DocDispatch utiliza SQLite para almacenar:

- destinatarios;
- configuración general;
- configuración de correo protegida;
- historial de envíos.

En Windows, la base de datos de producción se almacena en:

```text
%LOCALAPPDATA%\DocDispatch\docdispatch.db
```

Por ejemplo:

```text
C:\Users\Usuario\AppData\Local\DocDispatch\docdispatch.db
```

Los datos no se almacenan dentro de la carpeta de instalación del programa.

Esto permite separar la aplicación de la información persistente del usuario y facilita que una actualización o reinstalación pueda conservar los datos existentes.

---

## Instalaciones nuevas

Cada instalación nueva de DocDispatch genera su propia base de datos local.

La aplicación no incluye datos personales ni información precargada del desarrollador.

Una instalación nueva comienza sin:

- destinatarios;
- historial de envíos;
- cuenta de correo configurada;
- usuario SMTP;
- contraseña SMTP;
- App Password;
- claves de cifrado pertenecientes a otra instalación.

Sí se incluyen valores iniciales de configuración para facilitar el primer uso:

```text
Código de documento: LIQ
Asunto: Documentacion
Cuerpo: Se adjunta la documentacion correspondiente.
```

Cada usuario debe configurar sus propios destinatarios y sus propias credenciales de correo.

---

## Actualización y reinstalación

Los archivos de la aplicación se instalan de forma independiente de la base de datos del usuario.

La aplicación se instala normalmente en una ubicación administrada por Windows, mientras que la información persistente permanece en:

```text
%LOCALAPPDATA%\DocDispatch
```

Por este motivo, una actualización o reinstalación de DocDispatch está diseñada para poder conservar:

- destinatarios;
- configuración;
- historial;
- configuración de correo protegida.

La eliminación manual de la carpeta de datos locales implica la pérdida de esa información.

---

## Historial de envíos

DocDispatch registra los intentos de envío realizados.

El historial permite consultar:

- fecha y hora;
- número de documento;
- estado;
- destinatarios;
- archivos;
- detalle del resultado.

Esto permite comprobar posteriormente qué documentos fueron procesados y cuál fue el resultado del envío.

Los registros más recientes se muestran primero.

---

## Manual integrado

DocDispatch incluye un manual accesible desde el menú principal.

El manual explica:

- qué es DocDispatch;
- qué revisar antes de comenzar;
- cómo configurar la cuenta de correo;
- cómo configurar el código de documento;
- cómo configurar el asunto y cuerpo del mensaje;
- cómo preparar los archivos;
- cómo registrar destinatarios;
- cómo analizar una carpeta;
- cómo simular envíos;
- cómo realizar envíos reales;
- cómo consultar el historial;
- recomendaciones de seguridad.

---

## Instalación

DocDispatch 1.0.0 se distribuye mediante un instalador para Windows.

Descargue el archivo:

```text
DocDispatch-1.0.0.exe
```

desde la sección **Releases** del repositorio y ejecute el instalador.

La instalación incluye el runtime necesario para ejecutar la aplicación.

No es necesario instalar Java manualmente.

La aplicación puede instalarse con acceso desde el menú Inicio y acceso directo en el escritorio.

---

## Requisitos para utilizar la aplicación

- Windows.
- Acceso a Internet para los envíos de correo.
- Cuenta SMTP compatible.

No es necesario instalar:

- Java;
- Maven;
- SQLite;
- WiX.

Estas herramientas solamente son necesarias para desarrollo y generación de nuevas distribuciones.

---

## Tecnologías utilizadas

DocDispatch 1.0.0 está desarrollado utilizando:

- Java 21
- Maven
- Jakarta Mail
- SQLite
- SQLite JDBC
- JNA
- Windows DPAPI
- AES-256/GCM
- JUnit 5
- Maven Shade Plugin
- jpackage
- WiX Toolset

---

## Desarrollo

### Requisitos de desarrollo

Para trabajar con el código fuente se requiere:

- JDK 21;
- Maven 3.9 o superior;
- Windows para las funcionalidades dependientes de DPAPI;
- WiX Toolset para generar el instalador Windows mediante `jpackage`.

### Ejecutar los tests

```bash
mvn test
```

### Generar el paquete

```bash
mvn package
```

### Ejecutar el JAR

```bash
java -jar target/doc-dispatch-1.0-SNAPSHOT.jar
```

---

## Generación del instalador

DocDispatch utiliza `jpackage` para generar su distribución de Windows.

La aplicación se empaqueta incluyendo su propio runtime, por lo que el usuario final no necesita instalar Java.

El instalador utiliza el icono ubicado en:

```text
assets/docdispatch.ico
```

Las opciones de distribución permiten incluir:

- ejecutable de Windows;
- menú Inicio;
- acceso directo;
- runtime de Java;
- icono propio de DocDispatch.

---

## Tests

DocDispatch 1.0.0 dispone actualmente de:

```text
47 tests
0 failures
0 errors
0 skipped
```

Las pruebas cubren, entre otros:

- identificación de documentos;
- validación del código configurable;
- procesamiento y agrupación de archivos;
- destinatarios;
- persistencia SQLite;
- repositorios;
- simulación;
- preparación de envíos;
- construcción de solicitudes de correo;
- múltiples destinatarios;
- múltiples adjuntos;
- resultados SMTP;
- errores SMTP;
- historial;
- orden del historial;
- configuración de correo;
- SMTP personalizado;
- almacenamiento y recuperación de credenciales;
- cifrado de contraseñas;
- reemplazo de configuraciones;
- reemplazo de contraseñas;
- detección de credenciales dañadas;
- ausencia de configuración;
- validación de credenciales.

La versión 1.0.0 fue además validada mediante pruebas funcionales de la aplicación instalada.

---

## Arquitectura

El proyecto separa sus responsabilidades en diferentes paquetes:

```text
com.vandrugg.docdispatch
├── database
├── enums
├── model
├── repository
├── security
├── service
└── ui
```

La aplicación mantiene separadas las responsabilidades de:

- persistencia;
- seguridad;
- configuración;
- procesamiento de documentos;
- envío de correo;
- lógica de negocio;
- presentación.

Esto permite mantener y evolucionar el proyecto sin acoplar la lógica principal a la interfaz de usuario.

La separación actual también permite que futuras versiones puedan reemplazar la interfaz de consola sin necesidad de reescribir el núcleo de servicios, seguridad y persistencia.

---

## Estructura general del proyecto

```text
doc-dispatch/
├── assets/
│   └── docdispatch.ico
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│       └── java/
├── pom.xml
├── README.md
└── .gitignore
```

Las carpetas generadas durante compilación o distribución no forman parte del repositorio:

```text
target/
dist/
installer/
data/
```

Tampoco se versionan:

- bases de datos locales;
- archivos SQLite;
- credenciales;
- logs generados;
- configuraciones sensibles locales.

---

## Seguridad y privacidad

DocDispatch fue diseñado para que la información sensible permanezca en el equipo del usuario.

El instalador público no incluye:

- destinatarios;
- historial;
- bases de datos del desarrollador;
- cuentas de correo;
- usuarios SMTP;
- contraseñas;
- App Passwords;
- claves AES pertenecientes a otras instalaciones.

Cada instalación genera y utiliza su propia información local.

Las credenciales SMTP se almacenan cifradas y protegidas mediante mecanismos de seguridad de Windows.

La contraseña de correo no se almacena en texto plano.

---

## Base de datos

DocDispatch utiliza SQLite como sistema de persistencia local.

La base de datos es creada automáticamente cuando la aplicación se ejecuta por primera vez.

Entre la información persistida se encuentra:

- destinatarios;
- estado de los destinatarios;
- configuración general;
- configuración SMTP protegida;
- historial de envíos.

No es necesario que el usuario instale o configure un servidor de base de datos.

---

## Valores iniciales

En una instalación nueva DocDispatch crea automáticamente los valores generales necesarios para comenzar a trabajar.

Los valores iniciales son:

```text
Código de documento: LIQ
Asunto: Documentacion
Cuerpo: Se adjunta la documentacion correspondiente.
```

Estos valores pueden ser modificados desde el menú **Configuración**.

---

## Gestión de destinatarios

Los destinatarios se relacionan con un número de documento.

Un mismo documento puede contener múltiples destinatarios.

Los registros pueden ser:

- agregados;
- consultados;
- desactivados.

La aplicación utiliza desactivación lógica durante el funcionamiento normal para evitar eliminar accidentalmente información que pueda ser necesaria posteriormente.

Los datos puramente de prueba pueden eliminarse físicamente de la base antes de generar una distribución definitiva cuando sea necesario.

---

## Procesamiento de documentos

El procesamiento se realiza sobre una carpeta seleccionada por el usuario.

DocDispatch:

1. revisa los archivos disponibles;
2. identifica los que coinciden con el código configurado;
3. obtiene el número de documento;
4. agrupa los archivos pertenecientes al mismo número;
5. consulta los destinatarios activos;
6. prepara el grupo para simulación o envío.

Los archivos que no coinciden con el patrón esperado son ignorados.

---

## Envío de correo

El envío real se realiza únicamente después de:

1. analizar los documentos;
2. verificar los destinatarios;
3. realizar la simulación;
4. comprobar que todos los grupos estén listos;
5. solicitar una confirmación explícita.

Una vez confirmado, DocDispatch envía un correo por cada grupo de documentos.

Cada correo puede contener:

- uno o varios destinatarios;
- uno o varios archivos adjuntos;
- asunto configurable;
- cuerpo configurable.

---

## Configuración del mensaje

El asunto y el cuerpo del correo pueden modificarse desde la aplicación.

El cuerpo admite múltiples líneas.

Para finalizar la edición del cuerpo desde la interfaz de consola se utiliza:

```text
FIN
```

en una línea separada.

Antes de realizar envíos se recomienda revisar siempre los valores configurados.

---

## Prueba de conexión

DocDispatch incluye una opción para probar la conexión con el servidor de correo.

Se recomienda utilizarla:

- después de configurar una cuenta por primera vez;
- después de cambiar la contraseña;
- después de cambiar una App Password;
- después de modificar host, puerto o seguridad SMTP;
- cuando el proveedor de correo rechace un envío.

Una conexión válida confirma que DocDispatch puede autenticarse con la configuración almacenada.

---

## Estado del proyecto

### V1.0.0

DocDispatch 1.0.0 es la primera versión estable de producción.

Incluye:

- procesamiento de documentos;
- identificación configurable;
- agrupación automática;
- gestión de destinatarios;
- simulación;
- validación previa;
- envío real;
- múltiples adjuntos;
- múltiples destinatarios;
- historial;
- configuración SMTP;
- Gmail;
- Outlook / Microsoft;
- SMTP personalizado;
- prueba de conexión;
- almacenamiento seguro de credenciales;
- persistencia SQLite;
- manual integrado;
- instalador para Windows.

La versión fue validada mediante:

- 47 tests automatizados;
- pruebas funcionales;
- pruebas de envío;
- pruebas de persistencia;
- pruebas del instalador;
- ejecución desde la aplicación instalada.

---

## Próxima versión

La futura **V2** estará orientada principalmente a mejorar la experiencia del usuario mediante una interfaz gráfica de escritorio.

La evolución prevista incluye:

- JavaFX;
- FXML;
- CSS;
- interfaz gráfica moderna;
- navegación mediante ventanas y formularios;
- selección visual de carpetas;
- tablas gráficas para destinatarios;
- tablas gráficas para historial;
- formularios de configuración;
- cuadros de confirmación;
- flujo visual de simulación y envío;
- mejor presentación de errores y resultados.

El objetivo es conservar el núcleo actual de:

- servicios;
- repositorios;
- seguridad;
- correo;
- persistencia;
- reglas de negocio;

y reemplazar progresivamente la interfaz de consola por una interfaz gráfica.

La V1.0.0 permanecerá como versión estable mientras se desarrolla la V2.

---

## Estrategia de ramas

A partir de V1.0.0 el proyecto utiliza tres ramas principales.

### `main`

Rama de producción.

Contiene únicamente versiones consideradas estables y listas para distribución.

Ejemplos:

```text
v1.0.0
v1.1.0
v2.0.0
```

No se utilizará como rama habitual de desarrollo.

Los cambios llegan a `main` únicamente después de haber sido desarrollados y validados previamente.

### `preproduccion`

Rama destinada a validación final.

Recibe funcionalidades previamente terminadas y permite realizar pruebas integrales antes de promover una nueva versión a producción.

Las mejoras se incorporan progresivamente para comprobar su funcionamiento junto con las funcionalidades previamente validadas.

No se utilizará como rama principal de desarrollo.

### `desarrollo`

Rama principal de integración y desarrollo.

Las nuevas funcionalidades y mejoras se incorporan primero a esta rama.

Para funcionalidades suficientemente grandes pueden utilizarse ramas temporales.

Ejemplos:

```text
feature/javafx
feature/historial-grafico
feature/configuracion-grafica
fix/error-smtp
```

El flujo general será:

```text
feature/*
    ↓
desarrollo
    ↓
preproduccion
    ↓
main
```

Una versión solamente llega a `main` cuando el contenido de `preproduccion` ha sido probado y considerado estable.

---

## Versionado

DocDispatch utiliza releases y tags de Git para identificar versiones estables.

La primera versión estable corresponde a:

```text
v1.0.0
```

Las futuras versiones podrán seguir una estructura como:

```text
v1.0.1
v1.1.0
v2.0.0
```

Los tags permiten conservar una referencia exacta e inmutable del código correspondiente a cada release.

---

## Release V1.0.0

La Release oficial de DocDispatch 1.0.0 incluye:

- código fuente correspondiente a `v1.0.0`;
- instalador para Windows;
- documentación de uso;
- manual integrado;
- 47 tests automatizados aprobados.

El instalador puede descargarse desde la sección **Releases** del repositorio.

Archivo esperado:

```text
DocDispatch-1.0.0.exe
```

Para utilizar la aplicación no es necesario descargar el código fuente.

---

## Distribución

Los archivos compilados no se almacenan directamente en el historial del repositorio.

Elementos como:

```text
target/
dist/
installer/
```

se generan localmente y permanecen excluidos mediante `.gitignore`.

El instalador oficial se distribuye como un asset asociado a la Release correspondiente en GitHub.

Esto mantiene separado:

- el código fuente;
- los artefactos de compilación;
- el instalador distribuible;
- los datos locales de cada usuario.

---

## Datos que no se distribuyen

Antes de publicar una versión se verifica que no formen parte del repositorio ni del instalador:

```text
*.db
*.sqlite
*.sqlite3
```

También se excluyen:

- destinatarios reales;
- correos de prueba;
- historial local;
- credenciales SMTP;
- App Passwords;
- claves de cifrado;
- bases de datos de desarrollo.

De esta forma, cada usuario comienza con una instalación independiente.

---

## Limitaciones de V1.0.0

DocDispatch 1.0.0 utiliza una interfaz de consola.

Aunque la aplicación es completamente funcional, la experiencia de usuario está orientada principalmente a un flujo mediante menús de texto.

La compatibilidad SMTP depende además de las políticas de autenticación del proveedor de correo.

Especialmente en cuentas Microsoft modernas, algunas configuraciones pueden exigir OAuth2 u otros mecanismos de autenticación no implementados en esta versión.

Estas limitaciones forman parte de los aspectos previstos para evolucionar en versiones futuras.

---

## Objetivos del proyecto

DocDispatch busca ofrecer una solución sencilla para automatizar tareas repetitivas de distribución documental mediante correo electrónico.

Los principales objetivos son:

- reducir trabajo manual;
- disminuir errores de selección de archivos;
- evitar omisiones de destinatarios;
- permitir validaciones previas al envío;
- mantener trazabilidad;
- proteger las credenciales del usuario;
- permitir una instalación sencilla en Windows;
- mantener una arquitectura preparada para evolucionar.

---

## Autor

**VandruGG / Andrés Galo**

---

## Licencia

Actualmente el proyecto no define una licencia pública específica.

Todos los derechos permanecen reservados hasta que se incorpore una licencia explícita al repositorio.