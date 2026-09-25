package com.example.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Supported Theme Modes for NEXORA
 */
enum class AppThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark")
}

/**
 * Nexora dynamic semantic color set for Dark and Light themes.
 */
data class NexoraColorPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val surfaceBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color,
    val accent: Color,
    val accentLight: Color,
    val violetAccent: Color,
    val pinkAccent: Color,
    val success: Color,
    val successContainer: Color,
    val error: Color,
    val errorContainer: Color,
    val warning: Color,
    val cardBackground: Color
)

val DarkNexoraPalette = NexoraColorPalette(
    isDark = true,
    background = Color(0xFF0D0F17),
    surface = Color(0xFF141724),
    surfaceElevated = Color(0xFF1B2032),
    surfaceBorder = Color(0xFF262C42),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    primary = Color(0xFF6366F1),
    primaryLight = Color(0xFF818CF8),
    primaryDark = Color(0xFF4338CA),
    accent = Color(0xFF06B6D4),
    accentLight = Color(0xFF67E8F9),
    violetAccent = Color(0xFFA855F7),
    pinkAccent = Color(0xFFEC4899),
    success = Color(0xFF10B981),
    successContainer = Color(0xFF064E3B),
    error = Color(0xFFF43F5E),
    errorContainer = Color(0xFF4C0519),
    warning = Color(0xFFF59E0B),
    cardBackground = Color(0xFF141724)
)

val LightNexoraPalette = NexoraColorPalette(
    isDark = false,
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFF1F3F5),
    surfaceBorder = Color(0xFFE2E8F0),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF94A3B8),
    primary = Color(0xFF4F46E5),
    primaryLight = Color(0xFF6366F1),
    primaryDark = Color(0xFF3730A3),
    accent = Color(0xFF0891B2),
    accentLight = Color(0xFF06B6D4),
    violetAccent = Color(0xFF9333EA),
    pinkAccent = Color(0xFFDB2777),
    success = Color(0xFF059669),
    successContainer = Color(0xFFD1FAE5),
    error = Color(0xFFE11D48),
    errorContainer = Color(0xFFFFE4E6),
    warning = Color(0xFFD97706),
    cardBackground = Color(0xFFFFFFFF)
)

val LocalNexoraColors = staticCompositionLocalOf { DarkNexoraPalette }

object NexoraThemeManager {
    private const val PREFS_NAME = "nexora_theme_prefs"
    private const val KEY_THEME_MODE = "app_theme_mode"

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedModeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        val mode = try {
            AppThemeMode.valueOf(savedModeStr ?: AppThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            AppThemeMode.SYSTEM
        }
        _themeMode.value = mode
    }

    fun setThemeMode(context: Context, mode: AppThemeMode) {
        _themeMode.value = mode
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }
}
