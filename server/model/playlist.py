import sqlite3
from datetime import datetime

create_playlist_table_command = """
CREATE TABLE Playlists (
    playlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    creation_date DATE NOT NULL,
    user_id INT,
    description VARCHAR(255),
    is_public VARCHAR(1) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
"""


def connect_to_database(database_name='./model/db/Flow.db'):
    """
    Connect to the SQLite database and return the connection and cursor.
    """
    conn = sqlite3.connect(database_name)
    cursor = conn.cursor()
    return conn, cursor


def create_playlist_table(cursor):
    """
    `Create musics and users tables.
    """
    cursor.execute(create_playlist_table_command)


def close_connection(conn):
    """
    Commit changes and close the database connection.
    """
    conn.commit()
    conn.close()


def insert_playlist_data(name, user_id, description, is_public):
    """
    Insert data into the users table.
    """
    conn, cursor = connect_to_database()

    creation_date = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

    playlist = [name, creation_date, user_id, description, is_public]
    cursor.execute(
        'INSERT INTO Playlists (name, creation_date, user_id, description, is_public) VALUES (?, ?, ?, ?, ?)',
        playlist)

    close_connection(conn)


def find_playlist_by_name(name):
    """
    Find a user by their email.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM Playlists WHERE name=?', (name,))
    playlist = cursor.fetchall()

    close_connection(conn)

    return playlist


def find_playlist_by_id(playlist_id):
    """
    Find a user by their email.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM Playlists WHERE playlist_id=?', (playlist_id,))
    playlist = cursor.fetchall()

    close_connection(conn)

    return playlist


def find_playlist_by_user_id(user_id):
    """
    Find a user by their email.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM Playlists WHERE user_id=?', (user_id,))
    playlist = cursor.fetchall()

    close_connection(conn)

    return playlist


def delete_playlist_by_id(playlist_id):
    """
    Delete a user from the users table by user_id.
    """
    conn, cursor = connect_to_database()

    cursor.execute('DELETE FROM Playlists WHERE playlist_id = ?', (playlist_id,))

    close_connection(conn)


def drop_playlist_table():
    """
    Drop the users table.
    """
    conn, cursor = connect_to_database()
    cursor.execute('DROP TABLE IF EXISTS Playlists')

    close_connection(conn)

