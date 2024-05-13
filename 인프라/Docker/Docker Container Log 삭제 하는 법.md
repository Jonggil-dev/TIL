# Docker Container Log 삭제 하는 법

```bash
sudo su # 명령어로 루트 계정 생성,
cd /var/lib/docker/containers/<container-id>/  # 해당 도커 컨테이너 디렉토리로 이동
sudo rm <container-id>-json.log # 로그파일 삭제

# 파일 삭제돼도 로그 파일은 알아서 다시 자동 생성됨
```