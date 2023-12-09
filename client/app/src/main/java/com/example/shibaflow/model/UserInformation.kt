package com.example.shibaflow.model

import com.google.gson.JsonArray

object MyInfo {
    var userInformation: UserInformation = UserInformation()
    var song: Song = Song()
}

data class SongsResponse(
    val message: String,
    val songs_info: List<JsonArray>
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
    fun isUploadEmpty(): Boolean {
        return title== "" || mp3File == null
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

data class UserInformation(
    var username: String = "",
    var password: String = "",
    var firstname: String = "",
    var lasttname: String = "",
    var email: String = "",
    var gender: String = "",
) {
    fun isLoginNotEmpty(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }

    fun isSignupNotEmpty(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }
}
data class CommentsResponse(val comments: List<String>)

