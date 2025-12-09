package com.cs407.safebite.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cs407.safebite.R
import com.cs407.safebite.auth.*
import com.cs407.safebite.data.UserState
//import com.cs407.safebite.data.UserState
import com.google.firebase.auth.FirebaseUser

//Create composables for ErrorText, userEmail, userPassword, and LogInSignUpButton
//Handle onclick function for LogInSignUpButton
@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (error != null)
        Text(
            text = error,
            color = Color.Red,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(8.dp))
}

// Handle email input
@Composable
fun UserEmail(email: String,
              onEmailChange: (String) -> Unit,
              modifier: Modifier = Modifier) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = {Text(stringResource(id = R.string.email_hint))},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun UserPassword(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier){
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {Text(stringResource(id = R.string.password_hint))},
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun LogInSignUpButton(
    email: String,
    password: String,
    onResult: (String?) -> Unit,
    onSuccess: (FirebaseUser) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    //add other parameters if you need
    modifier: Modifier = Modifier
) {
    val emptyEmail = stringResource(id = R.string.empty_email)
    val invalidEmail = stringResource(id = R.string.invalid_email)
    val emptyPassword = stringResource(id = R.string.empty_password)
    val shortPassword = stringResource(id = R.string.short_password)
    val invalidPassword = stringResource(id = R.string.invalid_password)


    Button(
        onClick = {
            onLoadingChange(true)
            // Validate email
            val emailResult = checkEmail(email)
            val passwordResult = checkPassword(password)

            when {
                emailResult == EmailResult.Empty ->
                    onResult(emptyEmail)
                emailResult == EmailResult.Invalid ->
                    onResult(invalidEmail)
                passwordResult == PasswordResult.Empty ->
                    onResult(emptyPassword)
                passwordResult == PasswordResult.Short ->
                    onResult(shortPassword)
                passwordResult == PasswordResult.Invalid ->
                    onResult(invalidPassword)
                else -> {
                    // Inputs are valid
                    signIn(email, password) { success, message, user ->

                        if (success && user != null) {
                            onResult(null)
                            onSuccess(user)
                        } else {
                            onResult(message)

                        }
                        onLoadingChange(false)
                    } // Firebase call
                }
            }
        },
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Text(stringResource(id = R.string.login_button))
    }

}



@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    loginButtonClick: (UserState) -> Unit
    //add callback functions or other parameters if you need
) {
    var email by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    var errorMessage by remember {mutableStateOf<String?>(null)}
    var loading by remember { mutableStateOf(false) }


    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Text("Loading...", Modifier.padding(top = 16.dp))
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            UserEmail(email = email, onEmailChange = {email = it})
            Spacer(modifier = Modifier.height(16.dp))
            UserPassword(password = password, onPasswordChange = {password = it})
            Spacer(modifier = Modifier.height(24.dp))
            ErrorText(errorMessage)
            LogInSignUpButton(
                email = email,
                password = password,
                onResult = {
                        msg -> errorMessage = msg
                },
                onLoadingChange = {loading = it},
                onSuccess = { user ->
                    // Check display name exists
                    if (user.displayName.isNullOrEmpty()) {
                        loginButtonClick(UserState(name = "", uid = user.uid))
                    } else {
                        loginButtonClick(UserState(name= user.displayName ?: "", uid = user.uid))
                    }
                }
            )
        }
    }

}
