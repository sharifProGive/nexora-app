package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

private val NexoraDarkColorScheme = darkColorScheme(
    primary = DarkNexoraPalette.primary,
    onPrimary = Color.White,
    primaryContainer = DarkNexoraPalette.primaryDark,
    onPrimaryContainer = DarkNexoraPalette.primaryLight,
    secondary = DarkNexoraPalette.accent,
    onSecondary = Color(0xFF090A0F),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = DarkNexoraPalette.accentLight,
    tertiary = DarkNexoraPalette.violetAccent,
    onTertiary = Color.White,
    background = DarkNexoraPalette.background,
    onBackground = DarkNexoraPalette.textPrimary,
    surface = DarkNexoraPalette.surface,
    onSurface = DarkNexoraPalette.textPrimary,
    surfaceVariant = DarkNexoraPalette.surfaceElevated,
    onSurfaceVariant = DarkNexoraPalette.textSecondary,
    outline = DarkNexoraPalette.surfaceBorder,
    error = DarkNexoraPalette.error,
    onError = Color.White,
    errorContainer = DarkNexoraPalette.errorContainer,
    onErrorContainer = Color(0xFFFFB4AB)
)

private val NexoraLightColorScheme = lightColorScheme(
    primary = LightNexoraPalette.primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = LightNexoraPalette.primaryDark,
    secondary = LightNexoraPalette.accent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF155E75),
    tertiary = LightNexoraPalette.violetAccent,
    onTertiary = Color.White,
    background = LightNexoraPalette.background,
    onBackground = LightNexoraPalette.textPrimary,
    surface = LightNexoraPalette.surface,
    onSurface = LightNexoraPalette.textPrimary,
    surfaceVariant = LightNexoraPalette.surfaceElevated,
    onSurfaceVariant = LightNexoraPalette.textSecondary,
    outline = LightNexoraPalette.surfaceBorder,
    error = LightNexoraPalette.error,
    onError = Color.White,
    errorContainer = LightNexoraPalette.errorContainer,
    onErrorContainer = Color(0xFF9F1239)
)

/**
 * Access the active NEXORA semantic colors for either Dark or Light theme.
 */
object NexoraTheme {
    val colors: NexoraColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalNexoraColors.current
}

@Composable
fun NexoraTheme(
    themeMode: AppThemeMode? = null,
    content: @Composable () -> Unit
) {
    val activeMode by if (themeMode != null) {
        androidx.compose.runtime.rememberUpdatedState(themeMode)
    } else {
        NexoraThemeManager.themeMode.collectAsState()
    }

    val isDark = when (activeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) NexoraDarkColorScheme else NexoraLightColorScheme
    val nexoraPalette = if (isDark) DarkNexoraPalette else LightNexoraPalette

    CompositionLocalProvider(LocalNexoraColors provides nexoraPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NexoraTheme(content = content)
}
