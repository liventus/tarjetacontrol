package com.dabeliz.card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dabeliz.card.model.CategoriaInventario
import com.dabeliz.card.model.HormasDeEjemplo
import com.dabeliz.card.model.InventarioDeEjemplo
import com.dabeliz.card.model.ItemInventario
import com.dabeliz.card.model.ModelosDeEjemplo
import com.dabeliz.card.model.MovimientosDeEjemplo
import com.dabeliz.card.model.PedidosDeEjemplo
import com.dabeliz.card.ui.contable.ContableScreen
import com.dabeliz.card.ui.contable.NuevoMovimientoScreen
import com.dabeliz.card.ui.hormas.DetalleHormaScreen
import com.dabeliz.card.ui.hormas.EditarHormaScreen
import com.dabeliz.card.ui.hormas.HormasScreen
import com.dabeliz.card.ui.hormas.NuevaHormaScreen
import com.dabeliz.card.ui.inventario.DetalleItemInventarioScreen
import com.dabeliz.card.ui.inventario.EditarItemInventarioScreen
import com.dabeliz.card.ui.inventario.InventarioScreen
import com.dabeliz.card.ui.inventario.NuevaHormaInventarioScreen
import com.dabeliz.card.ui.inventario.NuevoItemInventarioScreen
import com.dabeliz.card.ui.login.LoginScreen
import com.dabeliz.card.ui.menu.MenuScreen
import com.dabeliz.card.ui.modelos.DetalleModeloScreen
import com.dabeliz.card.ui.modelos.EditarModeloScreen
import com.dabeliz.card.ui.modelos.ModelosScreen
import com.dabeliz.card.ui.modelos.NuevoModeloScreen
import com.dabeliz.card.ui.produccion.ProduccionScreen
import com.dabeliz.card.ui.tarjetas.NuevoPedidoScreen
import com.dabeliz.card.ui.tarjetas.TarjetasScreen
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

private const val RUTA_LOGIN = "login"
private const val RUTA_MENU = "menu"
private const val RUTA_CONTABLE = "contable"
private const val RUTA_CONTABLE_NUEVO = "contable/nuevo"
private const val RUTA_MODELOS = "modelos"
private const val RUTA_MODELOS_NUEVO = "modelos/nuevo"
private const val RUTA_MODELOS_DETALLE = "modelos/detalle"
private const val RUTA_MODELOS_EDITAR = "modelos/editar"
private const val RUTA_HORMAS = "hormas"
private const val RUTA_HORMAS_NUEVA = "hormas/nueva"
private const val RUTA_HORMAS_DETALLE = "hormas/detalle"
private const val RUTA_HORMAS_EDITAR = "hormas/editar"
private const val RUTA_INVENTARIO = "inventario"
private const val RUTA_INVENTARIO_NUEVO = "inventario/nuevo"
private const val RUTA_INVENTARIO_DETALLE = "inventario/detalle"
private const val RUTA_INVENTARIO_EDITAR = "inventario/editar"
private const val RUTA_INVENTARIO_HORMA_NUEVA = "inventario/horma-nueva"
private const val RUTA_PEDIDOS = "pedidos"
private const val RUTA_PEDIDOS_NUEVO = "pedidos/nuevo"
private const val RUTA_PRODUCCION = "produccion"

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
    val hormas = remember { mutableStateListOf(*HormasDeEjemplo.lista.toTypedArray()) }
    val inventario = remember { mutableStateListOf(*InventarioDeEjemplo.lista.toTypedArray()) }
    val pedidos = remember { mutableStateListOf(*PedidosDeEjemplo.lista.toTypedArray()) }
    val movimientos = remember { mutableStateListOf(*MovimientosDeEjemplo.lista.toTypedArray()) }
    var hormaSeleccionadaId by remember { mutableStateOf<Int?>(null) }
    var modeloSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var itemInventarioSeleccionadoId by remember { mutableStateOf<Int?>(null) }

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
            ContableScreen(
                pedidos = pedidos,
                movimientos = movimientos,
                onBack = { navController.popBackStack() },
                onNuevoMovimiento = { navController.navigate(RUTA_CONTABLE_NUEVO) }
            )
        }
        composable(RUTA_CONTABLE_NUEVO) {
            NuevoMovimientoScreen(
                siguienteId = (movimientos.maxOfOrNull { it.id } ?: 0) + 1,
                onGuardar = { nuevoMovimiento ->
                    movimientos.add(nuevoMovimiento)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(RUTA_MODELOS) {
            ModelosScreen(
                modelos = modelos,
                hormas = hormas,
                onBack = { navController.popBackStack() },
                onNuevoModelo = { navController.navigate(RUTA_MODELOS_NUEVO) },
                onSeleccionarModelo = { modelo ->
                    modeloSeleccionadoId = modelo.id
                    navController.navigate(RUTA_MODELOS_DETALLE)
                }
            )
        }
        composable(RUTA_MODELOS_NUEVO) {
            NuevoModeloScreen(
                siguienteId = (modelos.maxOfOrNull { it.id } ?: 0) + 1,
                hormasDisponibles = hormas,
                onGuardar = { nuevoModelo ->
                    modelos.add(nuevoModelo)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(RUTA_MODELOS_DETALLE) {
            val modelo = modelos.firstOrNull { it.id == modeloSeleccionadoId }
            if (modelo != null) {
                DetalleModeloScreen(
                    modelo = modelo,
                    hormasDisponibles = hormas,
                    onBack = { navController.popBackStack() },
                    onEditar = { navController.navigate(RUTA_MODELOS_EDITAR) }
                )
            }
        }
        composable(RUTA_MODELOS_EDITAR) {
            val modelo = modelos.firstOrNull { it.id == modeloSeleccionadoId }
            if (modelo != null) {
                EditarModeloScreen(
                    modelo = modelo,
                    hormasDisponibles = hormas,
                    onGuardar = { modeloEditado ->
                        val indice = modelos.indexOfFirst { it.id == modeloEditado.id }
                        if (indice != -1) {
                            modelos[indice] = modeloEditado
                        }
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(RUTA_HORMAS) {
            HormasScreen(
                hormas = hormas,
                onBack = { navController.popBackStack() },
                onNuevaHorma = { navController.navigate(RUTA_HORMAS_NUEVA) },
                onSeleccionarHorma = { horma ->
                    hormaSeleccionadaId = horma.id
                    navController.navigate(RUTA_HORMAS_DETALLE)
                }
            )
        }
        composable(RUTA_HORMAS_NUEVA) {
            NuevaHormaScreen(
                siguienteId = (hormas.maxOfOrNull { it.id } ?: 0) + 1,
                onGuardar = { nuevaHorma ->
                    hormas.add(nuevaHorma)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(RUTA_HORMAS_DETALLE) {
            val horma = hormas.firstOrNull { it.id == hormaSeleccionadaId }
            if (horma != null) {
                DetalleHormaScreen(
                    horma = horma,
                    onBack = { navController.popBackStack() },
                    onEditar = { navController.navigate(RUTA_HORMAS_EDITAR) }
                )
            }
        }
        composable(RUTA_HORMAS_EDITAR) {
            val horma = hormas.firstOrNull { it.id == hormaSeleccionadaId }
            if (horma != null) {
                EditarHormaScreen(
                    horma = horma,
                    onGuardar = { hormaEditada ->
                        val indice = hormas.indexOfFirst { it.id == hormaEditada.id }
                        if (indice != -1) {
                            hormas[indice] = hormaEditada
                        }
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(RUTA_INVENTARIO) {
            InventarioScreen(
                items = inventario,
                onBack = { navController.popBackStack() },
                onNuevoItem = { navController.navigate(RUTA_INVENTARIO_NUEVO) },
                onAgregarHorma = { navController.navigate(RUTA_INVENTARIO_HORMA_NUEVA) },
                onSeleccionarItem = { item ->
                    itemInventarioSeleccionadoId = item.id
                    navController.navigate(RUTA_INVENTARIO_DETALLE)
                }
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
        composable(RUTA_INVENTARIO_DETALLE) {
            val item = inventario.firstOrNull { it.id == itemInventarioSeleccionadoId }
            if (item != null) {
                DetalleItemInventarioScreen(
                    item = item,
                    horma = hormas.firstOrNull { it.id == item.hormaId },
                    onBack = { navController.popBackStack() },
                    onEditar = { navController.navigate(RUTA_INVENTARIO_EDITAR) },
                    onReponerStock = { cantidadAgregada ->
                        val indice = inventario.indexOfFirst { it.id == item.id }
                        if (indice != -1) {
                            inventario[indice] = inventario[indice].copy(cantidad = inventario[indice].cantidad + cantidadAgregada)
                        }
                    }
                )
            }
        }
        composable(RUTA_INVENTARIO_EDITAR) {
            val item = inventario.firstOrNull { it.id == itemInventarioSeleccionadoId }
            if (item != null) {
                EditarItemInventarioScreen(
                    item = item,
                    onGuardar = { itemEditado ->
                        val indice = inventario.indexOfFirst { it.id == itemEditado.id }
                        if (indice != -1) {
                            inventario[indice] = itemEditado
                        }
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(RUTA_INVENTARIO_HORMA_NUEVA) {
            NuevaHormaInventarioScreen(
                hormasDisponibles = hormas,
                onGuardar = { hormaId, cantidad, costo, stockMinimo ->
                    val indiceExistente = inventario.indexOfFirst { it.hormaId == hormaId }
                    if (indiceExistente != -1) {
                        val actual = inventario[indiceExistente]
                        inventario[indiceExistente] = actual.copy(
                            cantidad = actual.cantidad + cantidad,
                            costo = costo
                        )
                    } else {
                        val horma = hormas.first { it.id == hormaId }
                        inventario.add(
                            ItemInventario(
                                id = (inventario.maxOfOrNull { it.id } ?: 0) + 1,
                                nombre = horma.nombre,
                                categoria = CategoriaInventario.HORMAS,
                                cantidad = cantidad,
                                unidad = "pares",
                                stockMinimo = stockMinimo,
                                costo = costo,
                                hormaId = hormaId
                            )
                        )
                    }
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
                onCambiarEstado = { id, nuevoEstado ->
                    val indice = pedidos.indexOfFirst { it.id == id }
                    if (indice != -1) {
                        pedidos[indice] = pedidos[indice].copy(estado = nuevoEstado)
                    }
                },
                onActualizarPago = { id, estadoPago, monto ->
                    val indice = pedidos.indexOfFirst { it.id == id }
                    if (indice != -1) {
                        pedidos[indice] = pedidos[indice].copy(estadoPago = estadoPago, montoPagado = monto)
                    }
                }
            )
        }
        composable(RUTA_PEDIDOS_NUEVO) {
            NuevoPedidoScreen(
                siguienteId = (pedidos.maxOfOrNull { it.id } ?: 0) + 1,
                modelosDisponibles = modelos,
                hormasDisponibles = hormas,
                onGuardar = { nuevoPedido ->
                    pedidos.add(nuevoPedido)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(RUTA_PRODUCCION) {
            ProduccionScreen(
                pedidos = pedidos,
                modelos = modelos,
                hormas = hormas,
                onBack = { navController.popBackStack() },
                onCambiarEstadoLinea = { pedidoId, indiceLinea, nuevoEstado ->
                    val indicePedido = pedidos.indexOfFirst { it.id == pedidoId }
                    if (indicePedido != -1) {
                        val pedido = pedidos[indicePedido]
                        if (indiceLinea in pedido.lineas.indices) {
                            val nuevasLineas = pedido.lineas.toMutableList()
                            nuevasLineas[indiceLinea] = nuevasLineas[indiceLinea].copy(estadoProduccion = nuevoEstado)
                            pedidos[indicePedido] = pedido.copy(lineas = nuevasLineas)
                        }
                    }
                }
            )
        }
    }
}
