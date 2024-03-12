# Jenkins-GitLab webhook 설정

### 1. GitLab Personal Access Token 생성

1. GitLab에서 사용자 아이콘을 클릭하고, "설정"을 선택합니다.
2. "Access Tokens" 탭으로 이동합니다.
3. 새 토큰에 이름을 지정하고, 만료 날짜를 선택합니다(선택 사항).
4. 필요한 권한을 선택합니다. Jenkins에서는 주로 `api` 권한이 필요합니다.
5. "Create personal access token"을 클릭합니다.
6. 생성된 토큰을 안전한 곳에 복사해 두세요. 페이지를 벗어나면 다시 볼 수 없습니다.

### 2. Jenkins에 GitLab 플러그인 설치

1. Jenkins 대시보드로 이동합니다.
2. "Jenkins 관리" > "Plugins"로 이동합니다.
3. "Available Plugins" 탭에서 "GitLab Plugin"을 찾아 설치합니다.
4. 필요하다면 Jenkins를 재시작합니다.

### 3. Jenkins에서 GitLab 연동 설정 (여기서 다시 시작하기 system에 gitlab 추가하는게 무슨 역할인지?)

1. "Jenkins 관리" > "System Configuration" > "System"으로 이동합니다.
2. "GitLab" 섹션을 찾아 gitLab"GitLab connections"에 추가 합니다
3. "Credentials" 옆의 "Add" 버튼을 클릭하고, "Jenkins"를 선택합니다.
4. "Kind"에서 "GitLab API token"을 선택합니다.
5. 복사해 둔 Personal Access Token을 입력합니다.
6. "Test Connection"을 클릭하여 연결을 확인합니다.
7. "Save"를 클릭합니다.

### 4. Jenkins Job 생성 및 구성

1. Jenkins 대시보드에서 "New Item"을 선택합니다.
2. 프로젝트에 이름을 지정하고, "Pipeline"을 선택한 후 "OK"를 클릭합니다.
3. "Build Triggers" 섹션에서 "Build when a change is pushed to GitLab"을 선택합니다. GitLab 웹 훅을 사용합니다.
4. "Pipeline" 섹션에서 "Pipeline script"를 선택하고, GitLab 저장소에서 Jenkinsfile을 불러오거나 파이프라인 스크립트를 직접 입력합니다.
5. "Save"를 클릭합니다.

### 5. GitLab에서 Webhook 설정

1. GitLab에서 Jenkins와 연동할 프로젝트로 이동합니다.
2. "Settings" > "Webhooks"으로 이동합니다.
3. "URL" 필드에 Jenkins URL을 입력하고, "/project/이름"을 추가합니다(예: `http://your-jenkins-url/project/your-project-name`).
4. "Secret Token"은 비워둘 수 있습니다(또는 Jenkins에서 설정한 대로 입력).
5. "Push events"를 체크하고, "Add webhook"을 클릭합니다.

### 6. 테스트 및 확인

- GitLab 저장소에 변경사항을 `push`하여 Jenkins에서 빌드가 자동으로 시작되는지 확인합니다.
- Jenkins 대시보드에서 빌드 상태를 확인할 수 있습니다.

위 단계를 따라서 GitLab 저장소의 `master` 브랜치에 대한 변경사항을 Jenkins에서 자동으로 감지하고 빌드를 실행하는 파이프라인을 구축할 수 있습니다. Jenkinsfile이나 파이프라인 스크립트는 프로젝트의 요구사항에 맞게 커스터마이징해야 합니다.