import requests
import json
# Create your views here.

BASE_URL = 'http://finlife.fss.or.kr/finlifeapi/'
url =  BASE_URL + 'depositProductsSearch.json'

params ={
    'auth' : 'c80d28bbd5d6dbf81590668a229cdb66',
    'topFinGrpNo': '020000',
    'pageNo' : 1
}

response = requests.get(url,params=params).json()
response_str = json.dumps(response, ensure_ascii=False, indent=4)

products = response['result']['baseList']
products_str = json.dumps(products, ensure_ascii=False, indent=4)
options = response['result']['optionList']
options_str = json.dumps(options, ensure_ascii=False, indent=4)

    

 # products_str = json.dumps(products, ensure_ascii=False, indent=4)
    # with open('products.txt','w',encoding='utf-8') as file:
    #     file.write(products_str)
    
# with open('response.txt','w',encoding='utf-8') as file:
#     file.write(response_str)

with open('products.txt','w',encoding='utf-8') as file:
    file.write(products_str)

with open('options.txt','w',encoding='utf-8') as file:
    file.write(options_str)
