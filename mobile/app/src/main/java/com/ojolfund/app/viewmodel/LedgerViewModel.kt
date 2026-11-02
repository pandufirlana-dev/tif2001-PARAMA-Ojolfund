package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import com.ojolfund.app.data.ExpenseCategory
import com.ojolfund.app.data.Platform
import com.ojolfund.app.data.SampleData
import com.ojolfund.app.data.TagKind
import com.ojolfund.app.data.Transaction
import com.ojolfund.app.data.formatDecimal
import com.ojolfund.app.data.formatRupiah
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Buku kas sederhana (in-memory) yang dibagi antar layar. */
class LedgerViewModel : ViewModel() {

    private val _transactions = MutableStateFlow(SampleData.transactions)
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _tripsToday = MutableStateFlow(BASE_TRIPS_TODAY)

    /** Jumlah trip hari ini (dasar 12 + order yang dicatat pada sesi ini). */
    val tripsToday: StateFlow<Int> = _tripsToday.asStateFlow()

    fun addExpense(amount: Long, category: ExpenseCategory, note: String) {
        val kind = when (category) {
            ExpenseCategory.BBM -> TagKind.BBM
            ExpenseCategory.PARKIR -> TagKind.PARKIR
            ExpenseCategory.MAKAN -> TagKind.MAKAN
            else -> TagKind.LAINNYA
        }
        val now = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date())
        val tx = Transaction(
            id = (_transactions.value.maxOfOrNull { it.id } ?: 0) + 1,
            tag = if (category == ExpenseCategory.BBM) "Pengeluaran BBM" else category.label,
            tagKind = kind,
            time = now,
            title = note.ifBlank { category.label },
            subtitle = "Operasional Motor",
            amount = -amount,
            method = "Tunai",
            group = SampleData.GROUP_TODAY
        )
        _transactions.update { listOf(tx) + it }
    }

    /** Mencatat order masuk; yang disimpan adalah pendapatan bersih setelah komisi. */
    fun addOrder(platform: Platform, gross: Long, commissionPct: Int, route: String, method: String, note: String) {
        val id = nextId()
        val net = gross - gross * commissionPct / PERCENT
        val tx = Transaction(
            id = id,
            tag = "${platform.label} #${ORDER_NUMBER_BASE + id}",
            tagKind = platform.tagKind,
            time = nowLabel(),
            title = route.ifBlank { "Order ${platform.label}" },
            subtitle = buildString {
                append("Kotor: ${formatRupiah(gross)} (Komisi $commissionPct%)")
                if (note.isNotBlank()) append(" • $note")
            },
            amount = net,
            method = method,
            group = SampleData.GROUP_TODAY
        )
        _transactions.update { listOf(tx) + it }
        _tripsToday.update { it + 1 }
    }

    /** Mencatat pengisian BBM sebagai pengeluaran operasional. */
    fun addFuel(fuelLabel: String, liters: Double, amount: Long, method: String, station: String) {
        val tx = Transaction(
            id = nextId(),
            tag = "Pengeluaran BBM",
            tagKind = TagKind.BBM,
            time = nowLabel(),
            title = "$fuelLabel ${formatDecimal(liters, 2)}L ${station.ifBlank { "SPBU" }}".trim(),
            subtitle = "Operasional Motor",
            amount = -amount,
            method = method,
            group = SampleData.GROUP_TODAY
        )
        _transactions.update { listOf(tx) + it }
    }

    private fun nextId(): Int = (_transactions.value.maxOfOrNull { it.id } ?: 0) + 1

    private fun nowLabel(): String = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date())

    private companion object {
        const val BASE_TRIPS_TODAY = 12
        const val ORDER_NUMBER_BASE = 1_000
        const val PERCENT = 100
    }
}
