package com.naufal.belimotor.ui.register

import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    openLoginScreen: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Daftar", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var isButtonEnabled by rememberSaveable { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {
                var email by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable { mutableStateOf("") }
                var motor by rememberSaveable { mutableStateOf("") }

                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Daftar",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(50.dp))

                EmailField(
                    text = email,
                    onTextChanged = {
                        email = it

                        isButtonEnabled = isButtonEnabled(email, password, motor)
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(
                    text = password,
                    onTextChanged = {
                        password = it

                        isButtonEnabled = isButtonEnabled(email, password, motor)
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotorField(
                    text = motor,
                    onTextChanged = {
                        motor = it

                        isButtonEnabled = isButtonEnabled(email, password, motor)
                    },
                )
            }

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                text = "Daftar",
                isEnabled = isButtonEnabled
            ) {

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


@Composable
fun MotorField(
    text: String,
    onTextChanged: (String) -> Unit,
) {
    Text(
        modifier = Modifier,
        text = "Motor Favorit",
        style = MaterialTheme.typography.titleMedium,
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
                text = "Masukkan motor favorit kamu..",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
    )
}

fun isButtonEnabled(email: String, password: String, motor: String): Boolean {
    return email.isEmailValid() && password.length >= 4 && motor.length > 1
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

@Preview
@Composable
fun RegisterScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        RegisterScreen()
    }
}