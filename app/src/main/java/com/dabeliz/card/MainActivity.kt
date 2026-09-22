package com.dabeliz.card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dabeliz.card.model.InventarioDeEjemplo
import com.dabeliz.card.model.ModelosDeEjemplo
import com.dabeliz.card.model.PedidosDeEjemplo
import com.dabeliz.card.ui.contable.ContableScreen
import com.dabeliz.card.ui.inventario.InventarioScreen
import com.dabeliz.card.ui.inventario.NuevoItemInventarioScreen
import com.dabeliz.card.ui.login.LoginScreen
import com.dabeliz.card.ui.menu.MenuScreen
import com.dabeliz.card.ui.modelos.ModelosScreen
import com.dabeliz.card.ui.modelos.NuevoModeloScreen
import com.dabeliz.card.ui.tarjetas.NuevoPedidoScreen
import com.dabeliz.card.ui.tarjetas.TarjetasScreen
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

private const val RUTA_LOGIN = "login"
private const val RUTA_MENU = "menu"
private const val RUTA_CONTABLE = "contable"
private const val RUTA_MODELOS = "modelos"
private const val RUTA_MODELOS_NUEVO = "modelos/nuevo"
private const val RUTA_INVENTARIO = "inventario"
private const val RUTA_INVENTARIO_NUEVO = "inventario/nuevo"
private const val RUTA_PEDIDOS = "pedidos"
private const val RUTA_PEDIDOS_NUEVO = "pedidos/nuevo"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TarjetaconotrolTheme {
                TarjetaControlApp()
            }
        }
    }
}

@Composable
fun TarjetaControlApp() {
    val navController = rememberNavController()
    val modelos = remember { mutableStateListOf(*ModelosDeEjemplo.lista.toTypedArray()) }
    val inventario = remember { mutableStateListOf(*InventarioDeEjemplo.lista.toTypedArray()) }
    val pedidos = remember { mutableStateListOf(*PedidosDeEjemplo.lista.toTypedArray()) }

    NavHost(navController = navController, startDestination = RUTA_LOGIN) {
        composable(RUTA_LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(RUTA_MENU) {
                        popUpTo(RUTA_LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(RUTA_MENU) {
            MenuScreen(
                onAreaSeleccionada = { ruta -> navController.navigate(ruta) },
                onCerrarSesion = {
                    navController.navigate(RUTA_LOGIN) {
                        popUpTo(RUTA_MENU) { inclusive = true }
                    }
                }
            )
        }
        composable(RUTA_CONTABLE) {
            ContableScreen(pedidos = pedidos, onBack = { navController.popBackStack() })
        }
        composable(RUTA_MODELOS) {
            ModelosScreen(
                modelos = modelos,
                onBack = { navController.popBackStack() },
                onNuevoModelo = { navController.navigate(RUTA_MODELOS_NUEVO) }
            )
        }
        composable(RUTA_MODELOS_NUEVO) {
            NuevoModeloScreen(
                siguienteId = (modelos.maxOfOrNull { it.id } ?: 0) + 1,
                onGuardar = { nuevoModelo ->
                    modelos.add(nuevoModelo)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(RUTA_INVENTARIO) {
            InventarioScreen(
                items = inventario,
                onBack = { navController.popBackStack() },
                onNuevoItem = { navController.navigate(RUTA_INVENTARIO_NUEVO) }
            )
        }
        composable(RUTA_INVENTARIO_NUEVO) {
            NuevoItemInventarioScreen(
                siguienteId = (inventario.maxOfOrNull { it.id } ?: 0) + 1,
                onGuardar = { nuevoItem ->
                    inventario.add(nuevoItem)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(RUTA_PEDIDOS) {
            TarjetasScreen(
                pedidos = pedidos,
                onBack = { navController.popBackStack() },
                onNuevoPedido = { navController.navigate(RUTA_PEDIDOS_NUEVO) },
                onFacturarPedido = { id ->
                    val indice = pedidos.indexOfFirst { it.id == id }
                    if (indice != -1) {
                        pedidos[indice] = pedidos[indice].copy(facturado = true)
                    }
                }
            )
        }
        composable(RUTA_PEDIDOS_NUEVO) {
            NuevoPedidoScreen(
                siguienteId = (pedidos.maxOfOrNull { it.id } ?: 0) + 1,
                modelosDisponibles = modelos,
                onGuardar = { nuevoPedido ->
                    pedidos.add(nuevoPedido)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
