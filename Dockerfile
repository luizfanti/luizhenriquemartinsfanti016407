# =========================
# Build stage
# =========================
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

# Copia arquivos necessários para cache de dependências
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd ./

RUN chmod +x mvnw || true
RUN ./mvnw -q -DskipTests dependency:go-offline

# Copia o código-fonte
COPY src/ src/

# Build do JAR
RUN ./mvnw -q -DskipTests clean package

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Usuário não-root (boa prática de produção)
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]