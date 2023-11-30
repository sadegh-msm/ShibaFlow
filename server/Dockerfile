FROM python:3.9-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . .

# Install any needed packages specified in requirements.txt
RUN pip install --trusted-host pypi.python.org -r requirements.txt

# Make port 80 available to the world outside this container
EXPOSE 8080

# set environment variables
ENV HOST 0.0.0.0
ENV PORT 8080
ENV S3_URL https://flow-musics.s3.ir-thr-at1.arvanstorage.ir
ENV MUSIC_BUCKET musics
ENV COVER_BUCKET covers
ENV ACCESS_KEY ee82ad29-9bec-40f7-a64c-d854390c51a2
ENV SECRET_KEY 24e63f33c5255a3862f0e7f83d6d37d519e2c55489b855f5dbba7bc5b41a45c4

# Run app.py when the container launches
CMD ["python3", "main.py"]
