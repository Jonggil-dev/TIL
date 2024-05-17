# 도커 컨테이너 내부에서 Docker 명령어 사용하기

- 기본적으로 실행한 도커 컨테이너 내부에 Docker가 설치되어 있지 않으면 Docker 명령어를 사용 못함

- 도커 컨테이너 내부에서 Docker 명령어 사용이 필요한 경우 해결 방법 2가지**(ex. Jenkins 컨테이너)**

  1. 도커 컨테이너 내부에 Docker를 설치해서 사용하기

  2. 도커 컨테이너를 실행할 때 아래 2가지 볼륨 마운트 해서 사용하기

     - ```bash
       -v /var/run/docker.sock:/var/run/docker.sock \
       -v $(which docker):/usr/bin/docker \
       ```
       
       - `/var/run/docker.sock` : 컨테이너가 호스트(EC2)의 도커 데몬과 통신할 수 있도록 하는 볼륨 마운트
       
       - `$(which docker)` : Docker CLI를 사용할 수 있게 하는 볼륨 마운트. 즉, `docker` 명령어를 사용할 수 있도록 함
       - **정리하면, Docker CLI를 통해 docker 명령어를 호스트의 도커 데몬에게 보내야 docker 명령어가 사용 가능한거임**


