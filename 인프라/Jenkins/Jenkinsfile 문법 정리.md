# 1. Jenkinsfile 문법 정리

### 1. `dir`과 `cd`의 차이

- **`cd` (Change Directory)**

    - `cd` 명령은 주로 스크립트의 한 부분으로서 사용되며, 명령이 실행되는 동안만 디렉토리 변경이 유지됩니다.


    ```groovy
    sh 'cd someDirectory && ./doSomething'
    ```

    이 경우, `cd someDirectory`는 새 디렉토리로 이동한 후 `./doSomething` 명령을 실행하고, 명령 실행이 끝나면 원래 디렉토리로 돌아오지 않습니다. 즉, 해당 명령 라인이 실행될 때만 디렉토리 변경이 적용됩니다.

- **`dir` (Directory Block)**

    - 특정 블록 내의 모든 작업이 지정된 디렉토리 내에서 실행되도록 합니다.

    ```groovy
    dir('someDirectory') {
        sh './doSomething'
    }
    ```

    `dir` 스텝을 사용하면 해당 블록 내의 모든 명령이 `someDirectory` 내에서 실행되며, 블록이 종료되면 자동으로 원래의 작업 디렉토리로 돌아옵니다.


### 2. echo

- `echo`는 Jenkins Pipeline에서 로그나 콘솔 출력에 메시지를 표시하는 데 사용되는 기본 스텝

- `echo` 스텝은 주로 `steps` 블록 내에서 사용되며, 문자열 인자를 받아 Jenkins 콘솔 로그에 해당 문자열을 출력합니다.

```groovy
pipeline {
    agent any
    stages {
        stage('Example') {
            steps {
                echo 'Hello, Jenkins Pipeline!'
            }
        }
    }
}
```

위 파이프라인 스크립트 예제에서 `echo` 스텝은 "Hello, Jenkins Pipeline!" 메시지를 출력합니다. 이는 파이프라인이 해당 단계에 도달했음을 알리고, 특정 정보를 로그에 기록하는 간단한 방법으로 사용됩니다.
