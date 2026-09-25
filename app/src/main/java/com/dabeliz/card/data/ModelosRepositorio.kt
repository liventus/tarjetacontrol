package com.dabeliz.card.data

import android.content.Context
import com.dabeliz.card.model.ModeloCalzado
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Colección "modelos" de Firestore. El id del documento es el id numérico del modelo
 * (los pedidos lo referencian por ese número). hormaId apunta a un documento de la
 * colección "hormas". Las fotos van a Storage en modelos/{id}/.
 */
object ModelosRepositorio {

    private const val COLECCION = "modelos"

    private val coleccion get() = FirebaseFirestore.getInstance().collection(COLECCION)

    fun escucharModelos(): Flow<List<ModeloCalzado>> = callbackFlow {
        val registro = coleccion.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.documents.orEmpty().mapNotNull { it.aModelo() }.sortedBy { it.id })
        }
        awaitClose { registro.remove() }
    }

    /** Crea o actualiza el modelo, subiendo sus fotos nuevas y borrando las que se quitaron. */
    suspend fun guardar(context: Context, modelo: ModeloCalzado, anterior: ModeloCalzado? = null) {
        val fotos = FotosStorage.subirNuevas(context, COLECCION, modelo.id, modelo.imagenes, modelo.imagenPrincipal)

        val datos = mapOf(
            "id" to modelo.id,
            "nombre" to modelo.nombre,
            "categoria" to modelo.categoria,
            "costo" to modelo.costo,
            "precioVenta" to modelo.precioVenta,
            "hormaId" to modelo.hormaId,
            "imagenes" to fotos.imagenes,
            "imagenPrincipal" to fotos.principal,
            "actualizadoEn" to FieldValue.serverTimestamp()
        ).let { if (anterior == null) it + ("creadoEn" to FieldValue.serverTimestamp()) else it }

        coleccion.document(modelo.id.toString()).set(datos, SetOptions.merge()).await()
        FotosStorage.borrarQuitadas(anterior?.imagenes.orEmpty(), fotos.imagenes)
    }

    private fun DocumentSnapshot.aModelo(): ModeloCalzado? {
        val id = getLong("id")?.toInt() ?: this.id.toIntOrNull() ?: return null
        @Suppress("UNCHECKED_CAST")
        return ModeloCalzado(
            id = id,
            nombre = getString("nombre").orEmpty(),
            categoria = getString("categoria").orEmpty(),
            costo = getDouble("costo") ?: 0.0,
            precioVenta = getDouble("precioVenta") ?: 0.0,
            hormaId = getLong("hormaId")?.toInt(),
            imagenes = (get("imagenes") as? List<String>).orEmpty(),
            imagenPrincipal = getString("imagenPrincipal")
        )
    }
}
