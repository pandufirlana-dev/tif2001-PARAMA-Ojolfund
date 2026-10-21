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
    val group: String
) {
    val isIncome: Boolean get() = amount >= 0
}

enum class ExpenseCategory(val label: String) {
    BBM("BBM Motor"),
    PARKIR("Parkir"),
    MAKAN("Makan & Es"),
    DATA("Paket Data"),
    SERVIS("Servis & Ban"),
    LAINNYA("Lainnya")
}

object SampleData {
    const val GROUP_TODAY = "Hari Ini — Kamis, 24 Okt 2024"
    const val GROUP_YESTERDAY = "Kemarin — Rabu, 23 Okt 2024"

    val transactions: List<Transaction> = listOf(
        Transaction(
            id = 1,
            tag = "GoRide #1084",
            tagKind = TagKind.GORIDE,
            time = "11:42",
            title = "Tebet ke Senayan",
            subtitle = "Kotor: Rp22.500 (Komisi 20%)",
            amount = 18_000,
            method = "Dompet",
            group = GROUP_TODAY
        ),
        Transaction(
            id = 2,
            tag = "Pengeluaran BBM",
            tagKind = TagKind.BBM,
            time = "10:15",
            title = "Pertalite 2.5L SPBU 34",
            subtitle = "Operasional Motor",
            amount = -25_000,
            method = "Tunai",
            group = GROUP_TODAY
        ),
        Transaction(
            id = 3,
            tag = "GrabBike #2291",
            tagKind = TagKind.GRAB,
            time = "09:30",
            title = "Antar ke Kuningan",
            subtitle = "Kotor: Rp19.000 (Komisi 20%)",
            amount = 15_200,
            method = "OVO Driver",
            group = GROUP_TODAY
        ),
        Transaction(
            id = 4,
            tag = "Parkir",
            tagKind = TagKind.PARKIR,
            time = "08:45",
            title = "Parkir Stasiun Cawang",
            subtitle = "Tunggu Penumpang",
            amount = -3_000,
            method = "Koin Tunai",
            group = GROUP_TODAY
        ),
        Transaction(
            id = 5,
            tag = "Maxim #8831",
            tagKind = TagKind.MAXIM,
            time = "17:10",
            title = "Pasar Minggu ke Kalibata",
            subtitle = "Kotor: Rp16.000 (Komisi 15%)",
            amount = 13_600,
            method = "Saldo Maxim",
            group = GROUP_YESTERDAY
        ),
        Transaction(
            id = 6,
            tag = "Makan Siang",
            tagKind = TagKind.MAKAN,
            time = "13:00",
            title = "Warung Nasi Padang Sederhana",
            subtitle = "Konsumsi Harian",
            amount = -18_000,
            method = "QRIS Bank",
            group = GROUP_YESTERDAY
        )
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
