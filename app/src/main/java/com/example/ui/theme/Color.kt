package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Default Dark static constants kept for backwards-compatibility
val NexoraVoidBlack = Color(0xFF090A0F)
val NexoraDarkBackground = Color(0xFF0D0F17)
val NexoraSurfaceDark = Color(0xFF141724)
val NexoraSurfaceElevated = Color(0xFF1B2032)
val NexoraSurfaceBorder = Color(0xFF262C42)

val NexoraIndigoPrimary = Color(0xFF6366F1)
val NexoraIndigoLight = Color(0xFF818CF8)
val NexoraIndigoDark = Color(0xFF4338CA)

val NexoraCyanAccent = Color(0xFF06B6D4)
val NexoraCyanLight = Color(0xFF67E8F9)
val NexoraVioletAccent = Color(0xFFA855F7)
val NexoraPinkAccent = Color(0xFFEC4899)

val NexoraTextPrimary = Color(0xFFF8FAFC)
val NexoraTextSecondary = Color(0xFF94A3B8)
val NexoraTextMuted = Color(0xFF64748B)

val NexoraSuccess = Color(0xFF10B981)
val NexoraSuccessGreen = NexoraSuccess
val NexoraSuccessContainer = Color(0xFF064E3B)
val NexoraError = Color(0xFFF43F5E)
val NexoraErrorRed = NexoraError
val NexoraErrorContainer = Color(0xFF4C0519)
val NexoraWarning = Color(0xFFF59E0B)

/**
 * Convenient dynamic colors that adapt to current Light/Dark theme.
 * Use these or `NexoraTheme.colors` for seamless theme switching!
 */
object NexoraDynamicColors {
    val background: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.background

    val surface: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.surface

    val surfaceElevated: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.surfaceElevated

    val surfaceBorder: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.surfaceBorder

    val textPrimary: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.textPrimary

    val textSecondary: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.textSecondary

    val textMuted: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.textMuted

    val primary: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.primary

    val accent: Color
        @Composable
        @ReadOnlyComposable
        get() = NexoraTheme.colors.accent
}
