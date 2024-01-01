from django.http import JsonResponse
import pandas as pd

csv_path = 'data/test_data_has_null.CSV'
df = pd.read_csv(csv_path, encoding='cp949')

def read_data(request):
    data = df.to_dict('records')

    context = {
        'data':data,
    }
    return JsonResponse(context)


def fillna_data(request):
    df.fillna('null', inplace=True)
    data = df.to_dict('records')

    context = {
        'data':data,
    }
    return JsonResponse(context)


def average_data(request):
    # '나이' 열의 결측치를 제외하고 평균 나이를 계산
    average_age = df['나이'].dropna().mean()

    # 평균 나이와 가장 근접한 나이를 가진 10개의 행을 찾기
    # '나이' 열의 결측치를 제외한 데이터만 이용
    df['age_diff'] = abs(df['나이'] - average_age)
    closest_ages = df.dropna(subset=['나이']).nsmallest(10, 'age_diff')

    # 불필요한 'age_diff' 열을 제거
    closest_ages.drop('age_diff', axis=1, inplace=True)

    # 결과를 딕셔너리로 변환하여 JsonResponse로 반환
    data = closest_ages.to_dict('records')

    context = {
        'data': data,
    }
    return JsonResponse(context)