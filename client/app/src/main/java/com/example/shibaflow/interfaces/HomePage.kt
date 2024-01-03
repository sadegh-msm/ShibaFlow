package com.example.shibaflow.interfaces

import android.app.DownloadManager
import androidx.compose.ui.platform.LocalContext


import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shibaflow.R
import com.example.shibaflow.api.getAllSongs
import com.example.shibaflow.model.Song
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateListOf
import com.example.shibaflow.api.checkSongLiked
import com.example.shibaflow.api.deleteSongHandler
import com.example.shibaflow.api.likeDislikeSong
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.Playlist


var exoPlayer: ExoPlayer? = null
fun playSong(url: String, context: Context) {
    releasePlayer()
    val mediaItem = MediaItem.fromUri(Uri.parse(url))
    exoPlayer = ExoPlayer.Builder(context).build()
    exoPlayer?.setMediaItem(mediaItem)
    exoPlayer?.prepare()
    exoPlayer?.playWhenReady = true
}

private fun releasePlayer() {
    exoPlayer?.release()
    exoPlayer = null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    state: MutableState<TextFieldValue>
) {
    val transparentBlue = Color(56, 119, 191, 75)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            value = state.value,
            onValueChange = { value ->
                state.value = value
            },
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
                .background(transparentBlue, RoundedCornerShape(50)),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary),
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            },
            trailingIcon = {
                if (state.value.text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            state.value = TextFieldValue("")
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = transparentBlue,
                textColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(50)
        )
    }
}


@Composable
fun SongCard(
    song: Song,
    modifier: Modifier = Modifier,
    playlists: List<Playlist>?,
    navController: NavController,
    enableDelete: Boolean = false
) {
    val s = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(false) }
    var isDeleted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        s.launch {
            isLiked = checkSongLiked(song.id, MyInfo.userInformation.artist_name)
            Log.d("myTag", "$isLiked song ID: ${song.id}")
        }
    }
    var firstTime by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Card(
        modifier = modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .clickable { navController.navigate("song_detail/${song.id}") },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(size = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
//                    Text(
//                        text = song.album,
//                        style = MaterialTheme.typography.titleSmall,
//                        textAlign = TextAlign.Center
//                    )
                }
            }

            if (song.coverImage == "") {
                Image(
                    painter = painterResource(id = R.drawable.default_cover),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                AsyncImage(
                    model = song.coverImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isLiked) R.drawable.heart_filled else R.drawable.heart_unfilled
                    ),
                    contentDescription = "Like",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isLiked = !isLiked
                        }
                )
                val scope = rememberCoroutineScope()

                LaunchedEffect(key1 = isLiked) {
                    scope.launch {
                        firstTime = if (isLiked) {
                            if (firstTime) {
                                likeDislikeSong(song.id, MyInfo.userInformation.artist_name, "like")
                            }
                            true
                        } else {
                            if (firstTime) {
                                likeDislikeSong(song.id, MyInfo.userInformation.artist_name, "dislike")
                            }
                            true
                        }
                    }
                }
                Icon(
                    painterResource(id = R.drawable.comment),
                    contentDescription = "Comment",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("comment_page/${song.id}")
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.download_icon),
                    contentDescription = "Download",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            downloadSong(song.mp3File, song.title, context)
                        }
                )



                if (enableDelete){
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete song"
                        ,modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                isDeleted = true

                            })
                    if (isDeleted){
                        LaunchedEffect(key1 = isDeleted) {
                            scope.launch {
                                val result = deleteSongHandler(MyInfo.userInformation.userID,song.id)
                                if (result == "ok") {
                                    Toast.makeText(context, "Song deleted successfully", Toast.LENGTH_SHORT).show()
                                    navController.navigate("panel_page")
                                } else {
                                    isDeleted = false
                                    Toast.makeText(context, "Can not delete song", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }


                }


                if (enableDelete) {
                    Icon(imageVector = Icons.Default.Delete,
                        contentDescription = "Delete song",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                isDeleted = true

                            })
                    if (isDeleted) {
                        LaunchedEffect(key1 = isDeleted) {
                            scope.launch {
                                val result = deleteSongHandler(MyInfo.userInformation.userID, song.id)
                                if (result == "ok") {
                                    Toast.makeText(
                                        context,
                                        "Song deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("panel_page")
                                } else {
                                    isDeleted = false
                                    Toast.makeText(context, "Can not delete song", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }


                }


        }


        }
        if(playlists!= null){
            CascadingMenu(playlists = playlists)
        }
    }



}


fun downloadSong(url: String, title: String, context: Context) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(title)
        .setDescription("Downloading")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "$title.mp3")

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongList(navController: NavController, modifier: Modifier = Modifier) {
    var songListState = remember { mutableStateListOf<Song>() }
    var isLoad by remember { mutableStateOf(false) }
    var isLoad2 by remember { mutableStateOf(false) }
    var isFiltering by remember { mutableStateOf(false) }
    var songFilteredListState = remember { mutableStateListOf<Song>() }
    val context = LocalContext.current
    var textState = remember { mutableStateOf(TextFieldValue("")) }
    val scope = rememberCoroutineScope()
    val playlistState = remember { mutableStateListOf<Playlist>() }
    if (!isLoad) {
        LaunchedEffect(key1 = songListState,key2 = playlistState) {
            Toast.makeText(context, "Load...", Toast.LENGTH_SHORT).show()
            scope.launch {
                val (songs, ok) = getAllSongs()
                val (ok2, userInfo) = getAllUserInfo(MyInfo.userInformation.artist_name)
                if (userInfo != null) {
                    userInfo.password = MyInfo.userInformation.password
                    MyInfo.userInformation = userInfo
                }
                val (playlists, ok3) = getPlaylists(MyInfo.userInformation.userID)
                playlistState.clear()
                if (playlists != null) {
                    playlistState.addAll(playlists)
                }

                songListState.clear()
                songListState.addAll(songs)
                if (!isFiltering) {
                    songFilteredListState.clear()
                    songFilteredListState.addAll(songListState)
                }

                if (ok == "ok" && ok2 && ok3) {
                    isLoad = true
                    isLoad2 = true
                }
            }
        }
    }

    if (textState.value.text.trim().length > 2 && isLoad2) {
        LaunchedEffect(key1 = textState.value.text) {
            scope.launch {
                songFilteredListState.clear()
                songFilteredListState.addAll(songListState.filter { song ->
                    song.title.contains(textState.value.text, ignoreCase = true)
                })
                if (songFilteredListState.isEmpty()) {
                    Toast.makeText(context, "No results found.", Toast.LENGTH_SHORT).show()
                }
                isFiltering = true
            }
        }
    } else {
        isFiltering = false
        songFilteredListState.clear()
        songFilteredListState.addAll(songListState)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    { it ->
        LazyColumn(modifier = modifier.padding(all = 10.dp), contentPadding = it) {
            item {
                SearchView(modifier, textState)
            }
            if (isLoad2) {
                items(songFilteredListState) { song ->
                    SongCard(
                        song = song,
                        playlists = playlistState,
                        modifier = Modifier.padding(1.dp),
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun SongListApp(navController: NavController) {
    SongList(navController)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shibainu),
                    contentDescription = null,
                    modifier = Modifier
                        .size(height = 50.dp, width = 50.dp)
                        .padding(all = 4.dp)
                )
                Text(
                    text = "ShibaFlow",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Image(
                    painter = painterResource(id = R.drawable.shibainu),
                    contentDescription = null,
                    modifier = Modifier
                        .size(height = 50.dp, width = 50.dp)
                        .padding(all = 4.dp)
                )
            }
        },
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}