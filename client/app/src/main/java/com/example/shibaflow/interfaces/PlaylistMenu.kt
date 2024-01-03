package com.example.shibaflow.interfaces


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.shibaflow.model.Playlist


@Composable
fun CascadingMenu(playlists:List<Playlist>) {
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    Column {
        CascadingDropdown(
            items = playlists,
            selectedItem = selectedPlaylist,
            onItemSelected = { playlist -> selectedPlaylist = playlist
            },
            label = "Select State",
            onClick = {

            }
        )
        val context = LocalContext.current

    }
}

@Composable
fun CascadingDropdown(
    items: List<Playlist>,
    selectedItem: Playlist?,
    onItemSelected: (Playlist) -> Unit,
    label: String,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(start = 30.dp, end = 30.dp)) {
        Text(text = label)
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, MaterialTheme.shapes.small),
            contentAlignment = Alignment.CenterStart
        ) {
            onClick()
            Text(
                text = selectedItem?.name ?: "Select an item",
                modifier = Modifier
                    .clickable { expanded = true }
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.background
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .heightIn(max = 200.dp) // Set a maximum height for the dropdown
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                onItemSelected(item)
                                expanded = false
                            },
                            text = { Text(text = item.name) }
                        )
                    }
                }
            }


            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {

                items.forEach { item ->
                    DropdownMenuItem(

                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        text = {Text(text = item.name)}
                    )
                }
            }
        }
    }
}


