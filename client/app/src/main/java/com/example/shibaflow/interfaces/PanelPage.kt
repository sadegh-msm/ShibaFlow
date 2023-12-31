package com.example.shibaflow.interfaces

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shibaflow.R
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
                val (songs, ok) = getUserSongs(username = MyInfo.userInformation.artist_name)
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
                SongCard(
                    song = song, playlists = null, modifier = Modifier.padding(8.dp), navController = navController,
                    enableDelete = true)
            }
        }
    }
}

@Composable
fun UserInformationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(3.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Welcome, ${MyInfo.userInformation.artist_name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }

        UserInfoItem(Icons.Default.Person, "Username", MyInfo.userInformation.artist_name)
        UserInfoItem(icon = painterResource(id = R.drawable.id), "Full Name", MyInfo.userInformation.fname + " " + MyInfo.userInformation.lname)
//        UserInfoItem(icon = painterResource(id = R.drawable.l), "Last Name", MyInfo.userInformation.lname)
        UserInfoItem(Icons.Default.Email, "Email", MyInfo.userInformation.email)
//        UserInfoItem(icon = painterResource(id = R.drawable.id), "UserID", MyInfo.userInformation.userID.toString())
    }
}

@Composable
fun UserInfoItem(icon: Painter, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(25.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: $value", color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun UserInfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: $value", color = MaterialTheme.colorScheme.onPrimary)
    }
}
