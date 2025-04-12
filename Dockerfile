FROM eclipse-temurin:21

LABEL author=Gustavo.Matias.Alvarez

COPY target/user-management-platform-0.0.1-SNAPSHOT.jar  /app.jar

ENTRYPOINT ["java","-jar","app.jar"]