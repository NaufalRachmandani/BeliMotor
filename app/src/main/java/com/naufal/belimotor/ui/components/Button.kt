package com.naufal.belimotor.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.naufal.belimotor.ui.theme.BeliMotorTheme

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    text: String = "",
    isEnabled: Boolean = true,
    contentPaddingValues: PaddingValues = PaddingValues(12.dp),
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.outline,
    ),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderStroke: BorderStroke? = null,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier
            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        contentPadding = contentPaddingValues,
        onClick = {
            onClick()
        },
        shape = MaterialTheme.shapes.small,
        colors = buttonColors,
        enabled = isEnabled,
        border = borderStroke,
    ) {
        Text(
            text = text,
            style = textStyle,
            color = textColor,
            fontWeight = FontWeight.W500
        )
    }
}

@Preview
@Composable
fun ButtonPreview() {
    BeliMotorTheme {
        Surface {
            CustomButton(text = "Selanjutnya")
        }
    }
}