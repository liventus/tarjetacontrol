package com.dabeliz.card.model

enum class TipoMovimiento {
    INGRESO,
    EGRESO
}

data class MovimientoContable(
    val id: Int,
    val concepto: String,
    val tipo: TipoMovimiento,
    val monto: Double,
    val fecha: String
)

object MovimientosDeEjemplo {
    val lista = listOf(
        MovimientoContable(1, "Venta lote zapatillas deportivas", TipoMovimiento.INGRESO, 3200.0, "18/09/2026"),
        MovimientoContable(2, "Compra de cuero", TipoMovimiento.EGRESO, 950.0, "19/09/2026"),
        MovimientoContable(3, "Pago de nómina", TipoMovimiento.EGRESO, 1800.0, "20/09/2026"),
        MovimientoContable(4, "Venta botas industriales", TipoMovimiento.INGRESO, 2100.0, "20/09/2026"),
        MovimientoContable(5, "Pago de servicios", TipoMovimiento.EGRESO, 320.0, "21/09/2026")
    )
}
