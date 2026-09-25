package com.dabeliz.card.ui.hormas

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.Horma
import com.dabeliz.card.model.SerieTalla
import com.dabeliz.card.model.generarCodigoHorma
import com.dabeliz.card.ui.common.ContenidoCentrado
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.common.SelectorTallas
import com.dabeliz.card.ui.common.crearUriParaFoto
import com.dabeliz.card.ui.common.FotoRemota
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun NuevaHormaScreen(
    siguienteId: Int,
    onGuardar: suspend (Horma) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    FormularioHorma(
        titulo = "Nueva horma",
        codigo = generarCodigoHorma(siguienteId),
        nombreInicial = "",
        paresPorTallaInicial = emptyMap(),
        imagenesIniciales = emptyList(),
        imagenPrincipalInicial = null,
        textoBoton = "Guardar horma",
        onGuardar = { nombre, tallas, imagenes, imagenPrincipal ->
            onGuardar(
                Horma(
                    id = siguienteId,
                    nombre = nombre,
                    tallas = tallas,
                    imagenes = imagenes,
                    imagenPrincipal = imagenPrincipal
                )
            )
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun EditarHormaScreen(
    horma: Horma,
    onGuardar: suspend (Horma) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    FormularioHorma(
        titulo = "Editar horma",
        codigo = horma.codigo,
        nombreInicial = horma.nombre,
        paresPorTallaInicial = horma.tallas.associate { it.talla to it.cantidad.toString() },
        imagenesIniciales = horma.imagenes.map { Uri.parse(it) },
        imagenPrincipalInicial = horma.imagenPrincipal?.let { Uri.parse(it) },
        textoBoton = "Guardar cambios",
        onGuardar = { nombre, tallas, imagenes, imagenPrincipal ->
            onGuardar(
                horma.copy(
                    nombre = nombre,
                    tallas = tallas,
                    imagenes = imagenes,
                    imagenPrincipal = imagenPrincipal
                )
            )
        },
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioHorma(
    titulo: String,
    codigo: String,
    nombreInicial: String,
    paresPorTallaInicial: Map<Int, String>,
    imagenesIniciales: List<Uri>,
    imagenPrincipalInicial: Uri?,
    textoBoton: String,
    onGuardar: suspend (nombre: String, tallas: List<SerieTalla>, imagenes: List<String>, imagenPrincipal: String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by rememberSaveable { mutableStateOf(nombreInicial) }
    val paresPorTalla = remember { mutableStateMapOf<Int, String>().apply { putAll(paresPorTallaInicial) } }
    var imagenes by rememberSaveable { mutableStateOf(imagenesIniciales) }
    var imagenPrincipal by rememberSaveable { mutableStateOf(imagenPrincipalInicial) }
    var uriFotoPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    fun agregarImagen(uri: Uri) {
        imagenes = imagenes + uri
        if (imagenPrincipal == null) imagenPrincipal = uri
    }

    val tomarFotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito -> if (exito) uriFotoPendiente?.let(::agregarImagen) }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val uri = crearUriParaFoto(context)
            uriFotoPendiente = uri
            tomarFotoLauncher.launch(uri)
        } else {
            errorMensaje = "Se necesita permiso de cámara para tomar la foto"
        }
    }

    val elegirImagenLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) agregarImagen(uri) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { DabelizTopBar(title = titulo, onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
          ContenidoCentrado(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Código: $codigo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }

            Text(
                text = "Fotos de la horma",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Toca la estrella para elegir la foto principal.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(imagenes) { uri ->
                    FotoHormaThumbnail(
                        uri = uri,
                        esPrincipal = uri == imagenPrincipal,
                        onMarcarPrincipal = { imagenPrincipal = uri },
                        onQuitar = {
                            imagenes = imagenes - uri
                            if (imagenPrincipal == uri) imagenPrincipal = imagenes.firstOrNull()
                        }
                    )
                }
                item {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Agregar foto",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { permisoCamaraLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tomar foto")
                }
                OutlinedButton(
                    onClick = {
                        elegirImagenLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Subir imagen")
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorMensaje = null },
                label = { Text("Nombre de la horma") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Text(
                text = "Tallas de la horma",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            SelectorTallas(
                tallasSeleccionadas = paresPorTalla.keys,
                onToggleTalla = { talla ->
                    if (paresPorTalla.containsKey(talla)) {
                        paresPorTalla.remove(talla)
                    } else {
                        paresPorTalla[talla] = ""
                    }
                    errorMensaje = null
                },
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (paresPorTalla.isNotEmpty()) {
                Text(
                    text = "Pares por talla",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                paresPorTalla.keys.sorted().forEach { talla ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Talla $talla",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(80.dp)
                        )
                        OutlinedTextField(
                            value = paresPorTalla[talla] ?: "",
                            onValueChange = { paresPorTalla[talla] = it; errorMensaje = null },
                            label = { Text("Pares") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { paresPorTalla.remove(talla) }) {
                            Icon(Icons.Default.Close, contentDescription = "Quitar talla $talla")
                        }
                    }
                }
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
                    val seriesValidas = paresPorTalla.mapNotNull { (talla, texto) ->
                        texto.toIntOrNull()?.let { cantidad -> SerieTalla(talla, cantidad) }
                    }
                    when {
                        nombre.isBlank() -> errorMensaje = "Ingresa el nombre de la horma"
                        paresPorTalla.isEmpty() -> errorMensaje = "Selecciona al menos una talla"
                        seriesValidas.size != paresPorTalla.size ->
                            errorMensaje = "Ingresa cuántos pares tienes de cada talla seleccionada"
                        else -> {
                            guardando = true
                            errorMensaje = null
                            scope.launch {
                                try {
                                    onGuardar(
                                        nombre,
                                        seriesValidas.sortedBy { it.talla },
                                        imagenes.map { it.toString() },
                                        imagenPrincipal?.toString()
                                    )
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    errorMensaje = "No se pudo guardar: ${e.message}"
                                } finally {
                                    guardando = false
                                }
                            }
                        }
                    }
                },
                enabled = !guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                if (guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Guardando…")
                } else {
                    Text(textoBoton)
                }
            }
          }
        }
    }
}

@Composable
private fun FotoHormaThumbnail(
    uri: Uri,
    esPrincipal: Boolean,
    onMarcarPrincipal: () -> Unit,
    onQuitar: () -> Unit
) {
    Box(modifier = Modifier.size(96.dp)) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (esPrincipal) 2.dp else 1.dp,
                    color = if (esPrincipal) DabelizGold else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            FotoRemota(
                imagen = uri.toString(),
                contentDescription = "Foto de la horma",
                modifier = Modifier.fillMaxSize()
            )
        }

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = if (esPrincipal) "Foto principal" else "Marcar como principal",
            tint = if (esPrincipal) DabelizGold else MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.35f))
                .clickable(onClick = onMarcarPrincipal)
                .padding(4.dp)
                .size(16.dp)
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Quitar foto",
            tint = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .clickable(onClick = onQuitar)
                .padding(3.dp)
                .size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NuevaHormaScreenPreview() {
    TarjetaconotrolTheme {
        NuevaHormaScreen(siguienteId = 1, onGuardar = {}, onBack = {})
    }
}
