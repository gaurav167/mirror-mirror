from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'make_caricature$', views.mk_ccr, name="make_caricature"),
    url(r'response$', views.aud_resp, name="response"),
]