package com.example.shibaflowproject.interfaces

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shibaflow.R
import com.example.shibaflowproject.model.Song


@Composable
fun SongCard(song: Song, modifier: Modifier = Modifier) {
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
                        Toast.makeText(context, "Start downloading..", Toast.LENGTH_SHORT).show()
                    }
            ){
                Icon(
                    painter = painterResource(id = R.drawable.download),
                    modifier = Modifier.width(500.dp).height(500.dp)
                        .padding(15.dp),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.background
                )

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
                    text = song.artist,
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
            Image(
                painter = painterResource(id = song.coverResourceId),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(all = 8.dp)
                    .clip(CircleShape)
                ,
                contentScale = ContentScale.Crop
            )

        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongList(songList: List<Song>,navController: NavController, modifier: Modifier = Modifier) {
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
                        modifier = Modifier.width(20.dp).height(20.dp),
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
//        isFloatingActionButtonDocked = true
        ) { it ->
            LazyColumn(modifier = modifier.padding(all = 16.dp), contentPadding = it) {
                items(songList) { song ->
                    SongCard(
                        song = song,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
}



@Composable
fun SongListApp(navController: NavController) {
    SongList(songList = getSampleSongs(),navController)
}


@Composable
fun getSampleSongs(): List<Song> {
    return listOf(
        Song("Siah Mese Barf", "Sepehr Khalse", "4:15", R.drawable.shibainu),
        Song("Radioactive", "Imagine Dragons", "3:50", R.drawable.shibainu),
        Song("Tehran Ta LA", "Koorosh", "5:22", R.drawable.shibainu),
        // Add more songs as needed
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(modifier: Modifier = Modifier){
    CenterAlignedTopAppBar(
        title = {
            Row (
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer)

    )
}