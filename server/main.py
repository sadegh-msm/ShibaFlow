import logging.config
from flask import Flask, request, jsonify, send_from_directory
from waitress import serve
from model import user, music, music_interaction
from configs import config
from objectStorage.s3 import arvan_uploader, arvan_downloader
from utils import util
import os

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

    infos = ['fname', 'lname', 'artist_name', 'email', 'password']
    if util.check_for_key(user_info, infos):
        logger.info('bad request', user_info)
        return jsonify({'error': 'bad request'}), 400

    if user_info['fname'] == '' or user_info['lname'] == '' or user_info['artist_name'] == '' or user_info[
        'email'] == '' or \
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


@app.route("/login", methods=['POST'])
def login_user():
    user_info = request.form.to_dict()
    logger.info('user requested login', user_info)

    infos = ['artist_name', 'password']
    if util.check_for_key(user_info, infos):
        logger.info('bad request', user_info)
        return jsonify({'error': 'bad request'}), 400

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

    infos = ['title', 'album_name', 'genre', 'artist_name', 'password']
    if util.check_for_key(song_info, infos):
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

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

    if data['cover'].filename.split('.')[-1] != 'jpg' and data['cover'].filename.split('.')[-1] != 'jpeg' and \
            data['cover'].filename.split('.')[-1] != 'png':
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

        if 'cover' in data.keys() or data['cover'].filename != '':
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

    infos = ['title', 'artist_name']
    if util.check_for_key(song_info, infos):
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

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


@app.route("/allsongs", methods=['GET'])
def get_all_songs():
    logger.info('user requested all songs')

    songs = music.get_all_musics()
    for i in range(len(songs)):
        songs[i] = list(songs[i])
        songs[i][4] = 'http://195.248.242.169:8080/songbyid/' + songs[i][4]
        if songs[i][5] != '':
            songs[i][5] = 'http://195.248.242.169:8080/coverbyid/' + songs[i][5]
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


@app.route("/like", methods=['GET'])
def like_song():
    song_info = request.form.to_dict()
    logger.info('user requested like song', song_info)

    infos = ['title', 'artist_name']
    if util.check_for_key(song_info, infos):
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    if song_info['title'] == '' or song_info['artist_name'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.get_musics_by_title_artist(song_info['title'], song_info['artist_name'])
    if song:
        ok = music.like_song(song[0])
        if ok:
            logger.info('song liked', song_info)
            return jsonify({'ok': 'song liked successfully'}), 200
        else:
            logger.info('song failed to like', song_info)
            return jsonify({'error': 'bad request'}), 400
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/likebyid", methods=['GET'])
def like_by_id():
    song_info = request.form.to_dict()
    logger.info('user requested like song by id', song_info)

    infos = ['music_id']
    if util.check_for_key(song_info, infos):
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    if song_info['music_id'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.find_music_by_id(song_info['music_id'])
    if song:
        ok = music.like_song(song[0])
        if ok:
            logger.info('song liked', song_info)
            return jsonify({'ok': 'song liked successfully'}), 200
        else:
            logger.info('song failed to like', song_info)
            return jsonify({'error': 'bad request'}), 400
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/report", methods=['GET'])
def report_song():
    song_info = request.form.to_dict()
    logger.info('user requested report song', song_info)

    infos = ['title', 'artist_name']
    if util.check_for_key(song_info, infos):
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    if song_info['title'] == '' or song_info['artist_name'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.get_musics_by_title_artist(song_info['title'], song_info['artist_name'])
    if song:
        ok = music.report_song(song[0])
        if ok:
            logger.info('song reported', song_info)
            return jsonify({'ok': 'song reported successfully'}), 200
        else:
            logger.info('song failed to report', song_info)
            return jsonify({'error': 'bad request'}), 400
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


@app.route("/reportbyid", methods=['GET'])
def report_by_id():
    song_info = request.form.to_dict()
    logger.info('user requested report song by id', song_info)

    infos = ['music_id']
    if util.check_for_key(song_info, infos):
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    if song_info['music_id'] == '':
        logger.info('bad request', song_info)
        return jsonify({'error': 'bad request'}), 400

    song = music.find_music_by_id(song_info['music_id'])
    if song:
        ok = music.report_song(song[0])
        if ok:
            logger.info('song reported', song_info)
            return jsonify({'ok': 'song reported successfully'}), 200
        else:
            logger.info('song failed to report', song_info)
            return jsonify({'error': 'bad request'}), 400
    else:
        logger.info('song not found', song_info)
        return jsonify({'error': 'song not found'}), 404


if __name__ == "__main__":
    logger.info("starting server")
    logger.info("server is started on port:" + config.port)
    serve(app, host=config.host, port=config.port)
