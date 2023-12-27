package com.example.shibaflow

import CommentsPage
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shibaflow.ui.theme.ShibaFlowTheme
import com.example.shibaflow.interfaces.LoginForm
import com.example.shibaflow.interfaces.PanelPage
import com.example.shibaflow.interfaces.SignupForm
import com.example.shibaflow.interfaces.SongDetailScreen
import com.example.shibaflow.interfaces.SongListApp
import com.example.shibaflow.interfaces.TopAppBar
import com.example.shibaflow.interfaces.UploadForm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShibaFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    UsersApplication()
                    AppWithDrawer()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar(drawerState: DrawerState, coroutineScope: CoroutineScope) {
    SmallTopAppBar(
        title = { Text("App Title") },
        navigationIcon = {
            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
        // ... other parameters if needed
    )
}
@Composable
fun DrawerListItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            lineHeight = TextUnit.Unspecified // Updated line
        )


    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWithDrawer() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                color = Color.Red,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp)
            ){
                Column {
                    IconButton(onClick = { coroutineScope.launch { drawerState.close() } }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                    DrawerListItem(label = "Home Page", icon = Icons.Default.Home) {
                        navController.navigate("music_page")
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerListItem(label = "Profile", icon = Icons.Default.Person) {
                        navController.navigate("panel_page")
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerListItem(label = "Upload Page", icon = Icons.Default.Send) {
                        navController.navigate("upload_page")
                        coroutineScope.launch { drawerState.close() }
                    }
                    DrawerListItem(label = "Exit", icon = Icons.Default.ExitToApp) {
                        coroutineScope.launch { drawerState.close() }
                    }



                }

            }
        }
) {
        Scaffold(
            topBar = { MyAppTopBar(drawerState, coroutineScope) }
        ) {
            NavHost(navController = navController, startDestination = "login_page",Modifier.padding(top = 60.dp)) {
                composable(route = "login_page") {
                    LoginForm(navController)
                }
                composable(route = "signup_page") {
                    SignupForm(navController)
                }
                composable(route = "music_page") {
                    SongListApp(navController)
                }
                composable(route = "upload_page") {
                    UploadForm(navController)
                }

                composable(route = "comment_page/{songId}") { backStackEntry ->
                    val songId = backStackEntry.arguments?.getString("songId")?.toIntOrNull()
                    songId?.let {
                        CommentsPage(songId = it, navController = navController)
                    } ?: run {
                    }
                }

                composable(route = "panel_page") {
                    PanelPage(navController)
                }
                composable(route = "song_detail/{songId}") { backStackEntry ->
                    val songId = backStackEntry.arguments?.getString("songId")?.toIntOrNull() ?: return@composable
                    SongDetailScreen(songId, navController)
                }


            }
        }
    }
}
@Composable
fun DrawerListItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Add an icon if you like
        // Icon(...)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun UsersApplication() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login_page") {
        composable(route = "login_page") {
            LoginForm(navController)
        }
        composable(route = "signup_page") {
            SignupForm(navController)
        }
        composable(route = "music_page") {
            SongListApp(navController)
        }
        composable(route = "upload_page") {
            UploadForm(navController)
        }

        composable(route = "comment_page/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toIntOrNull()
            songId?.let {
                CommentsPage(songId = it, navController = navController)
            } ?: run {
            }
        }

        composable(route = "panel_page") {
            PanelPage(navController)
        }
        composable(route = "song_detail/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toIntOrNull() ?: return@composable
            SongDetailScreen(songId, navController)
        }


    }
}