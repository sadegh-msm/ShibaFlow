from utils import util
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


def connect_to_database(database_name='./model/db/ShibaFlow.db'):
    """
    Connect to the SQLite database and return the connection and cursor.
    """
    conn = sqlite3.connect(database_name)
    cursor = conn.cursor()
    return conn, cursor


def create_user_table(cursor):
    """
    `Create musics and users tables.
    """
    cursor.execute(create_users_table_command)


def close_connection(conn):
    """
    Commit changes and close the database connection.
    """
    conn.commit()
    conn.close()


def insert_users_data(fname, lname, artist_name, email, password):
    """
    Insert data into the users table.
    """
    conn, cursor = connect_to_database()

    current_datetime = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    password = util.hash_password(password)
    verified = 'N'
    gender = 'U'

    ok = util.is_valid_email(email)
    if ok:
        user = [fname, lname, artist_name, verified, email, password, gender, current_datetime]
        cursor.execute(
            'INSERT INTO users (fname, lname, artist_name, verified, email, password, gender, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)',
            user)
        close_connection(conn)
        return True
    else:
        close_connection(conn)
        return False


def delete_user_by_id(user_id):
    """
    Delete a user from the users table by user_id.
    """
    conn, cursor = connect_to_database()

    cursor.execute('DELETE FROM users WHERE user_id = ?', (user_id,))

    close_connection(conn)


def drop_user_table():
    """
    Drop the users table.
    """
    conn, cursor = connect_to_database()
    cursor.execute('DROP TABLE IF EXISTS users')

    close_connection(conn)


def check_user(artist_name, password):
    """
    Check if a user exists in the users table.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT password FROM users WHERE artist_name = ?', (artist_name,))
    user = cursor.fetchone()

    close_connection(conn)

    password = util.hash_password(password)
    if user and user[0] == password:  # Replace 5 with the index of the password column in your table
        return True
    else:
        return False


def user_exists(artist_name):
    """
    Check if a user exists in the users table.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM users WHERE artist_name = ?', (artist_name,))
    user = cursor.fetchone()

    close_connection(conn)

    return user is not None


def find_user_by_artist_name(artist_name):
    """
    Find a user in the users table by artist_name.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM users WHERE artist_name = ?', (artist_name,))
    user = cursor.fetchone()

    close_connection(conn)

    return user if user else None


def find_user_id_by_artist_name(artist_name):
    """
    Find a user in the users table by artist_name.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT user_id FROM users WHERE artist_name = ?', (artist_name,))
    user = cursor.fetchone()

    close_connection(conn)

    return user[0] if user else None


def find_user_by_email(email):
    """
    Find a user in the users table by email.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM users WHERE email = ?', (email,))
    user = cursor.fetchone()

    close_connection(conn)

    return user if user else None


def find_artist_name_by_id(user_id):
    """
    Find a user in the users table by user_id.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT artist_name FROM users WHERE user_id = ?', (user_id,))
    user = cursor.fetchone()

    close_connection(conn)

    return user[0] if user else None


def find_user_by_id(user_id):
    """
    Find a user in the users table by user_id.
    """
    conn, cursor = connect_to_database()

    cursor.execute('SELECT * FROM users WHERE user_id = ?', (user_id,))
    user = cursor.fetchone()

    close_connection(conn)

    return user if user else None


def update_user_data(fname, lname, email, userID):
    """
    Update a user's data in the users table.
    """
    conn, cursor = connect_to_database()

    cursor.execute('UPDATE users SET fname = ?, lname = ?, email = ? WHERE user_id = ?',
                   (fname, lname, email, userID))

    close_connection(conn)


def update_user_password(artist_name, password):
    """
    Update a user's data in the users table.
    """
    conn, cursor = connect_to_database()

    password = util.hash_password(password)
    cursor.execute('UPDATE users SET password = ? WHERE artist_name = ?', (password, artist_name))

    close_connection(conn)
