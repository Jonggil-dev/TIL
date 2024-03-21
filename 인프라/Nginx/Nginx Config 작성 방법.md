# Nginx Config 참고 사항

- nginx에 관한 설정 파일을 작성하는 방법은 3가지 방법이 있음

  - **결론적으로 nginx.conf파일에 sites-available의 설정 코드들이 올라가서 동작하므로 아래 3개 중에 1가지 방법 선택**
  - `/etc/nginx/nginx.conf` 를 수정
  - `/etc/nginx/sites-available`의 default를 수정
  - `/etc/nginx/sites-available`에 설정 파일을 새로 작성 후 저장


- **설정 파일 작성 끝나고 나면 항상 아래 명령어 실행하기**

  ```bash
  # 구성 파일에 오류가 없는지 확인
  sudo nginx -t
  
  # NGINX 재시작
  sudo systemctl restart nginx
  
  # NGINX 상태확인
  sudo systemctl status nginx
  ```

### 주요 개념

- 한 서버에서도 도메인명을 여러개 가질 수 있음. 하지만 도메인명들은 결국 같은 서버의 IP주소를 가르키고 있음(동일한 주소를 가르킴). **그런데 config에 server_name 속성을 사용하면 요청이 오는 도메인명에 따라 응답을 다르게 처리할 수 있음.**

- `http:도메인:8080` 이런식으로 요청이 오면 명시적으로 url에 작성된 포트(8080)가 listen 되고 있는 server 블록이 실행됨.
  80포트(http)의 server 블록이 실행되는게 아님

- location 블록에서 proxy_pass를 작성할 때 끝에 "/"의 유무에 따라 내부 처리 방식이 달라짐

  - **예외 : `location /` 블록에서는 `proxy_pass` 마지막에`/`를 붙이는 것과 붙이지 않는 것 사이에는 차이가 사실상 없음**

  - **슬래시(`/`) 없이 `proxy_pass` 사용:**

    - **예시**
  
      ````nginx
      location /api {
      	proxy_pass http://localhost:8080;
      	}
      ````

    - **동작 방식**: 이 설정은 클라이언트로부터 받은 요청의 URI를 변경하지 않고, 그대로 프록시 대상 서버로 전달합니다. 즉, 클라이언트가 `http://도메인/api/example`로 요청을 보내면, NGINX는 이 요청을 `http://도메인/api/example`로 프록시합니다. 여기서 `/api/example` 부분이 변경되지 않고 그대로 유지됩니다.

  - **슬래시(`/`)를 붙여 `proxy_pass` 사용:**

    - **예시**
  
      ```nginx
      location /api {
      	proxy_pass http://localhost:8080/;
      	}
      ```
  
    - **동작 방식**: 이 설정은 요청의 URI에서 `location` 블록과 매치되는 부분을 제거하고, 남은 부분을 프록시 대상 서버로 전달합니다클라이언트가 `http://yourdomain.com/api/example`로 요청을 보내면, NGINX는 이 요청을`http://localhost:8080/example`로 프록시합니다. 여기서 원래 요청 URI의 `/api` 부분이 제거되고, 남은 `/path` 부분만 프록시 대상 서버로 전달됩니다.

- `proxy_set_header`와 `X-Forwarded-For`
  - `proxy_set_header` 지시문은 요청 헤더를 수정하거나 추가하기 위해 사용. `proxy_pass`는 요청의 URI 전달 방식을 결정하지만, 요청과 함께 전달되는 HTTP 헤더를 조작하려면 `proxy_set_header`이 필요.
  - `X-Forwarded-Host` 헤더는 프록시 서버나 로드 밸런서를 거치는 HTTP 요청에서 원본 요청의 호스트를 식별하기 위해 사용. 이 헤더는 원본 요청에서 사용된 호스트 헤더의 값을 백엔드 서버에 전달하여, 백엔드 서버가 실제 요청이 어떤 도메인을 통해 이루어졌는지 알 수 있게 해줌. 이는 리다이렉션 처리, 보안 검사, 멀티 테넌시 어플리케이션에서 중요할 수 있습니다.
