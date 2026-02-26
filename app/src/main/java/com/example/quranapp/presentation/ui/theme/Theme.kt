package com.example.quranapp.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.MutableState

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

val LocalThemeMode = compositionLocalOf<MutableState<ThemeMode>> { error("No ThemeMode provided") }

private val LightColors = lightColorScheme(
    primary = GreenPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6F3EB), // Very light green
    onPrimaryContainer = Color(0xFF01361B),
    
    secondary = GoldSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFDF8EE), // Very light gold
    onSecondaryContainer = Color(0xFF5A4412),
    
    tertiary = GreenPrimaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE6F3EB),
    onTertiaryContainer = Color(0xFF01361B),
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    
    surfaceVariant = Color(0xFFE7E9E6),
    onSurfaceVariant = Color(0xFF444744),
    outline = Color(0xFF747975)
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF015228),
    onPrimaryContainer = Color(0xFF8FF5BA),
    
    secondary = GoldSecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5A4412),
    onSecondaryContainer = Color(0xFFFFDEA3),
    
    tertiary = GreenPrimaryDark,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF015228),
    onTertiaryContainer = Color(0xFF8FF5BA),

    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    
    surfaceVariant = Color(0xFF444744),
    onSurfaceVariant = Color(0xFFC4C7C3),
    outline = Color(0xFF8E928E)
)

@Composable
fun QuranAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = QuranTypography,
            content = content
        )
    }
}

val MaterialTheme.spacing: Spacing
    @Composable
    get() = LocalSpacing.current