package com.dabeliz.card.model

/**
 * Roles de la fábrica. El valor es lo que se guarda en Firestore (usuarios/{uid}.rol)
 * y lo que leen las reglas de seguridad, así que no se debe cambiar a la ligera.
 * Esta app es solo para ADMIN; la futura app del trabajador usará TRABAJADOR.
 */
enum class Rol(val valor: String, val etiqueta: String) {
    ADMIN("admin", "Administrador"),
    TRABAJADOR("trabajador", "Trabajador");

    companion object {
        fun desdeValor(valor: String?): Rol = entries.firstOrNull { it.valor == valor } ?: TRABAJADOR
    }
}

/** Documento usuarios/{uid} en Firestore. El id es el uid de Firebase Auth. */
data class Usuario(
    val uid: String,
    val email: String,
    val nombre: String,
    val fotoUrl: String? = null,
    val rol: Rol = Rol.TRABAJADOR,
    val activo: Boolean = true
) {
    val puedeUsarAppAdmin: Boolean get() = activo && rol == Rol.ADMIN
}
