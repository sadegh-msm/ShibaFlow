package com.example.shibaflow.interfaces

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shibaflow.api.getAllSongs
import com.example.shibaflow.model.Song
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shibaflow.R

import kotlinx.coroutines.launch

@Composable
fun SongDetailScreen(songId: Int, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var song by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    LaunchedEffect(songId) {
        scope.launch {
            val songs = fetchAllSongs()
            song = findSongById(songs, songId)
            song?.let { songData ->
                exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(songData.mp3File)))
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    progress = (exoPlayer.currentPosition / exoPlayer.duration.toFloat()).coerceIn(0f, 1f)
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.onPrimaryContainer) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            song?.let { songData ->
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    AsyncImage(
                        model = songData.coverImage,
                        contentDescription = "Song Cover",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = songData.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "from ${songData.album}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = progress,
                    onValueChange = { newValue ->
                        exoPlayer.seekTo((exoPlayer.duration * newValue).toLong())
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        val newPosition = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0)
                        exoPlayer.seekTo(newPosition)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.backward10s),
                            contentDescription = "Rewind",
                            tint = Color(47,137,102)
                        )
                    }

                    IconButton(onClick = {
                        isPlaying = !isPlaying
                        exoPlayer.playWhenReady = isPlaying
                    }) {
                        Icon(
                            painter = if (isPlaying) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color(47,137,102),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    IconButton(onClick = {
                        val newPosition = (exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration)
                        exoPlayer.seekTo(newPosition)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.forward10s),
                            contentDescription = "Fast Forward",
                            tint = Color(47,137,102)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth(.6f)
                        .padding(8.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(47,137,102))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Back to Library",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}






suspend fun fetchAllSongs(): List<Song> {
    val (songs, _) = getAllSongs()
    return songs
}

fun findSongById(songs: List<Song>, songId: Int): Song? {
    return songs.find { it.id == songId }
}

