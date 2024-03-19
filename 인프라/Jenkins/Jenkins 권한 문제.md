# Jenkins 권한 문제 

### !!jenkins의 동작 관련해서 권한으로 발생하는 에러는 사실 컨테이너를 실행 할 때 root계정으로 실행하면 해결되긴 함. 보안과 관련해서 조금 더 신경이 쓰이면 밑에 처럼 부분적으로 해결하기!!

- ```bash
  sudo docker run -d -p 8080:8080 \
  -u root \
  -v /home/ubuntu/jenkins-data:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v $(which docker):/usr/bin/docker \
  --name jenkins jenkins/jenkins:latest
  ```

<hr/>

### 1. jenkins 사용 계정의 파일 수정 권한 문제

- **문제점**
  - Jenkins Credentials에 저장된 properties 파일을 spring 프로젝트의 resources 경로에 복사(cp)하는 과정에서 권한 에러가 발생

  - **EC2 인스턴스 내에서 Jenkins가 파일을 수정하는 권한이 없어서 발생하는 것임**

- **해결 방법**
  - 필요한 폴더나 파일에 대해서만 특정 권한을 부여하기
    - `chomd 명령어` 사용
  - Jenkins 자체에 Root 권한을 부여하는 방법
    - 인터넷 참고

### 2. jenkins 사용자 계정의 docker 데몬 접근 권한 문제

- 참고자료 : https://sftth.github.io/cicd/cicd-jenkins001/

- **문제점**

  - jenkins 컨테이너 내부에서는 jenkins 계정으로 작업을 수행하는데 jenkins 계정은 docker 명령을 수행할 권한이 없음
  - jenkins 계정으로 docker를 수행할 수 없는 이유는 /var/run/docker.sock 이 root 계정에만 실행 권한을 갖기 때문

- **해결 방법**

  - ```bash
    # Host에서 입력
    $ docker exec -it -u root ${jenkins-container-name} /bin/bash
    
    #컨테이너 내부(root) 에서 입력
    $ chown jenkins:jenkins /var/run/docker.sock
    ```