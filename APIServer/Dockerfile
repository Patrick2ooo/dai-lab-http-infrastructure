FROM openjdk:21

# Copie le dichier JAR de Javalin dans notre conteneur
COPY target/myAPI-1.0.jar /usr/app/myAPI-1.0.jar

# Règle le direectory de travail
WORKDIR /usr/app

# Commande pour exécuter le code Javalin
CMD ["java", "-jar", "myAPI-1.0.jar"]
