package com.dabeliz.card.ui.inventario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.dabeliz.card.model.Horma
import com.dabeliz.card.model.InventarioDeEjemplo
import com.dabeliz.card.model.ItemInventario
import com.dabeliz.card.ui.common.formatoMoneda
import com.dabeliz.card.ui.common.ContenidoCentrado
import com.dabeliz.card.ui.common.DabelizBadge
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.common.GaleriaFotos
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleItemInventarioScreen(
    item: ItemInventario,
    horma: Horma? = null,
    onBack: () -> Unit,
    onEditar: () -> Unit,
    onReponerStock: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogoReponer by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DabelizTopBar(
                title = item.nombre,
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEditar) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar material",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ContenidoCentrado(modifier = Modifier.padding(16.dp)) {
                GaleriaFotos(imagenes = item.imagenes, imagenPrincipal = item.imagenPrincipal)

                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = item.categoria.etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                HorizontalDivider()

                InfoFila("Cantidad", "${item.cantidad} ${item.unidad}")
                InfoFila("Costo por unidad", formatoMoneda(item.costo))
                InfoFila("Stock mínimo", "${item.stockMinimo} ${item.unidad}")
                if (horma != null) {
                    InfoFila("Horma vinculada", "${horma.codigo} · ${horma.nombre}")
                }

                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estado del stock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    DabelizBadge(
                        texto = if (item.stockBajo) "Stock bajo" else "Normal",
                        color = if (item.stockBajo) Color(0xFFE53935) else Color(0xFF43A047)
                    )
                }

                Button(
                    onClick = { mostrarDialogoReponer = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(" Reponer stock", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }

    if (mostrarDialogoReponer) {
        DialogoReponerStock(
            unidad = item.unidad,
            onConfirmar = { cantidad ->
                onReponerStock(cantidad)
                mostrarDialogoReponer = false
            },
            onCancelar = { mostrarDialogoReponer = false }
        )
    }
}

@Composable
private fun DialogoReponerStock(
    unidad: String,
    onConfirmar: (Int) -> Unit,
    onCancelar: () -> Unit
) {
    var texto by remember { mutableStateOf("") }
    val cantidad = texto.toIntOrNull()

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Reponer stock") },
        text = {
            Column {
                Text("¿Cuántas $unidad vas a agregar al inventario?")
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Cantidad") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { cantidad?.let(onConfirmar) },
                enabled = cantidad != null && cantidad > 0
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun InfoFila(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}


@Preview(showBackground = true)
@Composable
fun DetalleItemInventarioScreenPreview() {
    TarjetaconotrolTheme {
        DetalleItemInventarioScreen(
            item = InventarioDeEjemplo.lista.first(),
            onBack = {},
            onEditar = {},
            onReponerStock = {}
        )
    }
}
