# Nginx Config - root, index 지시어 동작 방식

### 0. 요약

- `root` 지시어는 Nginx에서 요청된 파일을 찾을 기본 경로를 설정. 이는 `location` 블록별로 다르게 설정할 수 있으며, 특정 `location`에서 다른 처리 규칙(`proxy_pass` 등)이 정의된 경우, 그 규칙에 따라 요청이 처리 됨
- `index` 지시어는 클라이언트가 디렉토리에 접근했을 때 제공될 기본 파일을 지정



### 1. `root` 지시어

- Nginx 서버에서 웹사이트의 파일을 찾을 때 사용되는 기본 디렉토리를 설정

- **요청을 받으면, 설정된 `root` 경로와 요청 URL을 결합하여 찾아볼 파일의 위치를 결정한다고 생각하면 됨 **

- `server` 블록 레벨에 작성된 root 지시어는 `http(s)://도메인/` 으로 매핑 됨

- `server`블록 레벨에 작성된 root 지시어가 있더라도, location 블록과 매핑되는 url의 경우에는 location 블록의 실행이 우선적임

- (예시) `server` 블록 레벨 수준에 `root /var/www/html` 작성

  - `http(s)://도메인/example` 로 요청이 오는 경우
  - 가장 먼저 location 블록에 일치하는 규칙이 있는지 찾아봄
      - location 블록에 일치하는 규칙이 있다면 그 블록에 정의된 설정에 따라 요청을 처리
      - location 블록에 일치하는 규칙이 없다면 Nginx는 다음 두 가지를 시도
        - 먼저,  `example`를 파일로 인식하고`/var/www/html/`에서 `example` 파일을 제공하려 함
        - 파일이 없고  `/example/`처럼 명시적으로 디렉토리를 가리키지 않더라도, Nginx는 `/var/www/html/example` 경로를 디렉토리로 간주하고, 이 디렉토리 안에서 `index` 지시어에 지정된 파일을 제공하려함.
    
- `http(s)://도메인/example/` 로 요청이 오는 경우
    - location 블록에 일치하는 규칙이 없다면 `/example/`을 바로 디렉토리 접근으로 간주함
    - Nginx는 `/var/www/html/example` 경로를 디렉토리로 간주하고, 이 디렉토리 안에서 `index` 지시어에 지정된 파일을 제공하려 함
    
- `http(s)://도메인/example.jpg` 로 요청이 오는 경우
    - location 블록에 일치하는 규칙이 없다면 `/example.jpg` 를  바로 파일 접근으로 간주함
  - `Nginx`는 `/var/www/html/` 디렉토리에서 `example.jpg` 파일을 찾아서 제공 하려함


- `root`는 `location` 블록에도 설정될 수 있음
  - `location 매핑 url` 까지가 root 에 작성된 디렉토리 경로가 됨
    - (예시) `location /test` 블록 내에 `root /var/ww/html` 작성
      - `http(s)/도메인/test/example`로 요청이 오면 `/var/ww/html/example/` 디렉토리 주소로 매핑됨
  - `location` 블록에서 `root`를 설정하지 않으면, 상위 레벨(예: 서버 블록)에서 정의된 `root` 설정이 적용

### 2. `index` 지시어

- 클라이언트가 디렉토리에 접근했을 때 Nginx가 기본적으로 제공할 파일을 지정

- (예시) `root /var/www/html` +`index index.html index.htm; 작성`

  - `http(s)://domain.com/photos` 요청이 오고, 매핑되는 `location` 블록이 없는 경우

    - `/var/www/html/photos/` 에서 `index` 지시어에 지정된 파일 목록을 순서대로 검색하여 첫 번째로 찾은 파일을 반환
    - 즉, `/var/www/html/photos/`의 `index.html` 또는 `index.htm` 파일 중 하나를 반환

    


### 3. `proxy_pass`와 `root`의 상호 작용

- `proxy_pass`는 요청을 다른 서버로 전달하고, 그 서버로부터 받은 응답을 클라이언트에게 반환하는 역할
- `proxy_pass`가 설정된 `location` 블록에서는 요청이 외부로 전달되어 처리되므로, `root` 지시어가 지정하는 경로에서 파일을 찾는 과정은 발생하지 않음
- (예시)`location /api` 블록에서 `proxy_pass`와 `root`가 함께 설정되어 있더라도, 요청 처리는 `proxy_pass`에 의해 결정되며, `root` 지시어는 이 경우 요청 처리에 직접적으로 관여하지 않음
