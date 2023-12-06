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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.RectangleShape
import com.example.shibaflow.api.checkSongLiked
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
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
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
fun SongCard(song: Song, modifier: Modifier = Modifier) {
    val s = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        s.launch {
            isLiked = checkSongLiked(song.id, MyInfo.userInformation.username)
            Log.d("myTag", "$isLiked song ID: ${song.id}");
        }
    }
    var firstTime by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(all = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(size = 50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val context = LocalContext.current

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        isLiked = !isLiked
                    }
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isLiked) R.drawable.heart_filled else R.drawable.heart_unfilled
                    ),
                    modifier = Modifier
                        .width(1000.dp)
                        .height(1000.dp)
                        .padding(10.dp),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.background
                )

                val scope = rememberCoroutineScope()

                LaunchedEffect(key1 = isLiked) {
                    scope.launch {
                        if (isLiked) {
                            if (firstTime) {
                                Toast
                                    .makeText(context, "Liked!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            firstTime = true
                        } else {
                            if (firstTime) {
                                Toast
                                    .makeText(context, "Unliked!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            firstTime = true
                        }
                    }
                }
            }


            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.title,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = song.album,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = song.duration,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }

            AsyncImage(
                model = song.coverImage,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(all = 8.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongList(navController: NavController, modifier: Modifier = Modifier) {
    var songListState by remember { mutableStateOf(listOf<Song>()) }
    var isLoad by remember { mutableStateOf(false) }
    var isLoad2 by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var textState = remember { mutableStateOf(TextFieldValue("")) }

    if (!isLoad) {
        val scope = rememberCoroutineScope()
        LaunchedEffect(key1 = songListState) {
            Toast.makeText(context, "Load...", Toast.LENGTH_SHORT).show()
            scope.launch {
                val (songs, ok) = getAllSongs()
                songListState = songs
                if (ok == "ok") {
                    isLoad = true
                    isLoad2 = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar()
        },
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("upload_page")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.upload),
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp),
                    contentDescription = "",
                    tint = Color.Black
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
//        isFloatingActionButtonDocked = true
    )
    { it ->
        LazyColumn(modifier = modifier.padding(all = 10.dp), contentPadding = it) {
            if (isLoad2) {
                items(songListState) { song ->
                    SongCard(
                        song = song,
                        modifier = Modifier.padding(1.dp)
                    )
                }
            }
        }
        Column {
            SearchView(modifier, textState)
            ItemList(state = textState)
        }
    }
}

@Composable
fun ItemList(state: MutableState<TextFieldValue>) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val showToast = remember { mutableStateOf(false) }

    if (state.value.text != "") {
        LaunchedEffect(key1 = state.value.text) {
            scope.launch {
                val (songs, ok) = getAllSongs()
                if (ok == "ok") {
                    val filteredSongs = songs.filter { song ->
                        song.title.contains(state.value.text, ignoreCase = true)
                    }
                    if (filteredSongs.isNotEmpty()) {
//                        Column {
//                            filteredSongs.forEach { song ->
//                                SongCard(
//                                    song = song,
//                                    modifier = Modifier.padding(1.dp)
//                                )
//                            }
//                        }
                    } else {
                        showToast.value = true
                    }
                }
            }
        }
    }

    if (showToast.value) {
        Toast.makeText(context, "No results found.", Toast.LENGTH_SHORT).show()
        showToast.value = false
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