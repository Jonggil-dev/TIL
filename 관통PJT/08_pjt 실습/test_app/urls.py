from django.urls import path
from . import views

urlpatterns = [
    path('requestA/',views.read_data),
    path('requestB/',views.fillna_data),
    path('requestC/',views.average_data),
    # path('normal_sort/', views.normal_sort),
    # path('priority_queue/', views.priority_queue),
    # path('bubble_sort/', views.bubble_sort),
]

