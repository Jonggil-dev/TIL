# 특화 플젝 Nginx 설정 예시

```nginx
#/etc/nginx/sites-available/defaulf 파일

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


        location / {
				#SPA를 사용하는 CSR의 경우 어떤 요청이든 index.html을 제공해야 하기 때문에 try_files 설정 필요
                try_files $uri $uri/ /index.html;

        }


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

