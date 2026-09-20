package com.dabeliz.card.model

enum class EstadoProduccion(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    TERMINADO("Terminado")
}

data class TarjetaProduccion(
    val id: Int,
    val modelo: String,
    val talla: Int,
    val cantidad: Int,
    val estado: EstadoProduccion,
    val fechaEntrega: String
)

object TarjetasDeEjemplo {
    val lista = listOf(
        TarjetaProduccion(1, "Zapato Clásico Cuero", 40, 120, EstadoProduccion.EN_PROCESO, "22/09/2026"),
        TarjetaProduccion(2, "Bota Industrial", 42, 80, EstadoProduccion.PENDIENTE, "25/09/2026"),
        TarjetaProduccion(3, "Zapatilla Deportiva", 38, 200, EstadoProduccion.TERMINADO, "18/09/2026"),
        TarjetaProduccion(4, "Mocasín Elegante", 41, 60, EstadoProduccion.PENDIENTE, "28/09/2026"),
        TarjetaProduccion(5, "Sandalia Verano", 37, 150, EstadoProduccion.EN_PROCESO, "24/09/2026"),
        TarjetaProduccion(6, "Bota Montaña", 43, 90, EstadoProduccion.TERMINADO, "19/09/2026")
    )
}
