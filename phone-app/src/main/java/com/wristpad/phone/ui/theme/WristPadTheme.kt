package com.wristpad.phone.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val WristPadLightColorScheme = lightColorScheme(
    primary = "#BB86FC",
    onPrimary = "#1E1E2F",
    secondary = "#33D9B2",
    onSecondary = "#1E1E2F",
    surface = "#1E1E2F",
    onSurface = "#F8F9FA",
    background = "#F8F9FA",
    error = "#CF6679",
    onError = "#FFFFFF",
    surfaceVariant = "#ECEFF1",
    onSurfaceVariant = "#212121",
    container = "#FFFFFF",
    onContainer = "#212121",
    tertiary = "#FFB74D",
    onTertiary = "#1E1E2F"
)

val WristPadDarkColorScheme = darkColorScheme(
    primary = "#BB86FC",
    onPrimary = "#1E1E2F",
    secondary = "#33D9B2",
    onSecondary = "#1E1E2F",
    surface = "#121212",
    onSurface = "#F8F9FA",
    background = "#121212",
    error = "#CF6679",
    onError = "#FFFFFF",
    surfaceVariant = "#1E1E2F",
    onSurfaceVariant = "#E0E0E0",
    container = "#1E1E2F",
    onContainer = "#CCCCCC",
    tertiary = "#FFB74D",
    onTertiary = "#1E1E2F"
)

@Composable
fun WristPadTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme) WristPadDarkColorScheme else WristPadLightColorScheme,
        typography = Typography(),
        content = content
    )
}

@Composable
fun isSystemInDarkTheme(): Boolean {
    val config = LocalConfiguration.current
    return config.uiMode.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}