# Jenkins Agent

- 에이전트(Agent)는 Jenkins 마스터 서버에 의해 할당된 빌드, 테스트, 배포 등의 작업을 실행하는 역할을 함. 

- 에이전트를 통해 복잡한 빌드와 배포 프로세스를 여러 컴퓨터에 분산시켜 처리할 수 있으며, 각 컴퓨터는 특정 작업을 위해 최적화될 수 있습니다.

  ### 1. 에이전트의 역할과 중요성
  - **작업 실행**: Jenkins 마스터로부터 할당받은 빌드, 테스트, 배포와 같은 CI/CD 작업을 실행합니다.
  - **병렬 처리 및 분산 빌드**: 여러 에이전트를 동시에 사용함으로써 다수의 작업을 병렬로 처리할 수 있습니다. 이는 빌드 시간을 단축시키고, 리소스 사용을 최적화합니다.
  - **환경 다양성**: 다양한 운영 체제, 소프트웨어 버전, 하드웨어 사양을 가진 에이전트를 구성함으로써, 다양한 환경에서의 빌드와 테스트가 가능해집니다.

  ### 2. 에이전트 구성 방법
  - **자체 관리 에이전트**: 개인이나 조직이 직접 관리하는 서버나 가상 머신에 Jenkins 에이전트 소프트웨어를 설치하여 사용합니다.
  - **클라우드 기반 에이전트**: AWS, Azure 등 클라우드 서비스에서 제공하는 가상 머신을 동적으로 할당받아 에이전트로 사용합니다. 필요에 따라 자동으로 확장하거나 축소할 수 있습니다.
  - **개발자의 개인 컴퓨터**: 임시적인 작업이나 개발 과정에서의 테스트 목적으로 사용될 수 있습니다.

  ### 3. 에이전트 선택과 할당
  - **`agent any`**: 사용 가능한 어떤 에이전트에서든지 작업을 실행할 수 있도록 Jenkins에 지시합니다. 특별한 요구 사항이 없을 때 사용됩니다.
  - **`agent { label 'labelName' }`**: 특정 레이블(예: 'linux', 'high-memory')이 지정된 에이전트에서 작업을 실행하도록 지정합니다. 이를 통해 특정 환경이나 요구 사항을 만족하는 에이전트에서만 작업을 수행할 수 있습니다. (사용하기 전에 jenkins에서 사용할 에이전트를 추가하는 작업이 선행되어야 함)

  ### 4. 구축 및 관리
  - 에이전트는 Jenkins 시스템을 구축하고 관리하는 개인이나 조직에 의해 추가되고 관리됩니다. 분산 빌드 환경을 구축하기 위해, 여러 에이전트를 네트워크에 추가하고 각각을 Jenkins 마스터와 연결해야 합니다.

  
