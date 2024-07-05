# Django Model

### 1. Model

- DB(데이터베이스)를 관리하는 역할

- DB의 테이블을 정의하고 데이터를 조작할 수 있는 기능들을 제공

        → 테이블 구조를 설계하는 '청사진(blueprint)'

### 2. Model 클래스 살펴보기

```
from django.db import models.

class Article(models.Model):  #Article 클래스는 models.Model클래스를 상속받    
    title = models.CharField(max_length=10) #title은 CharField 클래스의 인스턴스
    content = models.TextField()           #content은 TextField 클래스의 인스턴스 
```

**※ models 패키지 안에 Field모듈 안에 CharField, TextField 등이 선언되어 있음**

**→ 원래는 패키지만 import 해서 모듈 안에 선언된 것들을 사용할 수 없는데, 패키지의 init 파일을 조작해 놓으면 가능함 (아마 이러한 조작이 되어 있을 듯)**

- 위 작성한 모델 클래스는 최종적으로 DB에 id,title,content 필드를 가지는 테이블 구조를 만듦 → **id 필드는 django에서 자동 생성**

- django.db.models 모듈의 Model이라는 부모 클래스를 상속받음

- Model은 model에 관련된 모든 코드가 이미 작성되어있는 클래스
  
  - https://github.com/django/django/blob/main/django/db/models/base.py#L459

- 개발자는 가장 중요한 테이블 구조를 어떻게 설계할지에 대한 고민만 작성하도록 하기 위한 것 (프레임워크의 이점)

- **클래스 변수명은 테이블의 각 필드(=열) 이름**, ※ 참고 **DB에서 행은 레코드라고 부름**

- charField, TextField 등 Field 클래스들은 테이블 필드의 "데이터 타입"을 설정

- model Field 클래스의 키워드 인자 (필드옵션) : 테이블 필드의 "제약조건" 관련 설정

### 3. Migrations

- model 클래스의 변경사항(필드 생성, 수정 삭제 등)을 DB에 최종 반영하는 방법
  
  ![migration과정](https://github.com/JeongJonggil/TIL/assets/139416006/05cc4d01-da90-4aed-9649-77e8380e4214)


- **makemigrations**
  
  (1) 파이썬 언어를 DB에서 사용하는 SQL언어로 바꾸는 과정(최종 설계도 작성)
  
  (2) 명령어 : pyhton manage.py makemigrations

- **migrate**

       (1) 완성된 migrations(최종설계도)을 DB로 보내는 과정

       (2) 명령어 : python manage.py migrate

- **showmigrations**
  
  (1) DB는 잘못되면 법적인 책임을 져야할 정도로 중요한 자료들이므로 신중하게 다루어야 함
  
  (2) 그래서 git status 처럼 showmigrations를 통해 현재 migrate 상태를 수시로 확인하는 습관을 들여야 함

### 4. DB테이블 내용 수정 하는 방법

#### : 반드시 model에 변경사항 수정 → makemigrations → migrate 순서

- model에 수정 내용 작성

- 이미 기존 테이블이 존재하기 때문에 필드를 추가 할 때는 필드의 기본 값 설정이 필요
  
  → **기존에 있던 테이블 자료에도 추가 되는 필드의 값을 어떻게 할 건지 선택해야됨**

  ※ makemigrations하면 django에서 하기 2가지 선택지를 줌

  (1) 1번은 현재 대화를 유지하면서 직접 기본값을 입력하는 방법

    - 1번 선택 후 아무것도 입력하지 않고 enter 누르면 django가 제안하는 기본 값으로 설정 됨(일반적으로 권장하는 방법)
  
  (2) 2번은 현재 대화에서 나간 후 models.py에 기본 값 관련 직접 설정하는 코드 작성

    - migrations 과정 종료 후 2번 째 migration 파일이 생성됨

- **django는 새로 생성되는 migration을 쌓아가면서 추후 문제가 생겼을 시 복구하거나 되돌릴 수 있도록 함 (이전 migration이 이후에 있는 migration에 영향을 줌으로 함부로 조작, 삭제 X)** 

### 5. SQL lite

- DB를 시각적으로 확인하게 해주는 VS Code 확장 프로그램

- 다운로드 후 DB파일 우클릭 Open database 클릭해서 실행

- 테이블의 이름은 **app 이름__class이름**으로 생성됨

### 6. Model Field

- DB 테이블의 필드(열)을 정의하며, 해당 필드에 저장되는 데이터 타입과 제약 조건을 정의

    (1) CharField() 

         - 길이의 제한이 있는 문자열을 넣을 때 사용

         - **필드의 최대 길이를 결정하는 max_length를 필수 인자로 입력해야 됨**

    (2) TextField()

         - 글자의 수가 많을 때 사용

    (3) DateTi`meField()

         - 선택인자 auto_now : 데이터가 저장될 때마다 자동으로 현재 날짜시간을 저장

         - 선택인자 auto_now_add : 데이터가 처음 생성될 때만 자동으로 현재 날짜시간을 저장

### 7. Admin site

(1) Automatic admin interface

        : Django는 추가 설치 및 설정 없이 자동으로 관리자 인터페이스를 제공

           → 데이터 확인 및 테스트 등을 진행하는데 매우 유용



(2) admin계정 생성

- 명령어 : python manage.py createsuperuser

- email은 선택사항이기 때문에 입력하지 않고 진행 가능

- 비밀번호 입력 시 보안상 터미널에 출력되지 않으니 무시하고 입력 이어가기

- auth_user DB table에서 확인 가능

    

(3) admin에 모델 클래스 등록

- admin.py에 작성한 모델 클래스를 등록해야만 admin site에서 확인 가능

```
# articles/admin.py

from django.contrib import admin
from .models import Article

admin.site.register(Article)


```
