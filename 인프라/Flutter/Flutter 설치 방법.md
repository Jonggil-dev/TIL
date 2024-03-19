# Flutter 빌드 도구 설치 방법

- jenkins에서 flutter build를 사용하려면 호스트 컴퓨터(EC2 인스턴스) 또는 Jenkins 컨테이너 내부에 flutter가 다운로드 되어 있어야 함
- **여기서는 호스트 컴퓨터(EC2 인스턴스, ubuntu 환경) 내부에 flutter build를 사용하기 위한 도구들을 설치하는 과정을 방법을 설명**



### 플러터 설치 방법 2가지

- **Snap을 통한 설치**: Snap 패키지 매니저는 소프트웨어를 쉽게 설치하고 자동으로 업데이트하는 메커니즘을 제공합니다. 사용자는 단순히 `snap install flutter` 명령어를 실행함으로써 Flutter를 설치할 수 있으며, Snap 시스템이 배포판의 종속성 문제를 처리합니다. 또한 Snap 패키지는 자동으로 업데이트되므로 Flutter의 최신 버전을 유지하기 쉽습니다.
- **직접 다운로드 및 설치**: 사용자는 Flutter의 공식 웹사이트에서 SDK를 직접 다운로드하고, 압축을 풀어 원하는 위치에 설치합니다. 이 방법은 사용자가 설치 경로를 직접 선택할 수 있고, 필요에 따라 특정 버전의 Flutter를 설치할 수 있다는 장점이 있습니다. 하지만, 사용자가 수동으로 업데이트를 확인하고 적용해야 하며, 시스템의 다른 종속성과의 호환성도 스스로 관리해야 합니다.



### Android 앱 빌드에 필요한 도구

Flutter 자체가 Java Development Kit (JDK)에 직접적으로 의존하지는 않음. 그러나 Android 애플리케이션을 빌드하는 과정에서 Android SDK가 사용되며, Android SDK는 Java를 사용합니다. 따라서, **Android 앱을 Flutter로 개발하고 빌드하기 위해서는 JDK가 필요**합니다. 이는 Android 앱을 컴파일하고 실행하는 데 필수적인 도구인 Gradle이 Java를 기반으로 하기 때문입니다.



### Android 앱 빌드를 위한 도구 설치 과정

- 참고 : https://pitching-gap.tistory.com/entry/Flutter-install-flutter-on-linuxubuntu-2004-LTS

- ```bash
  #flutter(snap 패키지) 및 JDK 설치
  sudo snap install flutter --classic
  
  flutter sdk-path
  
  flutter doctor
  
  sudo apt install openjdk-11-jdk
  
  java --version
  
  
  #Android studio 설치
  sudo add-apt-repository ppa:maarten-fonville/android-studio
  
  sudo apt update
  
  sudo apt install android-studio -y
  ```

-  **위 과정 중 unable to find bundled Java version 발생 시**

  - jre파일에 접근하지 못해서 생기는 문제
  - 기존 jbr 폴더의 링크를 jre에 걸어줘 문제 해결 가능
  - **참고 링크 보고 해결하기**