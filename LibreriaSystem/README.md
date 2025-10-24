# LibreriaSystem

Aplicación Java de escritorio (Swing) para control de inventario y ventas de librería.

Requisitos previos (Windows):
- JDK 17 instalado y configurado (JAVA_HOME)
- Maven instalado y en PATH (opcional si usas un IDE como IntelliJ/Eclipse que gestione Maven)
- MySQL server en ejecución

Pasos para configurar y ejecutar:

1) Configurar la base de datos
- Abrir PowerShell y conectarte a MySQL (ejemplo):

```powershell
# Conéctate con tu usuario root u otro
mysql -u root -p
# Luego en el prompt de MySQL importa el script
SOURCE C:/ruta/a/tu/proyecto/src/main/resources/database.sql;
```

> Nota: reemplaza la ruta por la ruta absoluta a `src/main/resources/database.sql` en este proyecto.

2) Actualizar credenciales de conexión
- Edita `src/main/java/com/libreria/util/DatabaseConnection.java` y ajusta `URL`, `USER` y `PASSWORD` según tu instalación de MySQL.

3) Compilar el proyecto
- Desde PowerShell en la carpeta del proyecto (donde está `pom.xml`):

```powershell
cd "C:\WordsitoUTP\CICLO 6\Curso Integrador\PROYECTO FINAL INTEGRADOR\LibreriaSystem"
mvn -DskipTests package
```

Si `mvn` no está instalado, abre tu IDE (IntelliJ/Eclipse/NetBeans) y carga el proyecto Maven, luego compílalo desde el IDE.

4) Ejecutar la aplicación
- Desde el IDE: ejecuta la clase `com.libreria.view.MainFrame` como aplicación Java.
- Desde terminal (si generaste JAR):

```powershell
# Si el pom produce un JAR ejecutable o usas el JAR generado
java -cp target/LibreriaSystem-1.0-SNAPSHOT.jar com.libreria.view.MainFrame
```

Notas y limitaciones
- El proyecto usa MySQL. Si prefieres H2 en memoria para pruebas, puedo añadirlo.
- Si no tienes `mvn` disponible, puedo explicarte cómo instalar Maven en Windows.

Si quieres, puedo:
- Generar un JAR ejecutable (con plugin maven-shade) en el `pom.xml`.
- Añadir instrucciones para configurar un usuario MySQL y permisos.
- Añadir un script PowerShell que importe `database.sql` automáticamente.

Dime qué prefieres y lo hago.