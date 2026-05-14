package com.farmmanager.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF052E08),
    secondary = Color(0xFFFF8F00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECB3),
    tertiary = Color(0xFF00897B),
    background = Color(0xFFFFF8E1),
    surface = Color(0xFFFFFBF2),
    surfaceVariant = Color(0xFFE8F5E9),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF003A06),
    primaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFFFFCC80),
    tertiary = Color(0xFF80CBC4),
    background = Color(0xFF10180F),
    surface = Color(0xFF182317),
    surfaceVariant = Color(0xFF243424),
)

@Composable
fun FarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
