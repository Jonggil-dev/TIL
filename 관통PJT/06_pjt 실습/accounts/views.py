from django.shortcuts import render,redirect
from django.contrib.auth import login as auth_login
from django.contrib.auth import logout as auth_logout
from django.contrib.auth import get_user_model
from django.views.decorators.http import require_GET,require_http_methods,require_POST
from django.contrib.auth.forms import AuthenticationForm
from .forms import CustomUserChangeForm,CustomUserCretionForm
# Create your views here.

@require_http_methods(['GET','POST'])
def login(request):
    if request.method == "POST":
        form = AuthenticationForm(request,request.POST)
        if form.is_valid():
            auth_login(request,form.get_user())
            return redirect('boards:index')
    else:
        form = AuthenticationForm()
    context = {
        'form' : form,
    }
    return render(request,'accounts/login.html',context)


@require_POST
def logout(request):
    if request.method == 'POST':
        auth_logout(request)
        return redirect('boards:index')


@require_http_methods(['GET','POST'])
def signup(request):
    if request.method == "POST":
        form = CustomUserCretionForm(request.POST)
        if form.is_valid():
            user = form.save()
            auth_login(request,user)
            return redirect('boards:index')
    else:
        form = CustomUserCretionForm()
    context = {
        'form' : form,
    }
    return render(request,'accounts/signup.html',context)

@require_POST
def follow(request,user_pk):
    you = get_user_model().objects.get(pk=user_pk)
    me = request.user
    if me != you:
        if me in you.followers.all():
            you.followers.remove(me)
        else:
            you.followers.add(me)
    return redirect('accounts:profile', user_pk)



@require_http_methods(['GET','POST'])
def profile(request,user_pk):
    person = get_user_model().objects.get(pk=user_pk)
    articles = person.board_set.all()
    comments = person.comment_set.all()
    followers = person.followers.all()
    followings = person.followings.all()
    context = {
        'person' : person,
        'articles' : articles,
        'comments' : comments,
        'followers' : followers,
        'followings' : followings,
    }
    return render(request,'accounts/profile.html', context)


@require_http_methods(["GET"])
def profile_index(request):
    persons = get_user_model().objects.all()
    context = {
        'persons': persons
    }
    return render(request, 'accounts/profile_index.html', context)