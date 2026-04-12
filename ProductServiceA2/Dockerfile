# ---------- Build stage ----------
FROM eclipse-temurin:21 AS build
WORKDIR /workspace

# Copy only build descriptors first to cache deps
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Make mvnw executable
RUN chmod +x mvnw

# Pre-fetch dependencies (fast rebuilds)
RUN ./mvnw -q -DskipTests dependency:go-offline

# Now copy source (this invalidates cache when code changes)
COPY src ./src

# Build the jar
RUN ./mvnw -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -ms /bin/bash spring
USER spring

# Copy the freshly built jar from the previous stage
#COPY --from=build /workspace/target/*SNAPSHOT.jar /app/app.jar
COPY --from=build /workspace/target/*.jar /app/app.jar

#EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]