# EC2 Jenkins실행 및 초기 환경 설정

### ※ Jenkins Docker 컨테이너로 실행

### (1) jenkins container 생성 및 실행

```bash
cd /home/ubuntu && mkdir jenkins-data

sudo ufw allow 8080/tcp
sudo ufw reload
sudo ufw status


sudo docker run -d -p 8080:8080 -v /home/ubuntu/jenkins-data:/var/jenkins_home --name jenkins jenkins/jenkins:latest

sudo docker logs jenkins

sudo docker stop jenkins
sudo docker ps -a
```

- `sudo docker run -d -p 8080:8080 -v /home/ubuntu/jenkins-data:/var/jenkins_home --name jenkins jenkins/jenkins:lts`
  - `-d`: 컨테이너를 백그라운드에서 실행하라는 옵션
  - `-p 8080:8080`: 호스트와 컨테이너 간의 포트 매핑을 설정
  - `-v /home/ubuntu/jenkins-data:/var/jenkins_home`: 볼륨을 사용하여 데이터 지속성을 관리하는 옵션
    - **볼륨 마운트**: Docker는 호스트의 `/home/ubuntu/jenkins-data` 디렉토리를 컨테이너의 `/var/jenkins_home` 디렉토리에 마운트합니다. 이 과정에서 두 디렉토리 사이에는 데이터가 실시간으로 공유되는 "동기화" 관계가 형성됩니다.
    - **초기 상태**: 컨테이너가 시작될 때, `/var/jenkins_home`에 존재하는 데이터는 `/home/ubuntu/jenkins-data`의 현재 상태를 반영합니다. 만약 `/home/ubuntu/jenkins-data`가 비어 있다면, `/var/jenkins_home`도 비어 있는 상태로 시작됩니다.
    - **데이터 추가**: Jenkins를 통해 생성되는 모든 설정과 데이터는 `/var/jenkins_home`에 저장됩니다. 이때, 이 디렉토리는 호스트의 `/home/ubuntu/jenkins-data`와 동기화되므로, `/var/jenkins_home`에 추가되는 모든 데이터는 자동으로 `/home/ubuntu/jenkins-data`에도 추가됩니다.
    - **데이터 지속성**: 컨테이너를 재시작하거나 삭제 후 다시 생성하더라도, `/home/ubuntu/jenkins-data`에 저장된 데이터는 유지됩니다. 새로운 컨테이너에서 동일한 볼륨 마운트 설정을 사용하면, `/var/jenkins_home`은 `/home/ubuntu/jenkins-data`의 데이터를 다시 로드하여 이전 상태를 복원합니다.
    - **결과적으로**, `/home/ubuntu/jenkins-data`와 `/var/jenkins_home` 사이에는 양방향 동기화 관계가 형성되며, 이는 데이터의 추가, 수정, 삭제 작업이 어느 한 쪽에서 발생하더라도 다른 한 쪽에도 동일하게 반영되도록 합니다. 이 메커니즘은 Jenkins 설정과 데이터의 안정성 및 지속성을 보장합니다.
  - `--name jenkins`: 실행되는 컨테이너에 `jenkins`라는 이름을 부여
  - `jenkins/jenkins:lts`: 사용할 Docker 이미지

#### (2) 환경 설정 변경 (매우 중요)

**아래는 일부 젠킨스 미러 사이트가 접속되지 않는 현상이 발생하여 Jenkins 서버의 업데이트 센터를 사용자 정의된 소스로 변경하는 코드임**. Jenkins가 플러그인 업데이트 정보나 새로운 플러그인을 다운로드 받을 기본 위치(URL)를 변경하는 작업.

```bash
cd /home/ubuntu/jenkins-data

mkdir update-center-rootCAs

wget https://cdn.jsdelivr.net/gh/lework/jenkins-update-center/rootCA/update-center.crt -O ./update-center-rootCAs/update-center.crt

sudo sed -i 's#https://updates.jenkins.io/update-center.json#https://raw.githubusercontent.com/lework/jenkins-update-center/master/updates/tencent/update-center.json#' ./hudson.model.UpdateCenter.xml

sudo docker restart jenkins
```



> #### 주요 명령어
>
> ```bash
> sudo docker start jenkins
> sudo docker stop jenkins
> sudo docker logs jenkins
> sudo docker logs -f jenkins
> ```



## ※ Jenkins 초기 환경 설정

- Jenkins가 실행 되었다면 웹 브라우저를 열어 `http://도메인:8080`으로 접속
- 터미널에 `sudo docker logs jenkins` 명령어를 검색했을 때 나타나는 초기 패스워드를 입력 
- Install suggested plugins 클릭 하여 기본적으로 추천하는 플러그인 설치
- admin 계정 설정
  - jenkins 환경이 무방비로 노출될 경우 서버의 보안이 취약해질 수 있음
  - admin 패스워드는 반드시 설정 유출되지 않도록 주의
- 외부 접속 URL 설정
  - Jenkins URL - `http://도메인:8080`
- 초기 환경 설정 완료

> 플러그인 설치 과정에서 에러가 있는경우
>
> - 웹 Jenkins로 접속 -> Jenkins 관리 -> Plugins 이동
> - Advance settings에서 "업데이트 사이트 항목"을 찾아 "사이트 경로" 변경
> - 변경 가능한 사이트 경로는 다음 사이트에서 확인 가능
>   - https://github.com/lework/jenkins-update-center