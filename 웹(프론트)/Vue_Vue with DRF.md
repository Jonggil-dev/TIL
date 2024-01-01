# Vue_Vue with DFR

### 1. DFR 서버에 직접 요청하여 데이터를 응답 받아 store에 저장 후 출력하기

- axios로 Vue →  django 로 요청
  - django server는 문제없이 응답했음 (200 OK)
  - Vue console을 확인해보니 **CORS policy**에 의해 차단됨

- **CORS Policy 관련**
  - SOP(Same-origin policy, 동일 출처 정책)
    - 어떤 출처(Origin)에서 불러온 문서나 스크립트가 다른 출처에서 가져온 리소스와 상호 작용하는 것을 제안하는 보안 방식
    - 웹 애플리케이션의 도메인이 다른 도메인의 리소스에 접근하는 것을 제어하여 사용자의 개인 정보와 데이터의 보안을 보호하고, 잠재적인 보안 위협을 방지
    - 잠재적으로 해로울 수 있는 문서를 분리함으로써 공격 받을 수 있는 경로를 줄임

- Origin(출처)

  - URL의 Protocol, Host, Port를 모두 포함하여 '출처'라고 부름

  - Same Origin 예시

    - 아래 세영역이 일치하는 경우에만 동일 출처(Same-origin)로 인정
![1](https://github.com/JeongJonggil/TIL/assets/139416006/d5e85e6f-7643-4ed2-bb71-2f97dd6d8892)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/2e971504-19de-4362-882c-1c6933e44090)

### 2. CORS (Cross-Origin Resource Sharing, 교차 출처 리소스 공유)

- **CORS Policy : 다른 출처에서 온 리소스를 공유하는 것에 대한 정책**
- **※ 해당 에러가 뜨면 서버에서 처리를 해주어야됨**
- 특정 출처(Origin)에서 실행 중인 웹 애플리케이션이 **다른 출처의 자원에 접근할 수 있는 권한을 부여**하도록 브라우저에 알려주는 체제
- 만약 다른 출처의 리소스를 가져오기 위해서는 **이를 제공하는 서버가 브라우저에게 다른 출처지만 접근해도 된다는 사실을 알려야 함**

![3](https://github.com/JeongJonggil/TIL/assets/139416006/cdb2090e-5eb5-4223-bfad-d10555f6706a)


- **CORS 적용방법**
  - CORS Headers 설정
    - Django에서는 django-cors-headers 라이브러리 활용
    - 손쉽게 응답 객체에 CORS header를 추가해주는 라이브러리
    ![4](https://github.com/JeongJonggil/TIL/assets/139416006/5c33c0be-d850-45e1-99df-513d4f614fad)
    ![5](https://github.com/JeongJonggil/TIL/assets/139416006/74e19285-093f-45c4-9928-a98fca9d9226)
    ![6](https://github.com/JeongJonggil/TIL/assets/139416006/8dc46574-2a05-41d5-a5d1-d1016ef9a7d8)
    ![7](https://github.com/JeongJonggil/TIL/assets/139416006/bc51a3f9-b2de-462f-884b-aff986eb1c49)

### 3. Authentication

- 인증
  - 수신된 요청을 해동 요청의 사용자 또는 자격 증명과 연결하는 메커니즘
  - 누구인지를 확인하는 과정
- 권한
  - 요청에 대한 접근 허용 또는 거부 여부를 결정
- 인증과 권한
  - 인증이 먼저 진행되며 수신 요청을 해당 요청의 서명된 토큰(token)과 같은 일련의 자격 증명과 연결
  - 그런 다음 권한 및 제한정책(throttling polices)은 인증이 완료된 해당 자격 증명을 사용하여 요청을 허용해야 하는지를 결정
- DRF 에서의 인증
  - 인증은 항상 view 함수 시작 시, 권한 및 제한 확인이 발생하기 전, 다른 코드의 진행이 허용되기 전에 실행됨
  - **인증 자체로는 들어오는 요청을 허용하거나 거부할 수 없으며, 단순히 요청에 사용된 자격 증명만 식별한다는 점에 유의**

- **승인되지 않은 응답 및 금지된 응답**
  - HTTP 401 Unauthorized
    - 요청된 리소스에 대한 유효한 인증 자격 증명이 없기 때문에 클라이언트 요청이 완료되지 않았음을 나타냄
  - HTTP 403 Forbidden (Permission Denied)
    - 서버에 요청이 전달되었지만, 권한 때문에 거절되었다는 것을 의미
    - 401과 다른 점은 서버는 클라이언트가 누구인지 알고 있음

### 4. 인증, 권한 설정 (전역 설정, view 함수 별 설정 2가지 방법)

- 전역 설정

  - DEFAULT_AUTHENTICATION_CLASSES 사용

  - ```django
    #settings.py
    REST_FRAMEWORK = {
        # Authentication
        'DEFAULT_AUTHENTICATION_CLASSES': [
            'rest_framework.authentication.TokenAuthentication',
        ]
    ```

- View 함수 별 설정

  - authentication_classes 데코레이터 사용

  - ```django
    permission Decorators
    from rest_framework.decorators import permission_classes
    from rest_framework.permissions import IsAuthenticated
    
    @api_view(['GET', 'POST'])
    @permission_classes([IsAuthenticated])
    def article_list(request):
        if request.method == 'GET':
            articles = get_list_or_404(Article)
            serializer = ArticleListSerializer(articles, many=True)
            return Response(serializer.data)
    ```

- 상세 내용은 교안 참고

