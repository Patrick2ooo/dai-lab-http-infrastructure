# HTTP Infrastructure

## Etape 1: Serveur Web Statique
Pour ce laboratoire nous allons utilisé un serveur http static nginx. Il nous faut tous d'abord choisir un template de site, dans notre que nous avons pris [celui-ci](https://startbootstrap.com/theme/agency). 

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

