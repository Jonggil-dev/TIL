# Django(장고)

### 1. Django

- Python 기반의 대표적인 웹 프레임 워크

### 2. 프로젝트 생성 전 루틴

![image](https://github.com/JeongJonggil/TIL/assets/139416006/d74509f2-5173-4741-854a-3d0ce0901ab3)


- VS Code 가상환경 활성화(source 명령어를 매 번 입력하는게 번거롭기 때문에)
  : ctrl + shift + p → interpreter →  Python Selcect Interpreter → venv python 선택

### 3. 프로젝트 생성 및 서버 실행

    (1) 프로젝트 생성 : $django-admin startproject firstpjt .

    (2) Django 서버 실행 : $ python manage.py reunserver

        ※ manage.py(Django 핵심파일)와 동일한 경로에서 명령어 실행 해야 됨

        ※ Django에서 **python manage.py**는 모든 명령어 앞에 사용됨

        ※ 서버 종료 : ctrl + c (잘 안되면 연타하기)

            **→ 서버 강제 종료시 오류가 날 수 있으므로 ctrl+c로  종료하기**

    (3) 서버 확인 : 터미널에 있는 링크(http://127.0.0.1:8000/) ctrl+좌클릭

### 4. Django 프로젝트 생성 루틴 정리 + git

![image](https://github.com/JeongJonggil/TIL/assets/139416006/4ed41c74-9ab1-4c02-8ef9-9985655562f8)


### 5. Django 프로젝트와 앱

(1) Django project : 애플리케이션의 집합 (DB 설정, URL 연결, 전체 앱 설정 등을 처리)

(2) Django application : 독립적으로 작동하는 기능 단위 모듈

            → 각자 특정한 기능을 담당하며 다른 앱들과 함께 하나의 프로젝트를 구성



- 하나의 프로젝트 안에 여러 개의 앱이 들어가는 구조로 생각하기

- **실제 생성되는 파일들을 보면  프로젝트폴더 안에 앱이 생성되는건 아님**

- 앱을 생성하고 난 뒤 프로젝트에 앱을 등록해야 됨
  
  
  
  

### 6. 앱

- <u>**반드시 앱을 먼저 생성한 후 등록해야 함 (등록 후 생성은 불가능)**</u>

        (1) 앱 생성 : $ python manange.py startapp 앱이름명

                             → 앱의 이름은 '복수형'으로 지정하는 것을 권장

        (2) 앱 등록

              :  project 안의 setting.py → 본문의 Installed_Apps 에 생성된 앱 추가



### 7. 디자인 패턴

- 소프트웨어 설계에서 발생하는 문제를 해결하기 위한 일반적인 해결책

- 공통적인 문제를 해결하는데 쓰이는 형식화된 관행



(1) MVC(Model, View, Controller) 디자인 패턴

- 애플리케이션을 구조화 하는 대표적인 패턴 (대부분의 프레임워크들이 사용하는 패턴)

- 데이터, 사용자 인터페이스 비지니스 로직을 분리

- 시각적 요소와 뒤에서 실행되는 로직을 서로 영향없이, 독립적으로 유지 보수할 수 있는 앱을 만들기 위해 사용



(2) MTV(Model, Template(=View), View(=Controller)) 디자인 패턴

- Django에서 애플리케이션을 구조화하는 패턴

- **MVC와 똑같은데 그냥 명칭만 다르게 적용한거임**
  (MVC에서 View → Template, Controller → View)



### 8. 프로젝트 구조

- **setting.py : 프로젝트의 모든 설정을 관리**

- **urls.py : URL과 이에 해당하는 적절한 views를 연결**



    ※ 하기 파일들은 수업 중 사용 할 일 없음

- init.py : 해당 폴더를 패키지로 인식하도록 설정

- asgi.py : 비동기식 웹 서버와의 연결 관련 설정

- wsgi.py : 웹 서버와의 연결 관련 설정

- manage.py : Django 프로젝트와 다양한 방법으로 상호작용하는 커맨드라인 유틸리티 



### 9. 앱 구조

- **admin.py : 관리자용 페이지 설정**

- **models.py : DB와 관련된 Model을 정의 (MTV 패턴의 M)**

- **views.py : HTTP 요청을 처리하고 해당 요청에 대한 응답을 반환 (url,mode,template과 연계. MTV 패턴의 V)
  
  

    ※ 하기 파일들은 수업 중 사용 할 일 없음

- apps.py : 앱의 정보가 작성된 곳

- tests.py : 프로젝트 테스트 코드를 작성하는 곳



### 10. 요청과 응답

![image](https://github.com/JeongJonggil/TIL/assets/139416006/867fde7c-a30b-40ec-b030-a86592cc6898)


#### ※ 백엔드는 위 사진처럼 동작함을 항상 떠올려야 함 ※



##### (1) URL
![image](https://github.com/JeongJonggil/TIL/assets/139416006/8d675540-35fb-4d09-ace6-d0e9798b91b3)


##### (2) View
![image](https://github.com/JeongJonggil/TIL/assets/139416006/218dc62b-2aae-4b9e-88e5-b6c19a0935d5)

##### (3) Template
1) 앱폴더 안에 templates 폴더 생성 **(폴더명은 반드시 templates여야 하며 개발자가 직접 생성해야함)**
2) templates 폴더 안에 파일 생성
- Django에서 template를 인식하는 경로 규칙  
  : app폴더 / templates (여기까지는 기본경로로 인식하기 때문에 이 지점 이후의 경로를 작성해야 함)  
     (ex) (app폴더/templates)생략/articles/index.html
    
