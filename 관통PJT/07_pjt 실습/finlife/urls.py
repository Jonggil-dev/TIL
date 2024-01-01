
from django.urls import path
from . import views
app_name = 'finlife'

urlpatterns = [
    path('save-deposit-products/',views.save_deposit_products,name='save'),
    path('deposit-porducts/',views.deposit_products,name='products'),
    path('deposit-porducts-options/<str:fin_prdt_cd>/',views.deposit_product_options,name='options'),
    path('deposit-porducts/top_rate/',views.top_rate,name='top_rate'),
]
