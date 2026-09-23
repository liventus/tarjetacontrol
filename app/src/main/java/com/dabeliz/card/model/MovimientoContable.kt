package com.dabeliz.card.model

import java.time.DayOfWeek
import java.time.LocalDate

enum class CategoriaMovimiento(val etiqueta: String, val esIngreso: Boolean) {
    VENTA("Venta", true),
    OTRO_INGRESO("Otro ingreso", true),
    IMPUESTOS("Impuestos", false),
    SERVICIO_CORTADO("Servicio de cortado", false),
    SERVICIO_APARADO("Servicio de aparado", false),
    SERVICIO_ARMADO("Servicio de armado", false),
    SERVICIO_ACABADO("Servicio de acabado", false),
    ELECTRICIDAD("Electricidad", false),
    AGUA("Agua", false),
    INTERNET("Internet", false),
    MATERIALES("Materiales", false),
    NOMINA("Nómina", false),
    OTRO_EGRESO("Otro egreso", false)
}

data class MovimientoContable(
    val id: Int,
    val concepto: String,
    val categoria: CategoriaMovimiento,
    val monto: Double,
    val fecha: LocalDate
) {
    val esIngreso: Boolean get() = categoria.esIngreso
}

object MovimientosDeEjemplo {
    private val hoy: LocalDate = LocalDate.now()
    private val inicioSemana: LocalDate = hoy.with(DayOfWeek.MONDAY)
    private val inicioMes: LocalDate = hoy.withDayOfMonth(1)

    val lista = listOf(
        MovimientoContable(1, "Venta lote zapatillas deportivas", CategoriaMovimiento.VENTA, 3200.0, inicioSemana.plusDays(1)),
        MovimientoContable(2, "Compra de cuero", CategoriaMovimiento.MATERIALES, 950.0, inicioSemana.plusDays(2)),
        MovimientoContable(3, "Pago de nómina", CategoriaMovimiento.NOMINA, 1800.0, inicioSemana),
        MovimientoContable(4, "Venta botas industriales", CategoriaMovimiento.VENTA, 2100.0, inicioSemana.plusDays(3)),
        MovimientoContable(5, "Recibo de luz", CategoriaMovimiento.ELECTRICIDAD, 180.0, inicioSemana.plusDays(1)),
        MovimientoContable(6, "Recibo de agua", CategoriaMovimiento.AGUA, 65.0, inicioSemana.plusDays(1)),
        MovimientoContable(7, "Plan de internet", CategoriaMovimiento.INTERNET, 120.0, inicioMes.plusDays(4)),
        MovimientoContable(8, "Pago taller de aparado", CategoriaMovimiento.SERVICIO_APARADO, 420.0, inicioMes.plusDays(6)),
        MovimientoContable(9, "Pago taller de cortado", CategoriaMovimiento.SERVICIO_CORTADO, 300.0, inicioMes.plusDays(8)),
        MovimientoContable(10, "IGV del mes", CategoriaMovimiento.IMPUESTOS, 540.0, inicioMes.plusDays(10)),
        MovimientoContable(11, "Venta zapato clásico", CategoriaMovimiento.VENTA, 1950.0, hoy.minusMonths(1)),
        MovimientoContable(12, "Pago taller de armado", CategoriaMovimiento.SERVICIO_ARMADO, 380.0, hoy.minusMonths(1)),
        MovimientoContable(13, "Pago taller de acabado", CategoriaMovimiento.SERVICIO_ACABADO, 260.0, hoy.minusMonths(1).minusDays(3)),
        MovimientoContable(14, "Recibo de luz", CategoriaMovimiento.ELECTRICIDAD, 175.0, hoy.minusMonths(1)),
        MovimientoContable(15, "Renta del local", CategoriaMovimiento.OTRO_EGRESO, 800.0, hoy.minusMonths(2)),
        MovimientoContable(16, "Venta lote a distribuidor", CategoriaMovimiento.OTRO_INGRESO, 400.0, hoy.minusMonths(3)),
        MovimientoContable(17, "Pago de impuesto a la renta", CategoriaMovimiento.IMPUESTOS, 1200.0, hoy.minusMonths(4))
    )
}
