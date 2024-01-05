package com.example.shibaflow.model

import com.google.gson.JsonArray

object MyInfo {
    var userInformation: UserInformation = UserInformation()
    var song: Song = Song()
    var uploadSong : UploadSong = UploadSong()
}

data class SongsResponse(
    val message: String,
    val songs_info: List<JsonArray>
)
data class SongsResponseWithoutMessage(
    val musics: List<JsonArray>
)
data class UserSongsResponse(
    val ok: String,
    val songs: List<JsonArray>
)
data class UploadSong(
    var title: String="",
    var album: String = "",
    var mp3File: ByteArray? = null,
    var coverImage: ByteArray? = null,
    var genre: String = ""

) {
    fun uploadIsNotEmpty(): Boolean {
        return title!= "" && mp3File != null && coverImage!= null && genre!= "" && album!=""
    }
}

data class Song(
    var id: Int = 0,
    var title: String = "",
    var artistId: Int = 0,
    var album: String = "",
    var mp3File: String = "",
    var coverImage: String = "",
    var genre: String = "",
    var playCount: Int = 0,
    var skipCount: Int = 0,
    var duration: String = "",
    var lastPlayed: String = ""
)
data class Playlist(
    var id: Int = 0,
    var name: String = "",
    var date: String = "",
    var userID: Int = 0,
    var description :String = "",
    var nField :String =""
)
data class PlaylistResponse(
    val ok: String,
    var playlists: List<JsonArray>
)

data class UserInformation(
    var artist_name: String = "",
    var password: String = "",
    var fname: String = "",
    var lname: String = "",
    var email: String = "",
    var gender: String = "",
    var userID: Int = 0,
) {
    fun isLoginNotEmpty(): Boolean {
        return artist_name.isNotEmpty() && password.isNotEmpty()
    }

    fun isSignupNotEmpty(): Boolean {
        return artist_name.isNotEmpty() && password.isNotEmpty()
    }
}
data class UserResponse(
    val message: String = "",
    val user_info: UserInformation
)


data class CommentsResponse(val comments: List<String>)

