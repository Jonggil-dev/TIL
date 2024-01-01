# Django ORM

### 1. ORM(Object-Relational-Mapping)

- 객체 지향 프로그래밍 언어를 사용하여 호환되지 않는 유형의 시스템 간에 데이터를 변환하는 기술  

- Django(파이썬) ↔ **ORM** ↔ SQL(DB)  을 통해 서로 언어가 달라도 소통이 가능함  

### 2. QureySet API

- ORM에서 데이터를 검색, 필터링, 정렬 및 그룹화 하는데 사용하는 도구  
  
  → **API를 사용하여 SQL이 아닌 python 코드로 데이터를 처리**  

![QuerySet](https://github.com/JeongJonggil/TIL/assets/139416006/812af1cd-ce1c-4d6e-8d52-2a06820fd0b4)

- 데이터가 여러 개면 QuerySet 형태, 1개면 Instance 형태로 반환함  
  
  ※ all() 메서드로 받아오면 항상 QuerySet 형태로 반환됨(빈 데이터 일지라도)  

### 3. Query

- **데이터 베이스에 특정한 데이터를 보여 달라는 요청**

- "쿼리문을 작성한다" → 원하는 데이터를 얻기 위해 데이터베이스에 요청을 보낼 코드를 작성한다.

- 파이썬으로 작성한 코드가 ORM에 의해 SQL로 변환되어 데이터베이스에 전달되며, 데이터베이스의 응답 데이터를 ORM이 QuerySet이라는 자료 형태로 변환하여 우리에게 전달

### 4. QuerySet

- 데이터베이스에게서 전달 받은 객체 목록(데이터 모음)
  
  → **순회가 가능한 데이터로써 1개 이상의 데이터를 불러와 사용할 수 있음**

- QuerySet API 구문  
  
   : **Model class.Manager.Queryset API 형태 → (ex) Article.objects.all()**

- Django ORM을 통해 만들어진 자료형

- 단, 데이터베이스가 단일한 객체를 반환 할 때는 QuerySet이 아닌 모델(Class)의 인스턴스로 반환됨 

### 5. QuerySet API 사용을 도와주는 라이브러리

- pip install ipython

- pip install django-extensions → **프로젝트 setting.py에 django_extenstions 등록해야됨**

### 6. Django shell

- Django 환경 안에서 실행되는 python shell

- Django Shell을 사용해야 입력하는 QuerySet API 구문이 Django 프로젝트에 영향을 미침

### 7. Create (데이터 생성) : 객체를 만드는(생성하는) 3가지 방법

##### : 우선, 인스턴스 하나가 하나의 레이블(행)이라고 생각하면 됨

**(1) 인스턴스를 만들어서 메서드 형태로 데이터를 저장하는 방법 (가장 권장하는 방법)**

    (ex)

        → article = Article()   
        → article.title = "first"   
        → article.content = "django!"  
        **→ article.save()**  

(2) 인스턴스 생성과 동시에 데이터를 함께 입력하는 방법  

    (ex)

        → article = Article(title="second", content="django!")  
        **→ article.save()**  

(3) QuerySet API 'create' 구문 사용 (save가 구문에 포함되어 있음)  

     (ex)  

        → Article.objects.create(title='third', content='django!')  

### 8. Read (데이터 조회)

- all(인자) : 전체 데이터 조회  
  → 데이터는 무조건 QuerrySet으로 반환 (데이터가 없어도 빈 쿼리셋 반환)  

- get(인자) : 단일 데이터 조회   
  → 데이터가 없거나 여러 개인 경우 예외 발생  
  → 따라서 primary key와 같이 고유성을 보장하는 조회에서 사용해야 함  

- filter(인자) : 특정 조건 데이터들 조회   
  → 데이터는 무조건 QuerrySet으로 반환 (데이터가 없어도 빈 쿼리셋 반환) 

- **Field lookup**
  
  → 사용법 : ____(언더바 2개)
  
  → 특정 레코드에 대한 세부 조건을 설정하는 방법
  
  → QuerySet 메서드 filter, exclude, get에 대한 키워드 인자로 지정됨
  
  (ex) Article.objects.filter(content___contains = 'dja')

### 9. Update (데이터 수정)

- 데이터 수정 절차  
  
   : 데이터 조회 → 데이터 수정 → 데이터 저장 순으로 이루어져야 함  

### 10. Delete (데이터 삭제)

- 삭제하려는 데이터 조회 후 delete메서드 호출 (저장을 안해도 지워짐)  
  
  → 현업에서는 위처럼 데이터를 바로 삭제하지 않고 is visible과 같은 방법으로 데이터를 클라이언트가 보이지 않도록 처리해 놓음, 이후 특정 주기를 가지고 데이터를 삭제함  
