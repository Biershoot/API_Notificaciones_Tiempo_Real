# 🔒 Guía de Seguridad Docker

## 📋 Resumen

Esta guía describe las medidas de seguridad implementadas para evitar fugas de información en los contenedores Docker de la API de Notificaciones.

## 🛡️ Medidas de Seguridad Implementadas

### 1. **Archivo .dockerignore**
- **Propósito**: Evitar copiar archivos sensibles al contenedor
- **Archivos excluidos**:
  - Archivos de configuración sensibles (`.env`, `application-prod.properties`)
  - Credenciales y claves (`*.key`, `*.pem`, `*.jks`)
  - Archivos de desarrollo (`.idea/`, `.vscode/`)
  - Logs y archivos temporales
  - Archivos de Git y documentación

### 2. **Dockerfile Seguro**
- **Usuario no-root**: Ejecuta la aplicación como usuario `appuser`
- **Permisos correctos**: Archivos con permisos 755
- **Health checks**: Monitoreo de salud del contenedor
- **Configuración JVM segura**: Parámetros de seguridad optimizados

### 3. **Variables de Entorno**
- **Archivo de ejemplo**: `env.example` sin información sensible
- **Variables requeridas**: Todas las credenciales deben configurarse
- **Valores por defecto**: Solo para desarrollo, no para producción

### 4. **Docker Compose Seguro**
- **Health checks**: Verificación de salud de servicios
- **Redes aisladas**: Red interna para comunicación entre servicios
- **Volúmenes seguros**: Datos persistentes con permisos correctos
- **Límites de recursos**: Prevención de ataques DoS

### 5. **Configuración de Producción**
- **Puertos restringidos**: Solo acceso local para bases de datos
- **Nginx reverse proxy**: Capa adicional de seguridad
- **Límites de memoria y CPU**: Control de recursos
- **Configuración de seguridad**: `no-new-privileges` y `read-only`

## 🚀 Uso Seguro

### Desarrollo Local
```bash
# 1. Copiar archivo de ejemplo
cp env.example .env

# 2. Configurar variables sensibles en .env
# NUNCA committear .env al repositorio

# 3. Ejecutar con Docker Compose
docker-compose up -d
```

### Producción
```bash
# 1. Configurar variables de entorno de producción
# Usar secrets de Docker o variables de entorno del sistema

# 2. Ejecutar con configuración de producción
docker-compose -f docker-compose.prod.yml up -d
```

## 🔍 Verificación de Seguridad

### Script de Verificación
```bash
# En Linux/Mac
./scripts/docker-security.sh

# En Windows (PowerShell)
# Ejecutar manualmente las verificaciones del script
```

### Verificaciones Manuales
1. **Archivo .env**: Verificar que no esté en el repositorio
2. **Permisos**: Archivos sensibles con permisos 600
3. **Contenedores**: Ejecutando como usuario no-root
4. **Redes**: Servicios en red aislada
5. **Logs**: Sin información sensible en logs

## ⚠️ Puntos de Atención

### ❌ NO Hacer
- Committear archivo `.env` al repositorio
- Usar credenciales hardcodeadas en Dockerfiles
- Exponer puertos de base de datos públicamente
- Ejecutar contenedores como root
- Usar imágenes Docker no oficiales

### ✅ SÍ Hacer
- Usar variables de entorno para credenciales
- Implementar health checks
- Usar redes Docker aisladas
- Mantener imágenes actualizadas
- Implementar logging seguro
- Escanear vulnerabilidades regularmente

## 🔧 Configuración Avanzada

### Docker Daemon Seguro
```json
{
  "userns-remap": "default",
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "10m",
    "max-file": "3"
  },
  "live-restore": true,
  "userland-proxy": false
}
```

### Nginx Configuration
```nginx
# Configuración de seguridad para nginx
server {
    listen 80;
    server_name _;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name _;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    # Headers de seguridad
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains";
    
    location / {
        proxy_pass http://app:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 📊 Monitoreo de Seguridad

### Métricas a Monitorear
- Intentos de acceso fallidos
- Uso de recursos anormal
- Logs de errores de seguridad
- Vulnerabilidades en imágenes
- Acceso a puertos restringidos

### Herramientas Recomendadas
- **Trivy**: Escaneo de vulnerabilidades
- **Clair**: Análisis de imágenes
- **Falco**: Detección de comportamiento anormal
- **Prometheus + Grafana**: Monitoreo de métricas

## 🆘 Incidentes de Seguridad

### En Caso de Fuga de Información
1. **Detener servicios**: `docker-compose down`
2. **Rotar credenciales**: Cambiar todas las contraseñas
3. **Auditar logs**: Revisar logs de acceso
4. **Actualizar imágenes**: Rebuild con imágenes seguras
5. **Notificar**: Seguir protocolo de incidentes

### Contacto de Seguridad
- **Email**: alejodim27@gmail.com
- **GitHub**: [@Biershoot](https://github.com/Biershoot)

## 📚 Recursos Adicionales

- [Docker Security Best Practices](https://docs.docker.com/engine/security/)
- [OWASP Docker Security](https://owasp.org/www-project-docker-security/)
- [CIS Docker Benchmark](https://www.cisecurity.org/benchmark/docker/)
- [Docker Security Scanning](https://docs.docker.com/engine/scan/)

---

**Última actualización**: $(date)
**Versión**: 1.0.0
