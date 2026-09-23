package com.dabeliz.card.ui.contable

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.CategoriaMovimiento
import com.dabeliz.card.model.MovimientoContable
import com.dabeliz.card.ui.common.ContenidoCentrado
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formateadorFechaForm = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoScreen(
    siguienteId: Int,
    onGuardar: (MovimientoContable) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var esIngreso by rememberSaveable { mutableStateOf(false) }
    var categoria by rememberSaveable { mutableStateOf(CategoriaMovimiento.OTRO_EGRESO) }
    var categoriaExpandida by remember { mutableStateOf(false) }
    var concepto by rememberSaveable { mutableStateOf("") }
    var montoTexto by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val categoriasDisponibles = CategoriaMovimiento.entries.filter { it.esIngreso == esIngreso }

    fun abrirSelectorFecha() {
        DatePickerDialog(
            context,
            { _, anio, mesIndice, dia -> fecha = LocalDate.of(anio, mesIndice + 1, dia) },
            fecha.year,
            fecha.monthValue - 1,
            fecha.dayOfMonth
        ).show()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = "Nuevo movimiento", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
          ContenidoCentrado(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                FilterChip(
                    selected = esIngreso,
                    onClick = { esIngreso = true; categoria = CategoriaMovimiento.VENTA; errorMensaje = null },
                    label = { Text("Ingreso") }
                )
                FilterChip(
                    selected = !esIngreso,
                    onClick = { esIngreso = false; categoria = CategoriaMovimiento.OTRO_EGRESO; errorMensaje = null },
                    label = { Text("Egreso") }
                )
            }

            OutlinedTextField(
                value = concepto,
                onValueChange = { concepto = it; errorMensaje = null },
                label = { Text("Concepto") },
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
                    categoriasDisponibles.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.etiqueta) },
                            onClick = { categoria = opcion; categoriaExpandida = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = montoTexto,
                onValueChange = { montoTexto = it; errorMensaje = null },
                label = { Text("Monto") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            OutlinedButton(
                onClick = { abrirSelectorFecha() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null)
                Text("  Fecha: ${fecha.format(formateadorFechaForm)}")
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
                    val monto = montoTexto.replace(",", ".").toDoubleOrNull()
                    when {
                        concepto.isBlank() -> errorMensaje = "Ingresa el concepto del movimiento"
                        monto == null || monto <= 0 -> errorMensaje = "Ingresa un monto válido"
                        else -> onGuardar(
                            MovimientoContable(
                                id = siguienteId,
                                concepto = concepto,
                                categoria = categoria,
                                monto = monto,
                                fecha = fecha
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                Text("Guardar movimiento")
            }
          }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NuevoMovimientoScreenPreview() {
    TarjetaconotrolTheme {
        NuevoMovimientoScreen(siguienteId = 1, onGuardar = {}, onBack = {})
    }
}
