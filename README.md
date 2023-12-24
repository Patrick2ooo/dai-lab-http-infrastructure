# HTTP Infrastructure

## Etape 1: Serveur Web Statique
Pour ce laboratoire nous allons utilisé un serveur http static nginx. Il nous faut tous d'abord choisir un template de site, dans notre cas nous avons pris [celui-ci](https://startbootstrap.com/theme/agency). 

En premier lieu il faut configurer le fichier Dockerfile qui nous servira à créer un image nginx de notre site web, après plusieurs recherche sur internet voici le résultat auxquel je suis arrivé:

```bash
# Use the Nginx image from Docker Hub
FROM nginx:alpine

# Copy the static site content into the Nginx web directory
COPY startbootstrap-agency-gh-pages /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start Nginx when the container launches
CMD ["nginx", "-g", "daemon off;"]
```
Une fois le Dockerfile, il ne faut pas oublier de configurer un nginx.conf qui est aussi ajouter au DockerFile, se fichier permet de configurer les interaction des clients aux site, gérer la performance du site et autres, sa configuration à été faites à l'aide de plusieurs recherche sur internet: 

```bash
user  nginx;
worker_processes  auto;

error_log  /var/log/nginx/error.log notice;
pid        /var/run/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    include /etc/nginx/conf.d/*.conf;
}
```
Une fois les fichier nginx.conf et DockerFile configurer, on peut tester si notre site web fonctionne, pour se faire metter vous dans le répertoire où se situe nos 2 fichiers à l'aide de la commande suivante :

```bash
cd path/to/StaticWebServer
```

Puis faite un build et un run docker du site à l'aide des 2 commande suivante :
```bash
docker build -t static-web-server .
docker run -p 80:80 static-web-server
```

Une fois cela fais vous pouvez vous connectez à votre site en allant au lien http://localhost:80

## Etape 2: Docker compose
Pour cet 2ème partie il nous faut mettre en place un fichier `docker-compose.yml` à la racine de notre projet, Voici à quoi ressemble notre `docker-compose.yml` pour le moment :
```bash
# Version du docker compose
version: '3'

# Défini le container que l'on veut run
services:
  # nom du serice
  static-web-server:
    # Emplacement du Dockerfile
    build: ./StaticWebServer
    # port auxquel on veux accéder notre site au travers de http://localhost
    ports:
      - "80:80"
    # Met le contenu statique du site dans le conteneur
    volumes:
      - ./StaticWebServer/startbootstrap-agency-gh-pages:/usr/share/nginx/html
```
Une fois se fichier configurer il nous suffit juste de tester à l'aide de la commande suivante (Attention à supprimé le container créer à l'étape 1 avant) :
```bash
docker compose up
```
Vous pouvez ensuite faire de même qu'a la fin de l'étape une est entrer dans la barre de recherche de votre navigateur http://localhost:80, votre site devrait apparaitre.
si vous souhaitez stoppez le container utilisez la commande suivante:
```bash
docker compose down
```
Pour rebuild le toutes si vous avez modifié votre Dockerfile ou votre nginx.conf entrez la commande suivante:
```bash
docker compose build
```

## Etape 3 Serveur API HTTP
L'étape 3 consiste à mettre en place un serveur API en parrallèele du serveur statique en utilisant Javalin, pour see faire il nous faut un code en java implémentant le fonctionnaement de notre API, un DockerFile permettant d'exécuter le code avec le fichier docker-compose.yml et un fichier pom.xml permettant de compiler le code à l'aide de maven.
Voici, le contenu du DockerFile:
```bash
FROM openjdk:21

# Copie le dichier JAR de Javalin dans notre conteneur
COPY target/myAPI-1.0.jar /usr/app/myAPI-1.0.jar

# Règle le direectory de travail
WORKDIR /usr/app

# Commande pour exécuter le code Javalin
CMD ["java", "-jar", "myAPI-1.0.jar"]
```
POM.xml :
```bash
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>myAPI</groupId>
    <artifactId>myAPI</artifactId>
    <version>1.0</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>myAPI.myAPI</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>

			<plugin>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>3.1.2</version>
			</plugin>
			
			<plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <createDependencyReducedPom>false</createDependencyReducedPom>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>myAPI.myAPI</mainClass>  <!-- Replace with your main class -->
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        </plugins>
    </build>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.junit</groupId>
				<artifactId>junit-bom</artifactId>
				<version>5.10.0</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<dependencies>
		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
    			<groupId>io.javalin</groupId>
    			<artifactId>javalin</artifactId>
    			<version>5.6.3</version>
		</dependency>
		<dependency>
    			<groupId>org.slf4j</groupId>
    			<artifactId>slf4j-simple</artifactId>
    			<version>2.0.7</version>
		</dependency>
		<dependency>
    			<groupId>com.fasterxml.jackson.core</groupId>
    			<artifactId>jackson-databind</artifactId>
    			<version>2.15.0</version>
		</dependency>
	</dependencies>


</project>
```
pour que le toute fonctionne ensemble avec le serveur static et que l'on puisse tout simplemeent utiliser la commande `docker compose up` pour exécuter le programme , il faut rajouter les lignes suivantes au fichier docker-compose.yml (port modifiable selon vos besoin) :
```bash
  javalin-app:
    build:
      context: ./APIServer
      dockerfile: Dockerfile
    ports:
      - "7000:7000"
```
Notre serveur API à une fonction très simple qui nous sert tout simplement à faire une liste de Todo en utilisant une ArrayList, pour voir que le toute fonctionne bien une liste de base à été défini `["Buy groceries","Read a book","Complete lab assignment"]`, la liste se retrouve au lien suivant : `http://localhost:7000/todos` .
Pour tester le bon fonctionnement de l'API j'ai utilisé Insomnia, Pour ajouter (post) un nouveau todo il faut exécuter le lien suivant: `http://localhost:7000/todos/add/"Votre texte"`
et pour supprimer (delete) un todo il faut utiliser le lien suivant : `http://localhost:7000/todos/delete/"index"`
Voici un exemple sur insomnia: 
Get de base:
![image](https://github.com/Patrick2ooo/dai-lab-http-infrastructure/assets/44113916/4b8671bc-dde9-4058-8214-01783117a6f9)

Post:
![image](https://github.com/Patrick2ooo/dai-lab-http-infrastructure/assets/44113916/b122c539-3e33-41bb-8391-4f414e75f6d6)

Delete:
![image](https://github.com/Patrick2ooo/dai-lab-http-infrastructure/assets/44113916/d2892265-d13a-44ed-b2d8-14c3049a27ca)
![image](https://github.com/Patrick2ooo/dai-lab-http-infrastructure/assets/44113916/e95edc73-19e5-4790-8c7b-c96cc644f78c)





