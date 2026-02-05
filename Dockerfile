# =========================
# Build stage
# =========================
FROM eclipse-temurin:11-jdk AS build

WORKDIR /app

COPY pom.xml .
COPY mvnw mvnw.cmd ./
COPY .mvn/ .mvn/

RUN chmod +x mvnw || true
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -q -DskipTests clean package

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:11-jre

WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

COPY --from=build /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
