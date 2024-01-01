# Ajax with Django

### 1. Ajax와 서버

![1](https://github.com/JeongJonggil/TIL/assets/139416006/80f856e8-6ab2-4b6b-b020-49ec30a74a07)

- Ajax를 활용한 팔로우/팔로워 기능 구현 코드

```html
#profile.html

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
</head>
<body>
  <h1>{{ person.username }}님의 프로필</h1>

  <div>
    <div>
      팔로잉 : <span id="followings-count">{{ person.followings.all|length }}</span> / 
      팔로워 : <span id="followers-count">{{ person.followers.all|length }}</span>
    </div>
    {% if request.user != person and request.user.is_authenticated %}
      <div>
        <form id="follow-form" data-user-id="{{ person.pk }}">
          {% csrf_token %}
          {% if request.user in person.followers.all %}
            <input type="submit" value="Unfollow">
          {% else %}
            <input type="submit" value="Follow">
          {% endif %}
        </form>
      </div>
    {% endif %}
  </div>

  <hr>

  <h2>작성한 게시글</h2>
  {% for article in person.article_set.all %}
    <p>{{ article.title }}</p>
  {% endfor %}

  <hr>

  <h2>작성한 댓글</h2>
  {% for comment in person.comment_set.all %}
    <p>{{ comment.content }}</p>
  {% endfor %}

  <hr>

  <h2>좋아요를 누른 게시글</h2>
  {% for article in person.like_articles.all %}
    <p>{{ article.title }}</p>
  {% endfor %}

  <hr>

  <a href="{% url "articles:index" %}">[back]</a>

  <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
  <script>
    // 1. form 요소 선택
    const formTag = document.querySelector('#follow-form')
    // 6. csrftoken value 값 선택
    const csrftoken = document.querySelector('[name=csrfmiddlewaretoken]').value
    
    
    // 2. form 요소에 이벤트 리스터 부착
    formTag.addEventListener('submit', function (event) {
      // 3. submit 이벤트 기본 동작 취소
      event.preventDefault()
      // 5. form 요소에 지정한 data 속성 접근하기
      const userId = formTag.dataset.userId

      // 4. axios로 비동기적으로 팔로우/언팔로우를 요청
      axios({
        url: `/accounts/${userId}/follow/`,
        method: 'post',
        headers: {'X-CSRFToken': csrftoken},
      })
        .then((response) => {
          console.log(response.data)
          // 7. Django에서 보낸 follow 여부를 알 수 있는 변수를 저장
          const isFollowed = response.data.is_followed
          // 8. 팔로우 버튼 선택
          const followBtn = document.querySelector('#follow-form > input[type=submit]:nth-child(2)')
          // 9. 팔로우 버튼 토글
          if (isFollowed === true) {
            followBtn.value = 'Unfollow'
          } else {
            followBtn.value = 'Follow'
          }

          // 10. 팔로워 / 팔로잉 수 조작
          const followingsCountTag = document.querySelector('#followings-count')
          const followersCountTag = document.querySelector('#followers-count')
          
          followingsCountTag.textContent = response.data.followings_count
          followersCountTag.textContent = response.data.followers_count
        })
        .catch((error) => {
          console.log(error)
        })
    })
  </script>
</body>
</html>
```



```python
#views.py

from django.shortcuts import render, redirect
from django.contrib.auth.forms import AuthenticationForm, PasswordChangeForm
from django.contrib.auth import login as auth_login
from django.contrib.auth import logout as auth_logout
from django.contrib.auth import update_session_auth_hash
from django.contrib.auth.decorators import login_required
from django.contrib.auth import get_user_model
from django.http import JsonResponse
from .forms import CustomUserCreationForm, CustomUserChangeForm



@login_required
def follow(request, user_pk):
    User = get_user_model()
    you = User.objects.get(pk=user_pk)
    me = request.user

    if me != you:
        if me in you.followers.all():
            you.followers.remove(me)
            is_followed = False
        else:
            you.followers.add(me)
            is_followed = True
        context = {
            'is_followed': is_followed,
            'followings_count': you.followings.count(),
            'followers_count': you.followers.count(),
        }
        return JsonResponse(context)
    return redirect('accounts:profile', you.username)
```



- Ajax를 활용한 좋아요 기능 구현 코드

```javascript
#index.html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Document</title>
</head>
<body>
  <h1>INDEX</h1>
  {% if request.user.is_authenticated %}
    <h3>{{ user.username }}님 안녕하세요!</h3>
    <a href="{% url "accounts:profile" user.username %}">내 프로필</a>
    <form action="{% url "accounts:logout" %}" method="POST">
      {% csrf_token %}
      <input type="submit" value="LOGOUT">
    </form>
    <form action="{% url "accounts:delete" %}" method="POST">
      {% csrf_token %}
      <input type="submit" value="회원탈퇴">
    </form>
    <a href="{% url "accounts:update" %}">회원정보수정</a>
  {% else %}
    <a href="{% url "accounts:login" %}">LOGIN</a>
    <a href="{% url "accounts:signup" %}">SIGNUP</a>
  {% endif %}

  <hr>
  
  <a href="{% url 'articles:create' %}">CREATE</a>
  <hr>
  {% for article in articles %}
    <p>
      작성자 : 
      <a href="{% url "accounts:profile" article.user.username %}">{{ article.user }}</a>
    </p>
    <p>글 번호 : {{ article.pk }}</p>
    <a href="{% url "articles:detail" article.pk %}">
      <p>글 제목 : {{ article.title }}</p>
    </a>
    <p>글 내용 : {{ article.content }}</p>
    <form class="likes-forms" data-article-id="{{ article.pk }}">
      {% csrf_token %}
      {% if request.user in article.like_users.all %}
        <input type="submit" value="좋아요 취소" id="like-{{ article.pk }}">
      {% else %}
        <input type="submit" value="좋아요" id="like-{{ article.pk }}">
      {% endif %}
    </form>
    <hr>
  {% endfor %}

  <script src="https://cdn.jsdelivr.net/npm/axios/dist/axios.min.js"></script>
  <script>
    const formTags = document.querySelectorAll('.likes-forms')
    const csrftoken = document.querySelector('[name=csrfmiddlewaretoken]').value

    formTags.forEach((formTag) => {
      formTag.addEventListener('submit', function (event) {
        event.preventDefault()

        const articleId = formTag.dataset.articleId

        axios({
          url: `/articles/${articleId}/likes/`,
          method: 'post',
          headers: {'X-CSRFToken': csrftoken},
        })
          .then((response) => {
            // console.log(response.data.is_liked)
            const isLiked = response.data.is_liked
            const likeBtn = document.querySelector(`#like-${articleId}`)

            if (isLiked === true) {
              likeBtn.value = '좋아요 취소'
            } else {
              likeBtn.value = '좋아요'
            }
          })
          .catch((error) => {
            console.log(error)
          })
      })
    })
  </script>
</body>
</html>

```

```python
#views.py

from django.shortcuts import render, redirect
from django.contrib.auth.decorators import login_required
from django.http import JsonResponse
from .models import Article, Comment
from .forms import ArticleForm, CommentForm

@login_required
def likes(request, article_pk):
    article = Article.objects.get(pk=article_pk)
    if request.user in article.like_users.all():
        article.like_users.remove(request.user)
        is_liked = False
    else:
        article.like_users.add(request.user)
        is_liked = True
    context = {
        'is_liked': is_liked,
    }
    return JsonResponse(context)
```

