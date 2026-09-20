package com.dabeliz.card.model

data class ModeloCalzado(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val tallasDisponibles: String,
    val precio: Double
)

object ModelosDeEjemplo {
    val lista = listOf(
        ModeloCalzado(1, "Zapato Clásico Cuero", "Formal", "38 - 44", 65.0),
        ModeloCalzado(2, "Bota Industrial", "Trabajo", "39 - 45", 78.0),
        ModeloCalzado(3, "Zapatilla Deportiva", "Deportivo", "36 - 43", 55.0),
        ModeloCalzado(4, "Mocasín Elegante", "Formal", "39 - 44", 70.0),
        ModeloCalzado(5, "Sandalia Verano", "Casual", "35 - 41", 32.0),
        ModeloCalzado(6, "Bota Montaña", "Outdoor", "38 - 45", 89.0)
    )
}
