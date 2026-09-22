package com.dabeliz.card.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

/** Crea un archivo temporal en caché y devuelve su Uri vía FileProvider, para que la cámara escriba la foto ahí. */
fun crearUriParaFoto(context: Context): Uri {
    val carpetaImagenes = File(context.cacheDir, "imagenes").apply { mkdirs() }
    val archivo = File.createTempFile("modelo_", ".jpg", carpetaImagenes)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}

/** Decodifica una Uri (foto tomada o imagen elegida de galería) a un Bitmap, sea cual sea la versión de Android. */
fun decodificarBitmap(context: Context, uri: Uri): Bitmap? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
} catch (e: Exception) {
    null
}
