# Jenkinsfile 작성

```groovy
pipeline {
    agent any

    environment {
        // Docker 이미지 이름과 태그 설정
        IMAGE_NAME = 'simcheonge_spring'
        IMAGE_TAG = 'latest'
        
        //ANDROID_HOME 환경변수 설정(flutter 빌드 시 필요)
        ANDROID_HOME = '/home/ubuntu/android-studio/'
    }


    stages {
        stage('Check Changes') {
            steps {
                script {

                // 마지막 성공한 빌드 이후 변경된 파일 목록을 가져옴
                def changedFiles = sh(script: "git diff --name-only HEAD \$(git rev-parse HEAD~1)", returnStdout: true).trim()

                // // 변경된 파일이 프론트엔드 디렉토리 내에 있는지 확인
                env.BUILD_FE = changedFiles.contains("simcheonge_front/") ? "true" : "false"
                // // 변경된 파일이 백엔드 디렉토리 내에 있는지 확인
                env.BUILD_BE = changedFiles.contains("simcheonge_server/") ? "true" : "false"

                // // 변경된 파일이 관리자 페이지 디렉토리 내에 있는지 확인
                env.BUILD_ADMIN = changedFiles.contains("simcheonge_admin/") ? "true" : "false"

                }
            }
        }


        // Spring 소스 코드 빌드에 사용될 env 파일을 Crenditals에서 소스코드의 디렉토리로 복사하는 단계
        stage('Spring Env Prepare') {
                when {
                    expression { env.BUILD_BE == "true" }
                }
                steps {
  
                    withCredentials([
                        file(credentialsId: 'Spring_Prod', variable: 'PROD_PROPERTIES'),
                        file(credentialsId: 'Spring_Secret', variable: 'SECRET_PROPERTIES')
                        ]) {

                    script{
                        // Jenkins가 EC2 내에서 특정 디렉토리를 수정할 수 있도록 권한 변경
                        sh 'chmod -R 755 simcheonge_server/src/main/resources/'

                        // Secret File Credential을 사용하여 설정 파일을 Spring 프로젝트의 resources 디렉토리로 복사
                        sh 'cp "${PROD_PROPERTIES}" simcheonge_server/src/main/resources/application-prod.properties'
                        sh 'cp "${SECRET_PROPERTIES}" simcheonge_server/src/main/resources/application-secret.properties'

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
                    sh "docker run -d -p 8090:8090 --name spring ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }

        stage('Build APK for Andrioid  ') {
            when {
                expression { env.BUILD_FE == "true" }
            }
            steps {

                script {
                    dir('simcheonge_front') {

                
                    /*
                    jenkins 컨테이너 내부에서 flutter 명령어를 사용하지 못함.
                    jenkins 컨테이너 실행할 때 flutter 관련 볼륨마운트 했던
                    /var/flutter/bin/flutter 파일을 명시적으로 작성해서 flutter 명령어 사용 
                    */
                    
                    /*
                    Git 설정을 추가하여 /var/flutter 디렉토리를 안전하다고 표시 하는 작업
					아래 명령어 안하면,   Git 소유권 문제로 인해 최신 git 커밋 날짜를 찾지 못해 VersionCheckError 발생함
					(Git 2.35.2 버전 이후로, 안전 검사를 추가해서 해줘야 되는 명령어임)
                    */
                    sh 'git config --global --add safe.directory /var/flutter'
                        
                    // Flutter 종속성 가져오기
                    sh '/var/flutter/bin/flutter pub get'
                        
                    // APK 빌드
                    sh '/var/flutter/bin/flutter build apk'
                	}
                }
            }
        }
        stage('Upload APK to EC2 ') {
             when {
                expression { env.BUILD_FE == "true" }
            }
            
            steps {
                script {
                dir('simcheonge_front') {
                    // 버전 정보 추출
                    // pubspec.yaml 파일에 version 이라는 단어가 많아서 8번째에 해당하는게 버전이라 그 부분만 추출하는 코드
                    def version = sh(script: "grep version pubspec.yaml | head -n 8 | tail -n 1 | awk '{print \$2}'", returnStdout: true).trim()

                    // 클라이언트 다운로드 용 APK 파일 복사
                    sh "cp -f build/app/outputs/flutter-apk/app-release.apk /home/ubuntu/apk_files/deploy/simcheonge.apk"
                    
                    // 버전 관리 용 APK 파일 복사 (저장용)
                    sh "cp build/app/outputs/flutter-apk/app-release.apk /home/ubuntu/apk_files/stores/simcheonge-${version}.apk"
                    }
                }
            }
        }

         // React 소스 코드 빌드에 사용될 env 파일을 Crenditals에서 소스코드의 디렉토리로 복사하는 단계
        stage('React Env Prepare') {
                when {
                    expression { env.BUILD_ADMIN == "true" }
                }
                steps {
  
                    withCredentials([
                        file(credentialsId: 'React_Env', variable: 'ENV_CONFIG')
                        ]) {

                    script{
                        // Jenkins가 EC2 내에서 특정 디렉토리를 수정할 수 있도록 권한 변경
                        sh 'chmod -R 755 simcheonge_admin/'

                        // Secret File Credential을 사용하여 설정 파일을 React 프로젝트의 simcheonge_admin 디렉토리로 복사
                        sh 'cp "${ENV_CONFIG}" simcheonge_admin/.env'
                    }
                }   
            }
        }

        stage('Build React for Admin ') {
            when {
                expression { env.BUILD_ADMIN == "true" }
            }
            steps {
                script {
                    dir('simcheonge_admin') {
                        // EC2에 node.js가 설치되어 있지 않아 npm을 사용할 수 없음
                        // 그래서 node 이미지를 기반으로 한 임시 컨테이너에서 npm build 작업을 수행함
                        // stage가 끝나면 컨테이너는 자동으로 삭제됨 (stage가 끝나도 이미지는 로컬(여기서는 EC2)에 남아있음)
                        // 로컬(EC2)에 이미지가 없으면 자동으로 docker hub에서 다운받아서 작업을 수행함
                        docker.image('node:20.10.0').inside {
                        
                        // Node.js를 사용하여 React 프로젝트 빌드
                        sh "rm -rf node_modules package-lock.json"
                        sh "npm install"
                        sh "CI=false npm run build"
                    }
                  }
                }
            }
        }

        stage('Upload React to EC2 ') {
             when {
                expression { env.BUILD_ADMIN == "true" }
            }
            
            steps {
                script {
                dir('simcheonge_admin') {
                    // 클라이언트 다운로드 용 React build 파일 복사
                    sh "cp -rf build/ /var/www/react/"
                    }
                }
            }
        }
    }
}
```
