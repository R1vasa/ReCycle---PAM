package com.example.recycle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ReCycleLightColorScheme = lightColorScheme(
    primary = Primary800,
    onPrimary = PureWhite,
    primaryContainer = SurfaceGreen,
    onPrimaryContainer = Primary900,
    secondary = Primary700,
    onSecondary = PureWhite,
    secondaryContainer = Primary200,
    onSecondaryContainer = Primary900,
    tertiary = Primary500,
    onTertiary = PureWhite,
    tertiaryContainer = WarmCream,
    onTertiaryContainer = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = PureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceGreen,
    onSurfaceVariant = TextSecondary,
    outline = OutlineLight,
    error = ErrorRed,
    onError = PureWhite,
    errorContainer = ErrorSurface,
    onErrorContainer = ErrorRed
)

@Composable
fun ReCycleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ReCycleLightColorScheme,
        typography = ReCycleTypography,
        shapes = ReCycleShapes,
        content = content
    )
}