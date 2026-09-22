package com.dabeliz.card.model

data class ModeloCalzado(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val tallasDisponibles: String,
    val costo: Double,
    val precioVenta: Double,
    val imagenes: List<String> = emptyList(),
    val imagenPrincipal: String? = null
) {
    val margenGanancia: Double get() = precioVenta - costo
    /** La foto a mostrar en listados: la marcada como principal, o si no hay ninguna marcada, la primera. */
    val imagenDestacada: String? get() = imagenPrincipal ?: imagenes.firstOrNull()
}

object ModelosDeEjemplo {
    val lista = listOf(
        ModeloCalzado(1, "Zapato Clásico Cuero", "Formal", "38 - 44", costo = 38.0, precioVenta = 65.0),
        ModeloCalzado(2, "Bota Industrial", "Trabajo", "39 - 45", costo = 46.0, precioVenta = 78.0),
        ModeloCalzado(3, "Zapatilla Deportiva", "Deportivo", "36 - 43", costo = 30.0, precioVenta = 55.0),
        ModeloCalzado(4, "Mocasín Elegante", "Formal", "39 - 44", costo = 41.0, precioVenta = 70.0),
        ModeloCalzado(5, "Sandalia Verano", "Casual", "35 - 41", costo = 17.0, precioVenta = 32.0),
        ModeloCalzado(6, "Bota Montaña", "Outdoor", "38 - 45", costo = 52.0, precioVenta = 89.0)
    )
}
