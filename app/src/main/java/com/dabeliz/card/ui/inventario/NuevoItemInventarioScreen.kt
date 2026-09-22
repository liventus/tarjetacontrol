package com.dabeliz.card.ui.inventario

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.CategoriaInventario
import com.dabeliz.card.model.ItemInventario
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoItemInventarioScreen(
    siguienteId: Int,
    onGuardar: (ItemInventario) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf(CategoriaInventario.CUERO) }
    var categoriaExpandida by remember { mutableStateOf(false) }
    var cantidadTexto by rememberSaveable { mutableStateOf("") }
    var unidad by rememberSaveable { mutableStateOf("") }
    var stockMinimoTexto by rememberSaveable { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Nuevo material", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorMensaje = null },
                label = { Text("Nombre del material") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = categoriaExpandida,
                onExpandedChange = { categoriaExpandida = it },
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                OutlinedTextField(
                    value = categoria.etiqueta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(
                    expanded = categoriaExpandida,
                    onDismissRequest = { categoriaExpandida = false }
                ) {
                    CategoriaInventario.entries.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.etiqueta) },
                            onClick = {
                                categoria = opcion
                                categoriaExpandida = false
                            }
                        )
                    }
                }
            }

            Row {
                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { cantidadTexto = it; errorMensaje = null },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 12.dp, end = 8.dp)
                )
                OutlinedTextField(
                    value = unidad,
                    onValueChange = { unidad = it; errorMensaje = null },
                    label = { Text("Unidad (m², rollos...)") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 12.dp)
                )
            }

            OutlinedTextField(
                value = stockMinimoTexto,
                onValueChange = { stockMinimoTexto = it; errorMensaje = null },
                label = { Text("Stock mínimo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            errorMensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    val cantidad = cantidadTexto.toIntOrNull()
                    val stockMinimo = stockMinimoTexto.toIntOrNull()
                    when {
                        nombre.isBlank() -> errorMensaje = "Ingresa el nombre del material"
                        unidad.isBlank() -> errorMensaje = "Ingresa la unidad de medida"
                        cantidad == null -> errorMensaje = "Ingresa una cantidad válida"
                        stockMinimo == null -> errorMensaje = "Ingresa un stock mínimo válido"
                        else -> onGuardar(
                            ItemInventario(
                                id = siguienteId,
                                nombre = nombre,
                                categoria = categoria,
                                cantidad = cantidad,
                                unidad = unidad,
                                stockMinimo = stockMinimo
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                Text("Guardar material")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NuevoItemInventarioScreenPreview() {
    TarjetaconotrolTheme {
        NuevoItemInventarioScreen(siguienteId = 1, onGuardar = {}, onBack = {})
    }
}
