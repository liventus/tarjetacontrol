package com.dabeliz.card.ui.common

import java.util.Locale

/** Formatea un monto en soles peruanos, ej. "S/ 1,234.50". */
fun formatoMoneda(valor: Double): String =
    "S/ " + String.format(Locale.getDefault(), "%,.2f", valor)
