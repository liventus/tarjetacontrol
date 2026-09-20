package com.dabeliz.card.model

data class ItemInventario(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val cantidad: Int,
    val unidad: String,
    val stockMinimo: Int
) {
    val stockBajo: Boolean get() = cantidad <= stockMinimo
}

object InventarioDeEjemplo {
    val lista = listOf(
        ItemInventario(1, "Cuero sintético negro", "Materia prima", 40, "m²", 50),
        ItemInventario(2, "Suela de goma talla 40", "Materia prima", 120, "pares", 30),
        ItemInventario(3, "Hilo industrial", "Materia prima", 15, "rollos", 20),
        ItemInventario(4, "Zapato Clásico Cuero", "Producto terminado", 85, "pares", 20),
        ItemInventario(5, "Zapatilla Deportiva", "Producto terminado", 200, "pares", 40),
        ItemInventario(6, "Pegamento industrial", "Materia prima", 8, "litros", 10)
    )
}
