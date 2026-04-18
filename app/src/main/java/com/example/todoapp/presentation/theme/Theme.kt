package com.example.todoapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalThemeColor = staticCompositionLocalOf { Color(0xFF6200EE) }

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFFBB86FC),
        secondary = Color(0xFF03DAC6),
        tertiary = Color(0xFF3700B3),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onTertiary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
    )

private fun getLightColorScheme(primaryColor: Color) =
    lightColorScheme(
        primary = primaryColor,
        secondary = Color(0xFF03DAC6),
        tertiary = Color(0xFF3700B3),
        background = Color(0xFFE7E7E7),
        surface = Color(0xFFFFFBFE),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onTertiary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black,
    )

private fun getDarkColorScheme(primaryColor: Color) =
    DarkColorScheme.copy(
        primary = primaryColor,
    )

@Composable
fun YourAppTheme(
    primaryColor: Color,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme =
        if (darkTheme) {
            getDarkColorScheme(primaryColor)
        } else {
            getLightColorScheme(primaryColor)
        }

    CompositionLocalProvider(LocalThemeColor provides primaryColor) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
