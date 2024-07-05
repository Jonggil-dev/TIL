# EC2 SSH 명령어 부분 색상 입히기

- 명령어랑 출력문이랑 생상이 동일해서 구분이 안됨 -> 명령어 부분 색깔 입혀서 구분되게 하기
- SSH 접속 후 `sudo nano ~/.bashrc` 입력
- `#force_color_prompt=yes` 이 부분 주석 해제하기
- `source ~/.bashrc` 입력