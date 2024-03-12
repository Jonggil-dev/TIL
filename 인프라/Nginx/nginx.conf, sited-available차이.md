# nginx.conf, sited-available차이

### 1. nginx.conf

- /etx/nginx 디렉토리에 위치한 파일
- `nginx.conf`는 Nginx의 메인 설정 파일로, 전체 서버 설정을 정의합니다. 
- 이 파일에서 다른 설정 파일을 포함시킬 수 있으며, 기본적으로 `sites-enabled` 디렉토리 내의 설정 파일을 포함시키도록 설정되어 있음

### 2. sites-available

- `sites-available` 에는 사용 가능한 모든 사이트의 설정 파일이 저장되며, 이 중 실제로 활성화하고 싶은 사이트의 설정 파일을 `sites-enabled` 디렉토리로 심볼릭 링크합니다. 
- `sites-enabled` 내의 설정은 `nginx.conf`에 의해 자동으로 로드되어 Nginx에 의해 적용됩니다. 이 구조는 설정 관리를 유연하게 해주어, 사이트를 쉽게 활성화하거나 비활성화할 수 있게 해줍니다.



### 3. 요약

- 결국 sites-available/enabled은 nginx.conf에 의해 로드되고 적용되게 됨. 이렇게 하면 설정에 대한 유지보수나 관리가 용이해짐. nginx.conf에 결국 sites-available/enabled의 파일의 코드가 작성된다고 바도 무방 할 듯?