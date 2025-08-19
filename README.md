# 🔔 API de Notificaciones en Tiempo Real

API completa de notificaciones con WebSockets, Redis Pub/Sub, **persistencia avanzada**, **métricas con Spring Boot Actuator** y **contenerización Docker** para monitoreo en tiempo real.

## 🚀 Características Avanzadas

- ✅ **API REST** completa con Swagger UI
- ✅ **WebSockets** para notificaciones en tiempo real
- ✅ **Redis Pub/Sub** para arquitectura distribuida
- ✅ **Persistencia avanzada** con estado de lectura
- ✅ **Spring Boot Actuator** con métricas personalizadas
- ✅ **Monitoreo en tiempo real** con contadores y gauges
- ✅ **Métricas para Prometheus** listas para producción
- ✅ **Contenerización Docker** completa
- ✅ **Docker Compose** para stack completo
- ✅ **Configuración flexible** para múltiples entornos
- ✅ **Filtros inteligentes** (todas/no leídas)
- ✅ **Contadores** de notificaciones
- ✅ **Marcado masivo** como leídas
- ✅ **Base de datos** MySQL + H2 embebida
- ✅ **Documentación** automática con OpenAPI
- ✅ **Cliente demo avanzado** HTML incluido

## 🐳 Despliegue con Docker

### 🛠️ Construcción rápida
```bash
# Windows
.\build-docker.bat

# Linux/Mac
./build-docker.sh
```

### 📦 Opciones de despliegue

#### Desarrollo (solo H2, sin dependencias externas):
```bash
docker-compose -f docker-compose.dev.yml up
```

#### Producción completa (MySQL + Redis + App):
```bash
docker-compose up
```

#### Solo la aplicación:
```bash
docker run -p 8080:8080 notifications-api:latest
```

### 🎯 URLs disponibles después del despliegue:
- **API REST**: http://localhost:8080/api/
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Cliente WebSocket Demo**: http://localhost:8080/websocket-client.html
- **Actuator Health**: http://localhost:8080/actuator/health
- **Métricas personalizadas**: http://localhost:8080/api/metrics/notifications

## 🔧 Configuraciones Docker

### docker-compose.yml (Producción)
```yaml
# Stack completo: App + MySQL + Redis
# Persistencia de datos con volumes
# Red interna para servicios
# Reinicio automático
```

### docker-compose.dev.yml (Desarrollo)
```yaml
# Solo la aplicación con H2 embebida
# Perfecto para desarrollo rápido
# Sin dependencias externas
```

## 📊 Monitoreo en Contenedores

### Métricas disponibles en Docker:
```bash
# Health check de la aplicación
curl http://localhost:8080/actuator/health

# Métricas de la aplicación
curl http://localhost:8080/api/metrics/notifications/summary

# Métricas para Prometheus
curl http://localhost:8080/actuator/prometheus
```

### Logs de contenedores:
```bash
# Ver logs de la aplicación
docker-compose logs -f app

# Ver logs de MySQL
docker-compose logs -f mysql

# Ver logs de Redis
docker-compose logs -f redis
```

## 🏗️ Arquitectura de Contenedores

```
┌─────────────────────────────────────────┐
│              Docker Host                │
├─────────────────────────────────────────┤
│  ┌─────────────────┐  ┌──────────────┐  │
│  │ notifications-  │  │   Redis      │  │
│  │     api         │  │  :6379       │  │
│  │   :8080         │  │              │  │
│  └─────────────────┘  └──────────────┘  │
│  ┌─────────────────────────────────────┐ │
│  │            MySQL                    │ │
│  │            :3306                    │ │
│  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

# 🔔 API de Notificaciones en Tiempo Real

API completa de notificaciones con WebSockets, Redis Pub/Sub y **persistencia avanzada** para gestión completa de estado de lectura.

## 🚀 Características Avanzadas

- ✅ **API REST** completa con Swagger UI
- ✅ **WebSockets** para notificaciones en tiempo real
- ✅ **Redis Pub/Sub** para arquitectura distribuida
- ✅ **Persistencia avanzada** con estado de lectura
- ✅ **Filtros inteligentes** (todas/no leídas)
- ✅ **Contadores** de notificaciones
- ✅ **Marcado masivo** como leídas
- ✅ **Base de datos** MySQL + H2 embebida
- ✅ **Documentación** automática con OpenAPI
- ✅ **Cliente demo avanzado** HTML incluido

## 📡 Endpoints Avanzados de la API

### 📩 Notificaciones
- `POST /api/notifications/send` - Enviar notificación
- `GET /api/notifications/{username}` - Ver **todas** las notificaciones (ordenadas)
- `GET /api/notifications/{username}/unread` - Ver **solo no leídas** (ordenadas)
- `GET /api/notifications/{username}/unread/count` - **Contar** no leídas
- `PUT /api/notifications/{id}/read` - Marcar **una específica** como leída
- `POST /api/notifications/{username}/mark-all-read` - Marcar **todas** como leídas

### 👥 Usuarios
- `POST /api/users` - Crear usuario
- `GET /api/users` - Listar usuarios
- `GET /api/users/{username}` - Buscar usuario
- `DELETE /api/users/{id}` - Eliminar usuario

## 🎯 Nuevas Funcionalidades del Cliente Web

### 📊 Dashboard de Estadísticas
- **Contador total** de notificaciones
- **Contador de no leídas** con badge visual
- **Auto-actualización** cada 10 segundos

### 🔍 Filtros Inteligentes
- **"Todas"** - Ver historial completo
- **"Solo No Leídas"** - Focus en pendientes
- **Ordenamiento** por timestamp descendente

### ✅ Gestión Avanzada
- **Marcar individual** como leída
- **Marcar todas** como leídas de una vez
- **Estados visuales** diferenciados (leída/no leída)
- **Notificaciones toast** para nuevos mensajes

## 🧪 Casos de Uso Avanzados

### Escenario 1: Dashboard Empresarial
```bash
# Ver resumen de notificaciones
curl "http://localhost:8080/api/notifications/admin/unread/count"
# Respuesta: 5

# Obtener solo las urgentes
curl "http://localhost:8080/api/notifications/admin/unread"

# Marcar todas como revisadas
curl -X POST "http://localhost:8080/api/notifications/admin/mark-all-read"
```

### Escenario 2: App Móvil
```bash
# Badge de notificaciones
GET /api/notifications/{user}/unread/count

# Lista para mostrar
GET /api/notifications/{user}/unread

# Usuario lee una específica
PUT /api/notifications/123/read
```

### Escenario 3: Sistema de Monitoreo
```bash
# Enviar alerta crítica
curl -X POST "http://localhost:8080/api/notifications/send" \
  -d "username=admin&message=🚨 Sistema crítico: CPU al 95%"

# Todas las instancias reciben via Redis → WebSocket
# Admin ve notificación instantáneamente con badge actualizado
```

## 🏗️ Arquitectura de Persistencia

### Base de Datos
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    message TEXT,
    timestamp DATETIME,
    read BOOLEAN DEFAULT FALSE,
    user_id BIGINT,
    INDEX idx_username_timestamp (username, timestamp),
    INDEX idx_username_read (username, read)
);
```

### Consultas Optimizadas
- **Por usuario y timestamp**: `ORDER BY timestamp DESC`
- **Solo no leídas**: `WHERE read = false`
- **Conteos eficientes**: Índices en `username` y `read`
- **Updates masivos**: `UPDATE WHERE username = ?`

## 📱 Cliente Web Avanzado

### Nuevas Características
1. **Panel de Estadísticas** - Contadores en tiempo real
2. **Filtros Dinámicos** - Alternar entre vistas
3. **Gestión Individual** - Botón "marcar como leída" por notificación
4. **Gestión Masiva** - "Marcar todas como leídas"
5. **Estados Visuales** - Colores diferenciados leída/no leída
6. **Toast Notifications** - Popup para nuevas notificaciones
7. **Auto-refresh** - Sincronización automática cada 10s

### Uso del Cliente
1. **Conectar** como usuario (ej: user1)
2. **Ver estadísticas** - Total y no leídas en tiempo real
3. **Filtrar** - Alternar "Todas" / "Solo No Leídas"
4. **Gestionar** - Marcar individual o masivamente
5. **Recibir** - Notificaciones instantáneas con toast

## 🎯 Testing de Funcionalidades Avanzadas

### Test de Persistencia
```bash
# 1. Enviar varias notificaciones
curl -X POST "localhost:8080/api/notifications/send" -d "username=user1&message=Mensaje 1"
curl -X POST "localhost:8080/api/notifications/send" -d "username=user1&message=Mensaje 2"

# 2. Verificar conteo
curl "localhost:8080/api/notifications/user1/unread/count"  # Respuesta: 2

# 3. Marcar una como leída
curl -X PUT "localhost:8080/api/notifications/1/read"

# 4. Verificar nuevo conteo
curl "localhost:8080/api/notifications/user1/unread/count"  # Respuesta: 1

# 5. Ver solo no leídas
curl "localhost:8080/api/notifications/user1/unread"      # Solo mensaje 2

# 6. Ver todas (incluye leídas)
curl "localhost:8080/api/notifications/user1"             # Ambos mensajes

# 7. Marcar todas como leídas
curl -X POST "localhost:8080/api/notifications/user1/mark-all-read"

# 8. Verificar resultado
curl "localhost:8080/api/notifications/user1/unread/count"  # Respuesta: 0
```

## 📋 Requisitos

### Opción 1: Desarrollo rápido (H2 + Redis embebido)
- Java 17+
- Redis Server (opcional - ver instalación abajo)

### Opción 2: Producción (MySQL + Redis)
- Java 17+
- MySQL Server
- Redis Server

## 🛠️ Instalación de Redis

### Windows
```bash
# Descargar Redis desde: https://github.com/microsoftarchive/redis/releases
# O usar Docker:
docker run -d -p 6379:6379 redis:latest
```

### Linux/Mac
```bash
# Ubuntu/Debian
sudo apt install redis-server
sudo systemctl start redis-server

# Mac con Homebrew
brew install redis
brew services start redis
```

## 🏃‍♂️ Ejecución

### Desarrollo rápido con H2
```bash
# Inicia Redis primero (si no tienes Docker)
redis-server

# Ejecuta la aplicación
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

### Producción con MySQL
```bash
# Asegúrate de tener MySQL y Redis ejecutándose
./mvnw spring-boot:run
```

## 🔗 Accesos

Una vez iniciada la aplicación:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Cliente WebSocket Demo**: http://localhost:8080/websocket-client.html
- **H2 Console**: http://localhost:8080/h2-console (perfil h2)
- **API REST**: http://localhost:8080/api/

## 👥 Usuarios de Prueba

| Username | Password | Rol   |
|----------|----------|-------|
| admin    | password | ADMIN |
| user1    | password | USER  |
| user2    | password | USER  |

## 📚 Tecnologías

- **Spring Boot 3.5.4**
- **Spring WebSocket + STOMP**
- **Spring Data JPA**
- **Spring Data Redis**
- **MySQL + H2**
- **Swagger/OpenAPI**
- **Lombok**

## 👨‍💻 Autor

Alejandro - [GitHub](https://github.com/Biershoot)
