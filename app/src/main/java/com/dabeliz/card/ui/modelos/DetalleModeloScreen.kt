package com.dabeliz.card.ui.modelos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.Horma
import com.dabeliz.card.model.ModeloCalzado
import com.dabeliz.card.model.ModelosDeEjemplo
import com.dabeliz.card.ui.common.formatoMoneda
import com.dabeliz.card.ui.common.ContenidoCentrado
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.common.GaleriaFotos
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleModeloScreen(
    modelo: ModeloCalzado,
    hormasDisponibles: List<Horma> = emptyList(),
    onBack: () -> Unit,
    onEditar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val horma = hormasDisponibles.firstOrNull { it.id == modelo.hormaId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DabelizTopBar(
                title = modelo.nombre,
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEditar) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar modelo",
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
                GaleriaFotos(imagenes = modelo.imagenes, imagenPrincipal = modelo.imagenPrincipal)

                Text(
                    text = modelo.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = modelo.categoria,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                HorizontalDivider()

                InfoFila("Horma", horma?.let { "${it.codigo} · ${it.nombre}" } ?: "Sin horma asignada")
                InfoFila("Tallas", horma?.numerosTexto ?: "Asigna una horma para conocer las tallas")
                InfoFila("Costo", formatoMoneda(modelo.costo))
                InfoFila("Precio de venta", formatoMoneda(modelo.precioVenta))

                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Margen de ganancia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatoMoneda(modelo.margenGanancia),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
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
fun DetalleModeloScreenPreview() {
    TarjetaconotrolTheme {
        DetalleModeloScreen(modelo = ModelosDeEjemplo.lista.first(), onBack = {}, onEditar = {})
    }
}
