FROM php:8.2-apache
RUN apt update && apt install libjs-jquery-jfeed -y
RUN chown -R www-data:www-data /var/www/html && chmod -R 755 /var/www/html
RUN export TMP_KEY=$(cat /proc/sys/kernel/random/uuid) && \
    echo $TMP_KEY > /var/www/secret_key.txt && \
    chown www-data:www-data /var/www/secret_key.txt
COPY verygame/src /var/www/html/
COPY flag.txt /flag.txt
RUN mv /flag.txt /flag-$(cat /proc/sys/kernel/random/uuid).txt 

EXPOSE 80
