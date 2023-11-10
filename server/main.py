from waitress import serve
import dotenv
import os
from flask import Flask, request, Response


app = Flask(__name__)

dotenv.load_dotenv('.env')
port = os.getenv("PORT")


@app.route("/healthz", methods=['GET'])
def healthz():
    return "ok"


@app.route("/register", methods=['POST'])
def register_user():
    pass


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
