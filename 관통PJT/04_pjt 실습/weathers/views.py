from django.shortcuts import render
from matplotlib import pyplot as plt
import pandas as pd
import base64
from io import BytesIO
import matplotlib.dates as mdates

# Create your views here.
plt.switch_backend('Agg')
csv_path = 'weathers/data/austin_weather.csv'
df = pd.read_csv(csv_path)
df['Date'] = pd.to_datetime(df['Date'])  # 'Date' 컬럼을 날짜 형식으로 변환


def problem1(request):
    context = {
        'df':df,
    }
    return render(request,'weathers/problem1.html',context)

def problem2(request):
    df = pd.read_csv(csv_path)
    df['Date'] = pd.to_datetime(df['Date'])  # 'Date' 컬럼을 날짜 형식으로 변환 
    temp_high = df['TempHighF']
    temp_avg = df['TempAvgF']
    temp_low = df['TempLowF']
    # 그래프 초기화
    plt.clf()
    # 큰 그림 생성
    plt.figure(figsize=(10, 6))  # 원하는 크기로 수정 (가로 10, 세로 6)

    # 선 그래프 그리기
    plt.plot(df['Date'], temp_high, label='High Temperature')
    plt.plot(df['Date'], temp_avg, label='Average Temperature')
    plt.plot(df['Date'], temp_low, label='Low Temperature')

    # 축과 범례 설정
    plt.xlabel('Date')
    plt.ylabel('Temperature (Fahrenheit)')
    plt.title('Temperature Variation')
    plt.legend(loc='lower center')
    plt.grid(True)

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
    return render(request, 'weathers/problem2.html', context)


def problem3(request):
    df = pd.read_csv(csv_path)                          
    df = df[['Date','TempHighF','TempAvgF','TempLowF']]  # 사용할 필드만 가져오기
    df['Date'] = pd.to_datetime(df['Date'])             # 'Date' 컬럼을 날짜 형식으로 변환 
    df['TempHighF'] = pd.to_numeric(df['TempHighF'])    # 평균값 계산이 가능하도록 타입을 변환
    df['TempAvgF'] = pd.to_numeric(df['TempAvgF'])      # 평균값 계산이 가능하도록 타입을 변환
    df['TempLowF'] = pd.to_numeric(df['TempLowF'])      # 평균값 계산이 가능하도록 타입을 변환

    Monthly_data = df.groupby(df['Date'].dt.to_period('M')).mean()    # Date의 Month를 기준으로 평균값으로 그룹화함
    
    plt.clf()   # 그래프 초기화


    plt.figure(figsize=(10, 6))  # 원하는 크기로 수정 (가로 10, 세로 6)
    plt.plot(Monthly_data['Date'], Monthly_data['TempHighF'], label='High Temperature')
    plt.plot(Monthly_data['Date'], Monthly_data['TempAvgF'], label='Average Temperature')
    plt.plot(Monthly_data['Date'], Monthly_data['TempLowF'], label='Low Temperature')
    plt.xlabel('Date')
    plt.ylabel('Temperature (Fahrenheit)')
    plt.title('Temperature Variation')
    plt.legend(loc='lower right')
    plt.grid(True)

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
    
    return render(request,'weathers/problem3.html', context)



def problem4(request):
    df = pd.read_csv(csv_path)

    # 결측치 처리 - 한 칸 띄워쓰기로 되어있음
    df['Events'] = df['Events'].replace(' ', 'No Events')

    # 분리된 다중값을 단일 값으로 변환하여 카운트
    # 다중값 - 쉼표(,) 좌우로 공백이 들어가 있어 아래와 같이 split 하여 계산
    event_counts = df['Events'].str.split(' , ').explode().value_counts()

    # 그래프 초기화
    plt.clf()
    plt.bar(event_counts.index, event_counts.values)
    plt.xlabel('Events')
    plt.ylabel('Count')
    plt.title('Event Counts')
    # plt.xticks(rotation=45)  # x축 레이블 회전
    plt.legend()
    plt.grid(True)

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
    return render(request, 'weathers/problem4.html', context)
