from django.shortcuts import render,redirect
from .models import Keyword,Trend
from .forms import keywordForm
import requests
from bs4 import BeautifulSoup
from selenium import webdriver
from matplotlib import pyplot as plt
from io import BytesIO
import matplotlib.font_manager as fm
import base64
# Create your views here.


def keyword(request): 
    if request.method == "POST":
        form = keywordForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect('trends:keyword')
    else:
        form = keywordForm()

    words = Keyword.objects.all()
    context = {
        'form': form,
        'words': words,
    }
    return render(request,'trends/keyword.html',context)


def keyword_delete(request,pk):
    keyword = Keyword.objects.get(pk=pk)
    trend = Trend.objects.get(keyword_id=pk)
    trend.delete()
    keyword.delete()
    return redirect('trends:keyword')


def crawling(request):
    keywords = Keyword.objects.all()
    result = []

    for keyword in keywords:
        
        url = f"https://www.google.com/search?q={keyword.name}"
        

        # 그냥 우리가 크롬 브라우저를 열고 주소창에 url을 입력하는 과정을 동일하게 그냥 코드로 작성했다고 보면 됨
        driver = webdriver.Chrome() # 크롬 브라우저가 열린다. 이 때, 동적인 내용들이 모두 채워짐
        driver.get(url)				# 열린 브라우저를 해당 url로 이동시킴

        # driver모듈로 열린 페이지 소스를 받아오고 BeautifulSoup를 통해 데이터를 파싱.
        html = driver.page_source 
        

        soup = BeautifulSoup(html, "html.parser")

        # div 태그 중 id 가 result-stats 인 요소 검색
        result_stats = soup.select_one("div#result-stats")
        string_data = result_stats.text.split()[-2][:-1]
        result = int(string_data.replace(',', ''))

        if Trend.objects.filter(name=keyword.name, search_period="all").exists():
            trend = Trend.objects.get(name=keyword.name, search_period="all")
            trend.result = result
            trend.save()
        else:
          Trend.objects.create(keyword=keyword, name=keyword.name, result=result, search_period="all")
    
    # 크롤링된 자료들 class 명이나 기타 참고하기 위해 파일로 저장해놓음
    with open('soup1.txt','w',encoding='utf-8') as file:
        file.write(soup.prettify())

    trends = Trend.objects.filter(search_period="all")
    context = {
        'trends': trends
    }
    return render(request, 'trends/crawling.html', context)
    

def crawling_histogram(request):
    plt.switch_backend('Agg')

    x = []
    y = []

    
    trends = Trend.objects.filter(search_period ='all')
    for trend in trends:
        x.append(trend.name)
        y.append(trend.result)


    #그래프 초기화
    plt.clf()
    
    plt.figure(figsize=(10, 6))  # 그래프 원하는 크기로 수정 (가로 10, 세로 6)
    plt.bar(x, y, label='Trends', width=0.3)    # 막대 그래프 그리기, width는 막대기 사이즈(막대기 1개일 때는 그래프 기본속성에 덮혀서 안먹힘)

    # 축과 범례 설정
    plt.xticks(rotation=45)  # x 축 레이블 45도 회전
    plt.xlabel('Keyword')
    plt.ylabel('Result')
    plt.title('Technology Trend Analysis')
    plt.legend(loc='upper right')
    plt.grid(True)


    # Matplotlib에서 한글을 사용할 때 글꼴을 지정해주지 않으면, 
    #기본 글꼴에서 한글을 지원하지 않아서 빈 사각형이 출력.
    # 한글 출력을 위해 한글을 지원하는 글꼴 설정
    # 해당 글꼴도 설치가 되어있어야함 
    plt.rcParams['font.family'] = 'Malgun Gothic'

    # django view 에서 이미지 형식의 데이터를 직접 전송할 수 없음
    # 버퍼로 저장 -> template 으로 전송해야함
    # BytesIO(): 그래프 이미지를 임시로 저장할 버퍼
    buffer = BytesIO()
    # 그래프를 버퍼에 저장. 이미지 형식은 png 로 설정
    plt.savefig(buffer, format='png')
    # 버퍼의 내용을 base64 로 인코딩
    image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8').replace('\n', '')
    # data:image/png;base64:
    buffer.close()

    context = {
        'chart_image': f'data:image/png;base64,{image_base64}',
    }
    return render(request,'trends/crawling_histogram.html',context)

def crawling_advanced(request):    
    keywords = Keyword.objects.all()
    result = []

    for keyword in keywords:
        url = f"https://www.google.com/search?q={keyword.name}&tbs=qdr:y"

        # 그냥 우리가 크롬 브라우저를 열고 주소창에 url을 입력하는 과정을 동일하게 그냥 코드로 작성했다고 보면 됨
        driver = webdriver.Chrome() # 크롬 브라우저가 열린다. 이 때, 동적인 내용들이 모두 채워짐
        driver.get(url)				# 열린 브라우저를 해당 url로 이동시킴

        # driver모듈로 열린 페이지 소스를 받아오고 BeautifulSoup를 통해 데이터를 파싱.
        html = driver.page_source 
        soup = BeautifulSoup(html, "html.parser")

        # div 태그 중 id 가 result-stats 인 요소 검색
        result_stats = soup.select_one("div#result-stats")
        string_data = result_stats.text.split()[-2][:-1]
        result = int(string_data.replace(',', ''))

        if Trend.objects.filter(name=keyword.name, search_period="year").exists():
            trend = Trend.objects.get(name=keyword.name, search_period="year")
            trend.result = result
            trend.save()
        else:
          Trend.objects.create(keyword=keyword, name=keyword.name, result=result, search_period="year")
    
    # 크롤링된 자료들 class 명이나 기타 참고하기 위해 파일로 저장해놓음
    with open('soup2.txt','w',encoding='utf-8') as file:
        file.write(soup.prettify())

    plt.switch_backend('Agg')
    x = []
    y = []

    trends = Trend.objects.filter(search_period ='year')
    for trend in trends:
        x.append(trend.name)
        y.append(trend.result)

    #그래프 초기화
    plt.clf()
    
    plt.figure(figsize=(10, 6))  # 그래프 원하는 크기로 수정 (가로 10, 세로 6)
    plt.bar(x, y, label='Trends', width=0.3)    # 막대 그래프 그리기, width는 막대기 사이즈(막대기 1개일 때는 그래프 기본속성에 덮혀서 안먹힘)

    # 축과 범례 설정
    plt.xticks(rotation=45)  # x 축 레이블 45도 회전
    plt.xlabel('Keyword')
    plt.ylabel('Result')
    plt.title('Technology Trend Analysis')
    plt.legend(loc='upper right')
    plt.grid(True)


    #Matplotlib에서 한글을 사용할 때 글꼴을 지정해주지 않으면, 
    #기본 글꼴에서 한글을 지원하지 않아서 빈 사각형이 출력.
    # 한글 출력을 위해 한글을 지원하는 글꼴 설정
    # 해당 글꼴도 설치가 되어있어야함 
    plt.rcParams['font.family'] = 'Malgun Gothic'


    # django view 에서 이미지 형식의 데이터를 직접 전송할 수 없음
    # 버퍼로 저장 -> template 으로 전송해야함
    # BytesIO(): 그래프 이미지를 임시로 저장할 버퍼
    buffer = BytesIO()
    # 그래프를 버퍼에 저장. 이미지 형식은 png 로 설정
    plt.savefig(buffer, format='png')
    # 버퍼의 내용을 base64 로 인코딩
    image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8').replace('\n', '')
    # data:image/png;base64:
    buffer.close()

    context = {
        'chart_image': f'data:image/png;base64,{image_base64}',
    }
    return render(request,'trends/crawling_advanced.html',context)