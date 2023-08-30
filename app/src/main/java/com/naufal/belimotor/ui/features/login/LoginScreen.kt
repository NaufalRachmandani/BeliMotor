package com.naufal.belimotor.ui.features.login

import android.text.TextUtils
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomOutlinedTextField
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    openMainScreen: () -> Unit = {},
    openRegisterScreen: () -> Unit = {},
) {
    val loginState by viewModel.loginState.collectAsState()

    LoginScreenContent(
        loginState = loginState,
        openMainScreen = openMainScreen,
        openRegisterScreen = openRegisterScreen,
        onLogin = { email, password ->
            viewModel.login(email, password)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    loginState: LoginViewModel.LoginState = LoginViewModel.LoginState(),
    openMainScreen: () -> Unit = {},
    openRegisterScreen: () -> Unit = {},
    onLogin: (String, String) -> Unit = { _, _ -> }
) {
    val snackScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Masuk",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }

            var isButtonEnabled by rememberSaveable { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                EmailField(
                    text = email,
                    onTextChanged = {
                        email = it

                        isButtonEnabled = isButtonEnabled(email, password)
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(
                    text = password,
                    onTextChanged = {
                        password = it

                        isButtonEnabled = isButtonEnabled(email, password)
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            openRegisterScreen()
                        }
                        .padding(10.dp),
                    text = "Daftar",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                text = "Masuk",
                isEnabled = isButtonEnabled
            ) {
                onLogin(email, password)
            }

            if (loginState.loading == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (loginState.error == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            loginState.message ?: "Gagal masuk"
                        )
                    }
                }
            }

            if (loginState.success == true) {
                LaunchedEffect(Unit) {
                    openMainScreen()
                }
            }
        }
    }
}

@Composable
fun EmailField(
    text: String,
    onTextChanged: (String) -> Unit,
) {
    Text(
        modifier = Modifier,
        text = "Email",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(modifier = Modifier.height(8.dp))
    CustomOutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        text = text,
        onValueChanged = {
            onTextChanged(it)
        },
        placeholder = {
            Text(
                text = "Masukkan Email..",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
    )
}

@Composable
fun PasswordField(
    text: String,
    onTextChanged: (String) -> Unit,
) {
    var currentPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Text(
        modifier = Modifier,
        text = "Password",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(modifier = Modifier.height(8.dp))
    CustomOutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        text = text,
        onValueChanged = {
            onTextChanged(it)
        },
        placeholder = {
            Text(
                text = "Masukkan Password..",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardType = KeyboardType.Password,
        trailingIcon = {
            val image = if (currentPasswordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description =
                if (currentPasswordVisible) "Hide password" else "Show password"

            IconButton(onClick = {
                currentPasswordVisible = !currentPasswordVisible
            }) {
                Icon(imageVector = image, description)
            }
        }
    )
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun isButtonEnabled(email: String, password: String): Boolean {
    return email.isEmailValid() && password.length >= 4
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    BeliMotorTheme {
        Surface {
            LoginScreenContent()
        }
    }
}
