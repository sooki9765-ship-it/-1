package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DeepNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = DeepNavySecondary,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldSoft,
    onSecondaryContainer = DeepNavyPrimary,
    tertiary = WoodAccent,
    background = WarmCreamBackground,
    onBackground = TextPrimaryDark,
    surface = WarmCardSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = WarmSurfaceVariant,
    onSurfaceVariant = TextSecondarySlate,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldHighlight,
    onPrimary = DeepNavyPrimary,
    primaryContainer = DeepNavySecondary,
    onPrimaryContainer = WarmCreamBackground,
    secondary = GoldAccent,
    onSecondary = DeepNavyPrimary,
    background = DeepNavyPrimary,
    onBackground = WarmCreamBackground,
    surface = DeepNavySecondary,
    onSurface = WarmCreamBackground,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

@Composable
fun ClassReadingJournalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
