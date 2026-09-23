package com.dabeliz.card.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Tabla de celdas para elegir tallas puntuales (no necesariamente consecutivas):
 * un modelo puede venir del 25 al 32, otro del 32 al 43, otro en una sola talla, etc.
 * Tocar una celda la agrega o la quita del conjunto seleccionado.
 */
@Composable
fun SelectorTallas(
    tallasSeleccionadas: Set<Int>,
    onToggleTalla: (Int) -> Unit,
    modifier: Modifier = Modifier,
    rango: IntRange = 12..46
) {
    Column(modifier = modifier) {
        Text(
            text = if (tallasSeleccionadas.isEmpty()) {
                "Ninguna talla seleccionada"
            } else {
                "Tallas: " + tallasSeleccionadas.sorted().joinToString(", ")
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(10.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rango.forEach { talla ->
                    CeldaTalla(
                        talla = talla,
                        seleccionada = talla in tallasSeleccionadas,
                        onClick = { onToggleTalla(talla) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CeldaTalla(talla: Int, seleccionada: Boolean, onClick: () -> Unit) {
    val fondo = if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contenido = if (seleccionada) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = talla.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal,
            color = contenido
        )
    }
}
