FROM webdevops/php-nginx:alpine-php7

# Install nginx, PHP and required extensions
RUN apk add --no-cache nginx php7 php7-fpm php7-opcache && \
    mkdir -p /run/nginx

# Copy the static site content into the Nginx web directory
COPY nginx.conf /etc/nginx/nginx.conf
# Copy the PHP application (formerly static HTML) to the container
COPY startbootstrap-agency-gh-pages /app

# Expose port 80
EXPOSE 80

# Start Nginx when the container launches
CMD php-fpm7; nginx -g 'daemon off;'
