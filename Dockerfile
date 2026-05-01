FROM eclipse-temurin:25-jdk

# Diretório da aplicação
WORKDIR /app

# Copia o build do Quarkus
COPY target/quarkus-app/ /app/

# Expõe a porta da API
EXPOSE 8081

# Comando de inicialização
CMD ["java", "-jar", "quarkus-run.jar"]