# 웹 서버 간단하게 구동하는 법

- **클라이언트가 EC2서버의 특정 url로 접근했을 때 파일을 제공하기 위해서는 웹 서버가 실행되고 있어야 함**

``` bash
#디렉토리로 이동
cd 디렉토리 주소

#웹서버 실행 
#nohup는 웹 서버를 백그라운드로 실행하기 위함
#1234는 포트 번호
nohup python3 -m http.server 1234 &

#실행되고 있는 웹서버 PID 찾기 (웹 서버 종료를 위해)
ps ax | grep "http.server" | grep -v grep

#웹서버 종료
kill {PID번호}
```







