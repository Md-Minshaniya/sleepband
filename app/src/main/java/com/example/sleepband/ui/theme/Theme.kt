package com.example.sleepband.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Orange,          // ✅ your orange becomes main color
    onPrimary = White,

    secondary = Amber,
    onSecondary = Black,

    background = LightGray,    // ✅ light grey background like real apps
    onBackground = Black,

    surface = White,           // ✅ cards/panels are white
    onSurface = Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = Black,

    secondary = Amber,
    onSecondary = Black,

    background = DarkGray,
    onBackground = White,

    surface = DarkGray,
    onSurface = White
)

@Composable
fun SleepBandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
