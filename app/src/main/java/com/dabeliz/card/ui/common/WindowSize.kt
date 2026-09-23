package com.dabeliz.card.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Clasificación de ancho de pantalla siguiendo los breakpoints estándar de Material
 * (compacto <600dp, mediano 600-840dp, expandido >=840dp). Sirve para adaptar
 * columnas de grillas y anchos de formulario entre celular y tablet, en cualquier
 * orientación, sin depender de si el dispositivo está en vertical u horizontal.
 */
enum class AnchoPantalla { COMPACTO, MEDIANO, EXPANDIDO }

@Composable
fun anchoPantallaActual(): AnchoPantalla {
    val anchoDp = LocalConfiguration.current.screenWidthDp
    return when {
        anchoDp < 600 -> AnchoPantalla.COMPACTO
        anchoDp < 840 -> AnchoPantalla.MEDIANO
        else -> AnchoPantalla.EXPANDIDO
    }
}
