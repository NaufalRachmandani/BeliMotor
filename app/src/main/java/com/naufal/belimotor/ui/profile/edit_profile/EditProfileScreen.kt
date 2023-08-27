package com.naufal.belimotor.ui.profile.edit_profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naufal.belimotor.R
import com.naufal.belimotor.ui.components.CustomAsyncImage
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomOutlinedTextField
import com.naufal.belimotor.ui.register.isEmailValid
import com.naufal.belimotor.ui.theme.BeliMotorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    openProfileScreen: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Profil",
                        style = MaterialTheme.typography.titleMedium
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
        }
    ) { paddingValues ->
        var name by rememberSaveable { mutableStateOf("") }
        var motor by rememberSaveable { mutableStateOf("") }

        var isButtonEnabled by rememberSaveable { mutableStateOf(false) }

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

                        },
                ) {
                    CustomAsyncImage(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        model = "https://picsum.photos/200",
                        contentDescription = "avatar",
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
                    isButtonEnabled = isButtonEnabled(name, motor)
                })

                Spacer(modifier = Modifier.height(24.dp))

                MotorField(text = motor, onTextChanged = {
                    motor = it
                    isButtonEnabled = isButtonEnabled(name, motor)
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
                text = "Masukkan motor Favorit kamu..",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
    )
}

fun isButtonEnabled(name: String, motor: String): Boolean {
    return name.length > 1 && motor.length > 1
}

@Preview(showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    BeliMotorTheme {
        Surface {
            EditProfileScreen()
        }
    }
}