# Jenkins-GitLab Jenkinsfile 설정



### 1. 사용할 Jenkinsfile의 GITLAB 경로 설정

- Pipeline Item의 "Pipeline" 설정 섹션으로 이동
  - Definition : Pipeline script from SCM 선택 (대충 jenkinsfile을 별도로 작성해서 사용하겠다는 의미)
  - SCM : Gi
  - Repository : 사용할 Jenkinsfile가 있는 레포지토리 URL 입력
  - Credentials : **Username with Password 타입을 사용해야 드롭 다운에 인식이 됨 -> Credentials 특이사항.md 파일 참고**
  - Branch Specifier : 사용할 Jenkinsfile가 있는 브랜치명 입력 (ex. */BE-Test/S10P22E102-179)
  - Script Path : 사용할 Jenkinsfile가 있는 디렉토리 주소 입력(ex. Infra/Jenkinsfile)