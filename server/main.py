import logging.config
from flask import Flask, request, jsonify, send_from_directory
from waitress import serve
from model import user, music
from configs import config
from objectStorage.s3 import arvan_uploader, arvan_downloader
import os

app = Flask(__name__)
logging.basicConfig(level=logging.NOTSET, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger()
logger.setLevel(logging.NOTSET)

app.config["SONGS_COVER"] = os.getenv("HOME")+'/ShibaFlow/server/Flows'
app.config["SONGS"] = os.getenv("HOME")+'/ShibaFlow/server/Cover_flows'


@app.route("/healthz", methods=['GET'])
def healthz():
    logger.info("called")
    return "ok"


@app.route("/register", methods=['POST'])
def register_user():
    user_info = request.form.to_dict()
    logger.info('user requested sign in', user_info)

    exist = user.user_exists(user_info['artist_name'])

    if exist:
        logger.info('user already exist', user_info)
        return jsonify({'error': "user with this artist name already exist"}), 409

    ok = user.insert_users_data(user_info['fname'], user_info['lname'], user_info['artist_name'], user_info['email'],
                                user_info['password'], user_info['gender'])
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

    ok = user.check_user(song_info['artist_name'], song_info['password'])
    if ok:
        music_name = 'http://195.248.242.169:8080/songbyid/' + song_info['artist_name'] + '@' + song_info['title'] + '.mp3'
        cover_name = 'http://195.248.242.169:8080/coverbyid/' + song_info['artist_name'] + '@' + song_info['title'] + '.jpg'
        logger.info('uploading music to s3')
        arvan_uploader(config.s3_url, config.access_key, config.secret_key, config.music_bucket,
                       data['music'], music_name)
        logger.info('finished uploading music to s3')

        logger.info('uploading cover to s3')
        arvan_uploader(config.s3_url, config.access_key, config.secret_key, config.cover_bucket,
                       data['cover'], cover_name)
        logger.info('finished uploading cover to s3')

        song_id = music.insert_musics_data(song_info['title'], song_info['album_name'], music_name,
                                           cover_name, song_info['genre'], song_info['duration'],
                                           song_info['artist_name'])
        if new_song:
            response_data = {
                'message': 'song uploaded successfully',
                'song_info': {
                    'song_id': song_id,
                    'title': song_info['title'],
                    'album_name': song_info['album_name'],
                    'genre': song_info['genre'],
                    'duration': song_info['duration'],
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

    ok = user.check_user(song_info['artist_name'], song_info['password'])
    if ok:
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
    else:
        logger.info('username or password is incorrect', song_info)
        return jsonify({'error': 'artist name or password is wrong'}), 401


@app.route("/allsongs", methods=['GET'])
def get_all_songs():
    song_info = request.form.to_dict()
    logger.info('user requested all songs', song_info)

    songs = music.get_all_musics()
    if songs:
        response_data = {
            'message': 'songs found',
            'songs_info': songs
        }
        logger.info('songs found', song_info)
        return jsonify(response_data), 200
    else:
        logger.info('songs not found', song_info)
        return jsonify({'error': 'songs not found'}), 404


@app.route("/songbyid/<music_filename>", methods=['GET'])
def get_music_by_id(music_filename):
    logger.info('user requested song by id', music_filename)

    song_info = music_filename.split('@')
    if song_info[1]:
        logger.info('downloading music from s3')
        arvan_downloader(config.s3_url, config.access_key, config.secret_key, config.music_bucket, music_filename, 'music')
        logger.info('finished downloading music from s3')

        logger.info('song found', music_filename)
        return send_from_directory(app.config['SONGS'], music_filename)
    else:
        logger.info('song not found', music_filename)
        return jsonify({'error': 'song not found'}), 404


@app.route("/coverbyid/<cover_filename>", methods=['GET'])
def get_cover_by_id(cover_filename):
    logger.info('user requested cover by id', cover_filename)

    cover_info = cover_filename.split('@')
    if cover_info[1]:
        logger.info('downloading cover from s3')
        arvan_downloader(config.s3_url, config.access_key, config.secret_key, config.cover_bucket, cover_filename, 'cover')
        logger.info('finished downloading cover from s3')

        logger.info('cover found', cover_filename)
        return send_from_directory(app.config['SONGS_COVER'], cover_filename)
    else:
        logger.info('cover not found', cover_filename)
        return jsonify({'error': 'cover not found'}), 404


@app.route("/like", methods=['GET'])
def like_song():
    pass


@app.route("/report", methods=['GET'])
def report_song():
    pass


if __name__ == "__main__":
    logger.info("starting server")
    logger.info("server is started on port:" + config.port)
    serve(app, host="0.0.0.0", port=config.port)
