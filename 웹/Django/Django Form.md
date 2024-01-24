# Django Form

### 1. 개요

- ##### html 의 form은 비정상적 혹은 악의적인 요청을 필터링 할 수 없음

- ##### 사용자로부터 받은 데이터에 대해 유효성 검사(형식,중복,범위, 보안 등)를 하기 위해 Django의 Form을 만들어서 사용

### 2. Django Form

- ##### 사용자 입력 데이터를 수집하고, 처리 및 유효성 검사를 수행하기 위한 도구

- ##### 유효성 검사를 단순화하고 자동화 할 수 있는 기능을 제공

- 사용법

  ```python
  1. app에 forms.py 파일 생성
  
  2.forms.py에 하기 코드 작성
  from django import forms
  
  class ArticleForm(forms.Form):
  	title = forms.CharField(max_length=10)
  	content = forms.CharField()
  	
  3.views.py 중 사용할 함수에 context로 넘겨주기
  from .forms import ArticleForm
  
  def new(request):
  	form = ArticleForm()
  	context = {
  		'form': form,
  	}
  	return render(request,'articles/new.html',context)
  
  4. html에서 변수 표기법(context key)으로 사용하기
  ```

  - ##### Form rendering options

    : label, input 쌍을 특정 HTML태그로 감싸는 옵션

    ex) {{ form.as_p }} → 각 input을 <p>로 감싸서 html에서 줄바꿈을 해서 표기됨

### 3. Widgets

- HTML 'input' element의 표현을 담당
- 단순히 input 요소의 속성 및 출력되는 부분을 변경하는 것

​	ex) content = forms.CharField(widget=forms.Textarea)
​		→ content input 창은 Textarea 박스로 표기 됨

### 4. Django ModelForm

##### 	(1) Form vs ModelForm

​		● Django Form : 사용자 입력 데이터를 DB에 저장하지 않을때

##### 		● Django ModelForm: 사용자 입력 데이터를 DB에 저장해야 할 때 (ex. 회원가입)

##### 	(2) 정의

##### 		●  Model과 연결된 Form을 자동으로 생성해 주는 기능을 제공 Form + Model

##### 	(3) 사용법

```python
1. forsm.py 생성 및 코드 작성
2. 코드
from django import forms
from .models import Article

class ArticleForm(forms.ModelForm):
	class Meta: 		#Meta는 데이터의 데이터 정도의 의미로 생각하기
		model = Article
		fields = '__all__'
```

##### 	(4) Meta class

​		●  ModelForm의 정보를 작성하는 곳

​		●  'fields' 및 'exclude' 속성 : exlude 속성을 사용하여 모델에서 포함하지 않을 필드를 지정할 수도 있음

​	ex) fields =('title',) , exclude = ('title',)



##### 	(5) ModelForm을 적용한 create 로직

![1](https://github.com/JeongJonggil/TIL/assets/139416006/96a13f94-54c8-4580-a108-a9064c552409)


##### 		●  if form.is_valid(): 를 통과하지 못했을 때는 context 하기 부분이 실행되는데 이 때 자동으로 vaild검사에서 실패힌 이유를 같이 html page에 rendering해줌. 그냥 ModelForm의 특성임

##### 	(6) ModelForm을 적용한 create 로직

![2](https://github.com/JeongJonggil/TIL/assets/139416006/e556aeee-230f-4234-b5f6-b5fd3c5c8a3e)


##### 	(7) ModelForm을 적용한 update 로직

![3](https://github.com/JeongJonggil/TIL/assets/139416006/ab755998-2be6-46a1-9f92-c6e265a55ab2)


​#####	(8) save() 메서드

![4](https://github.com/JeongJonggil/TIL/assets/139416006/aad6260d-3fea-4957-8ee8-fee48c21f07b)


### 5. HTTP request method 차이점을 활용한 view 함수 구조 변경

![5](https://github.com/JeongJonggil/TIL/assets/139416006/024ecfc1-716e-45cc-8e3a-811cba0b21b9)

![6](https://github.com/JeongJonggil/TIL/assets/139416006/a5959004-7d1f-4362-b9a6-902105121ec6)

### 6. Choice/ModelChoiceField  
- Form에 input 입력받을 시 셀렉트박스 등 사용자가 데이터 목록중에서 선택하여 입력가능하게 하는 필드
  
    
**(1) ChoiceField : ChoiceField는 choices 인자를 통해 선택 항목을 받아들임. choices 인자는 리스트나 튜플 사용. choices의 첫 번째 값은 데이터베이스에 저장될 실제 값이고, 두 번째 값은 사용자에게 표시될 레이블. choices인자를 다른 함수 리턴값으로 하여 동적으로도 선택항목을 조정가능함**  
```
# 예시코드
class CommentForm(forms.ModelForm):
    PICK_CHOICES = (
        (True, 'RED TEAM'),
        (False, 'BLUE TEAM'),
    )
    pick = forms.ChoiceField(choices=PICK_CHOICES, widget=forms.Select)
    
    class Meta:
        model = Comment
        exclude = ['question']
```
  
**(2) ModelChoiceField : 데이터베이스의 모델로부터 선택 항목을 생성하려면 ModelChoiceField를 사용. ChoiceField의 서브클래스로, 모델의 쿼리셋을 바탕으로 선택 항목을 자동으로 생성.**  
```
# 예시코드
from django import forms
from django.contrib.auth import get_user_model
from .models import Store,Product

UserModel = get_user_model()

class StoreForm(forms.ModelForm):
    user = forms.ModelChoiceField(queryset=UserModel.objects.filter(is_superuser=True))
    class Meta:
        model = Store
        fields = '__all__'
```
