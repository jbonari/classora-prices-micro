# syntax=docker/dockerfile:1

# --- Build stage: compile and package the fat JAR using the Maven Wrapper ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy the Maven Wrapper and POM first so dependency resolution can be cached
# independently from source changes.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Copy sources and build the application (tests run in the CI pipeline).
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# --- Runtime stage: lightweight JRE image running only the JAR ---
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
