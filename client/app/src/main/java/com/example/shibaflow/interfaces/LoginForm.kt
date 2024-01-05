package com.example.shibaflow.interfaces

import android.os.Message
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import android.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign

import androidx.navigation.NavHostController

import com.example.shibaflow.R
import com.example.shibaflow.api.LoginHandler
import com.example.shibaflow.api.getAllUserInfoHandler
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.UserInformation
import kotlinx.coroutines.launch

@Composable
fun LoginForm(navHostController: NavHostController) {
    Surface(
        color = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        var info by remember { mutableStateOf(UserInformation()) }
        var showError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        var isUsernameEmpty by remember { mutableStateOf(false) }
        MyInfo.userInformation = info

        var isLogin by remember { mutableStateOf(false) }
        val context = LocalContext.current
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            if (showError) {
                ErrorDialog(onDismiss = { showError = false }, text = errorMessage, navController = navHostController)
            }
            Image(
                painter = painterResource(id = R.drawable.shibainu),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .fillMaxHeight()
            )

            UsernameField(
                value = info.artist_name,
                onChange = { data -> info = info.copy(artist_name = data) },
                isEmpty= isUsernameEmpty,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
            )
            PasswordField(
                value = info.password,
                onChange = { data -> info = info.copy(password = data) },
                submit = {
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )
            val context = LocalContext.current


            ShibaFlowButton(
                onClick = {
                    isUsernameEmpty = info.artist_name == ""
                },
                onClickEnable = {
                    isLogin = true
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = info.isSignupNotEmpty(),
                color = MaterialTheme.colorScheme.surfaceTint,
            ){
                if (isLogin) {
                    Text("Login ...")
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(key1 = info) {
                        scope.launch {
                            val result = checkLogin(info)
                            if (
                                result.first) {
                                navHostController.popBackStack()
                                navHostController.navigate("music_page")
                            } else {
                                isLogin = false
                                errorMessage = result.second
                                showError = true
//                                val toast = Toast.makeText(context, "Wrong username or password", Toast.LENGTH_SHORT)
//                                toast.show()
                            }
                        }
                    }
                } else {
                    Text("Login")

                }
            }

            ShibaFlowButton(
                text = "Sign up",
                onClick = {},
                onClickEnable = {
                    navHostController.popBackStack()
                    navHostController.navigate("signup_page")
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                color = MaterialTheme.colorScheme.surfaceTint

            )
        }
    }
}

@Composable
fun ShibaFlowButton(
    onClickEnable: () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    text: String? = null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit = {
        text?.let { Text(it) }
    }
) {
    Box(
        contentAlignment = contentAlignment,
        modifier = modifier
            .fillMaxWidth()
            .background(color = color, shape = RoundedCornerShape(50.dp))
            .clickable(onClick = {
                onClick()
                if (enabled) {
                    onClickEnable()
                }
            })
            .padding(12.dp)
    ) {
        val textStyle = LocalTextStyle.current
        CompositionLocalProvider(
            LocalTextStyle provides textStyle.merge(
                TextStyle(
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            )
        ) {
            content()
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UsernameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Username",
    placeholder: String = "Enter your username",
    isEmpty: Boolean = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val tint:Color
    if (isEmpty){
        tint = MaterialTheme.colorScheme.error

    }
    else{
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
        shape = RoundedCornerShape(100),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent, // hide the indicator when focused
            unfocusedIndicatorColor = Color.Transparent // hide the indicator when unfocused
        )

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your Password"
) {

    var isPasswordVisible by remember { mutableStateOf(true) }

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Lock,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            if (isPasswordVisible) Icon(
                painter = painterResource(id = R.drawable.hide),
                modifier = Modifier.width(25.dp),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            ) else Icon(
                painter = painterResource(id = R.drawable.visible),
                modifier = Modifier.width(25.dp),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier
            .padding(top = 16.dp),
//            .background(
//                color = MaterialTheme.colorScheme.secondary,
//                shape = RoundedCornerShape(16.dp)
//            ),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(100),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent, // hide the indicator when focused
            unfocusedIndicatorColor = Color.Transparent // hide the indicator when unfocused
        )
    )
}
suspend fun checkLogin(userInfo: UserInformation): Pair<Boolean,String> {
    val (message,ok) = LoginHandler(
        userInfo.artist_name,
        userInfo.password,
    )
    if (ok == "ok"){
        return Pair(true,message)
    }
    else if (ok == "bad connection"){
        return Pair(false,"Connection error!")
    }
    else{
        return Pair(false,message)
    }
}
suspend fun getAllUserInfo(username:String):Pair<String,UserInformation?>{
    val (userInfo,ok) =  getAllUserInfoHandler(username)
    if (ok == "ok"){
        return Pair("ok",userInfo)
    }
    else if( ok == "bad connection"){
        return Pair("Connection error!",userInfo)
    }
    else{
        return Pair("",userInfo)
    }

}
