# Nginx Config 작성

- nginx에 관한 설정 파일을 작성하는 방법은 3가지 방법이 있음

  - **결론적으로 nginx.conf파일에 sites-available의 설정 코드들이 올라가서 동작하므로 아래 3개 중에 1가지 방법 선택**
  - `/etc/nginx/nginx.conf` 를 수정
  - `/etc/nginx/sites-available`의 default를 수정
  - `/etc/nginx/sites-available`에 설정 파일을 새로 작성 후 저장

  