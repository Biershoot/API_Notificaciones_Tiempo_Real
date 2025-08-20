# 🔔 API de Notificaciones en Tiempo Real - Enterprise Edition

[![CI/CD Pipeline](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/actions/workflows/ci-cd.yml)
[![codecov](https://codecov.io/gh/Biershoot/API_Notificaciones_Tiempo_Real/branch/main/graph/badge.svg)](https://codecov.io/gh/Biershoot/API_Notificaciones_Tiempo_Real)
[![Docker Hub](https://img.shields.io/docker/pulls/biershoot/notifications-api.svg)](https://hub.docker.com/r/biershoot/notifications-api)

**API empresarial completa de notificaciones** con WebSockets, Redis Pub/Sub, reportes automáticos, envío por correo electrónico, métricas avanzadas con Prometheus, monitoreo con Grafana y contenerización Docker para entornos de producción.

## 📋 Índice

- [Características](#-características-enterprise)
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
- ✅ **Limpieza automática** de notificaciones antiguas
- ✅ **Logs de auditoría** para trazabilidad completa

### 📊 Observabilidad y Monitoreo
- ✅ **Métricas Prometheus** integradas
- ✅ **Dashboards Grafana** preconfiguraados
- ✅ **Traces distribuidos** para seguimiento de operaciones
- ✅ **Health checks** avanzados
- ✅ **Alertas configurables** basadas en umbrales

### 🔒 Seguridad Empresarial
- ✅ **Autenticación OAuth2/JWT**
- ✅ **Autorización basada en roles**
- ✅ **Validación de entrada** exhaustiva
- ✅ **Protección contra ataques** CSRF/XSS
- ✅ **Cifrado de datos sensibles**
- ✅ **Análisis de dependencias** automatizado

### 🔄 DevOps y Operaciones
- ✅ **Contenerización Docker** completa
- ✅ **Composición Docker** para desarrollo y producción
- ✅ **Manifiestos Kubernetes** listos para usar
- ✅ **CI/CD automatizado** con GitHub Actions
- ✅ **Pruebas automatizadas** (unitarias, integración, carga)
- ✅ **Migraciones de BD** automáticas con Flyway

## 🛠 Tecnologías Utilizadas

- **Backend**: Java 17, Spring Boot 3.x, Spring WebFlux
- **Persistencia**: MySQL/PostgreSQL, Redis, Spring Data JPA
- **Comunicación**: WebSockets (STOMP), Redis Pub/Sub
- **Documentación**: Swagger/OpenAPI 3.0
- **Monitoreo**: Prometheus, Grafana, Micrometer
- **Seguridad**: Spring Security, OAuth2/JWT
- **Contenedores**: Docker, Docker Compose, Kubernetes
- **CI/CD**: GitHub Actions
- **Testing**: JUnit 5, Mockito, Testcontainers

## 📦 Requisitos

- Java 17+
- Maven 3.8+
- Docker y Docker Compose (opcional, para entorno contenerizado)
- MySQL/PostgreSQL (opcional, se puede usar H2 embebido para desarrollo)
- Redis (opcional, se puede deshabilitar)

## 💻 Instalación

### Método 1: Desde fuente

```bash
# Clonar el repositorio
git clone https://github.com/Biershoot/API_Notificaciones_Tiempo_Real.git
cd API_Notificaciones_Tiempo_Real

# Compilar y empaquetar
mvn clean package -DskipTests

# Ejecutar la aplicación
java -jar target/notifications-*.jar
```

### Método 2: Con Docker Compose

```bash
# Clonar el repositorio
git clone https://github.com/Biershoot/API_Notificaciones_Tiempo_Real.git
cd API_Notificaciones_Tiempo_Real

# Iniciar con Docker Compose
docker-compose up -d
```

### Método 3: Con Kubernetes

```bash
# Desplegar en Kubernetes
kubectl apply -f k8s/
```

## 📚 Documentación API

La documentación de la API está disponible a través de Swagger UI:

- Entorno local: http://localhost:8080/swagger-ui.html
- Documentación completa en la [wiki del proyecto](https://github.com/Biershoot/API_Notificaciones_Tiempo_Real/wiki)

## 📈 Monitoreo y Observabilidad

### Dashboards Grafana
- Sistema completo: http://localhost:3000/d/notifications-dashboard
- Rendimiento API: http://localhost:3000/d/api-performance
- Métricas de Notificaciones: http://localhost:3000/d/notifications-metrics

### Endpoints de Salud y Métricas
- Estado del sistema: http://localhost:8080/actuator/health
- Métricas Prometheus: http://localhost:8080/actuator/prometheus

## 🔄 Roadmap

### v2.5.0 - Próximas mejoras
- ➕ Notificaciones push móviles (FCM/APNS)
- ➕ Integración con sistemas de mensajería
- ➕ Generación de reportes avanzados

### v2.0.0 - Funcionalidades actuales
- ✅ API REST avanzada con filtros y búsqueda
- ✅ WebSockets para tiempo real
- ✅ Persistencia con MySQL/PostgreSQL
- ✅ Redis Pub/Sub para arquitectura distribuida
- ✅ Monitoring con Prometheus y Grafana
- ✅ Logs de auditoría completos
- ✅ Pipeline CI/CD con GitHub Actions
- ✅ Análisis de seguridad automatizado

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/nueva-caracteristica`)
3. Commit cambios (`git commit -am 'Agregar nueva característica'`)
4. Push al branch (`git push origin feature/nueva-caracteristica`)
5. Crear Pull Request

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
