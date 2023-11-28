package com.example.shibaflow

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
import com.example.shibaflowproject.interfaces.LoginForm


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShibaFlowTheme {
                // A surface container using the 'background' color from the theme
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
//class MainActivity : ComponentActivity() {
//    @OptIn(ExperimentalMaterial3Api::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//
//        super.onCreate(savedInstanceState)
//        setContent {
//            Surface {
//                MaterialTheme {
//                    Surface(
//                        modifier = Modifier.fillMaxSize(),
//                        color = MaterialTheme.colorScheme.primaryContainer
//                    ) {
//                        UsersApplication()
//
//                    }
//                }
//
//            }
//        }
//    }
//}
@Composable
fun UsersApplication() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login_page") {
        composable(route = "login_page") {
            LoginForm(navController)
        }
//        composable(route = "signup_page") {
//            SignupForm(navController)
//
//        }
//        composable(route = "music_page") {
//            SongListApp(navController)
//
//        }
//        composable(route = "upload_page") {
//            UploadForm(navController)
//
//        }
    }


}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShibaFlowTheme {
        Greeting("Android")
    }
}