# Django Template

### 1. Template

   : **데이터 표현을 제어하면서, 표현과 관련된 부분을 담당**



### 2. Django Template Language(DTL)

- **Template에서 조건, 반복, 변수 등의 프로그래밍적 기능을 제공하는 시스템**

- 공식 문서 : 구글에 The django template language 입력
  
   

    **(1) variable**

- 사용법 : 중괄호 2개 → {{variable}}

- render 함수의 세번째 인자로 **딕셔너리 데이터**를 사용

- 딕셔너리 key에 해당하는 문자열이 template에서 사용 가능한 변수명이 됨

-  dot(.)를 사용하여 변수 속성에 접근할 수 있음



    **(2) Filter**

- 사용법 : 변수에 | 붙이기 → {{variable|filter}}

- 표시할 변수를 수정할 때 사용

- chained가 가능하며 일부 필터는 인자를 받기도 함

- 약 60개의 built-in template filters를 제공



    **(3) Tag**

- 사용법 : 중괄호 + % → {% tag %}

- 반복 또는 논리를 수행하여 제어 흐름을 만듦

- 일부 태그는 시작과 종료 태그가 필요

- 약 24개의 built-in template tags를 제공

    

    (4) Comments (주석)

- 일부 주석 처리 : # → {# comments #}

- 여러줄 주석 처리 : {% comment %} + {% endcomment %}



### 3. Template 상속

- **페이지의 공통요소를 포함**하고, **하위 템플릿이 재정의 할 수 있는 공간**을 정의하는 기본 'skeleton'템플릿을 작성하여 상속 구조를 구축

##### (1) 'extends' tag

- 자식(하위) 템플릿이 부모 템플릿을 확장한다는 것을 알림

- **<u>반드시 템플릿 최상단에 작성되어야 함 (2개 이상 사용 불가)</u>**

   

##### (2) 'blcok' tag

- 하위 템플릿에서 재정의 할 수 있는 블록을 정의 (하위 템플릿이 작성되는 공간을 지정)



### 4. HTML form (요청과 응답)

##### (1) 'form' element

- 사용자로부터 할당된 데이터를 **서버**로 전송

- 데이터를 **어디(action), 어떤 방식(method)** 으로 요청할지 결정

        1) action

            - 입력 데이터가 전송될 URL을 지정 (목적지)

            - 만약 이 속성이 지정되지 않으면 현재 form이 있는 페이지의 URL로 보내짐

        2) method

            - 데이터를 어떤 방식으로 보낼 것인지 정의

            - 데이터의 HTTP request methods (GET, POST)를 지정

- form에 입력된 데이터를 가져오는 방법
  
![form 데이터를 가져오는 방법](https://github.com/JeongJonggil/TIL/assets/139416006/a8b3f7ad-d124-4f25-8cc7-b96bbe70a0c9)



##### (2) 'input' element

- 사용자의 데이터를 입력 받을 수 있는 요소

- 'name' attribute : 입력한 데이터에 붙이는 이름(key)

- **데이터를 제출했을 때 서버는 name 속성에 설정된 값을 통해 사용자가 입력한 데이터에 접근할 수 있음**



##### (3) Query String Parameters

- 사용자의 입력 데이터를 URL 주소에 파라미터를 통해 서버로 보내는 방법

- 문자열은 앰퍼샌드(&)로 연결된 key = value 쌍으로 구성

- 기본 URL과 물음표(?) 로 구분됨

- 예시 : http://host:port/path<u>?key=value&key-value</u>
