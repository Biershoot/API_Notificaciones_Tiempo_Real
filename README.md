# 🔔 API de Notificaciones en Tiempo Real - Enterprise Edition

[![CI/CD Pipeline](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/actions/workflows/ci-cd.yml)
[![codecov](https://codecov.io/gh/Biershoot/API_Notificaciones_Tiempo_Real/branch/main/graph/badge.svg)](https://codecov.io/gh/Biershoot/API_Notificaciones_Tiempo_Real)
[![Docker Hub](https://img.shields.io/docker/pulls/biershoot/notifications-api.svg)](https://hub.docker.com/r/biershoot/notifications-api)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

## 📋 Resumen Ejecutivo

**API de Notificaciones en Tiempo Real** es una **API REST empresarial** desarrollada con **Java 21** y **Spring Boot 3.2.5** que resuelve el problema crítico de **comunicación en tiempo real** entre aplicaciones y usuarios. Esta API proporciona un **sistema centralizado de notificaciones** que permite a cualquier aplicación enviar notificaciones instantáneas por múltiples canales (APP, EMAIL, SMS) con escalabilidad, seguridad y observabilidad empresarial.

## 🎯 **Problemática que Resuelve**

### **🚨 Desafíos Empresariales Actuales**

#### **1. Fragmentación de Canales de Notificación**
- **Problema**: Las aplicaciones necesitan enviar notificaciones por múltiples canales (app, email, SMS) pero cada uno requiere integraciones separadas y APIs diferentes
- **Solución**: **API unificada** que maneja **APP, EMAIL, SMS** y **múltiples canales simultáneamente** con una sola integración

#### **2. Falta de Tiempo Real**
- **Problema**: Los usuarios esperan notificaciones instantáneas, pero las APIs tradicionales tienen latencia alta y requieren polling
- **Solución**: **API con WebSockets** para notificaciones en tiempo real con latencia <100ms y push automático

#### **3. Escalabilidad Limitada**
- **Problema**: Las APIs de notificación tradicionales no escalan bien con el crecimiento de usuarios y aplicaciones
- **Solución**: **API con arquitectura distribuida** con Redis Pub/Sub que soporta miles de conexiones concurrentes y múltiples instancias

#### **4. Falta de Observabilidad**
- **Problema**: Es difícil monitorear el rendimiento y detectar problemas en APIs de notificación
- **Solución**: **API con métricas completas** con Prometheus, Grafana y logs de auditoría detallados para monitoreo en tiempo real

#### **5. Seguridad Insuficiente**
- **Problema**: Las APIs de notificación son vulnerables a ataques y no tienen control de acceso granular
- **Solución**: **API con autenticación robusta** con Spring Security, validaciones exhaustivas y análisis OWASP integrado

#### **6. Integración Compleja**
- **Problema**: Integrar servicios externos (Twilio, SendGrid) en aplicaciones requiere mucho código boilerplate y manejo de errores
- **Solución**: **API con integraciones nativas** que abstrae la complejidad con validaciones inteligentes y manejo de errores automático

#### **7. Falta de Reportes y Analytics**
- **Problema**: No hay visibilidad sobre el rendimiento de las notificaciones y engagement de usuarios en las aplicaciones
- **Solución**: **API con reportes automáticos** PDF/Excel y métricas detalladas de engagement para análisis de negocio

#### **8. Operaciones Manuales**
- **Problema**: Limpieza de datos antiguos, generación de reportes y monitoreo de APIs requieren intervención manual
- **Solución**: **API con automatización completa** con tareas programadas, CI/CD y contenerización para operaciones sin intervención

### **💼 Casos de Uso Empresariales**

#### **🏢 E-commerce**
- **Notificaciones de pedidos** en tiempo real
- **Alertas de stock** y promociones
- **Confirmaciones de pago** por múltiples canales
- **Seguimiento de envíos** con actualizaciones automáticas

#### **🏥 Healthcare**
- **Recordatorios de citas** médicas
- **Alertas de resultados** de laboratorio
- **Notificaciones de emergencia** con prioridad alta
- **Comunicaciones de seguimiento** post-tratamiento

#### **🏦 Fintech**
- **Alertas de transacciones** sospechosas
- **Confirmaciones de pagos** y transferencias
- **Notificaciones de seguridad** (cambios de contraseña)
- **Reportes financieros** automáticos

#### **🎓 Educación**
- **Notificaciones de calificaciones** y tareas
- **Recordatorios de clases** y eventos
- **Comunicaciones de emergencia** institucionales
- **Reportes de asistencia** y progreso

#### **🏭 Manufacturing**
- **Alertas de mantenimiento** preventivo
- **Notificaciones de calidad** y defectos
- **Reportes de producción** automáticos
- **Comunicaciones de seguridad** industrial

### **🎯 Valor Empresarial de la API**
- **API Unificada**: Una sola integración para múltiples canales de notificación
- **Escalabilidad**: Arquitectura distribuida con Redis Pub/Sub para alta disponibilidad
- **Tiempo Real**: WebSockets para notificaciones instantáneas sin polling
- **Observabilidad**: Métricas completas con Prometheus y Grafana para monitoreo proactivo
- **Seguridad**: Autenticación y autorización robustas con análisis OWASP integrado
- **DevOps**: CI/CD automatizado y contenerización completa para despliegues rápidos

### **📊 Métricas de Calidad**
- **Cobertura de Código**: >90% con JaCoCo
- **Análisis de Seguridad**: OWASP Dependency Check integrado
- **Análisis Estático**: SpotBugs para detección de bugs
- **Pruebas Automatizadas**: Unitarias e integración
- **Documentación**: Swagger/OpenAPI completa

## 🚀 Características Enterprise de la API

### 📡 API de Notificaciones Completa
- ✅ **API REST** completa con Swagger UI y validación Bean Validation
- ✅ **WebSockets** para notificaciones en tiempo real con STOMP
- ✅ **Redis Pub/Sub** para arquitectura distribuida y escalable
- ✅ **Persistencia avanzada** con JPA/Hibernate y migraciones Flyway
- ✅ **Filtros inteligentes** por usuario, estado, tipo y prioridad
- ✅ **Búsqueda de texto** optimizada con índices de base de datos
- ✅ **Limpieza automática** programada con Spring Scheduler
- ✅ **Logs de auditoría** para trazabilidad completa
- ✅ **Marcado masivo** como leídas con transacciones optimizadas
- ✅ **Contadores en tiempo real** con métricas Micrometer

### 📊 Reportes y Análisis
- ✅ **Generación automática** de reportes PDF (iText7) y Excel (Apache POI)
- ✅ **Reportes programados** con cron jobs y Spring Scheduler
- ✅ **Envío por correo electrónico** con Spring Mail
- ✅ **Métricas de rendimiento** detalladas con Micrometer
- ✅ **Estadísticas por usuario** y período con agregaciones SQL

### 🔒 Seguridad Empresarial
- ✅ **Autenticación** con Spring Security y JWT
- ✅ **Autorización basada en roles** (ADMIN, USER) con RBAC
- ✅ **Validación de entrada** exhaustiva con Bean Validation
- ✅ **Protección CORS** configurada para entornos de producción
- ✅ **Manejo de excepciones** centralizado con @ControllerAdvice
- ✅ **Análisis de dependencias** automatizado con OWASP Dependency Check

### 🔄 DevOps y Operaciones
- ✅ **Contenerización Docker** completa con multi-stage builds
- ✅ **Composición Docker** para desarrollo y producción
- ✅ **Manifiestos Kubernetes** listos para usar con health checks
- ✅ **CI/CD automatizado** con GitHub Actions y Maven
- ✅ **Pruebas automatizadas** (unitarias, integración) con JUnit 5
- ✅ **Migraciones de BD** automáticas con Flyway
- ✅ **Análisis estático** con SpotBugs y configuración personalizada

### 📱 API de Notificaciones Externas
- ✅ **Integración Twilio** para envío de SMS
- ✅ **Integración SendGrid** para envío de emails
- ✅ **Despacho multi-canal** (APP, EMAIL, SMS, ALL)
- ✅ **Validaciones inteligentes** según el canal de envío
- ✅ **Envío masivo** con procesamiento asíncrono

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

### 📱 Notificaciones Externas
- `POST /api/external/notifications/send` - Enviar notificación externa (SMS/Email)
- `POST /api/external/notifications/send/bulk` - Envío masivo de notificaciones
- `GET /api/external/notifications/channels/status` - Estado de canales
- `POST /api/external/notifications/test/sms` - Probar envío SMS
- `POST /api/external/notifications/test/email` - Probar envío Email

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

## 🛠 Stack Tecnológico

### **Backend & Framework**
- **Java 21** - Lenguaje de programación con características modernas (Records, Pattern Matching, Virtual Threads)
- **Spring Boot 3.2.5** - Framework principal con configuración automática
- **Spring Data JPA** - Persistencia de datos con Hibernate
- **Spring Security** - Seguridad y autenticación robusta
- **Spring WebSocket** - Comunicación en tiempo real con STOMP
- **Spring Data Redis** - Cache y mensajería Pub/Sub
- **Spring Mail** - Envío de correos electrónicos
- **Spring Scheduler** - Tareas programadas y cron jobs

### **Base de Datos & Persistencia**
- **MySQL 8.0** - Base de datos principal para producción
- **H2 Database** - Base de datos embebida para desarrollo
- **Redis** - Cache distribuido y mensajería Pub/Sub
- **Flyway** - Migraciones de base de datos versionadas

### **Documentación & Testing**
- **Swagger/OpenAPI 3.0** - Documentación interactiva de API
- **JUnit 5** - Framework de testing moderno
- **JaCoCo** - Cobertura de código y reportes
- **SpotBugs** - Análisis estático de código
- **TestContainers** - Testing con contenedores reales

### **Monitoreo & Observabilidad**
- **Spring Boot Actuator** - Endpoints de monitoreo y health checks
- **Micrometer** - Métricas de aplicación estandarizadas
- **Prometheus** - Recolección y almacenamiento de métricas
- **Grafana** - Visualización de métricas y dashboards

### **Reportes & Generación**
- **iText7** - Generación avanzada de PDFs
- **Apache POI** - Generación de archivos Excel
- **Thymeleaf** - Plantillas para emails HTML

### **DevOps & Contenerización**
- **Docker** - Contenerización con multi-stage builds
- **Docker Compose** - Orquestación de servicios
- **Kubernetes** - Despliegue en cluster
- **GitHub Actions** - CI/CD automatizado
- **Maven** - Gestión de dependencias y build

### **Integraciones Externas**
- **Twilio SDK** - Envío de SMS
- **SendGrid API** - Envío de emails transaccionales
- **Lombok** - Reducción de código boilerplate

## 📦 Requisitos del Sistema

### **Mínimos**
- Java 21+ (OpenJDK o Oracle JDK)
- Maven 3.8+
- Redis Server 6.0+ (opcional para desarrollo)

### **Recomendados para Producción**
- MySQL 8.0+ (base de datos principal)
- Docker y Docker Compose
- Kubernetes 1.24+
- Prometheus y Grafana

## 💻 Instalación y Configuración

### **Método 1: Desarrollo Local**

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

### **Método 2: Con Docker**

```bash
# Construir imagen Docker
docker build -t notifications-api .

# Ejecutar contenedor
docker run -p 8080:8080 notifications-api
```

### **Método 3: Con Docker Compose**

```bash
# Iniciar todos los servicios (API + Redis + MySQL)
docker-compose up -d

# Ver logs en tiempo real
docker-compose logs -f
```

## 🎯 Guía de Uso

### **1. Acceso a la Documentación**
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console (solo desarrollo)

### **2. Usuarios de Prueba**
| Username | Password | Rol   | Descripción |
|----------|----------|-------|-------------|
| admin    | password | ADMIN | Usuario administrador con todos los permisos |
| user1    | password | USER  | Usuario estándar para pruebas |
| user2    | password | USER  | Usuario adicional para pruebas |

### **3. Ejemplos de Uso**

#### **Enviar Notificación Básica**
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

#### **Enviar Notificación por SMS**
```bash
curl -X POST "http://localhost:8080/api/external/notifications/send" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "message": "Tu pedido ha sido confirmado",
    "title": "Confirmación de Pedido",
    "channel": "SMS",
    "phone": "+1234567890"
  }'
```

#### **Enviar Notificación por Email**
```bash
curl -X POST "http://localhost:8080/api/external/notifications/send" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "message": "Tu pedido ha sido confirmado",
    "title": "Confirmación de Pedido",
    "channel": "EMAIL",
    "email": "user1@example.com"
  }'
```

#### **Obtener Notificaciones No Leídas**
```bash
curl "http://localhost:8080/api/notifications/user1/unread"
```

#### **Contar Notificaciones No Leídas**
```bash
curl "http://localhost:8080/api/notifications/user1/unread/count"
```

#### **Marcar Como Leída**
```bash
curl -X PUT "http://localhost:8080/api/notifications/1/read"
```

### **4. WebSockets - Notificaciones en Tiempo Real**

#### **Conectar con JavaScript**
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

#### **Conectar con React**
```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const connectWebSocket = () => {
    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = Stomp.over(socket);
    
    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);
        
        stompClient.subscribe('/topic/notifications/user1', (notification) => {
            const notificationData = JSON.parse(notification.body);
            // Manejar la notificación en tu componente React
        });
    });
};
```

## 📚 Documentación API

La documentación completa de la API está disponible en:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console (solo desarrollo)

## 📈 Monitoreo y Observabilidad

### **Endpoints de Actuator**
- **Health Check**: http://localhost:8080/actuator/health
- **Métricas**: http://localhost:8080/actuator/metrics
- **Info**: http://localhost:8080/actuator/info
- **Prometheus**: http://localhost:8080/actuator/prometheus

### **Métricas Disponibles**
- `notifications.sent` - Notificaciones enviadas
- `notifications.read` - Notificaciones leídas
- `notifications.unread` - Notificaciones no leídas
- `websocket.connections` - Conexiones WebSocket activas
- `redis.publish.events` - Eventos publicados en Redis
- `system.cpu.usage` - Uso de CPU del sistema
- `system.memory.usage` - Uso de memoria del sistema

## 🧪 Testing y Calidad

### **Ejecutar Todas las Pruebas**
```bash
./mvnw test
```

### **Ejecutar con Cobertura**
```bash
./mvnw clean test jacoco:report
```

### **Análisis de Seguridad**
```bash
./mvnw dependency-check:check
```

### **Análisis Estático**
```bash
./mvnw spotbugs:check
```

### **Pruebas de Integración**
```bash
./mvnw verify
```

## 🚀 Despliegue en Producción

### **Docker**
```bash
# Construir imagen optimizada
docker build -t notifications-api .

# Ejecutar con variables de entorno
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/notifications \
  notifications-api
```

### **Kubernetes**
```bash
# Aplicar manifiestos
kubectl apply -f k8s/

# Verificar estado
kubectl get pods -l app=notifications-api

# Ver logs
kubectl logs -f deployment/notifications-api
```

### **Variables de Entorno de Producción**
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

# Twilio
TWILIO_ACCOUNT_SID=tu_account_sid
TWILIO_AUTH_TOKEN=tu_auth_token
TWILIO_PHONE_NUMBER=+123456789

# SendGrid
SENDGRID_API_KEY=tu_api_key
SENDGRID_FROM_EMAIL=tu_correo@empresa.com
```

## 🏗️ Arquitectura de la API

### **Diagrama de Componentes**
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
                    │   (Spring Boot 3.2.5)     │
                    └─────────────┬─────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
┌─────────▼─────────┐  ┌─────────▼─────────┐  ┌─────────▼─────────┐
│   MySQL Database  │  │   Redis Cache     │  │   WebSocket       │
│   (Flyway Mig.)   │  │   & Pub/Sub       │  │   Broker          │
└───────────────────┘  └───────────────────┘  └───────────────────┘
```

### **Flujo de la API de Notificaciones**
1. **Recepción**: API recibe solicitud de notificación desde cualquier aplicación
2. **Validación**: Bean Validation valida los datos de entrada según el canal
3. **Persistencia**: Se guarda en base de datos con JPA para trazabilidad
4. **Publicación**: Se publica en Redis Pub/Sub para distribución
5. **Distribución**: WebSocket envía a todos los clientes conectados en tiempo real
6. **Logging**: Se registra en logs de auditoría para compliance
7. **Métricas**: Se actualizan métricas con Micrometer para monitoreo

### **Patrones de Diseño Implementados**
- **Repository Pattern**: Abstracción de acceso a datos
- **Service Layer**: Lógica de negocio centralizada
- **DTO Pattern**: Transferencia de datos optimizada
- **Observer Pattern**: Notificaciones en tiempo real
- **Factory Pattern**: Creación de reportes
- **Strategy Pattern**: Múltiples canales de envío

## 🔄 Roadmap y Evolución

### **v3.0.0 - Próximas Funcionalidades**
- ➕ **Notificaciones Push Móviles** (FCM/APNS)
- ➕ **Integración con Sistemas de Mensajería** (Slack, Teams, Discord)
- ➕ **Generación de Reportes Avanzados** con gráficos interactivos
- ➕ **Dashboard Administrativo Web** con React/Angular
- ➕ **Machine Learning** para personalización de notificaciones
- ➕ **API Rate Limiting** y throttling avanzado

### **v2.5.0 - Mejoras Planificadas**
- ➕ **Webhooks** para integración con sistemas externos
- ➕ **Templates de Notificaciones** personalizables
- ➕ **A/B Testing** para optimización de engagement
- ➕ **Analytics Avanzados** con Elasticsearch

### **v2.0.0 - Funcionalidades Actuales ✅**
- ✅ **API REST Avanzada** con filtros, búsqueda y paginación
- ✅ **WebSockets** para notificaciones en tiempo real
- ✅ **Persistencia Robusta** con MySQL/H2 y migraciones
- ✅ **Redis Pub/Sub** para arquitectura distribuida
- ✅ **Generación de Reportes** PDF y Excel automática
- ✅ **Envío de Correos** electrónicos transaccionales
- ✅ **Métricas Completas** con Prometheus y Grafana
- ✅ **Logs de Auditoría** para trazabilidad completa
- ✅ **Pipeline CI/CD** automatizado con GitHub Actions
- ✅ **Análisis de Seguridad** automatizado con OWASP
- ✅ **Contenerización Docker** completa
- ✅ **Manifiestos Kubernetes** para orquestación
- ✅ **Integración Twilio** para SMS
- ✅ **Integración SendGrid** para emails
- ✅ **Validaciones Inteligentes** por canal

## 🤝 Contribución

### **Cómo Contribuir**
1. **Fork** el proyecto
2. **Crear** feature branch (`git checkout -b feature/nueva-caracteristica`)
3. **Commit** cambios (`git commit -am 'Agregar nueva característica'`)
4. **Push** al branch (`git push origin feature/nueva-caracteristica`)
5. **Crear** Pull Request

### **Guías de Contribución**
- Seguir las **convenciones de código Java** y Spring Boot
- Agregar **pruebas unitarias** para nuevas funcionalidades
- Mantener **cobertura de código** >90%
- Actualizar **documentación** según sea necesario
- Verificar que **todas las pruebas pasen**
- Seguir **principios SOLID** y **Clean Code**

### **Estándares de Código**
- **Java 21** con características modernas
- **Spring Boot 3.2.5** con configuración automática
- **Lombok** para reducir boilerplate
- **Validación Bean Validation** para inputs
- **Logging estructurado** con SLF4J
- **Manejo de excepciones** centralizado



---

## 👨‍💻 **Desarrollador**

**Alejandro** - Desarrollador Full Stack Senior
- 🐙 **GitHub**: [@Biershoot](https://github.com/Biershoot)
- 💼 **LinkedIn**: [Alejandro](https://linkedin.com/in/tu-perfil)
- 📧 **Email**: alejandro@empresa.com
- 🌐 **Portfolio**: [tu-portfolio.com](https://tu-portfolio.com)

### **Habilidades Técnicas**
- **Backend**: Java, Spring Boot, JPA/Hibernate, Redis
- **Frontend**: React, Angular, JavaScript, TypeScript
- **DevOps**: Docker, Kubernetes, CI/CD, AWS
- **Base de Datos**: MySQL, PostgreSQL, MongoDB
- **Testing**: JUnit, Mockito, TestContainers
- **Monitoreo**: Prometheus, Grafana, ELK Stack

---

⭐ **¿Te gustó este proyecto?** ¡Dale una estrella en GitHub!

🐛 **¿Encontraste un bug?** Crea un [issue](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/issues)

💡 **¿Tienes una idea?** ¡Las contribuciones son bienvenidas!

🚀 **¿Quieres colaborar?** Revisa las [guías de contribución](#-contribución)
