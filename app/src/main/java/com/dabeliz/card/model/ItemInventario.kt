package com.dabeliz.card.model

enum class CategoriaInventario(val etiqueta: String) {
    CUERO("Cuero"),
    HILO("Hilo"),
    BADANA("Badana"),
    CAJAS("Cajas"),
    AGUJAS("Agujas"),
    REPUESTOS_MAQUINA("Repuestos de máquina"),
    MAQUINARIA("Maquinaria"),
    HORMAS("Hormas"),
    PRODUCTO_TERMINADO("Producto terminado"),
    OTRO("Otro")
}

data class ItemInventario(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaInventario,
    val cantidad: Int,
    val unidad: String,
    val stockMinimo: Int,
    val costo: Double = 0.0,
    /** Si este ítem viene de Área Hormas, referencia la horma para poder sumarle stock más adelante. */
    val hormaId: Int? = null,
    val imagenes: List<String> = emptyList(),
    val imagenPrincipal: String? = null
) {
    val stockBajo: Boolean get() = cantidad <= stockMinimo
    val imagenDestacada: String? get() = imagenPrincipal ?: imagenes.firstOrNull()
}

object InventarioDeEjemplo {
    val lista = listOf(
        ItemInventario(1, "Cuero sintético negro", CategoriaInventario.CUERO, 40, "m²", 50, costo = 8.50),
        ItemInventario(2, "Cuero genuino café", CategoriaInventario.CUERO, 25, "m²", 20, costo = 18.0),
        ItemInventario(3, "Hilo industrial negro", CategoriaInventario.HILO, 15, "rollos", 20, costo = 3.20),
        ItemInventario(4, "Hilo industrial blanco", CategoriaInventario.HILO, 30, "rollos", 15, costo = 3.20),
        ItemInventario(5, "Badana forro claro", CategoriaInventario.BADANA, 18, "m²", 15, costo = 6.0),
        ItemInventario(6, "Cajas para calzado talla estándar", CategoriaInventario.CAJAS, 300, "unidades", 100, costo = 0.45),
        ItemInventario(7, "Agujas industriales #18", CategoriaInventario.AGUJAS, 12, "paquetes", 10, costo = 2.10),
        ItemInventario(8, "Repuesto correa de máquina de coser", CategoriaInventario.REPUESTOS_MAQUINA, 3, "unidades", 5, costo = 15.0),
        ItemInventario(9, "Aguja de máquina de coser plana", CategoriaInventario.REPUESTOS_MAQUINA, 6, "unidades", 4, costo = 4.0),
        ItemInventario(10, "Máquina de coser plana industrial", CategoriaInventario.MAQUINARIA, 2, "unidades", 1, costo = 850.0),
        ItemInventario(11, "Zapato Clásico Cuero", CategoriaInventario.PRODUCTO_TERMINADO, 85, "pares", 20, costo = 38.0),
        ItemInventario(12, "Zapatilla Deportiva", CategoriaInventario.PRODUCTO_TERMINADO, 200, "pares", 40, costo = 30.0),
        ItemInventario(13, "Pegamento industrial", CategoriaInventario.OTRO, 8, "litros", 10, costo = 12.0)
    )
}
