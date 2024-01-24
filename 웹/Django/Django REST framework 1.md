# Django REST framework 1

### 1. REST API

- API(Application Programming Interface) 
  - 애플리케이션과 프로그래밍으로 소통하는 방법
  - 클라이언트-서버처럼 서로 다른 프로그램에서 요청과 응답을 받을 수 있도록 만든 체계

- REST(Representational State Transfer)

  - API Server를 개발하기 위한 일종의 소프트웨어 설계 방법론 "약속(규칙X)"

  - REST 원리를 따르는 시스템을 RESTful 하다고 부름

  - **"자원을 정의"**하고 **"자원에 대한 주소를 지정"**하는 전반적인 방법을 서술

    > "각각 API구조를 작성하는 모습이 너무 다르니 약속을 만들어서 다같이 통일해서 쓰자!"

- REST API
  - REST라는 설계 디자인 약속을 지켜 구현한 API

![1](https://github.com/JeongJonggil/TIL/assets/139416006/8d668953-f1ee-41c4-b530-ac565566bcc5)




### 2. 자원의 식별

- URI(Uniform Resource Identifier, 통합 자원 식별자)

  - 인터넷에서 리소스(자원)를 식별하는 문자열
  - 가장 일반적인 URI는 웹 주소로 알려진 URL (URL보다 조금 더 큰 범주를 나타내는 개념)

- URL(uniform Resource Locator, 통합 자원 위치)

  - 웹에서 주어진 리소스의 주소

  - 네트워크 상에 리소스가 어디 있는지를 알려주기 위한 약속

![2](https://github.com/JeongJonggil/TIL/assets/139416006/15ca4a78-1a98-43b1-a41b-158a22a7dd07)


  - > 1. Schema (or Protocol)
    >    - 브라우저가 리소스를 요청하는데 사용해야 하는 규약
    >    - URL의 첫 부분은 브라우저가 어떤 규악을 사용하는지를 나타냄
    >    - 기본적으로 웹은 HTTP(S)를 요구하며 메일을 열기위한 mailto:, 파일을 전송하기 위한 ftp: 등 다른 프로토콜도 존재
    > 2. Domain Name
    >    - 요청 중인 웹 서버를 나타냄
    >    - 어떤 웹 서버가 요구되는 지를 가리키며 직접 IP 주소를 사용하는 것도 가능하지만, 사람이 외우기 어렵기 때문에 주로 Domain Name으로 사용
    >    - 예를 들어 도메인 google.com의 IP 주소는 142.251.42.142
    > 3. Port
    >    - 웹 서버의 리소스에 접근하는데 사용되는 기술적인 문(Gate)
    >    - HTTP 프로토콜의 포준 포트
    >    - 표준 포트만 생략 가능
    > 4. Path
    >    - 웹 서버의 리소스 경로
    >    - 초기에는 실제 파일이 위치한 물리적 위치를 나타냈지만, 오늘날은 실제 위치가 아닌 추상화된 형태의 구조를 표현
    >    - 예를 들어 /articles/create는 실제 articles 폴더 안에 create 폴더안을 나타내는 것은 아님
    > 5. Parameters
    >    - 웹 서버에 제공하는 추가적인 데이터
    >    - '&' 기호로 구분되는 key-value 쌍 목록
    >    - 서버는 리소스를 응답하기 전에 이러한 파라미터를 사용하여 추가 작업을 수행할 수 있음
    > 6. Anchor
    >    - 일종의 "북마크"를 나타내며 브라우저에 해당 지점에 있는 콘텐츠를 표시
    >    - 즉, 사용자에게 페이지 안에서도 특정 위치를 가리키기 위한 추가 수단
    >    - fragment identifier(부분 식별자)라고 부르는 '#'이후 부분은 서버에 전송되지 않음

### 3. 자원의 행위

- HTTP Request Methods
  - 리소스에 대한 행위(수행하고자 하는 동작)를 정의
  - HTTP verbs 라고도 함
  - GET, POST, PUT, DELETE 등
    - GET : 서버에 리소스의 표현을 요청, GET을 사용하는 요청은 데이터만 검색해야 함
    - POST : 데이터를 지정된 리소스에 제출, 서버의 상태를 변경
    - PUT : 요청한 주소의 리소스를 수정
    - DELETE : 지정된 리소스를 삭제

- HTTP response status codes
  - 특정 HTTP 요청이 성공적으로 완료 되었는지 여부를 나타냄
  - ex)  404,405 error 같은 것들



### 4. 자원의 표현

- 지금까지 Django 서버는 사용자에게 페이지(html)만 응답하고 있었음
- 하지만 서버가 응답할 수 있는 것은 페이지 뿌난 아니라 다양한 데이터 타입을 응답할 수 있음
- REST API는 이 중에서도 **JSON** 타입으로 응답하는 것을 권장

![1](https://github.com/JeongJonggil/TIL/assets/139416006/2b547134-4ac0-4bfb-8010-5066ead47101)
![2](https://github.com/JeongJonggil/TIL/assets/139416006/9147e4e9-5a88-45f6-9139-ed678c59421f)
![3](https://github.com/JeongJonggil/TIL/assets/139416006/6faf2541-37fb-4ed2-961c-3b775a8cd27f)
![4](https://github.com/JeongJonggil/TIL/assets/139416006/c916ca89-99f8-4a79-b4bd-4b81a1e19f55)



### 5. DRF(Django REST Framework)

- Django에서 Restful API 서버를 쉽게 구축할 수 있도록 도와주는 오픈소스 라이브러리



### 6. Serialization(직렬화)

- 여러 시스템에서 활요하기 위해 데이터 구조나 객체 상태를 나중에 재구성할 수 있는 포맷으로 변환하는 과정

![5](https://github.com/JeongJonggil/TIL/assets/139416006/818a1c2b-5266-43a0-85ec-6aa4ded7f55b)

### 7. DRF with Single Model

- POSTMAN 설치

![6](https://github.com/JeongJonggil/TIL/assets/139416006/4390da4e-a048-4104-bb5c-0f4e2d9d3865)
![7](https://github.com/JeongJonggil/TIL/assets/139416006/85cb3ac3-623c-4213-a1f2-058388480249)
![8](https://github.com/JeongJonggil/TIL/assets/139416006/09fae8c3-df80-4376-8fa6-87847a883066)

- URL및 HTTP requests methods 설계

![9](https://github.com/JeongJonggil/TIL/assets/139416006/21cb233f-75fa-427f-b66f-d14c34bdec0d)

- 'api_view' decorator

  - DRF view 함수에서는 **필수로 작성**되며 view 함수를 실행하기 전 HTTP 메서드를 확인
  - 매개변수가 없이 디폴트 값으로는 GET 메서드만 허용되며 다른 메서드 요청에 대해서는 405 Method Not Allowed로 응답
  - DRF view 함수가 응답해야 하는 HTTP 메서드 목록을 작성

- GET 

  - ModelSerializer : Django 모델과 연결된 Serializer 변환기

    ```python
    ex)
    #views,py
    
    @api_view(['GET'])	#'Get' 요청만 아래 함수를 통과할 수 있다는 데코레이터, 매개변수 없이 그냥 ()만 쓰면 디폴트가 Get이긴 한데 명시해주는게 좋음. serializer를 쓸 대는 꼭 @api_view 데코레이터를 써줘야됨
    def article_list(request):
    	articles = Article.objects.all()
    	serializer = ArticleListSerializer(articles, many=True)	#첫번째 인자가 복수 쿼리셋이면 many=True 적어줘야됨, 단수이면 안적어도 됨
    	return Response(serializer.data)
    ```
  

![10](https://github.com/JeongJonggil/TIL/assets/139416006/90b20beb-0ca2-4ebc-bf22-6524943de39a)
![11](https://github.com/JeongJonggil/TIL/assets/139416006/d17909f1-e0f7-4256-9aa9-55bf47c25bb8)
![12](https://github.com/JeongJonggil/TIL/assets/139416006/7137cdc0-f4a8-48e9-bbb2-f511d3c017c4)
![13](https://github.com/JeongJonggil/TIL/assets/139416006/fc908e72-5a14-43db-8599-7af62d2d367f)


- POST
  - 하기예시 url은 path('article/',views.article_list)

![14](https://github.com/JeongJonggil/TIL/assets/139416006/f5807569-5b70-4aab-81e5-f1bea1dd5027)
![15](https://github.com/JeongJonggil/TIL/assets/139416006/aa5bfd50-369f-4cab-90aa-c47d94d6ae86)


- DELETE
  - 위의 GET URL과 동일한 url 사용(article_pk를 사용하기 때문에)

![16](https://github.com/JeongJonggil/TIL/assets/139416006/2e823a17-adee-4602-95da-a653a6c5c8aa)


- PUT
  - 위의 GET URL과 동일한 url 사용(article_pk를 사용하기 때문에)
  - serializer.is_valid()의 경우 #serializer.py에서 정의한 Serializer field 속성에 적혀있는 필드에 대해 validation을 검사하게 됨. 그래서 **data의 일부만 수정하려면** fields 속성을 조절하거나, **views.py에서 serializer 클래스를 사용할 떄 partial = True를 인자로 넣어주어야 함**(partial은 디폴트값이 False로 되어 있음)
  - 수정의 성공은 일반적으로 응답코드 200을 사용하기 때문에 Response 인자로 안넣어 주어도 괜찮음

![17](https://github.com/JeongJonggil/TIL/assets/139416006/2fd418ad-9ae8-419d-9eae-dd57e35d5a80)

### 8. raise_exception (참고)

![18](https://github.com/JeongJonggil/TIL/assets/139416006/bd90c4d8-c394-4681-82b0-e9d883f28de5)
