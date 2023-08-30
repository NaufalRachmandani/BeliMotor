package com.naufal.belimotor.ui.features.profile.edit_profile

import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naufal.belimotor.data.auth.model.request.EditUserRequest
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomCoilImage
import com.naufal.belimotor.ui.components.CustomOutlinedTextField
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    openProfileScreen: () -> Unit = {},
) {
    val editProfileState by viewModel.editProfileState.collectAsState()

    EditProfileScreenContent(
        editProfileState = editProfileState,
        openProfileScreen = openProfileScreen,
        onSaveClick = {
            viewModel.editUser(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(
    editProfileState: EditProfileViewModel.EditProfileState = EditProfileViewModel.EditProfileState(),
    openProfileScreen: () -> Unit = {},
    onSaveClick: (EditUserRequest) -> Unit = { }
) {
    val snackScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profil",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = { openProfileScreen() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        var name by rememberSaveable { mutableStateOf("") }
        var motor by rememberSaveable { mutableStateOf("") }
        var image by rememberSaveable { mutableStateOf(Uri.EMPTY) }

        var isButtonEnabled by rememberSaveable { mutableStateOf(false) }

        val singleGalleryPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    image = uri

                    isButtonEnabled = isButtonEnabled(name, motor, image)
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
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
                    CustomCoilImage(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        model = image.toString(),
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                    )

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

                NameField(text = name, onTextChanged = {
                    name = it
                    isButtonEnabled = isButtonEnabled(name, motor, image)
                })

                Spacer(modifier = Modifier.height(24.dp))

                MotorField(text = motor, onTextChanged = {
                    motor = it
                    isButtonEnabled = isButtonEnabled(name, motor, image)
                })
            }

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                text = "Simpan",
                isEnabled = isButtonEnabled
            ) {
                onSaveClick(EditUserRequest(image = image, name = name, favMotor = motor))
            }

            if (editProfileState.loading == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (editProfileState.error == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            editProfileState.message ?: "Gagal memuat data"
                        )
                    }
                }
            }

            if (editProfileState.userResponse != null) {
                LaunchedEffect(Unit) {
                    image = editProfileState.userResponse.image
                    name = editProfileState.userResponse.name
                    motor = editProfileState.userResponse.favMotor

                    isButtonEnabled = isButtonEnabled(name, motor, image)
                }
            }

            if (editProfileState.success == true) {
                LaunchedEffect(Unit) {
                    openProfileScreen()
                }
            }
        }
    }
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
                text = "Masukkan nama kamu..",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
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
                text = "Masukkan motor Favorit kamu..",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
    )
}

fun isButtonEnabled(name: String, motor: String, image: Uri): Boolean {
    return name.length > 1 && motor.length > 1 && image != Uri.EMPTY
}

@Preview(showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    BeliMotorTheme {
        Surface {
            EditProfileScreenContent()
        }
    }
}