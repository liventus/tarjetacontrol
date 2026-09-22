package com.dabeliz.card.ui.tarjetas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.EstadoProduccion
import com.dabeliz.card.model.LineaPedido
import com.dabeliz.card.model.OrdenPedido
import com.dabeliz.card.model.PedidosDeEjemplo
import com.dabeliz.card.model.UnidadPedido
import com.dabeliz.card.ui.common.DabelizBadge
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetasScreen(
    pedidos: List<OrdenPedido> = PedidosDeEjemplo.lista,
    onBack: () -> Unit = {},
    onNuevoPedido: () -> Unit = {},
    onFacturarPedido: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Orden de Pedido", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoPedido,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar nuevo pedido")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(pedidos, key = { it.id }) { pedido ->
                OrdenPedidoItem(pedido, onFacturar = { onFacturarPedido(pedido.id) })
            }
        }
    }
}

private fun formatoMoneda(valor: Double): String =
    String.format(Locale.getDefault(), "$%,.2f", valor)

private fun describirSeries(linea: LineaPedido): String {
    val sufijo = if (linea.unidad == UnidadPedido.MILLAR) "millar(es)" else "pares"
    return linea.series.joinToString(", ") { "${it.cantidad} $sufijo talla ${it.talla}" }
}

@Composable
private fun OrdenPedidoItem(pedido: OrdenPedido, onFacturar: () -> Unit) {
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
                EstadoBadge(pedido.estado)
            }
            Text(
                text = "Entrega: ${pedido.fechaEntrega}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            HorizontalDivider()

            pedido.lineas.forEach { linea ->
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(linea.nombreModelo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text(linea.unidad.etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = describirSeries(linea),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = "${linea.totalPares} pares · Ingreso ${formatoMoneda(linea.ingresoTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(top = 4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Gasto", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatoMoneda(pedido.costoTotal), style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Ingreso", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatoMoneda(pedido.ingresoTotal), style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Ganancia", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        formatoMoneda(pedido.gananciaTotal),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pedido.facturado) {
                    DabelizBadge(texto = "Facturado", color = DabelizGold)
                } else {
                    Button(onClick = onFacturar) {
                        Text("Marcar como facturado")
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: EstadoProduccion) {
    val color = when (estado) {
        EstadoProduccion.PENDIENTE -> Color(0xFFF9A825)
        EstadoProduccion.EN_PROCESO -> Color(0xFF1E88E5)
        EstadoProduccion.TERMINADO -> Color(0xFF43A047)
    }
    DabelizBadge(texto = estado.etiqueta, color = color)
}

@Preview(showBackground = true)
@Composable
fun TarjetasScreenPreview() {
    TarjetaconotrolTheme {
        TarjetasScreen()
    }
}
