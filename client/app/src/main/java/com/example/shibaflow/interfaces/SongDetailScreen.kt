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

    // Listener for ExoPlayer
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

    // UI Layout
    Column {
        song?.let {
            AsyncImage(
                model = it.coverImage,
                contentDescription = "Song Cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = progress,
                onValueChange = { newValue ->
                    exoPlayer.seekTo((exoPlayer.duration * newValue).toLong())
                }
            )

            Row {
                Button(onClick = {
                    isPlaying = !isPlaying
                    exoPlayer.playWhenReady = isPlaying
                }) {
                    Text(if (isPlaying) "Pause" else "Play")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { navController.popBackStack() }) {
                    Text("Back")
                }
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
