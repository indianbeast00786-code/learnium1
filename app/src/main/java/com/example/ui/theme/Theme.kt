package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SchoolNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = SchoolNavyDark,
    secondary = SchoolGold,
    onSecondary = Color.White,
    secondaryContainer = SchoolGoldLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    tertiaryContainer = SuccessGreenLight,
    onTertiaryContainer = Color(0xFF064E3B),
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    outline = Slate200
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = SchoolNavyDark,
    primaryContainer = SchoolNavy,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF78350F),
    background = Slate900,
    onBackground = Color.White,
    surface = Slate800,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Slate200,
    outline = Color(0xFF475569)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep the custom Learnium School branding crisp
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
