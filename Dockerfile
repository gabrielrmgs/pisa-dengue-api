FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

RUN apt-get update \
    && apt-get install -y --no-install-recommends unzip \
    && rm -rf /var/lib/apt/lists/*

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY src/ src/

RUN ./mvnw package -DskipTests


FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /workspace/target/quarkus-app/ /app/

EXPOSE 8081

CMD ["java", "-jar", "quarkus-run.jar"]
