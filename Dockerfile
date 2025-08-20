# Imagen base de Java 21 con seguridad mejorada
FROM eclipse-temurin:21-jdk AS builder

# Usuario no-root para seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Carpeta de trabajo
WORKDIR /app

# Copiar el pom.xml y descargar dependencias
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Configurar permisos y descargar dependencias
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa final: imagen ligera con seguridad mejorada
FROM eclipse-temurin:21-jre

# Usuario no-root para seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Crear directorio de trabajo y establecer permisos
WORKDIR /app
RUN chown -R appuser:appuser /app

# Copiar el .jar desde la etapa anterior
COPY --from=builder /app/target/notifications-0.0.1-SNAPSHOT.jar app.jar

# Establecer permisos correctos
RUN chown appuser:appuser app.jar && \
    chmod 755 app.jar

# Cambiar al usuario no-root
USER appuser

# Puerto expuesto
EXPOSE 8080

# Health check para monitoreo
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de ejecución con configuración de seguridad
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dfile.encoding=UTF-8", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", "app.jar"]
