package com.dabeliz.card.data

import android.content.Context
import com.dabeliz.card.model.Horma
import com.dabeliz.card.model.SerieTalla
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Colección "hormas" de Firestore. El id del documento es el id numérico de la horma,
 * que es el mismo que guardan los modelos en su campo hormaId (de ahí salen las tallas
 * del modelo). Las fotos van a Storage en hormas/{id}/.
 */
object HormasRepositorio {

    private const val COLECCION = "hormas"

    private val coleccion get() = FirebaseFirestore.getInstance().collection(COLECCION)

    fun escucharHormas(): Flow<List<Horma>> = callbackFlow {
        val registro = coleccion.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.aHorma() }.sortedBy { it.id })
        }
        awaitClose { registro.remove() }
    }

    /** Crea o actualiza la horma, subiendo sus fotos nuevas y borrando las que se quitaron. */
    suspend fun guardar(context: Context, horma: Horma, anterior: Horma? = null) {
        val fotos = FotosStorage.subirNuevas(context, COLECCION, horma.id, horma.imagenes, horma.imagenPrincipal)

        val datos = mapOf(
            "id" to horma.id,
            "codigo" to horma.codigo,
            "nombre" to horma.nombre,
            "tallas" to horma.tallas.sortedBy { it.talla }.map { mapOf("talla" to it.talla, "cantidad" to it.cantidad) },
            "imagenes" to fotos.imagenes,
            "imagenPrincipal" to fotos.principal,
            "actualizadoEn" to FieldValue.serverTimestamp()
        ).let { if (anterior == null) it + ("creadoEn" to FieldValue.serverTimestamp()) else it }

        coleccion.document(horma.id.toString()).set(datos, SetOptions.merge()).await()
        FotosStorage.borrarQuitadas(anterior?.imagenes.orEmpty(), fotos.imagenes)
    }

    private fun DocumentSnapshot.aHorma(): Horma? {
        val id = getLong("id")?.toInt() ?: this.id.toIntOrNull() ?: return null
        @Suppress("UNCHECKED_CAST")
        val tallas = (get("tallas") as? List<Map<String, Any?>>).orEmpty().mapNotNull { serie ->
            val talla = (serie["talla"] as? Number)?.toInt() ?: return@mapNotNull null
            SerieTalla(talla, (serie["cantidad"] as? Number)?.toInt() ?: 0)
        }
        @Suppress("UNCHECKED_CAST")
        return Horma(
            id = id,
            nombre = getString("nombre").orEmpty(),
            tallas = tallas,
            imagenes = (get("imagenes") as? List<String>).orEmpty(),
            imagenPrincipal = getString("imagenPrincipal")
        )
    }
}
