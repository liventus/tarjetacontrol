package com.dabeliz.card.ui.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.CategoriaInventario
import com.dabeliz.card.model.InventarioDeEjemplo
import com.dabeliz.card.model.ItemInventario
import com.dabeliz.card.ui.common.formatoMoneda
import com.dabeliz.card.ui.common.DabelizBadge
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    items: List<ItemInventario> = InventarioDeEjemplo.lista,
    onBack: () -> Unit = {},
    onNuevoItem: () -> Unit = {},
    onAgregarHorma: () -> Unit = {},
    onSeleccionarItem: (ItemInventario) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val grupos = CategoriaInventario.entries
        .mapNotNull { categoria ->
            val itemsCategoria = items.filter { it.categoria == categoria }
            if (itemsCategoria.isEmpty()) null else categoria to itemsCategoria
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DabelizTopBar(
                title = "Área Inventario",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onAgregarHorma) {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = "Agregar horma al inventario",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
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
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { FilaEncabezado() }
                grupos.forEach { (categoria, itemsCategoria) ->
                    item(key = "categoria_${categoria.name}") {
                        FilaCategoria(categoria.etiqueta)
                    }
                    itemsIndexed(itemsCategoria, key = { _, item -> item.id }) { indice, item ->
                        FilaItem(
                            item = item,
                            fondoAlterno = indice % 2 == 1,
                            onClick = { onSeleccionarItem(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaEncabezado() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text("Material", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.weight(2f))
        Text("Cant.", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
        Text("Costo", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
        Text("Estado", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.End, modifier = Modifier.weight(1.1f))
    }
    HorizontalDivider()
}

@Composable
private fun FilaCategoria(etiqueta: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = etiqueta.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun FilaItem(item: ItemInventario, fondoAlterno: Boolean, onClick: () -> Unit) {
    val fondo = if (fondoAlterno) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f) else Color.Transparent

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(fondo)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.nombre,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "${item.cantidad} ${item.unidad}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatoMoneda(item.costo),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.weight(1.1f), contentAlignment = Alignment.CenterEnd) {
                EstadoStockBadge(stockBajo = item.stockBajo)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }
}

@Composable
private fun EstadoStockBadge(stockBajo: Boolean) {
    val color = if (stockBajo) Color(0xFFE53935) else Color(0xFF43A047)
    DabelizBadge(texto = if (stockBajo) "Bajo" else "OK", color = color)
}


@Preview(showBackground = true)
@Composable
fun InventarioScreenPreview() {
    TarjetaconotrolTheme {
        InventarioScreen()
    }
}
