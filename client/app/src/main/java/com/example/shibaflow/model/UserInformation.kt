package com.example.shibaflowproject.model

object MyInfo {
    var userInformation: UserInformation = UserInformation()
    var song: Song = Song()
}
data class Song(
    val title: String="",
    val artist: String= "",
    val duration: String= "",
    val coverResourceId: Int= 0
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
