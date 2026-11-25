package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import com.ojolfund.app.data.ExpenseCategory
import com.ojolfund.app.data.Platform
import com.ojolfund.app.data.SampleData
import com.ojolfund.app.data.TagKind
import com.ojolfund.app.data.Transaction
import com.ojolfund.app.data.TransactionDraft
import com.ojolfund.app.data.categoryLabel
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

    /** Tambah transaksi dari form Catat Transaksi. Sementara di memori; sambungan ke API menyusul (Pertemuan 11). */
    fun addTransaction(draft: TransactionDraft) {
        val tx = Transaction(
            id = nextId(),
            tag = draft.category.categoryLabel,
            tagKind = draft.category,
            time = nowLabel(),
            title = draft.title.ifBlank { draft.category.categoryLabel },
            subtitle = draft.note.ifBlank { defaultNote(draft.isIncome) },
            amount = signedAmount(draft),
            method = draft.method,
            group = SampleData.GROUP_TODAY
        )
        _transactions.update { listOf(tx) + it }
    }

    /** Ubah transaksi [id]; waktu dan hari catatnya tidak berubah. Id yang tidak ada diabaikan. */
    fun updateTransaction(id: Int, draft: TransactionDraft) {
        _transactions.update { list ->
            list.map { tx ->
                if (tx.id != id) {
                    tx
                } else {
                    tx.copy(
                        tag = if (draft.category == tx.tagKind) tx.tag else draft.category.categoryLabel,
                        tagKind = draft.category,
                        title = draft.title.ifBlank { draft.category.categoryLabel },
                        subtitle = draft.note.ifBlank { defaultNote(draft.isIncome) },
                        amount = signedAmount(draft),
                        method = draft.method
                    )
                }
            }
        }
    }

    fun deleteTransaction(id: Int) {
        _transactions.update { list -> list.filterNot { it.id == id } }
    }

    private fun signedAmount(draft: TransactionDraft): Long = if (draft.isIncome) draft.amount else -draft.amount

    private fun defaultNote(isIncome: Boolean): String = if (isIncome) "Pendapatan manual" else "Operasional Motor"

    private fun nextId(): Int = (_transactions.value.maxOfOrNull { it.id } ?: 0) + 1

    private fun nowLabel(): String = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date())

    private companion object {
        const val BASE_TRIPS_TODAY = 12
        const val ORDER_NUMBER_BASE = 1_000
        const val PERCENT = 100
    }
}
