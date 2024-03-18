# Jenkinsfile 작성

### 1. Spring 백엔드 서버

- **소스코드를 Gradle로 빌드하고 Docker Container로 서버를 실행하는 로직**

```groovy
pipeline {
    agent any

    environment {
        // Docker 이미지 이름과 태그 설정
        IMAGE_NAME = 'Simchengonge_spring'
        IMAGE_TAG = 'latest'
    }


    stages {
        stage('Check Changes') {
            steps {
                script {
                // 마지막 성공한 빌드 이후 변경된 파일 목록을 가져옴
                def changedFiles = sh(script: "git diff --name-only HEAD \$(git rev-parse HEAD~1)", returnStdout: true).trim()
                // 변경된 파일이 백엔드 디렉토리 내에 있는지 확인
                env.BUILD_FE = changedFiles.contains("simcheonge_front/") ? "true" : "false"
                // 변경된 파일이 프론트엔드 디렉토리 내에 있는지 확인
                env.BUILD_BE = changedFiles.contains("simcheonge_server/") ? "true" : "false"
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
                    sh "docker run -d -p 8888:8888 --name spring ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }
    }

    post {
        always {
            echo 'Build process completed.'
        }
    }
}

```



