from model import music_interaction, user, music, playlist, playlist_musics
import hashlib
import re


def check_for_key_song(dic, keys, song):
    # title album genre
    res = {}
    for i in range(len(keys)):
        if keys[i] not in dic.keys():
            print(song)
            if keys[i] == 'title':
                res[keys[i]] = song[0]
            elif keys[i] == 'album_name':
                res[keys[i]] = song[1]
            elif keys[i] == 'genre':
                res[keys[i]] = song[2]
        else:
            res[keys[i]] = dic[keys[i]]

    return res


def check_for_key_user(dic, keys, user):
    # artist_name password
    res = {}
    for i in range(len(keys)):
        if keys[i] not in dic.keys():
            if keys[i] == 'fname':
                res[keys[i]] = user[1]
            elif keys[i] == 'lname':
                res[keys[i]] = user[2]
            elif keys[i] == 'email':
                res[keys[i]] = user[5]
        else:
            res[keys[i]] = dic[keys[i]]

    return res


def like_or_dislike_music(user_id, music_id, status):
    if status == 'like':
        music_interaction.like_music(user_id, music_id)
        return True
    elif status == 'dislike':
        music_interaction.dislike_music(user_id, music_id)
        return True
    else:
        return False


def hash_password(password):
    """
    Hash a password using SHA-256.
    """
    sha256 = hashlib.sha256()
    sha256.update(password.encode('utf-8'))
    return sha256.hexdigest()


def is_valid_email(email):
    """
    Check if the provided string is a valid email address.
    """
    email_regex = r'^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$'
    return re.match(email_regex, email)


def create_all_tables():
    n, cu = user.connect_to_database('./model/db/ShibaFlow.db')
    user.create_user_table(cu)
    music.create_music(cu)
    music_interaction.create_music(cu)
    playlist.create_playlist_table(cu)
    playlist_musics.create_playlist_musics_table(cu)
