import json
import os

import dotenv
from flask import Flask, request, jsonify
from waitress import serve
from model import user

app = Flask(__name__)

dotenv.load_dotenv('.env')
port = os.getenv("PORT")


@app.route("/healthz", methods=['GET'])
def healthz():
    return "ok"


@app.route("/register", methods=['POST'])
def register_user():
    user_info = json.loads(request.form['json_data'])
    print("request for login", user_info)

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
        return jsonify(response_data), 200
    else:
        return jsonify({'error': "cant create user"}), 400


@app.route("/login", methods=['POST'])
def login_user():
    user_info = json.loads(request.form['json_data'])
    print("request for login", user_info)


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
    print("server is started on port:", port)
    serve(app, host="0.0.0.0", port=port)
