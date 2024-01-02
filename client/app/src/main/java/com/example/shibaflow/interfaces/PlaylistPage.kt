package com.example.shibaflow.interfaces

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.shibaflow.R
import com.example.shibaflow.api.addPlaylistHandler
import com.example.shibaflow.api.getAllSongs
import com.example.shibaflow.api.getAllUserInfoHandler
import com.example.shibaflow.api.getPlaylistHandler
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.Playlist
import com.example.shibaflow.model.Song
import com.example.shibaflow.model.UserInformation
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistTopAppBar(modifier: Modifier = Modifier,navHostController: NavHostController){
    var playlistName by remember { mutableStateOf("") }
    var descriptionState by remember { mutableStateOf("") }
    var isPlaylistNameEmpty by remember { mutableStateOf(false) }
    var isCreatePlaylist by remember { mutableStateOf(false) }
    var newPlaylist by remember { mutableStateOf(Playlist()) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(10.dp),
    ) {
        PlaylistField(
            value = playlistName,
            onChange = { data -> playlistName = data },
            isEmpty= isPlaylistNameEmpty,
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        )
        DescriptionField(
            value = descriptionState,
            onChange = { data -> descriptionState = data },
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        )
        ShibaFlowButton(

            onClick = {
                isPlaylistNameEmpty = playlistName == ""
            },
            onClickEnable = {
                isCreatePlaylist = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = playlistName != "",
            color = MaterialTheme.colorScheme.surfaceTint,
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
//    CenterAlignedTopAppBar(
//        title = {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(8.dp),
//            ) {
//                PlaylistField(
//                    value = playlistName,
//                    onChange = { data -> playlistName = data },
//                    isEmpty= isPlaylistNameEmpty,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding()
//                )
//                ShibaFlowButton(
//
//                    onClick = {
//                        isPlaylistNameEmpty = playlistName == ""
//                    },
//                    onClickEnable = {
//                        isCreatePlaylist = true
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp),
//                    enabled = playlistName != "",
//                    color = MaterialTheme.colorScheme.surfaceTint,
//                ){
//                    if (isCreatePlaylist) {
//                        Text("Create playlist ...")
//                        val scope = rememberCoroutineScope()
//                        val context = LocalContext.current
//
//                        LaunchedEffect(key1 = newPlaylist) {
//                            scope.launch {
//                                val ok = addPlaylist(MyInfo.userInformation.userID,playlistName,"")
//                                if (ok) {
//                                    navHostController.navigate("playlist_page")
//
//                                }
//                                else {
//                                    isCreatePlaylist = false
//                                    Toast.makeText(context, "Can not create playlist", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        }
//                    } else {
//                        Text("Create playlist")
//
//                    }
//                }
//            }
//        },
//        modifier = modifier,
//        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
//    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistPage(navHostController: NavHostController){
    val playlistsState = remember { mutableStateListOf<Playlist>() }
    var isLoad by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if (!isLoad) {
        LaunchedEffect(key1 = playlistsState) {
            Toast.makeText(context, "Load...", Toast.LENGTH_SHORT).show()
            scope.launch {
                val (playlists, ok) = getPlaylists(MyInfo.userInformation.userID)
                playlistsState.clear()
                if (playlists != null) {
                    playlistsState.addAll(playlists)
                }
                if (ok) {
                    isLoad = true
                }
            }
        }
    }
    Scaffold(
        topBar = {
            PlaylistTopAppBar(navHostController = navHostController, modifier = Modifier.padding(50.dp).fillMaxWidth())
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
    Card(
        modifier = modifier
            .padding(all = 8.dp)
            .clickable {
//                navController.navigate("song_detail/${song.id}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(size = 16.dp)

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
                text = playlist.date,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = playlist.description,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )

        }
    }
}

suspend fun getPlaylists(userID: Int):Pair<List<Playlist>?, Boolean>{
    val(playlists,ok) = getPlaylistHandler(userID)
    return Pair(playlists,ok=="ok")
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
    )
}

suspend fun addPlaylist(userID: Int,playlistName:String,description:String):Boolean{
    val ok = addPlaylistHandler(userID, playlistName = playlistName, description = description)
    return ok == "ok"
}
