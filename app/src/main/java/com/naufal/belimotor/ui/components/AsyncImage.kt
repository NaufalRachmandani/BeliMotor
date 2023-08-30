package com.naufal.belimotor.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naufal.belimotor.R
import com.naufal.belimotor.ui.theme.BeliMotorTheme
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun CustomCoilImage(
    modifier: Modifier = Modifier,
    model: String = "",
    imageOptions: ImageOptions = ImageOptions(),
) {
    CoilImage(
        imageModel = { model },
        modifier = modifier,
        imageOptions = imageOptions,
        previewPlaceholder = R.drawable.baseline_person_24,
        loading = {
            Box(
                modifier = modifier
                    .shimmerEffect()
            )
        },
        failure = {
            Icon(
                modifier = modifier,
                imageVector = Icons.Filled.ImageSearch,
                contentDescription = "image",
                tint = MaterialTheme.colorScheme.primary,
            )
        },
    )
}

@Preview
@Composable
fun CustomCoilImagePreview() {
    BeliMotorTheme {
        Surface {
            CustomCoilImage(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                model = "",
            )
        }
    }
}