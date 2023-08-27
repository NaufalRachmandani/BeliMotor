package com.naufal.belimotor.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    openLoginScreen: () -> Unit = {},
    openMainScreen: () -> Unit = {},
) {
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        delay(500)

        if (loginState.isLogged == true) {
            openMainScreen()
        } else {
            openLoginScreen()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {

        Text(
            text = "Beli Motor",
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}