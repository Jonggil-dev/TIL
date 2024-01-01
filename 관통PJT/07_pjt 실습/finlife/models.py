from django.db import models

# Create your models here.
class DepositProducts(models.Model):
    fin_prdt_cd = models.TextField(unique=True) #금융 상품 코드
    kor_co_nm = models.TextField(null=True, default=-1) #금융 회사명
    fin_prdt_nm = models.TextField(null=True, default=-1) #금융 상품명
    etc_note = models.TextField(null=True, default=-1) #금융 상품 설명
    join_deny = models.IntegerField(null=True, default=-1) #가입 제한(1:제한 없음, 2:서민전용, 3:일부제한)
    join_member = models.TextField(null=True, default=-1) #가입 대상
    join_way = models.TextField(null=True, default=-1) #가입 방법
    spcl_cnd = models.TextField(null=True, default=-1) #우대 조건


class DepositOptions(models.Model):
    product = models.ForeignKey(DepositProducts, on_delete=models.CASCADE) #외래 키 (DepositProducts 클래스 참조)
    fin_prdt_cd = models.TextField(unique=True) #금융 상품 코드
    intr_rate_type_nm = models.CharField(max_length=100,null=True, default=-1) #저축금리 유형명
    intr_rate = models.FloatField(null=True, default=-1) #저축 금리
    intr_rate2 = models.FloatField(null=True, default=-1) #최고 우대 금리
    save_trm = models.IntegerField(null=True, default=-1) #저축 기간(단위 : 개월)