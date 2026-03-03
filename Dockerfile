# ──────────────────────────────────────────────
# Stage 1: Build the application with Maven
# ──────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# + Copy only dependency files first (cache layer for faster rebuilds)
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# + Copy source code and build the JAR (skip tests for Docker build)
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ──────────────────────────────────────────────
# Stage 2: Run with a minimal JRE image
# ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# + Create a non-root user for security
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

COPY --from=build /app/target/*.jar app.jar

# + Set ownership and switch to non-root user
RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

# + Use the Docker Spring profile by default
ENV SPRING_PROFILES_ACTIVE=docker

ENTRYPOINT ["java", "-jar", "app.jar"]