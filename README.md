# 🔔 API de Notificaciones en Tiempo Real - Enterprise Edition

[![CI/CD Pipeline](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/actions/workflows/ci-cd.yml)
[![codecov](https://codecov.io/gh/Biershoot/API_Notificaciones_Tiempo_Real/branch/main/graph/badge.svg)](https://codecov.io/gh/Biershoot/API_Notificaciones_Tiempo_Real)
[![Docker Hub](https://img.shields.io/docker/pulls/biershoot/notifications-api.svg)](https://hub.docker.com/r/biershoot/notifications-api)

**API empresarial completa de notificaciones** con WebSockets, Redis Pub/Sub, reportes automáticos, envío por correo electrónico, métricas avanzadas con Prometheus, monitoreo con Grafana y contenerización Docker para entornos de producción.

## 📋 Índice

- [Características](#-características-enterprise)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Tecnologías](#-tecnologías-utilizadas)
- [Requisitos](#-requisitos)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Documentación API](#-documentación-api)
- [Monitoreo](#-monitoreo-y-observabilidad)
- [Pruebas](#-pruebas)
- [Despliegue](#-despliegue)
- [Arquitectura](#-arquitectura)
- [Roadmap](#-roadmap)
- [Contribución](#-contribución)
- [Licencia](#-licencia)

## 🚀 Características Enterprise

### 📡 Sistema de Notificaciones Completo
- ✅ **API REST** completa con Swagger UI
- ✅ **WebSockets** para notificaciones en tiempo real
- ✅ **Redis Pub/Sub** para arquitectura distribuida y escalable
- ✅ **Persistencia avanzada** con estado de lectura y tipos de notificación
- ✅ **Filtros inteligentes** por usuario, estado, tipo y prioridad
- ✅ **Búsqueda de texto** en mensajes y títulos
- ✅ **Limpieza automática** de notificaciones antiguas
- ✅ **Logs de auditoría** para trazabilidad completa
- ✅ **Marcado masivo** como leídas
- ✅ **Contadores de notificaciones** en tiempo real

### 📊 Reportes y Análisis
- ✅ **Generación automática** de reportes PDF y Excel
- ✅ **Reportes programados** con cron jobs
- ✅ **Envío por correo electrónico** de reportes
- ✅ **Métricas de rendimiento** detalladas
- ✅ **Estadísticas por usuario** y período

### 🔒 Seguridad Empresarial
- ✅ **Autenticación** con Spring Security
- ✅ **Autorización basada en roles** (ADMIN, USER)
- ✅ **Validación de entrada** exhaustiva con Bean Validation
- ✅ **Protección CORS** configurada
- ✅ **Manejo de excepciones** centralizado
- ✅ **Análisis de dependencias** automatizado con OWASP

### 🔄 DevOps y Operaciones
- ✅ **Contenerización Docker** completa
- ✅ **Composición Docker** para desarrollo y producción
- ✅ **Manifiestos Kubernetes** listos para usar
- ✅ **CI/CD automatizado** con GitHub Actions
- ✅ **Pruebas automatizadas** (unitarias, integración)
- ✅ **Migraciones de BD** automáticas con Flyway
- ✅ **Análisis estático** con SpotBugs

## 📡 Endpoints de la API

### 🔔 Notificaciones

#### Gestión Básica
- `POST /api/notifications/send` - Enviar notificación
- `GET /api/notifications/{username}` - Obtener todas las notificaciones del usuario
- `GET /api/notifications/{username}/unread` - Obtener solo notificaciones no leídas
- `GET /api/notifications/{username}/unread/count` - Contar notificaciones no leídas
- `PUT /api/notifications/{id}/read` - Marcar una notificación como leída
- `POST /api/notifications/{username}/mark-all-read` - Marcar todas como leídas

#### Filtros y Búsqueda
- `GET /api/notifications/{username}/search?text={searchText}` - Buscar por texto
- `GET /api/notifications/{username}/filter?type={type}&priority={priority}` - Filtrar por tipo y prioridad
- `GET /api/notifications/{username}/date-range?start={startDate}&end={endDate}` - Filtrar por rango de fechas

#### Gestión Avanzada
- `DELETE /api/notifications/{username}/cleanup?days={days}` - Limpiar notificaciones antiguas
- `GET /api/notifications/{username}/stats` - Obtener estadísticas del usuario

### 👥 Usuarios
- `POST /api/users` - Crear usuario
- `GET /api/users` - Listar usuarios
- `GET /api/users/{username}` - Buscar usuario
- `PUT /api/users/{id}` - Actualizar usuario
- `DELETE /api/users/{id}` - Eliminar usuario

### 📊 Reportes
- `GET /api/reports/notifications/pdf` - Generar reporte PDF de notificaciones
- `GET /api/reports/notifications/excel` - Generar reporte Excel de notificaciones
- `GET /api/reports/users/pdf` - Generar reporte PDF de usuarios
- `GET /api/reports/users/excel` - Generar reporte Excel de usuarios

### 📈 Métricas y Monitoreo
- `GET /api/metrics/notifications` - Métricas de notificaciones
- `GET /api/metrics/users` - Métricas de usuarios
- `GET /api/metrics/system` - Métricas del sistema
- `GET /actuator/health` - Estado de salud de la aplicación
- `GET /actuator/metrics` - Métricas Prometheus

### 🔌 WebSockets
- `WS /ws` - Endpoint WebSocket para notificaciones en tiempo real
- `STOMP /topic/notifications/{username}` - Suscripción a notificaciones por usuario

## 🛠 Tecnologías Utilizadas

### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.2.5** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Seguridad y autenticación
- **Spring WebSocket** - Comunicación en tiempo real
- **Spring Data Redis** - Cache y Pub/Sub

### Base de Datos
- **MySQL** - Base de datos principal (producción)
- **H2** - Base de datos embebida (desarrollo)
- **Redis** - Cache y mensajería Pub/Sub
- **Flyway** - Migraciones de base de datos

### Documentación y Testing
- **Swagger/OpenAPI 3.0** - Documentación de API
- **JUnit 5** - Framework de testing
- **JaCoCo** - Cobertura de código
- **SpotBugs** - Análisis estático

### Monitoreo y Observabilidad
- **Spring Boot Actuator** - Endpoints de monitoreo
- **Micrometer** - Métricas de aplicación
- **Prometheus** - Recolección de métricas

### Reportes
- **iText7** - Generación de PDFs
- **Apache POI** - Generación de Excel
- **Spring Mail** - Envío de correos

## 📦 Requisitos

### Mínimos
- Java 21+
- Maven 3.8+
- Redis Server (opcional)

### Opcionales
- MySQL 8.0+ (para producción)
- Docker y Docker Compose
- Kubernetes

## 💻 Instalación

### Método 1: Desarrollo Local

```bash
# Clonar el repositorio
git clone https://github.com/Biershoot/API_Notificaciones_Tiempo_Real.git
cd API_Notificaciones_Tiempo_Real

# Compilar el proyecto
./mvnw clean compile

# Ejecutar pruebas
./mvnw test

# Ejecutar la aplicación
./mvnw spring-boot:run
```

### Método 2: Con Docker

```bash
# Construir imagen Docker
docker build -t notifications-api .

# Ejecutar contenedor
docker run -p 8080:8080 notifications-api
```

### Método 3: Con Docker Compose

```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f
```

## 🎯 Uso

### 1. Acceso a la Documentación
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### 2. Usuarios de Prueba
| Username | Password | Rol   |
|----------|----------|-------|
| admin    | password | ADMIN |
| user1    | password | USER  |
| user2    | password | USER  |

### 3. Ejemplos de Uso

#### Enviar Notificación
```bash
curl -X POST "http://localhost:8080/api/notifications/send" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "message": "Nueva notificación de prueba",
    "type": "INFO",
    "priority": "NORMAL"
  }'
```

#### Obtener Notificaciones No Leídas
```bash
curl "http://localhost:8080/api/notifications/user1/unread"
```

#### Contar Notificaciones No Leídas
```bash
curl "http://localhost:8080/api/notifications/user1/unread/count"
```

#### Marcar Como Leída
```bash
curl -X PUT "http://localhost:8080/api/notifications/1/read"
```

### 4. WebSockets

#### Conectar con JavaScript
```javascript
const socket = new WebSocket('ws://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    
    // Suscribirse a notificaciones del usuario
    stompClient.subscribe('/topic/notifications/user1', function (notification) {
        console.log('Nueva notificación:', JSON.parse(notification.body));
    });
});
```

## 📚 Documentación API

La documentación completa de la API está disponible en:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console (solo desarrollo)

## 📈 Monitoreo y Observabilidad

### Endpoints de Actuator
- **Health Check**: http://localhost:8080/actuator/health
- **Métricas**: http://localhost:8080/actuator/metrics
- **Info**: http://localhost:8080/actuator/info
- **Prometheus**: http://localhost:8080/actuator/prometheus

### Métricas Disponibles
- `notifications.sent` - Notificaciones enviadas
- `notifications.read` - Notificaciones leídas
- `notifications.unread` - Notificaciones no leídas
- `websocket.connections` - Conexiones WebSocket activas
- `redis.publish.events` - Eventos publicados en Redis

## 🧪 Pruebas

### Ejecutar Todas las Pruebas
```bash
./mvnw test
```

### Ejecutar con Cobertura
```bash
./mvnw clean test jacoco:report
```

### Análisis de Seguridad
```bash
./mvnw dependency-check:check
```

### Análisis Estático
```bash
./mvnw spotbugs:check
```

## 🚀 Despliegue

### Docker
```bash
# Construir imagen
docker build -t notifications-api .

# Ejecutar
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod notifications-api
```

### Kubernetes
```bash
# Aplicar manifiestos
kubectl apply -f k8s/

# Verificar estado
kubectl get pods -l app=notifications-api
```

### Variables de Entorno
```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/notifications
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Email
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu-email@gmail.com
SPRING_MAIL_PASSWORD=tu-password
```

## 🏗️ Arquitectura

### Componentes Principales
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Cliente Web   │    │   Cliente Móvil │    │   Otros APIs    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │    API Gateway/Load       │
                    │        Balancer           │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │   Notifications API       │
                    │   (Spring Boot)           │
                    └─────────────┬─────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
┌─────────▼─────────┐  ┌─────────▼─────────┐  ┌─────────▼─────────┐
│   MySQL Database  │  │   Redis Cache     │  │   WebSocket       │
│                   │  │   & Pub/Sub       │  │   Broker          │
└───────────────────┘  └───────────────────┘  └───────────────────┘
```

### Flujo de Notificaciones
1. **Recepción**: API recibe solicitud de notificación
2. **Persistencia**: Se guarda en base de datos
3. **Publicación**: Se publica en Redis Pub/Sub
4. **Distribución**: WebSocket envía a todos los clientes conectados
5. **Logging**: Se registra en logs de auditoría

## 🔄 Roadmap

### v2.5.0 - Próximas mejoras
- ➕ Notificaciones push móviles (FCM/APNS)
- ➕ Integración con sistemas de mensajería (Slack, Teams)
- ➕ Generación de reportes avanzados con gráficos
- ➕ Dashboard administrativo web

### v2.0.0 - Funcionalidades actuales ✅
- ✅ API REST avanzada con filtros y búsqueda
- ✅ WebSockets para tiempo real
- ✅ Persistencia con MySQL/H2
- ✅ Redis Pub/Sub para arquitectura distribuida
- ✅ Generación de reportes PDF/Excel
- ✅ Envío de correos electrónicos
- ✅ Métricas con Prometheus
- ✅ Logs de auditoría completos
- ✅ Pipeline CI/CD con GitHub Actions
- ✅ Análisis de seguridad automatizado
- ✅ Contenerización Docker
- ✅ Manifiestos Kubernetes

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/nueva-caracteristica`)
3. Commit cambios (`git commit -am 'Agregar nueva característica'`)
4. Push al branch (`git push origin feature/nueva-caracteristica`)
5. Crear Pull Request

### Guías de Contribución
- Seguir las convenciones de código Java
- Agregar pruebas para nuevas funcionalidades
- Actualizar documentación según sea necesario
- Verificar que todas las pruebas pasen

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---

**Alejandro** - Desarrollador Full Stack
- GitHub: [@Biershoot](https://github.com/Biershoot)
- LinkedIn: [Alejandro](https://linkedin.com/in/tu-perfil)
- Email: alejandro@empresa.com

⭐ **¿Te gustó este proyecto?** ¡Dale una estrella en GitHub!

🐛 **¿Encontraste un bug?** Crea un [issue](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/issues)

💡 **¿Tienes una idea?** ¡Las contribuciones son bienvenidas!
