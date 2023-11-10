import hashlib
import re
from datetime import datetime
import sqlite3

create_users_table_command = """CREATE TABLE users ( 
user_id INTEGER PRIMARY KEY AUTOINCREMENT, 
fname VARCHAR(20), 
lname VARCHAR(30), 
artist_name VARCHAR(30) UNIQUE, 
verified CHAR(1), 
email VARCHAR(30), 
password VARCHAR(100), 
gender CHAR(1), 
joining_date DATE
);"""

create_musics_table_command = """CREATE TABLE musics ( 
music_id INTEGER PRIMARY KEY AUTOINCREMENT, 
title VARCHAR(50), 
publisher_id INTEGER, 
album_name VARCHAR(50), 
file_path VARCHAR(100), 
cover_path VARCHAR(100), 
genre VARCHAR(20), 
likes INTEGER, 
reports INTEGER,
duration INTEGER, 
release_date DATE, 
CONSTRAINT fk_users 
    FOREIGN KEY (publisher_id) 
    REFERENCES users(user_id) 
);"""


def connect_to_database(database_name='./../db/Flow.db'):
    """
    Connect to the SQLite database and return the connection and cursor.
    """
    conn = sqlite3.connect(database_name)
    cursor = conn.cursor()
    return conn, cursor


def create_tables(cursor):
    """
    `Create musics and users tables.
    """
    cursor.execute(create_users_table_command)
    cursor.execute(create_musics_table_command)


def close_connection(conn):
    """
    Commit changes and close the database connection.
    """
    conn.commit()
    conn.close()


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


def insert_users_data(fname, lname, artist_name, verified, email, password, gender):
    """
    Insert data into the users table.
    """
    conn, cursor = connect_to_database()

    current_datetime = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    password = hash_password(password)
    if verified == 'N' or verified == 'NO':
        verified = 'N'
    elif verified == 'Y' or verified == 'YES':
        verified = 'Y'

    if gender == 'male':
        gender = 'M'
    elif gender == 'female':
        gender = 'Y'

    ok = is_valid_email(email)
    if ok:
        user = [fname, lname, artist_name, verified, email, password, gender, current_datetime]
        print(user)
        cursor.execute(
            'INSERT INTO users (fname, lname, artist_name, verified, email, password, gender, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
            user)
        close_connection(conn)
        return True
    else:
        close_connection(conn)
        return False


def insert_musics_data(title, album_name, file_path, cover_path, genre, duration, artist_name):
    """
    Insert data into the musics table.
    """
    conn, cursor = connect_to_database()

    release_date = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    reports = 0
    likes = 0
    publisher_id = find_user_id_by_artist_name(artist_name)
    music = [title, publisher_id, album_name, file_path, cover_path, genre, likes, reports, duration, release_date]
    cursor.execute(
        'INSERT INTO musics (title, publisher_id, album_name, file_path, cover_path, genre, likes, reports, duration, release_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
        music)

    close_connection(conn)


def delete_user_by_id(user_id):
    conn, cursor = connect_to_database()

    cursor.execute('DELETE FROM users WHERE user_id = ?', (user_id,))

    close_connection(conn)


def delete_music_by_id(music_id):
    conn, cursor = connect_to_database()

    cursor.execute('DELETE FROM musics WHERE music_id = ?', (music_id,))

    close_connection(conn)


def drop_tables():
    conn, cursor = connect_to_database()
    cursor.execute('DROP TABLE IF EXISTS users')

    cursor.execute('DROP TABLE IF EXISTS musics')

    close_connection(conn)


def find_user_id_by_artist_name(artist_name):
    conn, cursor = connect_to_database()

    cursor.execute('SELECT user_id FROM users WHERE artist_name = ?', (artist_name,))
    result = cursor.fetchone()

    close_connection(conn)

    return result[0] if result else None
