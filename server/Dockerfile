FROM python:3.9-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Install any needed packages specified in requirements.txt
RUN pip install --trusted-host pypi.python.org -r requirements.txt

EXPOSE 8080

# set environment variables
ENV HOST 0.0.0.0
ENV PORT 8080
ENV S3_URL https://flow-musics.s3.ir-thr-at1.arvanstorage.ir
ENV MUSIC_BUCKET musics
ENV COVER_BUCKET covers
ENV ACCESS_KEY YOUR_ACCESS_KEY
ENV SECRET_KEY YOUR_SECRET_KEY

CMD ["python3", "main.py"]
