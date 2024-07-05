# Docker Image 네이밍 규칙

### 이미지 이름 규칙

`docker build -t your_image_name:tagname .`

- **소문자 사용:** Docker 이미지 이름은 모두 소문자여야 합니다.

- **특수 문자:** 대시(-)와 언더스코어(_)는 사용할 수 있지만, 공백이나 기타 특수 문자는 피해야 합니다.

- **슬래시(/)로 구분):** 여러 계층을 가지는 경우, 예를 들어 사용자 계정 아래의 이미지라면 `username/imagename` 형식을 사용합니다.

  - Docker Hub push 할 때 사용하는 거임 (빌드 할 때 하는거 아님)

  ```
  #예시
  docker tag your_image_name:tagname your_dockerhub_username/your_image_name:tagname
  docker push your_dockerhub_username/your_image_name:tagname
  ```

- ex) `alice/webapp`에서 `alice`는 Docker Hub의 사용자 계정(또는 조직 계정)을 나타내고, `webapp`은 실제 이미지의 이름을 나타냅니다. 이미지 이름 자체는 `webapp`이 되지만, Docker Hub 같은 컨테이너 레지스트리에서는 `alice/webapp` 형태로 전체 경로를 사용하여 이미지를 구분하고 관리합니다. 여기서 `alice/` 부분은 이미지가 속한 네임스페이스를 나타냅니다.

### 태그명 규칙

- **구체적 명명:** 태그는 이미지의 특정 버전이나 상태를 명확히 식별할 수 있도록 구체적으로 명명해야 합니다.
- **버전 관리:** 소프트웨어의 버전을 따르는 경우, `v1.0.0`와 같이 세마틱 버저닝(Semantic Versioning)을 따르는 것이 일반적입니다.
- **최신 버전:** `latest` 태그는 가장 최근에 푸시된 이미지를 가리킵니다. 이 태그는 기본적으로 설정되지만, 프로덕션 환경에서는 특정 버전의 태그를 사용하는 것이 더 안정적입니다.
- **환경 또는 용도 기반 태그:** 개발, 테스트, 프로덕션 등의 환경이나 특정 용도를 나타내기 위해 태그를 사용할 수 있습니다. 예: `1.0.0-dev`, `1.0.0-prod`

### 예시

- `myapp:latest` – 가장 최근에 푸시된 `myapp` 이미지를 가리킵니다.
- `myapp:v1.2.3` – `myapp`의 특정 버전을 나타냅니다.
- `username/myapp:prod` – 사용자의 `myapp` 이미지 중 프로덕션 환경용을 나타냅니다.
