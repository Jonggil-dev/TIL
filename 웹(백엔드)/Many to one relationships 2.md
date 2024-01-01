# Many to one relationships 2

### 1. N : 1 관계

- Article(N) - User (1) : 0개 이상의 게시글은 1명의 회원에 의해 작성 될 수 있다.

- Comment(N) - User (1) : 0개 이상의 댓글은 1명의 회원에 의해 작성 될 수 있다.

### 2. Article(N) - User(1)

- Article - User 모델 관계 설정

  ```python
  #articles/models.py
  from django.conf import settings
  
  class Article(models.Model):
  	user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
  ```

- User 모델을 참조하는 2가지 방법

  - **기억할 점은 User 모델은 직접 참조하지 않는다는 것**

  - **setting.AUTH_USER_MODEL은 models.py에서만 User 모델의 참조가 필요 할 때 사용 권장함**

![11](https://github.com/JeongJonggil/TIL/assets/139416006/46448b86-d2bc-48c8-98a6-d4acdaa08bf1)


- Migration

  - 하기 data값에 대한 설정이 귀찮으면 그냥 DB파일 삭제 후 makemigrations 하면 바로 진행 됨  

![12](https://github.com/JeongJonggil/TIL/assets/139416006/781742f1-7c75-4cf2-b976-f3a0d57f0fb2)
![13](https://github.com/JeongJonggil/TIL/assets/139416006/2839ac89-570f-4aeb-80f6-e5ecea449ca2)
![14](https://github.com/JeongJonggil/TIL/assets/139416006/2fc59318-377e-44de-9544-8dc5772f52d1)
![15](https://github.com/JeongJonggil/TIL/assets/139416006/a6b21f4d-ef80-44f2-907c-312ca0dc97e0)


- ##### 게시글 CREATE

  - Article 모델이 User 외래키 필드가 추가되면서 ArticleForm도 변경되고, 게시글 생성 페이지에 불필요한 User모델에 대한  외래 키 데이터 입력 input이 출력됨

  - ArticleForm 출력 필드 수정 (외래 키 입력 input 출력 제거)

    ``` python
    #articles/forms.py
    
    class ArticleForm(forms.ModelForm):
    	class Meta:
    		model = Article
    		fields = ('title','content',)
    ```

  - 게시글 작성 시 에러 발생 → user_id 필드 데이터가 누락되었기 때문

    - 해결법 : 게시글 작성 시 작성자 정보가 함께 저장될 수 있도록 save의 commit 옵션 활용 (하기 이미지 참고)
    - 하기 이미지 article.save() 대신에 form.save()로 작성하기 → 두 가지 코드 모두 동일한 결과를 나타내지만, 인스턴스의 save() 메서드와 ModelForm의 save() 메서드의 동작 방식 자체는 조금 차이가 있음.  
![20](https://github.com/JeongJonggil/TIL/assets/139416006/150b491a-922a-421b-ac92-47aa5fb0e947)


- ##### 게시글 UPDATE

![26](https://github.com/JeongJonggil/TIL/assets/139416006/86828f79-4216-491a-a994-cba0f88aa828)  
![27](https://github.com/JeongJonggil/TIL/assets/139416006/cb596fef-1794-42d9-a55a-ce721a4d5c1d)  


- ##### 게시글 Delete

![29](https://github.com/JeongJonggil/TIL/assets/139416006/9250aea8-0002-4241-95d0-cc28f519a013)

### 3. Comment(N)-User(1)

- Comment-User 모델 관계 설정

  ```python
  #articles/models.py
  from django.conf import settings
  
  class Comment(models.Model):
  	user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
   
  ```

- Migration, 댓글 CREATE, 댓글 UPDATE, 댓글 Delete
  - 위 Article-User 로직 참고해서 동일한 로직으로 작성
