# Django URLS

### 1. URL (공식 이름은 URL dispatcher)

- **URL 패턴을 정의하고 해당 패턴이 일치하는 용청을 처리할 view 함수를 연결(매핑)**

### 2. Variable Routing

- URL 일부에 변수를 포함시키는 것

- 변수는 view 함수의 인자로 전달 할 수 있음

- 작성법

![vairiable routing](https://github.com/JeongJonggil/TIL/assets/139416006/0cc8fa3d-c186-40a7-be98-d31d3123fed9)

### 3. App URL mapping

- 각 앱에 URL을 정의하는 것

- 프로젝트와 각 앱이 URL을 나누어 관리를 편하게 하기 위함

- include() 사용 
  
  (1) 프로젝트 내부 앱들의 URL을 참조할 수 있도록 매핑하는 함수
  
  (2) URL의 일치하는 부분까지 잘라내고, 남은 문자열 부분은 후속 처리를 위해 include된 URL로 전달

![변경된 URL 구조](https://github.com/JeongJonggil/TIL/assets/139416006/d6206454-46f7-4c78-98f9-545203a643c3)

### 4. Naming URL patterns

- URL에 이름을 지정하는 것 (path 함수의 name 인자를 정의해서 사용)

    (ex) path ('index/', views.index, <mark>name = 'index'</mark>)

- URL 표기 변환

        - url을 작성하는 모든 위치에서 반영 가능 

        - 사용법 :  {% url '이름' %} 로 사용

- 여러 앱에서 동일한 URL 이름을 사용할 경우를 대비해 이름에 성(key)을 붙일 수 있음

        → 사용법 :  app_name = '이름'

### 5. 주의사항

![Trailing slashes](https://github.com/JeongJonggil/TIL/assets/139416006/3e63dee2-d96c-4d08-be17-6926827acc61)
