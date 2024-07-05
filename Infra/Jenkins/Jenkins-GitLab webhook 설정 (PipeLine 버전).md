# Jenkins-GitLab webhook 설정 (PipeLine 버전)

### - 1~4번 참고 : https://as-i-am-programing.tistory.com/32

### - 5~6번 참고: https://mrgamza.tistory.com/808

### 1. GitLab Personal Access Token 생성

1. GitLab에서 사용자 아이콘을 클릭하고, "설정 (or Edit Profile)"을 선택합니다.
2. "Access Tokens" 탭으로 이동합니다.
3. 새 토큰에 이름을 지정하고, 만료 날짜를 선택합니다(선택 사항).
4. 필요한 권한을 선택합니다. Jenkins에서는 주로 `api` 권한이 필요합니다.
5. "Create personal access token"을 클릭합니다.
6. 생성된 토큰을 안전한 곳에 복사해 두세요. 페이지를 벗어나면 다시 볼 수 없습니다.

### 2. Jenkins에 GitLab 플러그인 설치

1. Jenkins 대시보드로 이동합니다.
2. "Jenkins 관리" > "Plugins"로 이동합니다.
3. "Available Plugins" 탭에서 "GitLab Plugin"을 찾아 설치합니다.
4. Jenkins를 재시작합니다(`docker restart jenkins`)

### 3. Jenkins에 Personal Access Token 등록

1. Jenkins 대시보드로 이동합니다.
2. "Jenkins 관리" > "Credentials"로 이동합니다.
3. "Domains"의 "global"을 클릭하고 "Add Credentials"를 클릭합니다
   - Kind : GitLab API Token
   - Scope : Global
   - API token : gitlab에서 받은 PAT
   - ID / Description : 내가 식별할 수 있는 ID로 등록

### 4. Jenkins - GitLab 서버 레벨 수준 연동 설정

- **이 설정은 Jenkins에서 GitLab으로 API 요청을 보낼 때 필요한 설정임. 즉, GitLab -> Jenkins로의 Webhook 통신은 이 설정과는 무관하게 동작함 (GitLab에서 webhook test할 때 이 설정이 되어 있지 않아도 정상 동작 함)**

1. "Jenkins 관리" > "System Configuration" > "System"으로 이동합니다.

2. "GitLab" 섹션을 찾아 GITLAB의 "GitLab connections"에 추가 합니다

   - GitLab host URL

     - 여기서 설정하는 URL은 Jenkins에서 GitLab 프로젝트에 접근하기 위해 사용되는 GitLab 서버의 기본 URL 만을 작성
       (ex.https://lab.ssafy.com )
     - 레포지토리까지의 전체 URL이 아님 -> 해당 URL들은 pipeline 구축할 때 추가로 설정함
     -  **Jenkins는 이 주소를 사용하여 해당 GitLab 서버의 API에 접근하겠다라는 의미**

   - Credentials (API Token for accessing GitLab)

     - 위에서 등록한 Personal Access Token 선택

     > 참고
     >
     > - GITLAB 의 accesstoken은 2가지가 있음
     >   - **개인 액세스 토큰(Personal Access Tokens)**: 사용자 계정에 연결되어 있으며, 사용자가 직접 발급 받을 수 있습니다. 이 토큰은 사용자가 설정한 권한에 따라 GitLab 서버 내의 여러 작업을 수행할 수 있게 해줍니다. 예를 들어, 특정 사용자가 발급받은 개인 액세스 토큰에 "api" 권한을 부여했다면, 해당 토큰은 API를 통해 GitLab 내의 레포지토리 정보 조회, 이슈 관리, 웹훅 설정 등의 작업을 수행할 수 있습니다.
     >   - **프로젝트 또는 그룹 특정 액세스 토큰**: 이는 특정 프로젝트 또는 그룹 내에서만 유효한 토큰으로, 발급받은 토큰은 그 프로젝트 또는 그룹 내에서만 작업을 수행할 수 있는 권한을 부여받습니다
     > - 여기에 등록한 Token의 권한 범위에 따라서 Jenkins에 GitLab에 서버와 상호작용 할 수 있는 권한 범위가 부여됨
     > - 주의해야 할 점은, Jenkins를 통해 수행하려는 작업이 토큰에 부여된 권한 범위 내에 있는지 확인하는 것
     > - 결과적으로 CI/CD 구축을 위해 특정 레포지토리만을 사용할 꺼라면 특정 레포지토리에서 발급받은 accesstoken을 등록해도 무방함

3. "Test Connection"을 클릭하여 "success"를 확인합니다.

4. "저장"을 클릭합니다.

### 5. Jenkins Job(Pipeline)생성 및 구성

1. Jenkins 대시보드에서 "새로운 Item"을 선택합니다.
   -  "Item"은 새로운 자동화 작업 또는 프로젝트를 단위를 의미합니다. 이를 통해 소스 코드의 빌드, 테스트, 배포 등의 과정을 자동으로 실행할 수 있는 구성을 만들 수 있음. **각 Item은 고유한 설정과 트리거 조건을 가지며, Jenkins 서버에서 관리되는 작업의 컬렉션 중 하나가 됨.**
2. 프로젝트에 이름을 지정하고, **PipeLine**선택한 후 "OK"를 클릭합니다.
   - **Freestyle project**: **초보자나 간단한 빌드 및 배포 과정을 필요할 때 사용.** **GUI를 통해 쉽게 설정할 수 있으며**, 소스 코드 체크아웃, 빌드 스크립트 실행, 결과물 아카이빙 등 다양한 빌드 단계를 추가하고 구성할 수 있습니다. 간단한 프로젝트 또는 Jenkins를 처음 사용하는 경우에 적합합니다. Freestyle 프로젝트는 간단한 빌드 및 배포 과정을 빠르게 설정하고 실행해야 할 때 유용함.
   - **Pipeline**: **복잡한 파이프라인을 구현하고 관리해야 하는 경우 사용. CLI를 통해 설정** 코드 형식으로 빌드, 테스트, 배포 과정을 정의할 수 있는 고급 프로젝트 유형입니다. 'Jenkinsfile'이라는 텍스트 파일에 파이프라인을 코드로 작성하고, 이 파일을 프로젝트 소스 코드와 함께 버전 관리 시스템에 저장합니다. Pipeline은 현대적인 CI/CD 접근 방식과 더 잘 어울리며, 복잡한 파이프라인을 구현하고 관리해야 하는 경우 더 큰 유연성과 확장성을 제공합니다.
   - **Multibranch Pipeline**: 소스 코드 저장소 내의 다양한 브랜치에 대해 자동으로 파이프라인 작업을 생성하고 관리하는 프로젝트 유형입니다. 각 브랜치에 대해 별도의 파이프라인이 자동으로 설정되며, 브랜치가 생성되거나 삭제될 때 해당 파이프라인도 함께 생성되거나 삭제됩니다. 이 유형은 브랜치 기반의 개발 워크플로우를 가진 프로젝트에 적합합니다.
3. "OK"를 클릭
4. "General" tab의 "Build Triggers" 섹션으로 이동
5. Build when a change is pushed to GitLab 선택
   - 옵션으로 아래 사항 선택 (본인 프로젝트 의도에 맞게 설정하기)
     - Push Events
     - Accepted Merge Request Events
     - Approved Merge Requests
     - Comments 
   - "고급 누르고 Secret token 밑에 Generate 버튼을 눌러서 Secret token 생성 
     ("고급" 부분 옵션들은 기본으로 된 설정 외에는 건들지 말고 나중에 필요 시 수정하기)

### 6. GitLab에서 Webhook 설정 및 테스트

1. GitLab에서 Jenkins와 연동할 프로젝트로 이동합니다.
2. "Settings" > "Webhooks"으로 이동해서 "Add new webhook" 클릭
3. "URL" 필드에 5-5의Build when a change is pushed to GitLab 체크박스 옆에 있던 URL 입력
4. "Secret Token"에 5-5에서 생성한 "Secret Token" 입력
5. "Push events"를 체크
   - **특정 브랜치에 대해서만 webhook을 발생 시키려면 `Wildcard pattern, Regular expression` 옵션 활용하기**
6. , "Add webhook"을 클릭합니다.
7. 생성된 Project Hooks의 Test -> Push events를 눌렀을 때 상단에 "Hook executed successfully: HTTP 200" 라고 뜨면 설정 완료 된거임
