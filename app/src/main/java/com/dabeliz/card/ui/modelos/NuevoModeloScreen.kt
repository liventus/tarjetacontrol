package com.dabeliz.card.ui.modelos

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dabeliz.card.model.ModeloCalzado
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.common.crearUriParaFoto
import com.dabeliz.card.ui.common.decodificarBitmap
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoModeloScreen(
    siguienteId: Int,
    onGuardar: (ModeloCalzado) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var nombre by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var tallas by rememberSaveable { mutableStateOf("") }
    var costoTexto by rememberSaveable { mutableStateOf("") }
    var precioVentaTexto by rememberSaveable { mutableStateOf("") }
    var imagenes by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var imagenPrincipal by rememberSaveable { mutableStateOf<Uri?>(null) }
    var uriFotoPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

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
        topBar = { DabelizTopBar(title = "Nuevo modelo", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Fotos del modelo",
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
                    FotoModeloThumbnail(
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
                label = { Text("Nombre del modelo") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it; errorMensaje = null },
                label = { Text("Categoría") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = tallas,
                onValueChange = { tallas = it; errorMensaje = null },
                label = { Text("Tallas disponibles (ej. 38 - 44)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = costoTexto,
                    onValueChange = { costoTexto = it; errorMensaje = null },
                    label = { Text("Costo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = precioVentaTexto,
                    onValueChange = { precioVentaTexto = it; errorMensaje = null },
                    label = { Text("Precio de venta") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp)
                )
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
                    val costo = costoTexto.replace(",", ".").toDoubleOrNull()
                    val precioVenta = precioVentaTexto.replace(",", ".").toDoubleOrNull()
                    when {
                        nombre.isBlank() -> errorMensaje = "Ingresa el nombre del modelo"
                        categoria.isBlank() -> errorMensaje = "Ingresa la categoría"
                        tallas.isBlank() -> errorMensaje = "Ingresa las tallas disponibles"
                        costo == null -> errorMensaje = "Ingresa un costo válido"
                        precioVenta == null -> errorMensaje = "Ingresa un precio de venta válido"
                        else -> onGuardar(
                            ModeloCalzado(
                                id = siguienteId,
                                nombre = nombre,
                                categoria = categoria,
                                tallasDisponibles = tallas,
                                costo = costo,
                                precioVenta = precioVenta,
                                imagenes = imagenes.map { it.toString() },
                                imagenPrincipal = imagenPrincipal?.toString()
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                Text("Guardar modelo")
            }
        }
    }
}

@Composable
private fun FotoModeloThumbnail(
    uri: Uri,
    esPrincipal: Boolean,
    onMarcarPrincipal: () -> Unit,
    onQuitar: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(uri) { decodificarBitmap(context, uri) }

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
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto del modelo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Photo, contentDescription = null)
                }
            }
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
fun NuevoModeloScreenPreview() {
    TarjetaconotrolTheme {
        NuevoModeloScreen(siguienteId = 1, onGuardar = {}, onBack = {})
    }
}
