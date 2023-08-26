package com.naufal.belimotor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    text: String,
    onValueChanged: (String) -> Unit = { },
    placeholder: @Composable () -> Unit = {},
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    isEnable: Boolean = true,
    singleLine: Boolean = true,
    shape: Shape = RoundedCornerShape(6.dp),
    autoCorrect: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
//    textFieldColors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
//        focusedBorderColor = md_theme_light_primary,
//        cursorColor = md_theme_light_primary,
//        unfocusedBorderColor = md_theme_light_secondary,
//        containerColor = md_theme_light_primaryContainer,
//    ),
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.None,
) {
    OutlinedTextField(
        modifier = modifier,
        value = text,
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        placeholder = { placeholder() },
        onValueChange = {
            if (keyboardType == KeyboardType.Number) {
                if (it.isEmpty()) {
                    onValueChanged(it)
                } else if (it.last().isDigit()) {
                    onValueChanged(it)
                }
            } else {
                onValueChanged(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            capitalization = keyboardCapitalization,
            autoCorrect = autoCorrect,
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        shape = shape,
        textStyle = textStyle,
        enabled = isEnable,
        singleLine = singleLine,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
    )
}

@Preview
@Composable
fun InputPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        CustomOutlinedTextField(modifier = Modifier.align(Alignment.Center), text = "test")
    }
}