package com.example.shibaflow.interfaces

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun UploadForm(navController: NavController){
    var musicname :String
    val audioUri = remember { mutableStateOf<Uri?>(null) }

    val audioLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
        audioUri.value = result
    }
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val imageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result: Uri? ->
        imageUri.value = result
    }

    Column {
        ShibaFlowButton(
            text = audioUri.value?.toString() ?: "Upload music",
            onClick = {
                audioLauncher.launch("audio/*")
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = MaterialTheme.colorScheme.surfaceTint
        )

        ShibaFlowButton(
            text = imageUri.value?.toString() ?:"Upload cover",
            onClick = {
                      imageLauncher.launch("image/*")
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = MaterialTheme.colorScheme.surfaceTint
        )
//        MusicField(value = musicname, onChange = { } ,modifier = Modifier.fillMaxWidth().padding())
        var isUpload by remember { mutableStateOf(false) }
        val context = LocalContext.current
        ShibaFlowButton(
            onClick = { isUpload = true
                Toast.makeText(context, "Upload Completed", Toast.LENGTH_SHORT).show()
                navController.navigate("music_page")
                      },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = true,
            color = MaterialTheme.colorScheme.surfaceTint,
        ){
            Text("Upload")
        }
    }



}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Music name",
    placeholder: String = "Enter music name"
) {
    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            painter = painterResource(id = R.drawable.gender),
            modifier = Modifier.width(25.dp),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None
    )
}