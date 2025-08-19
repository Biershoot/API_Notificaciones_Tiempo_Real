#!/bin/bash
# Script para construir y ejecutar la API de Notificaciones con Docker

echo "🐳 Construyendo la imagen Docker para API de Notificaciones..."
docker build -t notifications-api:latest .

echo "✅ Imagen construida exitosamente!"
echo "🚀 Para ejecutar la aplicación, usa uno de estos comandos:"
echo ""
echo "   Desarrollo (solo H2):"
echo "   docker-compose -f docker-compose.dev.yml up"
echo ""
echo "   Producción completa (MySQL + Redis):"
echo "   docker-compose up"
echo ""
echo "   Solo la aplicación:"
echo "   docker run -p 8080:8080 notifications-api:latest"
