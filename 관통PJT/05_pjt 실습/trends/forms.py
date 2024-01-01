from django import forms
from .models import Keyword

# Create your models here.
class keywordForm(forms.ModelForm):
    name = forms.CharField(widget=forms.TextInput,label='키워드')

    class Meta:
        model = Keyword
        fields ='__all__'