package com.example.shibaflow.interfaces

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.shibaflow.R
import com.example.shibaflow.api.SignupHandler
import com.example.shibaflow.model.MyInfo
import com.example.shibaflow.model.UserInformation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SignupForm(navController: NavHostController) {
    Surface(
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
//        var information = MyInfo.userInformation
        var information by remember { mutableStateOf(UserInformation()) }
        MyInfo.userInformation = information
        var isSigningUp by remember { mutableStateOf(false) }
        var showError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }
        var isEmailValid by remember { mutableStateOf(true) }
        var isLastnameEmpty by remember { mutableStateOf(false) }
        var isFirstnameEmpty by remember { mutableStateOf(false) }
        var isUsernameEmpty by remember { mutableStateOf(false) }
        var isPasswordEmpty by remember { mutableStateOf(false) }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            if (showError) {
                ErrorDialog(onDismiss = { showError = false }, text = errorMessage)
            }
            Image(
                painter = painterResource(id = R.drawable.shibainu),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .width(150.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .fillMaxHeight(),
            )

            UsernameField(
                value = information.username,
                onChange = { data -> information = information.copy(username = data) },
                isEmpty= isUsernameEmpty,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
            )
            FirstnameField(
                value = information.firstname,
                onChange = { data -> information = information.copy(firstname = data) },
                isEmpty= isFirstnameEmpty,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            LastnameField(
                value = information.lasttname,
                onChange = { data -> information = information.copy(lasttname = data) },
                isEmpty= isLastnameEmpty,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            EmailField( information.email,{ data -> information = information.copy(email = data) }
                , isEmailValid = isEmailValid, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp) )
            PasswordField(
                value = information.password,
                onChange = { data -> information = information.copy(password = data) },
                submit = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )

            ShibaFlowButton(
                onClick = {
                    isEmailValid = isValidEmail(information.email)
                    isLastnameEmpty = information.lasttname == ""
                    isFirstnameEmpty = information.firstname == ""
                    isUsernameEmpty = information.username == ""
                    isPasswordEmpty = information.password == ""
                },
                onClickEnable = {

                    isSigningUp = true},
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = information.isSignupNotEmpty() && isValidEmail(information.email),
                color = MaterialTheme.colorScheme.surfaceTint,
            ){
                if (isSigningUp) {
                    Text("Signing Up...")
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current
                    LaunchedEffect(key1 = information) {
                        scope.launch {
                            val result = checkSignup(information)
                            if (result.first) {
                                navController.popBackStack()
                                navController.navigate("music_page")
                            } else {
                                isSigningUp = false
                                errorMessage = result.second
                                showError = true
                            }
                        }
                    }
                } else {
                    Text("Sign up")
//                    Toast.makeText(context, "Wrong UserInformation", Toast.LENGTH_SHORT).show()
                }
            }

            ShibaFlowButton(
                text = "Login",
                onClick = {},
                onClickEnable = {
                    navController.popBackStack()
                    navController.navigate("login_page") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = true,
                color = MaterialTheme.colorScheme.surfaceTint,
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FirstnameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Firstname",
    placeholder: String = "Enter your firstname",
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
            painter = painterResource(id = R.drawable.f),
            modifier = Modifier.width(25.dp),
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
    )
}
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LastnameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Lastname",
    placeholder: String = "Enter your lastname",
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
            painter = painterResource(id = R.drawable.l),
            modifier = Modifier.width(25.dp),
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
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmailField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Email",
    placeholder: String = "Enter your Email",
    isEmailValid: Boolean = true
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val leadingIcon = @Composable {
        Icon(
            painter = painterResource(id = R.drawable.email),
            modifier = Modifier.size(25.dp),
            contentDescription = "Email Icon",
            tint = if (isEmailValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }

    val errorIcon = @Composable {
        if (!isEmailValid) {
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
        isError = !isEmailValid,
//        colors = TextFieldDefaults.textFieldColors(
//            containerColor = if (!isEmailValid) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.background
//        )
    )
}



suspend fun checkSignup(userInfo: UserInformation): Pair<Boolean,String> {
    val (message, ok) = SignupHandler(
        userInfo.firstname,
        userInfo.lasttname,
        userInfo.email,
        userInfo.password,
        userInfo.gender,
        userInfo.username
    )
    return Pair(ok == "ok",message)
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return email.matches(emailRegex.toRegex())
}