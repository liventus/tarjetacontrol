package com.dabeliz.card.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

data class AreaMenu(
    val titulo: String,
    val icono: ImageVector,
    val ruta: String
)

val areasDeLaFabrica = listOf(
    AreaMenu("Área Contable", Icons.Default.AttachMoney, "contable"),
    AreaMenu("Área Modelos", Icons.Default.Checkroom, "modelos"),
    AreaMenu("Área Inventario", Icons.Default.Inventory2, "inventario"),
    AreaMenu("Orden de Pedido", Icons.Default.Assignment, "pedidos")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onAreaSeleccionada: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Fábrica de Calzado") },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Cerrar sesión")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(areasDeLaFabrica) { area ->
                AreaMenuItem(area = area, onClick = { onAreaSeleccionada(area.ruta) })
            }
        }
    }
}

@Composable
private fun AreaMenuItem(area: AreaMenu, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = area.icono,
                contentDescription = area.titulo,
                modifier = Modifier.padding(bottom = 12.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = area.titulo,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    TarjetaconotrolTheme {
        MenuScreen(onAreaSeleccionada = {}, onCerrarSesion = {})
    }
}
