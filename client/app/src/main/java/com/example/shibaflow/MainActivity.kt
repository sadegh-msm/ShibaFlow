package com.example.shibaflow

import CommentsPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shibaflow.ui.theme.ShibaFlowTheme
import com.example.shibaflow.interfaces.LoginForm
import com.example.shibaflow.interfaces.PanelPage
import com.example.shibaflow.interfaces.SignupForm
import com.example.shibaflow.interfaces.SongDetailScreen
import com.example.shibaflow.interfaces.SongListApp
import com.example.shibaflow.interfaces.UploadForm

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShibaFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UsersApplication()
                }
            }
        }
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