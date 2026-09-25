package com.dabeliz.card.data

import com.dabeliz.card.model.Rol
import com.dabeliz.card.model.Usuario
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Colección "usuarios" de Firestore: un documento por cuenta de Google, con su rol. */
object UsuariosRepositorio {

    private const val COLECCION = "usuarios"

    private val coleccion get() = FirebaseFirestore.getInstance().collection(COLECCION)

    /**
     * Se llama justo después de iniciar sesión. Si es la primera vez que entra esa cuenta,
     * se crea su documento con rol TRABAJADOR (las reglas no permiten auto-asignarse ADMIN);
     * si ya existía, solo se actualizan sus datos de perfil y la fecha de último acceso.
     */
    suspend fun registrarAcceso(usuario: FirebaseUser): Usuario {
        val ref = coleccion.document(usuario.uid)
        val perfil = mapOf(
            "email" to (usuario.email ?: ""),
            "nombre" to (usuario.displayName ?: usuario.email ?: ""),
            "fotoUrl" to usuario.photoUrl?.toString(),
            "ultimoAcceso" to FieldValue.serverTimestamp()
        )
        val existente = ref.get().await()
        if (existente.exists()) {
            ref.set(perfil, SetOptions.merge()).await()
        } else {
            ref.set(
                perfil + mapOf(
                    "rol" to Rol.TRABAJADOR.valor,
                    "activo" to true,
                    "creadoEn" to FieldValue.serverTimestamp()
                )
            ).await()
        }
        return ref.get().await().aUsuario()
    }

    fun escucharUsuarios(): Flow<List<Usuario>> = callbackFlow {
        val registro = coleccion.orderBy("nombre").addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.documents.orEmpty().map { it.aUsuario() })
        }
        awaitClose { registro.remove() }
    }

    suspend fun cambiarRol(uid: String, rol: Rol) {
        coleccion.document(uid).update("rol", rol.valor).await()
    }

    suspend fun cambiarActivo(uid: String, activo: Boolean) {
        coleccion.document(uid).update("activo", activo).await()
    }

    private fun DocumentSnapshot.aUsuario() = Usuario(
        uid = id,
        email = getString("email").orEmpty(),
        nombre = getString("nombre").orEmpty(),
        fotoUrl = getString("fotoUrl"),
        rol = Rol.desdeValor(getString("rol")),
        activo = getBoolean("activo") ?: true
    )
}
