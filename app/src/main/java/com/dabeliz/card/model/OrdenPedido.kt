package com.dabeliz.card.model

/** Estado general del pedido, visible en Orden de Pedido. Arranca siempre en INICIAR_PRODUCCION. */
enum class EstadoProduccion(val etiqueta: String) {
    INICIAR_PRODUCCION("Iniciar producción"),
    EN_PROCESO("En proceso"),
    TERMINADO("Terminado"),
    ENTREGADO("Entregado")
}

/** Etapa fabril de cada línea del pedido, gestionada en Área de Producción. */
enum class EstadoProduccionDetallado(val etiqueta: String) {
    INICIO("Inicio"),
    COMPRA("Compra de materiales"),
    CORTADO("Cortado"),
    APARADO("Aparado"),
    ARMADO("Armado"),
    ACABADO("Acabado"),
    ENTREGADO("Entregado")
}

enum class EstadoPago(val etiqueta: String) {
    SIN_PAGO("Sin pago"),
    PAGO_INICIAL("Pago inicial"),
    PAGADO_COMPLETO("Pagado completo"),
    FACTURADO("Facturado"),
    CANCELADO("Cancelado")
}

enum class UnidadPedido(val etiqueta: String) {
    PAR("Por par"),
    MILLAR("Por millar (x1000)")
}

data class SerieTalla(
    val talla: Int,
    val cantidad: Int
)

/** Una línea de pedido: un modelo específico, con sus tallas/cantidades, dentro de una orden. */
data class LineaPedido(
    val modeloId: Int,
    val nombreModelo: String,
    val unidad: UnidadPedido,
    val series: List<SerieTalla>,
    val costoUnitario: Double,
    val precioVentaUnitario: Double,
    /** Avance fabril de esta línea (corte, aparado, armado...), independiente del estado general del pedido. */
    val estadoProduccion: EstadoProduccionDetallado = EstadoProduccionDetallado.INICIO
) {
    private val cantidadDeclarada: Int get() = series.sumOf { it.cantidad }
    val totalPares: Int get() = if (unidad == UnidadPedido.MILLAR) cantidadDeclarada * 1000 else cantidadDeclarada
    val costoTotal: Double get() = totalPares * costoUnitario
    val ingresoTotal: Double get() = totalPares * precioVentaUnitario
    val gananciaTotal: Double get() = ingresoTotal - costoTotal
}

data class OrdenPedido(
    val id: Int,
    val cliente: String,
    val fechaEntrega: String,
    val estado: EstadoProduccion = EstadoProduccion.INICIAR_PRODUCCION,
    val estadoPago: EstadoPago = EstadoPago.SIN_PAGO,
    /** Cuánto se ha pagado hasta ahora (relevante sobre todo con PAGO_INICIAL). */
    val montoPagado: Double = 0.0,
    val lineas: List<LineaPedido>
) {
    val costoTotal: Double get() = lineas.sumOf { it.costoTotal }
    val ingresoTotal: Double get() = lineas.sumOf { it.ingresoTotal }
    val gananciaTotal: Double get() = lineas.sumOf { it.gananciaTotal }
    val saldoPendiente: Double get() = (ingresoTotal - montoPagado).coerceAtLeast(0.0)
}

object PedidosDeEjemplo {
    val lista = listOf(
        OrdenPedido(
            id = 1,
            cliente = "Calzados El Progreso",
            fechaEntrega = "22/09/2026",
            estado = EstadoProduccion.EN_PROCESO,
            estadoPago = EstadoPago.PAGO_INICIAL,
            montoPagado = 3000.0,
            lineas = listOf(
                LineaPedido(
                    modeloId = 1,
                    nombreModelo = "Zapato Clásico Cuero",
                    unidad = UnidadPedido.PAR,
                    series = listOf(SerieTalla(40, 40), SerieTalla(41, 60)),
                    costoUnitario = 38.0,
                    precioVentaUnitario = 65.0,
                    estadoProduccion = EstadoProduccionDetallado.APARADO
                ),
                LineaPedido(
                    modeloId = 3,
                    nombreModelo = "Zapatilla Deportiva",
                    unidad = UnidadPedido.PAR,
                    series = listOf(SerieTalla(38, 120), SerieTalla(39, 80)),
                    costoUnitario = 30.0,
                    precioVentaUnitario = 55.0,
                    estadoProduccion = EstadoProduccionDetallado.CORTADO
                )
            )
        ),
        OrdenPedido(
            id = 2,
            cliente = "Distribuidora Andina",
            fechaEntrega = "25/09/2026",
            estado = EstadoProduccion.INICIAR_PRODUCCION,
            estadoPago = EstadoPago.SIN_PAGO,
            lineas = listOf(
                LineaPedido(
                    modeloId = 2,
                    nombreModelo = "Bota Industrial",
                    unidad = UnidadPedido.MILLAR,
                    series = listOf(SerieTalla(42, 2)),
                    costoUnitario = 46.0,
                    precioVentaUnitario = 78.0
                )
            )
        ),
        OrdenPedido(
            id = 3,
            cliente = "Zapatería Rivas",
            fechaEntrega = "18/09/2026",
            estado = EstadoProduccion.ENTREGADO,
            estadoPago = EstadoPago.FACTURADO,
            montoPagado = 11000.0,
            lineas = listOf(
                LineaPedido(
                    modeloId = 3,
                    nombreModelo = "Zapatilla Deportiva",
                    unidad = UnidadPedido.PAR,
                    series = listOf(SerieTalla(37, 100), SerieTalla(38, 100)),
                    costoUnitario = 30.0,
                    precioVentaUnitario = 55.0,
                    estadoProduccion = EstadoProduccionDetallado.ENTREGADO
                )
            )
        )
    )
}
