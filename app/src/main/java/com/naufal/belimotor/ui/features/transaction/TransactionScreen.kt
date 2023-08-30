package com.naufal.belimotor.ui.features.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.naufal.belimotor.data.motor.model.MotorDetail
import com.naufal.belimotor.data.motor.model.Transaction
import com.naufal.belimotor.ui.components.CustomButton
import com.naufal.belimotor.ui.components.CustomCoilImage
import com.naufal.belimotor.ui.components.shimmerEffect
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import com.naufal.belimotor.ui.util.toCurrencyFormatID
import com.skydoves.landscapist.ImageOptions
import kotlinx.coroutines.launch

@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    openHomeScreen: () -> Unit = {},
    openMotorDetail: (String) -> Unit = {},
) {
    val transactionState by viewModel.transactionState.collectAsState()

    TransactionScreenContent(
        transactionState = transactionState,
        openHomeScreen = openHomeScreen,
        openMotorDetail = openMotorDetail,
        onContinue = {},
        onCancel = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenContent(
    transactionState: TransactionViewModel.TransactionState = TransactionViewModel.TransactionState(),
    openHomeScreen: () -> Unit = {},
    openMotorDetail: (String) -> Unit = {},
    onContinue: (String) -> Unit = {},
    onCancel: (String) -> Unit = {},
) {
    val snackScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Daftar Transaksi",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp, top = 24.dp),
            ) {
                if (transactionState.transactionList?.isNotEmpty() == true) {
                    val transactionList = transactionState.transactionList
                    items(transactionList.size) { index ->
                        val transaction: Transaction? = transactionList[index]
                        transaction?.let {
                            ItemTransaction(
                                transaction = it,
                                openMotorDetail = openMotorDetail,
                                onContinue = onContinue,
                                onCancel = onCancel,
                            )
                        }
                    }
                } else {
                    items(5) {
                        ItemTransactionShimmer()
                    }
                }
            }

            if (transactionState.error == true) {
                LaunchedEffect(snackbarHostState) {
                    snackScope.launch {
                        snackbarHostState.showSnackbar(
                            transactionState.message ?: "Gagal memuat data"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemTransaction(
    modifier: Modifier = Modifier,
    transaction: Transaction = Transaction(),
    openMotorDetail: (String) -> Unit = {},
    onContinue: (String) -> Unit = {},
    onCancel: (String) -> Unit = {},
) {
    Column(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {
                transaction.motorId?.let { openMotorDetail(it) }
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomCoilImage(
                modifier = Modifier
                    .size(100.dp),
                model = transaction.motorImage ?: "",
                imageOptions = ImageOptions(contentScale = ContentScale.Fit),
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    text = "asdasdsadasd",//motorDetail.motorName ?: "-",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = "asdasdasdasdasdasdasdasdasd",//motorDetail.motorDesc ?: "-",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 10.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.primary)
                .padding(6.dp)
        ) {
            Text(
                text = "asdasdasdsad",//motorDetail.motorPrice.toString().toCurrencyFormatID(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 10.dp)
                .align(Alignment.End),
        ) {
            CustomButton(
                text = "Batalkan",
                contentPaddingValues = PaddingValues(6.dp),
                buttonColors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                ),
                textColor = Color.White,
            ) {
                transaction.transactionId?.let { onCancel(it) }
            }

            Spacer(modifier = Modifier.width(6.dp))

            CustomButton(
                text = "Lanjutkan Transaksi",
                contentPaddingValues = PaddingValues(6.dp),
            ) {
                transaction.transactionId?.let { onContinue(it) }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ItemTransactionShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 10.dp, top = 10.dp)
                    .shimmerEffect(),
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(horizontal = 10.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(horizontal = 10.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(horizontal = 10.dp)
                        .shimmerEffect()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(26.dp)
                .padding(horizontal = 10.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 10.dp)
                .align(Alignment.End),
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp)
                    .clip(MaterialTheme.shapes.small)
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
fun TransactionScreenPreview() {
    BeliMotorTheme {
        Surface {
            TransactionScreenContent()
        }
    }
}