package com.dabeliz.card.ui.inventario

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.dabeliz.card.model.CategoriaInventario
import com.dabeliz.card.model.InventarioDeEjemplo
import com.dabeliz.card.model.ItemInventario
import com.dabeliz.card.ui.common.DabelizBadge
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    items: List<ItemInventario> = InventarioDeEjemplo.lista,
    onBack: () -> Unit = {},
    onNuevoItem: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val grupos = CategoriaInventario.entries
        .mapNotNull { categoria ->
            val itemsCategoria = items.filter { it.categoria == categoria }
            if (itemsCategoria.isEmpty()) null else categoria to itemsCategoria
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Área Inventario", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoItem,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar nuevo material")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            grupos.forEach { (categoria, itemsCategoria) ->
                item(key = "encabezado_${categoria.name}") {
                    Text(
                        text = categoria.etiqueta.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                items(itemsCategoria, key = { it.id }) { item ->
                    ItemInventarioRow(item)
                }
            }
        }
    }
}

@Composable
private fun ItemInventarioRow(item: ItemInventario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.nombre, style = MaterialTheme.typography.titleMedium)
                EstadoStockBadge(stockBajo = item.stockBajo)
            }
            Text(
                text = "Cantidad: ${item.cantidad} ${item.unidad}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "Stock mínimo: ${item.stockMinimo} ${item.unidad}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun EstadoStockBadge(stockBajo: Boolean) {
    val color = if (stockBajo) Color(0xFFE53935) else Color(0xFF43A047)
    DabelizBadge(texto = if (stockBajo) "Stock bajo" else "Normal", color = color)
}

@Preview(showBackground = true)
@Composable
fun InventarioScreenPreview() {
    TarjetaconotrolTheme {
        InventarioScreen()
    }
}
