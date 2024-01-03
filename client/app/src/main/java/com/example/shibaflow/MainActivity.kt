package com.example.shibaflow

import CommentsPage
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shibaflow.interfaces.ErrorDialog
import com.example.shibaflow.ui.theme.ShibaFlowTheme
import com.example.shibaflow.interfaces.LoginForm
import com.example.shibaflow.interfaces.PanelPage
import com.example.shibaflow.interfaces.PlaylistPage
import com.example.shibaflow.interfaces.PlaylistSongsPage
import com.example.shibaflow.interfaces.SignupForm
import com.example.shibaflow.interfaces.SongDetailScreen
import com.example.shibaflow.interfaces.SongListApp
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
//                    ErrorScreen()
//                    UsersApplication()
                    ShibaApp()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppTopBar(drawerState: DrawerState, coroutineScope: CoroutineScope) {
    SmallTopAppBar(
        title = { Text("Shibaflow", color = MaterialTheme.colorScheme.onPrimary) },
        navigationIcon = {
            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu",tint = MaterialTheme.colorScheme.onPrimary )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
@Composable
fun DrawerListItem(label: String, icon: ImageVector?,iconID:Int?,color:Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(200.dp)
            .background(color = color)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        if (iconID!= null){
            Icon(
                painter = painterResource(id = iconID),
                modifier = Modifier.width(25.dp),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        else if(icon!=null){
            Icon(imageVector = icon,  modifier = Modifier.width(25.dp),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(205,215,223),
            lineHeight = TextUnit.Unspecified
        )


    }
}
@Composable
fun ShibaApp(){
    val navController = rememberNavController()
    AppWithoutDrawer(navController = navController)
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWithDrawer() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                color = Color(7,55,99),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp)
            ){
                Column {
                    IconButton(onClick = { coroutineScope.launch { drawerState.close() } }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Menu")
                    }
                    if (currentRoute == "music_page"){
                        DrawerListItem(label = "Home Page", icon = Icons.Default.Home,null,Color(106,135,161)) {}
                    }
                    else{
                        DrawerListItem(label = "Home Page", icon = Icons.Default.Home,null,Color(56,94,130)) {
                            navController.popBackStack()
                            navController.navigate("music_page")
                            coroutineScope.launch { drawerState.close() }
                        }
                    }
                    if (currentRoute == "panel_page"){
                        DrawerListItem(label = "Profile", icon = Icons.Default.Person,null,Color(106,135,161)) {
                        }
                    } else{
                        DrawerListItem(label = "Profile", icon = Icons.Default.Person,null,Color(56,94,130)) {
                            navController.popBackStack()
                            navController.navigate("panel_page")
                            coroutineScope.launch { drawerState.close() }
                        }
                    }
                    if (currentRoute == "upload_page"){
                        DrawerListItem(label = "Upload Page",null, iconID = R.drawable.upload,Color(106,135,161)) {
                        }
                    } else{
                        DrawerListItem(label = "Upload Page",null, iconID = R.drawable.upload,Color(56,94,130)) {
                            navController.popBackStack()
                            navController.navigate("upload_page")
                            coroutineScope.launch { drawerState.close() }

                        }
                    }
                    if (currentRoute == "playlist_page"){
                        DrawerListItem(label = "Playlist Page",null, iconID = R.drawable.shibainu,Color(106,135,161)) {
                        }
                    } else{
                        DrawerListItem(label = "Playlist Page",null, iconID = R.drawable.shibainu,Color(56,94,130)) {
                            navController.popBackStack()
                            navController.navigate("playlist_page")
                            coroutineScope.launch { drawerState.close() }

                        }
                    }


                    DrawerListItem(label = "Exit", icon = Icons.Default.ExitToApp,null,Color(106,135,161)) {
                        coroutineScope.launch { drawerState.close() }
                    }



                }

            }
        }
) {
        Scaffold(
            topBar = {
                MyAppTopBar(drawerState, coroutineScope)
            }
        ) {
            NavHost(navController = navController, startDestination = "music_page",Modifier.padding(top = 60.dp)) {
//                composable(route = "login_page") {
//                    LoginForm(navController)
//                }
//                composable(route = "signup_page") {
//                    SignupForm(navController)
//                }
                composable(route = "music_page") {
                    SongListApp(navController)
                }
                composable(route = "upload_page") {
                    UploadForm(navController)
                }
                composable(route = "playlist_page") {
                    PlaylistPage(navController)
                }
                composable(route = "playlist_songs_page") {
                    PlaylistSongsPage(navController)
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
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWithoutDrawer(navController: NavHostController) {
//    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login_page") {
        composable(route = "login_page") {
            LoginForm(navController)
        }
        composable(route = "signup_page") {
            SignupForm(navController)
        }
        composable(route = "music_page") {
            AppWithDrawer()
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