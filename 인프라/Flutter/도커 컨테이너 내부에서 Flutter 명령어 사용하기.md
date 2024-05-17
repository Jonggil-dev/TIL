# 도커 컨테이너 내부에서 Flutter 명령어 사용하기

- Flutter 설치 방법 TIL을 참고해 EC2 인스턴스에 Flutter SDK, Andriod-Studio SDK 설치하고 환경변수 추가하기

- 기본적으로 실행한 도커 컨테이너 내부에 Flutter나 Android-Studio가 없으면 관련 명령어들을 사용 못함

- 도커 컨테이너 내부에서 Flutter 명령어 사용이 필요한 경우 해결 방법 2가지**(ex. Jenkins 컨테이너)**

  1. 도커 컨테이너 내부에 Flutter 명령어 관련 프로세스들을 설치해서 사용하기
  
  2. 도커 컨테이너를 실행할 때 아래 2가지 볼륨 마운트해서 사용하기(Flutter SDK와 Android SDK 명령어 관련 디렉토리)
  
     - ```bash
       -v /home/ubuntu/flutter:/var/flutter \
       -v /home/ubuntu/android-studio:/home/ubuntu/android-studio \
       ```
  
  

