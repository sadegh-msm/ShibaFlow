# Create your views here.

from django.shortcuts import (
    render,
    redirect,
    get_object_or_404
)

from .forms import UserRegistrationForm
from django.contrib.auth import authenticate
from django.contrib.auth import login as login_user
from django.contrib.auth import logout as logout_user
from django.views.generic import CreateView
from .models import Music
from django.contrib.auth.mixins import LoginRequiredMixin
from django.contrib.auth.decorators import login_required


class SongCreateView(LoginRequiredMixin, CreateView):
    model = Music
    template_name = 'spotify/song-create.html'
    fields = ['song_author', 'song_title', 'song_image', 'audio']
    success_url = '/'

    def form_valid(self, form):
        form.instance.user = self.request.user
        return super().form_valid(form)


def signup(request):
    if request.method == "POST":
        form = UserRegistrationForm(request.POST)
        username = request.POST.get('username')
        # print(username)
        password1 = request.POST.get('password1')
        # print(password1)
        if form.is_valid():
            user = form.save(commit=False)
            user.save()

            if user is not None:
                login_user(request, user)
                return redirect('login')
    else:
        form = UserRegistrationForm()
        return render(request, "spotify/register.html", {'form': form})


def login(request):
    if request.method == "POST":
        username = request.POST['username']
        password = request.POST['password']
        print(password)
        user = authenticate(request, username=username, password=password)

        if user is not None:
            login_user(request, user)
            return redirect('home')

    return render(request, 'spotify/login.html')


def logout(request):
    logout_user(request)
    return redirect('login')


@login_required(login_url='login')
def home(request):
    songs = Music.objects.all()
    return render(request, 'spotify/home.html', {'all': songs})


def playSong(request,id):
    obj = Music.objects.get(id=id)
    return render(request, 'spotify/playSong.html',{'song':obj })
