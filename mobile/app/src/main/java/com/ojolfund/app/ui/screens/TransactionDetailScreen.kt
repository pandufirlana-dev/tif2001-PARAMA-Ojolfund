package com.ojolfund.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ojolfund.app.data.Transaction
import com.ojolfund.app.data.categoryLabel
import com.ojolfund.app.data.formatRupiah
import com.ojolfund.app.ui.components.AnimatedEmptyState
import com.ojolfund.app.ui.components.DetailHeader
import com.ojolfund.app.ui.components.OjolCard
import com.ojolfund.app.ui.components.Pill
import com.ojolfund.app.ui.components.PrimaryButton
import com.ojolfund.app.ui.components.ReceiptPlaceholder
import com.ojolfund.app.ui.components.SoftButton
import com.ojolfund.app.ui.components.TagIcon
import com.ojolfund.app.ui.components.tagStyle
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText
import kotlin.math.abs

// Merah tetap (tidak ikut tema) supaya teks putih pada tombol Hapus selalu terbaca di mode terang dan gelap.
private val DangerRed = Color(0xFFB3141B)

/** Detail satu transaksi dengan aksi Ubah dan Hapus. [transaction] null berarti data tidak ditemukan. */
@Composable
fun TransactionDetailScreen(transaction: Transaction?, onBack: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var confirmDelete by remember { mutableStateOf(false) }

    // Data terakhir dipertahankan agar layar tidak berkedip "tidak ditemukan" saat animasi keluar setelah hapus.
    var lastKnown by remember { mutableStateOf(transaction) }
    LaunchedEffect(transaction) {
        if (transaction != null) lastKnown = transaction
    }
    val shown = transaction ?: lastKnown

    Column(Modifier.fillMaxSize().background(OjolColors.Canvas)) {
        DetailHeader(title = "Detail Transaksi", onBack = onBack, subtitle = shown?.tag)
        if (shown == null) {
            AnimatedEmptyState(
                icon = Icons.Filled.Receipt,
                title = "Transaksi tidak ditemukan",
                subtitle = "Transaksi ini mungkin sudah dihapus."
            )
        } else {
            DetailContent(shown, onEdit, onDelete = { confirmDelete = true }, modifier = Modifier.weight(1f))
        }
    }

    if (confirmDelete && shown != null) {
        ConfirmDeleteDialog(
            tx = shown,
            onDismiss = { confirmDelete = false },
            onConfirm = {
                confirmDelete = false
                onDelete()
            }
        )
    }
}

@Composable
private fun DetailContent(tx: Transaction, onEdit: () -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    val style = tagStyle(tx.tagKind)
    Column(modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OjolCard(Modifier.fillMaxWidth(), padding = 20.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TagIcon(tx.tagKind, size = 56.dp)
                    Pill(tx.tag, bg = style.bg, fg = style.fg)
                    Text(
                        formatRupiah(tx.amount, withSign = true),
                        style = OjolText.HeadlineLg.copy(fontSize = 32.sp),
                        color = if (tx.isIncome) OjolColors.Emerald else OjolColors.ExpenseText
                    )
                    Text(
                        tx.title,
                        style = OjolText.TitleMd,
                        color = OjolColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            OjolCard(Modifier.fillMaxWidth(), padding = 16.dp) {
                DetailRow("Jenis", if (tx.isIncome) "Pendapatan" else "Pengeluaran")
                DetailRow("Kategori", tx.tagKind.categoryLabel)
                DetailRow("Waktu", "${tx.group} • ${tx.time}")
                DetailRow("Metode", tx.method)
                DetailRow("Keterangan", tx.subtitle)
            }

            ReceiptPlaceholder()
        }

        Surface(color = OjolColors.Card, shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SoftButton(
                    text = "Hapus",
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Filled.Delete,
                    bg = OjolColors.ExpenseBg,
                    fg = OjolColors.ExpenseText,
                    height = 52.dp
                )
                PrimaryButton(
                    text = "Ubah",
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    leadingIcon = Icons.Filled.Edit
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(label, style = OjolText.BodyMd, color = OjolColors.TextVariant, modifier = Modifier.width(96.dp))
        Text(value, style = OjolText.LabelLg, color = OjolColors.TextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ConfirmDeleteDialog(tx: Transaction, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = OjolColors.Card) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Hapus transaksi?", style = OjolText.TitleLg, color = OjolColors.TextPrimary)
                Text(
                    "${tx.title} (${formatRupiah(abs(tx.amount))}) akan dihapus dari buku kas. " +
                        "Tindakan ini tidak bisa dibatalkan.",
                    style = OjolText.BodyMd,
                    color = OjolColors.TextVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SoftButton(
                        text = "Batal",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        fg = OjolColors.TextVariant
                    )
                    PrimaryButton(
                        text = "Hapus",
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        color = DangerRed,
                        height = 48.dp
                    )
                }
            }
        }
    }
}
