package com.example.trackrscope.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DefaultColorSchemeDark = darkColorScheme(
    primary = primaryDefault,
    secondary = secondaryDefault,
    background = backgroundDefault,
    surface = surfaceDefault,
    primaryContainer = primaryContainer,
    secondaryContainer = secondaryContainer,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White,
    outline = outline,
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

val DefaultColorSchemeLight = lightColorScheme(
    primary = primaryDefaultLight,
    secondary = secondaryDefaultLight,
    background = backgroundDefaultLight,
    surface = surfaceDefaultLight,
    primaryContainer = primaryContainerLight,
    secondaryContainer = secondaryContainerLight,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onPrimaryContainer = Color.Black,
    onSecondaryContainer = Color.Black,
    outline = outlineLight,
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

val LoLColorSchemeDark = darkColorScheme(
    primary = primaryLoL,
    secondary = secondaryLoL,
    background = backgroundLoL,
    surface = surfaceLoL,
    primaryContainer = primaryContainerLoL,
    secondaryContainer = secondaryContainerLoL,
    onPrimary = Color.White,
    onSecondary = Color(0xFF000020),
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White,
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

val LoLColorSchemeLight = lightColorScheme(
    primary = primaryLoLLight,
    secondary = secondaryLoLLight,
    background = backgroundLoLLight,
    surface = surfaceLoLLight,
    primaryContainer = primaryContainerLoLLight,
    secondaryContainer = secondaryContainerLoLLight,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onPrimaryContainer = Color.Black,
    onSecondaryContainer = Color.Black,
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

val ValorantColorSchemeDark = darkColorScheme(
    primary = primaryValorant,
    secondary = secondaryValorant,
    background = backgroundValorant,
    surface = surfaceValorant,
    primaryContainer = primaryContainerValorant,
    secondaryContainer = secondaryContainerValorant,
    onPrimary = Color(0xFFE0E0E0),
    onSecondary = Color(0xFFFFE0E0),
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color(0xFF000000),
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

val ValorantColorSchemeLight = lightColorScheme(
    primary = primaryValorantLight,
    secondary = secondaryValorantLight,
    background = backgroundValorantLight,
    surface = surfaceValorantLight,
    primaryContainer = primaryContainerValorantLight,
    secondaryContainer = secondaryContainerValorantLight,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onPrimaryContainer = Color.Black,
    onSecondaryContainer = Color.Black,
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

val TFTColorSchemeDark = darkColorScheme(
    primary = primaryTFT,
    secondary = secondaryTFT,
    background = backgroundTFT,
    surface = surfaceTFT,
    primaryContainer = primaryContainerTFT,
    secondaryContainer = secondaryContainerTFT,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimaryContainer = Color.White,
    onSecondaryContainer = Color.White,
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

val TFTColorSchemeLight = lightColorScheme(
    primary = primaryTFTLight,
    secondary = secondaryTFTLight,
    background = backgroundTFTLight,
    surface = surfaceTFTLight,
    primaryContainer = primaryContainerTFTLight,
    secondaryContainer = secondaryContainerTFTLight,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onPrimaryContainer = Color.Black,
    onSecondaryContainer = Color.Black,
    error = Color(0xFFFF5449),
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceVariant = Color(0xFF404040),  // Para cada tema
    onSurfaceVariant = Color(0xFFCAC4D0),
)

enum class GameTheme {
    DEFAULT, LOL, VALORANT, TFT
}

@Composable
fun TrackrScopeTheme(
    gameTheme: GameTheme = GameTheme.DEFAULT,
    isDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme = when (gameTheme) {
        GameTheme.DEFAULT -> if (isDarkTheme) DefaultColorSchemeDark else DefaultColorSchemeLight
        GameTheme.LOL -> if (isDarkTheme) LoLColorSchemeDark else LoLColorSchemeLight
        GameTheme.VALORANT -> if (isDarkTheme) ValorantColorSchemeDark else ValorantColorSchemeLight
        GameTheme.TFT -> if (isDarkTheme) TFTColorSchemeDark else TFTColorSchemeLight
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TrackrScopeTypography,
        content = content
    )
}