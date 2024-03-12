# EC2 인스턴스 Docker 설치 

### 1. 설치하기 전에 충돌하는 패키지 제거 (이전 버전 제거)

- `docker.io`
- `docker-compose`
- `docker-compose-v2`
- `docker-doc`
- `podman-docker`

충돌하는 모든 패키지를 제거하려면 다음 명령을 실행하십시오.

```bash
for pkg in docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc; do sudo apt-get remove $pkg; done
```

`apt-get`이러한 패키지가 설치되어 있지 않다고 보고할 수도 있습니다.

### 2. 패키지 인덱스 업데이트

시작하기 전에, Ubuntu 패키지 인덱스를 최신 상태로 업데이트해주세요.

```bash
sudo apt-get update
```

### 3. HTTPS를 통해 패키지를 설치할 수 있도록 필요한 패키지 설치

Docker를 설치하기 전에, `apt`가 HTTPS를 통해 패키지를 설치할 수 있도록 몇 가지 패키지를 설치해야 합니다.

```bash
sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
```

### 4. Docker의 공식 GPG 키 추가

Docker 패키지의 정합성을 검증하기 위해, Docker의 공식 GPG 키를 시스템에 추가합니다.

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

### 5. Docker 저장소를 APT 소스에 추가

이제 Docker의 공식 저장소를 시스템의 APT 소스 목록에 추가합니다.

```bash
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
```

### 6. 패키지 인덱스 업데이트

새로운 저장소를 추가한 후, 다시 한번 패키지 인덱스를 업데이트해야 합니다.

```bash
sudo apt-get update
```

### 7. Docker CE(Community Edition) 설치

이제 Docker CE를 설치할 준비가 되었습니다.

```bash
sudo apt-get install docker-ce
```

### 8. Docker가 정상적으로 설치되었는지 확인

Docker가 성공적으로 설치되었는지 확인하기 위해, 다음 명령어를 실행해보세요.

```bash
sudo docker run hello-world
```

이 명령어가 성공적으로 실행되고, "Hello from Docker!" 메시지가 나타난다면, Docker가 정상적으로 설치되고 실행된 것
