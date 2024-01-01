# 관통 PJT 04

### 1. 금융 상품 비교 앱(데이터 사이언스+ django)

- #### matplotlib 사용법

```python
#matplotlib 사용법
import matplotlib.pyplot as plt

plt.plot([1,2,3,4])

#뒤에 추가로 그래프를 그렸을 때 한 창에 겹쳐서 그려주기 위한 코드
plt.clf()  

x = [1,2,3,4]
y = [2,4,8,16]
plt.plot(x,y)
plt.title("Test Graph")	# title 설정 (한글로 쓰면 깨짐)
plt.ylabel('y label')	# y label 설정(한글로 쓰면 깨짐)
plt.xlabel('x label')	# x label 설정(한글로 쓰면 깨짐)

plt.savefig('filename.png') # 그려진 graph를 file로 저장

plt.show() #graph 출력

# ※plt.savefig는 plt.show()이후에 진행하면 흰색화면만 출력됨. show를 하면서 다 날라가버린다고 생각하기
```



- #### django에서 plt 그래프를 html로 렌더링 하기

```python
#django에서 plt 그래프를 html로 렌더링 하는 방법

# 과정 : Plot 데이터 → 버퍼(임시 저장) → 이진 데이터 인코딩 → template 출력



from django.shortcuts import render
import matplotlib.pyplot as plt
from io import BytesIO
import base64

# io : 입출력 연산을 위한 Python 표준 라이브러리
# BytesIO : 이진 데이터를 다루기 위한 버퍼를 제공
# 버퍼 : 임시 저장 공간. 파일 시스템과 유사하지만, 실제로 파일로 만들지 않고 메모리 단에서 작업할 수 있음.
# base64 : 텍스트 ↔ 이진 데이터를 변환할 수 있는 모듈

# 참고. 터미널 에러
# Userwarning: Starting a Matplotlib GUI outside of the main thread will likely fail.
# PLT를 만드는 곳과 화면에 그리는 곳이 달라서 오류가 날 수 있다고 경고를 주는곳.
# 백엔드를 Agg로 설정하여 해결
plt.switch_backend('Agg')

def index(request):
    x = [1,2,3,4]
    y = [2,4,8,16]

    plt.plot(x,y)
    plt.title("Test Graph")
    plt.ylabel('y label')
    plt.xlabel('x label')

    # 비어있는 버퍼를 생성
    buffer = BytesIO()

    # 버퍼에 그래프를 저장
    plt.savefig(buffer, format='png')

    # 버퍼의 내용을 base64로 인코딩(버퍼의 내용을 이진수로 바꾸는 작업)
    image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8').replace('\n','')

    # 버퍼를 닫아줌
    buffer.close()


    # 이미지를 웹 페이지로 넘기기
    # URL 형식(주소 형식)으로 만들어진 문자열을 생성
    context ={
        'chart_image': f'data:image/png;base64,{image_base64}'
    }

    return render(request,'index.html',context)

```

```django
#index.html

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <h1>메인 페이지</h1>
    <img src="{{ chart_image }}" alt="">
</body>
</html>
```

- #### django로 csv 파일 읽어와 html에 렌더링 하기(Pandas 사용)

```python
# views.py
# django로 csv 파일 읽어와 html에 렌더링 하기(Pandas 사용)

import pandas as pd
csv_path = 'firstapp/data/austin_weather.csv'

def example(request):
    df = pd.read_csv(csv_path)
    context ={
        'df':df,
    }
    return render(request,'example.html',context)

```

```django
#example.html

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <h1> Pandas 써보기 </h1>
    <table>
        <tr>
            {% for column in df.columns %}
            <th> {{ column }} </th>
        </tr>
        {% for row in df.values  %}
        <tr>
            {% for value in row %}
            <td> {{ value }} </td>
            {% endfor %}
        </tr>
        {% endfor %}
    </table>
</body>
</html>
```

