# Flutter 빌드 도구 설치 방법

- jenkins에서 flutter build를 사용하려면 호스트 컴퓨터(EC2 인스턴스) 또는 Jenkins 컨테이너 내부에 flutter가 다운로드 되어 있어야 함
- **여기서는 호스트 컴퓨터(EC2 인스턴스, ubuntu 환경) 내부에 flutter build를 사용하기 위한 도구들을 설치하는 과정을 방법을 설명**



### Android 앱 빌드에 필요한 도구

- Flutter 자체가 Java Development Kit (JDK)에 직접적으로 의존하지는 않음. 그러나 Android 애플리케이션을 빌드하는 과정에서 Android SDK가 사용되며, Android SDK는 Java를 사용합니다. 따라서, **Android 앱을 Flutter로 개발하고 빌드하기 위해서는 JDK가 필요**합니다. 이는 Android 앱을 컴파일하고 실행하는 데 필수적인 도구인 Gradle이 Java를 기반으로 하기 때문입니다.



## 플러터 설치 방법 2가지

### 1. 직접 다운로드 및 설치

- 사용자는 Flutter의 공식 웹사이트에서 SDK를 직접 다운로드하고, 압축을 풀어 원하는 위치에 설치합니다. 이 방법은 사용자가 설치 경로를 직접 선택할 수 있고, 필요에 따라 특정 버전의 Flutter를 설치할 수 있다는 장점이 있습니다. 하지만, 사용자가 수동으로 업데이트를 확인하고 적용해야 하며, 시스템의 다른 종속성과의 호환성도 스스로 관리해야 합니다.

- 설치 과정

  - 참고 : https://digging-on-bytes.com/android-sdk-%EC%84%A4%EC%B9%98-ubuntu%EC%97%90%EC%84%9C-command-line%EC%9C%BC%EB%A1%9C-%EC%84%A4%EC%B9%98%ED%95%98%EA%B8%B0/

  ```bash
  # flutter 설치
  cd ~
  git clone https://github.com/flutter/flutter.git -b stable
  
  
  # flutter 환경변수 등록
  nano ~/.bashrc
  #맨 아랫줄에 밑에 코드 작성 후 종료
  export PATH="$PATH:$HOME/flutter/bin"
  # 원래는 source ~/.bashrc 이렇게 환경변수 반영하는 코드 잇는데 잘 안먹힘 그냥 껏다키는게 나음
  ssh exit 후 재접속하기
  
  # flutter 종속성 확인
  flutter doctor
  
  # JDK 다운
  sudo apt install openjdk-17-jdk
  java --version
  
  #Android Studio CLI Manager설치
  #빌드 할 때 사용할 SDK만 필요하니까 "공식 홈페이지의 명령줄 도구"만 다운로드 받고 이후 명령어 이용해서 SDK 다운받음
  #명령줄 도구 다운로드 url는 다운로드 공식 홈페이지 가서 다운로드 누르는 버튼 우클릭하고 링크 주소 복사 하면 됨
  
  #파일 다운로드 받을 폴더로 이동
  mkdir android-studio
  cd android-studio/
  curl -O https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip?hl=ko
  
  #다운로드 받은 파일명에 query가 있어서 압축해제가 안되기 때문에 파일명 변경
  mv 'commandlinetools-linux-11076708_latest.zip?hl=ko' commandlinetools-linux-11076708_latest.zip
  
  #압축해제
  unzip commandlinetools-linux-11076708_latest.zip
  
  #cmdline-tools로 이동 후 tools 폴더 만들고 cmdline-tools있던 파일,폴더 전부 tools로 옮기기
  #왜 이렇게 하는건지 이유는 잘 모르겟는데 이렇게 안하면 나중에 환경변수 인식이 안됨 (SDK_ROOT 무슨 에러 뜸)
  cd cmdline-tools/
  mkdir tools
  mv bin lib NOTICE.txt source.properties tools/
  
  #명령어를 사용하기 위한 환경변수 추가
  nano ~/.bashrc
  
  #.bashrc 파일 맨 마지막에 아래 코드 추가 후 종료
  export ANDROID_SDK_ROOT="/home/ubuntu/android-studio/"
  export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/tools/bin"
  
  # 원래는 source ~/.bashrc 이렇게 환경변수 반영하는 코드 잇는데 잘 안먹힘 그냥 껏다키는게 나음
  ssh exit 후 재접속하기
  
  #Android Sdk 설치
  sdkmanager "platform-tools" "platforms;android-31"
  sdkmanager "build-tools;31.0.0"
  ```





