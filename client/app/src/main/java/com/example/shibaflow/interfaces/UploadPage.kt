package com.example.shibaflow.interfaces

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
    var uploadSong by remember { mutableStateOf(UploadSong()) }
    val audioUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val audioLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
            audioUri.value = result
            uploadSong.mp3File = getByteArrayFromUri(context,audioUri.value)
        }
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val imageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
            imageUri.value = result
            uploadSong.coverImage = getByteArrayFromUri(context,imageUri.value)

        }

    Column {
        SongTitleField(
            value = uploadSong.title,
            onChange = { data -> uploadSong = uploadSong.copy(title = data) },
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        )
        SongAlbumField(
            value = uploadSong.album,
            onChange = { data -> uploadSong = uploadSong.copy(album = data) },
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        )
        SongGenreField(
            value = uploadSong.genre,
            onChange = { data -> uploadSong = uploadSong.copy(genre = data) },
            modifier = Modifier
                .fillMaxWidth()
                .padding()
        )

        ShibaFlowButton(
            text = audioUri.value?.toString() ?: "Upload music",
            onClick = {},
            onClickEnable = {
                audioLauncher.launch("audio/*")
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
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
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = MaterialTheme.colorScheme.surfaceTint
        )
        var isUpload by remember { mutableStateOf(false) }
        val context = LocalContext.current
        ShibaFlowButton(
            onClick = {},
            onClickEnable = {
                isUpload = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !uploadSong.isUploadEmpty(),
            color = MaterialTheme.colorScheme.surfaceTint,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongTitleField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Title",
    placeholder: String = "Enter title of song"
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
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp)
        ),
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None
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
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp)
        ),
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None
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
        modifier = modifier.background(
            color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp)
        ),
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None
    )
}

suspend fun checkUpload(song: UploadSong): Boolean {
    if (song.mp3File != null){
        val (message, ok) = uploadMusicHandler(
            song.title,
            song.genre,
            userInformation.password,
            userInformation.username,
            song.album,
            song.mp3File!!,
            song.coverImage
        )
        return ok == "ok"
    }
    return false

}