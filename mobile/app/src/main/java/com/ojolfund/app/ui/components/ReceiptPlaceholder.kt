package com.ojolfund.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.ui.theme.OjolText

/**
 * Tempat foto struk. Belum bisa dipakai: unggah ke cloud storage dikerjakan di Pertemuan 11.
 * Sengaja tidak bisa diklik agar tidak ada tombol yang terlihat aktif tetapi tidak berfungsi.
 */
@Composable
fun ReceiptPlaceholder(modifier: Modifier = Modifier) {
    // TODO(P11): ganti dengan Photo Picker + pratinjau dari receipt_url.
    OjolCard(modifier = modifier.fillMaxWidth(), padding = 14.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                Icons.Filled.Receipt,
                contentDescription = null,
                tint = OjolColors.TextSecondary,
                modifier = Modifier.size(28.dp)
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Foto struk", style = OjolText.TitleMd, color = OjolColors.TextPrimary)
                Text(
                    "Unggah foto struk tersedia setelah penyimpanan cloud siap.",
                    style = OjolText.BodyMd,
                    color = OjolColors.TextVariant
                )
            }
            Pill("Segera hadir", bg = OjolColors.GrayBox2, fg = OjolColors.TextPrimary)
        }
    }
}
