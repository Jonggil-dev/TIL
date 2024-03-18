# Jenkins 빌드 시 프로젝트에 환경변수(env) 주입

### 1. 문제점
- `Spring의 application-env.properties 등` 민감한 정보(환경 변수)를 담고 있는 파일들은 .gitignore에 포함되어 gitlab에 올라가지 않지만 배포를 위한 프로젝트의 빌드 과정에서 필요한 자원일 경우 Jenkins build 단계에서 문제가 생김

### 2. 해결 방안 2가지

####   1. 환경 변수를 통한 설정 전달

- **Jenkins Credentials 설정**

  - Jenkins 대시보드에서 "Manage Jenkins" > "Manage Credentials"로 이동합니다.

  - 필요한 정보(예: 데이터베이스 URL, 사용자 이름, 비밀번호)를 Credentials로 추가합니다.

  - **Credentials은 원하는 타입에 맞게 설정해서 사용하면 됨. 대신, 해당 Credentials을 불러오는 jenkinsfile의 코드가 달라짐**

    - **Secret file 타입 사용 시 Jenkinsfile 예시**

      - ```groovy
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

        

    - **Secret Text Credentials 사용 시 Jenkinsfile 예시**

      - ```groovy
        pipeline {
            agent any
            environment {
                // Jenkins Credentials에서 환경 변수로 불러오기 ()
                DB_URL = credentials('DB_URL_CREDENTIALS_ID')
                DB_USERNAME = credentials('DB_USERNAME_CREDENTIALS_ID')
                DB_PASSWORD = credentials('DB_PASSWORD_CREDENTIALS_ID')
            }
            stages {
                stage('Build') {
                    steps {
                        // Gradle 또는 Maven 빌드 시 환경 변수를 사용
                        sh '''
                        ./gradlew build \
                        -Dspring.datasource.url=$DB_URL \
                        -Dspring.datasource.username=$DB_USERNAME \
                        -Dspring.datasource.password=$DB_PASSWORD
                        '''
                    }
                }
                // 필요한 다른 빌드 단계 추가
            }
        }
        ```



#### 2 설정 파일 동적 생성

- Jenkins 파이프라인에서 동적으로 `application-env.properties`와 같은 파일을 생성하고, 이를 빌드 또는 실행 시 애플리케이션에 제공합니다.

- **Jenkinsfile 수정**:

  - 빌드 과정에서 `application-env.properties` 파일을 동적으로 생성하는 스텝을 추가합니다.

  - 예:
    ```groovy
    stage('Prepare') {
        steps {
            script {
                writeFile file: 'src/main/resources/application-env.properties', text: """
                spring.datasource.url=${DB_URL}
                spring.datasource.username=${DB_USERNAME}
                spring.datasource.password=${DB_PASSWORD}
                """
            }
        }
    }
    ```

- **애플리케이션 설정**:

  - 생성된 `application-env.properties` 파일은 애플리케이션 실행 시 자동으로 인식됩니다.

  - 추가 설정 없이 Spring Boot 애플리케이션은 이 파일의 설정을 사용합니다.