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
