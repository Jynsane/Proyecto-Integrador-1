# Script de configuración para LibreriaSystem
Write-Host "Verificando requisitos del sistema..." -ForegroundColor Cyan

# Verificar Java
try {
    $javaVersion = cmd /c java -version 2>&1
    if ($javaVersion -like '*version "17*') {
        Write-Host "✓ Java 17 está instalado correctamente" -ForegroundColor Green
    } else {
        Write-Host "✗ Se requiere Java 17. Por favor instálalo desde: https://adoptium.net/" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Java no está instalado. Por favor instala Java 17 desde: https://adoptium.net/" -ForegroundColor Red
    exit 1
}

# Verificar Maven
try {
    $mvnVersion = cmd /c mvn -version
    Write-Host "✓ Maven está instalado correctamente" -ForegroundColor Green
} catch {
    Write-Host "✗ Maven no está instalado. Por favor instálalo desde: https://maven.apache.org/" -ForegroundColor Red
    exit 1
}

# Verificar MySQL
try {
    $mysqlService = Get-Service -Name "MySQL*" -ErrorAction Stop
    if ($mysqlService.Status -eq "Running") {
        Write-Host "✓ MySQL está instalado y ejecutándose" -ForegroundColor Green
    } else {
        Write-Host "✗ MySQL está instalado pero no se está ejecutando" -ForegroundColor Yellow
        Write-Host "Iniciando MySQL..." -ForegroundColor Cyan
        Start-Service $mysqlService.Name
    }
} catch {
    Write-Host "✗ MySQL no está instalado. Por favor instálalo desde: https://dev.mysql.com/downloads/installer/" -ForegroundColor Red
    exit 1
}

Write-Host "`nConfigurando la base de datos..." -ForegroundColor Cyan

# Ejecutar script SQL
try {
    cmd /c mysql -u root -proot -e "source src/main/resources/database.sql"
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Base de datos configurada correctamente" -ForegroundColor Green
    } else {
        Write-Host "✗ Error al configurar la base de datos" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Error al configurar la base de datos. Asegúrate de que MySQL está ejecutándose y la contraseña es correcta" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

Write-Host "`nCompilando el proyecto..." -ForegroundColor Cyan

# Compilar con Maven
try {
    mvn clean package
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Proyecto compilado correctamente" -ForegroundColor Green
    } else {
        Write-Host "✗ Error al compilar el proyecto" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "✗ Error al compilar el proyecto" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

Write-Host "`n¡Todo listo! Para ejecutar el proyecto:" -ForegroundColor Cyan
Write-Host "java -jar target/LibreriaSystem-1.0-SNAPSHOT.jar" -ForegroundColor Yellow