package com.example.shibaflow.interfaces

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shibaflow.R
import androidx.compose.ui.window.Dialog

import androidx.compose.material3.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun ErrorScreen() {
    var showError by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showError) {
            ErrorMessageCard(onTryAgainClicked = { showError = false })
        }
    }
}

@Composable
fun ErrorMessageCard(onTryAgainClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Use a placeholder for the error icon
            Icon(painter = painterResource(id = R.drawable.f), contentDescription ="" )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "An error occurred !",
                color = MaterialTheme.colorScheme.error,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "An error occurred, please check your input.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Button(
                    onClick = { /* You can handle dismiss here if needed */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dismiss")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onTryAgainClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Try Again")
                }
            }
        }
    }
}
@Composable
fun ErrorDialog(onDismiss: () -> Unit,text:String,navController: NavController) {
    var currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    AlertDialog(
        modifier=Modifier.height(200.dp),
        onDismissRequest = onDismiss,
        title = {
            Text("An error occurred")
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    if (currentRoute != null) {
                        if (currentRoute?.contains("playlist_songs_page") == true){
                            currentRoute = "playlist_page"
                        }
                        if (currentRoute?.contains("comment_page") == true){
                            currentRoute = "music_page"
                        }
                        navController.navigate(currentRoute!!)
                    }
                }
            ) {
                Text("Try Again")
            }
        },

    )
}
