package com.example.shibaflow.interfaces

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shibaflow.api.addSongToPlaylistHandler
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.Playlist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun CascadingMenu(playlists:List<Playlist>,songID:Int,scope :CoroutineScope) {
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CascadingDropdown(
            items = playlists,
            selectedItem = selectedPlaylist,
            onItemSelected = { playlist -> selectedPlaylist = playlist
            },
            songID = songID,
            label = "Add song to playlist",
            onClick = {

            }
            ,
            scope
        )
    }
}

@Composable
fun CascadingDropdown(
    items: List<Playlist>,
    selectedItem: Playlist?,
    onItemSelected: (Playlist) -> Unit,
    songID:Int,
    label: String,
    onClick: () -> Unit,
    scope: CoroutineScope
) {
    var expanded by remember { mutableStateOf(false) }
    var isAdded by remember { mutableStateOf(false) }
    var playlistState by remember { mutableStateOf(Playlist()) }
    Box(
        contentAlignment = Alignment.Center, // Aligns content in the center
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add to playlist ▼",
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(8.dp)
                    .background(Color(20, 119, 200, 75), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.background,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(color = Color(56, 119, 191, 75))
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                            playlistState = item
                            isAdded = true
                        },
                        text = { Text(text = item.name) }
                    )
                }

                val context = LocalContext.current
                if (isAdded){
                    LaunchedEffect(key1 = playlistState) {
                        scope.launch {

                            val result = addSongToPlaylistHandler(playlistID = playlistState.id,songID,MyInfo.userInformation.userID)
                            if (result == "ok") {
                                Toast.makeText(context, "Song added successfully", Toast.LENGTH_SHORT).show()
                                isAdded = false
                            } else {
                                Toast.makeText(context, "Can not add song", Toast.LENGTH_SHORT).show()
                                isAdded = false
                            }
                        }
                    }
                }
            }
        }
    }

//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
//                modifier = Modifier
//                    .background(MaterialTheme.colorScheme.tertiaryContainer)
//                    .heightIn(max = 200.dp) // Set a maximum height for the dropdown
//            ) {
//                Column(
//                    modifier = Modifier
//                        .verticalScroll(rememberScrollState())
//                ) {
//                    items.forEach { item ->
//                        DropdownMenuItem(
//                            onClick = {
//                                onItemSelected(item)
//                                expanded = false
//                            },
//                            text = { Text(text = item.name) }
//                        )
//                    }
//                }
//            }

//
}
