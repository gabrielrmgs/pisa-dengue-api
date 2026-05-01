# =========================
# BUILD
# =========================
FROM maven:3.9.14-eclipse-temurin-25 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# =========================
# RUNTIME
# =========================
FROM eclipse-temurin:25-jdk

WORKDIR /app
COPY --from=build /app/target/quarkus-app/ /app/

EXPOSE 8081

CMD ["java", "-jar", "quarkus-run.jar"]