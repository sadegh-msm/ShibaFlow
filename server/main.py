import binascii

from flask import Flask, request, jsonify
from waitress import serve
from model import user, music
from configs import config
from objectStorage.s3 import arvan_uploader, arvan_downloader

app = Flask(__name__)


@app.route("/healthz", methods=['GET'])
def healthz():
    return "ok"


@app.route("/register", methods=['POST'])
def register_user():
    user_info = request.form.to_dict()
    config.logging.info('user requested sign in', user_info)

    exist = user.user_exists(user_info['artist_name'])

    if exist:
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
        return jsonify(response_data), 201
    else:
        return jsonify({'error': "bad request"}), 400


@app.route("/login", methods=['POST'])
def login_user():
    user_info = request.form.to_dict()
    config.logging.info('user requested login', user_info)

    ok = user.check_user(user_info['artist_name'], user_info['password'])
    if ok:
        return jsonify({'ok': 'logged in successfully'}), 200
    else:
        return jsonify({'error': 'artist name or password is wrong'}), 401


@app.route("/song", methods=['POST'])
def new_song():
    song_info = request.form.to_dict()
    data = request.files.to_dict()
    config.logging.info('user requested new song', song_info)

    ok = user.check_user(song_info['artist_name'], song_info['password'])
    if ok:
        music_name = song_info['title'] + '.mp3'
        cover_name = song_info['title'] + '.jpg'
        config.logging.info('uploading music to s3')
        arvan_uploader(config.s3_url, config.access_key, config.secret_key, config.music_bucket,
                       data['music'], music_name)
        config.logging.info('finished uploading music to s3')

        config.logging.info('uploading cover to s3')
        arvan_uploader(config.s3_url, config.access_key, config.secret_key, config.cover_bucket,
                       data['cover'], cover_name)
        config.logging.info('finished uploading cover to s3')

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
            return jsonify(response_data), 201
    else:
        return jsonify({'error': 'artist name or password is wrong'}), 401


@app.route("/song", methods=['GET'])
def get_song():
    song_info = request.form.to_dict()
    config.logging.info('user requested song', song_info)

    ok = user.check_user(song_info['artist_name'], song_info['password'])
    if ok:
        song = music.get_musics_data(song_info['title'], song_info['album_name'], song_info['artist_name'])
        if song:
            music_name = song_info['title'] + '.mp3'
            cover_name = song_info['title'] + '.jpg'

            config.logging.info('downloading music from s3')
            arvan_downloader(config.s3_url, config.access_key, config.secret_key, config.music_bucket, music_name, 'music')
            config.logging.info('finished downloading music from s3')

            config.logging.info('downloading cover from s3')
            arvan_downloader(config.s3_url, config.access_key, config.secret_key, config.cover_bucket, cover_name, 'cover')
            config.logging.info('finished downloading cover from s3')

            song_file = open('./Flows/'+music_name, 'rb')
            cover_file = open('./Cover_flows/'+cover_name, 'rb')
            song_hex = binascii.hexlify(song_file.read())
            cover_hex = binascii.hexlify(cover_file.read())
            response_data = {
                'message': 'song found',
                'song_info': {
                    'title': song_info['title'],
                    'song': song_hex.decode('utf-8'),
                    'cover': cover_hex.decode('utf-8'),
                    'album_name': song_info['album_name'],
                    'artist_name': song_info['artist_name'],
                }
            }
            return jsonify(response_data), 200
        else:
            return jsonify({'error': 'song not found'}), 404
    else:
        return jsonify({'error': 'artist name or password is wrong'}), 401


@app.route("/like", methods=['GET'])
def like_song():
    pass


@app.route("/report", methods=['GET'])
def report_song():
    pass


if __name__ == "__main__":
    print("starting server")
    print("server is started on port:", config.port)
    serve(app, host="0.0.0.0", port=config.port)
