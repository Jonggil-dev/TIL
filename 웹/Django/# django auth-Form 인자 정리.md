# # django auth-Form 인자 정리

```python
# django auth-Form 인자 정리

from django.contrib.auth.forms import UserChangeForm,UserCreationForm,AuthenticationForm,PasswordChangeForm
from django.contrib.auth import login,logout,update_session_auth_hash


# form이 입력된 상태로 request가 넘어오는 경우(if request.method=="POST":)
# 기본적으로 request 속성에 request.POST, request.GET 등이 다 있는데 설계자가 class 설계할때 유연성을 위해 request, request.POST 등으로 구분지어서 인자를 받는듯 함
#UserCreationForm,UserChangeForm의 경우 forms.py에서 커스텀 필요
AuthenticationForm(request, data=request.POST)          
UserCreationForm(request.POST)                          # data = request.POST
UserChangeForm(request.POST, instance=request.user)     # data=request.POST, instance=request.user
PasswordChangeForm(request.user, request.POST)          # user=request.user, data=request.POST
update_session_auth_hash(request, user)

# 그냥 html에 form 출력하기 위한 용도(else:)
AuthenticationForm()
UserCreationForm()
UserChangeForm(instance=request.user)
PasswordChangeForm(request.user)

#login,logout
login(request, form.get_user()) #login(request, user, backend=None)
logout(request)                 #로그아웃은 request.user 속성에 현재 로그인한 사용자 인스턴스가 있기 때문에 User 객체를 인자로 따로 안넣어도 됨

```

