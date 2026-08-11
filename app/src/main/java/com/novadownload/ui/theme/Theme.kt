package com.novadownload.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    secondary = Color(0xFF06B6D4),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFF0F0F0F),
    surface = Color(0xFF1A1A1A)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF7C3AED),
    secondary = Color(0xFF0891B2),
    background = Color(0xFFFCFCFC)
)

@Composable
fun NovaDownloadTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}
