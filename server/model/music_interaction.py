import sqlite3

create_music_interactions = """
CREATE TABLE IF NOT EXISTS music_interactions (
    interaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    music_id INTEGER,
    comment_text TEXT,
    like_status BOOLEAN,
    dislike_status BOOLEAN,
    report_status BOOLEAN,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (music_id) REFERENCES musics(music_id)
);
"""


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
    cursor.execute(create_music_interactions)


def close_connection(conn):
    """
    Commit changes and close the database connection.
    """
    conn.commit()
    conn.close()


def insert_music_interactions_data(user_id, music_id, comment_text="", like_status=False, dislike_status=False, report_status=False):
    """
    Insert data into the musics table.
    """
    conn, cursor = connect_to_database()

    music_interaction = [user_id, music_id, comment_text, like_status, dislike_status, report_status]
    cursor.execute(
        'INSERT INTO music_interactions (user_id, music_id, comment_text, like_status, dislike_status, report_status) VALUES (?, ?, ?, ?, ?, ?)',
        music_interaction)

    close_connection(conn)


def find_music_interactions_by_music_id(music_id):
    """
    Find music interactions by music id.
    """
    conn, cursor = connect_to_database()
    cursor.execute('SELECT * FROM music_interactions WHERE music_id = ?', (music_id,))
    music_interactions = cursor.fetchall()
    close_connection(conn)
    return music_interactions


def find_music_interactions_by_user_id(user_id):
    """
    Find music interactions by user id.
    """
    conn, cursor = connect_to_database()
    cursor.execute('SELECT * FROM music_interactions WHERE user_id = ?', (user_id,))
    music_interactions = cursor.fetchall()
    close_connection(conn)
    return music_interactions


def find_music_interactions_by_user_id_and_music_id(user_id, music_id):
    """
    Find music interactions by user id and music id.
    """
    conn, cursor = connect_to_database()
    cursor.execute('SELECT * FROM music_interactions WHERE user_id = ? AND music_id = ?', (user_id, music_id))
    music_interactions = cursor.fetchall()
    close_connection(conn)
    return music_interactions


def like_music(user_id, music_id):
    """
    Like a music.
    """
    conn, cursor = connect_to_database()
    cursor.execute('UPDATE music_interactions SET like_status = ? WHERE user_id = ? AND music_id = ?', (True, user_id, music_id))
    close_connection(conn)


def dislike_music(user_id, music_id):
    """
    Dislike a music.
    """
    conn, cursor = connect_to_database()
    cursor.execute('UPDATE music_interactions SET dislike_status = ? WHERE user_id = ? AND music_id = ?', (True, user_id, music_id))
    close_connection(conn)


def report_music(user_id, music_id):
    """
    Report a music.
    """
    conn, cursor = connect_to_database()
    cursor.execute('UPDATE music_interactions SET report_status = ? WHERE user_id = ? AND music_id = ?', (True, user_id, music_id))
    close_connection(conn)


def comment_music(user_id, music_id, comment_text):
    """
    Comment a music.
    """
    conn, cursor = connect_to_database()
    cursor.execute('UPDATE music_interactions SET comment_text = ? WHERE user_id = ? AND music_id = ?', (comment_text, user_id, music_id))
    close_connection(conn)


def edit_comment_music(user_id, music_id, comment_text):
    """
    Edit comment a music.
    """
    conn, cursor = connect_to_database()
    cursor.execute('UPDATE music_interactions SET comment_text = ? WHERE user_id = ? AND music_id = ?', (comment_text, user_id, music_id))
    close_connection(conn)


def join_music_interactions_and_musics():
    """
    Join music interactions and musics.
    """
    conn, cursor = connect_to_database()
    cursor.execute('SELECT * FROM music_interactions INNER JOIN musics ON music_interactions.music_id = musics.music_id')
    music_interactions_and_musics = cursor.fetchall()
    close_connection(conn)
    return music_interactions_and_musics


def join_music_interactions_and_users():
    """
    Join music interactions and users.
    """
    conn, cursor = connect_to_database()
    cursor.execute('SELECT * FROM music_interactions INNER JOIN users ON music_interactions.user_id = users.user_id')
    music_interactions_and_users = cursor.fetchall()
    close_connection(conn)
    return music_interactions_and_users


def join_music_interactions_and_musics_and_users():
    """
    Join music interactions and musics and users.
    """
    conn, cursor = connect_to_database()
    cursor.execute('SELECT * FROM music_interactions INNER JOIN musics ON music_interactions.music_id = musics.music_id INNER JOIN users ON music_interactions.user_id = users.user_id')
    music_interactions_and_musics_and_users = cursor.fetchall()
    close_connection(conn)
    return music_interactions_and_musics_and_users

