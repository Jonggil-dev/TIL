# Jenkins 빌드 시 프로젝트에 환경변수(env) 주입

### 1. 문제점
- `Spring의 application-prod.properties 등` 민감한 정보(환경 변수)를 담고 있는 파일들은 .gitignore에 포함되어 gitlab에 올라가지 않지만 배포를 위한 프로젝트의 빌드 과정에서 필요한 자원일 경우 Jenkins build 단계에서 문제가 생김

### 2. 해결 방법

- **Spring 프로젝트의 application.properties 작성**

  - ```
    spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
    ```

- **application-prod.properties 작성**

  - ```
    SPRING_DATASOURCE_USERNAME=root
    ```

- **Jenkins Credentials 설정**

  - Jenkins 대시보드에서 "Manage Jenkins" > "Manage Credentials".

  - 필요한 정보(예: 데이터베이스 URL, 사용자 이름, 비밀번호)를 Credentials로 추가.

  - **Secret file 타입 사용 해서 application-prod.properties 업로드**
    - **Credentials은 원하는 타입에 맞게 설정해서 사용하면 됨. 대신, 해당 Credentials을 불러오는 jenkinsfile의 코드가 달라짐**

- **Jenkinsfile 작성**

  ```groovy
  # Jenkinsfile
  pipeline {
      agent any
      stages {
          stage('Prepare') {
              steps {
                  // Secret File Credential을 사용하여 설정 파일을 임시 경로로 복사
                  // variable은 Secret file Credential을 저장한 임시 파일의 경로를 담는 환경 변수의 이름 
                  withCredentials([file(credentialsId: 'YOUR_CREDENTIALS_ID', variable: 'CONFIG_FILE')]) {
                      // 설정 파일을 현재 작업 디렉토리로 복사
                      sh 'cp $CONFIG_FILE ./src/main/resources/application-env.properties'
                  }
              }
          }
          stage('Build') {
              steps {
                  // Spring Boot 빌드 (Gradle 사용 예시)
                  sh './gradlew build'
              }
          }
      }
  }
  ```

- **Dockerfile 작성**

    - Docker Container 실행 시 "xxx" profile의 파일의 변수들을 주입하겠다는 설정을 해줘야 됨.  `-Dspring.profiles.active=xxx`

    ```
    ENTRYPOINT ["java","-Dspring.profiles.active=xxx", "-jar", "/app/app.jar"]
    ```

