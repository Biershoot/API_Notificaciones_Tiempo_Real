#!/bin/bash

# =======================================================
# SCRIPT DE SEGURIDAD PARA DOCKER
# =======================================================
# Este script verifica y configura medidas de seguridad para Docker

set -e

echo "🔒 Iniciando verificación de seguridad Docker..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para imprimir mensajes
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Verificar si Docker está instalado
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker no está instalado"
        exit 1
    fi
    print_status "Docker está instalado"
}

# Verificar si Docker Compose está instalado
check_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose no está instalado"
        exit 1
    fi
    print_status "Docker Compose está instalado"
}

# Verificar archivo .env
check_env_file() {
    if [ ! -f .env ]; then
        print_warning "Archivo .env no encontrado"
        echo "Creando archivo .env desde env.example..."
        if [ -f env.example ]; then
            cp env.example .env
            print_status "Archivo .env creado desde env.example"
            print_warning "IMPORTANTE: Configura las variables de entorno en .env antes de continuar"
        else
            print_error "Archivo env.example no encontrado"
            exit 1
        fi
    else
        print_status "Archivo .env encontrado"
    fi
}

# Verificar credenciales sensibles en .env
check_sensitive_data() {
    if [ -f .env ]; then
        # Verificar si hay valores por defecto que deben ser cambiados
        if grep -q "your_" .env || grep -q "default_" .env; then
            print_warning "Se encontraron valores por defecto en .env que deben ser cambiados"
            echo "Variables que necesitan configuración:"
            grep -E "(your_|default_)" .env || true
        else
            print_status "No se encontraron valores por defecto en .env"
        fi
    fi
}

# Verificar permisos de archivos sensibles
check_file_permissions() {
    if [ -f .env ]; then
        PERMS=$(stat -c %a .env)
        if [ "$PERMS" != "600" ]; then
            print_warning "Permisos de .env no son seguros (actual: $PERMS, recomendado: 600)"
            chmod 600 .env
            print_status "Permisos de .env corregidos a 600"
        else
            print_status "Permisos de .env son seguros"
        fi
    fi
}

# Verificar configuración de Docker daemon
check_docker_daemon() {
    if [ -f /etc/docker/daemon.json ]; then
        print_status "Archivo de configuración de Docker daemon encontrado"
    else
        print_warning "Archivo de configuración de Docker daemon no encontrado"
        echo "Considera crear /etc/docker/daemon.json con configuraciones de seguridad"
    fi
}

# Verificar imágenes Docker vulnerables
check_docker_images() {
    echo "Verificando imágenes Docker..."
    docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | head -1
    docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep -v "REPOSITORY"
}

# Verificar contenedores en ejecución
check_running_containers() {
    echo "Verificando contenedores en ejecución..."
    if docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -q .; then
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    else
        print_status "No hay contenedores en ejecución"
    fi
}

# Verificar redes Docker
check_docker_networks() {
    echo "Verificando redes Docker..."
    docker network ls --format "table {{.Name}}\t{{.Driver}}\t{{.Scope}}"
}

# Verificar volúmenes Docker
check_docker_volumes() {
    echo "Verificando volúmenes Docker..."
    docker volume ls --format "table {{.Name}}\t{{.Driver}}"
}

# Función principal
main() {
    echo "=================================================="
    echo "🔒 VERIFICACIÓN DE SEGURIDAD DOCKER"
    echo "=================================================="
    
    check_docker
    check_docker_compose
    check_env_file
    check_sensitive_data
    check_file_permissions
    check_docker_daemon
    
    echo ""
    echo "=================================================="
    echo "📊 ESTADO ACTUAL DE DOCKER"
    echo "=================================================="
    
    check_docker_images
    echo ""
    check_running_containers
    echo ""
    check_docker_networks
    echo ""
    check_docker_volumes
    
    echo ""
    echo "=================================================="
    echo "✅ VERIFICACIÓN COMPLETADA"
    echo "=================================================="
    print_status "Script de seguridad completado"
    
    echo ""
    echo "📋 RECOMENDACIONES DE SEGURIDAD:"
    echo "1. Configura variables de entorno sensibles en .env"
    echo "2. Usa secrets de Docker para credenciales en producción"
    echo "3. Implementa escaneo de vulnerabilidades regular"
    echo "4. Mantén imágenes Docker actualizadas"
    echo "5. Usa redes Docker aisladas"
    echo "6. Implementa logging y monitoreo"
}

# Ejecutar función principal
main "$@"
