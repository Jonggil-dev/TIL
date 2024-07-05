# Django Authentication System1

### 1. 개요

- 우리가 서버로부터 받은 페이지를 둘러볼 때 우리는 서버와 서로 연결되어 있는 상태가 아님

### 2. HTTP

- HTML 문서와 같은 리소스들을 가져올 수 있도록 해주는 규약, 웹(WWW)에서 이루어지는 모든 데이터 교환의 기초

- ##### 특징

  - ##### 비 연결 지향(connectionless)

    : 서버는 요청에 대한 응답을 보낸 후 연결을 끊음

  - ##### 무상태(stateless)

    : 연결을 끊는 순간 클라이언트와 서버 간의 통신이 끝나며 상태 정보가 유지되지 않음

### 3. 쿠키(Cookie) & 세션(Session)

#### : 서버와 클라이언트 간의 상태를 유지하기 위한 목적으로 사용됨. 정보가 저장되는 위치에 따라 쿠키(클라이언트)와 세션(서버)을 구분해서 생각하면 됨

#### 	(1) 쿠키

​		●  서버가 사용자의 웹 브라우저에 전송하는 작은 데이터 조각

##### 		● 클라이언트 측에서 저장되는 작은 데이터 파일이며, 사용자 인증, 추적, 상태 유지 등에 사용되는 데이터 저장 방식

##### 		● 쿠키 사용 예시

![1](https://github.com/JeongJonggil/TIL/assets/139416006/95e7cfa7-8fdb-4db2-a29f-b062ae97e999)


##### 			● 쿠키 사용 원리

![2](https://github.com/JeongJonggil/TIL/assets/139416006/b4aee5b1-42a6-4ac2-9633-4662f8606b47)


​			● 쿠키 사용 목적

​				- 세션관리 : 로그인, 아이디 자동완성, 공지 하루 안 보기, 팝업 체크, 장바구니 등의 정보 관리

​				- 개인화 : 사용자 선호, 테마 등의 설정

​				- 트레킹 : 사용자 행동을 기록 및 분석



#### 	(2) 세션(Session)

##### 		● 서버 측에서 생성되어 클라이언트와 서버 간의 상태를 유지하기 위해 상태 정보를 저장하는 데이터 저장 방식

​		● 쿠키에 세션 데이터를 저장하여 매 요청시마다 세션 데이터를 함께 보냄

​		● 세션 작동 원리

![3](https://github.com/JeongJonggil/TIL/assets/139416006/280a433d-eae7-4bfc-a939-948361bcb6f5)


### 4. 쿠키(Cookie) & 세션(Session) 요약

- #### **서버 측에서는 세션 데이터를 생성 후 저장하고 세션 ID를 생성, 해당 ID를 클라이언트 측으로 전달하여, 클라이언트는 쿠키에 이 ID를 저장**

- #### 서버로부터 쿠키를 받아 브라우저에 저장하고, 클라이언트가 같은 서버에 재요청 시마다 저장해 두었던 쿠키도 요청과 함께 전송

  > 예를 들어 로그인 상태 유지를 위해 로그인 되어 있다는 사실을 입증하는 데이터를 매 요청마다 계속해서 보내는것

- django에서는  세션을 저장하고 key를 발급하는 등 일련의 과정들을 내부적으로 처리해주기 때문에 우리가 구현할 필요는 없음. 우리는 결과를 이해하고 보기만 하면 됨

![4](https://github.com/JeongJonggil/TIL/assets/139416006/a71bf95f-67cc-4133-a1f6-c5d24350fc12)


### 5. 인증(Authentication)

- 사용자가 자신이 누구인지 확인하는 것(신원 확인)

- auth와 관련한 경로나 키워드들을 django 내부적으로 accounts라는 이름으로 사용하고 있기 때문에 되도록 **앱 이름은 'accoutns'로 지정하는것을 권장**

### 6. Custom User Model

- django에서 기본적으로 제공하는 User model은 내장된 auth 앱의 User 클래스를 사용했었음. settings.py의 'django.contrib.auth'

- ##### 하지만, 개발자가 직접 User 정보에 대한 Field를 Custom 할 수 없는 문제가 존재하여 Custom User Model로 대체하여 사용.

- ##### Custom User Model 대체하기

![5](https://github.com/JeongJonggil/TIL/assets/139416006/7ebd95e4-6130-4e6c-a961-04e95341c8f2)

![6](https://github.com/JeongJonggil/TIL/assets/139416006/23e9b642-3851-4bf7-a16c-715f2bdd192a)

![7](https://github.com/JeongJonggil/TIL/assets/139416006/d0204305-6def-4b65-a86c-24b36dbc5b49)


- #### 주의사항

  ##### <span style="color:red"> ※ 주의 : 프로젝트 중간에 Auth_USER_MODEL을 변경 할 수 없음. </span>

  : 그래서 class User(AbstractUser): 를 pass로 선언해놓고 시작하는 거임. 미리 pass로라도 선언을 해놓고 진행하기 때문에 프로젝트 중간이라도 필드에 대한 수정이 가능함. 만약 Custom User Model 를 잊고 프로젝트가 이미 진행되고 있을 경우 데이터베이스 초기화 후 Custom User Model 생성 후 진행하면 됨

![8](https://github.com/JeongJonggil/TIL/assets/139416006/e3a3a661-cecd-4daa-92f9-ab7dda90764a)

### 7. Login

- ##### Session을 Create하는 과정이 Login

- django에서는 로그인/회원가입/회원정보수정/비밀번호변경에 필요한 form이 built-in 되어 있음

- ##### 로그인 페이지 작성(create-view 함수와 거의 동일함)

![9](https://github.com/JeongJonggil/TIL/assets/139416006/891b336f-2707-4127-90da-59e68e894ba8)
  
- 위 이미지에는 없는데 from django.contrib.auth.forms import AuthenticationForm 후 써야됨
- AuthenticationForm()
  - 로그인 인증에 사용할 데이터를 입력받는 built-in form,
  -**내부적으로 authenticate 함수(DB의 user정보와 입력받은 user정보를 비교하는 함수)를 사용하여 사용자의 자격 증명을 확인. 따라서 form.is_valid()를 호출할 때, AuthenticationForm은 제공된 username과 password를 사용하여 사용자를 인증하게 됨.**
  - **※주의※ : login(request, user) 함수는 그냥 2번 째 인자로 받은 user의 세션을 만드는(로그인) 역할임. 유효성 검사는 authenticate 함수를 명시적으로 사용하거나, AuthenticationForm의 내부 authenticate 함수에 의해 실행됨**
- get-user() : AuthenticationForm의 인스턴스 메서드
      → 유효성 검사를 통과했을 경우 로그인 한 사용자 객체를 반환

### 8. Logout

- ##### Session을 delete 하는 과정이 Logout

- logout(request) : 현재 요청에 대한 Session Data를 DB에서 삭제, 클라이언트의 쿠키에서도 Session Id를 삭제

![로그아웃](https://github.com/JeongJonggil/TIL/assets/139416006/0b4b1dae-75f3-41a5-b5c6-6ca1f4c2b952)  

  
### 9. Template with Authentication data

- ##### context processors : view함수에서 따로 context로 넘기지 않아도 settings.py-templates에 미리 load되어 호출이 가능한 변수들

![10](https://github.com/JeongJonggil/TIL/assets/139416006/58b56c40-f763-400b-a90b-0409342b80b1)

### 10.  참고(Abstract base classes, 추상 기본 클래스)

##### - Abstract base classes, 추상 기본 클래스

- 다른 자식 클래스를 찍어내기 위한 클래스로, 자식 클래스를 찍어내기 위한 틀
- migrate를 해도 table이 만들어지지 않도록 설계되어 있으며, 대신 다른 모델의 기본 클래스로 사용되는 경우 해당 필드가 하위 클래스의 필드에 추가됨.
- 파이썬에서 보통 Abstract가 붙어있으면 추상 기본 클래스임

### 11. login_required 데코레이터/request.path를 사용한 로그인 후 요청페이지 redirect
  (1) Django의 login_required 데코레이터를 사용하면, 로그인되지 않은 사용자가 보호된 뷰에 접근하려고 시도할 때 로그인 페이지로 리다이렉트됨(이 때 url을 확인해보면 login 페이지로 next 변수가 달려서 전달됨). 이 때, login_required 데코레이터에 의해 원래 접근하려고 했던 URL이 next 매개변수로 로그인 URL에 전달되어 이를 이용해 로그인 후 가려고 했던 페이지로 redirect 시킬 수 있음.  

```
#views.py 작성
from django.contrib.auth.decorators import login_required

@require_http_methods(['GET', 'POST'])
def login(request):
    if request.user.is_authenticated:
        return redirect('travels:index')

    if request.method == 'POST':
        form = AuthenticationForm(request, request.POST)
        if form.is_valid():
            auth_login(request, form.get_user())
            return redirect(request.GET.get('next') or 'travels:index')
    else:
        form = AuthenticationForm()
    context = {
        'form': form
    }
    return render(request, 'accounts/login.html', context)
```

```
# login.html 작성

{% extends 'base.html' %}

{% block content %}
  <h1>로그인</h1>
  <form action="{% url 'accounts:login' %}?next={{request.GET.next}}" method="POST">
    {% csrf_token %}
    {{ form.as_p }}
    <input type="submit">
  </form>
{% endblock content %}
```
  
  (2) {{ request.path }} : login_required 없이 현재 보고있던 페이지에서 로그인 버튼을 눌렀을 때 현재 url을 저장하는 방법.<br>
      ※ Django 프로젝트를 시작할 때 기본 settings.py에 이 context processor가 이미 포함되어 있기 때문에, Django의 template context에서는 기본적으로 request 객체에 접근할 수 있음. 따라서 템플릿 내에서 request.path를 직접 사용하면 현재 요청의 URL 경로를 얻을 수 있음.  
      
```
# html에 작성
<a href="{% url 'accounts:login' %}?next={{ request.path }}"> Login </a> #form을 별도로 않해도 url 형식으로 정보가 넘어감.

# views.py에 작성(위의 require_login데코레이터 next 변수 로직과 동일)
def login(request):
    if request.method == "POST":
        form = AuthenticationForm(request, request.POST)
        next_url = request.POST.get('next', '/')
        if form.is_valid():
            auth_login(request, form.get_user())
            return redirect(next_url)
    else:
        form = AuthenticationForm()
        next_url = request.GET.get('next', '/')
    context = {
        "form": form,
        "next": next_url
    }
    return render(request, 'accounts/login.html', context)
```
