package com.ojolfund.app.data

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

/** Jenis tag pada baris transaksi (menentukan warna badge & ikon). */
enum class TagKind { GORIDE, GRAB, MAXIM, BBM, PARKIR, MAKAN, LAINNYA }

data class Transaction(
    val id: Int,
    val tag: String,
    val tagKind: TagKind,
    val time: String,
    val title: String,
    val subtitle: String,
    val amount: Long,
    val method: String,
    val group: String,
) {
    val isIncome: Boolean get() = amount >= 0
}

enum class ExpenseCategory(val label: String) {
    BBM("BBM Motor"),
    PARKIR("Parkir"),
    MAKAN("Makan & Es"),
    DATA("Paket Data"),
    SERVIS("Servis & Ban"),
    LAINNYA("Lainnya"),
}

object SampleData {
    const val GROUP_TODAY = "Hari Ini — Kamis, 24 Okt 2024"
    const val GROUP_YESTERDAY = "Kemarin — Rabu, 23 Okt 2024"

    val transactions: List<Transaction> = listOf(
        Transaction(1, "GoRide #1084", TagKind.GORIDE, "11:42", "Tebet ke Senayan", "Kotor: Rp22.500 (Komisi 20%)", 18_000, "Dompet", GROUP_TODAY),
        Transaction(2, "Pengeluaran BBM", TagKind.BBM, "10:15", "Pertalite 2.5L SPBU 34", "Operasional Motor", -25_000, "Tunai", GROUP_TODAY),
        Transaction(3, "GrabBike #2291", TagKind.GRAB, "09:30", "Antar ke Kuningan", "Kotor: Rp19.000 (Komisi 20%)", 15_200, "OVO Driver", GROUP_TODAY),
        Transaction(4, "Parkir", TagKind.PARKIR, "08:45", "Parkir Stasiun Cawang", "Tunggu Penumpang", -3_000, "Koin Tunai", GROUP_TODAY),
        Transaction(5, "Maxim #8831", TagKind.MAXIM, "17:10", "Pasar Minggu ke Kalibata", "Kotor: Rp16.000 (Komisi 15%)", 13_600, "Saldo Maxim", GROUP_YESTERDAY),
        Transaction(6, "Makan Siang", TagKind.MAKAN, "13:00", "Warung Nasi Padang Sederhana", "Konsumsi Harian", -18_000, "QRIS Bank", GROUP_YESTERDAY),
    )
}

/** Format Rupiah ala Indonesia: Rp125.000, dengan tanda opsional (+/-). */
fun formatRupiah(value: Long, withSign: Boolean = false): String {
    val body = NumberFormat.getInstance(Locale("id", "ID")).format(abs(value))
    val sign = when {
        value < 0 -> "-"
        withSign -> "+"
        else -> ""
    }
    return "${sign}Rp$body"
}

/** Format nomor telepon 3-4-4 : "81287654321" -> "812 8765 4321". */
fun formatPhone(digits: String): String {
    val d = digits.take(12)
    val a = d.take(3)
    val b = d.drop(3).take(4)
    val c = d.drop(7)
    return listOf(a, b, c).filter { it.isNotEmpty() }.joinToString(" ")
}
