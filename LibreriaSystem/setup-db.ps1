# Inicializar la base de datos
Write-Host "Creando base de datos..." -ForegroundColor Cyan

try {
    # Crear la base de datos
    mysql --user=root --password=root -e "DROP DATABASE IF EXISTS libreria_db; CREATE DATABASE libreria_db;"

    # Leer el contenido del archivo SQL
    $sqlContent = Get-Content -Path "src/main/resources/database.sql" -Raw

    # Ejecutar el script SQL
    $command2 = 'mysql --user=root --password=root libreria_db'
    $sqlContent | cmd /c $command2

    Write-Host "✓ Base de datos configurada correctamente" -ForegroundColor Green
} catch {
    Write-Host "✗ Error al configurar la base de datos:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
