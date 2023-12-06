package com.example.shibaflow.interfaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shibaflow.api.getUserSongs
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.Song
import kotlinx.coroutines.launch

@Composable
fun PanelPage(navController: NavController) {
    var songListState by remember { mutableStateOf(emptyList<Song>()) }
    var isLoad by remember { mutableStateOf(false) }

    if (!isLoad) {
        val scope = rememberCoroutineScope()
        LaunchedEffect(key1 = songListState) {
            scope.launch {
//                val (songs, ok) = getAllSongs()
                val (songs, ok) = getUserSongs(username = MyInfo.userInformation.username)
                songListState = songs
                if (ok == "ok") {
                    isLoad = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        UserInformationSection()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            items(songListState) { song ->
                SongCard(song = song, modifier = Modifier.padding(8.dp), navController = navController)
            }
        }
    }
}

@Composable
fun UserInformationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Username: ${MyInfo.userInformation.username}")
        Text("First Name: ${MyInfo.userInformation.firstname}")
        Text("Last Name: ${MyInfo.userInformation.lasttname}")
        Text("Email: ${MyInfo.userInformation.email}")
    }
}

