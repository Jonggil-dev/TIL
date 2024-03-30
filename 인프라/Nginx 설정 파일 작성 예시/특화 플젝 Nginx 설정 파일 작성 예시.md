# 특화 플젝 Nginx 설정 예시

```nginx
  GNU nano 4.8                                                                                                                                                                                                                                                                                               /etc/nginx/sites-available/default
### You should look at the following URL's in order to grasp a solid understanding
# of Nginx configuration files in order to fully unleash the power of Nginx.
# https://www.nginx.com/resources/wiki/start/
# https://www.nginx.com/resources/wiki/start/topics/tutorials/config_pitfalls/
# https://wiki.debian.org/Nginx/DirectoryStructure
#
# In most cases, administrators will remove this file from sites-enabled/ and
# leave it as reference inside of sites-available where it will continue to be
# updated by the nginx packaging team.
#
# This file will automatically load configuration files provided by other
# applications, such as Drupal or Wordpress. These applications will be made
# available underneath a path with that package name, such as /drupal8.
#
# Please see /usr/share/doc/nginx-doc/examples/ for more detailed examples.
##

# Default server configuration
#


server {
    if ($host = 도메인) {
        return 301 https://$host$request_uri;
    } # managed by Certbot


        listen 80 default_server;
        listen [::]:80 default_server;

        server_name 도메인;

        return 404; # managed by Certbot


}



server {


        listen 443 ssl default_server;
        listen [::]:443 ssl default_server;

        server_name 도메인; # managed by Certbot

        root /var/www/react/build; # react build file directory(simcheonge admin page)

        index index.html index.htm index.nginx-debian.html;


        ssl_certificate /etc/letsencrypt/live/도메인/fullchain.pem; # managed by Certbot
        ssl_certificate_key /etc/letsencrypt/live/도메인/privkey.pem; # managed by Certbot
        include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
        ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot


        location /api {

                proxy_pass http://localhost:8090;
                proxy_set_header Host $host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header X-Forwarded-Proto $scheme;

        }

        location /simcheonge {
                alias /home/ubuntu/apk_files/deploy/;
                try_files /simcheonge.apk =404;
                add_header Content-Disposition 'attachment; filename="simcheonge.apk"';
        }

}


server {

        listen 8070 ssl default_server;

        server_name 도메인;



        ssl_certificate /etc/letsencrypt/live/도메인/fullchain.pem; # managed by Certbot
        ssl_certificate_key /etc/letsencrypt/live/도메인/privkey.pem; # managed by Certbot
        include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
        ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot

        location / {

            proxy_pass http://localhost:8080;
            proxy_set_header Host $host:$server_port;
            proxy_set_header X-Forwarded-Host $host:$server_port;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

        }

}


```

