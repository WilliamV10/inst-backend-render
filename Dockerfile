# =============================================================================
# Dockerfile Multi-Stage — SIE Backend
# -----------------------------------------------------------------------------
# Stage 1 · deps      → Caching de dependencias Maven (cache-friendly)
# Stage 2 · builder   → Compilación y empaquetado del JAR
# Stage 3 · runtime   → Imagen final mínima (JRE) con usuario no-root
#
# Compatible con: Render · AWS ECS · AWS EKS · Docker local
# =============================================================================

# ─── ARGs globales (disponibles en todos los stages) ─────────────────────────
ARG JAVA_VERSION=17
ARG MAVEN_VERSION=3.9.9

# =============================================================================
# STAGE 1 · deps — Descarga de dependencias (capa de cache independiente)
# =============================================================================
FROM maven:${MAVEN_VERSION}-eclipse-temurin-${JAVA_VERSION}-alpine AS deps

WORKDIR /build

# Copiar SÓLO los archivos de definición del proyecto.
# Si el código fuente cambia pero pom.xml no, esta capa se reutiliza desde cache.
COPY pom.xml .

# Descargar todas las dependencias sin compilar código fuente.
# -B  → batch mode  (sin colores, ideal para CI/CD logs)
# -q  → quiet       (menos ruido en el log de build)
RUN mvn dependency:go-offline -B -q

# =============================================================================
# STAGE 2 · builder — Compilación y empaquetado del JAR
# =============================================================================
FROM deps AS builder

# Copiar el código fuente (solo esta capa se invalida cuando cambia el código)
COPY src/ src/

# Compilar y empaquetar, omitiendo tests.
# Los tests se ejecutan en el pipeline de CI, no durante el build de la imagen.
RUN mvn package -B -q -DskipTests

# Extraer capas del JAR con Spring Boot Layertools.
# Permite que Docker cachée separadamente: libs / snapshot-deps / app code.
RUN java -Djarmode=tools \
         -jar target/inst-backend-*.jar \
         extract --layers --launcher --destination target/extracted

# =============================================================================
# STAGE 3 · runtime — Imagen final mínima y segura
# =============================================================================
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS runtime

# ---------------------------------------------------------------------------
# Metadatos OCI (buena práctica para trazabilidad en registros y auditorías)
# ---------------------------------------------------------------------------
LABEL maintainer="ONEC-BCR <info@bcr.gob.sv>"
LABEL org.opencontainers.image.title="SIE Backend"
LABEL org.opencontainers.image.description="Sistema Integrado de Encuestas — BCR"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.vendor="Banco Central de Reserva de El Salvador"
LABEL org.opencontainers.image.source="https://github.com/bcr-onec/sie-backend"

# ---------------------------------------------------------------------------
# Usuario no-root (security best practice)
# GID/UID fijos para consistencia entre Render, AWS ECS/EKS y Kubernetes.
# Usar ARGs permite sobreescribirlos en tiempo de build si el orquestador lo exige.
# ---------------------------------------------------------------------------
ARG APP_USER=appuser
ARG APP_UID=1001
ARG APP_GID=1001

RUN addgroup -g ${APP_GID} -S ${APP_USER} && \
    adduser  -u ${APP_UID} -S ${APP_USER} -G ${APP_USER}

# ---------------------------------------------------------------------------
# Directorio de trabajo y directorio de logs persistentes
# ---------------------------------------------------------------------------
WORKDIR /app

RUN mkdir -p /app/logs && \
    chown -R ${APP_USER}:${APP_USER} /app

# ---------------------------------------------------------------------------
# Copiar capas extraídas respetando el orden de volatilidad (menor → mayor).
# Docker reutilizará del cache las capas que no cambien entre builds.
#
#   dependencies/          → librerías de release   (cambian raramente)
#   spring-boot-loader/    → loader de Spring Boot  (cambia sólo con Boot bump)
#   snapshot-dependencies/ → librerías SNAPSHOT      (cambian ocasionalmente)
#   application/           → código propio           (cambia con cada commit)
# ---------------------------------------------------------------------------
COPY --from=builder --chown=${APP_USER}:${APP_USER} \
     /build/target/extracted/dependencies/           ./
COPY --from=builder --chown=${APP_USER}:${APP_USER} \
     /build/target/extracted/spring-boot-loader/     ./
COPY --from=builder --chown=${APP_USER}:${APP_USER} \
     /build/target/extracted/snapshot-dependencies/  ./
COPY --from=builder --chown=${APP_USER}:${APP_USER} \
     /build/target/extracted/application/            ./

# ---------------------------------------------------------------------------
# Cambiar a usuario no-root antes de cualquier EXPOSE o CMD
# ---------------------------------------------------------------------------
USER ${APP_USER}

# ---------------------------------------------------------------------------
# Puerto de la aplicación.
# Render lo detecta automáticamente; AWS lo usa en Security Groups y ALB.
# El valor real llega mediante la variable de entorno PORT en runtime.
# ---------------------------------------------------------------------------
EXPOSE 8080

# ---------------------------------------------------------------------------
# Health check integrado en la imagen.
# Útil para: Render · AWS ECS (enable health check) · Kubernetes liveness probe
# ---------------------------------------------------------------------------
HEALTHCHECK --interval=30s \
            --timeout=10s  \
            --start-period=60s \
            --retries=3 \
    CMD wget -qO- http://localhost:${PORT:-8080}/actuator/health || exit 1

# ---------------------------------------------------------------------------
# Opciones de la JVM optimizadas para contenedores
#
#  UseContainerSupport    → Respeta los límites de CPU/RAM del contenedor
#  MaxRAMPercentage=75.0  → Usa hasta el 75 % de la RAM asignada al pod/task
#  +OptimizeStringConcat  → Pequeña optimización de strings en runtime
#  java.security.egd      → Acelera el arranque de Tomcat (evita bloqueo en /dev/random)
#  file.encoding=UTF-8    → Encoding explícito (Alpine usa UTF-8 por defecto, pero es buena práctica)
# ---------------------------------------------------------------------------
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+OptimizeStringConcat \
               -Djava.security.egd=file:/dev/./urandom \
               -Dfile.encoding=UTF-8"

# ---------------------------------------------------------------------------
# Perfil de Spring activo por defecto: prod.
# Se puede sobreescribir en Render (env vars) o AWS ECS (task definition).
# ---------------------------------------------------------------------------
ENV SPRING_PROFILES_ACTIVE=prod

# ---------------------------------------------------------------------------
# Entrypoint — JarLauncher de Spring Boot Layered Jars.
# Más eficiente que "java -jar" porque no extrae el JAR en cada inicio.
# La clase exacta depende de la versión de Spring Boot:
#   ≥ 3.2  → org.springframework.boot.loader.launch.JarLauncher
#   < 3.2  → org.springframework.boot.loader.JarLauncher
# ---------------------------------------------------------------------------
ENTRYPOINT ["sh", "-c", \
  "exec java $JAVA_OPTS \
   -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
   org.springframework.boot.loader.launch.JarLauncher"]
