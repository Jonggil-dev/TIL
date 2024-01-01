from django.shortcuts import render
from django.conf import settings
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.exceptions import ValidationError
from .models import DepositProducts,DepositOptions
from .serializers import DepositProductsSerializer,DepositOptionsSerializer
import requests,json

# Create your views here.

@api_view(['GET'])
def save_deposit_products(request):
    if request.method == 'GET':
        BASE_URL = 'http://finlife.fss.or.kr/finlifeapi/'
        url =  BASE_URL + 'depositProductsSearch.json'

        topFingrpNos =['020000','030300']
        
        for topFingrpNo in topFingrpNos:
            params ={
                'auth' : 'c80d28bbd5d6dbf81590668a229cdb66',
                'topFinGrpNo': topFingrpNo,
                'pageNo' : 1
            }

            response = requests.get(url,params=params).json()
            products = response['result']['baseList']
            options = response['result']['optionList']
            
            response_str = json.dumps(response, ensure_ascii=False, indent=4)
            products_str = json.dumps(products, ensure_ascii=False, indent=4)
            options_str = json.dumps(options, ensure_ascii=False, indent=4)
            with open('response.txt','w',encoding='utf-8') as file:
                file.write(response_str)
            with open('products.txt','w',encoding='utf-8') as file:
                file.write(products_str)
            with open('options.txt','w',encoding='utf-8') as file:
                file.write(options_str)

            for product in products:
                serializer = DepositProductsSerializer(data=product)
                #'save-deposit-products/'을 들어갈 떄마다 DB에 저장을 해야되는데 fin_prdt_cd가 unique로 되어 있어서 에러가 뜸
                # unique 안되어 있으면 새로고침이나 url로 들어갈때마다 중복자료 계속 저장됨
                # 해당 데이터를 처리하기 위해 try, except 사용 → 그냥 raise_exception =True를 안하면 try,except 없이 해도 되긴함
                try:
                    if serializer.is_valid(raise_exception=True):
                        serializer.save()
                except ValidationError:
                    continue


            for option in options:
                try:
                    product = DepositProducts.objects.get(fin_prdt_cd=option['fin_prdt_cd'])
                    serializer =DepositOptionsSerializer(data=option)
                    if serializer.is_valid(raise_exception=True):
                        serializer.save(product=product)
                except DepositProducts.DoesNotExist: #get에서 일치하는 정보가 없을 경우
                    continue        
                except ValidationError:
                    continue

        return Response({"message": "성공적으로 처리되었습니다."},status=status.HTTP_201_CREATED)


@api_view(['GET','POST'])
def deposit_products(request):
    if request.method == 'GET':
        products = DepositProducts.objects.all()
        serializer = DepositProductsSerializer(products,many=True)
        return Response(serializer.data,status=status.HTTP_200_OK)
    
    elif request.method == 'POST':
        serializer = DepositProductsSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

@api_view(['GET'])
def deposit_product_options(request,fin_prdt_cd):
    if request.method == 'GET':
        product = DepositOptions.objects.get(fin_prdt_cd=fin_prdt_cd)
        serializer = DepositOptionsSerializer(product)
        return Response(serializer.data,status=status.HTTP_200_OK)


@api_view(['GET'])
def top_rate(request):
    if request.method == "GET":
        option = DepositOptions.objects.order_by('-intr_rate2').first()
        product = option.product
        serializer_option = DepositOptionsSerializer(option)
        serializer_product = DepositProductsSerializer(product)

        response_data = {
            'option': serializer_option.data,
            'product': serializer_product.data,
        }
    return Response(response_data,status=status.HTTP_200_OK)