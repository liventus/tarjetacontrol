package com.dabeliz.card.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import com.dabeliz.card.ui.common.decodificarBitmap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

/** Fotos ya resueltas a URLs de Storage, listas para guardar en Firestore. */
data class FotosSubidas(val imagenes: List<String>, val principal: String?)

/**
 * Subida y limpieza de fotos en Firebase Storage, compartida por Modelos y Hormas.
 * Las fotos se guardan en {carpeta}/{id}/{uuid}.jpg y en Firestore solo va su URL.
 */
object FotosStorage {

    private const val LADO_MAXIMO_FOTO = 1600
    private const val CALIDAD_JPEG = 85

    private val storage get() = FirebaseStorage.getInstance()

    /**
     * Sube las fotos nuevas (content://, file://) y deja tal cual las que ya eran URLs
     * de Storage. Devuelve la lista final y la principal traducida a su URL.
     */
    suspend fun subirNuevas(
        context: Context,
        carpeta: String,
        id: Int,
        imagenes: List<String>,
        principal: String?
    ): FotosSubidas {
        val urlPorOriginal = imagenes.associateWith { original ->
            if (esUrlRemota(original)) original else subir(context, "$carpeta/$id", Uri.parse(original))
        }
        val finales = imagenes.map { urlPorOriginal.getValue(it) }
        return FotosSubidas(finales, principal?.let { urlPorOriginal[it] } ?: finales.firstOrNull())
    }

    /** Borra de Storage las fotos que estaban antes y ya no están, para no dejar archivos huérfanos. */
    suspend fun borrarQuitadas(anteriores: List<String>, actuales: List<String>) {
        anteriores
            .filter { esUrlRemota(it) && it !in actuales }
            .forEach { url -> runCatching { storage.getReferenceFromUrl(url).delete().await() } }
    }

    private suspend fun subir(context: Context, ruta: String, uri: Uri): String {
        val bytes = withContext(Dispatchers.IO) { comprimir(context, uri) }
            ?: throw IllegalStateException("No se pudo leer una de las fotos seleccionadas.")
        val ref = storage.reference.child("$ruta/${UUID.randomUUID()}.jpg")
        val metadata = StorageMetadata.Builder().setContentType("image/jpeg").build()
        ref.putBytes(bytes, metadata).await()
        return ref.downloadUrl.await().toString()
    }

    /** Reduce la foto a un tamaño razonable antes de subirla (las de cámara pesan varios MB). */
    private fun comprimir(context: Context, uri: Uri): ByteArray? {
        val bitmap = decodificarReducido(context, uri) ?: return null
        return ByteArrayOutputStream().use { salida ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
            salida.toByteArray()
        }
    }

    private fun decodificarReducido(context: Context, uri: Uri): Bitmap? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                // Software para poder comprimirlo; ImageDecoder ya respeta la rotación EXIF.
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val escala = LADO_MAXIMO_FOTO.toFloat() / maxOf(info.size.width, info.size.height)
                if (escala < 1f) {
                    decoder.setTargetSize((info.size.width * escala).toInt(), (info.size.height * escala).toInt())
                }
            }
        } else {
            val original = decodificarBitmap(context, uri) ?: return null
            val escala = LADO_MAXIMO_FOTO.toFloat() / maxOf(original.width, original.height)
            if (escala < 1f) {
                Bitmap.createScaledBitmap(original, (original.width * escala).toInt(), (original.height * escala).toInt(), true)
            } else {
                original
            }
        }
    } catch (e: Exception) {
        null
    }

    private fun esUrlRemota(uri: String) = uri.startsWith("https://") || uri.startsWith("http://")
}
