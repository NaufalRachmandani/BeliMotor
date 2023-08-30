package com.naufal.belimotor.ui.features.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naufal.belimotor.data.auth.model.response.UserResponse
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomCoilImage
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditClick: () -> Unit = {},
    openLoginScreen: () -> Unit = {},
) {
    val profileState by viewModel.profileState.collectAsState()
    val logoutState by viewModel.logoutState.collectAsState()

    if (logoutState.success == true) {
        openLoginScreen()
    }

    ProfileScreenContent(
        profileState = profileState,
        logoutState = logoutState,
        onEditClick = onEditClick,
        onLogout = { viewModel.logout() })
}

@Composable
fun ProfileScreenContent(
    profileState: ProfileViewModel.ProfileState = ProfileViewModel.ProfileState(),
    logoutState: ProfileViewModel.LogoutState = ProfileViewModel.LogoutState(),
    onEditClick: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val snackScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onEditClick() },
                ) {
                    CustomCoilImage(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        model = profileState.userResponse?.image.toString(),
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

                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = profileState.userResponse?.name ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(
                        text = "email :",
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = profileState.userResponse?.email ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.align(Alignment.Start)) {
                    Text(
                        text = "Motor Favorit :",
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = profileState.userResponse?.favMotor ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                text = "Logout",
                buttonColors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                ),
                textColor = Color.White,
            ) {
                onLogout()
            }

            if (profileState.loading == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (profileState.error == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            profileState.message ?: "Gagal memuat data"
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    BeliMotorTheme {
        Surface {
            ProfileScreenContent(
                profileState = ProfileViewModel.ProfileState(
                    userResponse = UserResponse(
                        email = "naufrach@gmail.com",
                        name = "Naufal Rachmandani",
                        favMotor = "Supra GTR"
                    )
                )
            )
        }
    }
}