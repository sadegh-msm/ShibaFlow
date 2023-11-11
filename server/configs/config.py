import logging
import os

import dotenv

dotenv.load_dotenv('.env')
logging.basicConfig(format='%(asctime)s - %(message)s', datefmt='%d-%b-%y %H:%M:%S')

port = os.getenv("PORT")
host = os.getenv("HOST")
cover_bucket = os.getenv("COVER_BUCKET")
music_bucket = os.getenv("MUSIC_BUCKET")
s3_url = os.getenv("S3_URL")
access_key = os.getenv("ACCESS_KEY")
secret_key = os.getenv("SECRET_KEY")
