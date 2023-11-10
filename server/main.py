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



if __name__ == "__main__":
    print("starting server")
    print("server is started on port:", port)
    serve(app, host="0.0.0.0", port=port)
