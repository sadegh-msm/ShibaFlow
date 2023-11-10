from flask import Flask, render_template, flash, redirect, url_for, session, logging, request
from waitress import serve
import dotenv
import os
from flask import Flask, request, Response
from wtforms import Form, StringField, TextAreaField, PasswordField, validators
from passlib.hash import sha256_crypt
from itsdangerous import URLSafeTimedSerializer, SignatureExpired
import sqlite3


app = Flask(__name__)

sqliteConnection = sqlite3.connect('./db/Flow.db')
cursor = sqliteConnection.cursor()
dotenv.load_dotenv('.env')
s = URLSafeTimedSerializer('secret123')

port = os.getenv("PORT")


@app.route("/", methods=['GET'])
def hello_world():
    return ""


if __name__ == "__main__":
    print("starting server")
    print("server is started on port:", port)
    serve(app, host="0.0.0.0", port=port)
