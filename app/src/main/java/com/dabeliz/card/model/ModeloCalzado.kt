package com.dabeliz.card.model

data class ModeloCalzado(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val costo: Double,
    val precioVenta: Double,
    /** Horma sobre la que se hace el modelo. Las tallas del modelo son las que trae esa horma. */
    val hormaId: Int? = null,
    val imagenes: List<String> = emptyList(),
    val imagenPrincipal: String? = null
) {
    val margenGanancia: Double get() = precioVenta - costo
    /** La foto a mostrar en listados: la marcada como principal, o si no hay ninguna marcada, la primera. */
    val imagenDestacada: String? get() = imagenPrincipal ?: imagenes.firstOrNull()
}

object ModelosDeEjemplo {
    val lista = listOf(
        ModeloCalzado(1, "Zapato Clásico Cuero", "Formal", costo = 38.0, precioVenta = 65.0, hormaId = 1),
        ModeloCalzado(2, "Bota Industrial", "Trabajo", costo = 46.0, precioVenta = 78.0, hormaId = 3),
        ModeloCalzado(3, "Zapatilla Deportiva", "Deportivo", costo = 30.0, precioVenta = 55.0, hormaId = 2),
        ModeloCalzado(4, "Mocasín Elegante", "Formal", costo = 41.0, precioVenta = 70.0),
        ModeloCalzado(5, "Sandalia Verano", "Casual", costo = 17.0, precioVenta = 32.0),
        ModeloCalzado(6, "Bota Montaña", "Outdoor", costo = 52.0, precioVenta = 89.0)
    )
}
