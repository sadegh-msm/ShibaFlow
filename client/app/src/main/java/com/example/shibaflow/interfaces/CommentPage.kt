import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shibaflow.api.Comment
import com.example.shibaflow.api.getCommentsForSong
import com.example.shibaflow.api.postCommentToEndpoint
import com.example.shibaflow.interfaces.ErrorDialog
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.ui.theme.ShibaFlowTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CommentsPage(songId: Int, navController: NavController) {
    ShibaFlowTheme {
        var showError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        if (showError) {
            ErrorDialog(onDismiss = { showError = false }, text = errorMessage, navController = navController)
        }
        val (comments, setComments) = remember { mutableStateOf<List<Comment>>(emptyList()) }
        val (loading, setLoading) = remember { mutableStateOf(true) }
        val (newComment, setNewComment) = remember { mutableStateOf(TextFieldValue()) }

        val coroutineScope = rememberCoroutineScope()


        fun postComment() {
            coroutineScope.launch {
                try {
                    // Filter out empty comments
                    if (newComment.text.isNotBlank()) {
                        val (result, ok) = postCommentToEndpoint(
                            userID = MyInfo.userInformation.artist_name,
                            songId,
                            newComment.text
                        )
                        if (ok == "ok") {
                            // Fetch the updated comments, including all comments for the song
                            val (fetchedComments, _) = getCommentsForSong(songId)

                            // Append the new comment to the existing comments
                            val updatedComments = comments.toMutableList().apply {
                                add(
                                    Comment(
                                        username = MyInfo.userInformation.artist_name,
                                        comment = newComment.text
                                    )
                                )
                            }

                            // Set the updated comments state
                            setComments(updatedComments)

                            // Clear the text field after posting the comment
                            setNewComment(TextFieldValue())
                        } else {
                            // Handle the case where posting a comment was not successful
                        }
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                }
            }
        }



        LaunchedEffect(key1 = songId) {
            coroutineScope.launch {
                try {
                    val (fetchedComments, ok) = getCommentsForSong(songId)
                    if (ok == "bad connection"){
                        errorMessage = "Connection error!"
                        showError = true
                    }
                    else{
                        // Append the fetched comments to the existing list
                        val updatedComments = comments.toMutableList().apply {
                            addAll(fetchedComments)
                        }

                        setComments(updatedComments)
                    }



                } catch (e: Exception) {
                    // Handle exceptions, e.g., log an error
                } finally {
                    setLoading(false)
                }
            }
        }


        Scaffold(

            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp)

            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        items(comments) { comment ->
                            CommentItem(comment = comment)
                        }
                    }
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { setNewComment(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        label = { Text("Add a comment", color = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            IconButton(onClick = { postComment() }) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Send,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { postComment() }
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            textColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    )

                }
            }
        }
    }
}


@Composable
fun CommentItem(comment: Comment) {
    if (comment.comment.isNotBlank()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(
                text = "${comment.username}: ${comment.comment}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShibaflowTopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(text = "Comments")
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
    )
}
@Composable
fun ShowLoadPage(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}