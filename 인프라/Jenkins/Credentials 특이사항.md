# Credentials 특이사항

- GITLAB API Token 타입으로 등록된 Credentials의 경우 "Jenkins 관리" -> "System" 설정의 GitLab 에서는 드롭다운의 선택 옵션으로 인식이 되나, 다른 부분의 Credentials 설정에서는 드롭다운 선택 옵션으로 출력이 안됨 (원인은 모름)
- **다른 부분에서 GITLAB에서 발급 받은 Access Token을 Credentials로 사용하고 싶다면 Type을 Username with password 타입으로 등록 해야됨** 
  - Username : gitlab에서 사용하는 이름이나 이메일 주소
  - Password : 발급받은 Access Token
  - ID / Description : 내가 식별할 수 있는 ID / Description

