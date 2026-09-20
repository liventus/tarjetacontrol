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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.MovimientoContable
import com.dabeliz.card.model.MovimientosDeEjemplo
import com.dabeliz.card.model.TipoMovimiento
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContableScreen(
    movimientos: List<MovimientoContable> = MovimientosDeEjemplo.lista,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val balance = movimientos.sumOf {
        if (it.tipo == TipoMovimiento.INGRESO) it.monto else -it.monto
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Área Contable") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Balance actual", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = formatoMoneda(balance),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (balance >= 0) Color(0xFF43A047) else Color(0xFFE53935)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movimientos, key = { it.id }) { movimiento ->
                    MovimientoItem(movimiento)
                }
            }
        }
    }
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
