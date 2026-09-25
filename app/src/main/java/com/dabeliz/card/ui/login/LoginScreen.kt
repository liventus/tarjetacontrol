package com.dabeliz.card.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.BuildConfig
import com.dabeliz.card.ui.common.AnchoPantalla
import com.dabeliz.card.ui.common.DabelizLogo
import com.dabeliz.card.ui.common.anchoPantallaActual
import com.dabeliz.card.ui.theme.DabelizGoldLight
import com.dabeliz.card.ui.theme.DabelizNavyDeep
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esAncho = anchoPantallaActual() != AnchoPantalla.COMPACTO

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DabelizNavyDeep)
    ) {
        if (esAncho) {
            // Tablet: logo y formulario lado a lado para no dejar la pantalla luciendo
            // "alargada" con todo apilado y centrado en medio de tanto espacio vacío.
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 900.dp)
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Marca()
                }
                Column(modifier = Modifier.weight(1f)) {
                    TarjetaAcceso(onLoginSuccess = onLoginSuccess)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Marca()
                Column(modifier = Modifier.padding(top = 32.dp)) {
                    TarjetaAcceso(onLoginSuccess = onLoginSuccess)
                }
            }
        }

        Text(
            text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun Marca() {
    DabelizLogo(
        useBadge = false,
        markSize = 96.dp,
        wordmarkColor = Color.White,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Control de Producción · Zapatos",
        style = MaterialTheme.typography.titleMedium,
        color = DabelizGoldLight,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun TarjetaAcceso(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var conectando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun procesar(resultado: ResultadoLogin, lanzarFlujoClasico: () -> Unit) {
        when (resultado) {
            is ResultadoLogin.Exito -> onLoginSuccess()
            ResultadoLogin.Cancelado -> conectando = false
            ResultadoLogin.UsarFlujoClasico -> lanzarFlujoClasico()
            is ResultadoLogin.Error -> {
                error = resultado.mensaje
                conectando = false
            }
        }
    }

    // Respaldo para dispositivos con Google Play Services antiguo (sin Credential Manager).
    val flujoClasicoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        scope.launch {
            procesar(AutenticacionGoogle.completarFlujoClasico(context, resultado.data)) {
                error = "No se pudo iniciar sesión con Google."
                conectando = false
            }
        }
    }

    fun iniciarSesion() {
        if (conectando) return
        conectando = true
        error = null
        scope.launch {
            procesar(AutenticacionGoogle.iniciarSesion(context)) {
                val intent = AutenticacionGoogle.intentFlujoClasico(context)
                if (intent != null) {
                    flujoClasicoLauncher.launch(intent)
                } else {
                    error = "Falta el Web client ID de Google."
                    conectando = false
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 420.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Inicia sesión para continuar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Text(
                text = "Usa tu cuenta de Google para ingresar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )

            OutlinedButton(
                onClick = { iniciarSesion() },
                enabled = !conectando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (conectando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Conectando con Google…")
                } else {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text("Continuar con Google")
                }
            }

            error?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TarjetaconotrolTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
