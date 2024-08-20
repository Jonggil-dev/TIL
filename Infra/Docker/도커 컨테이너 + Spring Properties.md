# 도커 컨테이너 + Spring Properties

1. postigreSQL(postgis 익스텐션 포함된 이미지) 실행 명령어 및 Spring Properties 설정

```bash
sudo docker run -d -p 5432:5432 \
-e POSTGRES_USER=username \
-e POSTGRES_PASSWORD=password \
-e POSTGRES_DB=DataBaseName \
--name Postgres postgis/postgis:16-3.4-alpine
```

```yml
spring:
  datasource:
    url: ${DATABASE.POSTGRES.URL}
    username: ${DATABASE.POSTGRES.USERNAME}
    password: ${DATABASE.POSTGRES.PASSWORD}
    driver-class-name: org.postgresql.Driver
```

2. Redis 실행 명령어 및 Spring Properties 설정

```bash
docker run -p 6379:6379 --name Redis -d redis:latest --requirepass password
```

```yml
spring:
  data:
    redis:
      host: ${DATABASE.REDIS.HOST} #localhost로 사용함
      port: ${DATABASE.REDIS.PORT}
      password: ${DATABASE.REDIS.PASSWORD}
```

3. MariaDB 실행 명령어

```bash
docker run -d --name mariadb \
-v mariadb_volume:/var/lib/mysql \
-e TZ=Asia/Seoul \
-e MYSQL_ROOT_PASSWORD=password \
-p 3306:3306 mariadb:latest
```

4. Jenkins 실행 명령어

```bash
sudo docker run -d -p 8080:8080 \
-v /home/ubuntu/jenkins-data:/var/jenkins_home \
-v /var/run/docker.sock:/var/run/docker.sock \
-v $(which docker):/usr/bin/docker \
--name jenkins jenkins/jenkins:latest
```
