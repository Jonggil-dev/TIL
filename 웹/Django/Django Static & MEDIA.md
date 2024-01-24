# Django Static & MediA

### 0. 원리

- #### Client와 Server 사이에서 어떠한 파일을 제공하려면 요청에 응답하기 위한 URL이 필요함

- #### Client가 웹 페이지를 통해 확인할 수 있는 내용들은 모두 URL 주소를 통해 server에 요청을 하고 제공받은 것들임

- #### 그래서 결국 static 파일도 url 주소가 생성됨

  

### 1. Static Files(정적 파일)

- ##### 서버 측에서 변경되지 않고 고정적으로 제공되는 파일(이미지, JS, CSS 파일 등)

  

### 2. Static files 사용법

- ##### django에서 static 파일은 위치를 아무데나 두고 경로를 적어두어도 인식하지 못함.

- ##### 따라서 아래 규칙들과 3.Static files의 경로에 관한 규칙들을 명확히 준수해야 됨

- ##### static 파일들은 django에서 직접적인 물리적인 주소로 가져올 수 없음 → static 태그를 사용해야됨

- ##### static 태그는 django에 built-in 되어 있지 않으므로 import하는것 처럼 load를 하고 사용 해야함

  ```html
  (ex)
  
  {% load static %} → static 태그를 사용하려면 해당하는 html에서 load를 선언한 후에 사용해야 함
  
  1. <a href="articles/sample-1.png"></a> → X (물리적인 주소)
  2. <a href="{% static "articles/sample-1.png" %}"></a> → O (static 태그)
  ```



### 3. Static files 경로

##### 	(1) 기본경로: app폴더/static 

​		→ 기본 경로는 dajngo에서 static 파일을 찾을때 입력하지 않아도 자동으로 인식가능한 경로

​		→ 따라서 templates 처럼 **(app폴더/static)/app폴더/파일** 이렇게 파일이 위치하게 함

##### 	(2) 추가경로: STATICFILES_DIR에 문자열 값으로 추가 경로 설정

​		→  STATICFILES_DIR : 정적 파일의 기본 경로 외에 추가적인 경로 목록을 정의하는 리스트(리스트이므로 경로 여러개 작성 가능)

```
#settings.py에 작성
→ static 파일을 찾을때(static 테그 사용시) django에서 작성된 경로까지는 자동으로 인식함

STATICFILES_DIR = [
	BASE_DIR/'static1', #BASE_DIR내 static1 폴더까지는 자동으로 인식
	BASE_DIR/'static2', #BASE_DIR내 static2 폴더까지는 자동으로 인식
		  ●
		  ●
]
```



### 4. Static URL

- 기본 경로 및 추가 경로에 위치한 정적 파일을 참조하기 위한 URL. 
- 실제 파일이나 디렉토리가 아니며, URL로만 존재
- project의 setting.py에서 STATIC_URL 부분을 통해 생성되는 url 주소를 설정 가능함

> **생성되는 static 파일의 URL : URL + STATIC_URL + 정적파일 경로**
> 	**(ex) http://127.0.0.1:8000/static/articles/sample-1.png** 



### 5. Media Files

- ##### 사용자가 웹에서 업로드하는 정적 파일(user-uploaded)

- ImageField() : 이미지 업로드에 사용하는 모델 필드 

- 이미지 객체가 직접 저장되는 것이 아닌 '이미지 파일의 경로'가 문자열로 DB에 저장

- ##### 성능 측면에서, 직접 파일을 저장하면 DB 크기가 급격하게 증가하여 성능이 저하됨. 따라서 파일 자체는 파일시스템에 별도로 저장하고 DB에는 그 파일에 대한 문자열 경로만 저장함

- ##### 유지 보수 관점에서도, 만약 db에 직접 파일을 저장해버리면 파일 수정 시 db를 직접 조작해야함. 그러나 db에 경로만 저장되어 있다면 파일시스템에서만 파일을 수정하면 됨



### 6. Media File 업로드 받는 방법

- settings.py에 MEDIA_ROOT, MEDIA_URL 설정

  > MEDIA_ROOT : 미디어 파일들이 위치하는 디렉토리의 절대 경로
  >
  > ​				→ 클라이언트가 미디어 파일을 올렸을 때, 해당 미디어 파일을 어디 폴더에 놔둘껀지를 결정하는 경로 
  >
  > MEDIA_URL : MEDIA_ROOT에서 제공되는 미디어 파일에 대한 주소를 생성(STATIC_URL과 동일한 역할)
  >
  > ```python
  > #settings.py
  > 
  > MEDIA_ROOT = BASE_DIR/'media'  #ROOT에는 최상위 경로 하나만 작성해야함, 최상의 경로 내에 추가 경로를 만들고 싶으면 models.py에서 ImageField에 upload_to 속성 사용
  > MEDIA_URL = 'media/'
  > ```
  >
  > 

- 작성한 MEDIA_ROOT와 MEDIA_URL에 대한 urls.py에 url  지정 **(코드 외울 필요 없고 공식문서에서 복사해서 쓰기)**

  > ```python
  > #urls.py
  > 
  > from django.conf import settings
  > from django.conf.urls.static import static
  > 
  > urlpatterns = [
  >  ~~~
  > ] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
  > 
  > #urlpatterns 안에 넣어서 작성해도 되는데 관리적 측면을 고려하여 바깥에 + 로 작성하는걸 권장함
  > ```

- model에 ImageField 추가하기

  > ```python
  > #models.py
  > 
  > class Article(models.Model):
  > 	title = models.CharField(max_length=10)
  >     image = models.ImageField(blank=True)
  > 	content = models.TextField()
  >     
  >     # ImageField는 기존 필드 사이에 작성해도 실제 테이블 생성 시에는 가장 우측(뒤)에 추가됨
  >     # blank = True 옵션은 클라이언트가 이미지 업로드란을 공백으로 업로드 해도 괜찮다는 옵션
  > ```

- Pillow 라이브러리 다운 ($pip install Pillow)

  > ImageField를 사용하기 위한 라이브러리 정도로 생각하면 됨

- html form 태그에 enctype="multipart/form-data" 속성 추가하기

  > form은 기본적으로 문자열 밖에 처리를 못하기 때문에 enctype="multipart/form-data" 속성 추가

- view 함수에서 form 데이터를 처리하는 부분에 request.FILES 추가하기

  > imamge file은 request.post말고 request.files 로 데이터가 전송됨
  >
  > ```python
  > #views.py
  > 
  > form = ArticleForm(request.POST, request.FILES)
  > ```
  >

### 7. MEDIA FILE 제공하기

- 클라이언트가 업로드한 미디어 파일을 html 파일에서 출력하려면 {{ xx.xx.url }} 방식으로 미디어 파일의 url 경로를 호출함
![1](https://github.com/JeongJonggil/TIL/assets/139416006/16355ad9-06d9-4e4f-9d27-c03746769850)


- 업로드 이미지 수정

![2](https://github.com/JeongJonggil/TIL/assets/139416006/c1368645-1c7c-424a-8bc1-2d66e288a3d3)

![3](https://github.com/JeongJonggil/TIL/assets/139416006/a5761bcb-c764-4b7b-9a6d-c42639dc0711)


### 8. media-Image파일 크기 Resizing하여 DB저장하는 방법  
  
- imagekit 패키지의 ProcessedImageField + ResizeToFill 사용  
```
# models.py
from imagekit.models import ImageSpecField,ProcessedImageField
from imagekit.processors import ResizeToFill

class Memo(models.Model):
    image = ProcessedImageField(blank=True, processors=[ResizeToFill(200,200)])
```
  
  
### 9. 썸네일 설정하기(media파일)
  
- 기본 Media 파일 제공하는 방법과 동일함.  
  
  (1) (코드 인터넷 참고) 원본 파일을 바탕으로 썸네일 파일을 만들어 DB 저장후 업로드 하는 방법  
	:  썸네일을 동적으로 생성하는 오버헤드를 줄일 수 있음.  
  (※ 오버헤드 :어떤 작업을 수행하기 위해 필요한 추가적인 자원이나 시간. 주된 작업의 수행 외에 필요한 처리량이나 불가피하게 발생하는 부가적인 작업)

  **(2) 원본 이미지를 기반으로 썸네일을 동적으로 생성하는 방법**  
  	: django-imagekit의 ImageSpecField는 실제 데이터베이스 필드가 아님. 대신에 해당 필드는 원본 이미지 필드에 대한 동적 변환을 정의하는 역할을 함  
```
#models.py
from django.db import models
from imagekit.models import ImageSpecField
from imagekit.processors import ResizeToFill

class Profile(models.Model):
    avatar = models.ImageField(upload_to='avatars/')
    avatar_thumbnail = ImageSpecField(source='avatar',
                                      processors=[ResizeToFill(100, 100)],
                                      format='JPEG',
                                      options={'quality': 60})
```



