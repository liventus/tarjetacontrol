package com.dabeliz.card.model

enum class CategoriaInventario(val etiqueta: String) {
    CUERO("Cuero"),
    HILO("Hilo"),
    BADANA("Badana"),
    CAJAS("Cajas"),
    AGUJAS("Agujas"),
    REPUESTOS_MAQUINA("Repuestos de máquina"),
    PRODUCTO_TERMINADO("Producto terminado"),
    OTRO("Otro")
}

data class ItemInventario(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaInventario,
    val cantidad: Int,
    val unidad: String,
    val stockMinimo: Int
) {
    val stockBajo: Boolean get() = cantidad <= stockMinimo
}

object InventarioDeEjemplo {
    val lista = listOf(
        ItemInventario(1, "Cuero sintético negro", CategoriaInventario.CUERO, 40, "m²", 50),
        ItemInventario(2, "Cuero genuino café", CategoriaInventario.CUERO, 25, "m²", 20),
        ItemInventario(3, "Hilo industrial negro", CategoriaInventario.HILO, 15, "rollos", 20),
        ItemInventario(4, "Hilo industrial blanco", CategoriaInventario.HILO, 30, "rollos", 15),
        ItemInventario(5, "Badana forro claro", CategoriaInventario.BADANA, 18, "m²", 15),
        ItemInventario(6, "Cajas para calzado talla estándar", CategoriaInventario.CAJAS, 300, "unidades", 100),
        ItemInventario(7, "Agujas industriales #18", CategoriaInventario.AGUJAS, 12, "paquetes", 10),
        ItemInventario(8, "Repuesto correa de máquina de coser", CategoriaInventario.REPUESTOS_MAQUINA, 3, "unidades", 5),
        ItemInventario(9, "Aguja de máquina de coser plana", CategoriaInventario.REPUESTOS_MAQUINA, 6, "unidades", 4),
        ItemInventario(10, "Zapato Clásico Cuero", CategoriaInventario.PRODUCTO_TERMINADO, 85, "pares", 20),
        ItemInventario(11, "Zapatilla Deportiva", CategoriaInventario.PRODUCTO_TERMINADO, 200, "pares", 40),
        ItemInventario(12, "Pegamento industrial", CategoriaInventario.OTRO, 8, "litros", 10)
    )
}
