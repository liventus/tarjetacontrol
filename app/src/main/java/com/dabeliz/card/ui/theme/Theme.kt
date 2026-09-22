package com.dabeliz.card.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DabelizGoldLight,
    onPrimary = DabelizNavyDeep,
    secondary = DabelizGold,
    onSecondary = DabelizNavyDeep,
    tertiary = DabelizCream,
    background = DabelizNavyDeep,
    surface = DabelizNavySurface
)

private val LightColorScheme = lightColorScheme(
    primary = DabelizNavy,
    onPrimary = Color.White,
    secondary = DabelizGold,
    onSecondary = DabelizNavy,
    tertiary = DabelizGoldLight,
    background = DabelizCream,
    surface = Color.White
)

@Composable
fun TarjetaconotrolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // La identidad de marca de Dabeliz (azul marino + dorado) debe verse
    // siempre igual, así que no usamos los colores dinámicos del wallpaper.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = DabelizShapes,
        content = content
    )
}
