package com.example.shibaflow.interfaces

import ShowLoadPage
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shibaflow.R
import com.example.shibaflow.api.uploadMusicHandler
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.MyInfo.userInformation
import com.example.shibaflow.model.Song
import com.example.shibaflow.model.UploadSong
import com.example.shibaflow.model.UserInformation
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.magnifier
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import java.io.InputStream

fun getByteArrayFromUri(context: Context, uri: Uri?): ByteArray? {
    var byteArray: ByteArray? = null
    try {
        val inputStream: InputStream? = uri?.let { context.contentResolver.openInputStream(it) }
        byteArray = inputStream?.readBytes()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return byteArray
}


@Composable
fun UploadForm(navController: NavController) {

    var isTitleEmpty by remember { mutableStateOf(false) }
    var uploadSong by remember { mutableStateOf(UploadSong()) }
    val audioUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val audioLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
            audioUri.value = result
            uploadSong.mp3File = getByteArrayFromUri(context, audioUri.value)
        }
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val imageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
            imageUri.value = result
            uploadSong.coverImage = getByteArrayFromUri(context, imageUri.value)

        }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimaryContainer),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )  {
            var isUpload by remember { mutableStateOf(false) }
            val context = LocalContext.current
            if(isUpload){
                ShowLoadPage()
            }

            SongTitleField(
                value = uploadSong.title,
                onChange = { data -> uploadSong = uploadSong.copy(title = data) },
                isEmpty = isTitleEmpty,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(0.8f)
            )
            SongAlbumField(
                value = uploadSong.album,
                onChange = { data -> uploadSong = uploadSong.copy(album = data) },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(0.8f)
            )
            SongGenreField(
                value = uploadSong.genre,
                onChange = { data -> uploadSong = uploadSong.copy(genre = data) },
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(0.8f),
            )

            ShibaFlowButton(
                text = audioUri.value?.toString() ?: "Upload music",
                onClick = {},
                onClickEnable = {
                    audioLauncher.launch("audio/*")
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.surfaceTint
            )

            ShibaFlowButton(
                text = imageUri.value?.toString() ?: "Upload cover",
                onClick = {},
                onClickEnable = {
                    imageLauncher.launch("image/*")
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.surfaceTint
            )

            ShibaFlowButton(
                onClick = {
                    isTitleEmpty = uploadSong.title == ""
                },
                onClickEnable = {
                    isUpload = true
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 16.dp),
                enabled = !uploadSong.isUploadEmpty(),
                color = Color(255,124,76),
            ) {
                if (isUpload) {
                    Text("Upload ...")
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(key1 = uploadSong) {
                        scope.launch {
                            if (checkUpload(uploadSong)) {
                                navController.navigate("music_page")
                            } else {
                                isUpload = false
                                Toast.makeText(context, "Wrong info", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Text("Upload")

                }
            }

        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongTitleField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Title",
    placeholder: String = "Enter title of song",
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
            Icons.Default.Person,
            contentDescription = "",
            tint = tint
        )
    }

    val errorIcon = @Composable {
        if (isEmpty) {
            Image(
                painter = painterResource(id = R.drawable.error_icon),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongGenreField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Genre",
    placeholder: String = "Enter genre of song"
) {

    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Person, contentDescription = "", tint = MaterialTheme.colorScheme.primary
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
//        modifier = modifier.background(
//            color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(16.dp)
//        ),
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        shape = RoundedCornerShape(100),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent, // hide the indicator when focused
            unfocusedIndicatorColor = Color.Transparent, // hide the indicator when unfocused
            errorIndicatorColor = Color.Transparent
        )

    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongAlbumField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Album",
    placeholder: String = "Enter album of the song"
) {

    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.List, contentDescription = "", tint = MaterialTheme.colorScheme.primary
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
//        modifier = modifier.background(
//            color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp)
//        ),
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        shape = RoundedCornerShape(100),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent, // hide the indicator when focused
            unfocusedIndicatorColor = Color.Transparent, // hide the indicator when unfocused
            errorIndicatorColor = Color.Transparent
        )

    )
}

suspend fun checkUpload(song: UploadSong): Boolean {
    if (song.mp3File != null) {
        Log.d("title", song.title)
        val (message, ok) = uploadMusicHandler(
            song.title,
            song.genre,
            userInformation.password,
            userInformation.artist_name,
            song.album,
            song.mp3File!!,
            song.coverImage
        )
        return ok == "ok"
    }
    return false

}