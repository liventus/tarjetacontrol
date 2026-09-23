package com.dabeliz.card.ui.produccion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.EstadoProduccionDetallado
import com.dabeliz.card.model.Horma
import com.dabeliz.card.model.HormasDeEjemplo
import com.dabeliz.card.model.LineaPedido
import com.dabeliz.card.model.ModeloCalzado
import com.dabeliz.card.model.ModelosDeEjemplo
import com.dabeliz.card.model.OrdenPedido
import com.dabeliz.card.model.PedidosDeEjemplo
import com.dabeliz.card.model.UnidadPedido
import com.dabeliz.card.ui.common.DabelizBadge
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

/**
 * Junta pedido + modelo + horma, y permite editar la etapa fabril (corte, aparado,
 * armado, acabado...) de cada línea del pedido de forma independiente.
 */
@Composable
fun ProduccionScreen(
    pedidos: List<OrdenPedido> = PedidosDeEjemplo.lista,
    modelos: List<ModeloCalzado> = ModelosDeEjemplo.lista,
    hormas: List<Horma> = HormasDeEjemplo.lista,
    onBack: () -> Unit = {},
    onCambiarEstadoLinea: (pedidoId: Int, indiceLinea: Int, nuevoEstado: EstadoProduccionDetallado) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Área de Producción", onBack = onBack) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 380.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pedidos, key = { it.id }) { pedido ->
                PedidoProduccionCard(
                    pedido = pedido,
                    modelos = modelos,
                    hormas = hormas,
                    onCambiarEstadoLinea = { indiceLinea, nuevoEstado ->
                        onCambiarEstadoLinea(pedido.id, indiceLinea, nuevoEstado)
                    }
                )
            }
        }
    }
}

@Composable
private fun PedidoProduccionCard(
    pedido: OrdenPedido,
    modelos: List<ModeloCalzado>,
    hormas: List<Horma>,
    onCambiarEstadoLinea: (indiceLinea: Int, nuevoEstado: EstadoProduccionDetallado) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(pedido.cliente, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                DabelizBadge(texto = pedido.estado.etiqueta, color = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = "Entrega: ${pedido.fechaEntrega}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            pedido.lineas.forEachIndexed { indice, linea ->
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                LineaProduccion(
                    linea = linea,
                    modelos = modelos,
                    hormas = hormas,
                    onCambiarEstado = { nuevoEstado -> onCambiarEstadoLinea(indice, nuevoEstado) }
                )
            }
        }
    }
}

@Composable
private fun LineaProduccion(
    linea: LineaPedido,
    modelos: List<ModeloCalzado>,
    hormas: List<Horma>,
    onCambiarEstado: (EstadoProduccionDetallado) -> Unit
) {
    val modelo = modelos.firstOrNull { it.id == linea.modeloId }
    val horma = hormas.firstOrNull { it.id == modelo?.hormaId }
    val sufijo = if (linea.unidad == UnidadPedido.MILLAR) "millar(es)" else "pares"

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(linea.nombreModelo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            EtapaProduccionEditable(etapaActual = linea.estadoProduccion, onCambiar = onCambiarEstado)
        }
        Text(
            text = "Pedido: " + linea.series.joinToString(", ") { "${it.cantidad} $sufijo talla ${it.talla}" },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 2.dp)
        )
        if (horma != null) {
            Text(
                text = "Horma ${horma.codigo} · disponible por talla: ${horma.tallasTexto}",
                style = MaterialTheme.typography.bodySmall,
                color = DabelizGold,
                modifier = Modifier.padding(top = 2.dp)
            )
        } else {
            Text(
                text = "Modelo sin horma asignada",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun colorEtapa(etapa: EstadoProduccionDetallado): Color = when (etapa) {
    EstadoProduccionDetallado.INICIO -> Color(0xFF9E9E9E)
    EstadoProduccionDetallado.COMPRA -> Color(0xFFF9A825)
    EstadoProduccionDetallado.CORTADO -> Color(0xFF8E24AA)
    EstadoProduccionDetallado.APARADO -> Color(0xFF1E88E5)
    EstadoProduccionDetallado.ARMADO -> Color(0xFF00897B)
    EstadoProduccionDetallado.ACABADO -> Color(0xFFFB8C00)
    EstadoProduccionDetallado.ENTREGADO -> Color(0xFF43A047)
}

/** Muestra solo la etapa actual; el lápiz abre el diálogo para cambiarla. */
@Composable
private fun EtapaProduccionEditable(
    etapaActual: EstadoProduccionDetallado,
    onCambiar: (EstadoProduccionDetallado) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        DabelizBadge(texto = etapaActual.etiqueta, color = colorEtapa(etapaActual))
        IconButton(
            onClick = { mostrarDialogo = true },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Cambiar etapa de producción",
                modifier = Modifier.size(16.dp)
            )
        }
    }

    if (mostrarDialogo) {
        DialogoEtapaProduccion(
            etapaActual = etapaActual,
            onConfirmar = { nuevaEtapa ->
                onCambiar(nuevaEtapa)
                mostrarDialogo = false
            },
            onCancelar = { mostrarDialogo = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoEtapaProduccion(
    etapaActual: EstadoProduccionDetallado,
    onConfirmar: (EstadoProduccionDetallado) -> Unit,
    onCancelar: () -> Unit
) {
    var seleccion by remember { mutableStateOf(etapaActual) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cambiar etapa de producción") },
        text = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoProduccionDetallado.entries.forEach { opcion ->
                    FilterChip(
                        selected = seleccion == opcion,
                        onClick = { seleccion = opcion },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(seleccion) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProduccionScreenPreview() {
    TarjetaconotrolTheme {
        ProduccionScreen()
    }
}
