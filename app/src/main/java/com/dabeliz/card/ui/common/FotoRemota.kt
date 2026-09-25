package com.dabeliz.card.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage

/**
 * Muestra una foto venga de donde venga: URL de Firebase Storage (https://) o una
 * foto local recién tomada/elegida (content://). Mientras carga muestra un spinner
 * y si falla, el ícono genérico de foto.
 */
@Composable
fun FotoRemota(
    imagen: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    colorIcono: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fondo: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    if (imagen == null) {
        MarcadorFoto(modifier, colorIcono, fondo)
        return
    }
    SubcomposeAsyncImage(
        model = imagen,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(Modifier.fillMaxSize().background(fondo), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        },
        error = { MarcadorFoto(Modifier.fillMaxSize(), colorIcono, fondo) }
    )
}

@Composable
private fun MarcadorFoto(modifier: Modifier, colorIcono: Color, fondo: Color) {
    Box(modifier = modifier.background(fondo), contentAlignment = Alignment.Center) {
        Icon(imageVector = Icons.Default.Photo, contentDescription = null, tint = colorIcono)
    }
}
