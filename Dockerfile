# Build Stage
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Aseguramos que mvnw tenga permisos de ejecución y corregimos posibles line endings
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw
# Descargar dependencias
RUN ./mvnw dependency:go-offline -B
# Copiar código fuente
COPY src/ src/
# Compilar proyecto saltando tests y extraer capas con layertools
RUN ./mvnw clean package -DskipTests && \
    java -Djarmode=layertools -jar target/workflow-backend-*.jar extract --destination target/extracted

# Runtime Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear el directorio uploads y asignar permisos
RUN addgroup -S spring && adduser -S spring -G spring && \
    mkdir -p /app/uploads && \
    chown -R spring:spring /app

USER spring:spring

# Copiar las capas de forma individual para aprovechar al máximo la caché de capas de Docker
COPY --chown=spring:spring --from=builder /app/target/extracted/dependencies/ ./
COPY --chown=spring:spring --from=builder /app/target/extracted/spring-boot-loader/ ./
COPY --chown=spring:spring --from=builder /app/target/extracted/snapshot-dependencies/ ./
COPY --chown=spring:spring --from=builder /app/target/extracted/application/ ./

# Variables de Entorno por defecto (A ser sobrescritas por Google Cloud Run)
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Parámetros optimizados de JVM para entornos Serverless de baja memoria (<=2GB RAM)
# Se incluye -XX:+UseSerialGC y -XX:+OptimizeStringConcat
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:InitialRAMPercentage=50.0", \
  "-XX:+UseSerialGC", \
  "-XX:+OptimizeStringConcat", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "org.springframework.boot.loader.launch.JarLauncher"]
