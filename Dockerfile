# =========================
# STAGE 1 - BUILD
# =========================
FROM maven:3.9.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copia tudo do projeto
COPY . .

# Gera o build do Quarkus
RUN mvn clean package -DskipTests

# =========================
# STAGE 2 - RUNTIME
# =========================
FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copia apenas o build gerado (mais leve)
COPY --from=build /app/target/quarkus-app/ /app/

# Porta da API
EXPOSE 8081

# Melhor prática para containers Java
ENV JAVA_OPTS="-Xms128m -Xmx512m"

# Start da aplicação
CMD ["java", "-jar", "quarkus-run.jar"]