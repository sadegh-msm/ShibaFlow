package com.example.shibaflow.interfaces

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.RectangleShape
import com.example.shibaflow.api.checkSongLiked
import com.example.shibaflow.api.likeDislikeSong
import com.example.shibaflow.model.MyInfo


private var exoPlayer: ExoPlayer? = null
fun playSong(url: String, context: Context) {
    if (exoPlayer?.playWhenReady == true) {
        exoPlayer!!.release()
        exoPlayer = ExoPlayer.Builder(context).build()
    } else {
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        exoPlayer = ExoPlayer.Builder(context).build()
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    state: MutableState<TextFieldValue>
) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        state.value =
                            TextFieldValue("")
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape
    )
}

@Composable
fun SongCard(song: Song, modifier: Modifier = Modifier, navController: NavController) {
    val s = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        s.launch {
            isLiked = checkSongLiked(song.id, MyInfo.userInformation.username)
            Log.d("myTag", "$isLiked song ID: ${song.id}")
        }
    }
    var firstTime by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(all = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(size = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = song.title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = song.album,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = song.duration,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }

            AsyncImage(
                model = song.coverImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(size = 16.dp)),
                contentScale = ContentScale.Crop
            )

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
                                likeDislikeSong( song.id ,"dislike")
                            }
                            true
                        } else {
                            if (firstTime) {
                                likeDislikeSong( song.id ,"like")
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
            }
        }
    }
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

    if (!isLoad) {
        LaunchedEffect(key1 = songListState) {
            Toast.makeText(context, "Load...", Toast.LENGTH_SHORT).show()
            scope.launch {
                val (songs, ok) = getAllSongs()
                songListState.clear()
                songListState.addAll(songs)
                if (!isFiltering) {
                    songFilteredListState.clear()
                    songFilteredListState.addAll(songListState)
                }

                if (ok == "ok") {
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
        topBar = {
            TopAppBar()
        },
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("upload_page")
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.upload),
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        contentDescription = "",
                        tint = Color.Black
                    )
                }

                FloatingActionButton(
                    onClick = {
                        navController.navigate("panel_page")
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.user),
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
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