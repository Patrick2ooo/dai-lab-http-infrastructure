# Use the Nginx image from Docker Hub
FROM nginx:alpine

# Copy the static site content into the Nginx web directory
COPY startbootstrap-agency-gh-pages /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start Nginx when the container launches
CMD ["nginx", "-g", "daemon off;"]
