# Nginx 리버스 프록시 설정 방법

### 1. Nginx 리버스 프록시 설정

- `/etc/nginx/sites-available/`에 새로운 설정 파일을 생성합니다. 이 파일에서 리버스 프록시 설정을 정의합니다.

- 설정 예시:
  ```nginx
  server {
      listen 80;
      server_name example.com; # 사용할 도메인이나 IP 주소로 변경
  
      location / {
          proxy_pass http://localhost:3000; # React 애플리케이션 포트로 변경
          proxy_http_version 1.1;
          proxy_set_header Upgrade $http_upgrade;
          proxy_set_header Connection 'upgrade';
          proxy_set_header Host $host;
          proxy_cache_bypass $http_upgrade;
      }
  }
  ```
  
- 심볼릭 링크와 설정 파일생성한 설정 파일을 `/etc/nginx/sites-enabled/` 디렉토리로 심볼릭 링크를 생성해 활성화합니다.

  ```bash
  sudo ln -s /etc/nginx/sites-available/설정파일명 /etc/nginx/sites-enabled/
  ```

  

  - `/etc/nginx/sites-enabled/` 디렉토리에 있는 파일들은 Nginx 서버의 사이트 구성 파일들에 대한 심볼릭 링크입니다. Nginx는 이 디렉토리를 통해 활성화된 사이트 설정을 관리합니다.

    - `/etc/nginx/sites-available/`
      - 이 디렉토리에는 서버의 가능한 모든 사이트 구성 파일이 저장됩니다. 이 파일들은 실제로 Nginx에 의해 사용되지 않으며, 단지 가능한 설정의 저장소 역할을 합니다.

    - `/etc/nginx/sites-enabled/`
      - 이 디렉토리에는 `/etc/nginx/sites-available/` 디렉토리에서 심볼릭 링크를 통해 "활성화된" 사이트 구성 파일들이 있습니다. Nginx는 이 디렉토리에 있는 구성 파일들을 로드하여 사용합니다.

    - `/etc/nginx/sites-available/`에 있는 설정 파일이라도 `/etc/nginx/sites-enabled/` 디렉토리에 심볼릭 링크가 없다면, Nginx는 해당 설정을 무시함.

    즉, Nginx를 사용하여 여러 사이트를 설정할 때, 각 사이트에 대한 구성 파일을 `/etc/nginx/sites-available/`에 생성하고, 해당 사이트를 활성화하려면 그 구성 파일의 심볼릭 링크를 `/etc/nginx/sites-enabled/` 디렉토리에 생성합니다. 이렇게 하면 Nginx가 해당 구성을 읽어들여 사이트를 활성화할 수 있습니다.


### 2. Nginx 구성 테스트 및 재시작
- 구성이 올바른지 테스트합니다: `sudo nginx -t`
- 변경사항을 적용하기 위해 Nginx를 재시작합니다: `sudo systemctl restart nginx`
