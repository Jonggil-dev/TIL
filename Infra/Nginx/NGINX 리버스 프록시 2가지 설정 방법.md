# NGINX 리버스 프록시 2가지 설정 방법

### 1. `location` 지시어를 사용한 URI 기반 라우팅

- 이 설정에서는 443 포트(`https://도메인`) 로 오는 요청에 대해 도메인 뒤의 location 주소(/api, /jenkins) 를 통해 요청을 프록시 함
- `/api`로 시작하는 요청을 `http://localhost:8080`으로, `/jenkins`로 시작하는 요청을 `http://localhost:8081`으로 프록시합니다. 이를 통해 각각의 서비스에 대한 요청을 적절한 내부 서비스로 라우팅할 수 있습니다.
- `location` 지시어를 사용한 설정은 특정 URI 패턴에 대한 요청을 다루는 방법입니다. 이를 통해, NGINX는 들어오는 요청을 URI 패턴에 따라 다른 내부 서비스로 라우팅할 수 있습니다.

**예시 코드:**

```nginx
server {
    listen 443 ssl;
    server_name example.com; # 실제 도메인으로 변경

    ssl_certificate /path/to/your/certificate.pem; # 인증서 파일 경로
    ssl_certificate_key /path/to/your/private.key; # 비공개 키 파일 경로

    location /api {
        proxy_pass http://localhost:8090; # API 서비스로 프록시
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /jenkins {
        proxy_pass http://localhost:8080; # Jenkins 서비스로 프록시
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```



### 2. SSL을 사용하여 특정 포트에서 리버스 프록시 설정

이 방법은 NGINX를 사용하여 특정 포트(예: 8090)에서 SSL/TLS 암호화를 사용하여 HTTPS 트래픽만을 수신하고, 이 트래픽을 내부 서비스로 프록시하는 설정입니다. 이 구성은 보안이 중요한 서비스에 적합하며, 데이터 전송 중 정보의 기밀성과 무결성을 보장합니다. 이 설정은 8090 포트에서 들어오는 HTTPS 트래픽만을 `http://localhost:8080`으로 프록시합니다. SSL 인증서와 키 파일 경로는 실제 환경에 맞게 설정해야 합니다.

**예시 코드:**

```nginx
server {
    listen 8090 ssl;
    server_name example.com; # 실제 도메인으로 변경

    ssl_certificate /path/to/your/certificate.pem; # 인증서 파일 경로
    ssl_certificate_key /path/to/your/private.key; # 비공개 키 파일 경로

    location / {
        # 내부 서비스로 프록시 (nginx와 같은 레벨 수준의 다른 localhost 포트로 요청을 전송)
        proxy_pass http://localhost:8080; 

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```
