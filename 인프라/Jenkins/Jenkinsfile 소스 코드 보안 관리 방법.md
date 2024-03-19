# Jenkinsfile 소스 코드 보안 관리 방법

### 0. !!!주의사항!!!!

- 저장된 Credentials을 jenkinsfile에서 사용할 때 Credentials를 불러오는 경로에 공백이 있으면 해당 경로를 제대로 인식 못할 수 있음. 이 때 환경변수를 따옴표로 감싸야 공백이 포함된 경로도 정확히 인식하여 동작함

  - `$properties` -> `${properties}`

  - 예시
    ```groovy
    #이렇게 하면 properties를 가져오는 경로에 공백이 포함되어 있으면 제대로 인지를 못함
    withCredentials([file(credentialsId: 'Spring_Env', variable: 'properties')]) {
            sh 'cp $properties simcheonge_server/src/main/resources/application-env.properties'
        }
    
    #이렇게 하면 properties를 가져오는 경로에 공백이 포함되어 있어도 인식 가능
    withCredentials([file(credentialsId: 'Spring_Env', variable: 'properties')]) {
            sh 'cp "${properties}" simcheonge_server/src/main/resources/application-env.properties'
        }
    
    ```

    

### 1. 자격 증명(Credentials) 사용

- **용도**: API 키, 비밀번호, 액세스 토큰, SSH 키 등 민감한 정보를 저장하고 Jenkins 파이프라인에서 안전하게 사용할 수 있도록 합니다.
- **저장 방법**:
  1. Jenkins 대시보드에서 "Manage Jenkins" > "Manage Credentials"로 이동합니다.
  2. 적절한 스코프를 선택하고 "Add Credentials"를 클릭합니다.
  3. 필요한 정보를 입력하고 저장합니다.
- **Jenkinsfile에서 사용**:
  - Secret Text, Username with password, SSH 키 등 다양한 자격 증명 유형이 있으며, 각각의 사용 방법이 조금씩 다릅니다.
  - 예를 들어, Secret Text는 다음과 같이 사용할 수 있습니다:
    ```groovy
    environment {
        MY_SECRET = credentials('my-secret-id')
    }
    ```
  - 사용자 이름과 비밀번호는 `withCredentials` 블록 내에서 사용할 수 있습니다:
    ```groovy
    withCredentials([usernamePassword(credentialsId: 'my-username-password-id', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        // 여기서 USERNAME과 PASSWORD 변수 사용
    }
    ```



### 2. 파이프라인 파라미터

- **용도**: Jenkins 파이프라인을 실행시킬 때 사용자로부터 특정 값을 직접 입력받아 그 값을 파이프라인 내에서 사용할 수 있도록 하는 방법
- **설정 방법**: Jenkinsfile 내에 파라미터를 정의합니다.
  
  ```groovy
  parameters {
      string(name: 'DEPLOY_ENV', defaultValue: 'production', description: 'Deployment environment')
  }
  ```
- **사용 방법**: `params` 객체를 통해 접근합니다.
  ```groovy
  echo "Deploying to ${params.DEPLOY_ENV}"
  ```



### 3. 외부 환경 설정 파일 사용

프로젝트 레포지토리나 외부 위치에 환경 설정 파일을 두고, Jenkins 파이프라인 실행 시 이 파일을 읽어 해당 설정을 사용하는 방법.

#### 구현 단계:

1. **환경 설정 파일 준비**: 예를 들어, 프로젝트 루트 또는 접근 가능한 외부 위치에 `env.properties` 파일을 생성하고, 필요한 구성 정보를 키-값 쌍으로 저장

   ```
   makefileCopy codeMY_DOMAIN=example.com
   API_PORT=8080
   ```

2. **Jenkins 파이프라인에서 파일 읽기**: Jenkinsfile 내에서 쉘 스크립트 또는 Groovy 스크립트를 사용하여 환경 설정 파일을 읽고, 해당 값을 파이프라인의 환경 변수로 설정

   - Groovy 스크립트 예시

     ```groovy
     groovyCopy codepipeline {
         agent any
         stages {
             stage('Load Environment') {
                 steps {
                     script {
                         // env.properties 파일에서 환경 변수 로드
                         def props = readProperties file: 'env.properties'
                         env.MY_DOMAIN = props['MY_DOMAIN']
                     }
                 }
             }
             stage('Use Environment') {
                 steps {
                     echo "Using domain: ${env.MY_DOMAIN}"
                 }
             }
         }
     }
     ```