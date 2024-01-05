package com.example.shibaflow.interfaces

import ShowLoadPage
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.shibaflow.api.getAllSongs
import com.example.shibaflow.api.getAllUserInfoHandler
import com.example.shibaflow.api.getPlaylistHandler
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.Playlist
import com.example.shibaflow.model.Song
import com.example.shibaflow.model.UserInformation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSongsPage(playlistID:Int,navHostController: NavHostController){
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var songListState = remember { mutableStateListOf<Song>() }
    var isLoad by remember { mutableStateOf(false) }
    var isLoad2 by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    if (showError) {
        ErrorDialog(onDismiss = { showError = false }, text = errorMessage, navController = navHostController)
    }
    if (!isLoad) {
        LaunchedEffect(key1 = songListState) {
            scope.launch {
                val (ok,songs) = getPlaylistSongs(playlistID = playlistID)
                songListState.clear()
                songListState.addAll(songs)
                if (ok == "ok") {
                    isLoad = true
                    isLoad2 = true
                }
                else{
                    errorMessage = ok
                    showError = true
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    { it ->
        LazyColumn(modifier = Modifier.padding(all = 10.dp), contentPadding = it) {
            item {
                if (!isLoad){
                    ShowLoadPage()
                }
                if (isLoad && songListState.isEmpty()){
                    Column(
                        modifier = Modifier.background(color = Color.White)
                            .fillMaxSize()
                            .padding(16.dp), verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = "Your playlist is empty.")
                    }
                }
            }

            if (isLoad2) {
                items(songListState) { song ->
                    SongCard(
                        song = song,
                        playlists = null,
                        modifier = Modifier.padding(1.dp),
                        navController = navHostController,
                        enableDeleteFromPlaylist = true,
                        selectedPlaylistID = playlistID
                    )
                }
            }
        }
    }
}
