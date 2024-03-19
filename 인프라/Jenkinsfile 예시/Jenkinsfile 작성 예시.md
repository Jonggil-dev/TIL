# Jenkinsfile 작성

### 1. Spring 백엔드 서버

- **소스코드를 Gradle로 빌드하고 Docker Container로 서버를 실행하는 로직**

```groovy
pipeline {
    agent any

    environment {
        // Docker 이미지 이름과 태그 설정
        IMAGE_NAME = 'simchengonge_spring'
        IMAGE_TAG = 'latest'
    }


    stages {
        stage('Check Changes') {
            steps {
                script {
                // 마지막 성공한 빌드 이후 변경된 파일 목록을 가져옴
                def changedFiles = sh(script: "git diff --name-only HEAD \$(git rev-parse HEAD~1)", returnStdout: true).trim()
                // 변경된 파일이 백엔드 디렉토리 내에 있는지 확인
                env.BUILD_BE = "true"

                // env.BUILD_FE = changedFiles.contains("simcheonge_front/") ? "true" : "false"
                // 변경된 파일이 프론트엔드 디렉토리 내에 있는지 확인
                // env.BUILD_BE = changedFiles.contains("simcheonge_server/") ? "true" : "false"
                }
            }
        }


        // Spring 소스 코드 빌드에 사용될 env 파일을 Crenditals에서 소스코드의 디렉토리로 복사하는 단계
        stage('Spring Env Prepare') {
                when {
                    expression { env.BUILD_BE == "true" }
                }
                steps {
  
                    withCredentials([file(credentialsId: 'Spring_Env', variable: 'properties')]) {
                    script{
                        // Jenkins가 EC2 내에서 특정 디렉토리를 수정할 수 있도록 권한 변경
                        sh 'chmod -R 755 simcheonge_server/src/main/resources/'

                        // Secret File Credential을 사용하여 설정 파일을 Spring 프로젝트의 resources 디렉토리로 복사
                        sh 'cp "${properties}" simcheonge_server/src/main/resources/application-env.properties'
                    }
                }   
            }
        }

        stage('Build Spring Code') {
            when {
                expression { env.BUILD_BE == "true" }
            }
            steps {
                script {
                    dir('simcheonge_server') {
                        // Gradle을 사용하여 Spring 애플리케이션 빌드
                        sh 'chmod +x ./gradlew' // 실행 권한 추가
                        sh './gradlew build'
                        echo "Spring Build finished"
                    }
                }
            }
        }

        stage('Build Spring Image ') {
          when {
                expression { env.BUILD_BE == "true" }
            }
            steps {
                // Docker 이미지 빌드
                script {
                    dir('simcheonge_server') { 
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    }
                }
            }
        }

        stage('Run Spring Container') {
            when {
                expression { env.BUILD_BE == "true" }
            }
            steps {
                // Docker 컨테이너 실행
                script {

                    // 실행중인 spring 컨테이너가 있으면 종료하고 삭제
                    sh 'docker stop spring || true'
                    sh 'docker rm spring || true'
                    sh "docker run -d --name spring ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }
    }
}
```



