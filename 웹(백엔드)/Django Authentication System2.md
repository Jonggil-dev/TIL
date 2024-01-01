# Django Authentication System2

### 1. 회원가입

- User 객체를 Create 하는 과정

- UserCreationForm() : 회원 가입시 사용자 입력 데이터를 받을 built-in ModelForm

![1](https://github.com/JeongJonggil/TIL/assets/139416006/616123ee-c20d-4b0a-87c7-e21b0a1a49cc)

![2](https://github.com/JeongJonggil/TIL/assets/139416006/d92f7913-f21b-4769-8f44-dcff8d4fb7ce)

![3](https://github.com/JeongJonggil/TIL/assets/139416006/091ef6f0-9603-4421-a2f2-d7378ee545ed)

![4](https://github.com/JeongJonggil/TIL/assets/139416006/8a0a406b-abb8-4862-8afe-5162f1ab797f)

![5](https://github.com/JeongJonggil/TIL/assets/139416006/86f3682e-b280-4626-881b-5b9734dcb0d2)

![6](https://github.com/JeongJonggil/TIL/assets/139416006/caca4f55-6757-49e0-83c3-5559a4dbb9cd)

![7](https://github.com/JeongJonggil/TIL/assets/139416006/c4f1b1cf-7222-47dd-a1ab-af9c979411f6)


> User 모델을 직접 참조하게 되면 User모델에 변경사항이 생겼을 떄 해당 모델을 참조하고 있는 부분들을 모두 직접 수정해야 됨. get_user_model() 을 통해 간접적으로 모델을 참조함으로써 모델에 수정사항이 생겼을때 이를 참조하고 있는 부분들을 수정해야하는 불편함이 줄어듬
  
> 참고:  
UserCreationForm을 상속받으면 Meta클래스 fields속성에 password1(비밀번호입력),password2(비밀번호 재확인)필드를 작성하지 않아도 html input에 나타남.  
명시적으로 입력하고 싶을때 사용하는 필드명은 → 비밀번호 입력: password1, 비밀번호 재확인: password2 임  


### 2. 회원 탈퇴

- User 객체를 Delete 하는 과정

- 회원 탈퇴 시 로그아웃을 진행하려면
  - request.user.delete() 이후 auth_logout(request) 순으로 진행해야 됨
  - `auth_logout`을 먼저 호출하면 `request.user`가 `AnonymousUser`로 바뀌게 되므로, 이후에 `request.user.delete()`를 호출하면 에러가 발생 함. 따라서 사용자 데이터를 삭제하기 전에 로그아웃을 수행하면 안 되고, 반드시 사용자 데이터를 먼저 삭제한 후 로그아웃을 수행해야 함.
   ```
    #회원탈퇴와 동시에 로그아웃 로직
    @require_POST
    def delete(request):
    if request.method == "POST":
        request.user.delete()
        auth_logout(request)
        return redirect("articles:index")
     ```
- 참고 : request.user.delete()만 해도 로그아웃이 되긴 함.
  
   request.user.delete()만 하면 현재 세션에 연결된 사용자 정보가 데이터베이스에 없기 때문에 세션이 무효화되어request.user는 계속해서 AnonymousUser를 반환하고 사용자는 로그아웃된 것처럼 보이게 됨. 하지만, 명시적으로 logout(request)를 호출하여 세션 데이터를 명확하게 삭제해주는 것이 세션 데이터와 관련된 모든 정보가 명확하게 삭제되고, 서버 리소스도 절약하게 됨


  
![8](https://github.com/JeongJonggil/TIL/assets/139416006/ffd8c6af-8416-47c8-90cc-0ff018b7acd0)

### 3. 회원정보 수정

- User 객체를 Update 하는 과정
- UserChangeForm() : 회원정보 수정 시 사용자 입력 데이터를 받을 built-in ModelForm

![9](https://github.com/JeongJonggil/TIL/assets/139416006/94b72cb1-ff0d-4cac-b42e-d3bd6e7167da)

![10](https://github.com/JeongJonggil/TIL/assets/139416006/c484adf0-017f-44f0-a4c8-3e18a1db3658)

![11](https://github.com/JeongJonggil/TIL/assets/139416006/d7eef44d-6df3-4753-9e7c-8199b52a3067)

![12](https://github.com/JeongJonggil/TIL/assets/139416006/dd08f7e2-abf7-4368-8565-908d21689841)

![13](https://github.com/JeongJonggil/TIL/assets/139416006/dd2f8e87-9aa5-4491-840a-6b4c9bd8cd08)



### 4. 비밀번호 변경

- 인증된 사용자의 Session 데이터를 Update 하는 과정
- PasswordChangeForm() : 비밀번호 변경 시 사용자 입력 데이터를 받을 built-in Form
- **참고** :  
  (1) 회원정보 수정 form 안에 비밀번호 변경 a태그가 자동으로 생성 되는데 자꾸 url이 accounts/<user_id>/password/로 설정됨  
  (2) 문제점 : 회원정보수정 url을 <int:pk>/update/ 로 해놨었는데 이때 <int:pk>를 지우니까 /<user_id>/password/로 설정됨  
  (3) 원인 : 추정 불가    
  (4) 지피티 답변 : django 내부적으로 동적 url을 형성할 때, 정의된 URL 패턴들 중에서 가장 "가까운" 패턴을 찾아서 사용함. 이 때 "가까운" 패턴이란, 현재 처리 중인 뷰와 가장 유사한 패턴을 의미. 결국 회원정보수정 url의 <int:pk>와 비밀번호 변경 <user_id>가 동적 url 생성과정에서 문제를 일으켰을 가능성이 있음  

![14](https://github.com/JeongJonggil/TIL/assets/139416006/9f153ee3-4530-4f44-99a4-4b4ea538f6df)

![15](https://github.com/JeongJonggil/TIL/assets/139416006/b6afad95-c3c4-4efa-a555-1594632ef2db)

![16](https://github.com/JeongJonggil/TIL/assets/139416006/c2833cc5-27d1-4f75-9174-f259cfd91216)

![17](https://github.com/JeongJonggil/TIL/assets/139416006/f73ccd2b-9e73-4d1b-bf26-fac15f06efaa)

![18](https://github.com/JeongJonggil/TIL/assets/139416006/acbe1739-d055-4822-9579-f2101313f5bb)

![19](https://github.com/JeongJonggil/TIL/assets/139416006/14918c03-22f6-449d-ae59-95935d7aafff)


### 5. 인증된 사용자에 대한 접근 제한

- 로그인 사용자에 대해 접근을 제한하는 2가지 방법

  (1) is_authenticated 속성 

  : 사용자가 인증 되었는지 여부를 알 수 있는 User model의 속성.모든 User 인스턴스에 대해 항상 True인 읽기 전용 속성이며, 비인증 사용자에 대해서는 항상 False

  (2) login_required 데코레이터

  : 인증된 사용자에 대해서만 view 함수를 실행시키는 데코레이터. 비인증 사용자의 경우 /accounts/login/ 주소로 redirect 시킴

- #### is_authenticated 적용하기

![20](https://github.com/JeongJonggil/TIL/assets/139416006/ea786213-467b-42e2-b842-156d265b796b)

![21](https://github.com/JeongJonggil/TIL/assets/139416006/79bc1809-2bc5-45d3-b2fd-3ff1ff0e240f)

- ##### login_required 적용하기

![22](https://github.com/JeongJonggil/TIL/assets/139416006/601692fd-cb8e-4eed-a855-de894085be28)

![23](https://github.com/JeongJonggil/TIL/assets/139416006/7d74edc0-e8d4-4d11-a2bc-e137d1bed67f)
