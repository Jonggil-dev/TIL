# 230727(230721 결석 보충)

1. 서버에 API를 통해 데이터를 파싱하는 과정에 대해서는 이해 했으나, 원하는 데이터를 추출해오기 위한 각 서버 API의 URL 문법에 대해서는 추가 학습이 필요함.

2. 이중 for 문에서 모든 요소를 비교한다는 부분에서 for문의 순서는 크게 상관 없을 줄 알았습니다.
   하기 코드를 짜면서 새로운 list에 자료를 가공하는 과정에서 for문에 따라 값의 형태가 달라질 수 있음을 깨우쳤습니다.

```
# 하기 두개의 result 결과가 다름

1. censored_option_lst
for base in base_lst:
        censored_option_lst = []
        new_product = {}
        for option in option_lst:
            data_dic = {}
            if option['fin_prdt_cd'] == base['fin_prdt_cd']:
               .
               .
               .
        result.append({'금리정보' : censored_option_lst, '금융상품명': base['fin_prdt_nm'], '금융회사명': base['kor_co_nm']})

2.
for option in option_lst:
        censored_option_lst = []
        new_product = {}
        for base in base_lst:
            data_dic = {}
            if option['fin_prdt_cd'] == base['fin_prdt_cd']:
              .
              .
              .
        result.append({'금리정보' : censored_option_lst, '금융상품명': base['fin_prdt_nm'], '금융회사명': base['kor_co_nm']})
```
