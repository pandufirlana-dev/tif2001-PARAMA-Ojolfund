package com.ojolfund.app.data

import java.util.Locale

private val ID_LOCALE: Locale = Locale.forLanguageTag("id-ID")

/** Angka desimal ala Indonesia: 3.0 -> "3,00" (digits = 2). */
fun formatDecimal(value: Double, digits: Int = 1): String = String.format(ID_LOCALE, "%.${digits}f", value)

/** Angka ribuan ala Indonesia tanpa "Rp": 24850 -> "24.850". */
fun formatNumber(value: Long): String = String.format(ID_LOCALE, "%,d", value)
