package com.naufal.belimotor.ui.register

import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ImageSearch
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naufal.belimotor.data.auth.model.request.RegisterRequest
import com.naufal.belimotor.ui.components.CustomAsyncImage
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomOutlinedTextField
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    openLoginScreen: () -> Unit = {},
) {
    val registerState by viewModel.registerState.collectAsState()

    RegisterScreenContent(
        registerState = registerState,
        openLoginScreen = openLoginScreen,
        onRegister = {
            viewModel.register(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    registerState: RegisterViewModel.RegisterState = RegisterViewModel.RegisterState(),
    openLoginScreen: () -> Unit = {},
    onRegister: (RegisterRequest) -> Unit = {},
) {
    val context = LocalContext.current
    val snackScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Daftar", style = MaterialTheme.typography.titleMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = { openLoginScreen() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            var email by rememberSaveable { mutableStateOf("") }
            var password by rememberSaveable { mutableStateOf("") }
            var name by rememberSaveable { mutableStateOf("") }
            var motor by rememberSaveable { mutableStateOf("") }
            var image by rememberSaveable { mutableStateOf(Uri.EMPTY) }

            var isButtonEnabled by rememberSaveable { mutableStateOf(false) }

            val singleGalleryPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    if (uri != null) {
                        image = uri

                        isButtonEnabled = isButtonEnabled(email, password, name, motor, image)
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 100.dp),
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            singleGalleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        },
                ) {
                    if (image == Uri.EMPTY) {
                        Icon(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            imageVector = Icons.Filled.ImageSearch,
                            contentDescription = "avatar",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        CustomAsyncImage(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            model = image.toString(),
                            contentDescription = "avatar",
                            contentScale = ContentScale.Crop
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd),
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "edit",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                EmailField(
                    text = email,
                    onTextChanged = {
                        email = it

                        isButtonEnabled = isButtonEnabled(email, password, name, motor, image)
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(
                    text = password,
                    onTextChanged = {
                        password = it

                        isButtonEnabled = isButtonEnabled(email, password, name, motor, image)
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                NameField(
                    text = name,
                    onTextChanged = {
                        name = it

                        isButtonEnabled = isButtonEnabled(email, password, name, motor, image)
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                MotorField(
                    text = motor,
                    onTextChanged = {
                        motor = it

                        isButtonEnabled = isButtonEnabled(email, password, name, motor, image)
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
                onRegister(
                    RegisterRequest(
                        email = email,
                        password = password,
                        image = image.toString(),
                        name = name,
                        favMotor = motor,
                    )
                )
            }

            if (registerState.loading == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (registerState.error == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            registerState.message ?: "Gagal mendaftar"
                        )
                    }
                }
            }

            if (registerState.success == true) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Berhasil mendaftar", Toast.LENGTH_SHORT)
                        .show()

                    openLoginScreen()
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
fun NameField(
    text: String,
    onTextChanged: (String) -> Unit,
) {
    Text(
        modifier = Modifier,
        text = "Nama",
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
                text = "Masukkan nama kamu..",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
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

fun isButtonEnabled(
    email: String,
    password: String,
    name: String,
    motor: String,
    image: Uri
): Boolean {
    return email.isEmailValid() && password.length >= 4 && name.length > 1 && motor.length > 1 && image != Uri.EMPTY
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

@Preview(showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    BeliMotorTheme {
        Surface {
            RegisterScreenContent()
        }
    }
}