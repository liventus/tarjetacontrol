package com.dabeliz.card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dabeliz.card.ui.contable.ContableScreen
import com.dabeliz.card.ui.inventario.InventarioScreen
import com.dabeliz.card.ui.login.LoginScreen
import com.dabeliz.card.ui.menu.MenuScreen
import com.dabeliz.card.ui.modelos.ModelosScreen
import com.dabeliz.card.ui.tarjetas.TarjetasScreen
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

private const val RUTA_LOGIN = "login"
private const val RUTA_MENU = "menu"
private const val RUTA_CONTABLE = "contable"
private const val RUTA_MODELOS = "modelos"
private const val RUTA_INVENTARIO = "inventario"
private const val RUTA_PEDIDOS = "pedidos"

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
            ContableScreen(onBack = { navController.popBackStack() })
        }
        composable(RUTA_MODELOS) {
            ModelosScreen(onBack = { navController.popBackStack() })
        }
        composable(RUTA_INVENTARIO) {
            InventarioScreen(onBack = { navController.popBackStack() })
        }
        composable(RUTA_PEDIDOS) {
            TarjetasScreen(onBack = { navController.popBackStack() })
        }
    }
}
