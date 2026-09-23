package com.dabeliz.card.ui.tarjetas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.EstadoPago
import com.dabeliz.card.model.EstadoProduccion
import com.dabeliz.card.model.LineaPedido
import com.dabeliz.card.model.OrdenPedido
import com.dabeliz.card.model.PedidosDeEjemplo
import com.dabeliz.card.model.UnidadPedido
import com.dabeliz.card.ui.common.formatoMoneda
import com.dabeliz.card.ui.common.DabelizBadge
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetasScreen(
    pedidos: List<OrdenPedido> = PedidosDeEjemplo.lista,
    onBack: () -> Unit = {},
    onNuevoPedido: () -> Unit = {},
    onCambiarEstado: (Int, EstadoProduccion) -> Unit = { _, _ -> },
    onActualizarPago: (Int, EstadoPago, Double) -> Unit = { _, _, _ -> },
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
                OrdenPedidoItem(
                    pedido = pedido,
                    onCambiarEstado = { nuevoEstado -> onCambiarEstado(pedido.id, nuevoEstado) },
                    onGuardarPago = { estadoPago, monto -> onActualizarPago(pedido.id, estadoPago, monto) }
                )
            }
        }
    }
}


private fun describirSeries(linea: LineaPedido): String {
    val sufijo = if (linea.unidad == UnidadPedido.MILLAR) "millar(es)" else "pares"
    return linea.series.joinToString(", ") { "${it.cantidad} $sufijo talla ${it.talla}" }
}

@Composable
private fun OrdenPedidoItem(
    pedido: OrdenPedido,
    onCambiarEstado: (EstadoProduccion) -> Unit,
    onGuardarPago: (EstadoPago, Double) -> Unit
) {
    var mostrarDialogoPago by remember { mutableStateOf(false) }

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
                EstadoBadge(estado = pedido.estado, onCambiar = onCambiarEstado)
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
                        text = "${linea.totalPares} pares · Ingreso ${formatoMoneda(linea.ingresoTotal)} · ${linea.estadoProduccion.etiqueta}",
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

            HorizontalDivider(modifier = Modifier.padding(top = 10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Pago", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    PagoBadge(pedido.estadoPago)
                }
                if (pedido.estadoPago == EstadoPago.PAGO_INICIAL) {
                    Column {
                        Text("Saldo pendiente", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatoMoneda(pedido.saldoPendiente), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                OutlinedButton(onClick = { mostrarDialogoPago = true }) {
                    Text("Registrar pago")
                }
            }
        }
    }

    if (mostrarDialogoPago) {
        DialogoPago(
            pedido = pedido,
            onConfirmar = { estadoPago, monto ->
                onGuardarPago(estadoPago, monto)
                mostrarDialogoPago = false
            },
            onCancelar = { mostrarDialogoPago = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EstadoBadge(estado: EstadoProduccion, onCambiar: (EstadoProduccion) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    val color = when (estado) {
        EstadoProduccion.INICIAR_PRODUCCION -> Color(0xFFF9A825)
        EstadoProduccion.EN_PROCESO -> Color(0xFF1E88E5)
        EstadoProduccion.TERMINADO -> Color(0xFF43A047)
        EstadoProduccion.ENTREGADO -> DabelizGold
    }
    Box {
        DabelizBadge(
            texto = estado.etiqueta,
            color = color,
            modifier = Modifier.clickable { expandido = true }
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            EstadoProduccion.entries.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion.etiqueta) },
                    onClick = { onCambiar(opcion); expandido = false }
                )
            }
        }
    }
}

@Composable
private fun PagoBadge(estadoPago: EstadoPago) {
    val color = when (estadoPago) {
        EstadoPago.SIN_PAGO -> Color(0xFF9E9E9E)
        EstadoPago.PAGO_INICIAL -> Color(0xFF1E88E5)
        EstadoPago.PAGADO_COMPLETO -> Color(0xFF43A047)
        EstadoPago.FACTURADO -> DabelizGold
        EstadoPago.CANCELADO -> Color(0xFFE53935)
    }
    DabelizBadge(texto = estadoPago.etiqueta, color = color)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoPago(
    pedido: OrdenPedido,
    onConfirmar: (EstadoPago, Double) -> Unit,
    onCancelar: () -> Unit
) {
    var estadoSeleccionado by remember { mutableStateOf(pedido.estadoPago) }
    var montoTexto by remember {
        mutableStateOf(if (pedido.montoPagado == 0.0) "" else pedido.montoPagado.toString())
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar pago") },
        text = {
            Column {
                Text(
                    text = "Total del pedido: ${formatoMoneda(pedido.ingresoTotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EstadoPago.entries.forEach { opcion ->
                        FilterChip(
                            selected = estadoSeleccionado == opcion,
                            onClick = { estadoSeleccionado = opcion },
                            label = { Text(opcion.etiqueta) }
                        )
                    }
                }
                if (estadoSeleccionado == EstadoPago.PAGO_INICIAL) {
                    OutlinedTextField(
                        value = montoTexto,
                        onValueChange = { montoTexto = it },
                        label = { Text("Monto pagado") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val monto = when (estadoSeleccionado) {
                        EstadoPago.PAGADO_COMPLETO, EstadoPago.FACTURADO -> pedido.ingresoTotal
                        EstadoPago.PAGO_INICIAL -> montoTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
                        EstadoPago.SIN_PAGO, EstadoPago.CANCELADO -> 0.0
                    }
                    onConfirmar(estadoSeleccionado, monto)
                }
            ) {
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
fun TarjetasScreenPreview() {
    TarjetaconotrolTheme {
        TarjetasScreen()
    }
}
