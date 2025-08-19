# Imagen base de Java 21
FROM eclipse-temurin:21-jdk AS builder

# Carpeta de trabajo
WORKDIR /app

# Copiar el pom.xml y descargar dependencias
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Etapa final: imagen ligera
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar el .jar desde la etapa anterior
COPY --from=builder /app/target/notifications-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
