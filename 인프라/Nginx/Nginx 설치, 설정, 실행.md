# Nginx 설치, 설정, 실행

### 1. nginx 사용 방법 2가지

 	1. Docker image를 이용해 컨테이너로 실행하는 방법
 	2. 그냥 Docker없이 EC2 서버에 nginx를 설치해서 직접 실행하는 방법
 	 -  Docker 안쓰고 nginx를 직접 설치해서 사용하는게 설정이 편해보임

<hr>

### 2. 구현 과정 (Docker 사용 X)

AWS EC2 인스턴스에서 Nginx를 설치하고, HTTPS를 구성하여 접속하는 전체 과정을 단계별로 정리하겠습니다. 이 과정은 Ubuntu 기반 시스템을 예로 들고 있으며, 주로 사용되는 명령어와 설정 파일 수정 내용을 포함합니다.

#### (1) EC2 인스턴스 생성 및 초기 설정
- AWS 관리 콘솔에서 EC2 인스턴스를 생성합니다.
- 보안 그룹 설정에서 HTTP (80), HTTPS (443), 그리고 SSH (22) 포트가 열려 있는지 확인합니다.

#### (2) SSH를 통한 EC2 인스턴스 접속
- 터미널이나 SSH 클라이언트를 사용하여 생성한 EC2 인스턴스에 접속합니다.
  ```bash
  ssh -i /path/to/your-key.pem ubuntu@your-ec2-ip-address
  ```

#### (3) Nginx 설치
- 인스턴스에 접속한 후, Nginx를 설치합니다.
  ```bash
  sudo apt update
  sudo apt install nginx -y
  
  #nginx 상태(설치) 확인
  sudo systemctl status nginx
  ```

#### (4) HTTPS를 위한 SSL 인증서 생성
- **(도메인이 있는 경우) Let's Encrypt 사용** 

  - 인증서를 발급하는 기관이 여러개라 발급하는 방법이 여러 가지가 있음 (나는 Let's Encrypt에서 받음)

  - Certbot을 이용한 letsencrypt으로 SSL 인증서 발급

    - **Let's Encrypt**: SSL/TLS 인증서를 무료로 제공하는 CA입니다.
    - **Certbot**: Let's Encrypt에서 발급한 인증서를 웹 서버에 설치하고 자동으로 갱신하는 데 사용되는 소프트웨어 도구입니다.
    - **"letsencrypt"** : 일반적으로 Let's Encrypt 프로젝트나 관련 프로세스 및 도구를 의미하는 데 사용. Certbot을 설치하거나 Let's Encrypt 인증서를 발급하는 과정(명령어 포함)을 의미할 때 사용

  - 절차

    ```bash
    #sudo apt install letsencrypt 입력해도 sudo apt install certbot 이렇게 실행됨 (공식 클라이언트 이름이 바뀜) 
    sudo apt install certbot
    
    #Certbot의 Nginx 플러그인을 설치
    sudo apt install certbot python3-certbot-nginx
    
    #Certbot을 실행하여 Nginx 웹 서버를 위한 SSL/TLS 인증서를 발급받고, Nginx 설정에 인증서를 자동으로 적용
    sudo certbot --nginx
    ```

    - Certbot이 Nginx의 구성 파일에서 도메인 이름을 자동으로 찾지 못했을 경우 도메인 입력 프롬프트가 나옴

      - www.example.com 같이 정확한 도메인 입력
      - 방화벽 80포트 열려 있어야 가능함

    - choose whether or not to redirect HTTP traffic to HTTPS, removing HTTP access 옵션

      - Redirect로 설정 (nginx.conf에 관련 코드를 자동으로 작성해준다는거임)

      


- **(도메인이 없는 경우) 자체 서명된 인증서를 생성합니다.**

  - **OpenSSL 설치**: 시스템에 OpenSSL이 설치되어 있지 않은 경우, 먼저 설치합니다.

    ```
    bashCopy codesudo apt update
    sudo apt install openssl
    ```

  - **인증서를 위한 디렉토리 생성**: SSL 인증서와 키 파일을 저장할 디렉토리를 생성합니다.

    ```
    bashCopy codesudo mkdir /etc/nginx/ssl
    cd /etc/nginx/ssl
    ```

  - **자체 서명된 인증서 생성**: 다음 명령어를 사용하여 자체 서명된 인증서와 개인 키를 생성합니다. 이때 `your-ec2-ip-address`를 실제 EC2 인스턴스의 공개 IP 주소로 변경해야 합니다.

    ```
    bashCopy code
    sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout nginx-selfsigned.key -out nginx-selfsigned.crt -subj "/C=KR/ST=Seoul/L=Seoul/O=Example Company/OU=IT Department/CN=your-ec2-ip-address"
    ```

  - **DH 파라미터 생성**: 더 강력한 보안을 위해 Diffie-Hellman 그룹을 생성합니다.

    ```
    bashCopy code
    sudo openssl dhparam -out dhparam.pem 2048
    ```


#### (5) Nginx 설정 수정
- **Nginx 설정 파일 편집**: `/etc/nginx/sites-available/default` 파일을 편집모드로 엽니다.

  ```
  sudo nano /etc/nginx/sites-available/default
  ```

- **HTTPS 서버 블록 추가**:HTTPS 설정을 추가하고, HTTP에서 HTTPS로 리다이렉트 설정합니다.

  ```nginx
  server {
      listen 443 ssl;
      server_name your-ec2-ip-address;
  
      ssl_certificate /etc/nginx/ssl/nginx-selfsigned.crt;
      ssl_certificate_key /etc/nginx/ssl/nginx-selfsigned.key;
      ssl_dhparam /etc/nginx/ssl/dhparam.pem;
  
      ssl_protocols TLSv1.2 TLSv1.3;
      ssl_prefer_server_ciphers on;
      ssl_ciphers "ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384";
  
      root /var/www/html;
      index index.html index.htm;
  
      location / {
          try_files $uri $uri/ =404;
      }
  }
  
  server {
      listen 80;
      server_name your-ec2-ip-address;
      return 301 https://$server_name$request_uri;
  }
  ```

- 설정 변경 후 Nginx 문법 오류 확인, 재시작:
  ```bash
  sudo nginx -t
  sudo systemctl restart nginx
  ```
