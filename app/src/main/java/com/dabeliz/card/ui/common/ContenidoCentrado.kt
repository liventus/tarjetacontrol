package com.dabeliz.card.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Limita el ancho de formularios/contenido de lectura a algo legible y lo centra.
 * En celular (ancho menor al máximo) se comporta igual que un Column normal;
 * en tablet evita que los campos de texto y botones queden estirados de borde a borde.
 */
@Composable
fun ContenidoCentrado(
    modifier: Modifier = Modifier,
    anchoMaximo: Dp = 640.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.widthIn(max = anchoMaximo),
            content = content
        )
    }
}
