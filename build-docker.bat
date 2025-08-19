@echo off
echo 🐳 Construyendo la imagen Docker para API de Notificaciones...
docker build -t notifications-api:latest .

if %ERRORLEVEL% EQU 0 (
    echo ✅ Imagen construida exitosamente!
    echo 🚀 Para ejecutar la aplicacion, usa uno de estos comandos:
    echo.
    echo    Desarrollo ^(solo H2^):
    echo    docker-compose -f docker-compose.dev.yml up
    echo.
    echo    Produccion completa ^(MySQL + Redis^):
    echo    docker-compose up
    echo.
    echo    Solo la aplicacion:
    echo    docker run -p 8080:8080 notifications-api:latest
) else (
    echo ❌ Error construyendo la imagen Docker
    pause
)
