package com.example.shibaflowproject.api

import com.example.shibaflowproject.model.Song
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
        Pair("Incorrect Information","")

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
@Serializable
data class Music(
    val id: String="",
    val artist: String= "",
    val albumName : String= "",
    val musicName : String= "",
    val coverName : String= "",
    val genre : String= "",
    val like : String= "",
    val report : String= "",
    val duration: String= "",
    val time:String=""
)
suspend fun getAllSongs(){
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("http://195.248.242.169:8080/allsongs")
    val content: String = response.bodyAsText()
    println(content)
    val songs: List<Song> = Json.decodeFromString(content)
    println(songs)
}


