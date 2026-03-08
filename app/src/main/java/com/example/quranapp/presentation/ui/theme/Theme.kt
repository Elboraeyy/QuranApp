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

// Local ThemeMode and LocalThemeMode removed to use domain model AppSettings

private val LightColors = lightColorScheme(
    primary = GreenPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9F0EC), // Very soft sage green
    onPrimaryContainer = Color(0xFF0C3322),
    
    secondary = GoldSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFDFBF5), // Extremely light gold/ivory tint
    onSecondaryContainer = Color(0xFF5A4A17),
    
    tertiary = GreenPrimaryLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE9F0EC),
    onTertiaryContainer = Color(0xFF0C3322),
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    
    surfaceVariant = Color(0xFFF1F4F2), // Subtle contrast for cards
    onSurfaceVariant = Color(0xFF4A5650),
    outline = Color(0xFFD3DDD8) // Barely visible outline for luxury feel
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A402D),
    onPrimaryContainer = Color(0xFFA6E2C6),
    
    secondary = GoldSecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF4A3C21),
    onSecondaryContainer = Color(0xFFFFE0B2),
    
    tertiary = GreenPrimaryDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1A402D),
    onTertiaryContainer = Color(0xFFA6E2C6),

    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    
    surfaceVariant = Color(0xFF2B332F),
    onSurfaceVariant = Color(0xFFC0CAC4),
    outline = Color(0xFF3F4D45) // Soft dark outline
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
        LocalSpacing provides Spacing(),
        androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl
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