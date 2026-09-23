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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.dabeliz.card.model.Horma
import com.dabeliz.card.model.HormasDeEjemplo
import com.dabeliz.card.ui.common.ContenidoCentrado
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaHormaInventarioScreen(
    hormasDisponibles: List<Horma>,
    onGuardar: (hormaId: Int, cantidad: Int, costo: Double, stockMinimo: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hormaSeleccionadaId by rememberSaveable { mutableStateOf(hormasDisponibles.firstOrNull()?.id) }
    var hormaExpandida by remember { mutableStateOf(false) }
    var cantidadTexto by rememberSaveable { mutableStateOf("") }
    var costoTexto by rememberSaveable { mutableStateOf("") }
    var stockMinimoTexto by rememberSaveable { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val hormaSeleccionada = hormasDisponibles.firstOrNull { it.id == hormaSeleccionadaId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Agregar horma al inventario", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
          ContenidoCentrado(modifier = Modifier.padding(16.dp)) {
            if (hormasDisponibles.isEmpty()) {
                Text(
                    text = "Primero registra al menos una horma en Área Hormas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            } else {
                Text(
                    text = "Si esta horma ya está en el inventario, la cantidad se sumará a la que ya tienes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = hormaExpandida,
                    onExpandedChange = { hormaExpandida = it },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    OutlinedTextField(
                        value = hormaSeleccionada?.let { "${it.codigo} · ${it.nombre}" } ?: "Selecciona una horma",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Horma") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                    )
                    ExposedDropdownMenu(
                        expanded = hormaExpandida,
                        onDismissRequest = { hormaExpandida = false }
                    ) {
                        hormasDisponibles.forEach { horma ->
                            DropdownMenuItem(
                                text = { Text("${horma.codigo} · ${horma.nombre}") },
                                onClick = { hormaSeleccionadaId = horma.id; hormaExpandida = false }
                            )
                        }
                    }
                }

                if (hormaSeleccionada != null) {
                    Text(
                        text = "Tallas de esta horma: ${hormaSeleccionada.numerosTexto}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Row {
                    OutlinedTextField(
                        value = cantidadTexto,
                        onValueChange = { cantidadTexto = it; errorMensaje = null },
                        label = { Text("Pares que agregas") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 12.dp, end = 8.dp)
                    )
                    OutlinedTextField(
                        value = costoTexto,
                        onValueChange = { costoTexto = it; errorMensaje = null },
                        label = { Text("Costo por par") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 12.dp)
                    )
                }

                OutlinedTextField(
                    value = stockMinimoTexto,
                    onValueChange = { stockMinimoTexto = it; errorMensaje = null },
                    label = { Text("Stock mínimo (solo si es la primera vez)") },
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
                        val costo = costoTexto.replace(",", ".").toDoubleOrNull()
                        val stockMinimo = stockMinimoTexto.toIntOrNull() ?: 0
                        when {
                            hormaSeleccionada == null -> errorMensaje = "Selecciona una horma"
                            cantidad == null || cantidad <= 0 -> errorMensaje = "Ingresa cuántos pares vas a agregar"
                            costo == null -> errorMensaje = "Ingresa el costo por par"
                            else -> onGuardar(hormaSeleccionada.id, cantidad, costo, stockMinimo)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp)
                ) {
                    Text("Agregar al inventario")
                }
            }
          }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NuevaHormaInventarioScreenPreview() {
    TarjetaconotrolTheme {
        NuevaHormaInventarioScreen(hormasDisponibles = HormasDeEjemplo.lista, onGuardar = { _, _, _, _ -> }, onBack = {})
    }
}
