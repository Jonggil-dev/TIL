# POSTMAN accessToken 자동설정 되게 하는 법

- 환경변수 이용해서 로그인 시 발급받은 새로운 accessToken 자동으로 설정되게 하는 법

  - login 요청하는 request-> Scripts -> Post-response 에 하기 코드 작성 (옛날 버전은 reqeust -> Test)

    ```java
    if(pm.response.code == 200) {
        pm.environment.set('accessToken', pm.response.json().data.access_token);
        pm.environment.set('refreshToken', pm.response.json().data.refresh_token);
    }
    ```

- 좌측 환경변수 설정하는 Environments 설정 탭에 들어가서 "+" 누르고 Env 생성하고 저장하기

  - variable : accessToken, Type : default 설정

  - variable : refreshToken, Type : default 설정

- 다시 요청으로 돌아와서 우측 상단에 `No environment ->  Env`로 변경 (이렇게 해야 요청에 Env 적용됨)

- 보낼 요청의 Authorization 탭에 들어가서 Type: Bearer Token 설정하고 Token 값에 {{accessToken}} 설정

- 다시 로그인 한 번 하기

  -  로그인을 하면서 새로 발급받은 response Body의 access_token값이 Env의 accessToken 변수에 설정 됨
  - {{accessToken}}은 그 값을 불러와서 Authorization 탭의 Token값에 accessToken 값을 적용해버리는 거임