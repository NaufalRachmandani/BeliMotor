package com.naufal.belimotor.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.naufal.belimotor.R

@Composable
fun CustomAsyncImage(
    modifier: Modifier = Modifier,
    model: String = "",
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(data = model)
            .apply {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Filled.ImageSearch,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            .crossfade(true)
            .size(Size.ORIGINAL)
            .build(),
        loading = {
            CircularProgressIndicator()
        },
        contentDescription = contentDescription,
        contentScale = contentScale,
    )
}