from django.contrib import admin

# Register your models here.
from django.contrib import admin
from .models import Music


class SpotMusicAdmin(admin.ModelAdmin):
    list_display = ('user', 'song_author', 'song_title', 'song_image', 'audio', 'created_on')


admin.site.register(Music, SpotMusicAdmin)
