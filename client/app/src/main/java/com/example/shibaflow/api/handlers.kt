package com.example.shibaflow.api

import android.util.Log
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
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod


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
    try {
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

        return when (response.status.value) {
            201 -> Pair("User has been created successfully.", "ok")
            409 -> Pair("Duplicate Information", "")
            else -> Pair("Incorrect Information", "")
        }
    } catch (e: ClientRequestException) {
        return Pair("Client request error: ${e.response.status}", "")
    } catch (e: Exception) {
        return Pair("Error occurred: ${e.message}", "")
    }
}

suspend fun LoginHandler(artistName: String, password: String): Pair<String, String> {
    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.submitForm(
            url = "http://195.248.242.169:8080/login",
            formParameters = parameters {
                append("password", password)
                append("artist_name", artistName)
            }
        )
        client.close()

        return when (response.status.value) {
            200 -> Pair("Your username or password is incorrect.", "ok")
            else -> Pair("Your username or password is incorrect.", "")
        }
    } catch (e: ClientRequestException) {
        return Pair("Client request error: ${e.response.status}", "")
    } catch (e: Exception) {
        return Pair("Error occurred: ${e.message}", "")
    }
}


suspend fun getAllSongs(): Pair<List<Song>, String> {
    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get("http://195.248.242.169:8080/allsongs")

        val ok = if (response.status.value == 200) "ok" else ""

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
//                playCount = jsonArray[7].asInt,
//                skipCount = jsonArray[8].asInt,
//                duration = jsonArray[9].asString,
                lastPlayed = jsonArray[7].asString
            )
        }
        return Pair(songs, ok)
    } catch (e: ClientRequestException) {
        return Pair(emptyList(), "Client request error: ${e.response.status}")
    } catch (e: Exception) {
        return Pair(emptyList(), "Error occurred: ${e.message}")
    }
}

suspend fun uploadMusicHandler(
    title: String,
    genre: String,
    password: String,
    artistName: String,
    albumName: String,
    musicURI: ByteArray,
    imageURI: ByteArray?
): Pair<String, String> {
    try {
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
        return when (response.status.value) {
            201 -> Pair("Your information is correct.", "ok")
            else -> Pair("Your information is incorrect.", "")
        }
    } catch (e: ClientRequestException) {
        return Pair("Client request error: ${e.response.status}", "")
    } catch (e: Exception) {
        return Pair("Error occurred: ${e.message}", "")
    }
}

suspend fun likeDislikeSong(
    songID: Int,
    userID: String,
    action: String
): Pair<String, String> {
    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "http://195.248.242.169:8080/interact",
            formData = formData {
                append("songID", songID)
                append("userID", userID)
                append("action", action)
            }
        )
        client.close()
        return when (response.status.value) {
            201 -> Pair("Action is done", "ok")
            else -> Pair("Something went wrong", "")
        }
    } catch (e: ClientRequestException) {
        return Pair("Client request error: ${e.response.status}", "")
    } catch (e: Exception) {
        return Pair("Error occurred: ${e.message}", "")
    }
}

suspend fun checkSongLiked(
    songID: Int,
    userID: String
): Boolean {
    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get("http://195.248.242.169:8080/checklike") {
            setBody(MultiPartFormDataContent(parts = formData {
                append("songID", songID.toString())
                append("userID", userID)
            }))
//            method = HttpMethod.Get
//            parameter("songID", songID.toString())
//            parameter("userID", userID)
//            body
        }
        client.close()
        Log.d("YourTag", response.status.value.toString())
        return response.status.value == 200

    } catch (e: ClientRequestException) {
        // Handle specific client request exception if needed
    } catch (e: Exception) {
        // Handle other exceptions if needed
    }
    return false
}


suspend fun getCommentsForSong(songId: Int): Pair<List<Comment>, String> {
    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get("http://195.248.242.169:8080/comments?songId=$songId")

        val ok = response.headers[HttpHeaders.Server] ?: ""

        val content: String = response.bodyAsText().toString()
        Log.d("get comments", content)

        val gson = Gson()
        val commentsResponse = gson.fromJson(content, CommentsResponse::class.java)

        if (commentsResponse.ok == "comments found") {
            val comments: List<Comment> = commentsResponse.comments.map { pair ->
                Comment(username = pair[0], comment = pair[1])
            }
            return Pair(comments, "ok")
        } else {
            return Pair(emptyList(), "No comments found")
        }
    } catch (e: ClientRequestException) {
        return Pair(emptyList(), "Client request error: ${e.response.status}")
    } catch (e: Exception) {
        return Pair(emptyList(), "Error occurred: ${e.message}")
    }
}

data class Comment(val username: String, val comment: String)

data class CommentsResponse(val comments: List<List<String>>, val ok: String)


suspend fun postCommentToEndpoint(
    userID: String,
    songID: Int,
    comment: String
): Pair<String, String> {
    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "http://195.248.242.169:8080/comment",
            formData = formData {
                append("songID", songID.toString())
                append("userID", userID)
                append("comment", comment)
            }
        )
        client.close()
        Log.d("post comments", response.status.value.toString())
        return when (response.status.value) {
            201 -> Pair("Comment posted successfully.", "ok")
            else -> Pair("Failed to post comment.", "")
        }
    } catch (e: ClientRequestException) {
        return Pair("Client request error: ${e.response.status}", "")
    } catch (e: Exception) {
        return Pair("Error occurred: ${e.message}", "")
    }
}

suspend fun getUserSongs(username: String): Pair<List<Song>, String> {
    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get("http://195.248.242.169:8080/usersongs/$username")

        val ok = if (response.status.value == 200) "ok" else ""

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
    } catch (e: ClientRequestException) {
        return Pair(emptyList(), "Client request error: ${e.response.status}")
    } catch (e: Exception) {
        return Pair(emptyList(), "Error occurred: ${e.message}")
    }
}


