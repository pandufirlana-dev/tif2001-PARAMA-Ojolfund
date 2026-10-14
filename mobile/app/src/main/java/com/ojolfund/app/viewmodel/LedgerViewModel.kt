package com.ojolfund.app.viewmodel

import androidx.lifecycle.ViewModel
import com.ojolfund.app.data.ExpenseCategory
import com.ojolfund.app.data.SampleData
import com.ojolfund.app.data.TagKind
import com.ojolfund.app.data.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Buku kas sederhana (in-memory) yang dibagi antar layar. */
class LedgerViewModel : ViewModel() {

    private val _transactions = MutableStateFlow(SampleData.transactions)
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

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
            group = SampleData.GROUP_TODAY,
        )
        _transactions.update { listOf(tx) + it }
    }
}
