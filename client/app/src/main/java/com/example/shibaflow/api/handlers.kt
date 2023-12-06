package com.example.shibaflow.api

import com.example.shibaflow.model.Song
import com.example.shibaflow.model.SongsResponse
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import com.google.gson.Gson
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders


suspend fun main() {

}

suspend fun SignupHandler(
    firstname: String,
    lastname: String,
    email: String,
    password: String,
    gender: String,
    artistName: String
): Pair<String, String> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.submitForm(
        url = "http://195.248.242.169:8080/register",
        formParameters = parameters {
            append("fname", firstname)
            append("lname", lastname)
            append("email", email)
            append("password", password)
            append("gender", gender)
            append("artist_name", artistName)
        }
    )
    client.close()
    println(response)
    return if (response.status.value == 201) {
        Pair("User has been created successfully.", "ok")
    } else if (response.status.value == 409) {
        Pair("Duplicate Information", "")
    } else {
        Pair("Incorrect Information", "")
    }
}

suspend fun LoginHandler(artistName: String, password: String): Pair<String, String> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.submitForm(
        url = "http://195.248.242.169:8080/login",
        formParameters = parameters {
            append("password", password)
            append("artist_name", artistName)
        }
    )
    client.close()
    println(response)
    return if (response.status.value == 200) {
        Pair("Your username or password is incorrect.", "ok")
    } else {
        Pair("Your username or password is incorrect.", "")
    }
}


suspend fun getAllSongs(): Pair<List<Song>, String> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("http://195.248.242.169:8080/allsongs")
    var ok = ""
    if (response.status.value == 200) {
        ok = "ok"
    }
    val content: String = response.bodyAsText().toString()
    val gson = Gson()
    val jsonForm = gson.fromJson(content, SongsResponse::class.java)
    val songs: List<Song> = jsonForm.songs_info.map { jsonArray ->
        Song(
            id = jsonArray[0].asInt,
            title = jsonArray[1].asString,
            artistId = jsonArray[2].asInt,
            album = jsonArray[3].asString,
            mp3File = jsonArray[4].asString,
            coverImage = jsonArray[5].asString,
            genre = jsonArray[6].asString,
            playCount = jsonArray[7].asInt,
            skipCount = jsonArray[8].asInt,
            duration = jsonArray[9].asString,
            lastPlayed = jsonArray[10].asString
        )
    }
    return Pair(songs, ok)
}
suspend fun uploadMusicHandler (
    title: String,
    genre: String,
    password: String,
    artistName: String,
    albumName: String,
    musicURI:ByteArray,
    imageURI:ByteArray?


): Pair<String,String> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.submitFormWithBinaryData(
        url = "http://195.248.242.169:8080/song",
        formData = formData {
            append("artist_name", artistName)
            append("password", password)
            append("title", title)
            append("genre", genre)
            append("duration", "")
            append("album_name", albumName)
            append("music", musicURI, Headers.build {
                append(HttpHeaders.ContentType, "audio/mpeg")
                append(HttpHeaders.ContentDisposition, "filename=\"music.mp3\"")
            })
            if (imageURI != null) {
                append("cover", imageURI, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpg")
                    append(HttpHeaders.ContentDisposition, "filename=\"cover.jpg\"")
                })
            }

        }
    )
    client.close()
    return if (response.status.value == 201) {
        Pair("Your information is correct.", "ok")
    } else {
        Pair("Your information is incorrect.", "")
    }
}

suspend fun likeDislikeSong (
    songID: String,
    action: String,

): Pair<String,String> {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.submitFormWithBinaryData(
        url = "http://195.248.242.169:8080/    endpoint",
        formData = formData {
            append("songID", songID)
            append("action", action)
        }
    )
    client.close()
    return if (response.status.value == 201) {
        Pair("action is done", "ok")
    } else {
        Pair("something went wrong", "")
    }
}

suspend fun checkSongLiked (
    songID: Int,
    userID: String,

    ): Boolean {
//    val client = HttpClient(CIO)
//    val response: HttpResponse = client.submitFormWithBinaryData(
//        url = "http://195.248.242.169:8080/    endpoint",
//        formData = formData {
//            append("songID", songID)
//            append("userID", userID)
//        }
//    )
//    client.close()
//    return if (response.status.value == 201) {
//        "liked or not"
//    } else {
//        ""
//    }
    return songID % 2 != 0
}


