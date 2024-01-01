# Django ORM with view

- server error에서 NoReverseMatch가 나오면 다른거 필요없이 a테그의 하이퍼링크 url부분만 확인하면됨(예외 없음)
- url 뒤에 arguments를 추가하고 싶으면 그냥 공백으로 구분해서 나열하면 됨

```html
ex) <a href="{% url "restaurants:main" item.pk %}"></a>

"restaurants:main" 의 경로가 <int:pk>/main/일 경우 <int:pk>에 item.pk가 전달된다는 의미임
```

