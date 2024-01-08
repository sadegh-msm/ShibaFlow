# ShibaFlow

## Introduction
ShibaFlow is a modern music player with an intuitive Android interface. Designed for ease of use and seamless music experience, ShibaFlow provides a unique way to enjoy your favorite tunes.

## Installation and Setup
Given the typical structure of Android and Python server projects, I can provide you with a general template for installation steps. However, for precise instructions, you would need to tailor these steps based on the specific requirements and configurations of the ShibaFlow project. Here's a general guideline:

---

## Installation and Setup

### Prerequisites
Before installing ShibaFlow, ensure you have the following prerequisites installed:
- [Android Studio](https://developer.android.com/studio) for the client application
- [Python](https://www.python.org/downloads/) (version as per the project requirement) for the server

### Cloning the Repository
1. Open a terminal or command prompt.
2. Clone the ShibaFlow repository using Git:
   ```
   git clone git@github.com:sadegh-msm/ShibaFlow.git
   ```
3. Navigate to the cloned directory:
   ```
   cd ShibaFlow
   ```

### Setting Up the Client (Android App)
1. Open Android Studio.
2. Choose 'Open an Existing Project' and navigate to the `client` directory in the cloned repository.
3. Wait for Android Studio to index the project and download any necessary Gradle dependencies.
4. Once the setup is complete, you can run the app on an emulator or physical device through Android Studio.

### Setting Up the Server
1. Navigate to the `server` directory in the cloned repository:
   ```
   cd server
   ```
2. Install the required Python dependencies:
   ```
   pip install -r requirements.txt
   ```
3. Configure any environment variables or settings as required by the server application.

### Running the Server
1. From within the `server` directory, start the server using Python:
   ```
   python main.py
   ```
2. Ensure the server is running and accessible before using the client application.

## Client (Android App)
The client-side application is an Android-based music player.

### Key Features

1. **User-Friendly Interface**: A clean, intuitive interface that makes navigation and music selection effortless.

2. **Music Library Management**: Allows users to view, organize, and manage their music library with options like creating playlists, sorting by artist, album, or genre.

3. **Streaming and Offline Playback**: Supports streaming music from the server and offline playback of downloaded tracks.

4. **Search Functionality**: Enables users to search for songs, artists, or albums within their music library.

5. **Custom Playlists**: Users can create, edit, and save custom playlists for different moods or occasions.

6. **Music Player Controls**: Basic controls such as play, pause, skip, and repeat, along with a shuffle option.

8. **Song Recommendations**: Suggests songs or playlists based on user's listening history or preferences.

9. **Download Manager**: A feature to download songs from the server for offline access.

10. **User Account Integration**: Options for users to log in and synchronize their music preferences across different devices.

12. **Social Sharing**: Allows users to share their favorite music or playlists with friends on social media.


### User Interface

The ShibaFlow app features a user-centric interface designed for a smooth and engaging music listening experience. Here is a brief description of the user interface, accompanied by images from the app:

1. **Music Browsing Screen**
   <img src="./assets/photo_1402-10-18 12.18.26.jpeg" width="300" height="500" />
   - This screen showcases a list of songs with album art previews, making it visually appealing and easy to navigate. Users can interact with tracks directly to play music, add to playlists, or download for offline listening. The search bar at the top allows for quick filtering to find specific tracks or albums.

2. **Playlist Management Screen**
   <img src="./assets/photo_1402-10-18 12.18.28.jpeg" width="300" height="500" />
   - On this screen, users can create and manage their playlists. The interface provides simple controls for adding new playlists and customizing them with descriptions. Existing playlists are displayed with the option to edit or delete, providing a straightforward way to manage the user's music collections.

3. **User Profile Screen**
   <img src="./assets/photo_1402-10-18 12.18.31.jpeg" width="300" height="500" />
   - The profile screen offers personalized information for the user, including username, full name, and email. It also presents a summary of the user's music preferences and recently played tracks, enhancing the personalized experience of the app.

The images illustrate the vibrant and coherent color scheme of the app, reflecting a modern and dynamic aesthetic that resonates with music enthusiasts. The rounded corners and iconography contribute to a friendly and accessible user environment, ensuring an enjoyable navigation experience.

## Server
The server-side application handles music streaming and management.

### External APIs and Services

The ShibaFlow server utilizes a range of APIs and services to provide a robust and functional backend for the music player application. Here are the key external services and their purposes as inferred from the provided Python code:

1. **Flask**: Flask is a micro web framework written in Python, used to handle HTTP requests and serve the music player's backend functionality.

2. **Waitress**: This is a production-quality pure-Python WSGI server that serves as a production server for Flask applications. It is used to host the Flask app.

3. **Amazon S3 via Arvan Cloud**: The server code interacts with an object storage service, presumably Arvan Cloud which seems to be compatible with Amazon S3's API. The `arvan_uploader` and `arvan_downloader` functions are used to upload music files and cover images to the cloud storage and to download them for streaming or display within the app.

4. **Logging**: The Python `logging` library is used for logging information, errors, and debugging output from the server operations.

5. **Python's os and random Libraries**: Used for interacting with the operating system and performing random operations, respectively. This includes fetching environment variables and shuffling the list of songs for a randomized display.

6. **Database Interaction**: While not an external API, the server interacts with a database, as indicated by functions such as `user.user_exists`, `user.insert_users_data`, `music.insert_musics_data`, etc. The specific database used isn't mentioned in the snippet provided, but it is a critical external service for storing user and music data.

7. **Environment Variables**: The application's configuration, such as the location of songs and cover images, is determined by environment variables, which are a secure way to handle configuration settings without hard-coding them into the application.

8. **Flask's send_from_directory**: This function is used to serve files directly from a directory on the server, which is likely where music files and cover images are stored after being downloaded from the cloud storage.

These services are integral to the operation of the ShibaFlow server, facilitating user registration and login, song uploads, streaming, and other user interactions such as liking, commenting, and playlist management.

## Contributing
Guidelines for contributing to the ShibaFlow project. Include coding standards, how to submit pull requests, and any other relevant information for potential contributors.


