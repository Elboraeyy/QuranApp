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
    primaryContainer = Color(0xFFF1F7E9), // Soft Creamy Sage
    onPrimaryContainer = Color(0xFF1B4332),
    
    secondary = GoldSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF9E6), // Warm Buttercream
    onSecondaryContainer = Color(0xFF8B734B),
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    
    surfaceVariant = Color(0xFFF5F0DA), // Warm Almond Card feel
    onSurfaceVariant = Color(0xFF4E4034),
    outline = Color(0xFFE4DFCA) // Antique parchment feel
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF243B2E),
    onPrimaryContainer = Color(0xFFBCE6CC),
    
    secondary = GoldSecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF352F24), // Warm Dark Toasted Walnut
    onSecondaryContainer = Color(0xFFF1E6CF),
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    
    surfaceVariant = Color(0xFF2E271F), // Deep Warm Coffee
    onSurfaceVariant = Color(0xFFDED2C4),
    outline = Color(0xFF453A2F)
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
            shapes = QuranShapes,
            content = content
        )
    }
}

val MaterialTheme.spacing: Spacing
    @Composable
    get() = LocalSpacing.current