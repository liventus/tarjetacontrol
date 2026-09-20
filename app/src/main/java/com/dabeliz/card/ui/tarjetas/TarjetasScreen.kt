package com.dabeliz.card.ui.tarjetas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.EstadoProduccion
import com.dabeliz.card.model.TarjetaProduccion
import com.dabeliz.card.model.TarjetasDeEjemplo
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetasScreen(
    tarjetas: List<TarjetaProduccion> = TarjetasDeEjemplo.lista,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Orden de Pedido") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tarjetas, key = { it.id }) { tarjeta ->
                TarjetaProduccionItem(tarjeta)
            }
        }
    }
}

@Composable
private fun TarjetaProduccionItem(tarjeta: TarjetaProduccion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = tarjeta.modelo,
                    style = MaterialTheme.typography.titleMedium
                )
                EstadoBadge(tarjeta.estado)
            }
            Text(
                text = "Talla ${tarjeta.talla} · Cantidad ${tarjeta.cantidad}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Entrega: ${tarjeta.fechaEntrega}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp)
            )
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
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = estado.etiqueta,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetasScreenPreview() {
    TarjetaconotrolTheme {
        TarjetasScreen()
    }
}
