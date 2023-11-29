import sqlite3
from datetime import datetime

from model.user import find_user_id_by_artist_name

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


def connect_to_database(database_name='./model/db/Flow.db'):
    """
    Connect to the SQLite database and return the connection and cursor.
    """
    conn = sqlite3.connect(database_name)
    cursor = conn.cursor()
    return conn, cursor


def create_music(cursor):
    """
    `Create musics and users tables.
    """
    cursor.execute(create_musics_table_command)


def close_connection(conn):
    """
    Commit changes and close the database connection.
    """
    conn.commit()
    conn.close()


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

    return find_music_by_title(title)[0][0]



def update_music_data(title, album_name, genre, music_id):
    """
    Insert data into the musics table.
    """
    conn, cursor = connect_to_database()

    music = [title, album_name, genre, music_id]
    cursor.execute('UPDATE musics SET title = ?, album_name = ?, genre = ? WHERE music_id = ?', music)

    close_connection(conn)


def check_music_exist(title, album_name, genre, duration, artist_name):
    """
    Insert data into the musics table.
    """
    conn, cursor = connect_to_database()

    publisher_id = find_user_id_by_artist_name(artist_name)
    music = [title, album_name, genre, duration, publisher_id]
    cursor.execute(
        'SELECT * FROM musics WHERE title = ?, album_name = ?, genre = ?, duration = ? WHERE publisher_id = ?', music)
    result = cursor.fetchone()

    close_connection(conn)

    return result if result else None


def delete_music_by_id(music_id):
    conn, cursor = connect_to_database()

    cursor.execute('DELETE FROM musics WHERE music_id = ?', (music_id,))

    close_connection(conn)


def drop_music_table():
    conn, cursor = connect_to_database()
    cursor.execute('DROP TABLE IF EXISTS musics')

    close_connection(conn)


def find_music_by_id(music_id):
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM musics WHERE music_id = ?', (music_id,))
    result = cursor.fetchone()

    close_connection(conn)

    return result if result else None


def find_music_by_title(title):
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM musics WHERE title = ?', (title,))
    result = cursor.fetchall()

    close_connection(conn)

    return result if result else None


def find_music_by_genre(genre):
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM musics WHERE genre = ?', (genre,))
    result = cursor.fetchall()

    close_connection(conn)

    return result if result else None


def find_music_by_album_name(album_name):
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM musics WHERE album_name = ?', (album_name,))
    result = cursor.fetchall()

    close_connection(conn)

    return result if result else None


def find_music_by_publisher_id(publisher_id):
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM musics WHERE publisher_id = ?', (publisher_id,))
    result = cursor.fetchall()

    close_connection(conn)

    return result if result else None


def get_musics_by_title_artist(title, artist_name):
    conn, cursor = connect_to_database()

    publisher_id = find_user_id_by_artist_name(artist_name)
    music = [title, publisher_id]
    cursor.execute('SELECT * FROM musics WHERE title = ? AND publisher_id = ?', music)
    result = cursor.fetchone()

    close_connection(conn)

    return result if result else None


def get_all_musics():
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM musics')
    result = cursor.fetchall()

    close_connection(conn)

    return result if result else None
