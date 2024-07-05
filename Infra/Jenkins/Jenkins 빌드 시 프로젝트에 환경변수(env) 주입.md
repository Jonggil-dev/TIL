# Jenkins 빌드 시 Spring 환경변수 파일 활성화 방법



### 1. `application.properties`에 사용할 profile 파일이 명시적으로 작성하는 경우

- 예시 

  - `spring.profiles.include=secret` 또는 `spring.profiles.active=secret` 작성하는 경우

- **소스 코드 빌드에 해당 파일을 포함시키기만 하면 됨 (별도의 추가 작업 없음)** 

  - Jenkinsfile의 Spring 소스코드 빌드 Stage 이전에  `resource` 디렉토리에 해당 properties 파일이 존재하기만 하면 됨

  - 예시 : `application.properties`에 `spring.profiles.include=secret`가 작성 되어 있는경우

    ```groovy
      // Spring 소스 코드 빌드에 사용될 env 파일을 Crenditals에서 소스코드의 디렉토리로 복사하는 단계
            stage('Spring Env Prepare') {
                    steps {
      
                        withCredentials([
                            file(credentialsId: 'Spring_Secret', variable: 'SECRET_PROPERTIES')
                            ]) {
    
                        script{
                            // Jenkins가 EC2 내에서 특정 디렉토리를 수정할 수 있도록 권한 변경
                            sh 'chmod -R 755 simcheonge_server/src/main/resources/'
    
                            // Secret File Credential을 사용하여 설정 파일을 Spring 프로젝트의 resources 디렉토리로 복사
                            sh 'cp "${SECRET_PROPERTIES}" simcheonge_server/src/main/resources/application-secret.properties'
                        }
                    }   
                }
            }
    ```

    

### 2. `application.properties`에 사용할 profile 파일이 명시적으로 작성되지 않은 경우

- 예시 (아래 2가지 경우 모두 해당)
  - `spring.profiles.includ` 또는 `spring.profiles.active` 속성이 누락된 경우
  - `spring.profiles.include=${include}` 또는 `spring.profiles.active=${active}` 처럼 값 자체를 환경 변수로 사용하는 경우

- **소스 코드 빌드에 해당 파일을 포함시키기 + jar 파일 실행할 때(컨테이너 실행) active할 profile을 옵션을 명시하여 주입해 주어야 함**

  - Jenkinsfile의 Spring 소스코드 빌드 Stage 이전에  `resource` 디렉토리에 해당 properties 파일을 위치

  - jar 파일 실행할 때(컨테이너 실행) active할 profile을 옵션을 명시 (`-Dspring.profiles.active=xxx`)

  - 예시

    - **Spring 프로젝트의 application.properties 작성** (`spring.profiles.includ` 또는 `spring.profiles.active`속성 미기입)

      - ```properties
        spring.datasource.url=${SPRING_DATASOURCE_URL}
        ```


    - **application-prod.properties 작성**

      - ```properties
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

        ```dockerfile
        ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar", "/app/app.jar"]
        ```


### 
