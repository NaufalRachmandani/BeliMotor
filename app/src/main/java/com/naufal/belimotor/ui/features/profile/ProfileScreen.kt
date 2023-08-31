package com.naufal.belimotor.ui.features.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.naufal.belimotor.data.auth.model.response.UserResponse
import com.naufal.belimotor.data.motor.model.MotorDetail
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomCoilImage
import com.naufal.belimotor.ui.components.shimmerEffect
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import com.naufal.belimotor.ui.util.OnLifecycleEvent
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditClick: () -> Unit = {},
    openLoginScreen: () -> Unit = {},
) {
    OnLifecycleEvent { owner, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getUser()
            }

            else -> {}
        }
    }

    val profileState by viewModel.profileState.collectAsState()
    val motorListState by viewModel.motorListState.collectAsState()
    val logoutState by viewModel.logoutState.collectAsState()

    if (logoutState.success == true) {
        openLoginScreen()
    }

    ProfileScreenContent(
        profileState = profileState,
        motorListState = motorListState,
        logoutState = logoutState,
        onEditClick = onEditClick,
        onLogout = { viewModel.logout() })
}

@Composable
fun ProfileScreenContent(
    profileState: ProfileViewModel.ProfileState = ProfileViewModel.ProfileState(),
    motorListState: ProfileViewModel.MotorListState = ProfileViewModel.MotorListState(),
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                state = rememberLazyListState()
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                        text = profileState.userResponse?.name ?: "-",
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier) {
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

                    Row(modifier = Modifier) {
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

                    Spacer(modifier = Modifier.height(24.dp))
                }

                sectionList(motorListState)
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

fun LazyListScope.sectionList(
    motorListState: ProfileViewModel.MotorListState = ProfileViewModel.MotorListState(),
) {
    if (motorListState.motorList?.isNotEmpty() == true) {
        val motorList = motorListState.motorList
        items(motorList.size) { index ->
            val motorDetail: MotorDetail? = motorList[index]
            motorDetail?.let {
                ItemMotor(
                    motorDetail = motorDetail
                )
            }

            if (index < motorList.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    } else if (motorListState.loading == true) {
        items(3) {
            ItemMotorShimmer()

            if (it < 2) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ItemMotor(
    modifier: Modifier = Modifier,
    motorDetail: MotorDetail = MotorDetail(),
) {
    Column(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomCoilImage(
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 10.dp, top = 10.dp),
                model = motorDetail.motorImage ?: "",
                imageOptions = ImageOptions(contentScale = ContentScale.Fit),
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
                text = motorDetail.motorName ?: "-",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 10.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(Color.Red)
                .padding(6.dp)
        ) {
            Text(
                text = "Memiliki ${motorDetail.motorQty}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ItemMotorShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .padding(start = 10.dp, top = 10.dp)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
                    .padding(horizontal = 10.dp)
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 10.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .padding(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(20.dp)
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
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