package com.dabeliz.card.ui.inventario

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
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
import com.dabeliz.card.model.CategoriaInventario
import com.dabeliz.card.model.ItemInventario
import com.dabeliz.card.ui.common.ContenidoCentrado
import com.dabeliz.card.ui.common.DabelizTopBar
import com.dabeliz.card.ui.common.crearUriParaFoto
import com.dabeliz.card.ui.common.decodificarBitmap
import com.dabeliz.card.ui.theme.DabelizGold
import com.dabeliz.card.ui.theme.TarjetaconotrolTheme

@Composable
fun NuevoItemInventarioScreen(
    siguienteId: Int,
    onGuardar: (ItemInventario) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    FormularioItemInventario(
        titulo = "Nuevo material",
        nombreInicial = "",
        categoriaInicial = CategoriaInventario.CUERO,
        cantidadInicial = "",
        unidadInicial = "",
        stockMinimoInicial = "",
        costoInicial = "",
        imagenesIniciales = emptyList(),
        imagenPrincipalInicial = null,
        textoBoton = "Guardar material",
        onGuardar = { nombre, categoria, cantidad, unidad, stockMinimo, costo, imagenes, imagenPrincipal ->
            onGuardar(
                ItemInventario(
                    id = siguienteId,
                    nombre = nombre,
                    categoria = categoria,
                    cantidad = cantidad,
                    unidad = unidad,
                    stockMinimo = stockMinimo,
                    costo = costo,
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
fun EditarItemInventarioScreen(
    item: ItemInventario,
    onGuardar: (ItemInventario) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    FormularioItemInventario(
        titulo = "Editar material",
        nombreInicial = item.nombre,
        categoriaInicial = item.categoria,
        cantidadInicial = item.cantidad.toString(),
        unidadInicial = item.unidad,
        stockMinimoInicial = item.stockMinimo.toString(),
        costoInicial = if (item.costo == 0.0) "" else item.costo.toString(),
        imagenesIniciales = item.imagenes.map { Uri.parse(it) },
        imagenPrincipalInicial = item.imagenPrincipal?.let { Uri.parse(it) },
        textoBoton = "Guardar cambios",
        onGuardar = { nombre, categoria, cantidad, unidad, stockMinimo, costo, imagenes, imagenPrincipal ->
            onGuardar(
                item.copy(
                    nombre = nombre,
                    categoria = categoria,
                    cantidad = cantidad,
                    unidad = unidad,
                    stockMinimo = stockMinimo,
                    costo = costo,
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
private fun FormularioItemInventario(
    titulo: String,
    nombreInicial: String,
    categoriaInicial: CategoriaInventario,
    cantidadInicial: String,
    unidadInicial: String,
    stockMinimoInicial: String,
    costoInicial: String,
    imagenesIniciales: List<Uri>,
    imagenPrincipalInicial: Uri?,
    textoBoton: String,
    onGuardar: (
        nombre: String,
        categoria: CategoriaInventario,
        cantidad: Int,
        unidad: String,
        stockMinimo: Int,
        costo: Double,
        imagenes: List<String>,
        imagenPrincipal: String?
    ) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var nombre by rememberSaveable { mutableStateOf(nombreInicial) }
    var categoria by rememberSaveable { mutableStateOf(categoriaInicial) }
    var categoriaExpandida by remember { mutableStateOf(false) }
    var cantidadTexto by rememberSaveable { mutableStateOf(cantidadInicial) }
    var unidad by rememberSaveable { mutableStateOf(unidadInicial) }
    var stockMinimoTexto by rememberSaveable { mutableStateOf(stockMinimoInicial) }
    var costoTexto by rememberSaveable { mutableStateOf(costoInicial) }
    var imagenes by rememberSaveable { mutableStateOf(imagenesIniciales) }
    var imagenPrincipal by rememberSaveable { mutableStateOf(imagenPrincipalInicial) }
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
        topBar = { DabelizTopBar(title = titulo, onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
          ContenidoCentrado(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Fotos (opcional)",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Útil para máquinas, cueros u otros materiales que quieras identificar visualmente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(imagenes) { uri ->
                    FotoItemThumbnail(
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
                label = { Text("Nombre del material") },
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
                    CategoriaInventario.entries.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion.etiqueta) },
                            onClick = {
                                categoria = opcion
                                categoriaExpandida = false
                            }
                        )
                    }
                }
            }

            Row {
                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = { cantidadTexto = it; errorMensaje = null },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 12.dp, end = 8.dp)
                )
                OutlinedTextField(
                    value = unidad,
                    onValueChange = { unidad = it; errorMensaje = null },
                    label = { Text("Unidad (m², rollos...)") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 12.dp)
                )
            }

            Row {
                OutlinedTextField(
                    value = costoTexto,
                    onValueChange = { costoTexto = it; errorMensaje = null },
                    label = { Text("Costo por unidad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 12.dp, end = 8.dp)
                )
                OutlinedTextField(
                    value = stockMinimoTexto,
                    onValueChange = { stockMinimoTexto = it; errorMensaje = null },
                    label = { Text("Stock mínimo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    val cantidad = cantidadTexto.toIntOrNull()
                    val stockMinimo = stockMinimoTexto.toIntOrNull()
                    val costo = if (costoTexto.isBlank()) 0.0 else costoTexto.replace(",", ".").toDoubleOrNull()
                    when {
                        nombre.isBlank() -> errorMensaje = "Ingresa el nombre del material"
                        unidad.isBlank() -> errorMensaje = "Ingresa la unidad de medida"
                        cantidad == null -> errorMensaje = "Ingresa una cantidad válida"
                        stockMinimo == null -> errorMensaje = "Ingresa un stock mínimo válido"
                        costo == null -> errorMensaje = "Ingresa un costo válido"
                        else -> onGuardar(
                            nombre,
                            categoria,
                            cantidad,
                            unidad,
                            stockMinimo,
                            costo,
                            imagenes.map { it.toString() },
                            imagenPrincipal?.toString()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
            ) {
                Text(textoBoton)
            }
          }
        }
    }
}

@Composable
private fun FotoItemThumbnail(
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
                    contentDescription = "Foto del material",
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
fun NuevoItemInventarioScreenPreview() {
    TarjetaconotrolTheme {
        NuevoItemInventarioScreen(siguienteId = 1, onGuardar = {}, onBack = {})
    }
}
