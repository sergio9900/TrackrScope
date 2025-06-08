package com.example.trackrscope.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.trackrscope.R

val ExoFontFamily = FontFamily(
    Font(R.font.exo_regular),
    Font(R.font.exo_bold)
)

val TrackrScopeTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 10.sp
    ),
    titleLarge = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 14.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 30.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 20.sp
    ),
    displayLarge = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 32.sp
    ),
    displayMedium = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 28.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ExoFontFamily,
        fontSize = 24.sp
    ),
)
