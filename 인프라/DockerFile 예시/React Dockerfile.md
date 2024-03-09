# React DockerFile

```dockerfile
# 빌드 단계
FROM node:20.11.0 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 실행 단계
FROM node:20.11.0
# `serve` 패키지를 전역으로 설치
RUN npm install -g serve
# 빌드 디렉토리에서 파일 복사
COPY --from=build /app/dist /app
WORKDIR /app
# 3000번 포트에서 실행
EXPOSE 3000
CMD ["serve", "-s", ".", "-l", "3000"]
```

- Nginx를 리버스 프록시 서버로 두고 있을 때 작성한 React DockerFile코드임.
- `EXPOSE` 명령어는 Docker 컨테이너가 사용할 포트를 문서화하기 위한 것임. `docker inspect` 명령어를 사용하면 `EXPOSE`를 통해 지정된 포트 정보를 볼 수 있음. 실제로 포트를 열거나 트래픽을 해당 포트로 리디렉션하는 데 직접적인 영향을 주지 않음.

- CMD ["serve", "-s", ".", "-l", "3000"]
  - `serve`: Node.js 기반의 정적 파일 서버를 실행합니다. 이는 HTML, CSS, JavaScript 파일 등을 웹 서버를 통해 접근할 수 있게 해줍니다.
  - `-s` 또는 `--single`: Single-Page Application (SPA) 모드를 활성화합니다. 이 모드에서는 `serve`가 받은 모든 경로 요청을 `index.html`로 리다이렉션합니다, SPA를 서빙할 때 필수적입니다.
    - **`-s` 옵션 없이 사용**: 서버는 ``https://example.com/about(예시)`에 대한 요청을 받고, 해당 경로에 매칭되는 실제 파일을 찾으려고 시도합니다. 하지만 SPA에서는 `/about` 경로에 대한 실제 파일이 없기 때문에, 서버는 404 Not Found 에러를 반환할 수 있습니다.
    - **`-s` 옵션으로 사용**: 사용자가 `https://example.com/about(예시)`에 접근하려고 해도, 서버는 `index.html`을 제공하고, 클라이언트 측 JavaScript가 URL 경로를 해석하여 적절한 뷰를 렌더링합니다. 이 방식으로 SPA는 올바르게 작동하게 됩니다.
    - 로컬에서 개발 서버 도구(예: Create React App의 `react-scripts start`등)는 `-s`옵션과 같은 SPA 라우팅을 올바르게 처리할 수 있도록 설계되어 있어서 이태까지 오류 없이 라우팅 url로 바로 접근해도 정상 동작 했던거임

  - `.`: 현재 디렉토리의 파일들을 서빙하겠다는 의미입니다.
  - `-l 3000`: 서버가 리스닝할 포트 번호를 지정합니다. 여기서는 `3000`번 포트를 사용합니다.
