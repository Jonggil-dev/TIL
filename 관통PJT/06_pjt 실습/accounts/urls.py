from django.urls import path
from . import views

app_name="accounts"

urlpatterns = [
    path('login/', views.login, name="login"),
    path('logout/', views.logout, name="logout"),
    path('signup/', views.signup, name="signup"),
    path('profile/',views.profile_index, name='profile_index'),
    path('<int:user_pk>/profile', views.profile, name="profile"),
    path('<int:user_pk>/follow', views.follow, name="follow"),
]
