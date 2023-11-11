from flask import Flask, request, jsonify
from waitress import serve
from model import user
from configs import config

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
def register_song():
    pass


@app.route("/like", methods=['GET'])
def like_song():
    pass


@app.route("/report", methods=['GET'])
def report_song():
    pass


@app.route("/song", methods=['GET'])
def get_music():
    pass


if __name__ == "__main__":
    print("starting server")
    print("server is started on port:", config.port)
    serve(app, host="0.0.0.0", port=config.port)
