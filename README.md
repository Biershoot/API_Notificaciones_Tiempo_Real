# 🔔 API de Notificaciones en Tiempo Real

API completa de notificaciones con WebSockets y Redis Pub/Sub para escalabilidad distribuida.

## 🚀 Características

- ✅ **API REST** completa con Swagger UI
- ✅ **WebSockets** para notificaciones en tiempo real
- ✅ **Redis Pub/Sub** para arquitectura distribuida
- ✅ **Base de datos** MySQL + H2 embebida
- ✅ **Documentación** automática con OpenAPI
- ✅ **Cliente demo** HTML incluido

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

## 📡 Endpoints de la API

### Notificaciones
- `POST /api/notifications/send` - Enviar notificación
- `GET /api/notifications/{username}` - Ver notificaciones
- `GET /api/notifications/{username}/unread` - Ver no leídas
- `PUT /api/notifications/{id}/read` - Marcar como leída

### Usuarios
- `POST /api/users` - Crear usuario
- `GET /api/users` - Listar usuarios
- `GET /api/users/{username}` - Buscar usuario
- `DELETE /api/users/{id}` - Eliminar usuario

## 🌐 WebSockets

### Conexión
```javascript
// Conectar a WebSocket
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// Suscribirse a notificaciones
stompClient.subscribe('/topic/notifications/username', function(message) {
    const notification = JSON.parse(message.body);
    console.log('Nueva notificación:', notification);
});

// Enviar notificación
stompClient.send("/app/send", {}, JSON.stringify({
    username: 'destinatario',
    message: 'Hola mundo!'
}));
```

## 🏗️ Arquitectura Distribuida

El sistema usa Redis Pub/Sub para permitir múltiples instancias:

1. **Instancia A** recibe notificación via REST
2. **Redis** distribuye el mensaje a todas las instancias
3. **Todas las instancias** envían via WebSocket a sus clientes conectados

```
[Cliente] → [Instancia A] → [Redis] → [Instancia A, B, C] → [WebSockets] → [Clientes]
```

## 🧪 Pruebas

### Prueba básica con curl
```bash
# Enviar notificación
curl -X POST "http://localhost:8080/api/notifications/send" \
  -d "username=user1&message=Prueba de notificación"

# Ver notificaciones
curl "http://localhost:8080/api/notifications/user1"
```

### Prueba de escalabilidad
1. Ejecuta múltiples instancias en puertos diferentes
2. Conecta clientes WebSocket a diferentes instancias  
3. Envía notificaciones desde cualquier instancia
4. Verifica que todos los clientes reciben las notificaciones

## 📝 Configuración

### application.properties (Producción)
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/notifications_db
spring.datasource.username=root
spring.datasource.password=tu_password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### application-h2.properties (Desarrollo)
```properties
# Base de datos embebida
spring.datasource.url=jdbc:h2:mem:notificationsdb
spring.datasource.username=sa

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## 🐛 Troubleshooting

### Error de conexión a Redis
```bash
# Verificar si Redis está ejecutándose
redis-cli ping
# Debe responder: PONG

# Si no está instalado, usar Docker:
docker run -d -p 6379:6379 redis:latest
```

### Error de conexión a MySQL
```bash
# Usar el perfil H2 para desarrollo:
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

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
