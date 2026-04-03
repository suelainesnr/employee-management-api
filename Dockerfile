FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/CadastroFuncionario-0.0.1-SNAPSHOT.jar /app/CadastroFuncionario.jar
EXPOSE 8082
CMD ["java", "-jar", "CadastroFuncionario.jar"]