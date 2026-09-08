package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrilliantGold,
    secondary = GoldMetallic,
    tertiary = AmberGlow,
    background = RoyalPurple900,
    surface = RoyalPurple800,
    onPrimary = RoyalPurple900,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalPurple700,
    secondary = GoldMetallic,
    tertiary = BrilliantGold,
    background = BackgroundCanvas,
    surface = CardWhite,
    onPrimary = Color.White,
    onSecondary = TextDarkPrimary,
    onTertiary = RoyalPurple900,
    onBackground = TextDarkPrimary,
    onSurface = TextDarkPrimary,
    surfaceVariant = RoyalPurpleSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Enforce our branded Royal Purple & Gold theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

