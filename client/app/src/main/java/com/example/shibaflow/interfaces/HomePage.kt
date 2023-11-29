package com.example.shibaflow.interfaces

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shibaflow.R
import com.example.shibaflow.api.LoginHandler
import com.example.shibaflow.api.getAllSongs
import com.example.shibaflow.model.Song
import com.example.shibaflow.model.UserInformation
import kotlinx.coroutines.launch


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
                        Toast
                            .makeText(context, "Start downloading..", Toast.LENGTH_SHORT)
                            .show()
                    }
            ){
                Icon(
                    painter = painterResource(id = R.drawable.download),
                    modifier = Modifier
                        .width(500.dp)
                        .height(500.dp)
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
            Image(
                painter = painterResource(id = R.drawable.shibainu),
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
fun SongList(navController: NavController, modifier: Modifier = Modifier) {
    var songListState by remember { mutableStateOf(listOf<Song>()) }
    var isLoad by remember { mutableStateOf(false) }
    var isLoad2 by remember { mutableStateOf(false) }
    val context = LocalContext.current
    if (!isLoad) {
                val scope = rememberCoroutineScope()
                LaunchedEffect(key1 = songListState) {
                    Toast.makeText(context, "Load...", Toast.LENGTH_SHORT).show()
                    scope.launch {
                        val (songs,ok) = getAllSongs()
                        songListState = songs
                        if (ok == "ok"){
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
                        modifier = Modifier.width(20.dp).height(20.dp),
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
//        isFloatingActionButtonDocked = true
        ) { it -> LazyColumn(modifier = modifier.padding(all = 16.dp), contentPadding = it) {
                if (isLoad2){
                    items(songListState) { song ->
                        SongCard(
                            song = song,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
//    Column {
//        ShibaFlowButton(
//            onClick = {
//                isLoad = true
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 16.dp),
//            enabled = true,
//            color = MaterialTheme.colorScheme.surfaceTint,
//        ){
//            if (isLoad) {
//                Text("Load ...")
//                val scope = rememberCoroutineScope()
//
//                LaunchedEffect(key1 = songListState) {
//                    scope.launch {
//                        val (songs,ok) = getAllSongs()
//                        songListState = songs
////                        songListState = songs
//                        if (ok == "ok"){
//                            isLoad = false
//                            isLoad2 = true
//
//                        }
//
//                    }
//                    }
//                }
//            else{
//                Text(text = "Load")
//            }
//        }
//        if (isLoad2) {
//            LazyColumn(modifier = modifier.padding(all = 16.dp)) {
//                items(songListState) { song ->
//                    SongCard(
//                        song = song,
//                        modifier = Modifier.padding(8.dp)
//                    )
//                }
//            }
//        }
//    }

}




@Composable
fun SongListApp(navController: NavController) {
    SongList(navController)
}


//@Composable
//fun getSampleSongs(): List<Song> {
//    var songs by remember { mutableStateOf(emptyList<Song>()) }
//    var isLoad by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    if (!isLoad) {
//        val scope = rememberCoroutineScope()
//
//        LaunchedEffect(key1 = songs) {
//            scope.launch {
//                songs = getAllSongs()
//                Toast.makeText(context, "Wait to load songs", Toast.LENGTH_SHORT)
//                isLoad = true
//            }
//        }
//    }
//
////    return listOf(
////        Song("Siah Mese Barf", "Sepehr Khalse", "4:15", R.drawable.shibainu),
////        Song("Radioactive", "Imagine Dragons", "3:50", R.drawable.shibainu),
////        Song("Tehran Ta LA", "Koorosh", "5:22", R.drawable.shibainu),
////        // Add more songs as needed
////    )
//}
//
fun checkGetSongs(){

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