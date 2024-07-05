# Declarative Pipeline, Scripted Pipeline.

### 1. Declarative Pipeline

- **특징**: Declarative Pipeline은 Jenkins 파이프라인을 정의하기 위한 더 간결하고 구조화된 방법을 제공합니다. 
- **구조**: `pipeline` 블록으로 시작하며, 이 안에 `agent`, `stages`, `stage`, `steps` 등의 섹션이 포함됩니다. 각 섹션은 파이프라인의 다양한 측면을 정의합니다.
- **환경 설정**: 환경 변수는 `environment` 블록 내에 정의할 수 있으며, 전역, 스테이지 수준에서 사용할 수 있습니다.
- **에러 핸들링**: `post` 블록을 사용하여 빌드 상태(성공, 실패, 항상 등)에 따른 후처리 단계를 정의할 수 있습니다.
- **예시**:
  ```groovy
  pipeline {
      agent any
      stages {
          stage('Build') {
              steps {
                  echo 'Building...'
              }
          }
          stage('Test') {
              steps {
                  echo 'Testing...'
              }
          }
      }
      post {
          always {
              echo 'This will always run'
          }
          success {
              echo 'Build succeeded!'
          }
          failure {
              echo 'Build failed!'
          }
      }
  }
  ```

### 2. Scripted Pipeline

- **특징**: Scripted Pipeline은 Groovy 언어의 전체 기능을 활용할 수 있는 더 유연하고 동적인 방법을 제공합니다. 복잡한 로직이나 조건부 실행이 필요한 경우 유용합니다.
- **구조**: Groovy 스크립트 형식으로 작성되며, `node` 블록 내에서 실행되는 일련의 명령어로 구성됩니다.
- **환경 설정**: 변수를 직접 선언하거나 `env` 객체를 사용하여 환경 변수에 접근할 수 있습니다.
- **에러 핸들링**: `try/catch` 블록을 사용하여 에러를 직접 처리할 수 있습니다.
- **예시**:
  ```groovy
  node {
      stage('Build') {
          echo 'Building...'
      }
      stage('Test') {
          echo 'Testing...'
      }
  }
  ```

### 선택 기준

- **Declarative Pipeline**은 구조화된 접근 방식을 선호하고, 파이프라인의 구성을 명확하게 정의하고 싶을 때 적합합니다. 또한, Jenkins 파이프라인의 새로운 사용자에게 더 친숙할 수 있습니다.
- **Scripted Pipeline**은 복잡한 로직이 필요하거나 조건부 실행, 반복문 등 Groovy의 고급 기능을 사용하고 싶을 때 유용합니다.
