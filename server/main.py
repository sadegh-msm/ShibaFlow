import logging.config
from flask import Flask, request, jsonify, send_from_directory
from waitress import serve
from model import user, music, music_interaction, playlist, playlist_musics
from configs import config
from objectStorage.s3 import arvan_uploader, arvan_downloader
from utils import util
import random
import os

hashem = []
hashem

app = Flask(__name__)
logging.basicConfig(level=logging.NOTSET, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger()
logger.setLevel(logging.NOTSET)

app.config["SONGS"] = os.getenv("HOME") + '/android/ShibaFlow/server/Flows/'
app.config["SONGS_COVER"] = os.getenv("HOME") + '/android/ShibaFlow/server/Cover_flows/'


# app.config["SONGS"] = '/Users/mohamadsadegh/daneshgaaaa/term7/android/ShibaFlow/server/Flows/'
# app.config["SONGS_COVER"] = '/Users/mohamadsadegh/daneshgaaaa/term7/android/ShibaFlow/server/Cover_flows'


@app.route("/healthz", methods=['GET'])
def healthz():
    logger.info("called")
    return "ok"


@app.route("/register", methods=['POST'])
def register_user():
    user_info = request.form.to_dict()
    logger.info('user requested sign in', user_info)

    if user_info['fname'] == '' or user_info['lname'] == '' or user_info['artist_name'] == '' or user_info['email'] == '' or \
            user_info['password'] == '':
        logger.info('bad request', user_info)
        return jsonify({'error': 'bad request'}), 400

    exist = user.user_exists(user_info['artist_name'])

    if exist:
        logger.info('user already exist', user_info)
        return jsonify({'error': "user with this artist name already exist"}), 409

    ok = user.insert_users_data(user_info['fname'], user_info['lname'], user_info['artist_name'], user_info['email'],
                                user_info['password'])
    if ok:
        response_data = {
            'message': 'User registered successfully',
            'user_info': {
                'fname': user_info['fname'],
                'lname': user_info['lname'],
                'artist_name': user_info['artist_name'],
                'email': user_info['email'],
            }
        }
        logger.info('user created', user_info)
        return jsonify(response_data), 201
    else:
        logger.info('user failed to create', user_info)
        return jsonify({'error': "bad request"}), 400


@app.route("/user", methods=['GET'])
def get_user_info():
    user_info = request.form.to_dict()
    logger.info('user requested user', user_info)

    if user_info['artist_name'] == '':
        logger.info('bad request', user_info)
        return jsonify({'error': 'bad request'}), 400

    _user = user.find_user_by_artist_name(user_info['artist_name'])
    if _user:
        response_data = {
            'message': 'user found',
            'user_info': {
                'userID': _user[0],
                'fname': _user[1],
                'lname': _user[2],
                'artist_name': _user[3],
                'email': _user[5],
            }
        }
        logger.info('user found', user_info)
        return jsonify(response_data), 200
    else:
        logger.info('user not found', user_info)
        return jsonify({'error': 'user not found'}), 404


@app.route("/user", methods=['DELETE'])
def delete_user():
    user_info = request.form.to_dict()
    logger.info('user requested update user', user_info)

    if user_info['artist_name'] == '' and user_info['password'] == '':
        logger.info('bad request', user_info)
        return jsonify({'error': 'bad request'}), 400

    ok = user.check_user(user_info['artist_name'], user_info['password'])

    if ok:
        userID = user.find_user_id_by_artist_name(user_info['artist_name'])
        user.delete_user_by_id(userID)
        logger.info('user deleted', user_info)
        return jsonify({'ok': 'user deleted successfully'}), 200
    else:
        logger.info('user not found', user_info)
        return jsonify({'error': 'user not found'}), 404


@app.route("/user", methods=['PATCH'])
def update_user():
    user_info = request.form.to_dict()
    logger.info('user requested update user', user_info)

    if user_info['artist_name'] == '' and user_info['password'] == '':
        logger.info('bad request', user_info)
        return jsonify({'error': 'bad request'}), 400

    ok = user.check_user(user_info['artist_name'], user_info['password'])

    if ok:
        _user = user.find_user_by_artist_name(user_info['artist_name'])

        user_info_new = util.check_for_key_user(user_info, ['fname', 'lname', 'email'], _user)
        user.update_user_data(user_info_new['fname'], user_info_new['lname'], user_info_new['email'], _user[0])

        if 'new_password' in user_info.keys():
            if user_info['new_password'] != '':
                user.update_user_password(user_info['artist_name'], user_info['new_password'])

        logger.info('user updated', user_info)
        return jsonify({'ok': 'user updated successfully'}), 200
    else:
        logger.info('user not found', user_info)
        return jsonify({'error': 'user not found'}), 404


@app.route("/login", methods=['POST'])
def login_user():
    user_info = request.form.to_dict()
    logger.info('user requested login', user_info)

    if user_info['artist_name'] == '' or user_info['password'] == '':
        logger.info('bad request', user_info)
        return jsonify({'error': 'bad request'}), 400

    ok = user.check_user(user_info['artist_name'], user_info['password'])
    if ok:
        logger.info('user logged in', user_info)
        return jsonify({'ok': 'logged in successfully'}), 200
    else:
        logger.info('user failed to login', user_info)
        return jsonify({'error': 'artist name or password is wrong'}), 401


@app.route("/song", methods=['POST'])
def new_song():
    song_info = request.form.to_dict()
    data = request.files.to_dict()
    logger.info('user requested new song', song_info)

    if song_info['title'] == '' or song_info['album_name'] == '' or song_info['genre'] == '' or song_info[
        'artist_name'] == '' or song_info['password'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    if 'music' not in data.keys():
        logger.info('music file not found', song_info)
        return jsonify({'error': 'music file not found'}), 400

    if data['music'].filename.split('.')[-1] != 'mp3':
        logger.info('file format is not correct', song_info)
        return jsonify({'error': 'music file format is not correct'}), 400

    if 'cover' in data.keys():
        if data['cover'].filename.split('.')[-1] != 'jpg' and data['cover'].filename.split('.')[-1] != 'jpeg' and data['cover'].filename.split('.')[-1] != 'png':
            logger.info('file format is not correct', song_info)
            return jsonify({'error': 'cover file format is not correct'}), 400

    ok = user.check_user(song_info['artist_name'], song_info['password'])
    if ok:
        music_name = song_info['artist_name'] + '@' + song_info['title'] + '.mp3'
        cover_name = song_info['artist_name'] + '@' + song_info['title'] + '.jpg'
        logger.info('uploading music to s3')
        arvan_uploader(config.s3_url, config.access_key, config.secret_key, config.music_bucket, data['music'],
                       music_name)
        logger.info('finished uploading music to s3')

        if 'cover' in data.keys():
            logger.info('uploading cover to s3')
            arvan_uploader(config.s3_url, config.access_key, config.secret_key, config.cover_bucket, data['cover'],
                           cover_name)
            logger.info('finished uploading cover to s3')

            song_id = music.insert_musics_data(song_info['title'], song_info['album_name'], music_name,
                                               cover_name, song_info['genre'], song_info['artist_name'])
        else:
            song_id = music.insert_musics_data(song_info['title'], song_info['album_name'], music_name,
                                               '', song_info['genre'], song_info['artist_name'])

        if new_song:
            response_data = {
                'message': 'song uploaded successfully',
                'song_info': {
                    'song_id': song_id,
                    'title': song_info['title'],
                    'album_name': song_info['album_name'],
                    'genre': song_info['genre'],
                    'artist_name': song_info['artist_name'],
                }
            }
            logger.info('new song added', song_info)
            return jsonify(response_data), 201
        else:
            return jsonify({'error': 'cant add music'}), 400
    else:
        logger.info('username or password is incorrect', song_info)
        return jsonify({'error': 'artist name or password is wrong'}), 401


@app.route("/song", methods=['GET'])
def get_song_info():
    song_info = request.form.to_dict()
    logger.info('user requested song', song_info)

    if song_info['title'] == '' or song_info['artist_name'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.get_musics_by_title_artist(song_info['title'], song_info['artist_name'])
    if song:
        response_data = {
            'message': 'song found',
            'song_info': {
                'title': song_info['title'],
                'duration': song_info['duration'],
                'genre': song_info['genre'],
                'likes': song_info['likes'],
                'album_name': song_info['album_name'],
                'artist_name': song_info['artist_name'],
            }
        }
        logger.info('song found', song_info)
        return jsonify(response_data), 200
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/song", methods=['DELETE'])
def delete_song():
    song_info = request.form.to_dict()
    logger.info('user requested delete song', song_info)

    if song_info['songID'] == '' and song_info['userID'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_id(song_info['userID'])

    belongs = music.check_song_belong_to_user(song_info['userID'], song_info['songID'])
    if belongs is None:
        logger.info('user is not authorized to do this action', song_info)
        return jsonify({'error': 'user is not authorized to do this action'}), 401
    if song and _user:
        music.delete_music_by_id(song_info['songID'])
        logger.info('song deleted', song_info)
        return jsonify({'ok': 'song deleted successfully'}), 200
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/song", methods=['PATCH'])
def update_song():
    song_info = request.form.to_dict()
    logger.info('user requested update song', song_info)

    if song_info['songID'] == '' and song_info['userID'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_id(song_info['userID'])

    song_info = util.check_for_key_song(song_info, ['songID', 'userID', 'title', 'album_name', 'genre'], song)

    belongs = music.check_song_belong_to_user(song_info['userID'], song_info['songID'])
    if belongs is None:
        logger.info('user is not authorized to do this action', song_info)
        return jsonify({'error': 'user is not authorized to do this action'}), 401
    if song and _user:
        music.update_music_data(song_info['title'], song_info['album_name'], song_info['genre'], song_info['songID'])
        logger.info('song updated', song_info)
        return jsonify({'ok': 'song updated successfully'}), 200
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/allsongs", methods=['GET'])
def get_all_songs():
    logger.info('user requested all songs')

    songs = music.get_all_musics()
    for i in range(len(songs)):
        songs[i] = list(songs[i])
        songs[i][4] = 'http://37.32.11.62:8080/songbyid/' + songs[i][4]
        if songs[i][5] != '':
            songs[i][5] = 'http://37.32.11.62:8080/coverbyid/' + songs[i][5]
    random.shuffle(songs)
    if songs:
        response_data = {
            'message': 'songs found',
            'songs_info': songs
        }
        logger.info('songs found')
        return jsonify(response_data), 200
    else:
        logger.info('songs not found')
        return jsonify({'error': 'songs not found'}), 200


@app.route("/songbyid/<music_filename>", methods=['GET'])
def get_music_by_id(music_filename):
    logger.info('user requested song by id', music_filename)

    song_info = music_filename.split('@')
    if song_info[1]:
        logger.info('downloading music from s3')
        arvan_downloader(config.s3_url, config.access_key, config.secret_key, config.music_bucket, music_filename,
                         'music')
        logger.info('finished downloading music from s3')

        logger.info('song found', music_filename)
        return send_from_directory(app.config['SONGS'], music_filename), 200
    else:
        logger.info('song not found', music_filename)
        return jsonify({'error': 'song not found'}), 404


@app.route("/coverbyid/<cover_filename>", methods=['GET'])
def get_cover_by_id(cover_filename):
    logger.info('user requested cover by id', cover_filename)

    cover_info = cover_filename.split('@')
    if cover_info[1]:
        logger.info('downloading cover from s3')
        arvan_downloader(config.s3_url, config.access_key, config.secret_key, config.cover_bucket, cover_filename,
                         'cover')
        logger.info('finished downloading cover from s3')

        logger.info('cover found', cover_filename)
        return send_from_directory(app.config['SONGS_COVER'], cover_filename), 200
    else:
        logger.info('cover not found', cover_filename)
        return jsonify({'error': 'cover not found'}), 404


@app.route("/interact", methods=['POST'])
def like_or_dislike_song():
    song_info = request.form.to_dict()
    logger.info('user requested like song', song_info)

    if song_info['songID'] == '' or song_info['userID'] == '' or song_info['action'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_artist_name(song_info['userID'])
    if song and _user:
        ok = util.like_or_dislike_music(_user[0], song_info['songID'], song_info['action'])
        if ok:
            logger.info('interaction done', song_info)
            return jsonify({'ok': 'song liked successfully'}), 201
        else:
            logger.info('interaction failed', song_info)
            return jsonify({'error': 'bad request'}), 400
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/checklike", methods=['GET'])
def check_like():
    song_info = request.form.to_dict()
    logger.info('user requested check like', song_info)

    if song_info['songID'] == '' or song_info['userID'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_artist_name(song_info['userID'])
    if song and _user:
        ok = music_interaction.check_like_status(_user[0], song_info['songID'])
        if ok:
            logger.info('user liked song', song_info)
            return jsonify({'ok': 'user liked song'}), 200
        else:
            logger.info('user did not like song', song_info)
            return jsonify({'ok': 'user did not like song'}), 209
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/checkdislike", methods=['GET'])
def check_dislike():
    song_info = request.form.to_dict()
    logger.info('user requested check dislike', song_info)

    if song_info['songID'] == '' or song_info['userID'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_artist_name(song_info['userID'])
    if song and _user:
        ok = music_interaction.check_dislike_status(_user[0], song_info['songID'])
        if ok:
            logger.info('user disliked song', song_info)
            return jsonify({'ok': 'user disliked song'}), 200
        else:
            logger.info('user did not dislike song', song_info)
            return jsonify({'ok': 'user did not dislike song'}), 209
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/report", methods=['POST'])
def report_song():
    song_info = request.form.to_dict()
    logger.info('user requested report song', song_info)

    if song_info['songID'] == '' or song_info['userID'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_artist_name(song_info['userID'])
    if song and _user:
        ok = music_interaction.report_music(_user[0], song_info['songID'])
        if ok:
            logger.info('song reported', song_info)
            return jsonify({'ok': 'song reported successfully'}), 200
        else:
            logger.info('song failed to report', song_info)
            return jsonify({'error': 'bad request'}), 400
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/checkreport", methods=['GET'])
def check_report():
    song_info = request.form.to_dict()
    logger.info('user requested check report', song_info)

    if song_info['songID'] == '' or song_info['userID'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_artist_name(song_info['userID'])
    if song and _user:
        ok = music_interaction.check_report_status(_user[0], song_info['songID'])
        if ok:
            logger.info('user reported song', song_info)
            return jsonify({'ok': 'user reported song'}), 200
        else:
            logger.info('user did not report song', song_info)
            return jsonify({'ok': 'user did not report song'}), 209
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/comment", methods=['POST'])
def comment_song():
    song_info = request.form.to_dict()
    logger.info('user requested comment song', song_info)

    if song_info['songID'] == '' or song_info['userID'] == '' or song_info['comment'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info['songID'])
    _user = user.find_user_by_artist_name(song_info['userID'])
    if song and _user:
        ok = music_interaction.comment_music(_user[0], song_info['songID'], song_info['comment'])
        if ok:
            logger.info('song commented', song_info)
            return jsonify({'ok': 'song commented successfully'}), 201
        else:
            logger.info('song failed to comment', song_info)
            return jsonify({'error': 'bad request'}), 400
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/comments", methods=['GET'])
def get_comments():
    song_info = request.args.get('songId')
    logger.info('user requested get comments', song_info)

    if song_info == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.check_music_exist_by_id(song_info)
    if song:
        comments = music_interaction.get_comments(song_info)
        if comments:
            logger.info('comments found', song_info)
            return jsonify({'ok': 'comments found', 'comments': comments}), 200
        else:
            logger.info('no comments found', song_info)
            return jsonify({'ok': 'no comments found'}), 404
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/usersongs/<user_id>", methods=['GET'])
def get_songs_by_artist(user_id):
    logger.info('user requested get songs by artist', user_id)

    if user_id == '':
        logger.info('bad request', user_id)
        return jsonify({'error': 'bad request'}), 400

    _user = user.find_user_by_artist_name(user_id)
    if _user:
        musics = music.find_music_by_publisher_id(_user[0])
        for i in range(len(musics)):
            musics[i] = list(musics[i])
            musics[i][4] = 'http://37.32.11.62:8080/songbyid/' + musics[i][4]
            if musics[i][5] != '':
                musics[i][5] = 'http://37.32.11.62:8080/coverbyid/' + musics[i][5]
        if musics:
            logger.info('songs found', user_id)
            return jsonify({'ok': 'songs found', 'songs': musics}), 200
        else:
            logger.info('no songs found', user_id)
            return jsonify({'ok': 'no songs found'}), 200
    else:
        logger.info('artist not found', user_id)
        return jsonify({'error': 'artist not found'}), 404


@app.route("/playlist", methods=['POST'])
def new_playlist():
    playlist_info = request.form.to_dict()
    logger.info('user requested new playlist', playlist_info)

    if playlist_info['name'] == '' or playlist_info['userID'] == '':
        logger.info('bad request', playlist_info)
        return jsonify({'error': 'bad request'}), 400

    _user = user.find_user_by_id(playlist_info['userID'])
    if _user:
        desc = ''
        if 'description' in playlist_info.keys():
            desc = playlist_info['description']

        playlist.insert_playlist_data(playlist_info['name'], _user[0], desc, "N")
        response_data = {
            'message': 'playlist created successfully',
            'playlist_info': {
                'name': playlist_info['name'],
                'userID': _user[0],
                'description': desc,
                'is_public': False,
            }
        }
        logger.info('new playlist added', playlist_info)
        return jsonify(response_data), 201
    else:
        logger.info('artist not found', playlist_info)
        return jsonify({'error': 'artist not found'}), 404


@app.route("/playlist", methods=['GET'])
def get_playlists_by_user():
    playlist_info = request.form.to_dict()
    logger.info('user requested playlists', playlist_info)

    if playlist_info['userID'] == '':
        logger.info('bad request', playlist_info)
        return jsonify({'error': 'bad request'}), 400

    _user = user.find_user_by_id(playlist_info['userID'])
    if _user:
        playlists = playlist.find_playlist_by_user_id(_user[0])
        if playlists:
            logger.info('playlists found', playlist_info)
            return jsonify({'ok': 'playlists found', 'playlists': playlists}), 200
        else:
            logger.info('no playlists found', playlist_info)
            return jsonify({'ok': 'no playlists found'}), 200
    else:
        logger.info('artist not found', playlist_info)
        return jsonify({'error': 'artist not found'}), 404


@app.route("/getplaylist", methods=['GET'])
def get_playlist_info():
    playlist_info = request.form.to_dict()
    logger.info('user requested playlist', playlist_info)

    if playlist_info['playlistID'] == '':
        logger.info('bad request', playlist_info)
        return jsonify({'error': 'bad request'}), 400

    _playlist = playlist.find_playlist_by_id(playlist_info['playlistID'])
    if _playlist is not None:
        # musics = playlist_musics.find_all_songs_by_playlist_id(_playlist[0])
        musics = playlist_musics.find_all_songs_with_full_info_by_playlist_id(_playlist[0])
        for i in range(len(musics)):
            musics[i] = list(musics[i])
            musics[i][4] = 'http://37.32.11.62:8080/songbyid/' + musics[i][4]
            if musics[i][5] != '':
                musics[i][5] = 'http://37.32.11.62:8080/coverbyid/' + musics[i][5]

        response_data = {
            'musics': musics,
        }
        logger.info('playlist found', playlist_info)
        return jsonify(response_data), 200
    else:
        logger.info('playlist not found', playlist_info)
        return jsonify({'error': 'playlist not found'}), 404


@app.route("/playlist", methods=['DELETE'])
def delete_playlist():
    playlist_info = request.form.to_dict()
    logger.info('user requested delete playlist', playlist_info)

    if playlist_info['playlistID'] == '' and playlist_info['userID'] == '':
        logger.info('bad request', playlist_info)
        return jsonify({'error': 'bad request'}), 400

    plist = playlist.find_playlist_by_id(playlist_info['playlistID'])
    _user = user.find_user_by_id(playlist_info['userID'])

    belongs = playlist.check_playlist_belong_to_user(playlist_info['userID'], playlist_info['playlistID'])
    if belongs is None:
        logger.info('user is not authorized to do this action', playlist_info)
        return jsonify({'error': 'user is not authorized to do this action'}), 401
    if plist and _user:
        playlist.delete_playlist_by_id(playlist_info['playlistID'])
        logger.info('playlist deleted', playlist_info)
        return jsonify({'ok': 'playlist deleted successfully'}), 200
    else:
        logger.info('playlist not found', playlist_info)
        return jsonify({'error': 'playlist not found'}), 404


@app.route("/addplaylist", methods=['POST'])
def add_music_to_playlist():
    playlist_info = request.form.to_dict()
    logger.info('user requested add music to playlist', playlist_info)

    if playlist_info['playlistID'] == '' or playlist_info['songID'] == '' or playlist_info['userID'] == '':
        logger.info('bad request', playlist_info)
        return jsonify({'error': 'bad request'}), 400

    plist = playlist.find_playlist_by_id(playlist_info['playlistID'])
    _user = user.find_user_by_id(playlist_info['userID'])
    song = music.check_music_exist_by_id(playlist_info['songID'])

    belongs = playlist.check_playlist_belong_to_user(playlist_info['userID'], playlist_info['playlistID'])
    if playlist_musics.check_if_music_in_playlist(playlist_info['playlistID'], playlist_info['songID']) is not None:
        logger.info('music already in playlist', playlist_info)
        return jsonify({'error': 'music already in playlist'}), 409
    if belongs is None:
        logger.info('user is not authorized to do this action', playlist_info)
        return jsonify({'error': 'user is not authorized to do this action'}), 401
    if plist and _user and song:
        playlist_musics.insert_playlist_musics_data(playlist_info['playlistID'], playlist_info['songID'])
        logger.info('music added to playlist', playlist_info)
        return jsonify({'ok': 'music added to playlist successfully'}), 200
    else:
        logger.info('playlist not found', playlist_info)
        return jsonify({'error': 'playlist not found'}), 404


@app.route("/removeplaylist", methods=['DELETE'])
def remove_music_from_playlist():
    playlist_info = request.form.to_dict()
    logger.info('user requested remove music from playlist', playlist_info)

    if playlist_info['playlistID'] == '' or playlist_info['songID'] == '' or playlist_info['userID'] == '':
        logger.info('bad request', playlist_info)
        return jsonify({'error': 'bad request'}), 400

    plist = playlist.find_playlist_by_id(playlist_info['playlistID'])
    _user = user.find_user_by_id(playlist_info['userID'])
    song = music.check_music_exist_by_id(playlist_info['songID'])

    belongs = playlist.check_playlist_belong_to_user(playlist_info['userID'], playlist_info['playlistID'])
    if belongs is None:
        logger.info('user is not authorized to do this action', playlist_info)
        return jsonify({'error': 'user is not authorized to do this action'}), 401
    if plist and _user and song:
        playlist_musics.delete_playlist_musics_by_playlist_id_and_music_id(playlist_info['playlistID'], playlist_info['songID'])
        logger.info('music removed from playlist', playlist_info)
        return jsonify({'ok': 'music removed from playlist successfully'}), 200
    else:
        logger.info('playlist not found', playlist_info)
        return jsonify({'error': 'playlist not found'}), 404


if __name__ == "__main__":
    logger.info("starting server")
    logger.info("server is started on port:" + config.port)
    serve(app, host=config.host, port=config.port)
