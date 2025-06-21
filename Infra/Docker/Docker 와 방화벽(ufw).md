# Docker 와 방화벽(ufw)

- **도커의 네트워크 관리**
  - 도커는 iptables 규칙을 직접 조작하여 컨테이너의 포트를 호스트의 포트에 연결합니다. 이는 도커가 실행될 때 자동으로 이루어지며, 도커 엔진이 관리합니다.
- **UFW와 iptables**

  - UFW는 사용자 친화적인 방화벽 설정 도구이며, 실제로는 iptables 규칙을 관리합니다.

  - UFW로 설정한 규칙은 iptables의 규칙으로 변환되어 시스템에 적용됩니다.

  - 하지만 UFW는 도커가 iptables에 직접 추가하는 규칙을 알지 못하고, 이 규칙들은 UFW의 설정보다 우선 적용될 수 있습니다.
- **UFW와 도커**

  -  UFW와 도커 간에는 iptables 규칙을 동기화하는 기본 메커니즘이 없습니다. 이로 인해 UFW 설정이 iptables에 의해 우회될 수 있음
  -  **도커가 iptables 규칙을 자동으로 조작하지 않도록 하는 방법** 
     -  `/etc/docker/daemon.json` 파일에(없으면 생성)  { "iptables": false } 를 추가 후 `sudo systemctl restart docker`

