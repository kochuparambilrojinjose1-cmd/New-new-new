package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = DenseBackgroundDark,
    primaryContainer = Color(0xFF003F4A),
    onPrimaryContainer = Color(0xFFB5F5FF),
    secondary = AmberConcert,
    onSecondary = DenseBackgroundDark,
    secondaryContainer = Color(0xFF452B00),
    onSecondaryContainer = Color(0xFFFFE599),
    tertiary = LaserGreen,
    onTertiary = DenseBackgroundDark,
    tertiaryContainer = Color(0xFF003822),
    onTertiaryContainer = Color(0xFFA3FFCE),
    error = CrimsonAlert,
    onError = Color.White,
    errorContainer = Color(0xFF4C0B11),
    onErrorContainer = Color(0xFFFFD1D6),
    background = DenseBackgroundDark,
    onBackground = DenseTextPrimaryDark,
    surface = DenseSurfaceDark,
    onSurface = DenseTextPrimaryDark,
    surfaceVariant = DenseSurfaceElevatedDark,
    onSurfaceVariant = DenseTextSecondaryDark,
    outline = DenseBorderDark,
    outlineVariant = DenseBorderSubtleDark
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001B40),
    secondary = Color(0xFFB45309),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF451A03),
    tertiary = Color(0xFF047857),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF064E3B),
    error = Color(0xFFDC2626),
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = DenseBackgroundLight,
    onBackground = DenseTextPrimaryLight,
    surface = DenseSurfaceLight,
    onSurface = DenseTextPrimaryLight,
    surfaceVariant = DenseSurfaceElevatedLight,
    onSurfaceVariant = DenseTextSecondaryLight,
    outline = DenseBorderLight,
    outlineVariant = DenseBorderSubtleLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark console theme for AVL high-density workstation
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

