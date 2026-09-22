package com.dabeliz.card.ui.tarjetas

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.EstadoProduccion
import com.dabeliz.card.model.ModeloCalzado
import com.dabeliz.card.model.ModelosDeEjemplo
import com.dabeliz.card.model.OrdenPedido
import com.dabeliz.card.model.LineaPedido
import com.dabeliz.card.model.SerieTalla
import com.dabeliz.card.model.UnidadPedido
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import java.util.Locale

private class SerieEditState(talla: String = "", cantidad: String = "") {
    var talla by mutableStateOf(talla)
    var cantidad by mutableStateOf(cantidad)
}

private class LineaEditState(modeloId: Int?, unidad: UnidadPedido = UnidadPedido.PAR) {
    var modeloId by mutableStateOf(modeloId)
    var unidad by mutableStateOf(unidad)
    val series = mutableStateListOf(SerieEditState())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPedidoScreen(
    siguienteId: Int,
    modelosDisponibles: List<ModeloCalzado> = ModelosDeEjemplo.lista,
    onGuardar: (OrdenPedido) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var cliente by rememberSaveable { mutableStateOf("") }
    var fechaEntrega by rememberSaveable { mutableStateOf("") }
    var estado by rememberSaveable { mutableStateOf(EstadoProduccion.PENDIENTE) }
    var estadoExpandido by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    val lineas = remember {
        mutableStateListOf<LineaEditState>().apply {
            if (modelosDisponibles.isNotEmpty()) add(LineaEditState(modeloId = modelosDisponibles.first().id))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Nuevo pedido", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = cliente,
                onValueChange = { cliente = it; errorMensaje = null },
                label = { Text("Cliente") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = fechaEntrega,
                onValueChange = { fechaEntrega = it; errorMensaje = null },
                label = { Text("Fecha de entrega (DD/MM/AAAA)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = estadoExpandido,
                onExpandedChange = { estadoExpandido = it },
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                OutlinedTextField(
                    value = estado.etiqueta,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(
                    expanded = estadoExpandido,
                    onDismissRequest = { estadoExpandido = false }
                ) {
                    EstadoProduccion.entries.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.etiqueta) },
                            onClick = { estado = opcion; estadoExpandido = false }
                        )
                    }
                }
            }

            Text(
                text = "Modelos del pedido",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (modelosDisponibles.isEmpty()) {
                Text(
                    text = "Primero registra al menos un modelo en Área Modelos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            lineas.forEachIndexed { index, linea ->
                LineaPedidoEditor(
                    linea = linea,
                    modelosDisponibles = modelosDisponibles,
                    puedeQuitar = lineas.size > 1,
                    onQuitar = { lineas.removeAt(index) },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            OutlinedButton(
                onClick = { lineas.add(LineaEditState(modeloId = modelosDisponibles.firstOrNull()?.id)) },
                enabled = modelosDisponibles.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text("+ Agregar otro modelo a este pedido")
            }

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
                    val lineasFinal = mutableListOf<LineaPedido>()
                    var errorLinea: String? = null
                    for (linea in lineas) {
                        val modelo = modelosDisponibles.firstOrNull { it.id == linea.modeloId }
                        if (modelo == null) {
                            errorLinea = "Selecciona un modelo en cada línea del pedido"
                            break
                        }
                        val series = linea.series.mapNotNull { serie ->
                            val talla = serie.talla.toIntOrNull()
                            val cantidad = serie.cantidad.toIntOrNull()
                            if (talla != null && cantidad != null && cantidad > 0) SerieTalla(talla, cantidad) else null
                        }
                        if (series.isEmpty()) {
                            errorLinea = "Agrega al menos una talla con cantidad válida para ${modelo.nombre}"
                            break
                        }
                        lineasFinal.add(
                            LineaPedido(
                                modeloId = modelo.id,
                                nombreModelo = modelo.nombre,
                                unidad = linea.unidad,
                                series = series,
                                costoUnitario = modelo.costo,
                                precioVentaUnitario = modelo.precioVenta
                            )
                        )
                    }
                    when {
                        cliente.isBlank() -> errorMensaje = "Ingresa el cliente"
                        fechaEntrega.isBlank() -> errorMensaje = "Ingresa la fecha de entrega"
                        errorLinea != null -> errorMensaje = errorLinea
                        lineasFinal.isEmpty() -> errorMensaje = "Agrega al menos un modelo al pedido"
                        else -> onGuardar(
                            OrdenPedido(
                                id = siguienteId,
                                cliente = cliente,
                                fechaEntrega = fechaEntrega,
                                estado = estado,
                                facturado = false,
                                lineas = lineasFinal
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text("Guardar pedido")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineaPedidoEditor(
    linea: LineaEditState,
    modelosDisponibles: List<ModeloCalzado>,
    puedeQuitar: Boolean,
    onQuitar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var modeloExpandido by remember { mutableStateOf(false) }
    val modeloSeleccionado = modelosDisponibles.firstOrNull { it.id == linea.modeloId }
    val totalDeclarado = linea.series.sumOf { it.cantidad.toIntOrNull() ?: 0 }
    val totalPares = if (linea.unidad == UnidadPedido.MILLAR) totalDeclarado * 1000 else totalDeclarado

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modelo", style = MaterialTheme.typography.labelLarge)
                if (puedeQuitar) {
                    IconButton(onClick = onQuitar) {
                        Icon(Icons.Default.Close, contentDescription = "Quitar modelo del pedido")
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = modeloExpandido,
                onExpandedChange = { modeloExpandido = it },
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                OutlinedTextField(
                    value = modeloSeleccionado?.nombre ?: "Selecciona un modelo",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(
                    expanded = modeloExpandido,
                    onDismissRequest = { modeloExpandido = false }
                ) {
                    modelosDisponibles.forEach { modelo ->
                        DropdownMenuItem(
                            text = { Text(modelo.nombre) },
                            onClick = {
                                linea.modeloId = modelo.id
                                modeloExpandido = false
                            }
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UnidadPedido.entries.forEach { opcion ->
                    FilterChip(
                        selected = linea.unidad == opcion,
                        onClick = { linea.unidad = opcion },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }

            Text(
                text = "Tallas y cantidades",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            linea.series.forEachIndexed { index, serie ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = serie.talla,
                        onValueChange = { serie.talla = it },
                        label = { Text("Talla") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = serie.cantidad,
                        onValueChange = { serie.cantidad = it },
                        label = { Text(if (linea.unidad == UnidadPedido.MILLAR) "Millares" else "Pares") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    if (linea.series.size > 1) {
                        IconButton(onClick = { linea.series.removeAt(index) }) {
                            Icon(Icons.Default.Close, contentDescription = "Quitar talla")
                        }
                    }
                }
            }

            TextButton(onClick = { linea.series.add(SerieEditState()) }) {
                Text("+ Agregar talla")
            }

            if (modeloSeleccionado != null && totalPares > 0) {
                Text(
                    text = "$totalPares pares · Ingreso estimado ${
                        String.format(Locale.getDefault(), "$%,.2f", totalPares * modeloSeleccionado.precioVenta)
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NuevoPedidoScreenPreview() {
    TarjetaconotrolTheme {
        NuevoPedidoScreen(siguienteId = 1, onGuardar = {}, onBack = {})
    }
}
