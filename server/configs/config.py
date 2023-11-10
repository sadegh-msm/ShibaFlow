import logging
import os

import dotenv

dotenv.load_dotenv('.env')
logging.basicConfig(format='%(asctime)s - %(message)s', datefmt='%d-%b-%y %H:%M:%S')

port = os.getenv("PORT")
host = os.getenv("HOST")
