package com.naufal.belimotor.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.naufal.belimotor.R

val Inter = FontFamily(
    Font(R.font.inter_regular),
)

private val defaultTypography = Typography()
val typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = Inter),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = Inter),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = Inter),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = Inter),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = Inter),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = Inter),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = Inter),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = Inter),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = Inter),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = Inter),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = Inter),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = Inter),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = Inter),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = Inter),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = Inter),
)


