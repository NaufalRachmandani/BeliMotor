package com.naufal.belimotor.ui.features.motor_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naufal.belimotor.data.motor.model.MotorDetail
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomCoilImage
import com.naufal.belimotor.ui.components.shimmerEffect
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import com.naufal.belimotor.ui.util.toCurrencyFormatID
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.launch

@Composable
fun MotorDetailScreen(
    viewModel: MotorDetailViewModel = hiltViewModel(),
    motorId: String = "",
    openHomeScreen: () -> Unit = {},
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getMotor(motorId)
    }

    val motorDetailState by viewModel.motorDetailState.collectAsState()
    val buyMotorState by viewModel.buyMotorState.collectAsState()

    MotorDetailScreenContent(
        motorDetailState = motorDetailState,
        buyMotorState = buyMotorState,
        openHomeScreen = openHomeScreen,
        onBuyClick = {
            viewModel.buyMotor(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorDetailScreenContent(
    motorDetailState: MotorDetailViewModel.MotorDetailState = MotorDetailViewModel.MotorDetailState(),
    buyMotorState: MotorDetailViewModel.BuyMotorState = MotorDetailViewModel.BuyMotorState(),
    openHomeScreen: () -> Unit = {},
    onBuyClick: (MotorDetail) -> Unit = {},
) {
    val snackScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detail Motor",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = { openHomeScreen() }) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (motorDetailState.loading == true) {
                MotorDetailShimmer()
            } else if (motorDetailState.motorDetail != null) {
                val motorDetail = motorDetailState.motorDetail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        CustomCoilImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .align(Alignment.Center),
                            model = motorDetail.motorImage ?: "",
                            imageOptions = ImageOptions(contentScale = ContentScale.Fit),
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        text = motorDetail.motorName ?: "",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        text = motorDetail.motorDesc ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentWidth()
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Sisa",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = motorDetail.motorQty.toString(),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .wrapContentWidth()
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(6.dp)
                        ) {
                            Text(
                                text = motorDetail.motorPrice.toString().toCurrencyFormatID(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }

            CustomButton(
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                text = "Beli",
                contentPaddingValues = PaddingValues(vertical = 12.dp, horizontal = 18.dp),
                buttonColors = ButtonDefaults.buttonColors(
                    containerColor = Color(0XFF176d33),
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textColor = Color.White,
            ) {
                if (motorDetailState.motorDetail != null) {
                    onBuyClick(motorDetailState.motorDetail)
                }
            }

            if (motorDetailState.error == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            motorDetailState.message ?: "Gagal memuat data"
                        )
                    }
                }
            }

            if (buyMotorState.loading == true) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (buyMotorState.success == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            buyMotorState.message.toString()
                        )
                    }
                }
            }

            if (buyMotorState.error == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            buyMotorState.message ?: "Gagal membeli motor"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MotorDetailShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .shimmerEffect(),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(20.dp)
                .padding(horizontal = 10.dp)
                .shimmerEffect(),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 10.dp)
                .shimmerEffect(),
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 10.dp)
                .shimmerEffect(),
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 10.dp)
                .shimmerEffect(),
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 10.dp)
                .shimmerEffect(),
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(30.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .shimmerEffect()
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    BeliMotorTheme {
        Surface {
            MotorDetailScreenContent()
        }
    }
}