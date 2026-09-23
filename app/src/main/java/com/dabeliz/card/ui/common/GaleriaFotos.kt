package com.dabeliz.card.ui.common

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Galería de fotos de solo lectura (usada en pantallas de detalle de Modelos/Hormas):
 * fila de miniaturas, la principal con borde dorado, y al tocar cualquiera se abre
 * un visor a pantalla completa donde se puede deslizar entre el resto de las fotos.
 */
@Composable
fun GaleriaFotos(
    imagenes: List<String>,
    imagenPrincipal: String?,
    modifier: Modifier = Modifier
) {
    var indiceAmpliada by remember { mutableStateOf<Int?>(null) }

    if (imagenes.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .size(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Photo,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }
    } else {
        LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(imagenes) { indice, uriTexto ->
                FotoGaleriaThumbnail(
                    uriTexto = uriTexto,
                    esPrincipal = uriTexto == imagenPrincipal,
                    onClick = { indiceAmpliada = indice }
                )
            }
        }
    }

    if (indiceAmpliada != null) {
        VisorFotoModal(
            imagenes = imagenes,
            indiceInicial = indiceAmpliada!!,
            onCerrar = { indiceAmpliada = null }
        )
    }
}

@Composable
private fun FotoGaleriaThumbnail(uriTexto: String, esPrincipal: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember(uriTexto) { decodificarBitmap(context, Uri.parse(uriTexto)) }

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (esPrincipal) 2.dp else 1.dp,
                color = if (esPrincipal) com.dabeliz.card.ui.theme.DabelizGold else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto",
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
}

@Composable
private fun VisorFotoModal(
    imagenes: List<String>,
    indiceInicial: Int,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = indiceInicial) { imagenes.size }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pagina ->
                val bitmap = remember(imagenes[pagina]) {
                    decodificarBitmap(context, Uri.parse(imagenes[pagina]))
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = onCerrar),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto ampliada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Photo,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onCerrar,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }

            if (imagenes.size > 1) {
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${imagenes.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
