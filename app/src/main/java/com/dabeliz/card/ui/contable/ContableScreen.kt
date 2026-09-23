package com.dabeliz.card.ui.contable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.dabeliz.card.model.EstadoPago
import com.dabeliz.card.model.MovimientoContable
import com.dabeliz.card.model.MovimientosDeEjemplo
import com.dabeliz.card.model.OrdenPedido
import com.dabeliz.card.model.PedidosDeEjemplo
import com.dabeliz.card.ui.common.DabelizStatTile
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.common.formatoMoneda
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class PeriodoContable(val etiqueta: String) {
    SEMANA("Semana"),
    MES("Mes"),
    ANIO("Año")
}

private fun estaEnPeriodo(fecha: LocalDate, periodo: PeriodoContable, referencia: LocalDate): Boolean = when (periodo) {
    PeriodoContable.SEMANA -> {
        val inicio = referencia.with(DayOfWeek.MONDAY)
        val fin = inicio.plusDays(6)
        !fecha.isBefore(inicio) && !fecha.isAfter(fin)
    }
    PeriodoContable.MES -> fecha.year == referencia.year && fecha.monthValue == referencia.monthValue
    PeriodoContable.ANIO -> fecha.year == referencia.year
}

private fun periodoAnterior(periodo: PeriodoContable, referencia: LocalDate): LocalDate = when (periodo) {
    PeriodoContable.SEMANA -> referencia.minusWeeks(1)
    PeriodoContable.MES -> referencia.minusMonths(1)
    PeriodoContable.ANIO -> referencia.minusYears(1)
}

private fun periodoSiguiente(periodo: PeriodoContable, referencia: LocalDate): LocalDate = when (periodo) {
    PeriodoContable.SEMANA -> referencia.plusWeeks(1)
    PeriodoContable.MES -> referencia.plusMonths(1)
    PeriodoContable.ANIO -> referencia.plusYears(1)
}

private val localePeru: Locale = Locale.forLanguageTag("es-PE")
private val formateadorSemana = DateTimeFormatter.ofPattern("dd MMM", localePeru)
private val formateadorMes = DateTimeFormatter.ofPattern("MMMM yyyy", localePeru)

private fun etiquetaPeriodo(periodo: PeriodoContable, referencia: LocalDate): String = when (periodo) {
    PeriodoContable.SEMANA -> {
        val inicio = referencia.with(DayOfWeek.MONDAY)
        val fin = inicio.plusDays(6)
        "Semana del ${inicio.format(formateadorSemana)} al ${fin.format(formateadorSemana)}"
    }
    PeriodoContable.MES -> referencia.format(formateadorMes).replaceFirstChar { it.uppercase() }
    PeriodoContable.ANIO -> referencia.year.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContableScreen(
    pedidos: List<OrdenPedido> = PedidosDeEjemplo.lista,
    movimientos: List<MovimientoContable> = MovimientosDeEjemplo.lista,
    onBack: () -> Unit = {},
    onNuevoMovimiento: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var periodo by remember { mutableStateOf(PeriodoContable.SEMANA) }
    val hoy = remember { LocalDate.now() }
    var fechaReferencia by remember { mutableStateOf(hoy) }

    val movimientosPeriodo = remember(movimientos, periodo, fechaReferencia) {
        movimientos.filter { estaEnPeriodo(it.fecha, periodo, fechaReferencia) }.sortedByDescending { it.fecha }
    }
    val ventasPeriodo = movimientosPeriodo.filter { it.esIngreso }.sumOf { it.monto }
    val gastosPeriodo = movimientosPeriodo.filter { !it.esIngreso }.sumOf { it.monto }
    val balancePeriodo = ventasPeriodo - gastosPeriodo
    val gastosPorCategoria = movimientosPeriodo
        .filter { !it.esIngreso }
        .groupBy { it.categoria }
        .map { (categoria, lista) -> categoria to lista.sumOf { it.monto } }
        .sortedByDescending { it.second }

    val costoTotalProduccion = pedidos.sumOf { it.costoTotal }
    val gananciaTotalPedidos = pedidos.sumOf { it.gananciaTotal }
    val gananciaFacturada = pedidos.filter { it.estadoPago == EstadoPago.FACTURADO }.sumOf { it.gananciaTotal }
    val gananciaPorFacturar = gananciaTotalPedidos - gananciaFacturada

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Área Contable", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoMovimiento,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar nuevo movimiento")
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
            item { SeccionTitulo("Contabilidad por período") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PeriodoContable.entries.forEach { opcion ->
                        FilterChip(
                            selected = periodo == opcion,
                            onClick = { periodo = opcion },
                            label = { Text(opcion.etiqueta) }
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { fechaReferencia = periodoAnterior(periodo, fechaReferencia) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Período anterior")
                    }
                    Text(
                        text = etiquetaPeriodo(periodo, fechaReferencia),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { fechaReferencia = periodoSiguiente(periodo, fechaReferencia) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Período siguiente")
                    }
                }
            }
            if (fechaReferencia != hoy) {
                item {
                    TextButton(onClick = { fechaReferencia = hoy }) {
                        Text("Volver a hoy")
                    }
                }
            }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DabelizStatTile(
                        etiqueta = "Vendido",
                        valor = formatoMoneda(ventasPeriodo),
                        icono = Icons.AutoMirrored.Filled.TrendingUp,
                        acento = Color(0xFF43A047),
                        modifier = Modifier.weight(1f).widthIn(min = 148.dp)
                    )
                    DabelizStatTile(
                        etiqueta = "Gastado",
                        valor = formatoMoneda(gastosPeriodo),
                        icono = Icons.AutoMirrored.Filled.TrendingDown,
                        acento = Color(0xFFE53935),
                        modifier = Modifier.weight(1f).widthIn(min = 148.dp)
                    )
                    DabelizStatTile(
                        etiqueta = "Balance del período",
                        valor = formatoMoneda(balancePeriodo),
                        icono = Icons.Default.Paid,
                        acento = if (balancePeriodo >= 0) Color(0xFF43A047) else Color(0xFFE53935),
                        modifier = Modifier.weight(1f).widthIn(min = 148.dp)
                    )
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Gasto por categoría",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        if (gastosPorCategoria.isEmpty()) {
                            Text(
                                text = "Sin gastos registrados en este período.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            gastosPorCategoria.forEach { (categoria, monto) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(categoria.etiqueta, style = MaterialTheme.typography.bodyMedium)
                                    Text(formatoMoneda(monto), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            item { SeccionTitulo("Resumen de pedidos (histórico)", topPadding = 12.dp) }
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DabelizStatTile(
                        etiqueta = "Gasto total de producción",
                        valor = formatoMoneda(costoTotalProduccion),
                        icono = Icons.AutoMirrored.Filled.TrendingDown,
                        acento = Color(0xFFE53935),
                        modifier = Modifier.weight(1f).widthIn(min = 148.dp)
                    )
                    DabelizStatTile(
                        etiqueta = "Ganancia total (todos los pedidos)",
                        valor = formatoMoneda(gananciaTotalPedidos),
                        icono = Icons.AutoMirrored.Filled.TrendingUp,
                        acento = Color(0xFF43A047),
                        modifier = Modifier.weight(1f).widthIn(min = 148.dp)
                    )
                    DabelizStatTile(
                        etiqueta = "Ganancia facturada",
                        valor = formatoMoneda(gananciaFacturada),
                        icono = Icons.AutoMirrored.Filled.ReceiptLong,
                        acento = DabelizGold,
                        modifier = Modifier.weight(1f).widthIn(min = 148.dp)
                    )
                    DabelizStatTile(
                        etiqueta = "Ganancia por facturar",
                        valor = formatoMoneda(gananciaPorFacturar),
                        icono = Icons.Default.Paid,
                        acento = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f).widthIn(min = 148.dp)
                    )
                }
            }

            item { SeccionTitulo("Movimientos del período", topPadding = 12.dp) }
            if (movimientosPeriodo.isEmpty()) {
                item {
                    Text(
                        text = "No hay movimientos registrados en este período.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        movimientosPeriodo.forEach { movimiento ->
                            MovimientoItem(movimiento, modifier = Modifier.weight(1f).widthIn(min = 280.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String, topPadding: Dp = 0.dp) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = topPadding, bottom = 4.dp)
    )
}

private val formateadorFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
private fun MovimientoItem(movimiento: MovimientoContable, modifier: Modifier = Modifier) {
    val color = if (movimiento.esIngreso) Color(0xFF43A047) else Color(0xFFE53935)

    Card(
        modifier = modifier.fillMaxWidth(),
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
                Text(
                    text = "${movimiento.categoria.etiqueta} · ${movimiento.fecha.format(formateadorFecha)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = (if (movimiento.esIngreso) "+ " else "- ") + formatoMoneda(movimiento.monto),
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContableScreenPreview() {
    TarjetaconotrolTheme {
        ContableScreen()
    }
}
