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

### 3. Jenkins에서 GitLab 연동 설정

1. "Jenkins 관리" > "System Configuration" > "System"으로 이동합니다.

2. "GitLab" 섹션을 찾아 GITLAB의 "GitLab connections"에 추가 합니다

   - GitLab host URL

     - 여기서 설정하는 URL은 Jenkins에서 GitLab 프로젝트에 접근하기 위해 사용되는 GitLab 서버의 기본 URL 만을 작성
       (ex.https://lab.ssafy.com )
     - 레포지토리까지의 전체 URL이 아님 -> 해당 URL들은 pipeline 구축할 때 추가로 설정함
     -  **Jenkins는 이 주소를 사용하여 해당 GitLab 서버의 API에 접근하겠다라는 의미**

   - Credentials (API Token for accessing GitLab)

     - GITLAB 의 accesstoken은 2가지가 있음
       - **개인 액세스 토큰(Personal Access Tokens)**: 사용자 계정에 연결되어 있으며, 사용자가 직접 발급 받을 수 있습니다. 이 토큰은 사용자가 설정한 권한에 따라 GitLab 서버 내의 여러 작업을 수행할 수 있게 해줍니다. 예를 들어, 특정 사용자가 발급받은 개인 액세스 토큰에 "api" 권한을 부여했다면, 해당 토큰은 API를 통해 GitLab 내의 레포지토리 정보 조회, 이슈 관리, 웹훅 설정 등의 작업을 수행할 수 있습니다.
       - **프로젝트 또는 그룹 특정 액세스 토큰**: 이는 특정 프로젝트 또는 그룹 내에서만 유효한 토큰으로, 발급받은 토큰은 그 프로젝트 또는 그룹 내에서만 작업을 수행할 수 있는 권한을 부여받습니다

     - 여기에 등록한 Token의 권한 범위에 따라서 Jenkins에 GitLab에 서버와 상호작용 할 수 있는 권한 범위가 부여됨
     - 주의해야 할 점은, Jenkins를 통해 수행하려는 작업이 토큰에 부여된 권한 범위 내에 있는지 확인하는 것
     - 결과적으로 CI/CD 구축을 위해 특정 레포지토리만을 사용할 꺼라면 특정 레포지토리에서 발급받은 accesstoken을 등록해도 무방함

3. "Test Connection"을 클릭하여 연결을 확인합니다.

4. "저장"을 클릭합니다.

### 4. Jenkins Job 생성 및 구성

1. Jenkins 대시보드에서 "새로운 Item"을 선택합니다.
   -  "Item"은 새로운 자동화 작업 또는 프로젝트를 단위를 의미합니다. 이를 통해 소스 코드의 빌드, 테스트, 배포 등의 과정을 자동으로 실행할 수 있는 구성을 만들 수 있음. **각 Item은 고유한 설정과 트리거 조건을 가지며, Jenkins 서버에서 관리되는 작업의 컬렉션 중 하나가 됨.**
2. 프로젝트에 이름을 지정하고, 옵션을 선택한 후 "OK"를 클릭합니다.
   - **Freestyle project**: **초보자나 간단한 빌드 및 배포 과정을 필요할 때 사용.** **GUI를 통해 쉽게 설정할 수 있으며**, 소스 코드 체크아웃, 빌드 스크립트 실행, 결과물 아카이빙 등 다양한 빌드 단계를 추가하고 구성할 수 있습니다. 간단한 프로젝트 또는 Jenkins를 처음 사용하는 경우에 적합합니다. Freestyle 프로젝트는 간단한 빌드 및 배포 과정을 빠르게 설정하고 실행해야 할 때 유용함.
   - **Pipeline**: **복잡한 파이프라인을 구현하고 관리해야 하는 경우 사용. CLI를 통해 설정** 코드 형식으로 빌드, 테스트, 배포 과정을 정의할 수 있는 고급 프로젝트 유형입니다. 'Jenkinsfile'이라는 텍스트 파일에 파이프라인을 코드로 작성하고, 이 파일을 프로젝트 소스 코드와 함께 버전 관리 시스템에 저장합니다. Pipeline은 현대적인 CI/CD 접근 방식과 더 잘 어울리며, 복잡한 파이프라인을 구현하고 관리해야 하는 경우 더 큰 유연성과 확장성을 제공합니다.
   - **Multibranch Pipeline**: 소스 코드 저장소 내의 다양한 브랜치에 대해 자동으로 파이프라인 작업을 생성하고 관리하는 프로젝트 유형입니다. 각 브랜치에 대해 별도의 파이프라인이 자동으로 설정되며, 브랜치가 생성되거나 삭제될 때 해당 파이프라인도 함께 생성되거나 삭제됩니다. 이 유형은 브랜치 기반의 개발 워크플로우를 가진 프로젝트에 적합합니다.
   - **GitHub Organization, GitLab Group 등**: 이러한 유형은 특정 소스 코드 호스팅 서비스의 조직이나 그룹에 속한 모든 레포지토리를 대상으로 파이프라인 작업을 자동으로 생성합니다. 이 옵션은 대규모 프로젝트나 여러 레포지토리를 효율적으로 관리하고자 할 때 유용합니다.
3. 



1. "Build Triggers" 섹션에서 "Build when a change is pushed to GitLab"을 선택합니다. GitLab 웹 훅을 사용합니다.
2. "Pipeline" 섹션에서 "Pipeline script"를 선택하고, GitLab 저장소에서 Jenkinsfile을 불러오거나 파이프라인 스크립트를 직접 입력합니다.
3. "Save"를 클릭합니다.

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



Host URL: 이는 Jenkins에서 GitLab 프로젝트에 접근하기 위해 사용되는 GitLab 서버의 기본 URL입니다. 예를 들어, GitLab.com을 사용하는 경우 https://gitlab.com이 될 수 있고, 자체 호스팅하는 GitLab 인스턴스의 경우 해당 서버의 주소가 됩니다(예: https://gitlab.mycompany.com). 이 URL을 통해 Jenkins는 GitLab API를 호출하여 레포지토리 정보를 가져오거나, 웹훅 설정과 같은 작업을 수행할 수 있습니다.

그러니까 여기에 입력하는 URL은 레포지토리의 URL까지 입력하는게 아니라 기본 URL만 입력하면 되는거가?? 내가 궁금한거는 그러면 내가 설정하는 레포지토리까지는 어떻게 찾아가는건데? 또 추가하는 accessToken은 해당 레포지토리에서 발급 받았는데 기본 URL을 등록하고 accessToken은 레포지토리의 토큰이면 뭔가 어색한거 같아서 물어보는거야