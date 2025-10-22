package com.hiddendanang.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.hiddendanang.app.utils.constants.AppThemeMode

// Light
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    error = LightError,
    onError = LightOnError,
)

// Dark
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    error = DarkError,
    onError = DarkOnError
)

// Sunset
private val SunsetColorScheme = lightColorScheme(
    primary = SunsetPrimary,
    onPrimary = SunsetOnPrimary,
    secondary = SunsetSecondary,
    onSecondary = SunsetOnSecondary,
    background = SunsetBackground,
    onBackground = SunsetOnBackground,
    surface = SunsetSurface,
    onSurface = SunsetOnSurface,
    error = SunsetError,
    onError = SunsetOnError
)

@Composable
fun HiddenDaNangTheme(
    themeApp: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeApp) {
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.SUNSET -> SunsetColorScheme
        AppThemeMode.SYSTEM ->
            if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
