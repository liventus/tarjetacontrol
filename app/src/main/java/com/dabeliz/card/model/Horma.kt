package com.dabeliz.card.model

/** HORMA1, HORMA2... el código se genera solo a partir del id, nunca se escribe a mano. */
fun generarCodigoHorma(id: Int): String = "HORMA$id"

data class Horma(
    val id: Int,
    val nombre: String,
    /** Cada talla tiene su propia cantidad de pares (32 puede tener 12 pares, 33 tener 14, etc). */
    val tallas: List<SerieTalla>,
    val imagenes: List<String> = emptyList(),
    val imagenPrincipal: String? = null
) {
    val codigo: String get() = generarCodigoHorma(id)
    val totalPares: Int get() = tallas.sumOf { it.cantidad }
    /** "38 (18), 39 (20)..." — número de talla y sus pares disponibles. */
    val tallasTexto: String get() = tallas.sortedBy { it.talla }.joinToString(", ") { "${it.talla} (${it.cantidad})" }
    /** "38, 39, 40..." — solo los números de talla, sin cantidades. */
    val numerosTexto: String get() = tallas.sortedBy { it.talla }.joinToString(", ") { it.talla.toString() }
    val imagenDestacada: String? get() = imagenPrincipal ?: imagenes.firstOrNull()
}

object HormasDeEjemplo {
    val lista = listOf(
        Horma(
            id = 1,
            nombre = "Horma Clásica Hombre",
            tallas = listOf(
                SerieTalla(38, 18),
                SerieTalla(39, 20),
                SerieTalla(40, 22),
                SerieTalla(41, 16),
                SerieTalla(42, 14),
                SerieTalla(43, 10),
                SerieTalla(44, 8)
            )
        ),
        Horma(
            id = 2,
            nombre = "Horma Deportiva",
            tallas = listOf(
                SerieTalla(36, 10),
                SerieTalla(37, 12),
                SerieTalla(38, 15),
                SerieTalla(39, 14),
                SerieTalla(40, 12)
            )
        ),
        Horma(
            id = 3,
            nombre = "Horma Bota Industrial",
            tallas = listOf(
                SerieTalla(39, 6),
                SerieTalla(40, 8),
                SerieTalla(41, 9),
                SerieTalla(42, 10)
            )
        )
    )
}
