package com.dabeliz.card.ui.contable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.MovimientoContable
import com.dabeliz.card.model.MovimientosDeEjemplo
import com.dabeliz.card.model.OrdenPedido
import com.dabeliz.card.model.PedidosDeEjemplo
import com.dabeliz.card.model.TipoMovimiento
import com.dabeliz.card.ui.common.DabelizStatTile
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import java.util.Locale

@Composable
fun ContableScreen(
    pedidos: List<OrdenPedido> = PedidosDeEjemplo.lista,
    movimientos: List<MovimientoContable> = MovimientosDeEjemplo.lista,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val costoTotalProduccion = pedidos.sumOf { it.costoTotal }
    val ingresoTotalPedidos = pedidos.sumOf { it.ingresoTotal }
    val gananciaTotalPedidos = pedidos.sumOf { it.gananciaTotal }
    val gananciaFacturada = pedidos.filter { it.facturado }.sumOf { it.gananciaTotal }
    val gananciaPorFacturar = gananciaTotalPedidos - gananciaFacturada

    val balanceManual = movimientos.sumOf {
        if (it.tipo == TipoMovimiento.INGRESO) it.monto else -it.monto
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Área Contable", onBack = onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SeccionTitulo("Resumen de pedidos")
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DabelizStatTile(
                        etiqueta = "Gasto total de producción",
                        valor = formatoMoneda(costoTotalProduccion),
                        icono = Icons.AutoMirrored.Filled.TrendingDown,
                        acento = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                    DabelizStatTile(
                        etiqueta = "Ganancia total (todos los pedidos)",
                        valor = formatoMoneda(gananciaTotalPedidos),
                        icono = Icons.AutoMirrored.Filled.TrendingUp,
                        acento = Color(0xFF43A047),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DabelizStatTile(
                        etiqueta = "Ganancia facturada",
                        valor = formatoMoneda(gananciaFacturada),
                        icono = Icons.AutoMirrored.Filled.ReceiptLong,
                        acento = DabelizGold,
                        modifier = Modifier.weight(1f)
                    )
                    DabelizStatTile(
                        etiqueta = "Ganancia por facturar",
                        valor = formatoMoneda(gananciaPorFacturar),
                        icono = Icons.Default.Paid,
                        acento = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                SeccionTitulo("Movimientos contables", topPadding = 12.dp)
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Balance de caja (ingresos y egresos manuales)", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                text = formatoMoneda(balanceManual),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (balanceManual >= 0) Color(0xFF43A047) else Color(0xFFE53935)
                            )
                        }
                    }
                }
            }

            items(movimientos, key = { it.id }) { movimiento ->
                MovimientoItem(movimiento)
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = topPadding, bottom = 4.dp)
    )
}

@Composable
private fun MovimientoItem(movimiento: MovimientoContable) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val color = if (esIngreso) Color(0xFF43A047) else Color(0xFFE53935)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(movimiento.concepto, style = MaterialTheme.typography.titleMedium)
                Text(movimiento.fecha, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = (if (esIngreso) "+ " else "- ") + formatoMoneda(movimiento.monto),
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}

private fun formatoMoneda(valor: Double): String =
    String.format(Locale.getDefault(), "$%,.2f", valor)

@Preview(showBackground = true)
@Composable
fun ContableScreenPreview() {
    TarjetaconotrolTheme {
        ContableScreen()
    }
}
