from rest_framework import serializers
from .models import DepositProducts,DepositOptions

class DepositProductsSerializer(serializers.ModelSerializer):
    class Meta:
        model = DepositProducts
        fields = '__all__' # 모델에 있는 필드만 명시

class DepositOptionsSerializer(serializers.ModelSerializer):
    class Meta:
        model = DepositOptions
        fields = '__all__' # 모델에 있는 필드만 명시
        read_only_fields = ('product',)
