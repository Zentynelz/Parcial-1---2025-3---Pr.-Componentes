package com.f1pitstop.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores F1 - Tema oscuro elegante sin blanco
private val F1Red = Color(0xFFDC143C)           // Rojo F1 clásico
private val F1Silver = Color(0xFF8C8C8C)        // Plata oscura
private val F1Gold = Color(0xFFB8860B)          // Oro oscuro
private val F1Black = Color(0xFF0A0A0A)         // Negro profundo
private val F1DarkGray = Color(0xFF1A1A1A)      // Gris muy oscuro
private val F1Blue = Color(0xFF003D82)          // Azul oscuro
private val F1Orange = Color(0xFFCC6600)        // Naranja oscuro
private val F1Green = Color(0xFF006B54)         // Verde oscuro

// Colores de superficie y fondo - Todo oscuro
private val SurfaceLight = Color(0xFF2A2A2A)    // Gris oscuro en lugar de blanco
private val SurfaceDark = Color(0xFF121212)
private val BackgroundLight = Color(0xFF1A1A1A) // Gris muy oscuro en lugar de blanco
private val BackgroundDark = Color(0xFF000000)

private val DarkColorScheme = darkColorScheme(
    primary = F1Red,
    onPrimary = Color(0xFFE0E0E0),
    secondary = F1Silver,
    onSecondary = Color(0xFFE0E0E0),
    tertiary = F1Gold,
    onTertiary = Color(0xFFE0E0E0),
    background = BackgroundDark,
    onBackground = Color(0xFFE0E0E0),
    surface = SurfaceDark,
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF555555),
    error = Color(0xFFFF453A),
    onError = Color(0xFFE0E0E0),
    primaryContainer = Color(0xFF3A0A0A),
    onPrimaryContainer = Color(0xFFFFAAAA),
    secondaryContainer = Color(0xFF2A2A2A),
    onSecondaryContainer = Color(0xFFCCCCCC)
)

private val LightColorScheme = lightColorScheme(
    primary = F1Red,
    onPrimary = Color(0xFFE0E0E0),
    secondary = F1Blue,
    onSecondary = Color(0xFFE0E0E0),
    tertiary = F1Orange,
    onTertiary = Color(0xFFE0E0E0),
    background = BackgroundLight,
    onBackground = Color(0xFFE0E0E0),
    surface = SurfaceLight,
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2F2F2F),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF555555),
    error = Color(0xFFFF4444),
    onError = Color(0xFFE0E0E0),
    primaryContainer = Color(0xFF3A0A0A),
    onPrimaryContainer = Color(0xFFFFAAAA),
    secondaryContainer = Color(0xFF0A1A3A),
    onSecondaryContainer = Color(0xFFAABBFF)
)

@Composable
fun F1PitStopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}