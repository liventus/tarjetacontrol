package com.dabeliz.card.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay
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

private enum class MetodoAcceso { GMAIL, HUELLA }

@Composable
private fun TarjetaAcceso(onLoginSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()
    var metodoEnCurso by remember { mutableStateOf<MetodoAcceso?>(null) }

    fun iniciarSesionSimulada(metodo: MetodoAcceso) {
        if (metodoEnCurso != null) return
        metodoEnCurso = metodo
        scope.launch {
            delay(900)
            onLoginSuccess()
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
                text = "Acceso simulado: todavía no está conectado a una cuenta real.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )

            OutlinedButton(
                onClick = { iniciarSesionSimulada(MetodoAcceso.GMAIL) },
                enabled = metodoEnCurso == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                if (metodoEnCurso == MetodoAcceso.GMAIL) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Conectando con Gmail…")
                } else {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text("Continuar con Gmail")
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "o",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Text(
                text = "Ingresa con tu huella digital",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        if (metodoEnCurso == MetodoAcceso.HUELLA) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable(enabled = metodoEnCurso == null) { iniciarSesionSimulada(MetodoAcceso.HUELLA) },
                contentAlignment = Alignment.Center
            ) {
                if (metodoEnCurso == MetodoAcceso.HUELLA) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Ingresar con huella digital",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Text(
                text = "Simulado — todavía no usa el lector real",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
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
