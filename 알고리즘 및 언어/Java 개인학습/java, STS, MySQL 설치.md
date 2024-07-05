# Java(=JDK), STS 설치, MySQL 설치

- **JDK 설치**
  1. azul.com 접속

  2. Download Now 클릭
  3. Java Version, Operating System, Architecture + JDK(Java Package) 선택 후 .msi 파일 다운
  4. 설치 (next 그냥 계속 누르면 됨)

> **※ Python 이든 Java든 언어 자체를 설치하는게 아님, 그냥 해당 언어를 사용하게 해주는 환경을 설치해서 해당 언어로 코드를 작성했을 때 컴퓨터가 인식 할 수 있게 하는거임 (Python은 인터프리터, Java는 JDK가 해당됨)**
>
> - JDK : Java 코드를 사용할 수 있도록(인식하게 해주는) 환경자체를 설치하는 도구라고 생각하면 될듯

- **STS 설치**
  1. spring.io 설치
  2. 상단 Projects 탭의 Spring tool4 클릭
  3. 하단 쯤에  Lokking for Spring Tool Suite 3? 안에 문구에 있는 Spring Tool Suite 3 wiki 클릭(=그냥 강의 중 Java 8 버전을 지원하는 버전을 사용하기 위해 해당 버전 다운하는거임, 다른거 설치하고 싶으면 Spring Tools 4에 있는거 설치해도 될둣?)
  4. 버전에서 첫 번째 url 링크 `64.zip` 링크 다운로드
  5. 다운로드 파일 압축 풀고 sts-버전.RELEASE 폴더만 따로 밖으로 빼기
  6. sts-버전.RELEASE 폴더 켜기
  7. STS.ini 켜고`-Dfile.encoding=UTF-8`마지막 줄에 추가하고 저장 
  8. Spirng 키려면 STS.exe 실행하고 workSpace 폴더(파일 저장소) 선택하면 됨 

> STS :  STS는 스프링 프레임워크를 사용하는 개발자들을 위한 특화된 개발 환경. 스프링은 자바 프로그래밍 언어를 위한 프레임워크이며 STS는 스프링 프레임워크를 사용하여 애플리케이션을 개발할 때 필요한 다양한 도구와 기능을 제공합니다.

- **MySQL 설치**
  1. https://dev.mysql.com/downloads/installer/ 접속
  1. Select Version, Select Operating System 선택
  1. 밑에 있는 MSI Installer 다운로드(용량 더 큰거)
  1. 밑에 No thanks, just start my download. 문구 클릭하면 로그인 없이 다운 가능
  1. 다운받은 파일 실행
  1. next 누르는데 next 활성화 안되면 excute누르고 next 반복
  1. Tpye and Networking에서 아래 사항 확인하기 (기본으로 되어있음)
     - Config Type : Development Computer
     - TCP/IP : 3306
     - Open Windows Firewall ports for network access 체크 표시
  1. Authentication Method에서밑에꺼 선택(Use Legacy Authentication Method)
  1. Accounts and Roles

     - 비밀번호 설정하기 (ssafy로 했음)

     - Add User 클릭
       - User Name 입력 (ssafy로 했음)
       - Host : All Hosts (외부에서도 DB 접근가능)
       - Role : DB Admin
       - 비밀번호 설정(ssafy로 했음)
  1. 이후 finish 뜰 때까지 next 클릭
- **MySQL WorkBench 다운로드 (MySQL 용 IDE 같은거임)**
  1. https://dev.mysql.com/downloads/workbench/ 접속
  2. MSI Installer 다운로드
  3. No thanks, just start my download 클릭
  4. 실행 후 그냥 next 갈기면서 설치
