package com.example.shibaflow.interfaces

import ShowLoadPage
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.shibaflow.R
import com.example.shibaflow.api.addPlaylistHandler
import com.example.shibaflow.api.deletePlaylistHandler
import com.example.shibaflow.api.getPlaylistHandler
import com.example.shibaflow.api.getPlaylistSongsHandler
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.Playlist
import com.example.shibaflow.model.Song
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistTopAppBar(modifier: Modifier = Modifier,navHostController: NavHostController){
    var playlistName by remember { mutableStateOf("") }
    var descriptionState by remember { mutableStateOf("") }
    var isPlaylistNameEmpty by remember { mutableStateOf(false) }
    var isCreatePlaylist by remember { mutableStateOf(false) }
    var newPlaylist by remember { mutableStateOf(Playlist()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        PlaylistField(
            value = playlistName,
            onChange = { data -> playlistName = data },
            isEmpty= isPlaylistNameEmpty,
            modifier = Modifier
                .fillMaxWidth(0.84f)
                .padding(all = 16.dp)
        )
        DescriptionField(
            value = descriptionState,
            onChange = { data -> descriptionState = data },
            modifier = Modifier
                .fillMaxWidth(.8f)
                .padding(all = 8.dp)
        )
        ShibaFlowButton(
            onClick = {
                isPlaylistNameEmpty = playlistName == ""
            },
            onClickEnable = {
                isCreatePlaylist = true
            },
            modifier = Modifier
                .fillMaxWidth(.5f)
                .padding(top = 8.dp),
            enabled = playlistName != "",
            color = Color(255,124,76),
        ){
            if (isCreatePlaylist) {
                Text("Create playlist ...")
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                LaunchedEffect(key1 = newPlaylist) {
                    scope.launch {
                        val ok = addPlaylist(MyInfo.userInformation.userID,playlistName,descriptionState)
                        if (ok) {
                    navHostController.navigate("playlist_page")

                }
                else {
                    isCreatePlaylist = false
                    Toast.makeText(context, "Can not create playlist", Toast.LENGTH_SHORT).show()
                }
            }
        }
            } else {
                Text("Create playlist")

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistPage(navHostController: NavHostController){
    val playlistsState = remember { mutableStateListOf<Playlist>() }
//    var showError by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf("") }
//    if (showError) {
//        ErrorDialog(onDismiss = { showError = false }, text = errorMessage, navController = navHostController)
//    }
    var isLoad by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (!isLoad) {
        LaunchedEffect(key1 = playlistsState) {
            Toast.makeText(context, "Load...", Toast.LENGTH_SHORT).show()
            scope.launch {
                val (playlists, ok) = getPlaylists(MyInfo.userInformation.userID)
                playlistsState.clear()
                playlistsState.addAll(playlists)
                if (ok == "ok") {
                    isLoad = true
                }
//                else if (ok == "Connection error!"){
//                    errorMessage = ok
//                    showError = true
//                }
            }
        }
    }
    Scaffold(
        topBar = {
            PlaylistTopAppBar(navHostController = navHostController, modifier = Modifier
                .padding(50.dp)
                .fillMaxWidth())
        },
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    { it ->
        LazyColumn(modifier = Modifier.padding(all = 10.dp), contentPadding = it) {
            if (isLoad) {
                items(playlistsState) { playlist ->
                    PlaylistCard(playlist = playlist, navHostController = navHostController,modifier = Modifier.padding(1.dp))
                }
            }
        }
    }
}

@Composable
fun PlaylistCard(playlist: Playlist,navHostController: NavHostController,modifier: Modifier = Modifier) {
    var isDeleted by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .padding(all = 8.dp)
            .clickable {
                navHostController.navigate("playlist_songs_page/${playlist.id}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(100)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = playlist.name,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = playlist.description,
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        isDeleted = true
                    }
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete song",
                    modifier = Modifier
                        .size(24.dp) // Size of the Icon
                )
            }
//            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete song"
//                ,modifier = Modifier
//                    .size(24.dp)
//                    .clickable {
//                        isDeleted = true
//                    })
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            LaunchedEffect(isDeleted) {
                scope.launch {
                    if (isDeleted){
                        val result = deletePlaylistHandler(MyInfo.userInformation.userID,playlist.id)
                        if (result == "ok") {
                            Toast.makeText(context, "Song deleted successfully", Toast.LENGTH_SHORT).show()
                            navHostController.navigate("playlist_page")
                        } else {
                            isDeleted = false
                            Toast.makeText(context, "Can not delete song", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

suspend fun getPlaylists(userID: Int):Pair<List<Playlist>, String>{
    val(playlists,ok) = getPlaylistHandler(userID)
    if (ok == "ok"){
        if (playlists != null){
            return Pair(playlists,ok)
        }
    }
    else if(ok == "bad connection") {
        return Pair(emptyList(),"Connection error!")
    }
    return Pair(emptyList(),"")
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlaylistField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Playlist",
    placeholder: String = "Enter playlist name",
    isEmpty: Boolean = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val tint: Color
    if (isEmpty) {
        tint = MaterialTheme.colorScheme.error

    } else {
        tint = MaterialTheme.colorScheme.primary
    }

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Add,
            contentDescription = "",
            tint = tint
        )
    }

    val errorIcon = @Composable {
        if (isEmpty) {
            Image(
                painter = painterResource(id = R.drawable.error_icon,),
                contentDescription = "",
                modifier = Modifier.width(25.dp)
            )
        }
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = errorIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
                keyboardController?.hide()
            }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        isError = isEmpty,
        shape = RoundedCornerShape(100),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent, // hide the indicator when focused
            unfocusedIndicatorColor = Color.Transparent, // hide the indicator when unfocused
            errorIndicatorColor = Color.Transparent
        )
    )
}
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DescriptionField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Description",
    placeholder: String = "Enter playlist description",
    isEmpty: Boolean = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val tint: Color
    if (isEmpty){
        tint = MaterialTheme.colorScheme.error

    }
    else{
        tint = MaterialTheme.colorScheme.primary
    }

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Add,
            contentDescription = "",
            tint = tint
        )
    }

    val errorIcon = @Composable {
        if (isEmpty) {
            Image(painter = painterResource(id = R.drawable.error_icon,), contentDescription ="",modifier = Modifier.width(25.dp))
        }
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = errorIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
                keyboardController?.hide()
            }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        isError = isEmpty,
        shape = RoundedCornerShape(100),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent, // hide the indicator when focused
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
    )
}

suspend fun addPlaylist(userID: Int,playlistName:String,description:String):Boolean{
    val ok = addPlaylistHandler(userID, playlistName = playlistName, description = description)
    return ok == "ok"
}
suspend fun getPlaylistSongs(playlistID:Int):Pair<String,List<Song>>{
    val(songs,ok) = getPlaylistSongsHandler(playlistID = playlistID)
    if (ok == "ok"){
        return Pair(ok,songs)
    }
    else if(ok == "bad connection"){
        return Pair("Connection error!", emptyList())
    }
    return Pair("", emptyList())
}
