package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ojolfund.app.data.Transaction
import com.ojolfund.app.data.TransactionDraft
import com.ojolfund.app.data.categoryLabel
import com.ojolfund.app.data.expenseCategories
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.data.incomeCategories
import com.ojolfund.app.data.paymentMethods
import com.ojolfund.app.data.toDraft
import com.ojolfund.app.ui.components.AmountField
import com.ojolfund.app.ui.components.ChoiceChip
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.OjolTextField
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.ReceiptPlaceholder
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

/**
 * Form Catat (initial = null) dan Ubah (initial = transaksi yang dipilih) transaksi.
 * Pada Pertemuan 10 hasilnya masih disimpan di memori lewat LedgerViewModel; sambungan ke API menyusul di Pertemuan 11.
 */
@Composable
fun TransactionFormScreen(initial: Transaction?, onBack: () -> Unit, onSave: (TransactionDraft) -> Unit) {
    var draft by remember(initial?.id) { mutableStateOf(initial?.toDraft() ?: TransactionDraft()) }
    var submitted by remember { mutableStateOf(false) }
    val editing = initial != null
    val categories = if (draft.isIncome) incomeCategories else expenseCategories
    val amountError = submitted && draft.amount <= 0L

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        DetailHeader(
            title = if (editing) "Ubah Transaksi" else "Catat Transaksi",
            subtitle = if (editing) initial?.tag else "Pendapatan atau pengeluaran",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChoiceChip("Pengeluaran", selected = !draft.isIncome, onClick = { draft = draft.withType(false) })
                ChoiceChip("Pendapatan", selected = draft.isIncome, onClick = { draft = draft.withType(true) })
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Nominal")
                AmountField(value = draft.amount, onValueChange = { draft = draft.copy(amount = it) })
                if (amountError) {
                    Text("Nominal harus lebih dari nol", style = OjolText.BodySm, color = OjolColors.ExpenseText)
                } else if (draft.amount > 0L) {
                    Text(formatRupiah(draft.amount), style = OjolText.LabelMd, color = OjolColors.Primary)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Kategori")
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { kind ->
                        ChoiceChip(
                            kind.categoryLabel,
                            selected = draft.category == kind,
                            onClick = { draft = draft.copy(category = kind) }
                        )
                    }
                }
            }

            OjolTextField(
                label = "Judul",
                value = draft.title,
                onValueChange = { draft = draft.copy(title = it) },
                placeholder = "Contoh: Tebet ke Senayan"
            )
            OjolTextField(
                label = "Keterangan (opsional)",
                value = draft.note,
                onValueChange = { draft = draft.copy(note = it) },
                placeholder = "Contoh: Tip Rp3.000",
                imeAction = ImeAction.Done
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Metode pembayaran")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    paymentMethods.forEach { method ->
                        ChoiceChip(
                            method,
                            selected = draft.method == method,
                            onClick = { draft = draft.copy(method = method) }
                        )
                    }
                }
            }

            ReceiptPlaceholder()
        }

        Surface(color = OjolColors.Card, shadowElevation = 8.dp) {
            PrimaryButton(
                text = if (editing) "Simpan Perubahan" else "Simpan Transaksi",
                onClick = {
                    submitted = true
                    if (draft.amount > 0L) onSave(draft)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, style = OjolText.LabelLg, color = OjolColors.TextPrimary)
}

/** Ganti jenis transaksi; kategori dikembalikan ke pilihan pertama bila tidak berlaku di jenis baru. */
private fun TransactionDraft.withType(income: Boolean): TransactionDraft {
    val options = if (income) incomeCategories else expenseCategories
    return copy(isIncome = income, category = if (category in options) category else options.first())
}
