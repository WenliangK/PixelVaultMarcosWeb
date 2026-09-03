# Etapa de construcción usando Eclipse Temurin
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución usando Eclipse Temurin (versión JRE más ligera)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copia el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]