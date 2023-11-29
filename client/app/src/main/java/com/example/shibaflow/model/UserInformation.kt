package com.example.shibaflow.model

object MyInfo {
    var userInformation: UserInformation = UserInformation()
    var song: Song = Song()
}
//data class Song(
//    val title: String="",
//    val artist: String= "",
//    val duration: String= "",
//    val coverResourceId: Int= 0
//)
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
    var firstname:String = "",
    var lasttname:String = "",
    var email:String = "",
    var gender:String = "",
) {
    fun isLoginNotEmpty(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }
    fun isSignupNotEmpty(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }
}
